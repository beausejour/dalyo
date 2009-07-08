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

import com.penbase.dma.Common;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
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
	private static StringBuffer sDirectory;
	private static String sDb_XML;
	private String mDesign_XML;
	private String mBehavior_XML;
	private String mResources_XML;
	
	//Resource file's path
	private static String sResourceFilePath;
	
	public DmaHttpClient(String login) {
		createFilesPath(login);
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

			sDirectory = new StringBuffer(Constant.PACKAGENAME);
			sDirectory.append(login).append("/");

			if (!new File(sDirectory.toString()).exists()) {
				new File(sDirectory.toString()).mkdir();
			}
			sDirectory.append(applicationName).append("/");

			if (!new File(sDirectory.toString()).exists()) {
				new File(sDirectory.toString()).mkdir();
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
		loginAction.append(password);
		loginAction.append("&probe=");
		loginAction.append(Dma.getVersion());
		loginAction.append("&useragent=ANDROID");
		String result = null;
		try {
			byte[] bytes = sendPost(loginAction.toString());
			if (bytes != null) {
				result = new String(bytes, "UTF8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getFilesPath() {
		return sDirectory.toString();
	}
	
	public static String getResourcePath() {
		return sResourceFilePath;
	}
	
	private byte[] sendPost(String parameters) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Constant.SECUREDSERVER + parameters);
		try {
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream in =  entity.getContent();
			int c;
			byte[] readByte = new byte[32 * 1024];
			while ((c = in.read(readByte)) > 0) {
				bos.write(readByte, 0, c);
			}
			in.close();
			httpPost.abort();
			httpClient.getConnectionManager().shutdown();   
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bos.size() > 0) {
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
		} else {
			return null;
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
		//Log.i("info", "getiddb");
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
			//Log.i("info", "download design xml");
			StringBuffer getDesign = new StringBuffer("act=getdesign");
			getDesign.append(urlRequest);
			byte[] bytes = sendPost(getDesign.toString());
			Common.streamToFile(bytes, mDesign_XML, false);
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
			Common.streamToFile(bytes, mResources_XML, false);
		} else {
			SAXParserFactory spFactory = SAXParserFactory.newInstance();
	    	SAXParser saxParser;
			try {
				saxParser = spFactory.newSAXParser();
				XMLReader xmlReader = saxParser.getXMLReader();
				EventsHandler eventsHandler = new EventsHandler(urlRequest);
		    	xmlReader.setContentHandler(eventsHandler);
		    	xmlReader.parse(new InputSource(new FileInputStream(new File(mResources_XML))));
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
		}
	}
	
	private class EventsHandler extends DefaultHandler {
		private String mUrlRequest;
		private boolean mWillDownload;
		
		public EventsHandler(String url) {
			mUrlRequest = url;
			mWillDownload = true;
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
				File resourceFile = new File(fileName.toString());
				if (resourceFile.exists()) {
					byte[] bytes = null;
					try {
						bytes = Common.getBytesFromFile(resourceFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					String hexString = Common.md5HexStringFromBytes(bytes);
					if (hexString.equals(hashcode)) {
						mWillDownload = false;
					}
				}
				if (mWillDownload) {
					StringBuffer getResource = new StringBuffer("act=getresource");
					getResource.append(mUrlRequest);
					getResource.append("&resourceid=");
					getResource.append(id);
					byte[] resourceStream = sendPost(getResource.toString());
					String hexString = Common.md5HexStringFromBytes(resourceStream);

					if (hexString.equals(hashcode)) {
						Common.streamToFile(resourceStream, fileName.toString(), true);
					}	
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
			Common.streamToFile(bytes, sDb_XML, false);
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
			Common.streamToFile(bytes, mBehavior_XML, false);
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
		if (ApplicationListView.getNetworkInfo() == null) {
			return false;
		} else {
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
					ArrayList<Integer> importTableList = DatabaseAdapter.getImportTableId();
					baos.write(Binary.intToByteArray(importTableList.size()));
					for (int id : importTableList) {
						baos.write(Binary.intToByteArray(id));
					}
				}
				byte[] inputbytes = baos.toByteArray();
				importSync = new DmaHttpBinarySync(ask.toString(), getBlob.toString(), report.toString(), inputbytes, Constant.IMPORTACTION);
				result = importSync.run();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}
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
		if (ApplicationListView.getNetworkInfo() == null) {
			return false;
		} else {
			String syncUrlRequest = generateSyncUrlRequest(AppId, DbId, login, pwd);
			StringBuffer sync = new StringBuffer("act=sync");
			sync.append(syncUrlRequest);
			StringBuffer send = new StringBuffer("act=send");
			send.append(syncUrlRequest);
			StringBuffer commit = new StringBuffer("act=commit");
			commit.append(syncUrlRequest);
			
			byte[] exportData = ApplicationView.getDataBase().syncExportTable(tables, filters);
			DmaHttpBinarySync exportSync = new DmaHttpBinarySync(sync.toString(), send.toString(), commit.toString(), exportData, Constant.EXPORTACTION);
			return exportSync.run();	
		}
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
		result.append(pwd);
		result.append("&probe=");
		result.append(Dma.getVersion());
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
		result.append(pwd);
		result.append("&probe=");
		result.append(Dma.getVersion());
		result.append("&useragent=ANDROID");
		return result.toString();
	}
}
