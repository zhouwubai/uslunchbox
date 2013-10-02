package com.uslunchbox.restaurant.order;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
//import org.json.simple.JSONException;
import org.json.simple.JSONObject;

/**
 * Servlet implementation class OrderGeoTemp
 */
@WebServlet("/DeliverGeoTimeServlet")
public class DeliverGeoTimeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeliverGeoTimeServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	private JSONArray getFeasible(String abbrev) {
		JSONArray jsonArray = new JSONArray();
		String noon = "";
		String evening = "";
		if ("FIUMMC".equalsIgnoreCase(abbrev)) {
			noon = " 12:00:00";
			evening = " 18:00:00";
		} else if ("FIUEC".equalsIgnoreCase(abbrev)) {
			noon = " 12:30:00";
			evening = " 18:30:00";
		}

		Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String deliver_time = dateFormat.format(cal.getTime());
		if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
				&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			if (cal.get(Calendar.HOUR_OF_DAY) < 11) {
				jsonArray.add(deliver_time + noon);
			}
		}

		if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
				&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
				&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
			if (cal.get(Calendar.HOUR_OF_DAY) < 17) {
				jsonArray.add(deliver_time + evening);
			}
		}

		// Tomorrow
		cal.add(Calendar.DAY_OF_YEAR, 1);
		deliver_time = dateFormat.format(cal.getTime());
		if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
				&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			jsonArray.add(deliver_time + noon);
		}
		if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
				&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
				&& cal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
			jsonArray.add(deliver_time + evening);
		}

		return jsonArray;

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		
		ArrayList<DeliverGeoTime> list = DeliverGeoTime.getDeliverGeoTimeList();

		for (DeliverGeoTime e : list) {
			JSONObject json = new JSONObject();
			json.put("location", e.getLocation());
			json.put("latitude", e.getLatitude());
			json.put("longitude", e.getLongitude());
			json.put("name", e.getAbbrev());
			json.put("deliverTime", getFeasible(e.getAbbrev()));
			
			jsonArray.add(json);
		}

		response.getWriter().write(jsonArray.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
