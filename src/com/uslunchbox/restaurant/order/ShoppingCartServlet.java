package com.uslunchbox.restaurant.order;

import java.io.IOException;
import java.text.DecimalFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.dish.Dish;
import com.uslunchbox.restaurant.site.Site;


/**
 * Servlet implementation class ShoppingCartServlet
 */
@WebServlet("/ShoppingCartServlet")
public class ShoppingCartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShoppingCartServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		
		String action = request.getParameter("action");
		if (action.equalsIgnoreCase("add")) {
			response.getWriter().write(addItem(request).toJSONString());			
		}
		else if (action.equalsIgnoreCase("list")) {
			response.getWriter().write(listAllItems(request).toJSONString());
		}
		else if (action.equalsIgnoreCase("update")) {
			response.getWriter().write(updateAllItems(request).toJSONString());
		}
		else if (action.equalsIgnoreCase("delete")) {
			response.getWriter().write(deleteItem(request).toJSONString());
		}
		else if (action.equalsIgnoreCase("numitems")) {
			response.getWriter().write(getNumItems(request).toJSONString());
		}
		else {
			throw new Error("Unknown action : "+action);
		}
	}
	
	private JSONObject getNumItems(HttpServletRequest request) {
		HttpSession session = request.getSession();
		ShoppingCart cart = ShoppingCart.getFromSession(session);
		if (cart == null) {
			cart = new ShoppingCart();
			ShoppingCart.addToSession(cart, session);
		}
		JSONObject retJSON = new JSONObject();
		retJSON.put("numitems", cart.getTotalItems());
		return retJSON;
	}
	
	private JSONObject addItem(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Site site = Site.getSiteFromSession(session);
		if (site == null) {
			throw new Error("no site specified!");
		}
		
		ShoppingCart cart = ShoppingCart.getFromSession(session);
		if (cart == null) {
			cart = new ShoppingCart();
			ShoppingCart.addToSession(cart, session);
		}
		int dishId = Integer.parseInt(request.getParameter("dish_id"));
		int staple_food_id = Integer.parseInt(request.getParameter("staple_food_id"));
		int side_dish_id = Integer.parseInt(request.getParameter("side_dish_id"));
		int quantity = Integer.parseInt(request.getParameter("quantity"));
		String location = request.getParameter("location");
		
		String time = request.getParameter("time");
		cart.addItem(site.getSiteId(), dishId, staple_food_id, side_dish_id, quantity, location, time);
		JSONObject retObj = new JSONObject();
		retObj.put("numitems", cart.getTotalItems());
		return retObj;
	}
	
	private JSONObject listAllItems(HttpServletRequest request) {
		HttpSession session = request.getSession();
		DecimalFormat df = new DecimalFormat(".##");
		ShoppingCart cart = ShoppingCart.getFromSession(session);
		if (cart == null) {
			cart = new ShoppingCart();
			ShoppingCart.addToSession(cart, session);
		}
		JSONObject retJSON = new JSONObject();
		JSONArray dishesJSON = new JSONArray();
		double total_price = 0;
		int total_quantity = 0;
		for (OrderDishInfo dishInfo : cart.getAllDishes()) {
			JSONObject dishJSON = new JSONObject();
			dishJSON.put("dish_id", dishInfo.getDishId());
			dishJSON.put("dish_name", dishInfo.getDishName());
			dishJSON.put("staple_food_id", dishInfo.getStapleFoodId());
			dishJSON.put("staple_food_name", dishInfo.getStapleFoodName());
			dishJSON.put("side_dish_id", dishInfo.getSideDishId());
			dishJSON.put("side_dish_name", dishInfo.getSideDishName());
			dishJSON.put("restaurant_name", dishInfo.getRestaurantName());
			dishJSON.put("quantity", dishInfo.getQuantity());
			dishJSON.put("price", df.format(dishInfo.getPrice()*dishInfo.getQuantity()));
			dishJSON.put("location", dishInfo.getLocation());
			dishJSON.put("time", dishInfo.getDeliverTime());
			dishesJSON.add(dishJSON);
			total_price += dishInfo.getPrice()*dishInfo.getQuantity();
			total_quantity += dishInfo.getQuantity();
		}
		retJSON.put("pricesum", df.format(total_price));
		retJSON.put("dishes", dishesJSON);
		retJSON.put("quantitysum", total_quantity);
		return retJSON;
	}
	
	private JSONObject updateAllItems(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Site site = Site.getSiteFromSession(session);
		JSONObject retJSON = new JSONObject();
		if (site == null) {
			retJSON.put("result", "failed");
			return retJSON;
		}
		retJSON.put("result", "success");
		String dish_ids_str = request.getParameter("dish_ids_str");
		if (dish_ids_str == null) {
			return retJSON;
		}
		dish_ids_str = dish_ids_str.trim();
		if (dish_ids_str.length() == 0) {
			return retJSON;
		}
		String[] dish_ids = dish_ids_str.split(",");
		if (dish_ids == null || dish_ids.length == 0) {
			retJSON.put("result", "failed");
			return retJSON;
		}
		int numDishes = dish_ids.length;
		String quantities_str = request.getParameter("dish_quantities_str");
		String[] quantities = quantities_str.split(",");
		String staplefood_id_str = request.getParameter("staple_food_ids_str");
		String[] staplefood_ids = staplefood_id_str.split(",");
		String sidedish_id_str = request.getParameter("side_dish_ids_str");
		String[] sidedish_ids = sidedish_id_str.split(",");
		String location_str = request.getParameter("location_str");
		String[] locations = location_str.split(",");
		String time_str = request.getParameter("time_str");
		String[] times = time_str.split(",");
		// Check the updated quantity
		boolean isQuantityValidate = true;
		for (int i = 0; i < numDishes; i++) {
			int dish_id = Integer.parseInt(dish_ids[i]);
			Dish dish = Dish.findActivatedDish(dish_id, site.getSiteId());
			int quantity = Integer.parseInt(quantities[i]);
			if (quantity <= 0) {
				continue;
			}
			if (dish.num_onsale >= 0 && quantity > dish.num_onsale) {
				retJSON.put("result", "excess");
				retJSON.put("dishname", dish.english_name);
				retJSON.put("num_onsale", dish.num_onsale);
				isQuantityValidate = false;
				break;
			}
		}
		if (isQuantityValidate == false) {
			return retJSON;
		}

		ShoppingCart cart = new ShoppingCart();
		ShoppingCart.addToSession(cart, session);

		for (int i = 0; i < numDishes; i++) {
			int dish_id = Integer.parseInt(dish_ids[i]);
			int quantity = Integer.parseInt(quantities[i]);
			int staplefood_id = Integer.parseInt(staplefood_ids[i]);
			int sidedish_id = Integer.parseInt(sidedish_ids[i]);
			if (quantity <= 0) {
				continue;
			}
			String location = locations[i];
			String time = times[i];
			cart.addItem(site.getSiteId(), dish_id, staplefood_id, sidedish_id, quantity, location, time);
		}

		return retJSON;
	}
	
	private JSONObject deleteItem(HttpServletRequest request) {
		HttpSession session = request.getSession();
		ShoppingCart cart = ShoppingCart.getFromSession(session);
		if (cart == null) {
			cart = new ShoppingCart();
			ShoppingCart.addToSession(cart, session);
		}
		int delete_dish_id = Integer.parseInt(request.getParameter("delete_dish_id"));
		int delete_staple_food_id = Integer.parseInt(request.getParameter("delete_staple_food_id"));
		cart.removeItem(delete_dish_id, delete_staple_food_id);
		return listAllItems(request);		
	}
	
	
	
}
