package com.penbase.dma.Dalyo.HTTPConnection;

import com.penbase.dma.Dma;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.ErrorCode;
import com.penbase.dma.View.ApplicationView;

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

/**
 * Manages synchronization HTTP connection
 */
public class DmaHttpBinarySync {
	private String mRequestAction;
	private String mResponseAction;
	private String mBlobAction;
	private byte[] mInputbytes;
	private String mCookie = null;
	private String mSyncType;
	
	public DmaHttpBinarySync(String request, String blob, String response, byte[] inputbytes, String syncType) {
		this.mRequestAction = request;
		this.mBlobAction = blob;
		this.mResponseAction = response;
		this.mInputbytes = inputbytes;
		this.mSyncType = syncType;
	}
	
	/**
	 * Creates HTTP connection
	 * @param action Connection parameters
	 * @param bytes Data transfered in HTTP connection
	 * @return Server's response
	 */
	private byte[] createConnection(String action, byte[] bytes) {
		byte[] result = null;
		try{
			URL newUrl = new URL(Constant.SERVER+action);
			HttpURLConnection connection = (HttpURLConnection) newUrl.openConnection();
			String boundary = getBoundary();
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
			if (mCookie != null) {
				connection.setRequestProperty("Cookie", mCookie);
			}
			connection.setRequestMethod("POST");
			connection.connect();
			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
			dos.write(sbbytes);
			dos.flush();
			dos.close();
			result = getData(connection);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Treats data sent from server
	 * @return If well done
	 */
	public boolean run() {
		boolean wellDone = false;
		byte[] data = createConnection(mRequestAction, mInputbytes);
		String codeStr = getErrorCode(data);
		if ((Integer.valueOf(codeStr) == ErrorCode.OK) || (Integer.valueOf(codeStr) == ErrorCode.CONTINUE)) {
			int newLength = data.length - codeStr.length() - 1;
			byte[] result = new byte[newLength];
			System.arraycopy(data, codeStr.length()+1, result, 0, result.length);
			if (mSyncType.equals(Constant.IMPORTACTION)) {
				DatabaseAdapter.beginTransaction();
				byte[] returnByte = ApplicationView.getDataBase().syncImportTable(result);
				
				//Check if there is blob data
				ArrayList<ArrayList<Object>> blobs = DatabaseAdapter.getBlobRecords();
				int blobNb = blobs.size();
				if (blobNb > 0) {
					for(int i=0; i<blobNb; i++) {
						ArrayList<Object> blob = blobs.get(i);
						StringBuffer getBlobAction = new StringBuffer(mBlobAction);
						getBlobAction.append("&table=");
						getBlobAction.append(blob.get(0));
						getBlobAction.append("&fieldid=");
						getBlobAction.append(blob.get(1));
						String recordId = blob.get(2).toString().split("_")[4].split("\\.")[0];
						getBlobAction.append("&record=");
						getBlobAction.append(recordId);
						
						byte[] responsedata = createConnection(getBlobAction.toString(), null);
						String codeResponseStr = getErrorCode(responsedata);
						
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
				byte[] responsedata = createConnection(mResponseAction, returnByte);
				String codeReportStr = getErrorCode(responsedata);
				if (Integer.valueOf(codeReportStr) == ErrorCode.OK) {
					DatabaseAdapter.commitTransaction();
					wellDone = true;
				} else{
					DatabaseAdapter.rollbackTransaction();
				}
				
				if (Integer.valueOf(codeStr) == ErrorCode.CONTINUE) {
					//continue to receive
					wellDone = new DmaHttpBinarySync(mRequestAction, mBlobAction, mResponseAction, mInputbytes, "Import").run();
				}
			} else if (mSyncType.equals(Constant.EXPORTACTION)) {
				//check if there is blob data to send
				ArrayList<ArrayList<Object>> blobs = DatabaseAdapter.getBlobRecords();
				int blobNb = blobs.size();
				if (blobNb > 0) {
					for (ArrayList<Object> blob : blobs) {
						StringBuffer sendAction = new StringBuffer(mBlobAction);
						sendAction.append("&fieldid=");
						sendAction.append(blob.get(0));
						sendAction.append("&blob=");
						sendAction.append(blob.get(1));
						sendAction.append("&format=jpg");
						//TODO use arbitary table to save the blob data
						StringBuffer imagePath = new StringBuffer(Constant.APPPACKAGE);
						imagePath.append(Constant.USERDIRECTORY);
						imagePath.append(ApplicationView.getUsername()).append("/");
						imagePath.append(ApplicationView.getApplicationId());
						imagePath.append("/");
						imagePath.append(blob.get(1));
						File image = new File(imagePath.toString());
						byte[] responsedata = createConnection(sendAction.toString(), this.getBytesFromFile(image));
						String codeResponseStr = getErrorCode(responsedata);
						if (Integer.valueOf(codeResponseStr) == ErrorCode.OK) {
							
						}
					}
				}
				DatabaseAdapter.updateIds(result);
				
				byte[] responsedata = createConnection(mResponseAction, null);
				String codeResponseStr = getErrorCode(responsedata);
				if (Integer.valueOf(codeResponseStr) == ErrorCode.OK) {
					//DatabaseAdapter.cleanTables();
					wellDone = true;
				}
				
				if (Integer.valueOf(codeStr) == ErrorCode.CONTINUE) {
					//continue to receive
					wellDone = new DmaHttpBinarySync(mRequestAction, mBlobAction, mResponseAction, result, "Export").run();
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return bytes;
    }
	
	private byte[] getData(HttpURLConnection connection) {
		if (mCookie == null) {
			mCookie = connection.getHeaderField("Set-Cookie").split(";")[0];
		}
		InputStream in;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			in = connection.getInputStream();
			int c;
			byte[] readByte = new byte[32 * 1024];
			while ((c = in.read(readByte)) > 0) {
				bos.write(readByte, 0, c);
			}
			in.close();
			connection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}
	
	private String getErrorCode(byte[] data) {
		StringBuffer codeStr = new StringBuffer("");
		int i = 0;
		while(i < data.length && data[i] != (int)'\n') {
			codeStr.append((char)data[i]);
			i++;
		}
		return codeStr.toString();
	}
	
	private String getBoundary() {
		return new java.util.Date(System.currentTimeMillis()).toString();
	}
}
