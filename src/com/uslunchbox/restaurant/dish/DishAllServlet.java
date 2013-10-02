package com.uslunchbox.restaurant.dish;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.json.JSONArray;
import org.json.JSONException;

import com.uslunchbox.restaurant.dish.Dish;
import com.uslunchbox.restaurant.site.Site;



/**
 * Servlet implementation class DishListServlet
 */
@WebServlet("/DishAllServlet")
public class DishAllServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DishAllServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("start");
		HttpSession session = request.getSession();
		int site_id=1;
		if (Site.getSiteFromSession(session) == null) {
			Site s=Site.getSiteFromSession(session);
			site_id=s.getSiteId();
		}
		String id = request.getParameter("id").toString();
		//System.out.println(id);
		JSONArray jarray;
				try {
					if(!id.equalsIgnoreCase("")){
					jarray = Dish.getRestaurantDisheWithReviews(site_id,Integer.parseInt(id));
					}else{
						jarray = new JSONArray();
					}
				} catch (NumberFormatException nFE) {
					jarray = new JSONArray();
				}
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		response.getWriter().write(jarray.toString());
			
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

/*	private JSONArray getActivatedDishes(int dish_id, HttpServletRequest request) {
		JSONArray dishesJson = new JSONArray();
		List<Dish> dishList;
		if (dish_id == 0) {
			dishList = Dish.getActivatedDishes('1');
		}
		else {
			dishList= new ArrayList<Dish>();
			dishList.add(Dish.findDishByDishid(dish_id,'1'));
		}
		for (Dish dish : dishList) {
			JSONObject dishJson = new JSONObject();
			dishJson.put("en_name", dish.english_name);
			dishJson.put("cn_name", dish.chinese_name);
			dishJson.put("desc",dish.desc);
			dishJson.put("price", dish.lunch_price);
			dishJson.put("id", dish.id);
			dishJson.put("rating", "4");
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
		}*/
}
