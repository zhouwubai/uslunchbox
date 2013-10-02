package com.uslunchbox.restaurant.review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;

public class LikeReview {
	private Integer user_id;
	private Integer review_id;

	public LikeReview(Integer user_id, Integer review_id) {
		super();
		this.user_id = user_id;
		this.review_id = review_id;
	}

	public static JSONArray getAllLikeReview() throws SQLException,
			JSONException {

		JSONArray jarray = new JSONArray();
		String query = "SELECT l.user_id,review_id,u.first_name,LEFT(r.content,50) content FROM user_likes_review l JOIN users u JOIN review_dish r ON l.user_id=u.user_id AND l.review_id=r.id ORDER BY review_id,user_id";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject jobject = new JSONObject();
				jobject.put("userid", rs.getInt("user_id"));
				jobject.put("reviewid", rs.getInt("review_id"));
				jobject.put("username", rs.getString("first_name"));
				jobject.put("preview", rs.getString("content"));
				jarray.put(jobject);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				rs.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jarray;
	}

	public static JSONArray getLikeReviewByUser(Integer userID)
			throws SQLException, JSONException {
		JSONArray jarray = new JSONArray();
		String query = "SELECT l.user_id,l.review_id FROM user_likes_review l WHERE l.user_id=?";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, userID);
			rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject jobject = new JSONObject();
				jobject.put("userid", rs.getInt("user_id"));
				jobject.put("review_id", rs.getInt("review_id"));
				jarray.put(jobject);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				rs.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// controler.close();
		return jarray;
	}

	public static JSONArray getLikeReviewByReview(Integer reviewID)
			throws SQLException, JSONException {

		
		JSONArray jarray = new JSONArray();
		String query = "SELECT l.user_id,l.review_id,u.first_name FROM user_likes_review l JOIN users u ON l.user_id=u.user_id WHERE l.review_id=?;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, reviewID);
			rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject jobject = new JSONObject();
			jobject.put("userid", rs.getInt("user_id"));
			jobject.put("review_id", rs.getInt("review_id"));
			jobject.put("username", rs.getString("first_name"));
			//jobject.put("useravatar", rs.getString("avatar"));
			jarray.put(jobject);
		}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				rs.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// controler.close();
		return jarray;
	}

	public static Boolean newLikeReview(Integer reviewID, Integer userID)
			throws SQLException {
		String sql="INSERT INTO user_likes_review (user_id,review_id) VALUES (?,?);";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		int rs=0;
		try {
		conn = ConnectionManager.getInstance().getConnection();
		ps = conn.prepareStatement(sql);
		ps.setInt(1, userID);
		ps.setInt(2, reviewID);
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

	public static Boolean unLikeReview(Integer reviewID, Integer userID)
			throws SQLException {
		String sql="DELETE FROM user_likes_review WHERE user_id=? AND review_id=?;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		int rs=0;
		try {
		conn = ConnectionManager.getInstance().getConnection();
		ps = conn.prepareStatement(sql);
		ps.setInt(1, userID);
		ps.setInt(2, reviewID);
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

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public Integer getReview_id() {
		return review_id;
	}

	public void setReview_id(Integer review_id) {
		this.review_id = review_id;
	}
}
