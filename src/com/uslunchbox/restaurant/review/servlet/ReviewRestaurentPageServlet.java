package com.uslunchbox.restaurant.review.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;

import com.uslunchbox.restaurant.dish.Dish;
import com.uslunchbox.restaurant.review.RestaurantReview;
import com.uslunchbox.restaurant.review.Review;
import com.uslunchbox.restaurant.user.User;

/**
 * Servlet implementation class ReviewServlet
 */
@WebServlet("/ReviewRestaurentPageServlet")
public class ReviewRestaurentPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReviewRestaurentPageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			//System.out.println("start");
			JSONArray jarray;
			String id = request.getParameter("id").toString();
			if(id.equalsIgnoreCase("")){
				jarray=new JSONArray();
			}else{
			jarray = RestaurantReview.getRestaurantRating(Integer.parseInt(id));
			}
			response.setHeader("Cache-Control", "no-cache");
			response.setContentType("text/json;charset=UTF-8");
			response.getWriter().write(jarray.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
