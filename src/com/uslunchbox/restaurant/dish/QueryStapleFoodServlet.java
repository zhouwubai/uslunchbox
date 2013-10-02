package com.uslunchbox.restaurant.dish;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;

/**
 * Servlet implementation class QueryStapleFoodServlet
 */
@WebServlet("/QueryStapleFoodServlet")
public class QueryStapleFoodServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryStapleFoodServlet() {
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
		JSONObject stapleFoodsJSON = new JSONObject();
		JSONArray stapleFoodArrayJSON = new JSONArray();
		Map<Integer, StapleFood> stapleFoodMap = StapleFood.getAllStapleFoods();
		for (Map.Entry<Integer, StapleFood> entry : stapleFoodMap.entrySet()) {
			JSONObject foodJSON = new JSONObject();
			foodJSON.put("id", entry.getKey());
			foodJSON.put("name", entry.getValue().name);
			stapleFoodArrayJSON.add(foodJSON);
		}
		stapleFoodsJSON.put("staplefoods", stapleFoodArrayJSON);
		response.getWriter().write(stapleFoodsJSON.toJSONString());
	}

}
