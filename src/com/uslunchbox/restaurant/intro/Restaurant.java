package com.uslunchbox.restaurant.intro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.uslunchbox.restaurant.database.ConnectionManager;

public class Restaurant {
	
	private int restaurantId;
	private String restaurantName;
	private String address;
	private String city;
	private String state;
	private String country;
	private String zip;
	private String phone;
	private String url;
	
	public Restaurant() {
	}

	public static Restaurant getRestaurant(int restaurantId) {
		Restaurant res = new Restaurant();
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rets = null;
		try {
			pstmt = conn.prepareStatement("select restaurant_name," +
					"restaurant_address, restaurant_city, restaurant_state, restaurant_country," +
					"restaurant_zip, restaurant_phone, restaurant_url from restaurants " +
					"where restaurant_id = ?");
			pstmt.setInt(1, restaurantId);
			rets = pstmt.executeQuery();
			if (rets.next()) {
				res.setRestaurantId(restaurantId);
				res.setRestaurantName(rets.getString("restaurant_name"));
				res.setAddress(rets.getString("restaurant_address"));
				res.setCity(rets.getString("restaurant_city"));
				res.setState(rets.getString("restaurant_state"));
				res.setCountry(rets.getString("restaurant_country"));
				res.setZip(rets.getString("restaurant_zip"));
				res.setPhone(rets.getString("restaurant_phone"));
				res.setUrl(rets.getString("restaurant_url"));
			}
			rets.close();
			pstmt.close();
			conn.close();
			return res;
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				rets.close();
				pstmt.close();
				conn.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String getRestaurantIntro(int restaurantId) {
		String intro = "";
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rets = null;
		try {
			pstmt = conn.prepareStatement("select restaurant_intro from restaurants_intro " +
					"where restaurant_id = ?");
			pstmt.setInt(1, restaurantId);
			rets = pstmt.executeQuery();
			if (rets.next()) {
				intro = rets.getString("restaurant_intro");
			}
			rets.close();
			pstmt.close();
			conn.close();
			return intro;
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				rets.close();
				pstmt.close();
				conn.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return intro;
	}
	
	public int getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}
	public String getRestaurantName() {
		return restaurantName;
	}
	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}	

}
