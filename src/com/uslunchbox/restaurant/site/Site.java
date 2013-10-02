package com.uslunchbox.restaurant.site;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.uslunchbox.restaurant.database.ConnectionManager;

/**
 * 
 * @author Liang Tang
 *
 */
public class Site {
	
	int siteId;
	String area;
	Set<Integer> restaurantIds = new HashSet<Integer>();
	Set<DeliverInfo> deliverInfos = new HashSet<DeliverInfo>();
	int defaultDeliverInfoIndex = -1;
	
	static Map<Integer, Site> siteMaps = new HashMap<Integer, Site>();
	
	public Site() {
		
	}
	
	public void addRestaurantId(int restaurant_id) {
		this.restaurantIds.add(restaurant_id);
	}
	
	public void addDeliverLocation(String loc, Time deliver_time, Time order_deadline, String map_image_url, String map_click_url, boolean isDefault) {
		this.deliverInfos.add(new DeliverInfo(loc, deliver_time, order_deadline, map_image_url, map_click_url));
		if (isDefault) {
			this.defaultDeliverInfoIndex = deliverInfos.size()-1;
		}
	}
	
	public int getSiteId() {
		return siteId;
	}
	
	public String getArea() {
		return area;
	}
	
	public Set<Integer> getRestaurantIds() {
		return restaurantIds;
	}
	
	public Set<DeliverInfo> getDeliverInfos() {
		return deliverInfos;
	}
	
	public int getDefaultDeliverInfoIndex() {
		return defaultDeliverInfoIndex;
	}
	
	public static Site getSiteFromSession(HttpSession session) {
		return (Site) session.getAttribute("site");		
	}
	
	public static void saveSiteToSession(HttpSession session, Site site) {
		session.setAttribute("site", site);
	}
	
	/**
	 * Get all active sites, the key is the site id, the value is the site object
	 * @return
	 */
	public static Map<Integer, Site> getActiveSites() {
		synchronized (siteMaps) {
			if (siteMaps.isEmpty()) {

				Connection conn = ConnectionManager.getInstance().getConnection();
				try {
					PreparedStatement pstmt = conn
							.prepareStatement("SELECT s.site_id, s.area, r.restaurant_id, d.deliver_location, d.deliver_time, d.order_deadline, d.default, "
									+ "d.map_image_url, d.map_click_url " 
									+ "FROM sites s, site_restaurants r,site_deliver_locations d "
									+ "WHERE s.site_id = r.site_id AND s.site_id = d.site_id AND s.status='on' AND d.status='on' ");
					ResultSet rets = pstmt.executeQuery();
					while (rets.next()) {
						int siteId = rets.getInt("site_id");
						String area = rets.getString("area");
						int restaurant_id = rets.getInt("restaurant_id");
						String delivery_location = rets.getString("deliver_location");
						Time delivery_time = rets.getTime("deliver_time");
						Time order_deadline = rets.getTime("order_deadline");
						String map_image_url = rets.getString("map_image_url");
						String map_click_url = rets.getString("map_click_url");
						int isDefaultOption = rets.getInt("default");
						Site site = siteMaps.get(siteId);
						if (site == null) {
							site = new Site();
							site.siteId = siteId;
							site.area = area;
							siteMaps.put(siteId, site);
						}
						site.addDeliverLocation(delivery_location, delivery_time, order_deadline, map_image_url, map_click_url, isDefaultOption==1);
						site.addRestaurantId(restaurant_id);
					}
					rets.close();
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

		}
		return siteMaps;
	}
	
	/**
	 * added by lei
	 * get deliver locations by site
	 * @param siteId
	 * @return
	 */
	public static List<String> getDeliverLocationsBySite(int siteId){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet ret = null;
		List<String> locations = new ArrayList<String>();
		try {
			pstmt = conn.prepareStatement("select deliver_location from site_deliver_locations" +
					" where site_id = ? and status = 'on'");
			pstmt.setLong(1, siteId);
			ret = pstmt.executeQuery();
			while (ret.next()) {
				locations.add(ret.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (ret != null)
					ret.close();
				conn.close();
			} catch (SQLException e) {
				// e.printStackTrace();
			}
		}
		return locations;
	}
	
	/**
	 * added by lei
	 * get deliver locations by site
	 * @param siteId
	 * @return
	 */
	public static List<String> getDeliverTimeBySiteAndLocation(int siteId, String location){
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet ret = null;
		List<String> times = new ArrayList<String>();
		try {
			pstmt = conn.prepareStatement("select deliver_time, map_click_url from site_deliver_locations" +
					" where site_id = ? and deliver_location = ? and status = 'on'");
			pstmt.setLong(1, siteId);
			pstmt.setString(2, location);
			ret = pstmt.executeQuery();
			while (ret.next()) {
				times.add(ret.getString(1) + "," + ret.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (ret != null)
					ret.close();
				conn.close();
			} catch (SQLException e) {
				// e.printStackTrace();
			}
		}
		return times;
	}
	
	/**
	 * 
	 * @author Liang
	 *
	 */
	public static class DeliverInfo {
		public String location;
		public Time deliver_time;
		public Time order_deadline;
		public String map_image_url;
		public String map_click_url;
		
		public DeliverInfo(String loc, Time deliver_time, Time order_deadline, String map_image_url, String map_click_url) {
			this.location = loc;
			this.deliver_time = deliver_time;
			this.order_deadline = order_deadline;
			this.map_image_url = map_image_url;
			this.map_click_url = map_click_url;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((deliver_time == null) ? 0 : deliver_time.hashCode());
			result = prime * result + ((location == null) ? 0 : location.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DeliverInfo other = (DeliverInfo) obj;
			if (deliver_time == null) {
				if (other.deliver_time != null)
					return false;
			} else if (!deliver_time.equals(other.deliver_time))
				return false;
			if (location == null) {
				if (other.location != null)
					return false;
			} else if (!location.equals(other.location))
				return false;
			return true;
		}
		
		
		
		
	}

}
