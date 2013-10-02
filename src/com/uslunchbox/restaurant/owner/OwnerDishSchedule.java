package com.uslunchbox.restaurant.owner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.uslunchbox.restaurant.database.ConnectionManager;

/**
 * 
 * @author lli003
 *
 */

public class OwnerDishSchedule {
	
	private long ownerId;
	
	private static final String QUERY_OWNER_SITE = "select s.site_id, s.area from sites s, site_restaurants sr, restaurant_owners ro" +
			" where s.site_id = sr.site_id and sr.restaurant_id = ro.restaurant_id and ro.owner_id = ?";
	private static final String QUERY_DISH_LIST = "select d.dish_id, d.dish_english from dish d, restaurant_owners ro " +
			"where d.restaurant_id = ro.restaurant_id and ro.owner_id = ?";
	private static final String DELETE_DISH_SCHEDULE = "delete from dish_schedule where dish_id = ?" +
			" and site_id = ? and dish_schedule_time = ?";
	private static final String ADD_DISH_SCHEDULE = "insert into dish_schedule" +
			" (dish_id,site_id,dish_schedule_time) values (?,?,?)";
	private static final String QUERY_DISH_SCHEDULE = 
			"select ds.dish_schedule_time, date_format(ds.dish_schedule_time, '%a'), " +
			"d.dish_id, d.dish_english, s.site_id, s.area " +
			"from sites s, site_restaurants sr, dish_schedule ds, dish d, restaurant_owners ro " +
			"where s.site_id = sr.site_id and sr.site_id = ds.site_id and d.dish_id = ds.dish_id " +
			"and sr.restaurant_id = d.restaurant_id and d.restaurant_id = ro.restaurant_id " +
			"and to_days(ds.dish_schedule_time)-to_days(now())>=0 " +
			"and ro.owner_id = ? order by dish_schedule_time, s.site_id";
	
	public OwnerDishSchedule(long ownerId) {
		this.ownerId = ownerId;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray queryOwnerSites(){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet ret = null;
		JSONArray retJson = new JSONArray();
		try{
			pstmt = conn.prepareStatement(QUERY_OWNER_SITE);
			pstmt.setLong(1, this.ownerId);
			ret = pstmt.executeQuery();
			while(ret.next()){
				JSONObject json = new JSONObject();
				json.put("siteid", ret.getInt(1));
				json.put("sitename", ret.getString(2));
				retJson.add(json);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				if(pstmt != null)
					pstmt.close();
				if(ret != null)
					ret.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return retJson;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray queryDishList(){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet ret = null;
		JSONArray retJson = new JSONArray();
		try{
			pstmt = conn.prepareStatement(QUERY_DISH_LIST);
			pstmt.setLong(1, this.ownerId);
			ret = pstmt.executeQuery();
			while(ret.next()){
				JSONObject json = new JSONObject();
				json.put("dishid", ret.getInt(1));
				json.put("dishname", ret.getString(2));
				json.put("img", "food_"+ret.getInt(1) + ".jpg");
				retJson.add(json);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				if(pstmt != null)
					pstmt.close();
				if(ret != null)
					ret.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return retJson;
	}
	
	public boolean deleteDishSchedule(int dishId, int siteId, String date){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try{
			java.util.Date parsedDate = dateFormat.parse(date);
			java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
			
			pstmt = conn.prepareStatement(DELETE_DISH_SCHEDULE);
			pstmt.setLong(1, dishId);
			pstmt.setLong(2, siteId);
			pstmt.setTimestamp(3, timestamp);
			return pstmt.execute();
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if(pstmt != null)
					pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean addDishSchedule(int dishId, int siteId, String date){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try{
			java.util.Date parsedDate = dateFormat.parse(date);
			java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
			
			pstmt = conn.prepareStatement(ADD_DISH_SCHEDULE);
			pstmt.setLong(1, dishId);
			pstmt.setLong(2, siteId);
			pstmt.setTimestamp(3, timestamp);
			return pstmt.execute();
		} catch (MySQLIntegrityConstraintViolationException e){
//			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if(pstmt != null)
					pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray queryDishSchedules(){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet ret = null;
		JSONArray retJson = new JSONArray();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try{
			pstmt = conn.prepareStatement(QUERY_DISH_SCHEDULE);
			pstmt.setLong(1, this.ownerId);
			ret = pstmt.executeQuery();
			while(ret.next()){
				JSONObject json = new JSONObject();
				json.put("time", dateFormat.format(ret.getTimestamp(1)));
				json.put("weekday", ret.getString(2));
				json.put("dishid", ret.getInt(3));
				json.put("dishname", ret.getString(4));
				json.put("siteid", ret.getInt(5));
				json.put("sitename", ret.getString(6));
				retJson.add(json);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				if(pstmt != null)
					pstmt.close();
				if(ret != null)
					ret.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return retJson;
	}

}
