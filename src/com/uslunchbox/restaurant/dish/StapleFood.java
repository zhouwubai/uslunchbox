package com.uslunchbox.restaurant.dish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.uslunchbox.restaurant.database.ConnectionManager;

public class StapleFood {
	
	public int id;
	
	public String name;
	
	public StapleFood(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static Map<Integer, StapleFood> getAllStapleFoods() {
		Connection conn = ConnectionManager.getInstance().getConnection();
		Map<Integer, StapleFood> foodMap = new HashMap<Integer, StapleFood>();
		try {
			PreparedStatement pstmt = conn.prepareStatement("SELECT staple_food_id, staple_food_name FROM staple_food " +
					"WHERE status='on'");
			ResultSet retSet = pstmt.executeQuery();			
			while(retSet.next()) {
				StapleFood food = new StapleFood(retSet.getInt("staple_food_id"), retSet.getString("staple_food_name"));
				foodMap.put(food.id, food);
			}
			retSet.close();
			pstmt.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();			
			}catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		return foodMap;
	}
	
	public static StapleFood findStapleFood(int staple_food_id) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		StapleFood food = null;
		try {
			PreparedStatement pstmt = conn.prepareStatement("SELECT staple_food_id, staple_food_name FROM staple_food " +
					"WHERE staple_food_id=? AND status='on'");
			pstmt.setInt(1, staple_food_id);
			ResultSet retSet = pstmt.executeQuery();			
			if(retSet.next()) {
				food = new StapleFood(retSet.getInt("staple_food_id"), retSet.getString("staple_food_name"));
			}
			retSet.close();
			pstmt.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();			
			}catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		return food;
	}
	}
