package com.uslunchbox.restaurant.owner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;
import com.uslunchbox.restaurant.dish.Dish;

public class OwnerDishManagement {

	private long ownerId;

	private static final String QUERY_ALL_DISH = "select d.dish_id, d.dish_english, d.dish_chinese_simple, d.dish_desc, d.dish_lunch_price, d.dish_dinner_price from dish d, restaurant_owners o "
			+ "where o.restaurant_id = d.restaurant_id and o.owner_id = ?";
	
	private static final String MANAGE_DISH_EN_NAME = "update dish set dish_english = ? where dish_id = ?";
	
	private static final String MANAGE_DISH_CN_NAME = "update dish set dish_chinese_simple = ? where dish_id = ?";
	
	private static final String MANAGE_DISH_DESC = "update dish set dish_desc = ? where dish_id = ?";
	
	private static final String MANAGE_DISH_LUNCH_PRICE = "update dish set dish_lunch_price = ? where dish_id = ?";

	private static final String MANAGE_DISH_DINNER_PRICE = "update dish set dish_dinner_price = ? where dish_id = ?";
	
	private static final String QUERY_MAX_DISH_ID = "select max(dish_id) from dish";
	private static final String QUERY_RESTAURANT_ID = "select restaurant_id from restaurant_owners where owner_id = ?";
	private static final String ADD_NEW_DISH = "insert into dish(dish_id,restaurant_id,dish_english,dish_chinese_simple,dish_desc,dish_category_id,dish_lunch_price,dish_dinner_price,num_onsale) values (?,?,?,?,?,?,?,?,?)";

	public OwnerDishManagement(long ownerId) {
		this.setOwnerId(ownerId);
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * get the information of all dishes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONArray queryAllDishes() {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet ret = null;
		JSONArray retJson = new JSONArray();
		try {
			pstmt = conn.prepareStatement(QUERY_ALL_DISH);
			pstmt.setLong(1, this.ownerId);
			ret = pstmt.executeQuery();
			while (ret.next()) {
				JSONObject json = new JSONObject();
				json.put("dishid", ret.getInt(1));
				json.put("dishname", ret.getString(2));
				json.put("dishcnname", ret.getString(3));
				json.put("dishdesc", ret.getString(4));
				json.put("lunchprice", ret.getDouble(5));
				json.put("dinnerprice", ret.getDouble(6));
				retJson.add(json);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				if (ret != null)
					ret.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return retJson;
	}
	
	/**
	 * update the names of dishes
	 * @param dishId
	 * @param nameType
	 * @param dishName
	 * @return
	 */
	public boolean manageDishName(long dishId, String nameType, String dishName){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		boolean success = false;
		try{
			if(nameType.equals("EN"))
				pstmt = conn.prepareStatement(MANAGE_DISH_EN_NAME);
			else
				pstmt = conn.prepareStatement(MANAGE_DISH_CN_NAME);
			if(dishName.equals(""))
				pstmt.setNull(1, java.sql.Types.INTEGER);
			else
				pstmt.setString(1, dishName);
			pstmt.setLong(2, dishId);
			if(pstmt.executeUpdate() != 0)
				success = true;
		}catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return success;
	}
	
	/**
	 * update the description of dishes
	 * @param dishId
	 * @param dishdesc
	 * @return
	 */
	public boolean manageDishesDesc(long dishId, String dishdesc){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		boolean success = false;
		try{
			pstmt = conn.prepareStatement(MANAGE_DISH_DESC);
			pstmt.setString(1, dishdesc);
			pstmt.setLong(2, dishId);
			if(pstmt.executeUpdate() != 0)
				success = true;
		}catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return success;
	}
	
	/**
	 * update the prices of dishes
	 * @param dishId
	 * @param priceType
	 * @param price
	 * @return
	 */
	public boolean manageDishPrice(long dishId, String priceType, String price){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		boolean success = false;
		try{
			if(priceType.equals("lunch")){
				pstmt = conn.prepareStatement(MANAGE_DISH_LUNCH_PRICE);
			}else{
				pstmt = conn.prepareStatement(MANAGE_DISH_DINNER_PRICE);
			}
			pstmt.setDouble(1, Double.valueOf(price));
			pstmt.setLong(2, dishId);
			
			if(pstmt.executeUpdate() != 0)
				success = true;
		}catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return success;
	}
	
	/**
	 * add a new dish
	 * @param dish
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject addNewDish(Dish dish){
		JSONObject json = new JSONObject();
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		ResultSet ret = null;
		ResultSet ret2 = null;
		try {
			pstmt = conn.prepareStatement(QUERY_MAX_DISH_ID);
			ret = pstmt.executeQuery();
			if(ret.next()){
				dish.id = ret.getInt(1) + 1;
			}
			pstmt = conn.prepareStatement(QUERY_RESTAURANT_ID);
			pstmt.setLong(1, this.ownerId);
			ret2 = pstmt.executeQuery();
			pstmt2 = conn.prepareStatement(ADD_NEW_DISH);
			pstmt2.setInt(1, dish.id);
			if(ret2.next()){
				pstmt2.setInt(2, ret2.getInt(1));
			}
			pstmt2.setString(3, dish.english_name);
			pstmt2.setString(4, dish.chinese_name);
			pstmt2.setString(5, dish.desc);
			pstmt2.setInt(6, 10);
			pstmt2.setDouble(7, dish.lunch_price);
			pstmt2.setDouble(8, dish.dinner_price);
//			if(dish.num_onsale == Integer.MAX_VALUE)
			pstmt2.setNull(9, java.sql.Types.NULL);
//			else
//			pstmt2.setInt(9, 0);
			int result = pstmt2.executeUpdate();
			json.put("result", result==1? true:false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				pstmt.close();
				ret.close();
				pstmt2.close();
				ret2.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return json;
	}

}
