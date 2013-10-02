package com.uslunchbox.restaurant.review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;


public class Rating {
	public static JSONArray getRatingByID(Integer dishID) throws SQLException, JSONException{
		
		
		
		JSONArray jarray = new JSONArray();
		String query="SELECT MAX(IF(a.rating=1,rating_count,0))  AS 'star1', " +
				"MAX(IF(a.rating=2,rating_count,0))  AS 'star2', MAX(IF(a.rating=3,rating_count,0))  AS 'star3', MAX(IF(a.rating=4,rating_count,0))  AS 'star4', " +
				"MAX(IF(a.rating=5,rating_count,0)) AS 'star5' " +" , SUM(a.rating_count) AS 'total' "+
				 " , SUM(a.sum_rating) AS 'total_rating' " +
				"FROM (SELECT d.dish_id, r.rating, COUNT(r.rating) as 'rating_count', SUM(r.rating) as 'sum_rating' FROM review_dish r , dish d WHERE d.dish_id = r.dish_id and d.dish_id = ? GROUP BY r.rating) a Group BY a.dish_id";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, dishID);
			rs = ps.executeQuery();
		while (rs.next()){
			JSONObject jobject = new JSONObject();
			jobject.put("star1", rs.getInt("star1"));
			jobject.put("star2", rs.getInt("star2"));
			jobject.put("star3", rs.getInt("star3"));
			jobject.put("star4", rs.getInt("star4"));
			jobject.put("star5", rs.getInt("star5"));
			jobject.put("total",rs.getInt("total"));
			int total_rating=rs.getInt("total_rating");
			jobject.put("total_rating",total_rating);
			//rs.getInt("total_rating");
			jarray.put(jobject);
		}
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally{
				try {
					if(jarray.isNull(0)){
						JSONObject jobject = new JSONObject();
						jobject.put("star1", 0);
						jobject.put("star2", 0);
						jobject.put("star3", 0);
						jobject.put("star4", 0);
						jobject.put("star5", 0);
						jobject.put("total",0);
						jobject.put("total_rating",0);
						jarray.put(jobject);
					}
					ps.close();
					rs.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
//		controler.close();
		return jarray;
	}
	
	public static JSONArray getRatingByUserID(Integer userID) throws SQLException, JSONException{
		
		JSONArray jarray = new JSONArray();
		String query="SELECT MAX(IF(a.rating=1,rating_count,0))  " +
				"AS '#1',MAX(IF(a.rating=2,rating_count,0))  " +
				"AS '#2',MAX(IF(a.rating=3,rating_count,0))  " +
				"AS '#3',MAX(IF(a.rating=4,rating_count,0))  AS '#4',MAX(IF(a.rating=5,rating_count,0)) " +
				"AS '#5'FROM (SELECT r.user_id, r.rating, COUNT(r.rating) rating_count FROM review_dish r WHERE r.user_id = ? GROUP BY r.rating) a Group BY a.user_id;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, userID);
			rs = ps.executeQuery();
		while (rs.next()){
			JSONObject jobject = new JSONObject();
			jobject.put("#1", rs.getInt("#1"));
			jobject.put("#2", rs.getInt("#2"));
			jobject.put("#3", rs.getInt("#3"));
			jobject.put("#4", rs.getInt("#4"));
			jobject.put("#5", rs.getInt("#5"));
			jarray.put(jobject);
		}
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally{
				try {
					if(jarray.isNull(0)){
						System.out.print("!!!!!!!!!");
					}
					ps.close();
					rs.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
//		controler.close();
		return jarray;
	}
	
	
	
	public static void main(String []args) throws SQLException, JSONException{
		Rating r=new Rating ();
		JSONArray ja=new JSONArray();
		ja=r.getRatingByID(31);
		System.out.print(ja);
	}
	
	
	
	/*
	 * SELECT MAX(IF(a.rating=1,rating_count,0))  AS '#1', MAX(IF(a.rating=2,rating_count,0))  AS '#2', MAX(IF(a.rating=3,rating_count,0))  AS '#3', MAX(IF(a.rating=4,rating_count,0))  AS '#4', MAX(IF(a.rating=5,rating_count,0)) AS '#5' FROM (SELECT d.id, r.rating, COUNT(r.rating) rating_count FROM uslunchbox_review.review r JOIN dish d on d.id = r.dish_id and d.id = 3 GROUP BY r.rating) a Group BY a.id
	 */
	
	
	
	
	
	/*
	 * SELECT d.id, AVG(r.rating) rating_average FROM uslunchbox_review.review r  JOIN dish d on d.id = r.dish_id and d.id = 3 AND (r.date BETWEEN DATE_SUB(CURDATE(), INTERVAL 30 DAY) AND NOW())
	 */
}
