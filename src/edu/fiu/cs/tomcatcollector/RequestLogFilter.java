package edu.fiu.cs.tomcatcollector;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.io.PrintStream;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.dom4j.Element;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.mbeans.MBeanUtils;
import org.apache.catalina.util.LifecycleSupport;
import org.apache.catalina.valves.Constants;
import org.apache.catalina.valves.ValveBase;

import com.uslunchbox.restaurant.utils.HostURL;


/**
 * Servlet Filter implementation class RequestLogFilter
 */
@WebFilter(filterName="/RequestLogFilter", urlPatterns={"/*"})
public class RequestLogFilter implements Filter {
	
	private SimpleDateFormat timestampSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
	
	private TomcatCollector collector = null;

    /**
     * Default constructor. 
     */
    public RequestLogFilter() {
        // TODO Auto-generated constructor stub
    }
    
    /**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		
		collector = new TomcatCollector();
		collector.init(fConfig.getServletContext());
		
		System.out.println("init filter");

	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		if (collector != null) {
			collector.destroy();
		}
	}
	
	
	
	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		collector = TomcatCollector.getTomcatCollector(request.getServletContext());
		if (collector.isDisabled() || collector.isInternalServletRequest(request)) {
			chain.doFilter(request, response);
			return;
		}
		
		
		// place your code here
		Element event = collector.createEvent("SerlvetRequest");
		fillRequestInfo(event, request);
		ServletResponse responseWrapper = response; 
		
		if (response instanceof HttpServletResponse) {
			responseWrapper = new HttpResponseLogWrapper((HttpServletResponse)response);
		}
		
		Throwable internalException = null;
		try{
			// pass the request along the filter chain
			chain.doFilter(request, responseWrapper);			
		}catch(Throwable e) {
			response.resetBuffer();
			response.getWriter().write("An error is encountered, please refresh the page.");			
			internalException = e;
		}
		fillResponseInfo(event, responseWrapper);
		if (responseWrapper instanceof HttpResponseLogWrapper && internalException == null) {
			((HttpResponseLogWrapper)responseWrapper).flushWrapperBuffer();
		}
		collector.writeEvent(event);
		
		if (internalException != null) {
			writeExceptionEvent(internalException);	
			internalException.printStackTrace();
			throw new ServletException(internalException);
		}
	}
	
	private void fillRequestInfo(Element event, ServletRequest request) throws IOException {
		event.addElement("remote_host").setText(request.getRemoteHost());
		event.addElement("remote_addr").setText(request.getRemoteAddr());
		event.addElement("remote_port").setText(request.getRemotePort()+"");
		event.addElement("session_count").setText(SessionLogListener.getActiveSessionCount()+"");
		if (request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest)request; 
			event.addElement("method").setText(req.getMethod());
			event.addElement("uri").setText(req.getRequestURI());
			String sessionId = req.getRequestedSessionId();
			if (sessionId == null) {
				sessionId = "";
			}
			event.addElement("session_id").setText(sessionId);
		}
		Element paramsElem = event.addElement("parameters");
		for (String param: request.getParameterMap().keySet()) {
			Element paramElem = paramsElem.addElement("parameter");
			paramElem.addAttribute("name", param);
			String[] values = request.getParameterValues(param);
			for (int i=0; i<values.length; i++) {
				paramElem.addElement("value").setText(values[i]);
			}
		}
	}
	
	private void fillResponseInfo(Element event, ServletResponse response) throws IOException {
		event.addElement("response_timestamp").setText(timestampSDF.format(new Date()));
		if (response instanceof HttpResponseLogWrapper) {
			HttpResponseLogWrapper responseWrapper = (HttpResponseLogWrapper)response;
			event.addElement("response_http_status").setText(responseWrapper.getStatus()+"");
			event.addElement("response_output").setText(responseWrapper.getWriterBuffer());
		}
	}
	
	private void writeExceptionEvent(Throwable e) throws IOException {
		Element event = collector.createEvent("Exception");
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		event.addElement("msg").setText(sw.toString());
		collector.writeEvent(event);
	}
	
	

}
