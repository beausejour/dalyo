package com.penbase.dma.Dalyo;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.penbase.dma.xml.XmlTag;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {
	private final Document dbDocument;
	private final Context context;
	private static SQLiteDatabase sqlite = null;	
	private static final String DATABASE_NAME = "data";
	
	private int DbId;	
	
	public Database(Context c, Document d)
	{
		this.context = c;
		this.dbDocument = d;
				
		createDatabase();
	}	
	
	public void createDatabase() throws SQLException
	{
		try
		{			
			//sqlite = context.createDatabase(DATABASE_NAME, 1, 0, null);
			context.createDatabase(DATABASE_NAME, 1, 0, null);
			Log.i("info", "create database");
			
			sqlite = context.openDatabase(DATABASE_NAME, null);
			Log.i("info", "open database");
		
			//deleteTable();
			createTable();
		}
		catch (FileNotFoundException e) 
		{e.printStackTrace();}
	}
	
	public void create()
	{
		String create = "create table test (id integer primary key ," +
				"title text);";
				
		sqlite.execSQL(create);
		
		String insert = "insert into test (id, title) values('1', 'dfsdsfdsfds');";
		sqlite.execSQL(insert);
		
		Log.i("info", "done");
	}
	
	public void createTable()
	{
		Element tagID = (Element)dbDocument.getElementsByTagName(XmlTag.TAG_DB).item(0);
		DbId = Integer.valueOf(tagID.getAttribute(XmlTag.TAG_DB_ID));
		
		NodeList tableList = dbDocument.getElementsByTagName(XmlTag.TAG_TABLE);
		int tableLen = tableList.getLength();
		
		Log.i("info", "table number "+tableLen);
		
		ArrayList<String> tables = new ArrayList<String>();
		ArrayList<String> fields = new ArrayList<String>();
		
		for (int i=0; i<tableLen; i++)
		{
			//String createquery = "CREATE TABLE ";
			String createquery = "CREATE TABLE IF NOT EXISTS ";
		    /*private static final String DATABASE_CREATE =
		        "create table notes (_id integer primary key autoincrement, "
		            + "title text not null, body text not null);";*/						
			
		    Element table = (Element) tableList.item(i);
		    String tableName = table.getAttribute(XmlTag.TAG_TABLE_NAME);		    
		    String tableSync = table.getAttribute(XmlTag.TAG_TABLE_SYNC);
		    int tableId = Integer.valueOf(table.getAttribute(XmlTag.TAG_TABLE_ID));
		    
		    Log.i("info", "value of i "+i+" element name "+tableName);
		    
		    tables.add(tableId, tableName);
		    
		    createquery += tableName+" (";
		    
		    NodeList fieldList = table.getChildNodes();
		    int fieldLen = fieldList.getLength();
		    
		    ArrayList<String> fieldHasFk = new ArrayList<String>();
		    ArrayList<String> fkTableList = new ArrayList<String>();
		    ArrayList<String> fkFieldList = new ArrayList<String>();
		    
		    if (fieldLen > 0)
		    {		    	
		    	for (int j=0; j<fieldLen; j++)
		    	{
		    		Element field = (Element) fieldList.item(j);
		    		int fieldId = Integer.valueOf(field.getAttribute(XmlTag.TAG_FIELD_ID));
		    		String fieldName = field.getAttribute(XmlTag.TAG_FIELD_NAME);
		    		String fieldType = field.getAttribute(XmlTag.TAG_FIELD_TYPE);
		    		String fieldSize = field.getAttribute(XmlTag.TAG_FIELD_SIZE);
		    		
		    		if (fieldType.equals("VARCHAR"))
		    		{
		    			fieldType = fieldType+"("+fieldSize+")";
		    		}
		    		
		    		String fieldSync = field.getAttribute(XmlTag.TAG_FIELD_SYNC);
		    		
		    		if (field.hasAttribute(XmlTag.TAG_FIELD_PK))
		    		{
		    			if (field.hasAttribute(XmlTag.TAG_FIELD_PK_AUTO))
		    			{
		    				createquery += fieldName+" "+fieldType+" PRIMARY KEY AUTOINCREMENT, ";
		    			}
		    			else
		    			{
		    				createquery += fieldName+" "+fieldType+" PRIMARY KEY,";
		    			}		    			
		    		}
		    		
		    		else if ((field.hasAttribute(XmlTag.TAG_FIELD_FORIEIGNTABLE)) 
		    				&& (field.hasAttribute(XmlTag.TAG_FIELD_FORIEIGNFIELD)))
		    		{
		    			int foreignTableId = Integer.valueOf(field.getAttribute(XmlTag.TAG_FIELD_FORIEIGNTABLE));
		    			int foreignFieldId = Integer.valueOf(field.getAttribute(XmlTag.TAG_FIELD_FORIEIGNFIELD));
		    			Element foreignTable = (Element)tableList.item(foreignTableId);
		    			Element foreignField =  (Element)foreignTable.getChildNodes().item(foreignFieldId);
		    			String foreignTableName = foreignTable.getAttribute(XmlTag.TAG_TABLE_NAME);
		    			String foreignFieldName = foreignField.getAttribute(XmlTag.TAG_FIELD_NAME);
		    			
		    			createquery += fieldName+" "+fieldType+", ";
		    			
		    			fieldHasFk.add(fieldName);
		    			fkTableList.add(foreignTableName);
		    			fkFieldList.add(foreignFieldName);		    					    			
		    		}
		    		else
		    		{
		    			createquery += fieldName+" "+fieldType+", ";
		    		}
		    	}
		    }
		    
		    if (fieldHasFk.size() > 0)
		    {
		    	int listSize = fieldHasFk.size();
		    	
		    	for (int k=0; k<listSize; k++)
		    	{
		    		createquery += " FOREIGN KEY ("+fieldHasFk.get(k)+") REFERENCES "+fkTableList.get(k)+"("+fkFieldList.get(k)+"), ";		    		
		    	}
		    }
		    
		    createquery = createquery.substring(0, createquery.length()-2);
		    createquery += ");";
		    
		    Log.i("info", "query "+createquery);
		    
			sqlite.execSQL(createquery);
		}
	}
	
	public void updateTable()
	{		
			
	}
	
	public void deleteTable()
	{
		sqlite.execSQL("DROP TABLE IF EXISTS CommandeProduit");
		sqlite.execSQL("DROP TABLE IF EXISTS PRODUIT");
		sqlite.execSQL("DROP TABLE IF EXISTS COMMANDE");
		sqlite.execSQL("DROP TABLE IF EXISTS CLIENT");
		sqlite.execSQL("DROP TABLE IF EXISTS Table_2");
		sqlite.execSQL("DROP TABLE IF EXISTS Table_3");
	}
	
	public void deleteDatabase()
	{
		
	}
	
	public static Cursor selectQuery(String table, String field)
	{
		Cursor result = sqlite.query(true, table, new String[] {field}, null, null, null, null, null);
		return result;
	}
}
