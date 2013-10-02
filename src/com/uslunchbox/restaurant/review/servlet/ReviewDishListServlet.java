package com.uslunchbox.restaurant.review.servlet;

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
@WebServlet("/ReviewDishListServlet")
public class ReviewDishListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReviewDishListServlet() {
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
		try {
			HttpSession session = request.getSession();
			int site_id=1;
			if (Site.getSiteFromSession(session) == null) {
				Site s=Site.getSiteFromSession(session);
				site_id=s.getSiteId();
			}
			//System.out.println("start");
			JSONArray jarray;
				String id = request.getParameter("id").toString();
				//System.out.println(id);

				if (id.equals("all")) {
					jarray = Dish.getDishOrderByRating(false);
				} else if (id.equals("")) {
					jarray = new JSONArray();
				} else {
					try {
						jarray = Dish.findDishByDishid(Integer.parseInt(id),site_id);
					} catch (NumberFormatException nFE) {
						jarray = new JSONArray();
					}
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}
