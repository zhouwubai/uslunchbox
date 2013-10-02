package com.uslunchbox.restaurant.order;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;
import com.uslunchbox.restaurant.dish.Dish;
import com.uslunchbox.restaurant.restaurant.Restaurant;
import com.uslunchbox.restaurant.site.Site;
import com.uslunchbox.restaurant.site.Site.DeliverInfo;
import com.uslunchbox.restaurant.user.User;
import com.uslunchbox.restaurant.utils.DataEncryption;
import com.uslunchbox.restaurant.utils.EmailSender;
import com.uslunchbox.restaurant.utils.HostURL;

/**
 * Servlet implementation class CheckOutServlet
 */
@WebServlet("/PlaceOrderServlet")
public class PlaceOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PlaceOrderServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		
		String action = request.getParameter("action");
		if (action.equalsIgnoreCase("getorders")) {
			response.getWriter().write(getOrders(request).toJSONString());
		}
		else {
			throw new Error("Unknown action : " + action);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		
		String action = request.getParameter("action");
		if (action.equalsIgnoreCase("place")) {
			response.getWriter().write(placeOrder(request).toJSONString());
		} else if (action.equalsIgnoreCase("prepare")) {
			response.getWriter().write(prepare(request).toJSONString());
		}  
		else {
			throw new Error("Unknown action : " + action);
		}
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject prepare(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Site site = Site.getSiteFromSession(session);
		JSONObject retJson = new JSONObject();		
		if (site == null) {
			retJson.put("result", "false");
			return retJson;
		}
		
		User user = User.getFromSession(session);
		// Get signed user information
		JSONObject userJson = new JSONObject();
		if (user != null) {
			userJson.put("result", "true");
			userJson.put("email", user.getEmail());
			userJson.put("contact_name", user.getFirstName()+" "+user.getLastName());
			String phoneNumber = user.getPhoneNumber();
			if (phoneNumber == null || phoneNumber.length() < 10) {
				phoneNumber = "";
			}
			userJson.put("phone", phoneNumber);
			userJson.put("prepaid", user.getPrepaid());
		}
		else {
			userJson.put("result", "false");
		}
		retJson.put("user", userJson);
		
		// Get feasible deliver time options
		retJson.put("deliver_loc_times", getFeasibleDeliverTimes(user,site));
		
		return retJson;
	}
	
	
	private static Calendar fillCurrentDate(Time time) {
		Calendar cal = Calendar.getInstance();
		Calendar timeCal = Calendar.getInstance();
		timeCal.setTime(time);
		timeCal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		return timeCal;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject getFeasibleDeliverTimes(User user, Site site) {
		// Get the current time
		Calendar now = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Collection<DeliverInfo> deliverInfos = site.getDeliverInfos();
		int defaultDeliverOption = site.getDefaultDeliverInfoIndex();
		// Special user account
		boolean isSpecialAccount = false;
		if (user != null && user.getEmail().equals("ltang002@cs.fiu.edu")) {
			isSpecialAccount = true;
		}
		
		JSONObject deliverTimesByLocation = new JSONObject();
		for (DeliverInfo deliverInfo : deliverInfos) {
			JSONObject locJSON = (JSONObject) deliverTimesByLocation.get(deliverInfo.location);
			JSONArray deliverTimesJSON = null;
			if (locJSON == null) {
				locJSON = new JSONObject();
				deliverTimesJSON = new JSONArray();
				locJSON.put("delivertimes", deliverTimesJSON);
				locJSON.put("map_image_url", deliverInfo.map_image_url);
				locJSON.put("map_click_url", deliverInfo.map_click_url);
				deliverTimesByLocation.put(deliverInfo.location, locJSON);				
			}
			else {
				deliverTimesJSON = (JSONArray) locJSON.get("delivertimes");
			}
			
			Calendar orderDeadline= fillCurrentDate(deliverInfo.order_deadline);
			Calendar deliveryDate = fillCurrentDate(deliverInfo.deliver_time);
			String deliver_time_str = null;
			
			// Today's
			if (now.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && now.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY ) {
				if (now.before(orderDeadline) || isSpecialAccount) {
					deliver_time_str = dateFormat.format(deliveryDate.getTime());
					deliverTimesJSON.add(deliver_time_str);
				}
			}
			// Tomorrow's 
			orderDeadline.add(Calendar.DAY_OF_YEAR, 1);
			deliveryDate.add(Calendar.DAY_OF_YEAR, 1);
			if (orderDeadline.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && orderDeadline.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY ) {
				if (now.before(orderDeadline) || isSpecialAccount) {
					deliver_time_str = dateFormat.format(deliveryDate.getTime());
					deliverTimesJSON.add(deliver_time_str);
				}
			}			
		}
		
		return deliverTimesByLocation;
	}
	
	private boolean isFeasibleDeliverTime(String deliver_time, User user, Site site) {
		JSONObject deliver_loc_times = getFeasibleDeliverTimes(user, site);
		for (Object loc: deliver_loc_times.keySet()) {
			JSONObject locJSON = (JSONObject) deliver_loc_times.get(loc);
			JSONArray options = (JSONArray)locJSON.get("delivertimes");
			for (Object option : options) {
				if (deliver_time.equals(option)) {
					return true;
				}
			}
		}
		return false;
	}
	

	@SuppressWarnings("unchecked")
	private JSONObject getOrders(HttpServletRequest request) {
		JSONObject orderJSON = new JSONObject();
		HttpSession session = request.getSession();		
		ShoppingCart cart = ShoppingCart.getFromSession(session);
		DecimalFormat df = new DecimalFormat(".##");
		Map<Integer, List<OrderDishInfo>> orders = cart.getOrdersByRestaurant();
		if (cart != null) {
			JSONArray orderDishesJSON = new JSONArray();
			double price_sum = 0;
			int quantity_sum = 0;
			for (Map.Entry<Integer, List<OrderDishInfo>> orderEntry : orders.entrySet()) {
				for (OrderDishInfo dishInfo : orderEntry.getValue()) {
					JSONObject dishJSON = new JSONObject();
					dishJSON.put("dish_id", dishInfo.getDishId());
					dishJSON.put("dish_name", dishInfo.getDishName());
					dishJSON.put("staple_food_name", dishInfo.getStapleFoodName());
					dishJSON.put("side_dish_name", dishInfo.getSideDishName());
					dishJSON.put("dish_price", df.format(dishInfo.getPrice() * dishInfo.getQuantity()));
					dishJSON.put("restaurant_name", dishInfo.getRestaurantName());
					dishJSON.put("quantity", dishInfo.getQuantity());
					orderDishesJSON.add(dishJSON);
					price_sum += dishInfo.getPrice() * dishInfo.getQuantity();
					quantity_sum += dishInfo.getQuantity();
				}				
			}
			orderJSON.put("dishes", orderDishesJSON);
			orderJSON.put("price_sum", df.format(price_sum));
			orderJSON.put("quantity_sum", quantity_sum);
		}
		return orderJSON;
	}

	@SuppressWarnings("unchecked")
	private JSONObject placeOrder(HttpServletRequest request) {
		JSONObject retJSON = new JSONObject();
		retJSON.put("result", "failed");
		HttpSession session = request.getSession();
		Site site = Site.getSiteFromSession(session);
		ShoppingCart cart = ShoppingCart.getFromSession(session);
		if (cart == null) {
			return retJSON;
		}
		String email = request.getParameter("email");
		String location = request.getParameter("location");
		String deliver_time = request.getParameter("deliver_time");
		String contact_name = request.getParameter("contact_name");
		String contact_phone = request.getParameter("contact_phone");
		double payment = 0.0;
		double total_price = 0;
		double prepaid_reduction = 0;

		// Validate this membership and password
		if (email == null || email.length() == 0) {
			return retJSON;
		}

		
		// Set default value for this information
		if (location == null) {
			location = "";
		}
		if (contact_phone == null) {
			contact_phone = "";
		}
		if (contact_name == null) {
			contact_name = "";
		}
		
		// Check the user credit score and redeem
		User user = User.getFromSession(request.getSession());

		// Validate delivery time
		if (deliver_time == null || deliver_time.length() < 1 || isFeasibleDeliverTime(deliver_time, user, site) == false) {
			retJSON.put("result", "wrong_deliver_time");
			return retJSON;
		}

		Map<Integer, List<OrderDishInfo>> orders = cart.getOrdersByRestaurant();
		
		
		// Check the status and the limit of ordered dishes
		Map<Integer, Dish> dishes = new HashMap<Integer, Dish>();
		for (int restaurant_id : orders.keySet()) {
			List<OrderDishInfo> dishList = orders.get(restaurant_id);
			if (dishList == null) {
				throw new Error("Do not have this restaurant_id : " + restaurant_id);
			}			
			boolean isOrderDishValidated = true;			
			for (OrderDishInfo dishInfo : dishList) {
				int dish_id = dishInfo.getDishId();
				int quantity = dishInfo.getQuantity();
				total_price += dishInfo.getPrice() * dishInfo.getQuantity();
				Dish dish = Dish.findActivatedDish(dish_id, site.getSiteId());
				if (dish == null) {
					retJSON.put("result", "notselling");
					retJSON.put("dishname", dish.english_name);
					isOrderDishValidated = false;
					break;
				}
				if (dish.num_onsale > 0 && dish.num_onsale < quantity) {
					retJSON.put("result", "excess");
					retJSON.put("dishname", dish.english_name);
					retJSON.put("num_onsale", dish.num_onsale+"");
					isOrderDishValidated = false;
					break;
				}
				dishes.put(dish_id, dish);
			}
			if (isOrderDishValidated == false) {
				return retJSON;
			}
		}
		
		// Set order status after this placement
		String status = "";
		if (user != null) {
			status = "placed";
		} else {
			status = "unplaced";
			
		}
		payment = total_price;
		// Update the prepaid balance for this user
		if (user != null && user.getPrepaid() > 0) {
			if (user.getPrepaid() >= total_price) {
				payment = 0;
				prepaid_reduction = total_price;
			}
			else {
				prepaid_reduction = user.getPrepaid();
				payment = total_price - prepaid_reduction;
			}
			double newPrepaid = user.getPrepaid() - prepaid_reduction;
			user.setPrepaid(newPrepaid);
			user.updatePrepaidInDB(newPrepaid);
		}

		
		Connection conn = ConnectionManager.getInstance().getConnection();
		try {
			conn.setAutoCommit(false);
			
			// Find the maximum order_id
			long max_order_id = findMaximumOrderId(conn);
			long start_order_id = max_order_id+1;
			long order_id = start_order_id;
			int numOrderIds = orders.keySet().size();
			JSONObject orderIdByRestaurant = new JSONObject();
			String requestIP = request.getRemoteAddr();
			// Place orders by restaurant
			for (int restaurant_id : orders.keySet()) {
				List<OrderDishInfo> dishList = orders.get(restaurant_id);
				
				// Calculate the total price
				double price_sum = 0;
				for (OrderDishInfo dishInfo : dishList) {
					price_sum += dishInfo.getPrice() * dishInfo.getQuantity();
				}
				DecimalFormat df = new DecimalFormat("#.##");
				price_sum = Double.parseDouble(df.format(price_sum));
				
				// Insert the order row
				PreparedStatement pstmt = conn
						.prepareStatement("INSERT INTO orders (order_id, location, deliver_time, total_price, restaurant_id,email,status,"
								+ "contact_phone, contact_name, method, place_time, payment, ip, site_id) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pstmt.setLong(1, order_id);
				pstmt.setString(2, location);
				pstmt.setString(3, deliver_time);
				pstmt.setDouble(4, price_sum);
				pstmt.setInt(5, restaurant_id);
				pstmt.setString(6, email);
				pstmt.setString(7, status);
				pstmt.setString(8, contact_phone);
				pstmt.setString(9, contact_name);
				pstmt.setString(10, "delivery");
				pstmt.setTimestamp(11, new Timestamp(new java.util.Date().getTime()));
				pstmt.setDouble(12, payment);
				pstmt.setString(13, requestIP);
				pstmt.setInt(14, site.getSiteId());

				int numInsertedRow = pstmt.executeUpdate();
				pstmt.close();
				if (numInsertedRow != 1) {
					throw new SQLException("Inserting the order table has an error!");
				}

				// Insert order_dishes rows
				for (OrderDishInfo dishInfo : dishList) {
					pstmt = conn.prepareStatement("INSERT INTO order_dishes (order_id, dish_id, staple_food_id, side_dish_id, quantity) "
									+ "VALUES (?,?,?,?,?)");
					pstmt.setLong(1, order_id);
					pstmt.setInt(2, dishInfo.getDishId());
					pstmt.setInt(3, dishInfo.getStapleFoodId());
					pstmt.setInt(4, dishInfo.getSideDishId());
					pstmt.setInt(5, dishInfo.getQuantity());
					numInsertedRow = pstmt.executeUpdate();
					pstmt.close();
					if (numInsertedRow != 1) {
						throw new SQLException("Inserting the order_dishes table has an error!");
					}

					Dish dish = dishes.get(dishInfo.getDishId());
					if (dish.num_onsale >= 0) {
						pstmt = conn.prepareStatement("UPDATE dish SET num_onsale=? WHERE dish_id=?");
						pstmt.setInt(1, dish.num_onsale - dishInfo.getQuantity());
						pstmt.setInt(2, dishInfo.getDishId());
						int numUpdatedRow = pstmt.executeUpdate();
						pstmt.close();
						if (numUpdatedRow != 1) {
							throw new SQLException("Updating the dish's num_onsale has an error!");
						}
					}
				}
				
				// Track the order id with the restaurant name
				String restaurant_name = Restaurant.findRestaurant(restaurant_id).getName();
				orderIdByRestaurant.put(order_id+"", restaurant_name);
				// Add the order ID
				order_id++;
			}
			
			// Commit all database operations
			conn.commit();

			// Set the JSON result
			retJSON.put("result", "success");
			String orderIdStr = "";
			for (order_id=start_order_id; order_id<start_order_id+numOrderIds; order_id++) {
				orderIdStr += ""+order_id;
				if (order_id < start_order_id+numOrderIds-1) {
					orderIdStr +=",";
				}
			}
			retJSON.put("order_id", orderIdByRestaurant);
			retJSON.put("order_status", status);

			// Send order confirmed email
			sendOrderEmail(request, status, orderIdStr, email, payment);

			// Clear shopping cart from the session
			ShoppingCart.clearFromSession(request.getSession());
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return retJSON;
	}
	
	private long findMaximumOrderId(Connection conn) throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement("SELECT MAX(order_id) as max_order_id FROM orders");
		ResultSet rets = pstmt.executeQuery();
		rets.next();
		long max_order_id = rets.getLong("max_order_id");
		rets.close();
		pstmt.close();
		return max_order_id;
	}
	
	private void sendOrderEmail(HttpServletRequest request, String status, String order_idStr, String email, double payment) throws Exception {
		JSONObject json = (JSONObject) getOrders(request);
		EmailSender ea = new EmailSender();
		String subject = "Order Confirmation: " + order_idStr + " on USLUNCHBOX.COM";
		StringBuffer msgBuf = new StringBuffer();
		if (status.equals("unplaced")) {
			msgBuf.append("Your order on USLUNCHBOX.COM is already submitted. The order number is <strong style='font-weight:bold;color:red'>" + order_idStr + "</strong>. It includes:<P>");
		}
		else {
			msgBuf.append("Your order on USLUNCHBOX.COM is already placed. The order number is <strong style='font-weight:bold;color:red'>" + order_idStr + "</strong>. It includes:<P>");
		}
		msgBuf.append("***********************************");
		msgBuf.append("<table border=\"1\"><tr><td>Dish</td><td>Side Dish</td><td>Staple Food</td><td>Quantity</td><td>Price</td></tr>");
		JSONArray ja = (JSONArray) json.get("dishes");
		for(int i = 0; i < ja.size(); i++){
			JSONObject obj = (JSONObject) ja.get(i);
			msgBuf.append("<tr><td>" + obj.get("dish_name") + 
					"</td><td>" + obj.get("side_dish_name")+
					"</td><td>" + obj.get("staple_food_name")+
					"</td><td>" + obj.get("quantity") + 
					"</td><td>$" + obj.get("dish_price") +  
					"</td></tr>");
		}
		msgBuf.append("</table><br>");
		double sum = Double.valueOf(json.get("price_sum").toString());
		msgBuf.append("Total dishes:&nbsp;&nbsp;<b>" + json.get("quantity_sum") + 
				"</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Total price:&nbsp;&nbsp;<b>$" + 
				sum  + "</b><br>");
		msgBuf.append("***********************************");
		
		msgBuf.append("<br/>");
		if(payment != 0.0)
			msgBuf.append("<strong style='font-weight:bold;color:red'>You need to pay $" + payment + " when you pick up your lunchbox (Cash Only). ");
		msgBuf.append("Please pick up the lunchbox at " + request.getParameter("deliver_time") + "</strong>. " + 
				"If you want to cancel or change this order, please contact the administrator.");
		msgBuf.append("<P>");
		
		if (status.equals("unplaced")) {
			String orderIdEncoded = "";			
			orderIdEncoded = java.net.URLEncoder.encode(DataEncryption.encryptStringData(order_idStr));
			msgBuf.append("<strong style='font-weight:bold;color:red'>Please click " +
					"<a href=\"http://"+HostURL.HOST_URL+"/orderconfirm.html?orderid=" + orderIdEncoded + "\">here</a> to confirm your order" +
				    "</strong>, otherwise your order will not be placed.<P>");
			msgBuf.append("By clicking <a href=\"http://" + HostURL.HOST_URL + "/register.html\">here</a>, you can become a member of USLUNCHBOX.COM. ");
		}
		else {
			msgBuf.append("<P>");
		}
		
		msgBuf.append("If you prefer further befenit (10% off for dine-in at Weis Long Gong Chinese Restaurant), "
				+ "you need to get a SUN DINING card (only $5). Please come to the following address to get your USLUNCHBOX card:<P>"
				+ "<I>ECS 251 or 268<br/>"
				+ "11200 SW 8th Street,<br/>Miami, Florida 33199</I><P>");
				
		msgBuf.append("<P>Sincerely,<br/>USLUNCHBOX Team");
		String from = "info@uslunchbox.com";
		ea.sendSSLMessage(new String[] { email }, null, null, subject, msgBuf.toString(), from);
	}


}
