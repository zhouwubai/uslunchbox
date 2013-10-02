package com.uslunchbox.restaurant.order;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.uslunchbox.restaurant.database.ConnectionManager;

public class DeliverGeoTime {

	private String abbrev = "";

	private String location = "";

	private double latitude;

	private double longitude;

	private static String QUERYGEOTEMP = "select locationname as location,latitude,longitude, abbrev from geolocation";

	public DeliverGeoTime() {
	}

	public static ArrayList<DeliverGeoTime> getDeliverGeoTimeList() {

		ArrayList<DeliverGeoTime> deliverGeoTimeList = new ArrayList<DeliverGeoTime>();

		Connection conn = ConnectionManager.getInstance().getConnection();
		Statement stmt = null;
		ResultSet rst = null;
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(QUERYGEOTEMP);
			while (rst.next()) {
				DeliverGeoTime deliverGeoTime = new DeliverGeoTime();
				deliverGeoTime.setLocation(rst.getString("location"));
				deliverGeoTime.setLatitude(rst.getDouble("latitude"));
				deliverGeoTime.setLongitude(rst.getDouble("longitude"));
				deliverGeoTime.setAbbrev(rst.getString("abbrev"));
				deliverGeoTimeList.add(deliverGeoTime);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rst != null)
					rst.close();
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return deliverGeoTimeList;

	}

	public String getAbbrev() {
		return abbrev;
	}

	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}


	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}


	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


}
