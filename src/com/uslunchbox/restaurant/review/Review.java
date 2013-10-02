package com.uslunchbox.restaurant.review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;



public class Review {
	private Integer id;
	private Date date;
	private String content;
	private Integer rating;
	private Integer Dish_id;
	private Integer User_id;
	
	private static final String QUERY_MAX_REVIEWID = "SELECT max(review_id) from review_dish";
	
	private static final String QUERT_DAYILY_RATING = "SELECT DATE_FORMAT(date,'%b-%e'), AVG(rating) FROM review_dish WHERE dish_id=? group by DATE_FORMAT(date,'%b-%e')";
	
	public static JSONArray getAllReviewsForUser(Integer userID)
			throws SQLException, JSONException {
		JSONArray jarray = new JSONArray();
		String query="select r.review_id, r.date, r.content, r.rating from review_dish r"
				+ " WHERE r.user_id = ? ORDER BY r.date DESC";
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
			jobject.put("reviewid", rs.getInt(1));
			jobject.put("reviewdate", rs.getDate("date") + " " + rs.getTime("date"));
			jobject.put("reviewcontent", rs.getString("content"));
			jobject.put("reviewrating", rs.getInt("rating"));

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

	public static JSONArray getAllReviewsForDish(Integer dishID)
			throws SQLException, JSONException {
		JSONArray jarray = new JSONArray();
		String query="SELECT r.review_id,r.date,r.rating,r.content,r.user_id,r.dish_id,u.first_name,u.last_name FROM review_dish r JOIN users u ON r.user_id=u.user_id WHERE r.dish_id=? ORDER BY r.date DESC";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, dishID);
			rs = ps.executeQuery();
			while (rs.next()) {
		
			JSONObject jobject = new JSONObject();
			jobject.put("reviewid", rs.getInt("review_id"));
			jobject.put("reviewdate",
					rs.getDate("date") + " " + rs.getTime("date"));
			jobject.put("reviewcontent", rs.getString("content"));
			jobject.put("reviewrating", rs.getInt("rating"));
			jobject.put("userid", rs.getInt("user_id"));
			jobject.put("dishid", rs.getInt("dish_id"));
			jobject.put("username", rs.getString("first_name"));
			
			//jobject.put("useravatar", rs.getString("avatar"));
			jobject.put("likereview",
					LikeReview.getLikeReviewByReview(rs.getInt("review_id")));
			jobject.put("comments",
					Comment.getCommentsForReview(rs.getInt("review_id")));
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

	public static JSONObject getAllReviewsWithDateForDish(int dishID) throws SQLException, JSONException {
		JSONObject jarray = new JSONObject();
		
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(QUERT_DAYILY_RATING);
			ps.setInt(1, dishID);
			rs = ps.executeQuery();
			JSONArray drillC = new JSONArray();
			JSONArray drillD = new JSONArray(); 
			while(rs.next()){
				drillC.put(rs.getString(1));
				drillD.put(rs.getDouble(2));
			}
			
		/*	while (rs.next()) {
		
			JSONObject jobject = new JSONObject();
//			jobject.put("reviewid", rs.getInt("review_id"));
			jobject.put("categories",
					rs.getString(1));
//			jobject.put("reviewcontent", rs.getString("content"));
			jobject.put("data", rs.getInt(2));
//			jobject.put("userid", rs.getInt("user_id"));
//			jobject.put("dishid", rs.getInt("dish_id"));
//			jobject.put("username", rs.getString("first_name"));
			
			//jobject.put("useravatar", rs.getString("avatar"));
//			jobject.put("likereview",
//					LikeReview.getLikeReviewByReview(rs.getInt("review_id")));
//			jobject.put("comments",
//					Comment.getCommentsForReview(rs.getInt("review_id")));
			jarray.put(jobject);
		}*/
			jarray.put("categories",drillC);
			jarray.put("data",drillD);
			
			
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
	public static JSONArray getReview(Integer reviewID) throws SQLException,
			JSONException {
		JSONArray jarray = new JSONArray();
		String query="SELECT r.review_id, r.date, r.content, r.rating,r.user_id FROM review_dish r WHERE id = ?;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, reviewID);
			rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject jobject = new JSONObject();
			jobject.put("reviewid", rs.getInt(1));
			jobject.put("reviewdate", rs.getDate("date")+ " " + rs.getTime("date"));
			jobject.put("reviewcontent", rs.getString("content"));
			jobject.put("reviewrating", rs.getString("rating"));
			jobject.put("reviewuserid", rs.getString("user_id"));

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

	// SELECT U.name FROM uslunchbox_review.review R JOIN uslunchbox_review.user
	// U on R.user_id = U.id;

	public static JSONArray getReviewAndName(Integer reviewID)
			throws SQLException, JSONException {
		JSONArray jarray = new JSONArray();
		String query="SELECT R.id,R.date,R.content,R.rating,R.user_id,U.name FROM review_dish R JOIN users U on R.user_id = U.user_id AND R.review_id = ? ";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, reviewID);
			rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject jobject = new JSONObject();
			jobject.put("reviewid", rs.getInt(1));
			jobject.put("reviewdate", rs.getDate("date")+ " " + rs.getTime("date"));
			jobject.put("reviewcontent", rs.getString("content"));
			jobject.put("reviewrating", rs.getString("rating"));
			jobject.put("reviewuserid", rs.getString("user_id"));
			jobject.put("reviewauthor", rs.getString("name"));

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

	public static JSONArray getAllReviews() throws SQLException, JSONException {
		JSONArray jarray = new JSONArray();
		String query="SELECT r.review_id, r.date, r.content, r.rating, r.dish_id, r.user_id, d.name dname, u.first_name uname FROM review_dish r JOIN users u JOIN dish d ON r.user_id=u.user_id AND r.dish_id=d.dish_id ORDER BY dish_id, date";
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
			jobject.put("reviewid", rs.getInt("id"));
			jobject.put("reviewdate", rs.getDate("date"));
			jobject.put("reviewcontent", rs.getString("content"));
			jobject.put("reviewrating", rs.getInt("rating"));
			jobject.put("reviewdishid", rs.getInt("dish_id"));
			jobject.put("reviewuserid", rs.getInt("user_id"));
			jobject.put("dishname", rs.getString("dname"));
			jobject.put("username", rs.getString("uname"));
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

	public static JSONArray getReviewsByPhraseID(Integer phraseID)
			throws SQLException, JSONException {

		
		JSONArray jarray = new JSONArray();
		String query="SELECT review_id FROM review_has_phrase WHERE phrase_id = ?;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, phraseID);
			rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject jobject = new JSONObject();
			jobject.put("reviewid", rs.getInt("review_id"));
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

	public static Boolean updateReview(int rating, String content, int user_id,
			int dish_id, int review_id) throws SQLException {
		String sql="UPDATE review_dish SET rating='?' content='?'  WHERE review_id='?';";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		int rs=0;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			
		ps.setInt(2, rating);
		ps.setString(3, content);
		ps.setInt(4, review_id);
		ps.setInt(5, user_id);
		ps.setInt(6, dish_id);
		
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
	
	public static Boolean deleteReview(int id) throws SQLException {
		String sql="DELETE FROM review_dish WHERE review_id=?;";
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

	public static Boolean insertReview(int rating, String content, int user_id,
			int dish_id) throws SQLException {
		int max = getMaxReviewId();
		
		String sql="INSERT INTO review_dish (date, rating, content,user_id, dish_id,review_id ) VALUES (NOW(),?,?,?,?,?);";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		int rs=0;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, rating);
			ps.setString(2, content);
			ps.setInt(3, user_id);
			ps.setInt(4, dish_id);
			ps.setInt(5, max+1);
			rs = ps.executeUpdate();
			List<String> gramMaster = new ArrayList<String>();
			gramMaster.addAll(Arrays.asList(content.split(" ")));
			List<String> twoGram = Phrase.ngrams(content, 2);
			gramMaster.addAll(twoGram);
			List<String> threeGram = Phrase.ngrams(content, 3);
			gramMaster.addAll(threeGram);
			for (String s : gramMaster) {
				//System.out.println(s);
				if (Phrase.existsPhrase(s)) {

				} else {
					Phrase.insertPhrase(s);
				}
				int phrase_id = Phrase.getPhraseIDByPhrase(s);
				Phrase.insertHasPhrase(dish_id, max+1, phrase_id);
			}
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

	
	
	public static JSONArray getMostLikeReview(int review_id) throws SQLException,
			JSONException {
		JSONArray jarray = new JSONArray();
		String query="SELECT u.first_name, LEFT(r.content,50) as content,r.date,r.rating,lr.review_id, COUNT(*) AS like_count FROM users u, review_dish r JOIN user_likes_review lr ON r.review_id = lr.review_id where u.user_id=lr.user_id AND r.dish_id=? ORDER BY like_count DESC, review_id ASC LIMIT 0,3;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, review_id);
			rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject jobject = new JSONObject();
			jobject.put("reviewid", rs.getInt("review_id"));
			jobject.put("reviewnumlikes", rs.getString("like_count"));
			jobject.put("reviewcontent", rs.getString("content"));
			jobject.put("reviewdate", rs.getDate("date") + " " + rs.getTime("date"));
			jobject.put("reviewrating", rs.getInt("rating"));
			jobject.put("username", rs.getString("first_name"));
			jobject.put("likereview",
					LikeReview.getLikeReviewByReview(rs.getInt("review_id")));
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

	public Integer getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public void setDishId(int dish_id) {
		this.Dish_id = dish_id;
	}

	public Integer getDishId() {
		return Dish_id;
	}

	public void setUserId(int user_id) {
		this.User_id = user_id;
	}

	public Integer getUserId() {
		return User_id;
	}

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

}
