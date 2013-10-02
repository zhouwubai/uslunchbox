package edu.fiu.cs.tomcatcollector;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class HttpResponseLogWrapper extends HttpServletResponseWrapper {
	
	int httpStatus;
	
	PrintWriter pw = null;
	
	StringWriter sw = null;
	
	HttpServletResponse response;

	public HttpResponseLogWrapper(HttpServletResponse response) throws IOException {
		super(response);
		// TODO Auto-generated constructor stub
		this.response = response;
	}
	
	@Override
    public void sendError(int sc) throws IOException {
        httpStatus = sc;
        super.sendError(sc);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        httpStatus = sc;
        super.sendError(sc, msg);
    }

    @Override
    public void setStatus(int sc) {
        httpStatus = sc;
        super.setStatus(sc);
    }

    public int getStatus() {
        return httpStatus;
    }
    
	public PrintWriter getWriter() throws IOException {
		if (pw == null) {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
		}
		return pw;
	}
    
    public String getWriterBuffer() {
    	if (pw == null) {
    		return "";
    	}
    	else {
    		return sw.toString();
    	}
    }
    
    public void flushWrapperBuffer() throws IOException {
    	if (pw != null) {
    		this.response.getWriter().write(getWriterBuffer());
    	}
    }
    
}
