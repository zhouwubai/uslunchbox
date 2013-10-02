package com.uslunchbox.restaurant.owner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;

public class OwnerOrderManagement {

	private long ownerId;

	private static final String QUERY_ORDER_BY_SITE = "select o.order_id, o.location, o.contact_name, o.contact_phone, "
			+ "o.deliver_time, o.status, o.payment from orders o, restaurant_owners r "
			+ "where r.owner_id = ? and r.restaurant_id = o.restaurant_id "
			+ "and o.site_id = ? and o.deliver_time = ? order by o.location, o.order_id";

	private static final String QUERY_OWNER_SITE = "select s.site_id, s.area from sites s, site_restaurants sr, restaurant_owners r "
			+ "where s.site_id = sr.site_id and sr.restaurant_id = r.restaurant_id and r.owner_id = ?";

	private static final String QUERY_DELIVER_TIME = "select distinct(o.deliver_time), date_format(o.deliver_time,'%W') from orders o, restaurant_owners r "
			+ " where r.owner_id = ? and r.restaurant_id = o.restaurant_id and o.site_id = ?"
			+ " and to_days(o.deliver_time)-to_days(now())>=0 order by o.deliver_time";

	private static final String QUERY_DISH_DETAILS = "select d.dish_english, s.staple_food_name, o.quantity from order_dishes o, dish d, staple_food s "
			+ "where o.dish_id = d.dish_id and o.staple_food_id = s.staple_food_id and o.order_id = ?";

	private static final String QUERY_DISH_STATISTICS = "SELECT d.dish_english as dishname, sd.staple_food_name as dishname, sum(od.quantity) as quantity"
			+ " from restaurant_owners ro, orders o, order_dishes od, dish d, staple_food sd"
			+ " where ro.owner_id = ? and o.site_id = ? and o.restaurant_id = ro.restaurant_id"
			+ " and o.order_id = od.order_id and od.dish_id = d.dish_id and od.staple_food_id = sd.staple_food_id"
			+ " and o.deliver_time = ? group by d.dish_english, sd.staple_food_name";

	private static final String QUERY_DAILY_REVENUE = "SELECT o.payment as payment, sum(od.quantity) as quantity, sum(od.quantity * d.dish_lunch_price) as revenue from restaurant_owners ro, orders o, order_dishes od, dish d "
			+ "where ro.owner_id = ? and ro.restaurant_id = o.restaurant_id and o.order_id = od.order_id and od.dish_id = d.dish_id "
			+ "and o.site_id = ? and o.deliver_time = ? group by o.payment";

	private static final String CHANGE_ORDER_STATUS = "update orders set status = ? where order_id = ?";

	public OwnerOrderManagement(long ownerId) {
		this.setOwnerId(ownerId);
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	@SuppressWarnings("unchecked")
	public JSONArray retrieveOwnerSites() {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet ret = null;
		JSONArray retJson = null;
		try {
			pstmt = conn.prepareStatement(QUERY_OWNER_SITE);
			retJson = new JSONArray();
			pstmt.setLong(1, this.ownerId);
			ret = pstmt.executeQuery();
			while (ret.next()) {
				JSONObject json = new JSONObject();
				json.put("siteid", ret.getInt(1));
				json.put("sitearea", ret.getString(2));
				retJson.add(json);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (ret != null)
					ret.close();
				conn.close();
			} catch (SQLException e) {
				// e.printStackTrace();
			}
		}
		return retJson;
	}

	@SuppressWarnings("unchecked")
	public JSONArray retrieveOwnerOrders(int siteId) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		ResultSet ret1 = null;
		ResultSet ret2 = null;
		ResultSet ret3 = null;
		JSONArray siteOrders = new JSONArray();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		try {
			pstmt1 = conn.prepareStatement(QUERY_DELIVER_TIME);
			pstmt2 = conn.prepareStatement(QUERY_ORDER_BY_SITE);
			pstmt3 = conn.prepareStatement(QUERY_DISH_DETAILS);
			pstmt1.setLong(1, this.ownerId);
			pstmt1.setInt(2, siteId);
			ret1 = pstmt1.executeQuery();
			while (ret1.next()) {

				JSONObject siteOrder = new JSONObject();
//				siteOrder.put("time", dateFormat.format(new Date(ret1
//						.getTimestamp(1).getTime())));
				siteOrder.put("time", ret1.getString(2));
				
				JSONArray retJson = new JSONArray();
				pstmt2.setLong(1, this.ownerId);
				pstmt2.setInt(2, siteId);
				pstmt2.setTimestamp(3, ret1.getTimestamp(1));
				ret2 = pstmt2.executeQuery();

				while (ret2.next()) {
					JSONObject object = new JSONObject();
					object.put("order_id", ret2.getLong(1));
					object.put("location", ret2.getString(2));
					object.put("contact_name", ret2.getString(3));
					object.put("contact_phone", ret2.getString(4));
					object.put("deliver_time", dateFormat.format(new Date(ret2
							.getTimestamp(5).getTime())));
					object.put("status", ret2.getString(6));
					object.put("payment", ret2.getDouble(7));

					// retrieve order details
					pstmt3.setLong(1, ret2.getLong(1));
					ret3 = pstmt3.executeQuery();
					JSONArray details = new JSONArray();
					while (ret3.next()) {
						JSONObject obDish = new JSONObject();
						obDish.put("dish_english", ret3.getString(1));
						obDish.put("staple_food_name", ret3.getString(2));
						obDish.put("quantity", ret3.getInt(3));
						details.add(obDish);
					}
					object.put("detail", details);

					retJson.add(object);
				}
				siteOrder.put("data", retJson);
				siteOrders.add(siteOrder);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt1 != null)
					pstmt1.close();
				if (pstmt2 != null)
					pstmt2.close();
				if (pstmt3 != null)
					pstmt3.close();
				if (ret1 != null)
					ret1.close();
				if (ret2 != null)
					ret2.close();
				if (ret3 != null)
					ret3.close();
				conn.close();
			} catch (SQLException e) {
				// e.printStackTrace();
			}
		}
		return siteOrders;
	}

	@SuppressWarnings("unchecked")
	public JSONArray queryDailyRevenue(int siteId) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		ResultSet ret1 = null;
		ResultSet ret2 = null;
		JSONArray siteRevenues = new JSONArray();

//		SimpleDateFormat dateFormat = new SimpleDateFormat(
//				"yyyy-MM-dd HH:mm:ss");

		try {
			pstmt1 = conn.prepareStatement(QUERY_DELIVER_TIME);
			pstmt2 = conn.prepareStatement(QUERY_DAILY_REVENUE);
			pstmt1.setLong(1, this.ownerId);
			pstmt1.setInt(2, siteId);
			ret1 = pstmt1.executeQuery();
			while (ret1.next()) {

				JSONObject siteRevenue = new JSONObject();
//				siteRevenue.put("time", dateFormat.format(new Date(ret1
//						.getTimestamp(1).getTime())));
				siteRevenue.put("time", ret1.getString(2));
				JSONArray json = new JSONArray();
				
				pstmt2.setLong(1, this.ownerId);
				pstmt2.setInt(2, siteId);
				pstmt2.setTimestamp(3, ret1.getTimestamp(1));
				ret2 = pstmt2.executeQuery();
				int quantity = 0;
				double revenue = 0.0;
				while (ret2.next()) {
					if (ret2.getInt(1) == 0) {
						JSONObject jObj = new JSONObject();
						jObj.put("name", "Prepaid");
						jObj.put("quantity", ret2.getInt(2));
						jObj.put("revenue", ret2.getDouble(3));
						json.add(jObj);
					} else {
						quantity += ret2.getInt(2);
						revenue += ret2.getDouble(3);
					}
				}
				if (quantity != 0) {
					JSONObject jObj = new JSONObject();
					jObj.put("name", "Unpaid");
					jObj.put("quantity", quantity);
					jObj.put("revenue", revenue);
					json.add(jObj);
				}
				siteRevenue.put("data", json);
				siteRevenues.add(siteRevenue);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt1 != null)
					pstmt1.close();
				if (pstmt2 != null)
					pstmt2.close();
				if (ret1 != null)
					ret1.close();
				if (ret2 != null)
					ret2.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return siteRevenues;
	}

	@SuppressWarnings("unchecked")
	public JSONArray queryOrderStatistics(int siteId) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		ResultSet ret1 = null;
		ResultSet ret2 = null;
		JSONArray siteStats = new JSONArray();

//		SimpleDateFormat dateFormat = new SimpleDateFormat(
//				"yyyy-MM-dd HH:mm:ss");

		try {
			pstmt1 = conn.prepareStatement(QUERY_DELIVER_TIME);
			pstmt2 = conn.prepareStatement(QUERY_DISH_STATISTICS);
			
			pstmt1.setLong(1, this.ownerId);
			pstmt1.setInt(2, siteId);
			ret1 = pstmt1.executeQuery();
			while (ret1.next()) {
				
				JSONObject siteStat = new JSONObject();
//				siteStat.put("time", dateFormat.format(new Date(ret1
//						.getTimestamp(1).getTime())));
				siteStat.put("time", ret1.getString(2));
				JSONArray retJson = new JSONArray();

				pstmt2.setLong(1, ownerId);
				pstmt2.setInt(2, siteId);
				pstmt2.setTimestamp(3, ret1.getTimestamp(1));
				ret2 = pstmt2.executeQuery();
				while (ret2.next()) {
					JSONObject json = new JSONObject();
					json.put("dishname", ret2.getString(1));
					json.put("staplename", ret2.getString(2));
					json.put("quantity", ret2.getInt(3));
					retJson.add(json);
				}
				siteStat.put("data", retJson);
				siteStats.add(siteStat);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt1 != null)
					pstmt1.close();
				if(pstmt2 != null)
					pstmt2.close();
				if (ret1 != null)
					ret1.close();
				if (ret2 != null)
					ret2.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return siteStats;
	}

	public boolean changeOrderStatus(int orderId, String status) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(CHANGE_ORDER_STATUS);
			pstmt.setString(1, status);
			pstmt.setLong(2, orderId);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean changeSetofOrderStatus(String[] orderIds, String status){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(CHANGE_ORDER_STATUS);
			pstmt.setString(1, status);
			for(int i = 0; i < orderIds.length; i++){
				pstmt.setInt(2, Integer.valueOf(orderIds[i].trim()).intValue());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
