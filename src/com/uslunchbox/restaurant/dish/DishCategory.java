package com.uslunchbox.restaurant.dish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.uslunchbox.restaurant.database.ConnectionManager;

public class DishCategory {
	
	public int id;
	
	public String english_name;
	
	public String description;
	
	public String chinese_name;
	
	public DishCategory(int id, String english_name, String desc, String chinese_name) {
		this.id = id;
		this.english_name = english_name;
		this.description = desc;
		this.chinese_name = chinese_name;
	}
	
	public static List<DishCategory> getAllFoodCategories() {
		Connection conn = ConnectionManager.getInstance().getConnection();
		List<DishCategory> cateList = new ArrayList<DishCategory>();
		try {
			PreparedStatement pstmt = conn.prepareStatement("SELECT dish_category_name, dish_category_id," +
					"dish_category_description, dish_category_name_chinese FROM dish_categories");
			ResultSet rets = pstmt.executeQuery();		
			while(rets.next()) {
				cateList.add(new DishCategory(rets.getInt("dish_category_id"),
						rets.getString("dish_category_name"),
						rets.getString("dish_category_description"),
						rets.getString("dish_category_name_chinese")));
			}
			rets.close();
			pstmt.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return cateList;
	}

}
