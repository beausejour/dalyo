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

import android.os.Looper;
import android.util.Log;

import com.penbase.dma.Dma;
import com.penbase.dma.R;
import com.penbase.dma.Binary.Binary;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DatabaseTag;
import com.penbase.dma.Constant.ErrorCode;
import com.penbase.dma.Constant.RessourceTag;
import com.penbase.dma.Constant.DesignTag;
import com.penbase.dma.Dalyo.Application;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationListView;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Manages HTTP connection
 */
public class DmaHttpClient{
	private URL mUrl;
	private int mLastError = 0;
	private int mErrorCode = 0;
	private static final String STRING="string";
	private static final String IMAGE="image";
	
	/**
	 * TODO
	 * <icon h="md5" id="">
	 */
	
	//Booleans of sending requests
	private boolean mSendBehavior = true;
	private boolean mSendDb = true;
	private boolean mSendDesign = true;
	
	//XML files
	private static String sDirectory;
	private static String sDb_XML;
	private String mDesign_XML;
	private String mBehavior_XML;
	private String mResources_XML;
	
	//Image file's path
	private String mImageFilePath;
	
	private Dma mDma;
	
	public DmaHttpClient() {
		createFilesPath();
		try {
			mUrl = new URL(Constant.LOCAL);
			//url = new URL("http://emvista.com/server/com.penbase.arbiter.Arbiter");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public DmaHttpClient(Dma dma) {
		mDma = dma;
		createFilesPath();
		try {
			mUrl = new URL(Constant.LOCAL);
			//url = new URL("http://emvista.com/server/com.penbase.arbiter.Arbiter");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void createFilesPath() {
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
			sDirectory = Constant.PACKAGENAME+applicationName+"/";
			if (!new File(sDirectory).exists()) {
				new File(sDirectory).mkdir();
			}
			sDb_XML = sDirectory+"db.xml";
			mDesign_XML = sDirectory+"design.xml";
			mBehavior_XML = sDirectory+"behavior.xml";
			mResources_XML = sDirectory+"resources.xml";
			mImageFilePath = sDirectory;
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
		return SendPost(loginAction.toString(), STRING);
	}
	
	public static String getFilesPath() {
		return sDirectory;
	}
	
	private String SendPost(String parameters, String type) {
		String result = null;
		StringBuilder response = null;
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
			
			// Get the input stream and read response
			InputStream in =  connection.getInputStream();
			if (type.equals(STRING)) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in), 10);
				response = new StringBuilder();
				String line = null;
				int lineNumber = 1;
				while ((line = reader.readLine()) != null) {
					if (lineNumber == 1) {
						mErrorCode = Integer.valueOf(line);
						lineNumber++;
					} else {
						response.append(line);
					}
				}
				in.close();
				result = response.toString();
			} else if (type.equals(IMAGE)) {
				StringBuffer buf = new StringBuffer();
				int c;
				while ((c = in.read()) != -1) {
					buf.append((char) c);
				}
				in.close();
				result = buf.toString();
				mErrorCode = Integer.parseInt(result.substring(0, result.indexOf('\n')));
				result = result.substring(result.indexOf('\n') + 1, result.length());
			}
			if (connection != null) {
				connection.disconnect();
			}
		} catch (ProtocolException pe) {
			mDma.showMessage("HTTPExample: ProtocolException; " + pe.getMessage());
		} catch (IOException ioe) {
			Looper.prepare();
			mDma.showMessage("HTTPExample: IOException; " + ioe.getMessage());
		}

		if (mErrorCode != ErrorCode.OK) {
			return null;
		} else {
			return result;
		}
	}
	
	/**
	 * Creates a parsing object from a string stream or a xml file
	 * @param xmlStream
	 * @param xmlFile
	 * @return
	 */
	public static Document CreateParseDocument(String xmlStream, File xmlFile) {
		DocumentBuilder docBuild;
		Document document = null;
		ByteArrayInputStream stream = null;
		try{
			docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			if (xmlStream != null) {
				stream = new ByteArrayInputStream(xmlStream.getBytes());
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
	
	public void update(String appList) {
		HashMap<String, Application> applicationMap = new HashMap<String, Application>();
		Document doc = DmaHttpClient.CreateParseDocument(appList, null);
		NodeList root = doc.getElementsByTagName(DesignTag.ROOT);
		NodeList apps = root.item(0).getChildNodes();
		int appsLen = apps.getLength();
		for (int s = 0; s < appsLen; s++) {
			NodeList els = apps.item(s).getChildNodes();
			int elsLength = els.getLength();
			String appId = "";
			String title = "";
			String appVer = "";
			String appBuild = "";
			String subId = "";
			String dbId = "";
			for (int t = 0; t < elsLength; t++) {
				Node node = els.item(t);
				NodeList nodes = node.getChildNodes();
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					if (node.getNodeName().equals(DesignTag.LOGIN_ID)) {
						appId = nodes.item(0).getNodeValue();
					} else if (node.getNodeName().equals(DesignTag.LOGIN_TIT)) {
						title = nodes.item(0).getNodeValue();
					} else if (node.getNodeName().equals(DesignTag.LOGIN_VER)) {
						appVer = nodes.item(0).getNodeValue();
					} else if (node.getNodeName().equals(DesignTag.LOGIN_BLD)) {
						appBuild = nodes.item(0).getNodeValue();
					} else if (node.getNodeName().equals(DesignTag.LOGIN_SUB)) {
						subId = nodes.item(0).getNodeValue();
					} else if (node.getNodeName().equals(DesignTag.LOGIN_DBID)) {
						dbId = nodes.item(0).getNodeValue();
					}
				}
			}
			ArrayList<Application> applications = Dma.getApplications();
			int currentApplicationsNb = applications.size();
			int deleteIndex = -1;
			for (int i=0; i<currentApplicationsNb; i++) {
				Application application = applications.get(i);
				if (application.getAppId().equals(appId)) {
					if (!(application.getAppVer().equals(appVer)) || !(application.getAppBuild().equals(appBuild)) ||
							!(application.getSubId().equals(subId)) || !(application.getDbId().equals(dbId))) {
						deleteIndex = i;
					}
				}
			}

			if (deleteIndex != -1) {
				String applicationName = Dma.getApplications().get(deleteIndex).getName();
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
				String folderPath = Constant.PACKAGENAME+applicationName+"/";
				File folder = new File(folderPath);
				if (folder.exists() && folder.isDirectory()) {
					//Delete all the files
					String[] children = folder.list();
					int childrenNb = children.length;
		            for (int i=0; i<childrenNb; i++) {
		            	new File(folder, children[i]).delete();
		            }
				}
				Dma.getApplications().remove(deleteIndex);
			}
			//add current application
			Application newApplication = new Application();
			newApplication.setAppId(appId);
			newApplication.setName(title);
			newApplication.setAppVer(appVer);
			newApplication.setAppBuild(appBuild);
			newApplication.setSubId(subId);
			newApplication.setDbId(dbId);
			newApplication.setIconRes(R.drawable.splash);
			applicationMap.put(newApplication.getName(), newApplication);
		}
		
		//add other apps in map
		ArrayList<Application> applicationsUnchanged = Dma.getApplications();
		int applicationsUnchangedNb = applicationsUnchanged.size();
		for (int i=0; i<applicationsUnchangedNb; i++) {
			Application appUnchanged = applicationsUnchanged.get(i);
			applicationMap.put(appUnchanged.getName(), appUnchanged);
		}
		
		Dma.sortApplicationsList(applicationMap);
	}
	
	public void checkXmlFiles() {
		//Design
		if (new File(mDesign_XML).exists()) {
			this.mSendDesign = false;
		}
		if (new File(sDb_XML).exists()) {
			this.mSendDb = false;
		}
		if (new File(mBehavior_XML).exists()) {
			this.mSendBehavior = false;
		}
	}
	
	public int getIdDb(File dbXml) {
		Log.i("info", "getiddb");
		Document dbDoc = CreateParseDocument(null, dbXml);
		Element tagID = (Element)dbDoc.getElementsByTagName(DatabaseTag.DB).item(0);
		return Integer.valueOf(tagID.getAttribute(DatabaseTag.DB_ID));
	}
	
	/**
	 * Gets the design of an application
	 * @param AppId
	 * @param AppVer
	 * @param AppBuild
	 * @param SubId
	 * @param login
	 * @param pwd
	 * @return
	 */
	public Document getDesign(String urlRequest) {
		if (mSendDesign) {
			Log.i("info", "download design xml");
			StringBuffer getDesign = new StringBuffer("act=getdesign");
			getDesign.append(urlRequest);
			String designStream = SendPost(getDesign.toString(), STRING);
			Log.i("info", "designStream "+designStream);
			StreamToFile(designStream, mDesign_XML, false);
			return CreateParseDocument(designStream, null);
		} else {
			Log.i("info", "start parsing design file");
			return CreateParseDocument(null, new File(mDesign_XML));
		}
	}
	
	/**
	 * Hashmap contains resource id and its hashcode
	 */
	public HashMap<String, String> getResourceMap(String value) {
		HashMap<String, String> resourceMapFile = new HashMap<String, String>();
		if (checkFileExist(mResources_XML)) {
			Document resuourcesFileDoc = CreateParseDocument(null, new File(mResources_XML));
			Log.i("info", "resourcexml exits "+resuourcesFileDoc.getElementsByTagName(RessourceTag.RESOURCES_RL).item(0).getChildNodes());
			NodeList resourceFileList = resuourcesFileDoc.getElementsByTagName(RessourceTag.RESOURCES_RL).item(0).getChildNodes();
			int resourceFileLen = resourceFileList.getLength();
			for (int i=0; i<resourceFileLen; i++) {
				Element resource = (Element) resourceFileList.item(i);
				if ((resource.hasAttribute(RessourceTag.RESOURCES_R_ID)) && 
						(resource.hasAttribute(RessourceTag.RESOURCES_R_HASHCODE))) {
					if (value.equals("hashcode")) {
						resourceMapFile.put(resource.getAttribute(RessourceTag.RESOURCES_R_ID), 
								resource.getAttribute(RessourceTag.RESOURCES_R_HASHCODE));
					} else if (value.equals("ext")) {
						resourceMapFile.put(resource.getAttribute(RessourceTag.RESOURCES_R_ID), 
								resource.getAttribute(RessourceTag.RESOURCES_R_EXT));
					}
				}
			}
		}
		return resourceMapFile;
	}
	
	/**
	 * Gets the resources of an application
	 * @param AppId
	 * @param AppVer
	 * @param AppBuild
	 * @param SubId
	 * @param login
	 * @param pwd
	 */
	public void getResources(String urlRequest) {
		HashMap<String, String> resourceMapFile = getResourceMap("hashcode");
		StringBuffer getResources = new StringBuffer("act=getresources");
		getResources.append(urlRequest);
		String resourcesStream = SendPost(getResources.toString(), STRING);
		Document resourceDocument = CreateParseDocument(resourcesStream, null);
		if (resourceDocument.getElementsByTagName(RessourceTag.RESOURCES_RL).getLength() > 0) {
			NodeList resourceList = resourceDocument.getElementsByTagName(RessourceTag.RESOURCES_RL).item(0).getChildNodes();
			int resourceLen = resourceList.getLength();
			
			for (int i=0; i<resourceLen; i++) {
				Element resource = (Element) resourceList.item(i);
				if (resource.hasAttribute(RessourceTag.RESOURCES_R_ID)) {
					String resourceId = resource.getAttribute(RessourceTag.RESOURCES_R_ID);
					StringBuffer fileName = new StringBuffer(mImageFilePath);
					fileName.append(resource.getAttribute(RessourceTag.RESOURCES_R_ID));
					fileName.append(".");
					fileName.append(resource.getAttribute(RessourceTag.RESOURCES_R_EXT));
					StringBuffer getResource = new StringBuffer("act=getresource");
					getResource.append(urlRequest);
					getResource.append("&resourceid=");
					getResource.append(resource.getAttribute(RessourceTag.RESOURCES_R_ID));
					if ((resourceMapFile.containsKey(resourceId)) && (checkFileExist(fileName.toString()))) {
						if (!resourceMapFile.get(resourceId).equals(resource.getAttribute(RessourceTag.RESOURCES_R_HASHCODE))) {
							Log.i("info", "download repalce image");
							new File(fileName.toString()).delete();
							String resourceStream = SendPost(getResource.toString(), IMAGE);
							StreamToFile(resourceStream, fileName.toString(), true);
						}
					} else {
						Log.i("info", "download image");
						String resourceStream = SendPost(getResource.toString(), IMAGE);
						StreamToFile(resourceStream, fileName.toString(), true);
					}
				}
			}
			StreamToFile(resourcesStream, mResources_XML, false);
		}
	}
	
	/**
	 * Gets the DB of an application
	 * @param AppId
	 * @param AppVer
	 * @param AppBuild
	 * @param SubId
	 * @param login
	 * @param pwd
	 * @return
	 */
	public Document getDB(String urlRequest) {
		Log.i("info", "sendDb "+mSendDb);
		if (mSendDb) {
			StringBuffer getDB = new StringBuffer("act=getdb");
			getDB.append(urlRequest);
			String dbStream = SendPost(getDB.toString(), STRING);
			Log.i("info", "dbstream ");
			StreamToFile(dbStream, sDb_XML, false);
			return CreateParseDocument(dbStream, null);
		} else {
			return CreateParseDocument(null, new File(sDb_XML));
		}
	}
	
	/**
	 * Gets the behavior of an application
	 * @param AppId
	 * @param AppVer
	 * @param AppBuild
	 * @param SubId
	 * @param login
	 * @param pwd
	 * @return
	 */
	public Document getBehavior(String urlRequest) {
		if (mSendBehavior) {
			StringBuffer getBehavior = new StringBuffer("act=getbehavior");
			getBehavior.append(urlRequest);
			String behaviorStream = SendPost(getBehavior.toString(), STRING);
			StreamToFile(behaviorStream, mBehavior_XML, false);
			return CreateParseDocument(behaviorStream, null);
		} else {
			return CreateParseDocument(null, new File(mBehavior_XML));
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
			importSync = new DmaHttpBinarySync(mUrl.toString(), ask.toString(), getBlob.toString(), report.toString(), inputbytes, "Import");
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
		Log.i("info", "exportData "+exportData.length);
		DmaHttpBinarySync exportSync = new DmaHttpBinarySync(mUrl.toString(), sync.toString(), send.toString(), commit.toString(), exportData, "Export");
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
	private void StreamToFile(String stream, String filePath, boolean isImage) {
        File file = new File(filePath);
        FileOutputStream fos;
        try{
                fos = new FileOutputStream(file);
                if (isImage) {
                	DataOutputStream dos = new DataOutputStream(fos);
                    dos.writeBytes(stream);	
                } else {
                	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"), 8 * 1024);
                    out.write(stream);
                    out.close();	
                }
                Log.i("info", "file create ok "+filePath);
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        } 
	}
	
	/**
	 * Checks if file exists and its length is great than 0
	 * @param fileName
	 * @return
	 */
	private boolean checkFileExist(String fileName) {
		boolean result = false;
		if ((new File(fileName).exists()) && (new File(fileName).length() > 0)) {
			result = true;
		}
		return result;
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
}
