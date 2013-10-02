package com.uslunchbox.restaurant.user;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.uslunchbox.restaurant.utils.DataEncryption;
import com.uslunchbox.restaurant.utils.EmailSender;
import com.uslunchbox.restaurant.utils.HostURL;

/**
 * Servlet implementation class UserRegisterServlet
 */
@WebServlet("/servlet/UserRegisterServlet")
public class UserRegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserRegisterServlet() {
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		User u = getUserObject(request);
		String userinfo = User.insertUser(u);
		JSONObject json = new JSONObject();
		try {
			if (userinfo.equals("")) {
				json.put("info", "failed");
			} else if(userinfo.equals("duplicateemail")){
				json.put("info", "duplicateemail");
			} else {
				json.put("info", "success");
				
				EmailSender ea = new EmailSender();
				String[] recipients = { request.getParameter("email") };
				String subject = "Email Confirmation: "
						+ request.getParameter("firstName") + " "
						+ request.getParameter("lastName")
						+ "'s recent registration on USLUNCHBOX.COM";
				String link = "http://"+HostURL.HOST_URL+"/registerconfirm.html?key="
						+  java.net.URLEncoder.encode(DataEncryption.encryptStringData(userinfo));
				String message = "Dear <B>" + request.getParameter("firstName") + " " + request.getParameter("lastName") + "</B>,<P>" 
						+ "Thank you for registering on <a href=http://www.uslunchbox.com>USLUNCHBOX.COM</a>. "
						+ "Please click<br/>"+ link + "<br/>to activate your account. Now you can start to enjoy the benefit of getting "
						+ "1 free lunchbox for every 10 orders once the online ordering is alive. "
						+ "<br>If you prefer further befenit (10% off for dine-in at South Garden Chinese Restaurant), "
						+ "you need to get a SUN DINING card (only $5). Please come to the following address to get your USLUNCHBOX card:<P>"
						+ "<I>ECS 251 or 268<br/>"
						+ "11200 SW 8th Street,<br/>Miami, Florida 33199</I><P>"
						+ "Sincerely,<br/>USLUNCHBOX Team";

				String from = "info@uslunchbox.com";
				ea.sendSSLMessage(recipients, null, null, subject, message, from);
			}
			
			response.getWriter().write(json.toString());	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static User getUserObject(HttpServletRequest request) {
		User u = new User();

		u.setFirstName(request.getParameter("firstName"));
		u.setLastName(request.getParameter("lastName"));
		u.setMiddle_name(request.getParameter("middleName").equals("") ? ""
				: request.getParameter("middleName"));
		u.setEmail(request.getParameter("email"));
		u.setInstitution_id(Integer.valueOf(request.getParameter("univ")));
		u.setUser_priviledge_id(1); // default
		u.setPassword(request.getParameter("password"));
		u.setNickName(request.getParameter("nickname"));
		u.setRegister_date(new Timestamp((new Date()).getTime()));
		u.setLast_access_date(new Timestamp((new Date()).getTime()));
		u.setPhoneNumber(request.getParameter("phone_number"));
		u.setActivated(false);
		return u;
	}
	
}
