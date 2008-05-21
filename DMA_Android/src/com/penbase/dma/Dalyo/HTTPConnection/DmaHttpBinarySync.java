package com.penbase.dma.Dalyo.HTTPConnection;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;

import com.penbase.dma.Dma;

public class DmaHttpBinarySync extends Thread {
	private String parameters;
	private HttpURLConnection connection;
	private byte[] inputbytes;
	private URL urlByte;
	private byte[] result;	
	private DmaHttpClient client;
	
	public DmaHttpBinarySync(DmaHttpClient client, String params, byte[] inputbytes)
	{
		this.client = client;
		this.parameters = params;
		this.inputbytes = inputbytes;
		
		try
		{
			urlByte = new URL("http://192.168.0.1/server/com.penbase.arbiter.Arbiter?"+parameters);
			connection = (HttpURLConnection) urlByte.openConnection();
		}		
		catch (IOException ioe) 
		{System.err.println("HTTPExample: IOException; " + ioe.getMessage());}
	}
	
	public void createConnection()
	{
		try
		{
			String boundary = DmaHttpClient.getBoundary(); 
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write("\r\n".getBytes());
			bos.write("--".getBytes());
			bos.write(boundary.getBytes());
			bos.write("\r\n".getBytes());	
			bos.write("Content-Disposition: form-data; name=\"request\"; filename=\"bin\"".getBytes());
			bos.write("\r\n".getBytes());
			bos.write("Content-Type: application/octet-stream".getBytes());
			bos.write("\r\n".getBytes());
			bos.write("\r\n".getBytes());
			bos.write(inputbytes);				
			bos.write("\r\n".getBytes());
			bos.write("--".getBytes());
			bos.write(boundary.getBytes());
			bos.write("--".getBytes());
			bos.write("\r\n".getBytes());
			byte[] sbbytes = bos.toByteArray();

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("User-Agent", "Penbase x35 ANDROID " + Dma.getVersion()); //runtime's version
			connection.setRequestProperty("Content-Type", "multipart/form-data" + "; boundary=" + boundary);      
			connection.setRequestProperty("Content-Length", "" + sbbytes.length); 
			connection.setRequestProperty("Connection", "close");
			
			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());				
			dos.write(sbbytes);
			dos.flush();
			dos.close();					
		}
		catch (IOException ioe) 
		{System.err.println("HTTPExample: IOException; " + ioe.getMessage());}
	}
	
	public void run()
	{
		createConnection();
		try
		{
			InputStream in =  connection.getInputStream();
			int c;
			byte[] readByte = new byte[1024];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    while ((c = in.read(readByte)) > 0)
		    {
		    	bos.write(readByte, 0, c);
		    }
		    in.close();	
		    byte[] data = bos.toByteArray();
		    
		    String codeStr = "";
		    int i = 0;
		    while(i < data.length && data[i] != (int)'\n') 
		    {
		    	codeStr += (char)data[i];
		    	i++;
		    }		    
		    
		    int code = Integer.parseInt(codeStr);
		    if ((codeStr.length() > 0) && (Integer.parseInt(codeStr) == 200)) 
		    {
		    	
		    	int newLength = data.length-codeStr.length()-1;
			    result = new byte[newLength];
			    System.arraycopy(data, codeStr.length()+1, result, 0, result.length);			    	 
		    }
		    		    
		    client.fireImportEnded(code, result);
		}
		catch (IOException ioe) 
		{System.err.println("HTTPExample: IOException; " + ioe.getMessage());}		
	}	
}
