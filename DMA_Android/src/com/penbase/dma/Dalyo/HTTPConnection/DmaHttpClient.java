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

import java.io.BufferedReader;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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

import android.os.Looper;
import android.util.Log;

public class DmaHttpClient{
	private URL url;
	private int lastError = 0;
	private int errorCode = 0;
	private static final String STRING="string";
	private static final String IMAGE="image";
	private boolean updated = false;
	
	//Booleans of sending requests
	private boolean sendBehavior = true;
	private boolean sendDb = true;
	private boolean sendDesign = true;
	
	//XML files
	private static String directory;
	private String login_XML;
	private static String db_XML;
	private String design_XML;
	private String behavior_XML;
	private String resources_XML;
	
	//Image file's path
	private String imageFilePath;
	
	private Dma mDma;
	
	public DmaHttpClient() {
		createFilesPath();
		try {
			url = new URL(Constant.LOCAL);
			//url = new URL("http://emvista.com/server/com.penbase.arbiter.Arbiter");
		}
		catch (MalformedURLException e) 
		{e.printStackTrace();}
	}
	
	public DmaHttpClient(Dma dma) {
		mDma = dma;
		createFilesPath();
		try {
			url = new URL(Constant.LOCAL);
			//url = new URL("http://emvista.com/server/com.penbase.arbiter.Arbiter");
		}
		catch (MalformedURLException e) 
		{e.printStackTrace();}
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
			directory = Constant.PACKAGENAME+applicationName+"/";
			if (!new File(directory).exists()) {
				new File(directory).mkdir();
			}
			login_XML = directory+"login.xml";
			db_XML = directory+"db.xml";
			design_XML = directory+"design.xml";
			behavior_XML = directory+"behavior.xml";
			resources_XML = directory+"resources.xml";
			imageFilePath = directory;
		}
	}
	
	public int GetLastError() {
		return lastError;
	}

	public String Authentication(String login, String password) {
		return SendPost("act=login&login="+login+"&passwd_md5="+md5(password)+"&useragent=ANDROID", STRING);
	}
	
	public static String getFilesPath() {
		return directory;
	}
	
	private String SendPost(String parameters, String type) {
		String result = null;
		StringBuilder response = null;
		HttpURLConnection connection = null;
		try{
			connection = (HttpURLConnection) url.openConnection();
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
						errorCode = Integer.valueOf(line);
						lineNumber++;
					}
					else {
						response.append(line);
					}
				}
				in.close();
				result = response.toString();
			}
			else if (type.equals(IMAGE)) {
				StringBuffer buf = new StringBuffer();
				int c;
				while ((c = in.read()) != -1) {
					buf.append((char) c);
				}
				in.close();
				result = buf.toString();
				errorCode = Integer.parseInt(result.substring(0, result.indexOf('\n')));
				result = result.substring(result.indexOf('\n') + 1, result.length());
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
		catch (ProtocolException pe) {
			mDma.showMessage("HTTPExample: ProtocolException; " + pe.getMessage());
		}
		catch (IOException ioe) {
			Looper.prepare();
			mDma.showMessage("HTTPExample: IOException; " + ioe.getMessage());
		}

		if (errorCode != ErrorCode.OK) {
			return "error";
		}
		else {
			return result;
		}
	}
	
	//Create a parsing object from a string stream or a xml file
	public static Document CreateParseDocument(String xmlStream, File xmlFile) {
		DocumentBuilder docBuild;
		Document document = null;
		ByteArrayInputStream stream = null;
		try{
			docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			if (xmlStream != null) {
				stream = new ByteArrayInputStream(xmlStream.getBytes());
				document = docBuild.parse(stream);
			}
			else if (xmlFile != null) {
				document = docBuild.parse(xmlFile);
			}
		}
		catch (SAXException e)
		{e.printStackTrace();}
		catch (IOException e)
		{e.printStackTrace();}
		catch (ParserConfigurationException e)
		{e.printStackTrace();}
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
				Node noeud = els.item(t);
				if (noeud.getNodeType() == Node.ELEMENT_NODE) {
					if (noeud.getNodeName().equals(DesignTag.LOGIN_ID)) {
						appId = noeud.getChildNodes().item(0).getNodeValue();
					}		
					else if (noeud.getNodeName().equals(DesignTag.LOGIN_TIT)) {
						title = noeud.getChildNodes().item(0).getNodeValue();
					}
					else if (noeud.getNodeName().equals(DesignTag.LOGIN_VER)) {
						appVer = noeud.getChildNodes().item(0).getNodeValue();
					}
					else if (noeud.getNodeName().equals(DesignTag.LOGIN_BLD)) {
						appBuild = noeud.getChildNodes().item(0).getNodeValue();
					}
					else if (noeud.getNodeName().equals(DesignTag.LOGIN_SUB)) {
						subId = noeud.getChildNodes().item(0).getNodeValue();
					}
					else if (noeud.getNodeName().equals(DesignTag.LOGIN_DBID)) {
						dbId = noeud.getChildNodes().item(0).getNodeValue();
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
		if (new File(design_XML).exists()) {
			this.sendDesign = false;
		}
		if (new File(db_XML).exists()) {
			this.sendDb = false;
		}
		if (new File(behavior_XML).exists()) {
			this.sendBehavior = false;
		}
	}
	
	/*public boolean update() {
		return updated = true;
	}*/
	
	public int getIdDb(File dbXml) {
		Log.i("info", "getiddb");
		Document dbDoc = CreateParseDocument(null, dbXml);
		Element tagID = (Element)dbDoc.getElementsByTagName(DatabaseTag.DB).item(0);
		return Integer.valueOf(tagID.getAttribute(DatabaseTag.DB_ID));
	}
	
	//Get the design of an application
	public Document getDesign(String AppId, String AppVer, String AppBuild, String SubId, String login, String pwd) {
		if (sendDesign) {
			Log.i("info", "download design xml");
			String getDesign = "act=getdesign" + this.generateRegularUrlRequest(AppId, AppVer, AppBuild, SubId, login, pwd);
			String designStream = SendPost(getDesign, STRING);
			Log.i("info", "get design ");
			StreamToFile(designStream, design_XML);
			return CreateParseDocument(designStream, null);
		}
		else {
			Log.i("info", "start parsing design file");
			return CreateParseDocument(null, new File(design_XML));
		}
	}
	
	//Hashmap contains resource id and its hashcode
	public HashMap<String, String> getResourceMap(String value) {
		HashMap<String, String> resourceMapFile = new HashMap<String, String>();
		if (checkFileExist(resources_XML)) {
			Document resuourcesFileDoc = CreateParseDocument(null, new File(resources_XML));
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
					}
					else if (value.equals("ext")) {
						resourceMapFile.put(resource.getAttribute(RessourceTag.RESOURCES_R_ID), 
								resource.getAttribute(RessourceTag.RESOURCES_R_EXT));
					}
				}
			}
		}
		return resourceMapFile;
	}
	
	//Get the resources of an application
	public void getResources(String AppId, String AppVer, String AppBuild, String SubId, String login, String pwd) {
		HashMap<String, String> resourceMapFile = getResourceMap("hashcode");
		String getResources = "act=getresources" + this.generateRegularUrlRequest(AppId, AppVer, AppBuild, SubId, login, pwd);
		String resourcesStream = SendPost(getResources, STRING);
		Document resourceDocument = CreateParseDocument(resourcesStream, null);
		if (resourceDocument.getElementsByTagName(RessourceTag.RESOURCES_RL).getLength() > 0) {
			NodeList resourceList = resourceDocument.getElementsByTagName(RessourceTag.RESOURCES_RL).item(0).getChildNodes();
			int resourceLen = resourceList.getLength();
			
			for (int i=0; i<resourceLen; i++) {
				Element resource = (Element) resourceList.item(i);
				if (resource.hasAttribute(RessourceTag.RESOURCES_R_ID)) {
					String resourceId = resource.getAttribute(RessourceTag.RESOURCES_R_ID);
					String fileName = imageFilePath+resource.getAttribute(RessourceTag.RESOURCES_R_ID)+"."+
					resource.getAttribute(RessourceTag.RESOURCES_R_EXT);
					String getResource = "act=getresource" + this.generateRegularUrlRequest(AppId, AppVer, AppBuild, SubId, login, pwd) + "&resourceid="+
					resource.getAttribute(RessourceTag.RESOURCES_R_ID);
					if ((resourceMapFile.containsKey(resourceId)) && (checkFileExist(fileName))) {
						if (!resourceMapFile.get(resourceId).equals(resource.getAttribute(RessourceTag.RESOURCES_R_HASHCODE))) {
							Log.i("info", "download repalce image");
							new File(fileName).delete();
							String resourceStream = SendPost(getResource, IMAGE);
							StreamToFile(resourceStream, fileName);
						}
					}
					else {
						Log.i("info", "download image");
						String resourceStream = SendPost(getResource, IMAGE);
						StreamToFile(resourceStream, fileName);
					}
				}
			}
			StreamToFile(resourcesStream, resources_XML);
		}
	}
	
	//Get the DB of an application
	public Document getDB(String AppId, String AppVer, String AppBuild, String SubId, String login, String pwd) {
		Log.i("info", "sendDb "+sendDb);
		if (sendDb) {
			String getDB = "act=getdb" + this.generateRegularUrlRequest(AppId, AppVer, AppBuild, SubId, login, pwd);
			String dbStream = SendPost(getDB, STRING);
			Log.i("info", "dbstream ");
			StreamToFile(dbStream, db_XML);
			return CreateParseDocument(dbStream, null);
		}
		else {
			return CreateParseDocument(null, new File(db_XML));
		}
	}
	
	//Get the behavior of an application
	public Document getBehavior(String AppId, String AppVer, String AppBuild, String SubId, String login, String pwd) {
		if (sendBehavior) {
			String getBehavior = "act=getbehavior" + this.generateRegularUrlRequest(AppId, AppVer, AppBuild, SubId, login, pwd);
			String behaviorStream = SendPost(getBehavior, STRING);
			StreamToFile(behaviorStream, behavior_XML);
			return CreateParseDocument(behaviorStream, null);
		}
		else {
			return CreateParseDocument(null, new File(behavior_XML));
		}
	}
	
	//Import data
	@SuppressWarnings("unchecked")
	public boolean importData(String AppId, String DbId, String login, String pwd, ArrayList<String> tables, Object filters) {
		boolean result = false;
		String ask = "act=ask" + this.generateSyncUrlRequest(AppId, DbId, login, pwd);
		String getBlob = "act=getblob" + this.generateSyncUrlRequest(AppId, DbId, login, pwd);
		String report = "act=rep" + this.generateSyncUrlRequest(AppId, DbId, login, pwd);
		
		if (filters != null) {
			int filtersSize = ((ArrayList<?>)filters).size();
			if (filtersSize > 0) {
				ask += "&fcount="+filtersSize;
				for (int i=0; i<filtersSize; i++) {
					ArrayList<Object> filter = (ArrayList<Object>)((ArrayList<Object>)filters).get(i);
					ask += "&ff"+i+"="+filter.get(0).toString();
					Object operator = Function.getOperator(((ArrayList<?>)filters).get(1));
					ask += "&fo"+i+"="+urlEncode(operator.toString());
					ask += "&fv"+i+"="+filter.get(2).toString();
				}
			}
		}
		Log.i("info", "action ask "+ask);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DmaHttpBinarySync importSync = null;
		try {
			if (tables != null) {
				int tablesNb = tables.size();
				baos.write(Binary.intToByteArray(tablesNb));
				for (int i=0; i<tablesNb; i++) {
					baos.write(Binary.intToByteArray(Integer.valueOf(tables.get(i))));
				}
			}
			else {
				baos.write(Binary.intToByteArray(DatabaseAdapter.getTableNb()));
				for (String s : DatabaseAdapter.getTableIds()) {
					baos.write(Binary.intToByteArray(Integer.valueOf(s)));
				}
			}
			byte[] inputbytes = baos.toByteArray();
			importSync = new DmaHttpBinarySync(url.toString(), ask, getBlob, report, inputbytes, "Import");
			result = importSync.run();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//Export local data to server
	public boolean exportData(String AppId, String DbId, String login, String pwd, ArrayList<String> tables, Object filters) {
	//public boolean launchExport(String AppId, String DbId, String login, String pwd, ArrayList<String> tables, Object filters) {
		String sync = "act=sync" + this.generateSyncUrlRequest(AppId, DbId, login, pwd);
		String send = "act=send" + this.generateSyncUrlRequest(AppId, DbId, login, pwd);
		String commit = "act=commit" + this.generateSyncUrlRequest(AppId, DbId, login, pwd);
		
		byte[] exportData = ApplicationView.getDataBase().syncExportTable(tables, filters);
		Log.i("info", "exportData "+exportData.length);
		DmaHttpBinarySync exportSync = new DmaHttpBinarySync(url.toString(), sync, send, commit, exportData, "Export");
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
		return "&from=runtime&appid="+AppId+"&dataid="+DbId+"&login="+login+"&passwd_md5="+md5(pwd)+"&stream=1&useragent=ANDROID&did="+Dma.getDeviceID();
	}
	
	private String generateRegularUrlRequest(String AppId, String AppVer, String AppBuild, String SubId, String login, String pwd) {
		return "&from=runtime&appid="+AppId+"&appversion="+AppVer+"&appbuild="+AppBuild+"&subid="+SubId+"&did="+Dma.getDeviceID()+"&login="+login+"&passwd_md5="+md5(pwd)+"&useragent=ANDROID";
	}
	
	//Save the downloaded xml stream to file
	private void StreamToFile(String stream, String filePath) {
        File file = new File(filePath);
        FileOutputStream fos;
        try{
                fos = new FileOutputStream(file);
                DataOutputStream dos = new DataOutputStream(fos);
                dos.writeBytes(stream);
                Log.i("info", "file create ok "+filePath);
        }
        catch (FileNotFoundException e)
        {e.printStackTrace();}
        catch (IOException e)
        {e.printStackTrace();} 
	}
	
	//Check if file exists and its length is great than 0
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
		} 
		catch (NoSuchAlgorithmException e) {
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
