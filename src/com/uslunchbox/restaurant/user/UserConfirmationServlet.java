package com.uslunchbox.restaurant.user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.uslunchbox.restaurant.utils.DataEncryption;

/**
 * Servlet implementation class UserConfirmationServlet
 */
@WebServlet("/servlet/UserConfirmationServlet")
public class UserConfirmationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserConfirmationServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String userinfo = "";
		try {
			userinfo = DataEncryption.decryptStringData(java.net.URLDecoder.decode(request.getParameter("key")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject json = new JSONObject();

		String[] infos = userinfo.split("\\|\\|\\|\\|");
		User u = new User();
		u.setEmail(infos[0]);
		u.setPassword(infos[1]);
		u = User.queryUser(u);
		try {
			if (u.getUserId() == 0) {
				json.put("info", "notexist");
			} else {
				u = User.queryUserActivated(u);
				if (u.isActivated()) {
					json.put("info", "already");
				} else {
					User.activateUser(u.getEmail());
					// int cardNo = Card.activateCardForUser(u);
					json.put("info", "success");
					JSONObject user = new JSONObject();
					user.put("firstname", u.getFirstName());
					user.put("lastname", u.getLastName());
					// user.put("cardno", cardNo);
					json.put("user", user);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response.getWriter().write(json.toString());

	}

}
