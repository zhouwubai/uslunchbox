package com.uslunchbox.restaurant.user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;


/**
 * Servlet implementation class UserAliveServlet
 */
@WebServlet("/UserAliveServlet")
public class UserAliveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserAliveServlet() {
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
		User user = User.getFromSession(request.getSession());
		JSONObject retJson = new JSONObject();
		
		if (user!=null) {
			retJson.put("result", "true");
			retJson.put("nick", user.nick_name);
			retJson.put("fname", user.getFirstName());
			retJson.put("email", user.getEmail());
			retJson.put("userid", user.getUserId());
			if (user.getFBAccount() != null)
				retJson.put("type", "fb");
			else
				retJson.put("type", "normal");
		}
		else {
			retJson.put("result", "false");
		}
		response.getWriter().write(retJson.toString());	
	}

}
