package com.penbase.dma.Dalyo.HTTPConnection;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.util.Log;

import com.penbase.dma.Dma;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.ErrorCode;
import com.penbase.dma.View.ApplicationListView;
import com.penbase.dma.View.ApplicationView;

public class DmaHttpBinarySync {
	private String requestAction;
	private String responseAction;
	private String blobAction;
	private byte[] inputbytes;
	private String urlString;
	private String cookie = null;
	private String syncType;
	
	public DmaHttpBinarySync(String url, String request, String blob, String response, byte[] inputbytes, String syncType) {
		this.requestAction = request;
		this.blobAction = blob;
		this.responseAction = response;
		this.inputbytes = inputbytes;
		this.urlString = url;
		this.syncType = syncType;
	}
	
	private byte[] createConnection(String action, byte[] bytes) {
		byte[] result = null;
		try{
			URL newUrl = new URL(urlString+"?"+action);
			HttpURLConnection connection = (HttpURLConnection) newUrl.openConnection();
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
			if (bytes != null) {
				bos.write(bytes);
			}
			bos.write("\r\n".getBytes());
			bos.write("--".getBytes());
			bos.write(boundary.getBytes());
			bos.write("--".getBytes());
			bos.write("\r\n".getBytes());
			byte[] sbbytes = bos.toByteArray();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("User-Agent", "Penbase x35 ANDROID " + Dma.getVersion()); //runtime's version
			connection.setRequestProperty("Content-Type", "multipart/form-data" + "; boundary=" + boundary);
			connection.setRequestProperty("Content-Length", "" + sbbytes.length);
			connection.setRequestProperty("Connection", "close");
			if (cookie != null) {
				connection.setRequestProperty("Cookie", cookie);
			}
			connection.setRequestMethod("POST");
			connection.connect();
			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
			dos.write(sbbytes);
			dos.flush();
			dos.close();
			result = getData(connection);
		}
		catch (IOException ioe) {
			Log.i("info", "HTTPExample: IOException; " + ioe.getMessage());}
		return result;
	}
	
	public boolean run() {
		boolean wellDone = false;
		byte[] data = createConnection(requestAction, inputbytes);
		String codeStr = getErrorCode(data);
		Log.i("info", "code of action "+requestAction+" : "+Integer.valueOf(codeStr));
		if ((Integer.valueOf(codeStr) == ErrorCode.OK) || (Integer.valueOf(codeStr) == ErrorCode.CONTINUE)) {
			int newLength = data.length - codeStr.length() - 1;
			byte[] result = new byte[newLength];
			System.arraycopy(data, codeStr.length()+1, result, 0, result.length);
			if (syncType.equals("Import")) {
				DatabaseAdapter.beginTransaction();
				byte[] returnByte = ApplicationView.getDataBase().syncImportTable(result);
				
				//Check if there is blob data
				ArrayList<ArrayList<Object>> blobs = DatabaseAdapter.getBlobRecords();
				int blobNb = blobs.size();
				if (blobNb > 0) {
					for(int i=0; i<blobNb; i++) {
						ArrayList<Object> blob = blobs.get(i);
						String getBlobAction = blobAction;
						getBlobAction += "&table="+blob.get(0);
						getBlobAction += "&fieldid="+blob.get(1);
						Log.i("info", "blob.get(2).toString() "+blob.get(2).toString());
						String recordId = blob.get(2).toString().split("_")[4].split("\\.")[0];
						getBlobAction += "&record="+recordId;
						
						byte[] responsedata = createConnection(getBlobAction, null);
						String codeResponseStr = getErrorCode(responsedata);
						Log.i("info", "responsea "+getBlobAction+" code "+codeResponseStr);
						
						if (Integer.valueOf(codeResponseStr) == ErrorCode.OK) {
							//Save picture
							int blobDataLength = responsedata.length - codeResponseStr.length() - 1;
							byte[] blobData = new byte[blobDataLength];
							System.arraycopy(responsedata, codeResponseStr.length()+1, blobData, 0, blobData.length);
							ApplicationView.getDataBase().saveBlobData(blobData, i);
						}
					}
				}
				
				//Get response
				byte[] responsedata = createConnection(responseAction, returnByte);
				String codeReportStr = getErrorCode(responsedata);
				Log.i("info", "report code "+Integer.valueOf(codeReportStr));
				if (Integer.valueOf(codeReportStr) == ErrorCode.OK) {
					DatabaseAdapter.commitTransaction();
					wellDone = true;
				}
				else{
					Log.i("info", "cancel transaction");
					DatabaseAdapter.rollbackTransaction();
				}
				
				if (Integer.valueOf(codeStr) == ErrorCode.CONTINUE) {
					Log.i("info", "continue import action");
					//continue to receive
					wellDone = new DmaHttpBinarySync(urlString, requestAction, blobAction, responseAction, inputbytes, "Import").run();
				}
			}
			else if (syncType.equals("Export")) {
				//check if there is blob data to send
				ArrayList<ArrayList<Object>> blobs = DatabaseAdapter.getBlobRecords();
				int blobNb = blobs.size();
				if (blobNb > 0) {
					for (ArrayList<Object> blob : blobs) {
						String sendAction = blobAction;
						sendAction += "&fieldid="+blob.get(0);
						sendAction += "&blob="+blob.get(1);
						sendAction += "&format=jpg";
						File image = new File(Constant.PACKAGENAME+ApplicationListView.getApplicationName()+"/"+blob.get(1));
						byte[] responsedata = createConnection(sendAction, this.getBytesFromFile(image));
						String codeResponseStr = getErrorCode(responsedata);
						Log.i("info", "responsea "+sendAction+" code "+codeResponseStr);
					}
				}
				DatabaseAdapter.updateIds(result);
				
				byte[] responsedata = createConnection(responseAction, null);
				String codeResponseStr = getErrorCode(responsedata);
				Log.i("info", "responsea "+responseAction+" code "+codeResponseStr);
				if (Integer.valueOf(codeResponseStr) == ErrorCode.OK) {
					//DatabaseAdapter.cleanTables();
					wellDone = true;
				}
				
				if (Integer.valueOf(codeStr) == ErrorCode.CONTINUE) {
					//continue to receive
					wellDone = new DmaHttpBinarySync(urlString, requestAction, blobAction, responseAction, result, "EXport").run();
				}
			}
		}
		return wellDone;
	}
	
	private byte[] getBytesFromFile(File file) {
        InputStream is;
        byte[] bytes = null;
		try {
			is = new FileInputStream(file);
		       long length = file.length();
		       
		        // Create the byte array to hold the data
		        bytes = new byte[(int)length];
		    
		        // Read in the bytes
		        int offset = 0;
		        int numRead = 0;
				while (offset < bytes.length
					       && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
					    offset += numRead;
				}
		    
		        // Ensure all the bytes have been read in
		        if (offset < bytes.length) {
		            throw new IOException("Could not completely read file "+file.getName());
		        }
		        // Close the input stream and return bytes
		        is.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
        return bytes;
    }
	
	private byte[] getData(HttpURLConnection connection) {
		if (cookie == null) {
			cookie = connection.getHeaderField("Set-Cookie").split(";")[0];
		}
		InputStream in;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			in = connection.getInputStream();
			int c;
			byte[] readByte = new byte[1024];
			while ((c = in.read(readByte)) > 0) {
				bos.write(readByte, 0, c);
			}
			in.close();
			connection.disconnect();
		} catch (IOException e) {
			Log.i("info", "ioe in getdata "+e.getMessage());
		}
		return bos.toByteArray();
	}
	
	private String getErrorCode(byte[] data) {
		String codeStr = "";
		int i = 0;
		while(i < data.length && data[i] != (int)'\n') {
			codeStr += (char)data[i];
			i++;
		}
		return codeStr;
	}
}
