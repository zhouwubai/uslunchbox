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

import com.uslunchbox.restaurant.dish.Dish;
import com.uslunchbox.restaurant.site.Site;

/**
 * Servlet implementation class SiteCheckServlet
 */
@WebServlet("/ReviewCheckDishServlet")
public class ReviewCheckDishServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReviewCheckDishServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONObject retJSON = new JSONObject();
		String dish_id = request.getParameter("id").toString();
		if (dish_id.equalsIgnoreCase("")){
			retJSON.put("ret", "false");
		}else{
			Boolean result=Dish.checkDishByDishid(Integer.parseInt(dish_id));
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
