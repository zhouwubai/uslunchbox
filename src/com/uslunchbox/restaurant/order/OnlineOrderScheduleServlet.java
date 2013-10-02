package com.uslunchbox.restaurant.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.dish.Dish;
import com.uslunchbox.restaurant.dish.DishCategory;
import com.uslunchbox.restaurant.site.Site;
import com.uslunchbox.restaurant.user.User;

/**
 * Servlet implementation class OnlineOrderScheduleServlet
 * @author Lei
 *
 */
@WebServlet("/OnlineOrderScheduleServlet")
public class OnlineOrderScheduleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OnlineOrderScheduleServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		
		HttpSession session = request.getSession();
		String siteIdStr = request.getParameter("siteid");
		JSONObject retJSON = new JSONObject();
		Site currentSite = null;
		if (siteIdStr != null) {
			// Check the site id
			int siteId = Integer.parseInt(siteIdStr);
			Map<Integer, Site> sites = Site.getActiveSites();
			if (sites.containsKey(siteId) == false) {				
				retJSON.put("ret", "nosite");
				response.getWriter().write(retJSON.toJSONString());
				return;
			}
			else {
				// Save the current site into the session
				currentSite = sites.get(siteId);
				Site.saveSiteToSession(session, currentSite);
			}
		}
		else {
			// Get the site from the session
			currentSite = Site.getSiteFromSession(session);
			if (currentSite == null) {
				retJSON.put("ret", "nosite");
				
				response.getWriter().write(retJSON.toJSONString());
				return;
			}
		}
		
		String categoryId = request.getParameter("categoryid");
		String date = request.getParameter("date");
		JSONArray categoriesJSON = getAllCategories();
		JSONArray dishesJSON;
		if (categoryId == null) {
			dishesJSON = getScheduledDishes(0, currentSite.getSiteId(), date, request);
		}
		else {
			dishesJSON = getScheduledDishes(Integer.parseInt(categoryId), currentSite.getSiteId(), date, request);
		}
		JSONObject catedishJSON = new JSONObject();
		catedishJSON.put("categories", categoriesJSON);
		catedishJSON.put("dishes", dishesJSON);
		
		User user = User.getFromSession(request.getSession());
		if (user!=null) {
			catedishJSON.put("username", user.getFirstName());
		}
		
		response.getWriter().write(catedishJSON.toJSONString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
	}
	
	private JSONArray getAllCategories()  {
		List<DishCategory> dishCategories = DishCategory.getAllFoodCategories();
		JSONArray cateNames = new JSONArray();
		for (DishCategory dishCate: dishCategories) {
			JSONObject cateName = new JSONObject();
			cateName.put("name", dishCate.english_name);
			cateName.put("id", dishCate.id+"");
			cateNames.add(cateName);
		}
		return cateNames;
	}
	
	/**
	 * 
	 * @param category_id If it is 0, it means all categories
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONArray getScheduledDishes(int category_id, int site_id, String date, HttpServletRequest request) {
		JSONArray dishesJson = new JSONArray();
		List<Dish> dishList;
		if (category_id == 0) {
			dishList = Dish.getScheduledDishes(site_id, date);
		}
		else {
			dishList = Dish.getScheduledDishes(category_id, site_id, date);
		}
		
		// Sort the dishes by their popularity		
		dishList = sortedDishListByPopularity(dishList);
		
		
		HttpSession session = request.getSession();
		ShoppingCart cart = ShoppingCart.getFromSession(session);
		if (cart == null) {
			cart = new ShoppingCart();
		}
		Map<Integer, OrderDishInfo> orderedDishes = new HashMap<Integer, OrderDishInfo>();
		for (OrderDishInfo dishInfo : cart.getAllDishes()) {
			orderedDishes.put(dishInfo.getDishId(), dishInfo);
		}
		
		for (Dish dish : dishList) {
			JSONObject dishJson = new JSONObject();
			dishJson.put("name", dish.english_name+"("+dish.chinese_name+")");
			// dishJson.put("chinese_name", dish.chinese_name);
			dishJson.put("price", dish.lunch_price);
			dishJson.put("id", dish.id);
			if(dish.num_onsale >= 0) {
				dishJson.put("num_onsale", dish.num_onsale);
				OrderDishInfo orderedDishInfo = orderedDishes.get(dish.id);
				if (orderedDishInfo == null) {
					dishJson.put("num_ordered", 0);
				}
				else {
					dishJson.put("num_ordered", orderedDishInfo.getQuantity());
				}
			}
			dishesJson.add(dishJson);
		}
		return dishesJson;
	}
	
	private List<Dish> sortedDishListByPopularity(List<Dish> dishList) {
		Map<Integer,Integer> dishSaleCounts = Dish.getRecentDishSaleCounts(7);
		List<KeyValue> dishCountList = new ArrayList<KeyValue>();
		for (Dish dish : dishList) {
			Integer count = dishSaleCounts.get(dish.id);
			if (count == null) {
				count = 0;
			}
			dishCountList.add(new DefaultKeyValue(dish, count));
		}
		
		Collections.sort(dishCountList, new Comparator<KeyValue>() {
			@Override
			public int compare(KeyValue o1, KeyValue o2) {
				// TODO Auto-generated method stub
				Integer count1 = (Integer)o1.getValue();
				Integer count2 = (Integer)o2.getValue();
				if (count1 < count2) {
					return 1;
				}
				else if (count1 > count2) {
					return -1;
				}
				else {
					return 0;
				}
			}
			
		});
		List<Dish> sortedDishList = new ArrayList<Dish>(dishList.size());
		for (KeyValue dishCount : dishCountList) {
			sortedDishList.add((Dish)dishCount.getKey());
		}
		return sortedDishList;
	}


}
