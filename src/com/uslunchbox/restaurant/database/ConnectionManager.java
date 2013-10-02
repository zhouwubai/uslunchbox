package com.uslunchbox.restaurant.database;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.uslunchbox.restaurant.utils.HostURL;

public class ConnectionManager {
	private static ConnectionManager instance;
	private static ComboPooledDataSource dataSource;

	private ConnectionManager() throws SQLException, PropertyVetoException {
		dataSource = new ComboPooledDataSource();

		dataSource.setUser("offcampus");
		dataSource.setPassword("kdrg268");
		
//		dataSource.setUser("kdrg_offcampus");
//		dataSource.setPassword("kdrg251");
		
		dataSource.setJdbcUrl(HostURL.DBHOST_URL + "/uslunchbox");
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setInitialPoolSize(5);
//		dataSource.setMinPoolSize(20);
//		dataSource.setMaxPoolSize(40);
		dataSource.setMinPoolSize(5);
		dataSource.setMaxPoolSize(5);
		
		dataSource.setMaxStatements(40);
		dataSource.setMaxIdleTime(20);
	}

	public static final ConnectionManager getInstance() {
		if (instance == null) {
			try {
				instance = new ConnectionManager();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	public synchronized final Connection getConnection() {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
}
