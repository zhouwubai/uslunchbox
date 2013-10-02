package com.uslunchbox.restaurant.order;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.uslunchbox.restaurant.site.Site;

/**
 * Servlet implementation class DeliverLocationAndTimeServlet
 */
@WebServlet("/DeliverLocationAndTimeServlet")
public class DeliverLocationAndTimeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeliverLocationAndTimeServlet() {
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
		
		Site site = Site.getSiteFromSession(request.getSession());
		String action = request.getParameter("action");
		JSONArray jarray = new JSONArray();
		
		if(action.equals("getlocation")){
			List<String> locations = Site.getDeliverLocationsBySite(site.getSiteId());
			for(String s : locations){
				JSONObject json = new JSONObject();
				try {
					json.put("location", s);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				jarray.put(json);
			}
		}else if(action.equals("gettime")){
			String location = request.getParameter("location");
			List<String> time = Site.getDeliverTimeBySiteAndLocation(site.getSiteId(), location);
			for(String s : time){
				JSONObject json = new JSONObject();
				try {
					json.put("time", s.split(",")[0]);
					json.put("mapurl", s.split(",")[1]);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				jarray.put(json);
			}
		}else{
			throw new Error("Unknown action : " + action);
		}
		
		response.getWriter().write(jarray.toString());		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
