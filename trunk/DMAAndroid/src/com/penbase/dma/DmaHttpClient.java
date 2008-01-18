package com.penbase.dma;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
/**
 * 
 * @author Bort Jean
 *
 */
public class DmaHttpClient {
	private URL url;
	private int lastError = 0;
	/**
	 * 
	 */
	public DmaHttpClient() {
		try {
			url = new URL("http://www.dalyo.com/server/com.penbase.arbiter.Arbiter");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @return
	 */
	public int GetLastError() {
		return lastError;
	}
	/**
	 * 
	 * @param login
	 * @param password
	 * @return
	 */
	public String Authentication(String login, String password) {
		return SendPost("act=login&login=" + login + "&passwd=" + password);
	}
	/**
	 * 
	 * @param parameters
	 * @return
	 */
	private String SendPost(String parameters) {
		String response = null;
		try {			
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);

			// Get the output stream and write the parameters
			PrintStream out = new PrintStream(connection.getOutputStream());
			out.print(parameters);
			out.close();
			// Get the input stream and read response
			InputStream in =  connection.getInputStream();
			StringBuffer sb = new StringBuffer();
		    int c;
		    while ((c = in.read()) != -1) 
		    	sb.append((char) c);
			in.close();
			response = sb.toString();
			lastError = GetResponseCode(response);
			if (lastError != 200) {
				return null;
			}
		} catch (ProtocolException pe) {
			System.err.println("HTTPExample: ProtocolException; "
					+ pe.getMessage());
		} catch (IOException ioe) {
			System.err.println("HTTPExample: IOException; " + ioe.getMessage());
		}
		return response;
	}
	private int GetResponseCode(String response) {
		//Log.d("Dalyo", response);
		return Integer.parseInt(response.substring(0, response.indexOf('\n')));
	}
}
