package com.penbase.dma.Dalyo.HTTPConnection;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;
import com.penbase.dma.Dma;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Constant.ErrorCode;
import com.penbase.dma.View.ApplicationView;

public class DmaHttpBinarySync {
	private String requestAction;
	private String responseAction;
	private byte[] inputbytes;
	private String urlString;
	private String cookie = null;
	private String syncType;
	
	public DmaHttpBinarySync(URL url, String request, String response, byte[] inputbytes, String syncType){
		this.requestAction = request;
		this.responseAction = response;
		this.inputbytes = inputbytes;
		this.urlString = url.toString();
		this.syncType = syncType;
		run();
	}
	
	private byte[] createConnection(String action, byte[] bytes){
		byte[] result = null;
		try{
			URL urlByte = new URL(urlString+"?"+action);
			HttpURLConnection connection = (HttpURLConnection) urlByte.openConnection();
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
			if (bytes != null){
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
			if (cookie != null){
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
		catch (IOException ioe){
			Log.i("info", "HTTPExample: IOException; " + ioe.getMessage());}
		return result;
	}
	
	private void run(){
		byte[] data = createConnection(requestAction, inputbytes);
		String codeStr = getErrorCode(data);
		int code = Integer.valueOf(codeStr);
		Log.i("info", "code of action "+requestAction+" : "+code);
		if (code == ErrorCode.OK){
			DatabaseAdapter.startTransaction();
			if (syncType.equals("Import")){
				int newLength = data.length-codeStr.length()-1;
				byte[] result = new byte[newLength];
				System.arraycopy(data, codeStr.length()+1, result, 0, result.length);
				byte[] returnByte = ApplicationView.getDataBase().syncImportTable(result);
				byte[] responsedata = createConnection(responseAction, returnByte);
				String codeReportStr = getErrorCode(responsedata);
				if (Integer.valueOf(codeReportStr) == ErrorCode.OK){
					Log.i("info", "report "+Integer.valueOf(codeReportStr));
					DatabaseAdapter.validateTransaction();
				}
				else{
					DatabaseAdapter.cancelTransaction();
				}
			}
			else if (syncType.equals("Export")){
				byte[] responsedata = createConnection(responseAction, null);
				String codeResponseStr = getErrorCode(responsedata);
				if (Integer.valueOf(codeResponseStr) == ErrorCode.OK){
					DatabaseAdapter.clearRecordsOperated();
				}
			}
		}
	}
	
	private byte[] getData(HttpURLConnection connection) {
		if (cookie == null){
			cookie = connection.getHeaderField("Set-Cookie").split(";")[0];
		}
		InputStream in;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			in = connection.getInputStream();
			int c;
			byte[] readByte = new byte[1024];
			while ((c = in.read(readByte)) > 0){
				bos.write(readByte, 0, c);
			}
			in.close();
			connection.disconnect();
		} catch (IOException e) {
			Log.i("info", "ioe in getdata "+e.getMessage());
		}
		return bos.toByteArray();
	}
	
	private String getErrorCode(byte[] data){
		String codeStr = "";
		int i = 0;
		while(i < data.length && data[i] != (int)'\n'){
			codeStr += (char)data[i];
			i++;
		}
		return codeStr;
	}
}
