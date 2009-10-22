package com.penbase.dma.Constant;

import android.os.Environment;

public class Constant {
	//Package name
	public static final String APPPACKAGE = Environment.getExternalStorageDirectory() + "/Dalyo/";
	
	//User directory
	public static final String USERDIRECTORY = "user/";
	
	//Database directory
	public static final String DATABASEDIRECTORY = "data/";
	public static final String SYSTEMTABLE = "system";
	public static final String CREATE_SYSTEMTABLE = "CREATE TABLE system (ID VARCHAR(255), Username VARCHAR(255), " +
			"Userpassword VARCHAR(255), Rememberme VARCHAR(255), Applicationlist TEXT)";
	
	//Application database
	public static final String APPDB = "db";
	
	//Text alignment
	public static final String LEFT = "left";
	public static final String CENTER = "center";
	public static final String RIGHT = "right";
	
	//Font size
	public static final String SMALL = "small";
	public static final String BIG = "big";
	public static final String EXTRA = "extra";
	
	//Font type
	public static final String ITALIC = "italic";
	public static final String BOLD = "bold";
	public static final String UNDERLINE = "underline";
	public static final String ITALICBOLD = "italicbold";
	
	//Server name
	//public static final String SERVER = "http://my.dalyo.com/server/com.penbase.arbiter.Arbiter?";
	//public static final String SECUREDSERVER = "https://my.dalyo.com/server/com.penbase.arbiter.Arbiter?";
	
	//Local server
	public static final String SERVER = "http://192.168.0.1/server/com.penbase.arbiter.Arbiter?";
	//public static final String SECUREDSERVER = "http://192.168.0.1/server/com.penbase.arbiter.Arbiter?";
	
	public static final String FALSE = "false";
	public static final String TRUE = "true";
	
	public static final String EMPTY_STRING = "";
	
	public static int INTBYTE = 4;	
	public static int TYPEBYTE = 1;
	
	//Xml files
	public static final String DBXML = "db.xml";
	public static final String DESIGNXML = "design.xml";
	public static final String BEHAVIORXML = "behavior.xml";
	public static final String RESOURCEXML = "resources.xml";
	
	//Resource directory
	public static final String RESOURCE = "resource/";
	
	public static final String IMPORTACTION = "import";
	public static final String EXPORTACTION = "export";
	
	//Locales
	public static final String FRENCH = "fr";
	
	//Blob table
	public static final String BLOBTABLE = "Table_Blob";
	public static final String CREATE_BLOBTABLE = "CREATE TABLE Table_Blob (File VARCHAR, Data BLOB)";
	public static final String BLOBFILE = "File";
	public static final String BLOBDATA = "Data";
	
	//Temporary directory for images (doodle or pictureBox)
	public static final String TEMPDIRECTORY = "temp/";
	
	//Attribute value
	public static final String TRIGGERMAIL = "mail";
	public static final String TRIGGERPHONE = "phone";
	public static final String TRIGGERURL = "url";
	public static final String POSITIVENUMERIC = "positivenumeric";
	public static final String NONE = "none";
	
	//Preference
	public static final String PREFERENCE = "preference";
	public static final String DEVICEID = "deviceid";
	public static final String VERSION = "version";
}