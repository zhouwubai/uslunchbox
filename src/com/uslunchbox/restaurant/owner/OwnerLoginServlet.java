package com.uslunchbox.restaurant.owner;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

/**
 * Servlet implementation class OwnerLoginServlet
 */
@WebServlet("/OwnerLoginServlet")
public class OwnerLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OwnerLoginServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String nickName = request.getParameter("nickname");
		String password = request.getParameter("password");
		JSONObject retJson = new JSONObject();
		if (Owner.validate(nickName, password, request.getSession())) {
			retJson.put("result", "true");
			Owner owner = Owner.getFromSession(request.getSession());
			retJson.put("nickname", owner.getNickName());
		} else {
			retJson.put("result", "false");
		}
		response.getWriter().write(retJson.toString());

	}

}
