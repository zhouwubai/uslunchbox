package edu.fiu.cs.tomcatcollector;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Element;

import edu.fiu.cs.tomcatcollector.output.JSONOutputStreamEventWriter;
import edu.fiu.cs.tomcatcollector.output.XMLEventToJSONEvent;
import edu.fiu.cs.tomcatcollector.output.XMLOutputStreamEventWriter;
import edu.fiu.cs.tomcatcollector.output.ZipUtil;

public class TomcatCollector {
	
	private final static String ATTRIBUTE_ID = "TomcatCollector_201204220741";

	private boolean isDisabled = false;
	
	private String logFilePath;
	
	private String webAppName;

	private JSONOutputStreamEventWriter logWriter = null;
	
	private String logFileName;
	
	private int logFileCreatedDay;

	private JVMEventTimerTask JVMEventTimerTask = null;

	private MySQLEventTimerTask MySQLEventTimerTask = null;
	
	private List<Timer> timers = new ArrayList<Timer>();
	
	private String accessKey = null;
	
	private LinkedList<Element> recentEventList = new LinkedList<Element>();
	
	private long[] lastGeneratedEventId = new long[2];
	
	private final static int MAX_RECENT_EVENTS = 100;

	private SimpleDateFormat timestampSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

	public TomcatCollector() {

	}

	public void init(ServletContext context) {
		context.setAttribute(ATTRIBUTE_ID, this);
		
		// Create the log file
		logFilePath = context.getInitParameter("tomcatcollector_log_filepath");
		if (logFilePath == null) {
			isDisabled = true;
			return;
		} else {
			isDisabled = false;
		}
		webAppName = context.getServletContextName();
		try {
			openLogWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Get public servlet access key
		this.accessKey = context.getInitParameter("tomcatcollector_access_key");

		// Set the JVM timer task
		String JVMTimerInSecondsStr = context.getInitParameter("tomcatcollector_jvm_timer");
		if (JVMTimerInSecondsStr != null) {
			int JVMTimerInSeconds = Integer.parseInt(JVMTimerInSecondsStr);
			Timer JVMEventTimer = new Timer();
			timers.add(JVMEventTimer);
			JVMEventTimerTask = new JVMEventTimerTask(this);
			JVMEventTimer.schedule(JVMEventTimerTask, 0, JVMTimerInSeconds);
		}

		// Set the MySQL timer task
		String MySQLTimerInSecondsStr = context.getInitParameter("tomcatcollector_mysql_timer");
		if (MySQLTimerInSecondsStr != null) {
			int MySQLTimerInSeconds = Integer.parseInt(MySQLTimerInSecondsStr);
			String username = context.getInitParameter("tomcatcollector_mysql_username");
			String password = context.getInitParameter("tomcatcollector_mysql_password");
			String url = context.getInitParameter("tomcatcollector_mysql_url");
			long clearLogPeriod = Long.parseLong(context.getInitParameter("tomcatcollector_mysql_clearlogperiod"));
			Timer MySQLEventTimer = new Timer();
			timers.add(MySQLEventTimer);
			MySQLEventTimerTask = new MySQLEventTimerTask(url, username, password, "com.mysql.jdbc.Driver", clearLogPeriod, this);
			MySQLEventTimer.schedule(MySQLEventTimerTask, 0, MySQLTimerInSeconds);
		}
		
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
		if (isDisabled) {
			return;
		}
		
		try {
			for (Timer timer: timers) {
				timer.cancel();
			}
			MySQLEventTimerTask.destroy();
			JVMEventTimerTask.destroy();
			
			logWriter.close();
			logWriter = null;
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isDisabled() {
		return isDisabled;
	}
	
	private void openLogWriter() throws IOException {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		logFileName = logFilePath + webAppName + "_tomcatcollector_" + sdf.format(date) + ".json";
		logWriter = new JSONOutputStreamEventWriter(logFileName);			
		Calendar cal = Calendar.getInstance();
		logFileCreatedDay = cal.get(Calendar.DAY_OF_YEAR);
		System.out.println("opened log file : "+logFileName);
	}
	
	public void writeEvent(Element event) throws IOException {
		synchronized (this) {
			if (logWriter != null) {
				logWriter.write(XMLEventToJSONEvent.toJSONObject(event));
				Calendar cal = Calendar.getInstance();
				if (logFileCreatedDay != cal.get(Calendar.DAY_OF_YEAR)) { // Different day with when the log file created
					// Compress the current log file to a zip file
					System.out.println("Close the log file : "+logFileName);
					logWriter.close();
					String oldLogFileName = logFileName;
					
					// Create another log file
					openLogWriter();
					
					// Start the thread for replacing the old log file by a zip file
					ZipUtil.replaceByZipFileConcurrent(oldLogFileName, oldLogFileName+".zip");									
				}				
			}
			recentEventList.add(event);
			if (recentEventList.size() > MAX_RECENT_EVENTS) {
				recentEventList.removeFirst();
			}
		}
	}
	
	public Element createEvent(String eventType) {
		Element event = XMLOutputStreamEventWriter.createElement();
		event.addElement("event_type").setText(eventType);
		Date now = new Date();
		event.addElement("timestamp").setText(timestampSDF.format(now));
		long eventId = now.getTime();
		String eventIdStr = null;
		synchronized (this) {
			if (lastGeneratedEventId[0] == eventId) {
				eventIdStr = eventId+"/"+lastGeneratedEventId[1];
				lastGeneratedEventId[1]++;
			}
			else {
				lastGeneratedEventId[0] = eventId;
				lastGeneratedEventId[1] = 1;
				eventIdStr = eventId+"/0";
			}
		}
		event.addAttribute("id",eventIdStr);
		return event;
	}
	
	public String getAccessKey() {
		return this.accessKey;
	}
	
	public static TomcatCollector getTomcatCollector(ServletContext context) {
		return (TomcatCollector)context.getAttribute(ATTRIBUTE_ID);
	}
	
	public boolean isInternalServletRequest(ServletRequest request) {
		if (request instanceof HttpServletRequest) {
			String uri = ((HttpServletRequest)request).getRequestURI();
			if (uri.endsWith("TomcatCollectorPublicServlet")) {
				return true;
			}
		}
		return false;
	}
	
	public List<Element> getRecentEvents(int k) {
		k = Math.min(MAX_RECENT_EVENTS, k);
		List<Element> events = new ArrayList<Element>(k);
		Iterator<Element> iter = this.recentEventList.descendingIterator();
		int i =0;
		while(i < k && iter.hasNext()){
			events.add(iter.next());
			i++;
		}
		return events;
	}

}
