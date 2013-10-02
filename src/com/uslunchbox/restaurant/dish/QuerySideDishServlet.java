package com.uslunchbox.restaurant.dish;

import java.io.IOException;

import java.util.List;
import java.util.Map;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Servlet implementation class QuerySideDishServlet
 */
@WebServlet("/QuerySideDishServlet")
public class QuerySideDishServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QuerySideDishServlet() {
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
		JSONObject sideDishJSON = new JSONObject();
		JSONArray sideDishArrayJSON = new JSONArray();
		List<SideDish> sideDishList = SideDish.getAllOnSideDishes();
		for (SideDish sideDish : sideDishList) {
			JSONObject foodJSON = new JSONObject();
			foodJSON.put("id", sideDish.id);
			foodJSON.put("name", sideDish.english_name);
			sideDishArrayJSON.add(foodJSON);
		}
		sideDishJSON.put("sidedishes", sideDishArrayJSON);
		response.getWriter().write(sideDishJSON.toJSONString());
	}

}
