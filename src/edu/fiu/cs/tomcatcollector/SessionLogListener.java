package edu.fiu.cs.tomcatcollector;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Application Lifecycle Listener implementation class SessionLogListener
 *
 */
@WebListener
public class SessionLogListener implements HttpSessionListener {
	
	private static volatile int activeSessionCount = 0;
	
	private SimpleDateFormat timestampSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    /**
     * Default constructor. 
     */
    public SessionLogListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent arg0) {
        // TODO Auto-generated method stub    	
    	System.out.println(timestampSDF.format(new Date())+" added a new session");
    	activeSessionCount++;
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent arg0) {
        // TODO Auto-generated method stub
    	if (activeSessionCount > 0) {
    		activeSessionCount--;
    		System.out.println(timestampSDF.format(new Date())+" destroyed a new session");
    	}
    }
    
    public static int getActiveSessionCount() {
    	return activeSessionCount;
    }
	
}
