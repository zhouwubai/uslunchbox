package com.uslunchbox.restaurant.owner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;

public class OwnerRevenueManagement {
	
	private long ownerId;
	
	private static final String QUERY_PREPAID_USERS = "select a.first_name, a.last_name, b.points from users a, points b where a.user_id = b.user_id and b.points > 0";
	
	// query owner revenue statistics
	private static final String QUERY_REVENUE_STATISTICS = "select date_format(deliver_time,'%Y-%M'), " +
			" sum(total_price) from restaurant_owners ro, orders o" +
			" where ro.owner_id = ? and ro.restaurant_id = o.restaurant_id and o.status <> 'unplaced'" +
			" group by date_format(deliver_time,'%Y-%M')";
	private static final String QUERY_MONTHLY_REVENUE_STATISTICS = "select date_format(deliver_time,'%b-%D')," +
			" sum(total_price) from restaurant_owners ro, orders o" +
			" where ro.owner_id = ? and ro.restaurant_id = o.restaurant_id and o.status <> 'unplaced'" +
			" and date_format(deliver_time,'%Y-%M') = ? group by date_format(deliver_time,'%b-%D')";
	
	// query owner usage fee
	private static final String QUERY_USAGE_STATISTICS = "select date_format(deliver_time,'%Y-%M'), " +
			" (sum(total_price)-5)*0.1 from restaurant_owners ro, orders o" +
			" where ro.owner_id = ? and ro.restaurant_id = o.restaurant_id" +
			" group by date_format(deliver_time,'%Y-%M')" +
			" order by date_format(deliver_time,'%Y-%M')";
	private static final String QUERY_MONTHLY_USAGE_STATISTICS = "select date_format(deliver_time,'%b-%D')," +
			" (sum(total_price)-5)*0.1 from restaurant_owners ro, orders o" +
			" where ro.owner_id = ? and ro.restaurant_id = o.restaurant_id and o.status <> 'unplaced'" +
			" and date_format(deliver_time,'%Y-%M') = ? group by date_format(deliver_time,'%b-%D')" +
			" order by date_format(deliver_time,'%b-%D')";
	
	public OwnerRevenueManagement(long ownerId) {
		this.setOwnerId(ownerId);
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	
	/**
	 * query prepaid users
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONArray queryPrepaidUsers(){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet ret = null;
		JSONArray retJson = new JSONArray();
		try{
			pstmt = conn.prepareStatement(QUERY_PREPAID_USERS);
			ret = pstmt.executeQuery();
			while(ret.next()){
				JSONObject json = new JSONObject();
				json.put("firstname", ret.getString(1));
				json.put("lastname", ret.getString(2));
				json.put("points", ret.getInt(3));
				retJson.add(json);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
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
	
	/**
	 * query owner's revenue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject queryOwnerRevenueStatistics(){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		ResultSet ret = null;
		ResultSet ret2 = null;
		JSONObject retJson = new JSONObject();
		JSONArray categories = new JSONArray();
		JSONArray data = new JSONArray();
		retJson.put("name", "Revenue");
		try{
			pstmt = conn.prepareStatement(QUERY_REVENUE_STATISTICS);
			pstmt.setLong(1, this.ownerId);
			pstmt2 = conn.prepareStatement(QUERY_MONTHLY_REVENUE_STATISTICS);
			ret = pstmt.executeQuery();
			while(ret.next()){
				categories.add(ret.getString(1));
				JSONObject dataM = new JSONObject();
				dataM.put("y", ret.getDouble(2));
				dataM.put("color", "#AA4643");
				JSONObject drill = new JSONObject();
				drill.put("name", ret.getString(1) + " Revenue");
				pstmt2.setLong(1, this.ownerId);
				pstmt2.setString(2, ret.getString(1));
				ret2 = pstmt2.executeQuery();
				JSONArray drillC = new JSONArray();
				JSONArray drillD = new JSONArray(); 
				while(ret2.next()){
					drillC.add(ret2.getString(1));
					drillD.add(ret2.getDouble(2));
				}
				drill.put("categories", drillC);
				drill.put("data", drillD);
				drill.put("color", "#AA4643");
				dataM.put("drilldown", drill);
				data.add(dataM);
			}
			retJson.put("categories", categories);
			retJson.put("data", data);
		}catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				pstmt.close();
				if(pstmt2 != null)
					pstmt2.close();
				if(ret != null)
					ret.close();
				if(ret2 != null)
					ret2.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return retJson;
	}
	
	/**
	 * query usage fee of an owner
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject queryOwnerRevenueStatement(){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		ResultSet ret = null;
		ResultSet ret2 = null;
		JSONObject retJson = new JSONObject();
		JSONArray categories = new JSONArray();
		JSONArray data = new JSONArray();
		retJson.put("name", "Revenue");
		try{
			pstmt = conn.prepareStatement(QUERY_USAGE_STATISTICS);
			pstmt.setLong(1, this.ownerId);
			pstmt2 = conn.prepareStatement(QUERY_MONTHLY_USAGE_STATISTICS);
			ret = pstmt.executeQuery();
			while(ret.next()){
				categories.add(ret.getString(1));
				JSONObject dataM = new JSONObject();
				dataM.put("color", "#AA4643");
				JSONObject drill = new JSONObject();
				drill.put("name", ret.getString(1) + " Revenue");
				pstmt2.setLong(1, this.ownerId);
				pstmt2.setString(2, ret.getString(1));
				ret2 = pstmt2.executeQuery();
				JSONArray drillC = new JSONArray();
				JSONArray drillD = new JSONArray(); 
				double usage = 0.0;
				while(ret2.next()){
					drillC.add(ret2.getString(1));
					if(ret2.getDouble(2) < 0){
						drillD.add(0);
						continue;
					}
					drillD.add(ret2.getDouble(2));
					usage += ret2.getDouble(2);
				}
				drill.put("categories", drillC);
				drill.put("data", drillD);
				drill.put("color", "#AA4643");
				dataM.put("drilldown", drill);
				dataM.put("y", usage);
				data.add(dataM);
			}
			retJson.put("categories", categories);
			retJson.put("data", data);
		}catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				pstmt.close();
				if(pstmt2 != null)
					pstmt2.close();
				if(ret != null)
					ret.close();
				if(ret2 != null)
					ret2.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return retJson;
	}

}
