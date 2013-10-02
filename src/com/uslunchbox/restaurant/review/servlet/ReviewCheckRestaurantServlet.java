package com.uslunchbox.restaurant.review.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.restaurant.Restaurant;


/**
 * Servlet implementation class SiteCheckServlet
 */
@WebServlet("/ReviewCheckRestaurantServlet")
public class ReviewCheckRestaurantServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReviewCheckRestaurantServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONObject retJSON = new JSONObject();
		String Restaurant_id = request.getParameter("id").toString();
		if (Restaurant_id.equalsIgnoreCase("")){
			retJSON.put("ret", "false");
		}else{
			Boolean result=(Restaurant.findRestaurant(Integer.parseInt(Restaurant_id))==null)?true:false;
			retJSON.put("ret", result);
		}
		response.getWriter().print(retJSON.toJSONString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
