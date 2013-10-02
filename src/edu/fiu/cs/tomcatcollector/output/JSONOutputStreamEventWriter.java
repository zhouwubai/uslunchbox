package edu.fiu.cs.tomcatcollector.output;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONOutputStreamEventWriter {
	
	private BufferedWriter writer;
	
	private boolean bFirstEvent = true;
	
	
	public JSONOutputStreamEventWriter(OutputStream out) throws IOException {		
		this.writer = new BufferedWriter(new OutputStreamWriter(out));
		this.writer.append("[\n");
		bFirstEvent = true;
	}
	
	public JSONOutputStreamEventWriter(String jsonFileName) throws IOException {
		File f = new File(jsonFileName);
		if (f.exists()) {
			// delete the last line and set appending mode 
			RandomAccessFile rf = new RandomAccessFile(f, "rw");
			rf.setLength(rf.length() - 2); // Remove "\n]" at the end of the file
			rf.close();
			this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFileName, true)));
			bFirstEvent = false;
		}
		else {
			this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFileName)));
			this.writer.append("[\n");
			bFirstEvent = true;
		}
	}
	
	public void write(JSONObject event) throws IOException{
		if (!bFirstEvent) {
			this.writer.append(",\n");			
		}
		try {
			this.writer.append(event.toString(2));
		}catch(JSONException e) {
			throw new IOException(e);
		}
		bFirstEvent = false;

	}
	
	public void close() throws IOException {
		this.writer.append("\n]");
		this.writer.close();
	}
	
	public static void main(String[] args) throws JSONException, IOException {
		JSONObject o1 = new JSONObject();
		o1.put("sdfsdf", "1111");
		o1.put("ff", "44444");
		System.out.println(o1.toString());
		JSONObject o2 = new JSONObject();
		o2.put("sdfsdf", "222");
		o2.put("ff", "88888");
		
		JSONOutputStreamEventWriter writer = new JSONOutputStreamEventWriter("test.json");
		writer.write(o1);
		writer.write(o2);
		writer.close();
		
	}

}
