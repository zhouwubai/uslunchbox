package com.uslunchbox.restaurant.dish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uslunchbox.restaurant.database.ConnectionManager;

public class SideDish {
	
	public int id;
	public int restaurant_id;
	public String english_name;
	
    static Map<Integer,SideDish> allOnSideDishes = null;
	
	public SideDish() {
		
	}
	
	public static List<SideDish> getAllOnSideDishes()  {
		if (allOnSideDishes != null) {
			List<SideDish> sideDishList = new ArrayList<SideDish>(allOnSideDishes.keySet().size());
			sideDishList.addAll(allOnSideDishes.values());
			return sideDishList;
		}
		else {
			Connection conn = ConnectionManager.getInstance().getConnection();
			List<SideDish> sideDishes = new ArrayList<SideDish>();
			try {
				PreparedStatement pstmt = conn.prepareStatement("SELECT d.side_dish_id,d.restaurant_id, " +
						" d.side_dish_english" +
						" FROM side_dish d" +
						" WHERE d.side_dish_status='on'");
				ResultSet rets = pstmt.executeQuery();
				while(rets.next()) {
					SideDish sideDish = new SideDish();
					sideDish.id = rets.getInt("side_dish_id");
					sideDish.restaurant_id = rets.getInt("restaurant_id");
					sideDish.english_name = rets.getString("side_dish_english");
					sideDishes.add(sideDish);
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
			
//			if (allOnSideDishes == null) {
//				allOnSideDishes = new HashMap<Integer, SideDish>();
//				for (SideDish sideDish : sideDishes) {
//					allOnSideDishes.put(sideDish.id, sideDish);
//				}
//			}
			return sideDishes;
		}
	}
	
	public static SideDish findSideDish(int side_dish_id) {
		SideDish sideDish = null;
		if (allOnSideDishes != null) {
			return allOnSideDishes.get(side_dish_id);
		}
		else {
			Connection conn = ConnectionManager.getInstance().getConnection();
			try {
				PreparedStatement pstmt = conn.prepareStatement("SELECT d.side_dish_id,d.restaurant_id," +
						" d.side_dish_english"+
						" FROM side_dish d" +
						" WHERE d.side_dish_status='on' " +
						" AND d.side_dish_id=?");
				pstmt.setInt(1, side_dish_id);
				ResultSet rets = pstmt.executeQuery();
				if (rets.next()) {
					sideDish = new SideDish();
					sideDish.id = rets.getInt("side_dish_id");
					sideDish.restaurant_id = rets.getInt("restaurant_id");
					sideDish.english_name = rets.getString("side_dish_english");
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
			return sideDish;
		}
	}

	@Override
	public String toString() {
		return "SideDish [id=" + id + ", restaurant_id=" + restaurant_id
				+ ", english_name=" + english_name + "]";
	}
	
	

}
