package edu.fiu.cs.tomcatcollector;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import org.dom4j.Element;

public class MySQLEventTimerTask extends TimerTask {
	
	private TomcatCollector collector = null;

	private Connection conn = null;
	
	private String original_general_log_variable = null;
	
	private String original_log_output_variable = null;
	
	private long clearLogTablePeriod = 10;
	
	private long run_times = 0;
	
	private Timestamp lastRunTimestamp = null;
	
	private Map<String, Long> prevGlobalVariables = new HashMap<String, Long>();
	
	private final static String[] GLOBAL_VAR_NAMES = {
		"THREADS_CONNECTED",
		"THREADS_RUNNING",
		"CREATED_TMP_DISK_TABLES",
		"CREATED_TMP_TABLES",
		"OPEN_TABLES",
		"SELECT_FULL_JOIN",
		"SLOW_QUERIES",
		"INNODB_ROWS_INSERTED",
		"INNODB_ROWS_READ",
		"INNODB_ROWS_UPDATED",
		"INNODB_ROWS_DELETED",
		"BYTES_SENT",
		"BYTES_RECEIVED",
		"QUERIES",
		"UPTIME",
	};
	
	private final static String[] INCR_GLOBAL_VAR_NAMES ={
		"CREATED_TMP_DISK_TABLES",
		"CREATED_TMP_TABLES",
		"OPEN_TABLES",
		"SELECT_FULL_JOIN",
		"SLOW_QUERIES",
		"INNODB_ROWS_INSERTED",
		"INNODB_ROWS_READ",
		"INNODB_ROWS_UPDATED",
		"INNODB_ROWS_DELETED",
		"BYTES_SENT",
		"BYTES_RECEIVED",
		"QUERIES",
		"UPTIME",
	};
	
	public MySQLEventTimerTask(String dbUrl, String username, String password, String driverClass, long clearLogTablePeriod,
			TomcatCollector collector) {
		this.collector = collector;
		this.clearLogTablePeriod = clearLogTablePeriod;
		try {
			Class.forName(driverClass).newInstance();
			conn = DriverManager.getConnection(dbUrl, username, password);
		} catch (Exception e) {
			e.printStackTrace();
			conn = null;
		}
		
		// Enable general log of MySQL
		try {
			enableGeneralLog();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
			try {
				conn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
		}
		
		System.out.println("MySQLEventTimerTask is started.");
	}
	
	private void enableGeneralLog() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rets = stmt.executeQuery("SHOW GLOBAL VARIABLES where Variable_Name='general_log'");
		if (rets.next()) {
			original_general_log_variable = rets.getString(2);
		}
		else {
			original_general_log_variable = null;
		}
		rets.close();
		rets = stmt.executeQuery("SHOW GLOBAL VARIABLES where Variable_Name='log_output'");
		if (rets.next()) {
			original_log_output_variable = rets.getString(2);
		}
		else {
			original_log_output_variable = null;
		}
		rets.close();
		
		stmt.execute("SET GLOBAL GENERAL_LOG='ON'");
		stmt.execute("SET GLOBAL LOG_OUTPUT='TABLE'");
		stmt.close();
	}
	
	private void disableGeneralLog() throws SQLException {
		Statement stmt = conn.createStatement();
		if (original_general_log_variable == null || original_general_log_variable.equalsIgnoreCase("OFF")) {
			stmt.execute("SET GLOBAL GENERAL_LOG='OFF'");
		}
		if (original_log_output_variable != null) {
			stmt.execute("SET GLOBAL LOG_OUTPUT='"+original_log_output_variable+"'");
		}
		stmt.close();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized(this) {
			Element event = collector.createEvent("MySQL");
			try {
				fillGlobalStatus(event);
				fillProcessList(event);
				fillGeneralLog(event);
				collector.writeEvent(event);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			this.run_times++;
		}
	}
	
	private void fillGlobalStatus(Element event) throws SQLException {
		Statement stmt = conn.createStatement();
		Map<String, String> varMap = new HashMap<String,String>();
		ResultSet rets = stmt.executeQuery("SELECT * FROM INFORMATION_SCHEMA.GLOBAL_STATUS");
		while (rets.next()) {
			varMap.put(rets.getString(1), rets.getString(2));
		}
		rets.close();
		stmt.close();
		for (String varName: GLOBAL_VAR_NAMES) {
			event.addElement(varName).setText(varMap.get(varName));
		}
		if (this.run_times > 0) {
			for (String varName: INCR_GLOBAL_VAR_NAMES) {
				long currentValue = Long.parseLong(varMap.get(varName));
				long prevValue = prevGlobalVariables.get(varName);
				long increment = currentValue - prevValue;
				event.addElement("INCR_"+varName).setText(increment+"");			
			}
		}
		for (String varName: INCR_GLOBAL_VAR_NAMES) {
			long currentValue = Long.parseLong(varMap.get(varName));
			prevGlobalVariables.put(varName, currentValue);
		}
	}
	
	private void fillProcessList(Element event) throws SQLException {
		Element processListElem = event.addElement("processes");
		Statement stmt = conn.createStatement();
		ResultSet rets = stmt.executeQuery("SELECT * FROM INFORMATION_SCHEMA.PROCESSLIST");
		while(rets.next()) {
			Element processElem = processListElem.addElement("process");
			processElem.addAttribute("id", rets.getString("ID"));
			processElem.addAttribute("user", rets.getString("USER"));
			processElem.addAttribute("host", rets.getString("HOST"));
			processElem.addAttribute("db", rets.getString("DB"));
			processElem.addAttribute("command", rets.getString("COMMAND"));
			processElem.addAttribute("time", rets.getString("TIME"));
			String state = rets.getString("STATE");
			if (state == null) {
				state = "";
			}
			processElem.addAttribute("state", state);
			String info = rets.getString("INFO");
			if (info == null) {
				info = "";
			}
			processElem.addElement("info").setText(info);
		}
		rets.close();
		stmt.close();
	}
	
	private void fillGeneralLog(Element event) throws SQLException {		
		if (lastRunTimestamp == null) {
			lastRunTimestamp = new Timestamp(new Date().getTime());
		}
		Element logElem = event.addElement("general_log");
		PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM mysql.general_log WHERE event_time>=?");
		pstmt.setTimestamp(1, lastRunTimestamp);
		ResultSet rets = pstmt.executeQuery();
		while(rets.next()) {
			Element queryElem = logElem.addElement("log");
			queryElem.addAttribute("time", rets.getString(1));
			queryElem.addAttribute("host", rets.getString(2));
			queryElem.addAttribute("thread_id", rets.getString(3));
			queryElem.addAttribute("server_id", rets.getString(4));
			queryElem.addAttribute("command_type", rets.getString(5));
			queryElem.addElement("argument").setText(rets.getString(6));
		}
		rets.close();
		pstmt.close();
		if (this.run_times % clearLogTablePeriod == 0) {
			if (original_general_log_variable.equalsIgnoreCase("ON") == false  || 
					original_log_output_variable.equalsIgnoreCase("TABLE") == false) {				
				// Delete all records in general_log table for saving space
				Statement stmt = conn.createStatement();
				stmt.execute("TRUNCATE mysql.general_log");
				stmt.close();
			}
		}
		lastRunTimestamp.setTime(new Date().getTime());
	}
	
	public void destroy() {
		synchronized(this) {
			try {
				disableGeneralLog();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			conn = null;
		}

	}
	
	

}
