package com.uslunchbox.restaurant.review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;

public class Comment {

	private Integer id;
	private Date date;
	private String content;

	private static final String QUERY_MAX_COMMENTID = "SELECT max(comment_id) from review_comments";
	
	public static JSONArray getAllCommentsForUser(Integer userID)
			throws SQLException, JSONException {

		JSONArray jarray = new JSONArray();
		String query="select c.comment_id, c.date, c.content from comments c, users u"
				+ " WHERE u.user_id = c.comment_id and u.user_id = ?;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, userID);
			rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject jobject = new JSONObject();
			jobject.put("commentid", rs.getInt(1));
			jobject.put("commentdate", rs.getDate("date"));
			jobject.put("commentcontent", rs.getString("content"));
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

	public static JSONArray getComment(Integer commentID) throws SQLException,
			JSONException {
		JSONArray jarray = new JSONArray();
		String query="SELECT c.comment_id, c.date, c.content FROM review_comments c WHERE comment_id = ?;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, commentID);
			rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject jobject = new JSONObject();
			jobject.put("commentid", rs.getInt(1));
			jobject.put("commentdate", rs.getDate("date"));
			jobject.put("commentcontent", rs.getString("content"));
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
//		controler.close();
		return jarray;
	}

	public static JSONArray getAllComments() throws SQLException, JSONException {

		
		JSONArray jarray = new JSONArray();
		String query="SELECT c.comment_id, c.date, c.content, c.user_id, c.review_id, u.first_name, r.rating, LEFT(r.content,100) rcontent FROM review_comments c JOIN users u JOIN dish_reviews r ON c.user_id=u.user_id AND c.review_id=r.review_id ORDER BY review_id, date DESC, user_id;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject jobject = new JSONObject();
			jobject.put("commentid", rs.getInt("comment_id"));
			jobject.put("commentdate", rs.getDate("date"));
			jobject.put("commentcontent", rs.getString("ccontent"));
			jobject.put("userid", rs.getInt("user_id"));
			jobject.put("reviewid", rs.getInt("review_id"));
			jobject.put("username", rs.getString("first_name"));
			jobject.put("reviewrating", rs.getInt("rating"));
			jobject.put("reviewcontent", rs.getString("rcontent"));
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
//		controler.close();
		return jarray;
	}

	public static JSONArray getCommentsForReview(Integer reviewID)
			throws SQLException, JSONException {

		
		
		JSONArray jarray = new JSONArray();
		String query="SELECT c.comment_id,c.date,c.content,c.user_id,c.review_id,u.first_name FROM review_comments c JOIN users u ON c.user_id=u.user_id WHERE c.review_id=?  ORDER BY c.date;";
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
			jobject.put("commentid", rs.getInt("comment_id"));
			jobject.put("commentdate",
					rs.getDate("date") + " " + rs.getTime("date"));
			jobject.put("commentcontent", rs.getString("content"));
			jobject.put("userid", rs.getInt("user_id"));
			jobject.put("reviewid", rs.getInt("review_id"));
			jobject.put("username", rs.getString("first_name"));
			
			jobject.put("likecomment",
					LikeComment.getLikeCommentByComment(rs.getInt("comment_id")));
			jarray.put(jobject);
		}
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally{
				try {
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

	public static JSONArray getCommentMostLikes(Integer reviewID)
			throws SQLException, JSONException {


		
		JSONArray jarray = new JSONArray();
		String query="SELECT c.comment_id, c.date, c.content, count(c.comment_id) AS like_number "
				+ "FROM review_comments c JOIN likes_comment "
				+ "ON c.comment_id=likes_comment.comment_id "
				+ "GROUP BY c.comment_id " + "ORDER BY like_number DESC;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject jobject = new JSONObject();
			jobject.put("commentid", rs.getInt(1));
			jobject.put("commentdate", rs.getDate("date"));
			jobject.put("commentcontent", rs.getString("content"));
			jobject.put("likes", rs.getInt("like_number"));
			jarray.put(jobject);
		}
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally{
				try {
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

	public static JSONArray getCommentLeastLikes(Integer reviewID)
			throws SQLException, JSONException {

		JSONArray jarray = new JSONArray();
		String query="SELECT c.comment_id, c.date, c.content, count(c.comment_id) AS like_number "
				+ "FROM review_comments c JOIN likes_comment "
				+ "ON c.comment_id=likes_comment.comment_id "
				+ "GROUP BY c.comment_id " + "ORDER BY like_number;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject jobject = new JSONObject();
			jobject.put("commentid", rs.getInt(1));
			jobject.put("commentdate", rs.getDate("date"));
			jobject.put("commentcontent", rs.getString("content"));
			jobject.put("likes", rs.getInt("like_number"));
			jarray.put(jobject);
		}
		}
			catch (SQLException e) {
				e.printStackTrace();
			} finally{
				try {
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

	public static int insertComment(int userID, int reviewID, String content)
			throws SQLException, JSONException {
		int max=getMaxCommentId();
		
		String sql="INSERT INTO review_comments (`date`, `content`, `user_id`, `review_id`, `comment_id`) VALUES (NOW(), ?,?,?,?);";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet keys1 = null;
		int rs=0;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
		
			// conn.setAutoCommit(false);
			

			ps.setString(1, content);
			ps.setInt(2, userID);
			ps.setInt(3, reviewID);
			ps.setInt(4, max+1);
			rs = ps.executeUpdate();
			
			
			if (rs == 0) {
				throw new SQLException(
						"Insert comment failed, no rows affected.");
			}
			
				conn.close();
				return max+1;
			
		} finally {
			if (keys1 != null)
				try {
					keys1.close();
				} catch (SQLException logOrIgnore) {
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException logOrIgnore) {
				}
		}
		// return rows != 0;
	}

	public static int updateComment(int commentID, int userID, int reviewID,
			String content) throws SQLException, JSONException {
		String sql="UPDATE review_comments"
				+ " SET `date` = NOW(), `content`= ?, `user_id` = ?, `review_id` = ? WHERE comment_id = ?;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet keys1 = null;
		int rs=0;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, content);
			ps.setInt(2, userID);
			ps.setInt(3, reviewID);
			ps.setInt(4, commentID);
			rs = ps.executeUpdate();
			if (rs == 0) {
				throw new SQLException(
						"Update review_comments failed, no rows affected.");
			}
			keys1 = ps.getGeneratedKeys();
			if (keys1.next()) {
				return keys1.getInt(1);
			} else {
				throw new SQLException(
						"Update review_comments failed, no generated key obtained.");
			}
			// return rows != 0;
		} finally {
			if (keys1 != null)
				try {
					keys1.close();
				} catch (SQLException logOrIgnore) {
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException logOrIgnore) {
				}
		}
	}
	
	public static Boolean deleteComment(int id) throws SQLException {
		String sql="DELETE FROM review_comments WHERE comment_id=?;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		int rs=0;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
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

	public static JSONArray getMostLikeComment() throws SQLException,
			JSONException {

		JSONArray jarray = new JSONArray();
		String query="SELECT c.comment_id, c.date, LEFT(c.content,50) AS content, count(c.comment_id) AS like_number FROM review_comments c JOIN likes_comment ON c.comment_id=likes_comment.comment_id GROUP BY c.comment_id ORDER BY like_number DESC Limit 0,3;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject jobject = new JSONObject();
			jobject.put("commentid", rs.getInt("comment_id"));
			jobject.put("commentdate", rs.getDate("date") + " " + rs.getTime("date"));
			jobject.put("commentcontent", rs.getString("content"));
			jobject.put("commentnumlikes", rs.getString("like_number"));
			jarray.put(jobject);
		}}
		catch (SQLException e) {
			e.printStackTrace();
		} finally{
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	private static int getMaxCommentId() {
		Connection conn = ConnectionManager.getInstance().getConnection();
		int max = 0;

		PreparedStatement pstmt = null;
		ResultSet retUser = null;
		try {
			pstmt = conn.prepareStatement(QUERY_MAX_COMMENTID);
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
}
