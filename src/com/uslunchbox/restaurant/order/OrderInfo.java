package com.uslunchbox.restaurant.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.uslunchbox.restaurant.database.ConnectionManager;

public class OrderInfo {

	private int orderId;
	private String location;
	private Timestamp deliverTime;
	private double totalPrice;
	private double discountRate;
	private int restaurantId;
	private String restaurantName;
	private int cardNo;
	private String status;
	private Timestamp callinTime;
	private String contact;
	private String comment;
	private String method;

	private Map<String, Integer> dishes;
	
	private static final String UPDATE_ORDER_STATUS = "update orders set status = ? where order_id = ?";
	
	private static final String QUERY_DELIVER_TIME_BY_ORDERID = "select deliver_time from orders where order_id = ?";

	public OrderInfo() {

	}

	public OrderInfo(int orderId, String location, Timestamp deliverTime,
			double totalPrice, double discountRate, int restaurantId,
			int cardNo, String status, Timestamp callinTime, String contact,
			String comment, String method,Map<String, Integer> dishes) {
		this.orderId=orderId;
		this.location=location;
		this.deliverTime=deliverTime;
		this.totalPrice=totalPrice;
		this.discountRate=discountRate;
		this.restaurantId=restaurantId;
		this.cardNo=cardNo;
		this.status=status;
		this.callinTime=callinTime;
		this.contact=contact;
		this.comment=comment;
		this.method=method;
		this.dishes=dishes;
	}
	
	public static boolean updateOrderStatus(long[] orderIds, String status){
		Connection conn = ConnectionManager.getInstance().getConnection();
		boolean updated = true;

		PreparedStatement pstmt = null;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(UPDATE_ORDER_STATUS);
			for (long orderId: orderIds) {
				pstmt.setString(1, status);
				pstmt.setLong(2, orderId);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			conn.commit();
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
		return updated;
	}
	
	public static boolean validateOrders(long[] orderIds) throws ParseException{
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet ret = null;
		boolean isValid = true;
		try{
			for (long orderId: orderIds) {
				pstmt = conn.prepareStatement(QUERY_DELIVER_TIME_BY_ORDERID);
				pstmt.setLong(1, orderId);
				ret = pstmt.executeQuery();
				if (ret.next() == false) { // Do not have this order
					isValid = false;
					break;
				}
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
		return isValid;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Timestamp getDeliverTime() {
		return deliverTime;
	}

	public void setDeliverTime(Timestamp deliverTime) {
		this.deliverTime = deliverTime;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public double getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(double discountRate) {
		this.discountRate = discountRate;
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public int getCardNo() {
		return cardNo;
	}

	public void setCardNo(int cardNo) {
		this.cardNo = cardNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCallinTime() {
		return callinTime;
	}

	public void setCallinTime(Timestamp callinTime) {
		this.callinTime = callinTime;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Map<String, Integer> getDishes() {
		return dishes;
	}

	public void setDishes(Map<String, Integer> dishes) {
		this.dishes = dishes;
	}

}
