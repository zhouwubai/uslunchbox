package com.uslunchbox.restaurant.social;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sun.misc.BASE64Encoder;
import sun.security.provider.SHA;

import com.google.gson.stream.JsonWriter;

import java.net.*;
import java.util.Random;

/**
 * Servlet implementation class LoginFbServlet
 */
@WebServlet("/LoginFbServlet")
public class LoginFbServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginFbServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String appID = FBApp.AppID;
		BASE64Encoder be = new BASE64Encoder();
		String randomStr = be.encode(new Double(new Random().nextDouble()).toString().getBytes());
		HttpSession session = request.getSession(true);
		session.setAttribute("state", randomStr);
		
		String origUrl = request.getParameter("redirect_uri");
		if (origUrl == null) {
			origUrl = "http://uslunchbox.com/uslunchbox/onlineorder.html";
		}
		session.setAttribute("curUrl", origUrl);
		
		session.setAttribute("state", randomStr);
		System.err.println(origUrl);
		String authURL = "https://www.facebook.com/dialog/oauth?"
			    + "client_id=" + appID + "&redirect_uri=" + URLEncoder.encode(origUrl,"utf-8") 
			    + "&state=" + URLEncoder.encode(randomStr,"utf-8") + "&scope=email,read_stream";
		JsonWriter jw = new JsonWriter(response.getWriter());
		jw.beginObject().name("redirect").value(authURL).endObject();
		jw.flush();
	}

}
