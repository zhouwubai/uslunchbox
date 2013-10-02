package com.uslunchbox.restaurant.dish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;

/**
 * 
 * @author Liang Tang
 * @date Mar 2, 2013 3:47:13 PM
 * 
 */
public class Dish {

	public int id;
	public int restaurant_id;
	public String restaurant_name;
	public String english_name;
	public String chinese_name;
	public int category_id;
	public double dinner_price;
	public double lunch_price;
	public int num_onsale;
	public String desc;

	static Map<Integer, Dish> allOnDishes = null;

	private static String QUERY_DISH_REVIEW_BYRID = "SELECT d.dish_chinese_simple, d.dish_id,d.restaurant_id,r.restaurant_name, "
			+ "d.dish_english, d.rate, d.count, d.dish_lunch_price "
			+ "FROM (SELECT di.dish_chinese_simple, "
			+ "di.dish_lunch_price, di.dish_id, "
			+ "di.restaurant_id, di.dish_english, "
			+ "COALESCE(FORMAT(10*ROUND(AVG(r.rating) * 2) / 2,0),'0') rate, "
			+ "COUNT(r.rating) AS count FROM dish di LEFT JOIN review_dish r ON di.dish_id=r.dish_id "
			+ "GROUP BY di.dish_id ) d,  "
			+ "restaurants r, site_restaurants s "
			+ "WHERE s.site_id=? AND d.restaurant_id=r.restaurant_id "
			+ "AND r.restaurant_id=s.restaurant_id AND r.restaurant_id=? ORDER BY rate desc;";

	private static String QUERY_VALID_DISHID = "SELECT d.dish_id FROM dish d WHERE dish_id=?";

	public Dish() {

	}

	public static List<Dish> getActivatedDishes(int category_id, int site_id) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		List<Dish> dishes = new ArrayList<Dish>();
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("SELECT d.dish_id,d.restaurant_id,r.restaurant_name, "
							+ "d.dish_english,d.dish_chinese_simple,"
							+ "d.dish_chinese_trandition,d.dish_spanish,d.dish_category_id,d.dish_dinner_price,"
							+ "d.dish_lunch_price,d.dish_desc,d.num_onsale "
							+ "FROM dish d, restaurants r, site_restaurants s "
							+ "WHERE s.site_id="
							+ site_id
							+ " AND d.restaurant_id=r.restaurant_id  AND r.restaurant_id=s.restaurant_id AND d.dish_category_id=? ");
			pstmt.setInt(1, category_id);
			ResultSet rets = pstmt.executeQuery();
			while (rets.next()) {
				dishes.add(fillDishFromResultSet(rets));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dishes;
	}

	public static List<Dish> getActivatedDishes(int site_id) {
		if (allOnDishes != null) {
			List<Dish> dishList = new ArrayList<Dish>();
			dishList.addAll(allOnDishes.values());
			return dishList;
		} else {
			Connection conn = ConnectionManager.getInstance().getConnection();
			List<Dish> dishes = new ArrayList<Dish>();
			try {
				PreparedStatement pstmt = conn
						.prepareStatement("SELECT d.dish_id,d.restaurant_id,r.restaurant_name, "
								+ "d.dish_english,d.dish_chinese_simple,"
								+ "d.dish_chinese_trandition,d.dish_spanish,d.dish_category_id,d.dish_dinner_price,"
								+ "d.dish_lunch_price,d.dish_desc, d.num_onsale FROM dish d, restaurants r, site_restaurants s "
								+ "WHERE s.site_id="
								+ site_id
								+ " AND d.restaurant_id=r.restaurant_id AND r.restaurant_id=s.restaurant_id ");
				ResultSet rets = pstmt.executeQuery();
				while (rets.next()) {
					dishes.add(fillDishFromResultSet(rets));
				}
				rets.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			return dishes;
		}
	}

	public static List<Dish> getScheduledDishes(int site_id, String date) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		List<Dish> dishes = new ArrayList<Dish>();
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("SELECT d.dish_id,d.restaurant_id,r.restaurant_name, "
							+ "d.dish_english,d.dish_chinese_simple,"
							+ "d.dish_chinese_trandition,d.dish_spanish,d.dish_category_id,d.dish_dinner_price,"
							+ "d.dish_lunch_price,d.dish_desc, d.num_onsale " 
							+ "FROM dish d, restaurants r, site_restaurants s, dish_schedule ds "
							+ "WHERE s.site_id="
							+ site_id
							+ " AND ds.site_id = s.site_id"
							+ " AND ds.dish_id = d.dish_id AND ds.dish_schedule_time = ?" 
							+ " AND d.restaurant_id=r.restaurant_id AND r.restaurant_id=s.restaurant_id");
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date parsedDate = dateFormat.parse(date);
			java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());			
			
			pstmt.setTimestamp(1, timestamp);
			ResultSet rets = pstmt.executeQuery();
			while (rets.next()) {
				dishes.add(fillDishFromResultSet(rets));
			}
			rets.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return dishes;
	}
	
	public static List<Dish> getScheduledDishes(int category_id, int site_id, String date) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		List<Dish> dishes = new ArrayList<Dish>();
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("SELECT d.dish_id,d.restaurant_id,r.restaurant_name, "
							+ "d.dish_english,d.dish_chinese_simple,"
							+ "d.dish_chinese_trandition,d.dish_spanish,d.dish_category_id,d.dish_dinner_price,"
							+ "d.dish_lunch_price,d.dish_desc, d.num_onsale " 
							+ "FROM dish d, restaurants r, site_restaurants s, dish_schedule ds "
							+ "WHERE s.site_id="
							+ site_id
							+ " AND ds.site_id = s.site_id"
							+ " AND ds.dish_id = d.dish_id AND ds.dish_schedule_time = ? AND d.dish_category_id=" 
							+ category_id
							+ " AND d.restaurant_id=r.restaurant_id AND r.restaurant_id=s.restaurant_id");
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date parsedDate = dateFormat.parse(date);
			java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());			
			
			pstmt.setTimestamp(1, timestamp);
			ResultSet rets = pstmt.executeQuery();
			while (rets.next()) {
				dishes.add(fillDishFromResultSet(rets));
			}
			rets.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return dishes;
	}

	public static Map<Integer, Integer> getRecentDishSaleCounts(
			int numRecentDays) {
		// SELECT d.dish_id, SUM(od.quantity) FROM dish d, order_dishes od,
		// orders o WHERE d.dish_id= od.dish_id AND od.order_id = o.order_id AND
		// o.deliver_time >= '2012-04-18 23:00:00' GROUP BY d.dish_id;
		Connection conn = ConnectionManager.getInstance().getConnection();
		Map<Integer, Integer> dishCounts = new HashMap<Integer, Integer>();
		Calendar cal = Calendar.getInstance();
		// DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		cal.add(Calendar.DAY_OF_YEAR, -numRecentDays);
		// String recent_time = dateFormat.format(cal.getTime());
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("SELECT d.dish_id, SUM(od.quantity) "
							+ "FROM dish d, order_dishes od, orders o "
							+ "WHERE d.dish_id= od.dish_id AND od.order_id = o.order_id AND o.deliver_time >= ? "
							+ "GROUP BY d.dish_id");
			pstmt.setTimestamp(1, new java.sql.Timestamp(cal.getTime()
					.getTime()));
			ResultSet rets = pstmt.executeQuery();
			while (rets.next()) {
				dishCounts.put(rets.getInt(1), rets.getInt(2));
			}
			rets.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dishCounts;
	}

	public static Dish findActivatedDish(int dish_id, int site_id) {
		if (allOnDishes != null) {
			return allOnDishes.get(dish_id);
		} else {
			Dish dish = null;
			Connection conn = ConnectionManager.getInstance().getConnection();
			try {
				PreparedStatement pstmt = conn
						.prepareStatement("SELECT d.dish_id,d.restaurant_id,r.restaurant_name, "
								+ "d.dish_english,d.dish_chinese_simple,"
								+ "d.dish_chinese_trandition,d.dish_spanish,d.dish_category_id,d.dish_dinner_price,"
								+ "d.dish_lunch_price,d.dish_desc, d.num_onsale FROM dish d, restaurants r, site_restaurants s "
								+ "WHERE s.site_id= ? "
								+ "AND d.restaurant_id=r.restaurant_id "
								+ "AND r.restaurant_id=s.restaurant_id "
								+ "AND d.dish_id=? ");
				pstmt.setInt(1, site_id);
				pstmt.setInt(2, dish_id);
				ResultSet rets = pstmt.executeQuery();
				if (rets.next()) {
					dish = fillDishFromResultSet(rets);
				}
				rets.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return dish;
		}
	}

	private static Dish fillDishFromResultSet(ResultSet rets)
			throws SQLException {
		Dish dish = new Dish();
		dish.id = rets.getInt("dish_id");
		dish.category_id = rets.getInt("dish_category_id");
		dish.restaurant_id = rets.getInt("restaurant_id");
		dish.restaurant_name = rets.getString("restaurant_name");
		dish.english_name = rets.getString("dish_english");
		dish.chinese_name = rets.getString("dish_chinese_simple");
		dish.dinner_price = rets.getDouble("dish_dinner_price");
		dish.lunch_price = rets.getDouble("dish_lunch_price");
		dish.desc = rets.getString("dish_desc");
		dish.num_onsale = rets.getInt("num_onsale");
		if (rets.wasNull()) {
			dish.num_onsale = -1;
		}
		return dish;
	}

	public static JSONArray getRestaurantDisheWithReviews(int site_id, int r_id) {
		JSONArray jarray = new JSONArray();
		// String
		// query="SELECT d.dish_chinese_simple, d.dish_id,d.restaurant_id,r.restaurant_name, d.dish_english, d.rate, d.count, d.dish_lunch_price FROM (SELECT di.dish_chinese_simple, di.dish_lunch_price, di.dish_id, di.restaurant_id, di.dish_english, COALESCE(FORMAT(10*ROUND(AVG(r.rating) * 2) / 2,0),'0') rate, COUNT(r.rating) AS count FROM dish di LEFT JOIN review_dish r ON di.dish_id=r.dish_id GROUP BY di.dish_id ) d,  restaurants r, site_restaurants s WHERE s.site_id=1 AND d.restaurant_id=r.restaurant_id AND r.restaurant_id=s.restaurant_id ORDER BY rate desc";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(QUERY_DISH_REVIEW_BYRID);
			ps.setInt(1, site_id);
			ps.setInt(2, r_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject jobject = new JSONObject();
				jobject.put(
						"name",
						rs.getString("dish_english") + " "
								+ rs.getString("dish_chinese_simple"));
				// jobject.put("desc",rs.getString("dish_desc"));
				jobject.put("price", rs.getDouble("dish_lunch_price"));
				jobject.put("count", rs.getInt("count"));
				jobject.put("id", rs.getInt("dish_id"));
				jobject.put("restaurantid", rs.getInt("restaurant_id"));
				jobject.put("restaurantname", rs.getString("restaurant_name"));
				jobject.put("rating", rs.getString("rate"));
				jarray.put(jobject);
			}
			ps.close();
			rs.close();
		}

		catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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

	public static JSONArray getDishOrderByRating(boolean isHighestFirst)
			throws SQLException, JSONException {
		JSONArray jarray = new JSONArray();
		String order = ",count";
		if (isHighestFirst)
			order = " DESC,count";
		String query = "SELECT d.dish_id,COALESCE(FORMAT(10*ROUND(AVG(r.rating) * 2) / 2,0),'0') rate, COUNT(r.rating) AS count FROM dish d LEFT JOIN review_dish r ON d.dish_id=r.dish_id GROUP BY d.dish_id ORDER BY rate"
				+ order;
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject jobject = new JSONObject();
				jobject.put("dishid", rs.getInt("dish_id"));
				jobject.put("dishrating", rs.getInt("rate"));
				jobject.put("ratingcount", rs.getInt("count"));
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

	public static JSONArray findDishByDishid(int dish_id, int site_id) {

		JSONArray jarray = new JSONArray();
		String query = "SELECT d.dish_id,d.restaurant_id,r.restaurant_name, "
				+ "d.dish_english,d.dish_chinese_simple,"
				+ "d.dish_chinese_trandition,d.dish_spanish,d.dish_category_id,d.dish_dinner_price,"
				+ "d.dish_lunch_price,d.dish_desc, d.num_onsale FROM dish d, restaurants r, site_restaurants s "
				+ "WHERE s.site_id=" + site_id + " "
				+ "AND d.restaurant_id=r.restaurant_id "
				+ "AND r.restaurant_id=s.restaurant_id " + "AND d.dish_id=? ";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, dish_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject jobject = new JSONObject();
				jobject.put("en_name", rs.getString("dish_english"));
				jobject.put("cn_name", rs.getString("dish_chinese_simple"));
				jobject.put("desc", rs.getString("dish_desc"));
				jobject.put("dn_price", rs.getDouble("dish_dinner_price"));
				jobject.put("lu_price", rs.getDouble("dish_lunch_price"));
				jobject.put("id", rs.getInt("dish_id"));
				jobject.put("restaurantid", rs.getInt("restaurant_id"));
				jobject.put("restaurantname", rs.getString("restaurant_name"));
				jarray.put(jobject);
			}
			ps.close();
			rs.close();
		}

		catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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

	public static Boolean checkDishByDishid(int dish_id) {
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Boolean result = false;
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(QUERY_VALID_DISHID);
			ps.setInt(1, dish_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = true;
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
		return result;
	}
	
	public static JSONArray getSchedules(int site_id){
		JSONArray jarray = new JSONArray();
		String query = "SELECT distinct(s.dish_schedule_time), date_format(s.dish_schedule_time, '%a')," +
				" date_format(s.dish_schedule_time, '%W'), date_format(s.dish_schedule_time, '%b %e')" +
				" from dish_schedule s, dish d, restaurants r, site_restaurants sr" +
				" where to_days(s.dish_schedule_time)-to_days(now())>=0" +
				//" and to_days(s.dish_schedule_time)-to_days(now())<7" +
				" and s.dish_id = d.dish_id and d.restaurant_id = r.restaurant_id" +
				" and r.restaurant_id = sr.restaurant_id and s.site_id = sr.site_id and sr.site_id = ?" +
				" order by s.dish_schedule_time asc limit 5";
		java.sql.Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, site_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject json = new JSONObject();
				json.put("date", dateFormat.format(rs.getTimestamp(1)));
				json.put("abweek", rs.getString(2));
				json.put("week", rs.getString(3));
				json.put("monthday", rs.getString(4));
				jarray.put(json);
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				if(rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jarray;
	}

	@Override
	public String toString() {
		return "Dish [id=" + id + ", restaurant_id=" + restaurant_id
				+ ", restaurant_name=" + restaurant_name + ", english_name="
				+ english_name + ", chinese_name=" + chinese_name
				+ ", category_id=" + category_id + ", dinner_price="
				+ dinner_price + ", lunch_price=" + lunch_price
				+ ", num_onsale=" + num_onsale + ", desc=" + desc + "]";
	}

}
