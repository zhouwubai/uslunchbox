package com.uslunchbox.restaurant.review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;

public class RestaurantReview {
	
	private static final String QUERY_MAX_REVIEWID = "SELECT max(review_id) from review_restaurant";
	
	private static final String INSERT_NEW_REVIEWID="";
	
	private static final String QERY_RESTAURANT_REVIEW="SELECT r.restaurant_name, " +
			"COALESCE(FORMAT(10*ROUND(AVG(rr.food_rating) * 2) / 2,0),'0') food_rating, " +
			"COALESCE(FORMAT(10*ROUND(AVG(rr.price_rating) * 2) / 2,0),'0') price_rating, " +
			"COALESCE(FORMAT(10*ROUND(AVG(rr.serv_rating) * 2) / 2,0),'0') serv_rating, " +
			"COALESCE(FORMAT(10*ROUND(AVG(rr.atmo_rating) * 2) / 2,0),'0') atmo_rating, " +
			"COUNT(rr.food_rating) AS count, ri.restaurant_intro  " +
			"FROM restaurants_intro ri, restaurants r LEFT JOIN review_restaurant rr " +
			" ON rr.restaurant_id=r.restaurant_id  " +
			"WHERE r.restaurant_id=? AND r.restaurant_id=ri.restaurant_id;";
	
	private static int getMaxReviewId() {
		Connection conn = ConnectionManager.getInstance().getConnection();
		int max = 0;

		PreparedStatement pstmt = null;
		ResultSet retUser = null;
		try {
			pstmt = conn.prepareStatement(QUERY_MAX_REVIEWID);
			retUser = pstmt.executeQuery();
			if (retUser.next()) {
				max = retUser.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				retUser.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return max;
	}
	
	

	public static Boolean insertReview(int food_rating, int price_rating, int serv_rating,
			int atmo_rating, String content,  int user_id,int restaurent_id
			) {
		int max = getMaxReviewId();
		
		String sql="INSERT INTO review_restaurant (date, food_rating,price_rating,serv_rating,atmo_rating, content,user_id,restaurant_id,review_id ) VALUES (NOW(),?,?,?,?,?,?,?,?);";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		int rs=0;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, food_rating);
			ps.setInt(2, price_rating);
			ps.setInt(3, serv_rating);
			ps.setInt(4, atmo_rating);
			ps.setString(5, content);
			ps.setInt(6, user_id);
			ps.setInt(7, restaurent_id);
			ps.setInt(8, max+1);
			rs = ps.executeUpdate();
		
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				ps.close();
				
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	return rs != 0;
	}
	
	
	public static JSONArray getRestaurantRating(int restaurant_id) throws SQLException, JSONException{
		JSONArray jarray = new JSONArray();
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(QERY_RESTAURANT_REVIEW);
			ps.setInt(1, restaurant_id);
			rs = ps.executeQuery();
		while (rs.next()){
			JSONObject jobject = new JSONObject();
			jobject.put("food_rating", rs.getInt("food_rating"));
			jobject.put("price_rating", rs.getInt("price_rating"));
			jobject.put("serv_rating", rs.getInt("serv_rating"));
			jobject.put("atmo_rating", rs.getInt("atmo_rating"));
			jobject.put("restaurant_name", rs.getString("restaurant_name"));
			jobject.put("count", rs.getInt("count"));
			jobject.put("intro", rs.getString("restaurant_intro"));
			jarray.put(jobject);
		}
		ps.close();
		rs.close();
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally{
				try {
					
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		return jarray;
	}
	
}
