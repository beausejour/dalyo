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
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.penbase.dma.Dma;
import com.penbase.dma.Binary.Binary;
import com.penbase.dma.Dalyo.Database;
import com.penbase.dma.XmlElement.XmlTag;

import android.app.Activity;
import android.security.MessageDigest;
import android.util.Log;

public class DmaHttpClient extends Activity {
	private URL url;
	private int lastError = 0;
	private int errorCode = 0;
	
	private static final String STRING="string";
	private static final String BYTE="byte";
	private static final String IMAGE="image";
	
	//Booleans of sending requests	
	private boolean sendBehavior = true;
	private boolean sendDb = true;
	private boolean sendDesign = true;
	
	//XML files	
	public static final String db_XML = "/data/misc/location/db.xml";
	public static final String design_XML = "/data/misc/location/design.xml";
	public static final String behavior_XML = "/data/misc/location/behavior.xml";	
	public static final String resources_XML = "/data/misc/location/resources.xml";	
	
	//Image file's path
	private static final String imageFilePath = "/data/misc/location/";
	
	private static byte[] importresult = null;
	private byte[] exportresult = null;
	
	public DmaHttpClient() 
	{
		try 
		{
			url = new URL("http://192.168.0.1/server/com.penbase.arbiter.Arbiter");
			//url = new URL("http://www.dalyo.com/server/com.penbase.arbiter.Arbiter");
		}
		catch (MalformedURLException e) 
		{e.printStackTrace();}
	}

	public int GetLastError() {
		return lastError;
	}

	public String Authentication(String login, String password) 
	{
		return SendPost("act=login&login=" + login + "&passwd=" + password + "&useragent=ANDROID", STRING);
	}
	
	private String SendPost(String parameters, String type) 
	{
		String result = null;
		StringBuilder response = null;
		try 
		{
			HttpURLConnection connection = null;			
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			PrintStream out = new PrintStream(connection.getOutputStream());
			out.print(parameters);
			out.close();
			
			// Get the input stream and read response
			InputStream in =  connection.getInputStream();				
			
			if (type.equals(STRING))
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(in), 10);
		        response = new StringBuilder();
		        String line = null;	        
	        	int lineNumber = 1;
	            while ((line = reader.readLine()) != null) 
	            {
	            	if (lineNumber == 1)
	            	{
	            		errorCode = Integer.valueOf(line);
	            		lineNumber++;
	            	}
	            	else
	            	{
	            		response.append(line);
	            	}
	            }
	            in.close();
	            result = response.toString();
			}
			else if (type.equals(IMAGE))
			{
				Log.i("info", "length "+in.available());
				StringBuffer buf = new StringBuffer();
				int c;
			    while ((c = in.read()) != -1)
			    {
			    	buf.append((char) c);
			    }
			    in.close();
			    result = buf.toString();
			    errorCode = Integer.parseInt(result.substring(0, result.indexOf('\n')));
			    result = result.substring(result.indexOf('\n') + 1, result.length());
			}
		}
		catch (ProtocolException pe) 
		{System.err.println("HTTPExample: ProtocolException; " + pe.getMessage());}
		catch (IOException ioe) 
		{System.err.println("HTTPExample: IOException; " + ioe.getMessage());}
		
		Log.i("info", "Server returned: " + errorCode);

		if (errorCode != 200)
		{
			new Dma().errorDialog("Connection failed");
			return null;
		}
		else
		{
			return result;
		}		
	}	
	
	//Create a parsing object from a string stream
	public static Document CreateParseDocument(String xmlStream)
	{
		DocumentBuilder docBuild = null;
		Document document = null;				
		ByteArrayInputStream stream = null;
		try 
		{
			docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			stream = new ByteArrayInputStream(xmlStream.getBytes());
			document = docBuild.parse(stream);			
		}
		catch (SAXException e) 
		{e.printStackTrace();}
		catch (IOException e) 
		{e.printStackTrace();}
		catch (ParserConfigurationException e)
		{e.printStackTrace();}		
		
		return document;		
	}
	
	//Create a parsing object from a xml file
	public static Document CreateParseDocument(File xmlFile)
	{		
		DocumentBuilder docBuild; 
		Document document = null;		
		try 
		{			
			docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = docBuild.parse(xmlFile);
		}
		catch (SAXException e) 
		{e.printStackTrace();}
		catch (IOException e) 
		{e.printStackTrace();}
		catch (ParserConfigurationException e)
		{e.printStackTrace();}
	
		return document;
	}
	
	//Check if we need to download all xml files
	public void checkDownloadFile(int position, String login, String pwd)
	{		
		String loginStream = SendPost("act=login&login="+login+"&passwd="+pwd+"&useragent=ANDROID", STRING);
		Document parserLogin = CreateParseDocument(loginStream);				
		NodeList list = parserLogin.getElementsByTagName("a");		
		Element element = (Element) list.item(position);
		NodeList idList = element.getChildNodes();
		int idLen = idList.getLength();
		
		int AppId = 0;
		int AppVer = 0;
		int AppBuild = 0;
		int SubId = 0;
		int DbId = 0;
		
		for (int i=0; i<idLen; i++)					
		{			
			if (idList.item(i).getNodeName().equals(XmlTag.TAG_LOGIN_ID))
			{
				AppId = Integer.parseInt(idList.item(i).getChildNodes().item(0).getNodeValue().trim());
			}
			else if (idList.item(i).getNodeName().equals(XmlTag.TAG_LOGIN_VER))
			{
				AppVer =  Integer.parseInt(idList.item(i).getChildNodes().item(0).getNodeValue().trim());
			}
			else if (idList.item(i).getNodeName().equals(XmlTag.TAG_LOGIN_BLD))
			{
				AppBuild = Integer.parseInt(idList.item(i).getChildNodes().item(0).getNodeValue().trim());
			}
			else if (idList.item(i).getNodeName().equals(XmlTag.TAG_LOGIN_SUB))
			{
				SubId = Integer.parseInt(idList.item(i).getChildNodes().item(0).getNodeValue().trim());
			}
			else if (idList.item(i).getNodeName().equals(XmlTag.TAG_LOGIN_DBID))
			{
				DbId = Integer.parseInt(idList.item(i).getChildNodes().item(0).getNodeValue().trim());
			}
		}
		Log.i("info", "before if");
		if ((AppId == Integer.valueOf(Dma.applicationList.get(position).getAppId())) && 
				(SubId == Integer.valueOf(Dma.applicationList.get(position).getSubId())) &&
				(checkFileExist(db_XML)) &&	(AppId == getIdDb(new File(db_XML))))
		{
			if (AppVer == Integer.valueOf(Dma.applicationList.get(position).getAppVer()))
			{
				/*this.sendDesign = false;
				Log.i("info", "don't send design request");*/
			}
			if ((AppBuild == Integer.valueOf(Dma.applicationList.get(position).getAppBuild())) &&
					(AppVer == Integer.valueOf(Dma.applicationList.get(position).getAppVer())))
			{
				this.sendDesign = false;
				Log.i("info", "don't send design request");
				this.sendBehavior = false;
				Log.i("info", "don't send behavior request");
			}
			if (DbId == Integer.valueOf(Dma.applicationList.get(position).getDbId()))
			{
				this.sendDb = false;
				Log.i("info", "don't send database request");
			}
		}
	}
	
	public int getIdDb(File dbXml)
	{
		Log.i("info", "getiddb");
		Document dbDoc = CreateParseDocument(dbXml);
		Element tagID = (Element)dbDoc.getElementsByTagName(XmlTag.TAG_DB).item(0);
		return Integer.valueOf(tagID.getAttribute(XmlTag.TAG_DB_ID));
	}
	
	//Get the design of an application
	public Document getDesign(String AppId, String AppVer, String AppBuild, String SubId, String login, String pwd)
	{		
		if (sendDesign)
		{
			Log.i("info", "start parsing design");
			String getDesign = "act=getdesign&from=runtime&appid="+AppId+"&appversion="+AppVer+"&appbuild="+
			AppBuild+"&subid="+SubId+"&login="+login+"&passwd="+pwd+"&useragent=ANDROID";		
			String designStream = SendPost(getDesign, STRING);
			Log.i("info", "design "+designStream);
			StreamToFile(designStream, design_XML);
			return CreateParseDocument(designStream);
		}
		else
		{
			Log.i("info", "start parsing design file");
			return CreateParseDocument(new File(design_XML));
		}	
	}
	
	//Hashmap contains resource id and its hashcode
	public HashMap<String, String> getResourceMap(String value)
	{
		Log.i("info", "getREsourcemap");
		HashMap<String, String> resourceMapFile = new HashMap<String, String>();
		if (checkFileExist(resources_XML))
		{			
			Document resuourcesFileDoc = CreateParseDocument(new File(resources_XML));			
			Log.i("info", "resourcexml exits "+resuourcesFileDoc.getElementsByTagName(XmlTag.TAG_RESOURCES_RL).item(0).getChildNodes());
			NodeList resourceFileList = resuourcesFileDoc.getElementsByTagName(XmlTag.TAG_RESOURCES_RL).item(0).getChildNodes();			
			int resourceFileLen = resourceFileList.getLength();		
			for (int i=0; i<resourceFileLen; i++)
			{
				Element resource = (Element) resourceFileList.item(i);
				if ((resource.hasAttribute(XmlTag.TAG_RESOURCES_R_ID)) && 
						(resource.hasAttribute(XmlTag.TAG_RESOURCES_R_HASHCODE)))
				{
					if (value.equals("hashcode"))
					{
						resourceMapFile.put(resource.getAttribute(XmlTag.TAG_RESOURCES_R_ID), 
								resource.getAttribute(XmlTag.TAG_RESOURCES_R_HASHCODE));
					}
					else if (value.equals("ext"))
					{
						resourceMapFile.put(resource.getAttribute(XmlTag.TAG_RESOURCES_R_ID), 
								resource.getAttribute(XmlTag.TAG_RESOURCES_R_EXT));
					}
				}
			}
		}		
		return resourceMapFile;
	}
	
	//Get the resources of an application
	public void getResources(String AppId, String AppVer, String AppBuild, String SubId, String login, String pwd)
	{
		HashMap<String, String> resourceMapFile = getResourceMap("hashcode");		
		
		String getResources = "act=getresources&from=runtime&appid="+AppId+"&appversion="+AppVer+
		"&appbuild="+AppBuild+"&subid="+SubId+"&did="+Dma.getDeviceID()+"&login="+login+"&passwd="+
		pwd+"&useragent=ANDROID";
		String resourcesStream = SendPost(getResources, STRING);		
		Document resourceDocument = CreateParseDocument(resourcesStream);
		NodeList resourceList = resourceDocument.getElementsByTagName(XmlTag.TAG_RESOURCES_RL).item(0).getChildNodes();
		int resourceLen = resourceList.getLength();
		
		for (int i=0; i<resourceLen; i++)
		{
			Element resource = (Element) resourceList.item(i);
			if (resource.hasAttribute(XmlTag.TAG_RESOURCES_R_ID))
			{
				Log.i("info", "ready to download");
				String resourceId = resource.getAttribute(XmlTag.TAG_RESOURCES_R_ID);
				String fileName = imageFilePath+resource.getAttribute(XmlTag.TAG_RESOURCES_R_ID)+"."+
				resource.getAttribute(XmlTag.TAG_RESOURCES_R_EXT);
				String getResource = "act=getresource&from=runtime&appid="+AppId+"&appversion="+AppVer+
				"&appbuild="+AppBuild+"&subid="+SubId+"&did="+Dma.getDeviceID()+"&resourceid="+
				resource.getAttribute(XmlTag.TAG_RESOURCES_R_ID)+"&login="+login+"&passwd="+pwd+"&useragent=ANDROID";
				
				Log.i("info", "resourceId "+resourceId);
				if ((resourceMapFile.containsKey(resourceId)) && (checkFileExist(fileName)))
				{
					Log.i("info", "contain key");

					if (!resourceMapFile.get(resourceId).equals(resource.getAttribute(XmlTag.TAG_RESOURCES_R_HASHCODE)))
					{
						Log.i("info", "download repalce image");

						new File(fileName).delete();
	
						String resourceStream = SendPost(getResource, IMAGE);
						StreamToFile(resourceStream, fileName);
					}
				}
				else
				{
					Log.i("info", "download image");
					String resourceStream = SendPost(getResource, IMAGE);
					StreamToFile(resourceStream, fileName);
				}
			}
		}
		
		StreamToFile(resourcesStream, resources_XML);
	}
	
	//Get the DB of an application
	public Document getDB(String AppId, String AppVer, String AppBuild, String SubId, String login, String pwd)
	{
		if (sendDb)
		{
			String getDB = "act=getdb&from=runtime&appid="+AppId+"&appversion="+AppVer+"&appbuild="+
			AppBuild+"&subid="+SubId+"&login="+login+"&passwd="+pwd+"&useragent=ANDROID";
			String dbStream = SendPost(getDB, STRING);
			StreamToFile(dbStream, db_XML);		
			
			return CreateParseDocument(dbStream);
		}
		else
		{
			return CreateParseDocument(new File(db_XML));
		}
	}
	
	//Get the behavior of an application	
	public Document getBehavior(String AppId, String AppVer, String AppBuild, String SubId, String login, String pwd)
	{	
		if (sendBehavior)
		{					
			String getBehavior = "act=getbehavior&from=runtime&appid="+AppId+"&appversion="+AppVer+
			"&appbuild="+AppBuild+"&subid="+SubId+"&did="+Dma.getDeviceID()+"&login="+login+"&passwd="+pwd+"&useragent=ANDROID";
			
			Log.i("info", "getbehavior "+getBehavior);
			
			String behaviorStream = SendPost(getBehavior, STRING);
			
			Log.i("info", "behavior "+behaviorStream);
			
			StreamToFile(behaviorStream, behavior_XML);		
			
			return CreateParseDocument(behaviorStream);
		}
		else
		{
			return CreateParseDocument(new File(behavior_XML));
		}
	}
	
	//Import server data
	public byte[] launchImport(String AppId, String DbId, String login, String pwd) throws IOException, InterruptedException
	{
		String ask = "act=ask&from=runtime&appid="+AppId+"&dataid="+DbId+"&login="+login+"&passwd="+pwd+
		"&stream=1&useragent=ANDROID&did="+Dma.getDeviceID();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(Binary.intToByteArray(Database.getTableNb()));
		for (String s : Database.getTableIds())
		{
			bos.write(Binary.intToByteArray(Integer.valueOf(s)));
		}
		byte[] inputbytes = bos.toByteArray();
		DmaHttpBinarySync dhb = new DmaHttpBinarySync(DmaHttpClient.this, ask, inputbytes);
		dhb.start();
		dhb.join();
		return getImportResult();
	}
	
	//// interface
	public void importEnded(int code, byte [] bytes) 
	{
		if (code == 200)
		{
			setImportResult(bytes);
		}
	}
	
	public static byte[] getImportResult()
	{
		return importresult;
	}
	
	private void setImportResult(byte[] ir)
	{
		importresult = ir;
	}
	
	//Save the download xml stream to file
	private void StreamToFile(String stream, String filePath)
	{
		File file = new File(filePath);
		FileOutputStream fos;
		try 
		{
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
	private boolean checkFileExist(String fileName)
	{
		Log.i("info", "check file exists "+fileName);
		boolean result = false;
		if (new File(fileName).exists())
		{
			if (new File(fileName).length() > 0)
			{
				result = true;
			}			
		}
		return result;
	}
	
	//Get errorcode
	public int getErrorCode()
	{
		return errorCode;
	}
	
	public static String getBoundary()
	{
		String result = "";
	    try 
	    {
			MessageDigest digest = android.security.MessageDigest.getInstance("MD5");
			String hex = Long.toHexString(System.currentTimeMillis());			
			digest.update(hex.getBytes());
			
			byte[] hash = digest.digest();
			
			//result = hash.toString();
			result = hex;
		}
	    catch (NoSuchAlgorithmException e) 
	    {
			e.printStackTrace();
		}
		return result;		
	}
}
