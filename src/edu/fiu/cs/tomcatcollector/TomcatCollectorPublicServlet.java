package edu.fiu.cs.tomcatcollector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;

import edu.fiu.cs.tomcatcollector.output.JSONOutputStreamEventWriter;
import edu.fiu.cs.tomcatcollector.output.XMLEventToJSONEvent;
import edu.fiu.cs.tomcatcollector.output.XMLOutputStreamEventWriter;

/**
 * Servlet implementation class TomcatCollectorPublicServlet
 */
@WebServlet("/TomcatCollectorPublicServlet")
public class TomcatCollectorPublicServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TomcatCollectorPublicServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String accessKey = request.getParameter("accesskey");
		String kStr = request.getParameter("k");
		int k=1;
		if (kStr != null) {
			k = Integer.parseInt(kStr);
		}
		
		TomcatCollector collector = TomcatCollector.getTomcatCollector(request.getServletContext());	
		if (collector != null && accessKey != null && accessKey.equals(collector.getAccessKey())) {
			List<Element> events = collector.getRecentEvents(k);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			JSONOutputStreamEventWriter writer = new JSONOutputStreamEventWriter(bos);			
			for (Element event: events) {
				writer.write(XMLEventToJSONEvent.toJSONObject(event));
			}
			writer.close();
			response.getWriter().write(bos.toString());
		}
	}

	
}
