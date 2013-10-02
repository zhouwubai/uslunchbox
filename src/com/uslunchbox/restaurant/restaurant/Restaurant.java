package com.uslunchbox.restaurant.restaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

import com.uslunchbox.restaurant.database.ConnectionManager;
import com.uslunchbox.restaurant.site.Site;

/**
 * 
 * @author Liang Tang
 * @date Mar 5, 2013 12:01:03 PM
 *
 */
public class Restaurant {
	int restaurant_id;
	String name;
	
	static Map<Integer, Restaurant> restaurants = new HashMap<Integer, Restaurant>();
	
	public Restaurant(int restaurant_id, String name) {
		this.restaurant_id = restaurant_id;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getID() {
		return restaurant_id;
	}
	
	
	public static Restaurant findRestaurant(int restaurant_id) {
		synchronized (restaurants) {
			if (restaurants.isEmpty()) { // Query from the database
				queryAllRestaruants();				
			}
		}
		return restaurants.get(restaurant_id);
	}
	
	/**
	 * Query all restaurants and store into a static hash map
	 */
	private static void queryAllRestaruants() {
		Connection conn = ConnectionManager.getInstance().getConnection();
		try {
			PreparedStatement pstmt = conn.prepareStatement("SELECT restaurant_id, restaurant_name FROM restaurants");
			ResultSet rets = pstmt.executeQuery();
			while (rets.next()) {
				int restaurant_id = rets.getInt("restaurant_id");
				String restaurant_name = rets.getString("restaurant_name");
				Restaurant restaurant = new Restaurant(restaurant_id, restaurant_name);
				restaurants.put(restaurant_id, restaurant);
			}
			rets.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
