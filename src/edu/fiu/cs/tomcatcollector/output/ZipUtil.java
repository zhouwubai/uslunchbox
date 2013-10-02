package edu.fiu.cs.tomcatcollector.output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	
	
	public static void createZipFile(String inputFileName, String zipFileName) throws IOException {
		// Create a buffer for reading the files
		byte[] buf = new byte[8*1024];

		// Create the ZIP file
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));

		// Compress the files
		FileInputStream in = new FileInputStream(inputFileName);
		
		File inFile = new File(inputFileName);

		// Add ZIP entry to output stream.
		out.putNextEntry(new ZipEntry(inFile.getName()));

		// Transfer bytes from the file to the ZIP file
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}

		// Complete the entry
		out.closeEntry();
		in.close();

		// Complete the ZIP file
		out.close();
	}
	
	public static void replaceByZipFileConcurrent(String inputFileName, String zipFileName)  {
		Thread zipThread = new Thread(new ZipReplaceTask(inputFileName, zipFileName));
		zipThread.start();
	}
	
	private static class ZipReplaceTask implements Runnable {
		
		String inputFileName;
		String zipFileName;
		
		public ZipReplaceTask(String inputFileName, String zipFileName) {
			this.inputFileName = inputFileName;
			this.zipFileName = zipFileName;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				// create zip file
				System.out.println("start zipping the file : "+inputFileName);
				createZipFile(inputFileName, zipFileName);
				System.out.println("finished zipping the file : "+inputFileName);
				
				// Delete the current raw log file
				File f = new File(inputFileName);
				boolean success = f.delete();
				if (!success) {
					System.out.println("Cannot delete the log file : "+inputFileName);
				}
				else {
					System.out.println("deleted the file : "+inputFileName);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	

}
