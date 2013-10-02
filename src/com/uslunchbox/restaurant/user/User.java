package com.uslunchbox.restaurant.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.uslunchbox.restaurant.database.ConnectionManager;
import com.uslunchbox.restaurant.social.FBAccount;

/**
 * 
 * @author ltang002
 * 
 */
public class User {

	public long user_id = 0;
	public String nick_name;
	private String first_name;
	private String last_name;
	private String middle_name;
	private String email;
	private int institution_id;
	private int user_priviledge_id;
	private String password;
	private Timestamp register_date;
	private Timestamp last_access_date;
	private boolean activated;
	private String phone_number;
	private double prepaid;
	
	private String type = "normal";
	
	private com.restfb.types.User userFBProfile;
	private FBAccount fbaccount;
	


	private static final String FB_TYPE_USER = "fb";

	private static final String CURRENT_USER_ATTRIBUTE = "currentUser";
	
	private static final String QUERY_MAX_USERID = "SELECT max(user_id) from users";
	
	private static final String INSERT_USER = "insert into users " +
			"(user_id, first_name, last_name, middle_name, email, " +
			"institution_id, user_priviledge_id, password, nick_name, register_date, last_access_date, status, phone_number,prepaid,type) "
			+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String QUERY_USER = "select u.user_id, u.first_name, u.last_name" +
			" from users u where u.email = ? and u.password = ?";
	
	private static final String QUERY_USER_ACTIVATED = "SELECT u.status FROM" +
			" users u WHERE u.user_id = ?";
	
	private static final String QUERY_USER_EMAIL_FOR_LAST_ORDER = "select distinct(email) from orders where deliver_time = ? and status = 'delivered'";
	
	public User(){
	}

	public User(long user_id, String nick_name, String first_name, String last_name) {
		this.user_id = user_id;
		this.nick_name = nick_name;
		this.first_name = first_name;
		this.last_name = last_name;
	}
	
	public User(long user_id, String nick_name) {
		this.user_id = user_id;
		this.nick_name = nick_name;
	}

	public static boolean validateByEmail(String email, String password, HttpSession session) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		boolean ret = false;
		try {
			PreparedStatement pstmt = conn
					.prepareStatement("SELECT m.user_id, m.nick_name, m.first_name, m.last_name, m.status, m.phone_number, m.prepaid "
							+ " FROM users m"
							+ " WHERE m.email=? AND m.password=?");
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			ResultSet retUser = pstmt.executeQuery();
			if (retUser.next()) {
				User user = new User(retUser.getLong("user_id"), retUser.getString("nick_name"), 
						retUser.getString("first_name"), retUser.getString("last_name"));
				boolean isActivated = retUser.getString("status").equals("activated");
				user.setActivated(isActivated);
				user.setEmail(email);
				user.setPassword(password);
				user.setPhoneNumber(retUser.getString("phone_number"));
				user.setPrepaid(retUser.getFloat("prepaid"));
				user.bindFBAccount();
				session.setAttribute(CURRENT_USER_ATTRIBUTE, user);
				ret = true;
			} else {
				ret = false;
			}
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
		return ret;
	}
	
	public static void removeFromSession(HttpSession session) {
		if (getFromSession(session) != null) {
			session.removeAttribute(CURRENT_USER_ATTRIBUTE);
		}
	}

	public static User getFromSession(HttpSession session) {
		if (session.getAttribute(CURRENT_USER_ATTRIBUTE) == null) {
			return null;
		} else {
			return (User) session.getAttribute(CURRENT_USER_ATTRIBUTE);
		}
	}

	private static long getMaxUserId() {
		Connection conn = ConnectionManager.getInstance().getConnection();
		long max = 0;

		PreparedStatement pstmt = null;
		ResultSet retUser = null;
		try {
			pstmt = conn.prepareStatement(QUERY_MAX_USERID);
			retUser = pstmt.executeQuery();
			if (retUser.next()) {
				max = retUser.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				retUser.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return max;
	}

	public static String insertUser(User u) {
		Long max = getMaxUserId();
		
		Connection conn = ConnectionManager.getInstance().getConnection();
		String userinfo = "";
		
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(INSERT_USER);
			pstmt.setLong(1, max+1);
			pstmt.setString(2, u.getFirstName());
			pstmt.setString(3, u.getLastName());
			pstmt.setString(4, u.getMiddle_name());
			pstmt.setString(5, u.getEmail());
			pstmt.setInt(6, u.getInstitution_id());
			pstmt.setInt(7, u.getUser_priviledge_id());
			pstmt.setString(8, u.getPassword());
			pstmt.setString(9, u.getNickName());
			pstmt.setTimestamp(10, u.getRegister_date());
			pstmt.setTimestamp(11, u.getLast_access_date());
			if (u.isActivated()) {
				pstmt.setString(12, "activated");				
			}
			else {
				pstmt.setString(12, "inactivated");	
			}
			pstmt.setString(13, u.getPhoneNumber());
			pstmt.setFloat(14, 0.0f);
			pstmt.setString(15, u.type);
			int success = pstmt.executeUpdate();
			
			if (success > 0) {
				userinfo = u.getEmail()+"||||"+u.getPassword();
				u.user_id = max + 1;
			} 
		} catch(MySQLIntegrityConstraintViolationException e){
			userinfo = "duplicateemail";
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return userinfo;
	}
	

	public static User queryUser(User u) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		
		PreparedStatement pstmt = null;
		ResultSet retUser = null;
		try {
			pstmt = conn.prepareStatement(QUERY_USER);
			pstmt.setString(1, u.getEmail());
			pstmt.setString(2, u.getPassword());
			retUser = pstmt.executeQuery();
			if (retUser.next()) {
				u.setUserId(retUser.getLong(1));
				u.setFirstName(retUser.getString(2));
				u.setLastName(retUser.getString(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				retUser.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return u;
	}
	
	public static User queryUserActivated(User u) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		
		PreparedStatement pstmt = null;
		ResultSet retUser = null;
		try {
			pstmt = conn.prepareStatement(QUERY_USER_ACTIVATED);
			pstmt.setLong(1, u.getUserId());
			retUser = pstmt.executeQuery();
			if (retUser.next()) {
				if (retUser.getString("status").equalsIgnoreCase("activated")) {
					u.setActivated(true);
				} else {
					u.setActivated(false);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				retUser.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return u;
	}
	
	public static void activateUser(String userEmail) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("UPDATE users SET status=? WHERE email=?");
			pstmt.setString(1, "activated");
			pstmt.setString(2, userEmail);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static List<String> getUserEmailsForLastOrder(String deliverTime) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		List<String> emails = new ArrayList<String>();

		PreparedStatement pstmt = null;
		ResultSet retUser = null;
		try {
			pstmt = conn.prepareStatement(QUERY_USER_EMAIL_FOR_LAST_ORDER);
			pstmt.setTimestamp(1, Timestamp.valueOf(deliverTime));
			retUser = pstmt.executeQuery();
			while (retUser.next()) {
				emails.add(retUser.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				retUser.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return emails;
	}

	public static User getUserByEmail(String userEmail) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet retUser = null;
		User user = null;
		try {
			pstmt = conn.prepareStatement("select * from users WHERE email=?");
			pstmt.setString(1, userEmail);
			retUser = pstmt.executeQuery();
			if (retUser.next()) {
				user = new User(retUser.getLong("user_id"), retUser.getString("nick_name"), 
						retUser.getString("first_name"), retUser.getString("last_name"));
				user.email = userEmail;
//				return user;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (user != null)
			user.bindFBAccount();
		return user;
	}
	
	public static User getUserByID(int id) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet retUser = null;
		User user = null;
		try {
			pstmt = conn.prepareStatement("select * from users WHERE user_id=?");
			pstmt.setInt(1, id);
			retUser = pstmt.executeQuery();
			if (retUser.next()) {
				user = new User(retUser.getLong("user_id"), retUser.getString("nick_name"), 
						retUser.getString("first_name"), retUser.getString("last_name"));
				user.email = retUser.getString("email");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (user != null)
			user.bindFBAccount();
		return user;
	}
	
	public long getUserId() {
		return user_id;
	}
	
	public void setUserId(long userId){
		this.user_id = userId;
	}

	public String getNickName() {
		return nick_name;
	}
	
	public void setNickName(String nickName){
		this.nick_name = nickName;
	}

	public String getFirstName() {
		return first_name;
	}

	public void setFirstName(String first_name) {
		this.first_name = first_name;
	}

	public String getLastName() {
		return last_name;
	}

	public void setLastName(String last_name) {
		this.last_name = last_name;
	}

	public String getMiddle_name() {
		return middle_name;
	}

	public void setMiddle_name(String middle_name) {
		this.middle_name = middle_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getInstitution_id() {
		return institution_id;
	}

	public void setInstitution_id(int institution_id) {
		this.institution_id = institution_id;
	}

	public int getUser_priviledge_id() {
		return user_priviledge_id;
	}

	public void setUser_priviledge_id(int user_priviledge_id) {
		this.user_priviledge_id = user_priviledge_id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getRegister_date() {
		return register_date;
	}

	public void setRegister_date(Timestamp register_date) {
		this.register_date = register_date;
	}

	public Timestamp getLast_access_date() {
		return last_access_date;
	}

	public void setLast_access_date(Timestamp last_access_date) {
		this.last_access_date = last_access_date;
	}
	
	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getPhoneNumber() {
		return phone_number;
	}

	public void setPhoneNumber(String phone_number) {
		this.phone_number = phone_number;
	}
	
	public void setPrepaid(double prepaid) {
		this.prepaid = prepaid;
	}
	
	public double getPrepaid() {
		return this.prepaid;
	}
	
	public void updatePrepaidInDB(double newPrepaid) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		try {
			PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET prepaid = ? where user_id = ?");
			pstmt.setFloat(1, (float)newPrepaid);
			pstmt.setLong(2, this.user_id);
			pstmt.executeUpdate();
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

	public com.restfb.types.User getUserFBProfile() {
		return userFBProfile;
	}

	public boolean setUserFBProfile(com.restfb.types.User profile) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		boolean success = true;
		try {
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users_fb_profile WHERE fb_id=?");
			pstmt.setString(1, profile.getId());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				long userid = rs.getLong("user_id");
				if (userid != this.user_id)
					success = false;
			} else {				
				pstmt = conn.prepareStatement("INSERT INTO users_fb_profile (fb_id,user_id) VALUES(?,?)");
				pstmt.setString(1,profile.getId());
				pstmt.setLong(2, getUserId());
				pstmt.executeUpdate();
				System.err.println("new user inserted by a facebook account");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (success) {
			this.userFBProfile = profile;
		}
		
		return success;
		
	}
	
	private void bindFBAccount() {
		Connection conn = ConnectionManager.getInstance().getConnection();
		boolean success = true;
		try {
			PreparedStatement pstmt = conn.prepareStatement("SELECT access_token, expiration FROM users_fb_profile WHERE user_id=?");
			pstmt.setLong(1, user_id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String accesstoken = rs.getString(1);
				Timestamp t = rs.getTimestamp(2);
				if (t.getTime() > Calendar.getInstance().getTimeInMillis() + 1000 * 3600) {
					FBAccount account = FBAccount.getFromAccessToken(accesstoken);
					this.fbaccount = account;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean bindFBAccount(FBAccount account) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		boolean success = true;
		try {
			
			if (this.fbaccount == null) {
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users_fb_profile WHERE fb_id=?");
				pstmt.setString(1, account.getProfile().getId());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					long userid = rs.getLong("user_id");
					if (userid != this.user_id)
						success = false;
				} else {				
					pstmt = conn.prepareStatement("INSERT INTO users_fb_profile (fb_id,user_id) VALUES(?,?)");
					pstmt.setString(1, account.getProfile().getId());
					pstmt.setLong(2, getUserId());
					pstmt.executeUpdate();
					System.err.println("new user inserted by a facebook account");
				}
			} else if (this.fbaccount.getProfile().getId() != account.getProfile().getId())
				success = false;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (success) {
			this.fbaccount = account;
		}
		
		return success;
		
	}
	
	public void updateFBProfileAccessExp(String accesstoken, Calendar expiration) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		try {
			PreparedStatement pstmt = conn.prepareStatement("UPDATE users_fb_profile SET access_token=?, " +
					"expiration=? WHERE fb_id=?");
			pstmt.setString(1, accesstoken);
			pstmt.setTimestamp(2, new java.sql.Timestamp(expiration.getTimeInMillis()));
			pstmt.setString(3, userFBProfile.getId());
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	static public User getUserFromFBAccount(FBAccount account, HttpSession session) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		User user = null; 
		try {
			conn.setAutoCommit(false);
			PreparedStatement pstmt = conn.prepareStatement("SELECT user_id FROM users_fb_profile WHERE fb_id=?");
			pstmt.setString(1, account.getProfile().getId());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				user = User.getUserByID(rs.getInt(1));
			} else {
				user = User.getUserByEmail(account.getProfile().getEmail());
				if (user == null) {
					user = new User();
					user.nick_name = account.getProfile().getName();
					user.first_name = account.getProfile().getFirstName();
					user.last_name = account.getProfile().getLastName();
					user.email = account.getProfile().getEmail();
					user.activated = true;
					user.last_access_date = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
					user.password = "0";
					user.user_priviledge_id = 1;
					user.type = FB_TYPE_USER;
					
					User.insertUser(user);
				}
			}
			user.bindFBAccount(account);
			session.setAttribute(CURRENT_USER_ATTRIBUTE, user);
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		} finally {
			try {
				conn.setAutoCommit(true);
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return user;
	}
	
	static public User getUserFromFBProfile(com.restfb.types.User profile, HttpSession session) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		User user = null; 
		try {
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users_fb_profile WHERE fb_id=?");
			pstmt.setString(1, profile.getId());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				user = User.getUserByID(rs.getInt(2));
			} else {
				user = User.getUserByEmail(profile.getEmail());
				if (user == null) {
					user = new User();
					user.nick_name = profile.getName();
					user.first_name = profile.getFirstName();
					user.last_name = profile.getLastName();
					user.email = profile.getEmail();
					user.activated = true;
					user.last_access_date = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
					user.password = "0";
					user.user_priviledge_id = 1;
					
					User.insertUser(user);
				}
			}
			user.setUserFBProfile(profile);
			session.setAttribute(CURRENT_USER_ATTRIBUTE, user);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return user;
	}
	
	public FBAccount getFBAccount() {
		return fbaccount;
	}
	
}
