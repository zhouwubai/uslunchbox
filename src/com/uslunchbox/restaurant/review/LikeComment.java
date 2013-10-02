package com.uslunchbox.restaurant.review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;


public class LikeComment {
	private Integer user_id;
	private Integer comment_id;
	public LikeComment(Integer user_id, Integer comment_id) {
		super();
		this.user_id = user_id;
		this.comment_id = comment_id;
	}
	
	public static JSONArray getAllLikeComment() throws SQLException, JSONException{
		JSONArray jarray = new JSONArray();
		String query="SELECT l.user_id,comment_id,u.first_name,LEFT(c.content,50) content FROM user_likes_comment l JOIN users u JOIN comments c ON l.user_id=u.user_id AND l.comment_id=c.id ORDER BY comment_id,user_id";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
		while (rs.next()){
			JSONObject jobject = new JSONObject();
			jobject.put("userid", rs.getInt("user_id"));
			jobject.put("commentid", rs.getInt("comment_id"));
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
		// controler.close();
		return jarray;
	}
	
	public static JSONArray getLikeCommentByUser(Integer userID) throws SQLException, JSONException{
		
		
		JSONArray jarray = new JSONArray();
		String query="SELECT l.user_id,l.comment_id FROM user_likes_comment l WHERE l.user_id=?";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, userID);
			rs = ps.executeQuery();
		while (rs.next()){
			JSONObject jobject = new JSONObject();
			jobject.put("userid", rs.getInt("user_id"));
			jobject.put("commentid", rs.getInt("comment_id"));
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
	
	public static JSONArray getLikeCommentByComment(Integer commentID) throws SQLException, JSONException{
		
		
		JSONArray jarray = new JSONArray();
		String query="SELECT l.user_id,l.comment_id,u.first_name FROM user_likes_comment l JOIN users u ON l.user_id=u.user_id WHERE l.comment_id=?;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, commentID);
			rs = ps.executeQuery();
		while (rs.next()){
			JSONObject jobject = new JSONObject();
			jobject.put("userid", rs.getInt("user_id"));
			jobject.put("commentid", rs.getInt("comment_id"));
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

	public static Boolean newLikeComment(Integer commentID, Integer userID) throws SQLException {
		String sql="INSERT INTO user_likes_comment (user_id,comment_id) VALUES (?,?);";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		int rs=0;
		try {
		conn = ConnectionManager.getInstance().getConnection();
		ps = conn.prepareStatement(sql);
		ps.setInt(1, userID);
		ps.setInt(2, commentID);
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
	
	public static Boolean unLikeComment(Integer commentID, Integer userID) throws SQLException {
		String sql="DELETE FROM user_likes_comment WHERE user_id=? AND comment_id=?;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		int rs=0;
		try {
		conn = ConnectionManager.getInstance().getConnection();
		ps = conn.prepareStatement(sql);
		ps.setInt(1, userID);
		ps.setInt(2, commentID);
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
	public Integer getComment_id() {
		return comment_id;
	}
	public void setComment_id(Integer comment_id) {
		this.comment_id = comment_id;
	}
}
