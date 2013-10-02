package com.uslunchbox.restaurant.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.uslunchbox.restaurant.database.ConnectionManager;

public class Card {
	
	private int cardNo;
	private int typeId;
	private Timestamp issueDate;
	private Timestamp expireDate;
	private Timestamp activationDate;
	private int userId;
	private String activated;
	private String delivered;
	private int credits;
	
	private static final String QUERY_MAX_CARDNO = "SELECT max(card_no) from cards";
	private static final String INSERT_CARD = "insert into cards values (?,?,?,?,?,?,?,?,?)";
	
	public Card(){
		
	}
	
	public static int activateCardForUser(User u){
		int max = getMaxCardNo();
		
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(INSERT_CARD);
			pstmt.setInt(1, max+1);
			pstmt.setInt(2, 1);//default
			pstmt.setTimestamp(3, new Timestamp((new Date().getTime())));
			pstmt.setTimestamp(4, new Timestamp((new Date().getTime())));
			pstmt.setTimestamp(5, new Timestamp((new Date().getTime())));
			pstmt.setLong(6, u.getUserId());
			pstmt.setString(7, "activated");
			pstmt.setString(8, "no");
			pstmt.setInt(9, 0);
			int success = pstmt.executeUpdate();
			
			if (success > 0) {
				max = max + 1;
			}else{
				max = 0;
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
		return max;
	}
	
	
	private static int getMaxCardNo(){
		Connection conn = ConnectionManager.getInstance().getConnection();
		int max = 0;

		PreparedStatement pstmt = null;
		ResultSet retCard = null;
		try {
			pstmt = conn.prepareStatement(QUERY_MAX_CARDNO);
			retCard = pstmt.executeQuery();
			if (retCard.next()) {
				max = retCard.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				retCard.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return max;
	}
	
	public int getCardNo() {
		return cardNo;
	}
	public void setCardNo(int cardNo) {
		this.cardNo = cardNo;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public Timestamp getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Timestamp issueDate) {
		this.issueDate = issueDate;
	}
	public Timestamp getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Timestamp expireDate) {
		this.expireDate = expireDate;
	}
	public Timestamp getActivationDate() {
		return activationDate;
	}
	public void setActivationDate(Timestamp activationDate) {
		this.activationDate = activationDate;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getActivated() {
		return activated;
	}
	public void setActivated(String activated) {
		this.activated = activated;
	}
	public String getDelivered() {
		return delivered;
	}
	public void setDelivered(String delivered) {
		this.delivered = delivered;
	}
	public int getCredits() {
		return credits;
	}
	public void setCredits(int credits) {
		this.credits = credits;
	}

}
