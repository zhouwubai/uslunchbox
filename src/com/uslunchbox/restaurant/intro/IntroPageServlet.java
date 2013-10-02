package com.uslunchbox.restaurant.intro;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.dish.Dish;
import com.uslunchbox.restaurant.dish.DishCategory;
import com.uslunchbox.restaurant.review.Review;

/**
 * Servlet implementation class HomePageServlet
 */
@WebServlet("/IntroPageServlet")
public class IntroPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IntroPageServlet() {
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
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// get restaurant information
		String rid = request.getParameter("restaurantid").trim();
		JSONObject resJson = getRestaurant(Integer.valueOf(rid));
		JSONArray newsJson = getRestaurantNews(Integer.valueOf(rid));
		
		// get dishes
		String categoryId = request.getParameter("categoryid");
		JSONArray categoriesJSON = getAllCategories();
		JSONArray dishesJSON;
		if (categoryId == null) {
			dishesJSON = getDishes(0);
		}
		else {
			dishesJSON = getDishes(Integer.parseInt(categoryId));
		}
		
		//System.out.println(newsJson.toJSONString());
		JSONObject restaurantJson = new JSONObject();
		restaurantJson.put("info", resJson);
		restaurantJson.put("news", newsJson);
		restaurantJson.put("categories", categoriesJSON);
		restaurantJson.put("dishes", dishesJSON);
		
//		JSONArray retJson = Review.queryTopThreePostedReviews(Integer.valueOf(rid));
//		if(retJson.size() == 0)
//			restaurantJson.put("message", "noreview");
//		else{
//			restaurantJson.put("message", "success");
//			restaurantJson.put("review", retJson);
//		}
		response.getWriter().write(restaurantJson.toJSONString());
		
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject getRestaurant(int rid){
		JSONObject resJson = new JSONObject();
		Restaurant res = Restaurant.getRestaurant(rid);
		
		resJson.put("rid", rid);
		resJson.put("rname", res.getRestaurantName());
		resJson.put("raddress", res.getAddress());
		resJson.put("rcity", res.getCity());
		resJson.put("rstate", res.getState());
		resJson.put("rcountry", res.getCountry());
		resJson.put("rzip", res.getZip());
		resJson.put("rphone", res.getPhone());
		resJson.put("rurl", res.getUrl());
		resJson.put("rintro", Restaurant.getRestaurantIntro(rid));
		
		return resJson;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray getRestaurantNews(int rid){
		JSONArray resJson = new JSONArray();
		List<News> news = News.getNewsForRestaurant(rid);
		for(News n : news){
			JSONObject json = new JSONObject();
			json.put("nid", n.getNewsId());
			json.put("ncontent", n.getNewsContent());
			json.put("ndate", n.getNewsDate());
			json.put("ntype", n.getNewsType());
			resJson.add(json);
		}
		return resJson;
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
	
	private JSONArray getDishes(int category_id) {
		JSONArray dishesJson = new JSONArray();
		List<Dish> dishList;
		int site_id = 0;
		if (category_id == 0) {
			dishList = Dish.getActivatedDishes(site_id);
		}
		else {
			dishList = Dish.getActivatedDishes(category_id, site_id);
		}
		for (Dish dish : dishList) {
			JSONObject dishJson = new JSONObject();
			dishJson.put("name", dish.english_name);
			dishJson.put("price", dish.lunch_price);
			dishJson.put("id", dish.id);
			dishesJson.add(dishJson);
		}
		return dishesJson;
	}

}
