package com.uslunchbox.restaurant.social;
import com.restfb.*;
import com.restfb.json.*;
import com.uslunchbox.restaurant.database.ConnectionManager;
import com.uslunchbox.restaurant.user.User;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class UpdateFbServlet
 */
@WebServlet("/UpdateFbServlet")
public class UpdateFbServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateFbServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String appID = FBApp.AppID;
		String appSecret = FBApp.AppSecret;
		
		String accesstoken = null;
		Integer exp = null;
		Calendar cal = Calendar.getInstance();
		
		
		
		String code = request.getParameter("code");
		boolean success = true;
		String message = "";
		if (code == null) {
			System.err.println("something wrong");
			success = false;
			message = "no code input";
		} else {
			HttpSession session = request.getSession(true);
			String state = (String)session.getAttribute("state");
			String stateReq = URLDecoder.decode(request.getParameter("state"),"utf-8");
			System.err.println("start1:" + state);
			System.err.println("start2:" + stateReq);
			if (request.getParameter("error") != null) {
				success = false;
				
				if (request.getParameter("error_message") != null)
					message = request.getParameter("error_message");
			}
			if (state != null && state.equals(stateReq)) {
				String curUrl = (String)session.getAttribute("curUrl");
				System.err.println(curUrl);
				String tokenUrl = "https://graph.facebook.com/oauth/access_token?"
			       + "client_id=" + appID + "&redirect_uri=" + URLEncoder.encode(curUrl, "utf-8")
			       + "&client_secret=" + appSecret + "&code=" + code;
				
				URL url = new URL(tokenUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET"); 
				connection.connect();
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line = br.readLine();
					String[] parastrs = line.split("&");
					
					for(String p : parastrs) {
						String[] fds = p.split("=");
						if (fds[0].equals("access_token"))
							accesstoken = fds[1];
						if (fds[0].equals("expires"))
							exp = new Integer(Integer.parseInt(fds[1]));
					}
					br.close();
				} catch (Exception e) {
					success = false;
					message = "fail to fetch access_token";
					e.printStackTrace();
				}
			} else {
				success = false;
				message = "states are not matched";
			}
		}
		
		
		if (accesstoken != null) {
			System.err.print("access_token: " + accesstoken);
//			FacebookClient fbClient = new DefaultFacebookClient(accesstoken);
//			com.restfb.types.User profile = fbClient.fetchObject("me", com.restfb.types.User.class);
			FBAccount fbaccount = FBAccount.getFromAccessToken(accesstoken);
			User user = User.getFromSession(request.getSession());
			if (user == null) {
//				user = new com.uslunchbox.restaurant.user.User();
//				user = User.getUserFromFBProfile(profile, request.getSession());
				user = User.getUserFromFBAccount(fbaccount, request.getSession());
				if (user == null) {
					success = false;
					message = "fail to generate new account from fb account";
				}
					
			} else {
//				user.setUserFBProfile(profile);
				if (!user.bindFBAccount(fbaccount)) {
					success = false;
					message = "fail to bind fb account to current user";
				}
					
			}
			if (success) {
				cal.add(Calendar.SECOND, exp);
				user.getFBAccount().updateAccessExp(accesstoken, cal);
			}
			
		} else if (success) {
			success = false;
			message = "fail to fetch access_token";
		}
		JsonWriter jw = new JsonWriter(response.getWriter());
		jw.object().key("result");
		if (success)
			jw.value("success");
		else {
			jw.value("error");
			jw.key("message").value(message);
		}
		jw.endObject();
		response.getWriter().flush();
	}

}
