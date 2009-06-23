/**
 	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.penbase.dma.Dalyo.HTTPConnection;

import android.util.Log;

import com.penbase.dma.Dma;
import com.penbase.dma.Binary.Binary;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DatabaseTag;
import com.penbase.dma.Constant.ErrorCode;
import com.penbase.dma.Constant.ResourceTag;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationListView;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Manages HTTP connection
 */
public class DmaHttpClient{
	private URL mUrl;
	private int mLastError = 0;
	private int mErrorCode = 0;
	
	/**
	 * TODO
	 * <icon h="md5" id="">
	 */
	
	//Booleans of sending requests
	private boolean mSendBehavior = true;
	private boolean mSendDb = true;
	private boolean mSendDesign = true;
	private boolean mSendResource = true;
	
	//XML files
	private static String sDirectory;
	private static String sDb_XML;
	private String mDesign_XML;
	private String mBehavior_XML;
	private String mResources_XML;
	
	//Resource file's path
	private static String sResourceFilePath;
	
	private Dma mDma;
	
	public DmaHttpClient(String login) {
		createFilesPath(login);
		try {
			mUrl = new URL(Constant.LOCAL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public DmaHttpClient(Dma dma, String login) {
		mDma = dma;
		createFilesPath(login);
		try {
			mUrl = new URL(Constant.LOCAL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void createFilesPath(String login) {
		if (ApplicationListView.getApplicationName() != null) {
			String applicationName = ApplicationListView.getApplicationName();
			if (applicationName.indexOf("(") != -1) {
				applicationName = applicationName.replace("(", "");
			}
			if (applicationName.indexOf(")") != -1) {
				applicationName = applicationName.replace(")", "");
			}
			if (applicationName.indexOf("<") != -1) {
				applicationName = applicationName.replace("<", "");
			}
			if (applicationName.indexOf(">") != -1) {
				applicationName = applicationName.replace(">", "");
			}

			sDirectory = Constant.PACKAGENAME + login + "/";
			if (!new File(sDirectory).exists()) {
				new File(sDirectory).mkdir();
			}
			sDirectory += applicationName + "/";
			if (!new File(sDirectory).exists()) {
				new File(sDirectory).mkdir();
			}
			sDb_XML = sDirectory + Constant.DBXML;
			mDesign_XML = sDirectory + Constant.DESIGNXML;
			mBehavior_XML = sDirectory + Constant.BEHAVIORXML;
			mResources_XML = sDirectory + Constant.RESOURCEXML;
			sResourceFilePath = Constant.PACKAGENAME + "/" + login + "/" + Constant.RESOURCE;
			if (!new File(sResourceFilePath).exists()) {
				new File(sResourceFilePath).mkdir();
			}
		}
	}
	
	public int GetLastError() {
		return mLastError;
	}

	public String Authentication(String login, String password) {
		StringBuffer loginAction = new StringBuffer("act=login&login=");
		loginAction.append(login);
		loginAction.append("&passwd_md5=");
		loginAction.append(md5(password));
		loginAction.append("&useragent=ANDROID");
		String result = "";
		try {
			result = new String(sendPost(loginAction.toString()), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getFilesPath() {
		return sDirectory;
	}
	
	public static String getResourcePath() {
		return sResourceFilePath;
	}
	
	private byte[] sendPost(String parameters) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		HttpURLConnection connection = null;
		try{
			connection = (HttpURLConnection) mUrl.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.connect();
			OutputStream out = connection.getOutputStream();
			out.write(parameters.getBytes());
			out.close();
			InputStream in =  connection.getInputStream();
			int c;
			byte[] readByte = new byte[32 * 1024];
			while ((c = in.read(readByte)) > 0) {
				bos.write(readByte, 0, c);
			}
			in.close();
			connection.disconnect();
		} catch (IOException e) {
			//e.printStackTrace();
			mDma.showMessage("Connection error");
		}
		byte[] data = bos.toByteArray();
		String codeStr = "";
		int i = 0;
		int length = data.length;
		while(i < length && data[i] != (int)'\n') {
			codeStr += (char)data[i];
			i++;
		}
		mErrorCode = Integer.valueOf(codeStr);
		if (mErrorCode != ErrorCode.OK) {
			return null;
		} else {
			int newLength = data.length - codeStr.length() - 1;
			byte[] result = new byte[newLength];
			System.arraycopy(data, codeStr.length()+1, result, 0, result.length);
			return result;	
		}
	}
	
	/**
	 * Creates a parsing object from a string stream or a xml file
	 * @param xmlStream
	 * @param xmlFile
	 * @return
	 */
	public static Document createParseDocument(byte[] xmlStream, File xmlFile) {
		DocumentBuilder docBuild;
		Document document = null;
		ByteArrayInputStream stream = null;
		try{
			docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			if (xmlStream != null) {
				stream = new ByteArrayInputStream(xmlStream);
				document = docBuild.parse(stream);
			} else if (xmlFile != null) {
				document = docBuild.parse(xmlFile);
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return document;
	}
	
	public void checkXmlFiles() {
		if (new File(mDesign_XML).exists()) {
			this.mSendDesign = false;
		}
		if (new File(sDb_XML).exists()) {
			this.mSendDb = false;
		}
		if (new File(mBehavior_XML).exists()) {
			this.mSendBehavior = false;
		}
		if (new File(mResources_XML).exists()) {
			this.mSendResource = false;
		}
	}
	
	public int getIdDb(File dbXml) {
		Log.i("info", "getiddb");
		Document dbDoc = createParseDocument(null, dbXml);
		Element tagID = (Element)dbDoc.getElementsByTagName(DatabaseTag.DB).item(0);
		return Integer.valueOf(tagID.getAttribute(DatabaseTag.DB_ID));
	}
	
	/**
	 * Gets the design of an application
	 * @return
	 */
	public Reader getDesignReader(String urlRequest) throws FileNotFoundException {
		if (mSendDesign) {
			Log.i("info", "download design xml");
			StringBuffer getDesign = new StringBuffer("act=getdesign");
			getDesign.append(urlRequest);
			byte[] bytes = sendPost(getDesign.toString());
			streamToFile(bytes, mDesign_XML, false);
			return new InputStreamReader(new ByteArrayInputStream(bytes));
		} else {
			return new FileReader(new File(mDesign_XML));
		}
	}
	
	/**
	 * Gets the resources of an application
	 */
	public void getResource(String urlRequest) {
		if (mSendResource) {
			StringBuffer getResources = new StringBuffer("act=getresources");
			getResources.append(urlRequest);
			byte[] bytes = sendPost(getResources.toString());
			SAXParserFactory spFactory = SAXParserFactory.newInstance();
	    	SAXParser saxParser;
			try {
				saxParser = spFactory.newSAXParser();
				XMLReader xmlReader = saxParser.getXMLReader();
				EventsHandler eventsHandler = new EventsHandler(urlRequest);
		    	xmlReader.setContentHandler(eventsHandler);
		    	xmlReader.parse(new InputSource(new ByteArrayInputStream(bytes)));
			}
	    	 catch (ParserConfigurationException e) {
	 			e.printStackTrace();
	 		} catch (SAXException e) {
	 			e.printStackTrace();
	 		} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			streamToFile(bytes, mResources_XML, false);
		} else {
			
		}
	}
	
	private class EventsHandler extends DefaultHandler {
		private String mUrlRequest;
		
		public EventsHandler(String url) {
			mUrlRequest = url;
		}
		
		@Override
		public void startDocument() throws SAXException {

        }
        
        @Override
        public void endDocument() throws SAXException {

        }
        
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        	if (localName.equals(ResourceTag.RESOURCES_R)) {
        		String id = atts.getValue(ResourceTag.RESOURCES_R_ID);
        		String hashcode = atts.getValue(ResourceTag.RESOURCES_R_HASHCODE);
        		String ext = atts.getValue(ResourceTag.RESOURCES_R_EXT);
        		StringBuffer fileName = new StringBuffer(sResourceFilePath);
        		fileName.append("/");
				fileName.append(id);
				fileName.append(".");
				fileName.append(ext);
				StringBuffer getResource = new StringBuffer("act=getresource");
				getResource.append(mUrlRequest);
				getResource.append("&resourceid=");
				getResource.append(id);
				byte[] resourceStream = sendPost(getResource.toString());
				java.security.MessageDigest messageDigest = null;
				try {
					messageDigest = java.security.MessageDigest.getInstance("MD5");
				}  catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				messageDigest.update(resourceStream, 0, resourceStream.length);
				String hexString = hexStringFromBytes(messageDigest.digest()).toUpperCase();

				if (hexString.equals(hashcode)) {
					streamToFile(resourceStream, fileName.toString(), true);
				}
        	}
        }
        
        @Override
		public void characters(char[] ch, int start, int length) throws SAXException {
        	
		}
        
        @Override
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException{
        	
        }      
	}
	
	/**
	 * Gets the DB of an application
	 * @return
	 */
	public Document getDB(String urlRequest) {
		if (mSendDb) {
			StringBuffer getDB = new StringBuffer("act=getdb");
			getDB.append(urlRequest);
			byte[] bytes = sendPost(getDB.toString());
			streamToFile(bytes, sDb_XML, false);
			return createParseDocument(bytes, null);
		} else {
			return createParseDocument(null, new File(sDb_XML));
		}
	}
	
	/**
	 * Gets the behavior of an application
	 * @return
	 */
	public Document getBehavior(String urlRequest) {
		if (mSendBehavior) {
			StringBuffer getBehavior = new StringBuffer("act=getbehavior");
			getBehavior.append(urlRequest);
			byte[] bytes = sendPost(getBehavior.toString());
			streamToFile(bytes, mBehavior_XML, false);
			return createParseDocument(bytes, null);
		} else {
			return createParseDocument(null, new File(mBehavior_XML));
		}
	}
	
	/**
	 * Imports data
	 * @param AppId
	 * @param DbId
	 * @param login
	 * @param pwd
	 * @param tables
	 * @param filters
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean importData(String AppId, String DbId, String login, String pwd, ArrayList<String> tables, Object filters) {
		boolean result = false;
		String syncUrlRequest = generateSyncUrlRequest(AppId, DbId, login, pwd);
		StringBuffer ask = new StringBuffer("act=ask");
		ask.append(syncUrlRequest);
		StringBuffer getBlob = new StringBuffer("act=getblob");
		getBlob.append(syncUrlRequest);
		StringBuffer report = new StringBuffer("act=rep");
		report.append(syncUrlRequest);
		
		if (filters != null) {
			int filtersSize = ((ArrayList<?>)filters).size();
			if (filtersSize > 0) {
				ask.append("&fcount=");
				ask.append(filtersSize);
				for (int i=0; i<filtersSize; i++) {
					ArrayList<Object> filter = (ArrayList<Object>)((ArrayList<Object>)filters).get(i);
					ask.append("&ff");
					ask.append(i);
					ask.append("=");
					ask.append(filter.get(0).toString());
					Object operator = Function.getOperator(((ArrayList<?>)filters).get(1));
					ask.append("&fo");
					ask.append(i);
					ask.append("=");
					ask.append(urlEncode(operator.toString()));
					ask.append("&fv");
					ask.append(i);
					ask.append("=");
					ask.append(filter.get(2).toString());
				}
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DmaHttpBinarySync importSync = null;
		try {
			if (tables != null) {
				int tablesNb = tables.size();
				baos.write(Binary.intToByteArray(tablesNb));
				for (int i=0; i<tablesNb; i++) {
					baos.write(Binary.intToByteArray(Integer.valueOf(tables.get(i))));
				}
			} else {
				baos.write(Binary.intToByteArray(DatabaseAdapter.getTableNb()));
				for (String s : DatabaseAdapter.getTableIds()) {
					baos.write(Binary.intToByteArray(Integer.valueOf(s)));
				}
			}
			byte[] inputbytes = baos.toByteArray();
			importSync = new DmaHttpBinarySync(mUrl.toString(), ask.toString(), getBlob.toString(), report.toString(), inputbytes, Constant.IMPORTACTION);
			result = importSync.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Exports local data to server
	 * @param AppId
	 * @param DbId
	 * @param login
	 * @param pwd
	 * @param tables
	 * @param filters
	 * @return
	 */
	public boolean exportData(String AppId, String DbId, String login, String pwd, ArrayList<String> tables, Object filters) {
		String syncUrlRequest = generateSyncUrlRequest(AppId, DbId, login, pwd);
		StringBuffer sync = new StringBuffer("act=sync");
		sync.append(syncUrlRequest);
		StringBuffer send = new StringBuffer("act=send");
		send.append(syncUrlRequest);
		StringBuffer commit = new StringBuffer("act=commit");
		commit.append(syncUrlRequest);
		
		byte[] exportData = ApplicationView.getDataBase().syncExportTable(tables, filters);
		DmaHttpBinarySync exportSync = new DmaHttpBinarySync(mUrl.toString(), sync.toString(), send.toString(), commit.toString(), exportData, Constant.EXPORTACTION);
		return exportSync.run();
	}
	
	private String urlEncode(String s) {
		String result = "";
		try {
			result = java.net.URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String generateSyncUrlRequest(String AppId, String DbId, String login, String pwd) {
		StringBuffer result = new StringBuffer("&from=runtime&appid=");
		result.append(AppId);
		result.append("&dataid=");
		result.append(DbId);
		result.append("&login=");
		result.append(login);
		result.append("&passwd_md5=");
		result.append(md5(pwd));
		result.append("&stream=1&useragent=ANDROID&did=");
		result.append(Dma.getDeviceID());
		return result.toString();
	}
	
	public String generateRegularUrlRequest(String AppId, String AppVer, String AppBuild, String SubId, String login, String pwd) {
		StringBuffer result = new StringBuffer("&from=runtime&appid=");
		result.append(AppId);
		result.append("&appversion=");
		result.append(AppVer);
		result.append("&appbuild=");
		result.append(AppBuild);
		result.append("&subid=");
		result.append(SubId);
		result.append("&did=");
		result.append(Dma.getDeviceID());
		result.append("&login=");
		result.append(login);
		result.append("&passwd_md5=");
		result.append(md5(pwd));
		result.append("&useragent=ANDROID");
		return result.toString();
	}
	
	/**
	 * Saves the downloaded xml stream or images to file
	 * @param stream
	 * @param filePath
	 */
	private void streamToFile(byte[] bytes, String filePath, boolean isImage) {
        File file = new File(filePath);
        FileOutputStream fos;
        try{
        	fos = new FileOutputStream(file);
        	if (isImage) {
        		DataOutputStream dos = new DataOutputStream(fos);
        		dos.write(bytes);
        		dos.close();
        	} else {
        		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos), 8 * 1024);
        		out.write(new String(bytes, "UTF8"));
        		out.close();	
        	}
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        } 
	}
	
	public static String md5(String string) {
		java.security.MessageDigest messageDigest = null;
		try {
			messageDigest = java.security.MessageDigest.getInstance("MD5");
		}  catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		messageDigest.update(string.getBytes(),0,string.length());
		return new BigInteger(1, messageDigest.digest()).toString(16);
	}

	public static String getBoundary() {
		return new java.util.Date(System.currentTimeMillis()).toString();
	}
	
	public static int getServerInfo() {
		//Modify this method when the server has immigrate to spv2
		return 1;
	}
	
	 /**
	 * Return a hexadecimal string for the given byte array.
	 *
	 * @param b the byte array to convert
	 * @return the hexadecimal string
	 */
	private static String hexStringFromBytes(byte[] b) {
		char[] hexChars ={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
		String hex = "";
		int msb;
		int lsb = 0;

		// MSB maps to idx 0
		for (int i = 0; i < b.length; i++) {
			msb = ((int)b[i] & 0x000000FF) / 16;
			lsb = ((int)b[i] & 0x000000FF) % 16;
			hex = hex + hexChars[msb] + hexChars[lsb];
		}

		return hex;
	}
}
