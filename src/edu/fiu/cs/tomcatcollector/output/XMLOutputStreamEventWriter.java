package edu.fiu.cs.tomcatcollector.output;


import java.io.FileOutputStream;
import java.io.OutputStream;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

public class XMLOutputStreamEventWriter {
	
	protected XMLWriter out;
	
	protected Element root = null;
	
	public XMLOutputStreamEventWriter(String fileName) throws IOException {
//		OutputFormat outputFormat = OutputFormat.createPrettyPrint();
//		outputFormat.setEncoding("UTF-8");
//		out = new XMLWriter(new FileOutputStream(fileName), outputFormat);
//		try {
//			out.startDocument();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			throw new IOException(e.toString());
//		}
		this(new FileOutputStream(fileName));
	}
	
	public XMLOutputStreamEventWriter(OutputStream outStream) throws IOException {
		OutputFormat outputFormat = OutputFormat.createPrettyPrint();
		outputFormat.setEncoding("UTF-8");
		out = new XMLWriter(outStream, outputFormat);
		try {
			out.startDocument();
			root = DocumentHelper.createElement("data");
			out.writeOpen(root);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException(e.toString());
		}
	}
	
	public void write(Map<String,String> event) throws IOException {
		// TODO Auto-generated method stub
		write(toXML(event));
	}
	
	public void write(Element elem) throws IOException {
		// TODO Auto-generated method stub
		out.write(elem);
	}
	
	public void close() throws IOException {
		// TODO Auto-generated method stub
		try {
			out.writeClose(root);
			out.endDocument();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException(e.toString());
		}
		out.close();
	}
	
	public void flush() throws IOException {
		out.flush();
	}
	
	public static Element createElement() {
		return DocumentHelper.createElement("event");
	}

	
	private static Element toXML(Map<String,String> event) {
		Element elem = createElement();
		for (String attrName: event.keySet()) {
			Element attrElem = elem.addElement(attrName);
			String value = event.get(attrName);
			if (value == null) {
				throw new Error("Attribute "+attrName+" is null, cannot export to XML");
			}
			attrElem.setText(value);
		}
		return elem;
	}
	

}
