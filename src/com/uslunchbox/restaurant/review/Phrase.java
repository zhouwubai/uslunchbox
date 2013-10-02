package com.uslunchbox.restaurant.review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;


public class Phrase {

	private Integer id;
	private String phrase;
	
	private static final String QUERY_MAX_PHRASEID = "SELECT max(phrase_id) from review_phrases";
	
	public static JSONArray getAllPhrases() throws SQLException, JSONException {

		JSONArray jarray = new JSONArray();
		String query = "SELECT phrase_id, phrase FROM phrases;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject jobject = new JSONObject();
			jobject.put("phraseid", rs.getInt("phrase_id"));
			jobject.put("phrasesphrase", rs.getString("phrase"));

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

	// Select Phrase for a Dish
	public static JSONArray PhraseByDish(Integer dishID) throws SQLException, JSONException{
		
		JSONArray jarray = new JSONArray();
		String query = "SELECT DISTINCT p.phrase_id, p.phrase FROM review_phrases p JOIN review_has_phrase h JOIN "+
				"dish d ON p.phrase_id=h.phrase_id AND d.dish_id=h.dish_id WHERE d.dish_id='?';";
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
			jobject.put("phraseid", rs.getInt(1));
			jobject.put("phrase", rs.getString(2));
			jarray.put(jobject);
		}
		ps.close();
		rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jarray;
	}
	
	public static JSONArray getPhraseById(int id) throws SQLException,
			JSONException {
		

		
		JSONArray jarray = new JSONArray();
		String query = "SELECT phrase_id, phrase  FROM review_phrases WHERE id = ?;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, id);
			rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject jobject = new JSONObject();
			jobject.put("phraseid", rs.getInt("phrase_id"));
			jobject.put("phrasesphrase", rs.getString("phrase"));

			jarray.put(jobject);
		}
		ps.close();
		rs.close();
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

	public static Boolean UpdatePhrase(int id, String phrase)
			throws SQLException {

		String sql="UPDATE review_phrases SET phrase='?'  WHERE id='?';";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet keys1 = null;
		int rs=0;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, phrase);
			ps.setInt(2, id);
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

	public static Boolean insertPhrase(String phrase)
			throws SQLException {
		int max=getMaxPhraseId();
		String sql="INSERT INTO review_phrases ( phrase,phrase_id) VALUES (?,?);";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet keys1 = null;
		int rs=0;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
		ps.setString(1, phrase);
		ps.setInt(2, max+1);
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
	
	public static Boolean existsPhrase(String phrase)
			throws SQLException {
		String sql="SELECT EXISTS (SELECT phrase FROM review_phrases WHERE phrase = ?) AS phraseexists;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet keys1 = null;
		Boolean rs=false;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
		ps.setString(1, phrase);
		keys1 = ps.executeQuery();
		while (keys1.next()) {
			rs = keys1.getBoolean("phraseexists");
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
		return rs;
	}
	
	//From http://stackoverflow.com/questions/3656762/n-gram-generation-from-a-sentence
	public static List<String> ngrams(String s, int len) {
	    try
	    {
		String[] parts = s.split(" ");
	    List<String> result = new ArrayList<String>();
	    for(int i = 0; i < parts.length - len + 1; i++) {
	       StringBuilder sb = new StringBuilder();
	       for(int k = 0; k < len; k++) {
	           if(k > 0)
	           	{
	        	   sb.append(" ");
	        	}
	           
	           sb.append(parts[i+k]);
	       }
	       result.add(sb.toString());
	    }
	    return result;
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    	return new ArrayList<String>();
	    }
	    
	}
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public static int getPhraseIDByPhrase(String phrase) throws SQLException {
		
		JSONArray jarray = new JSONArray();
		String query ="SELECT phrase_id FROM review_phrases WHERE phrase = ?;";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int phrase_id = 0;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, phrase);
			rs = ps.executeQuery();
		while (rs.next()) {
			phrase_id = rs.getInt("phrase_id");
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
		return phrase_id;
	}

	public static boolean insertHasPhrase(int dish_id, int review_id,
			int phrase_id) throws SQLException {
		
		String sql="INSERT INTO review_has_phrase ( dish_id, review_id, phrase_id) VALUES (?,?,?);";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet keys1 = null;
		int rs=0;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
		ps.setInt(1, dish_id);
		ps.setInt(2, review_id);
		ps.setInt(3, phrase_id);
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

	public static JSONArray getPhraseHighlights(int dishID) throws SQLException, JSONException {
		
		
		
		JSONArray jarray = new JSONArray();
		String query = "SELECT dish_id, COUNT(*) AS phrasecount, lower(phrase) AS phrase, p.phrase_id,(LENGTH(phrase) - LENGTH(REPLACE(phrase, ' ', ''))) AS phrase_length FROM review_has_phrase hp JOIN review_phrases p on hp.phrase_id = p.phrase_id AND dish_id = ? GROUP BY phrase_id HAVING phrasecount>1 ORDER BY phrasecount DESC,phrase_length DESC limit 0,3;";
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
			jobject.put("dishid", rs.getInt("dish_id"));
			jobject.put("phrasecount", rs.getInt("phrasecount"));
			jobject.put("phrase", rs.getString("phrase"));
			jobject.put("phraseid", rs.getString("phrase_id"));
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
	
	private static int getMaxPhraseId() {
		Connection conn = ConnectionManager.getInstance().getConnection();
		int max = 0;

		PreparedStatement pstmt = null;
		ResultSet retUser = null;
		try {
			pstmt = conn.prepareStatement(QUERY_MAX_PHRASEID);
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
