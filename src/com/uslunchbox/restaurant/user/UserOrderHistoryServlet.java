package com.uslunchbox.restaurant.user;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;

/**
 * Servlet implementation class UserOrderHistoryServlet
 */
@WebServlet("/UserOrderHistoryServlet")
public class UserOrderHistoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserOrderHistoryServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		User user = User.getFromSession(request.getSession());
		if(user == null){
			JSONObject userInfo = new JSONObject();
			userInfo.put("result", "notsignin");
			response.getWriter().write(userInfo.toJSONString());
			return;
		}
		
		long user_id = user.getUserId(); //1;
		Connection conn = ConnectionManager.getInstance().getConnection();
		String getUserOrders = "select o.order_id, o.location, o.deliver_time, o.total_price, " +
											"o.email, o.status, o.place_time, o.method, o.redeem_quantity, r.* " +			
									" from users c, orders o, restaurants r " +
										" where c.user_id = ? AND c.email = o.email AND o.restaurant_id = r.restaurant_id order by o.place_time DESC";

		String getOrderDishes = "select d.dish_english, od.quantity " +
				 					"from orders o, order_dishes od, dish d " +
				 						"where o.order_id = ? AND od.order_id = o.order_id AND od.dish_id = d.dish_id";
		//System.out.println(",," + user_id);
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(getUserOrders);
			pstmt.setLong(1, user_id);
			ResultSet retOrder = pstmt.executeQuery();
			JSONObject allOrders = new JSONObject();
			JSONArray orders = new JSONArray();
			java.util.Date date= new java.util.Date();
			Timestamp now = new Timestamp(date.getTime());
			String valid = "invalid";
			
			while (retOrder.next()) {
				JSONObject orderInfo = new JSONObject();
				orderInfo.put("d_order_id", retOrder.getString(1));     //order id
				
				int orderid = retOrder.getInt(1);
				PreparedStatement pstmt1 = conn.prepareStatement(getOrderDishes);
				pstmt1.setInt(1, orderid);
				ResultSet retDish = pstmt1.executeQuery();
				String name = "";
				while(retDish.next()){
					name += "(" + retDish.getString(1) + ")x" + retDish.getInt(2) + "\n";
				}
				name = name.substring(0, name.length()-1);
				retDish.close();
				pstmt1.close();
				
				orderInfo.put("d_dishes", name);
				
				orderInfo.put("d_location", retOrder.getString(2));     //location
				orderInfo.put("d_time", retOrder.getString(3)); //deliver_time
				Timestamp order_deliver_time = retOrder.getTimestamp(3);
				Timestamp deadline = null;
				/*if(order_deliver_time.getHours() == 12){ // noon
					deadline = new Timestamp(order_deliver_time.getYear(), order_deliver_time.getMonth(), 
												order_deliver_time.getDay(), 11, 0, 0, 0);
				}else if(order_deliver_time.getHours() == 18){ // evening
					deadline = new Timestamp(order_deliver_time.getYear(), order_deliver_time.getMonth(), 
							order_deliver_time.getDay(), 17, 0, 0, 0);
				}*/
				int hr = order_deliver_time.getHours();
				deadline = new Timestamp(order_deliver_time.getYear(), order_deliver_time.getMonth(), 
						order_deliver_time.getDay(), hr-1, 0, 0, 0);
				
				if(now.before(deadline)) // users are allowed to change this order's status
					valid = "valid";
				
				orderInfo.put("o_status", valid);
				
				double total_price = retOrder.getDouble(4);
				int redeem = retOrder.getInt(9);
				String display = "";
				double pay = total_price - 5 * redeem; // the actual money user needs to pay
				if(pay == 0)
					display = "0 (free)";
				else{
					if(redeem == 1)
						display = pay + " + " + redeem + "free";
					else
						display = pay+"";
				}
				
				orderInfo.put("d_price", display);  //total_price
				orderInfo.put("d_email", retOrder.getString(5));      //email
				orderInfo.put("d_status", retOrder.getString(6));    //status
				orderInfo.put("d_placetime", new SimpleDateFormat("yyyy.MM.dd h:mm a").format(retOrder.getTimestamp(7)));    //place_time
				orderInfo.put("d_method", retOrder.getString(8)); //method 
				//orderInfo.put("rs", retOrder.getString(10));   //restaurant_id 
				orderInfo.put("r_name", retOrder.getString(11)); //restaurant_name
				orderInfo.put("r_addr", retOrder.getString(12)); //restaurant_address
				orderInfo.put("r_city", retOrder.getString(13));    //restaurant_city
				orderInfo.put("r_state", retOrder.getString(14)); //restaurant_state 
				orderInfo.put("r_country", retOrder.getString(15));           //restaurant_country
				orderInfo.put("r_phone", retOrder.getString(16)); //restaurant_phone
				orderInfo.put("r_capacity", retOrder.getString(17)); //restaurant_capacity
				orderInfo.put("r_parking", retOrder.getString(18));           //restaurant_parking
				orderInfo.put("r_type", retOrder.getString(19)); //restaurant_type
				orderInfo.put("r_hour", retOrder.getString(20)); //restaurant_hour
				orderInfo.put("r_url", retOrder.getString(21));           //restaurant_url
				orderInfo.put("r_chinese", retOrder.getString(22)); //restaurant_name_chinese
				orderInfo.put("r_zip", retOrder.getString(23)); //restaurant_zip
				
				//System.out.println(retOrder.getString(9));
				orders.add(orderInfo);

			} 
			allOrders.put("orders", orders);
			response.getWriter().write(allOrders.toJSONString());
			retOrder.close();
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

}
