package com.uslunchbox.restaurant.site;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.order.ShoppingCart;

/**
 * Servlet implementation class SiteResetServlet
 */
@WebServlet("/SiteResetServlet")
public class SiteResetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SiteResetServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		JSONObject retJSON = new JSONObject();
		String selectedSiteIdStr = request.getParameter("site");
		int selectedSiteId = Integer.parseInt(selectedSiteIdStr);
		Site currentSite = Site.getSiteFromSession(session);
		if (currentSite == null || currentSite.getSiteId() != selectedSiteId) {
			// Clear shopping cart
			ShoppingCart.clearFromSession(session);
		}
		
		Map<Integer, Site> allSites = Site.getActiveSites();
		if (allSites.containsKey(selectedSiteId) == false) {
			retJSON.put("ret", "false");
		}
		else {
			Site selectedSite = allSites.get(selectedSiteId);
			Site.saveSiteToSession(session, selectedSite);
			retJSON.put("ret", "OK");
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
