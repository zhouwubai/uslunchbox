package com.uslunchbox.restaurant.user;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;


/**
 * Servlet implementation class MemberLoginServlet
 */
@WebServlet("/MemberLoginServlet")
public class UserLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserLoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("ssssssss");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		
		JSONObject retJson = new JSONObject();
		String email = request.getParameter("email").trim();
		String password = request.getParameter("password").trim();
		if (User.validateByEmail(email, password, request.getSession())) {			
			User user = User.getFromSession(request.getSession());
			if (user.isActivated() == false) {
				retJson.put("result", "inactivated");
				User.removeFromSession(request.getSession());
			}
			else {
				retJson.put("result", "true");
				retJson.put("nick_name", user.nick_name);
				retJson.put("firstname", user.getFirstName());
			}
		}
		else {
			retJson.put("result", "false");
		}
		response.getWriter().write(retJson.toString());		
	}

}
