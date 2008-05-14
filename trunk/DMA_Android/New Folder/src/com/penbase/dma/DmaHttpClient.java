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
package com.penbase.dma;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.penbase.dma.xml.XmlTag;

import android.app.Activity;
import android.util.Log;

public class DmaHttpClient extends Activity {
	private URL url;
	private int lastError = 0;
	
	//Booleans of sending requests	
	private boolean sendBehavior = true;
	private boolean sendDb = true;
	private boolean sendDesign = true;
	
	//XML files	
	private static final String db_XML = "/data/misc/location/db.xml";
	private static final String design_XML = "/data/misc/location/design.xml";
	private static final String behavior_XML = "/data/misc/location/behavior.xml";	
	private static final String resources_XML = "/data/misc/location/resources.xml";	
	
	//Image file's path
	private static final String imageFilePath = "/data/misc/location/";
	
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
		return SendPost("act=login&login=" + login + "&passwd=" + password + "&useragent=ANDROID");
	}
	
	private String SendPost(String parameters) 
	{
		String response = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
			System.err.println("response "+response);
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
		return response.substring(response.indexOf('\n') + 1, response.length());
	}
	
	private int GetResponseCode(String response) 
	{	
		return Integer.parseInt(response.substring(0, response.indexOf('\n')));
	}
	
	//Create a parsing object from a string stream
	public static Document CreateParseDocument(String xmlStream)
	{
		Log.i("info", "from xml stream");
		
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
		Log.i("info", "from xml file");
		
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
	public void checkDownloadFile(int position)
	{
		if ((new File(db_XML).exists()) && (new File(db_XML).length() > 0))
		{			
			sendDb = false;			
		}
		else if ((new File(design_XML).exists()) && (new File(design_XML).length() > 0))
		{
			sendDesign = false;
		}
		else if ((new File(behavior_XML).exists()) && (new File(behavior_XML).length() > 0))
		{
			sendBehavior = false;
		}		
	}
	
	//Get the design of an application
	public Document getDesign(int AppId, int AppVer, int AppBuild, int SubId, String login, String pwd)
	{		
		if (sendDesign)
		{
			Log.i("info", "start parsing design");
			String getDesign = "act=getdesign&from=runtime&appid="+AppId+"&appversion="+AppVer+"&appbuild="+
			AppBuild+"&subid="+SubId+"&login="+login+"&passwd="+pwd+"&useragent=ANDROID";		
			String designStream = SendPost(getDesign);
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
	
	//Get the resources of an application
	public void getResources(int AppId, int AppVer, int AppBuild, int SubId, int DbId, String login, String pwd)
	{		
		String getResources = "act=getresources&from=runtime&appid="+AppId+"&appversion="+AppVer+
		"&appbuild="+AppBuild+"&subid="+SubId+"&did="+DbId+"&login="+login+"&passwd="+
		pwd+"&useragent=ANDROID";
		
		Log.i("info", "getresources "+getResources);
		
		String resourcesStream = SendPost(getResources);
		
		Log.i("info", "resources "+resourcesStream);
		
		StreamToFile(resourcesStream, resources_XML);		
		
		Document resourceDocument = CreateParseDocument(resourcesStream);
		NodeList resourceList = resourceDocument.getElementsByTagName(XmlTag.TAG_RESOURCES_RL).item(0).getChildNodes();
		int resourceLen = resourceList.getLength();				
		
		for (int i=0; i<resourceLen; i++)
		{
			Element resource = (Element) resourceList.item(i);
			if (resource.hasAttribute(XmlTag.TAG_RESOURCES_R_ID))
			{
				String fileName = imageFilePath+resource.getAttribute(XmlTag.TAG_RESOURCES_R_ID)+".jpg";
				if (!new File(fileName).exists())
				{
					Log.i("info", "download image");
					
					String getResource = "act=getresource&from=runtime&appid="+AppId+"&appversion="+AppVer+
					"&appbuild="+AppBuild+"&subid="+SubId+"&did="+DbId+"&resourceid="+
					resource.getAttribute(XmlTag.TAG_RESOURCES_R_ID)+"&login=beausejour&passwd=iupgmi&useragent=ANDROID";
					
					String resourceStream = SendPost(getResource);
					StreamToFile(resourceStream, fileName);
				}				
			}
		}
	}
	
	//Get the DB of an application
	public Document getDB(int AppId, int AppVer, int AppBuild, int SubId, String login, String pwd)
	{
		if (sendDb)
		{
			String getDB = "act=getdb&from=runtime&appid="+AppId+"&appversion="+AppVer+"&appbuild="+
			AppBuild+"&subid="+SubId+"&login="+login+"&passwd="+pwd+"&useragent=ANDROID";
			String dbStream = SendPost(getDB);
			
			Log.i("info", "db "+dbStream);
			
			StreamToFile(dbStream, db_XML);		
			
			return CreateParseDocument(dbStream);
		}
		else
		{
			return CreateParseDocument(new File(db_XML));
		}
	}
	
	//Get the behavior of an application	
	public Document getBehavior(int AppId, int AppVer, int AppBuild, int SubId, int DbId, String login, String pwd)
	{	
		if (sendBehavior)
		{					
			String getBehavior = "act=getbehavior&from=runtime&appid="+AppId+"&appversion="+AppVer+
			"&appbuild="+AppBuild+"&subid="+SubId+"&did="+DbId+"&login="+login+"&passwd="+pwd+"&useragent=ANDROID";
			
			Log.i("info", "getbehavior "+getBehavior);
			
			String behaviorStream = SendPost(getBehavior);
			
			Log.i("info", "behavior "+behaviorStream);
			
			StreamToFile(behaviorStream, behavior_XML);		
			
			return CreateParseDocument(behaviorStream);
		}
		else
		{
			return CreateParseDocument(new File(behavior_XML));
		}
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
			Log.i("info", "file create ok");
		}
		catch (FileNotFoundException e) 
		{e.printStackTrace();}
		catch (IOException e) 
		{e.printStackTrace();}		
	}
}
