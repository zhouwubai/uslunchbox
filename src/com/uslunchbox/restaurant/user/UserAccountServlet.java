package com.uslunchbox.restaurant.user;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;
import com.uslunchbox.restaurant.dish.DishCategory;

/**
 * Servlet implementation class UserAccountServlet
 */
@WebServlet("/UserAccountServlet")
public class UserAccountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserAccountServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		User user = User.getFromSession(request.getSession());
		if (user == null) {
			JSONObject userInfo = new JSONObject();
			userInfo.put("result", "notsignin");
			response.getWriter().write(userInfo.toJSONString());
			return;
		}
		
		long user_id = user.getUserId(); //= 1;
		Connection conn = ConnectionManager.getInstance().getConnection();
		String getUserQuery = "select u.first_name, u.last_name, u.middle_name, u.email,  u.nick_name, u.register_date, u.last_access_date, u.status, u.phone_number, " +
							" i.institution_name, " +
								" p.user_priviledge_type, u.prepaid " +
									" from users u, institutions i, user_priviledge p " +
										" where u.user_id = ? AND u.institution_id = i.institution_id AND u.user_priviledge_id = p.user_priviledge_id";

		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_YEAR, 1); // after one day
		String expire_date = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(new Timestamp(now.getTimeInMillis()));
		System.out.println(expire_date);
		String getUserPoint = "select points from points where user_id = " + user_id + " AND expire_date > \'" + expire_date + "\'";
		//System.out.println(",," + user_id);
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(getUserQuery);
			pstmt.setLong(1, user_id);
			ResultSet retUser = pstmt.executeQuery();
			JSONObject userInfo = new JSONObject();
			if (retUser.next()) {
				userInfo.put("result", "success");
				userInfo.put("first_name", retUser.getString(1));
				userInfo.put("last_name", retUser.getString(2));
				userInfo.put("middle_name", retUser.getString(3));
				userInfo.put("email", retUser.getString(4));
				userInfo.put("nick_name", retUser.getString(5));
				userInfo.put("register_date", new SimpleDateFormat("yyyy.MM.dd").format(retUser.getTimestamp(6)));
				String lastaccess = new SimpleDateFormat("yyyy.MM.dd h:mm a").format(retUser.getTimestamp(6));
				if(retUser.getString(7)!=null && retUser.getString(7).length()!=0  ) {
					lastaccess = new SimpleDateFormat("yyyy.MM.dd h:mm a").format(retUser.getTimestamp(7));
				}
				userInfo.put("last_access_date", lastaccess);
				userInfo.put("status", retUser.getString(8));
				userInfo.put("phone_number", retUser.getString(9));
				userInfo.put("institution_name", retUser.getString(10));
				userInfo.put("priviledge_type", retUser.getString(11));
				userInfo.put("prepaid_balance", retUser.getFloat("prepaid"));
			} else {
				userInfo.put("result", "failed");
			}
			response.getWriter().write(userInfo.toJSONString());
			retUser.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("ddkdkkdkkdll;");
	}

}
