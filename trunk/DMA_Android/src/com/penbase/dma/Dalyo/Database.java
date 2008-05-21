package com.penbase.dma.Dalyo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.penbase.dma.Binary.Binary;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpBinarySync;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;
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
	
	private static HashMap<String, String> tablesMap;
	private HashMap<String, String> fieldsMap;
	
	private static final String tablePre = "Table_";
	private static final String fieldPre = "Field_";
	private static final int ADDVALUE = 1;
	private static final int UPDATEVALUE = 2;
	private static final int DELETEVALUE = 3;
	
	public Database(Context c, Document d)
	{
		this.context = c;
		this.dbDocument = d;
		tablesMap = new HashMap<String, String>();
		fieldsMap = new HashMap<String, String>();
		createDatabase();
	}	
	
	public void createDatabase() throws SQLException
	{
		try
		{			
			context.createDatabase(DATABASE_NAME, 1, 0, null);
			Log.i("info", "create database");
			
			sqlite = context.openDatabase(DATABASE_NAME, null);
			Log.i("info", "open database");
		
			deleteTable();
			createTable();
		}
		catch (FileNotFoundException e) 
		{e.printStackTrace();}
	}
	
	public void createTable()
	{
		Element tagID = (Element)dbDocument.getElementsByTagName(XmlTag.TAG_DB).item(0);
		DbId = Integer.valueOf(tagID.getAttribute(XmlTag.TAG_DB_ID));
		
		NodeList tableList = dbDocument.getElementsByTagName(XmlTag.TAG_TABLE);
		int tableLen = tableList.getLength();
		
		Log.i("info", "table number "+tableLen);		
		
		for (int i=0; i<tableLen; i++)
		{
			String createquery = "CREATE TABLE IF NOT EXISTS ";
			
		    Element table = (Element) tableList.item(i);
		    		    
		    String typeSync = table.getAttribute(XmlTag.TAG_TABLE_SYNC);
		    
		    String tableId = table.getAttribute(XmlTag.TAG_TABLE_ID);
		    String tableName = tablePre+tableId;
		    Log.i("info", "value of i "+i+" element name "+tableName);
		    tablesMap.put(tableId, tableName);
		    createquery += tableName+" (ID VARCHAR(255), ";
		    
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
		    		String fieldId = field.getAttribute(XmlTag.TAG_FIELD_ID);
		    		String fieldName = "Field_"+fieldId;
		    		fieldsMap.put(fieldId, fieldName);
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
		    			String foreignTableId = field.getAttribute(XmlTag.TAG_FIELD_FORIEIGNTABLE);
		    			String foreignFieldId = field.getAttribute(XmlTag.TAG_FIELD_FORIEIGNFIELD); 
		    			String foreignTableName = "Table_"+foreignTableId;
		    			String foreignFieldName = "Field_"+foreignFieldId;
		    			
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
	
	public void syncTable(byte[] bytes)
	{
		Log.i("info", "bytes length "+bytes.length);
		byte[] tableNb = Binary.byteArrayForInt(bytes);	
		Log.i("info", "tableNb length "+tableNb.length);
		int nb = Binary.byteArrayToInt(tableNb);
		Log.i("info", "nb "+nb);		
		byte[] tables = Binary.cutByteArray(bytes, Binary.INTBYTE);
		Log.i("info", "table "+tables.length);
		
		for (int i=0; i<nb; i++)
		{
			//Get table's id
			byte[] tableId = Binary.byteArrayForInt(tables);
			int tableIdInt = Binary.byteArrayToInt(tableId);
			Log.i("info", "tableid "+tableIdInt);
			tables = Binary.cutByteArray(tables, Binary.INTBYTE);
			
			//Get filed's number
			byte[] fieldsNb = Binary.byteArrayForInt(tables);
			int fieldsNbInt = Binary.byteArrayToInt(fieldsNb);
			Log.i("info", "fieldnb "+fieldsNbInt);
			tables = Binary.cutByteArray(tables, Binary.INTBYTE);
			
			//Get fields's ids
			byte[] fields = Binary.byteArrayForValue(tables, Binary.INTBYTE*fieldsNbInt);
			ArrayList<Integer> fieldList = new ArrayList<Integer>();
			for (int j=0; j<fieldsNbInt; j++)
			{
				byte[] field = Binary.byteArrayForInt(fields);
				fieldList.add(Binary.byteArrayToInt(field));
				Log.i("info", "field "+j+" "+Binary.byteArrayToInt(field));
				fields = Binary.cutByteArray(fields, Binary.INTBYTE);
			}
			tables = Binary.cutByteArray(tables, Binary.INTBYTE*fieldsNbInt);
			
			//Get number of records
			byte[] recordsNb = Binary.byteArrayForInt(tables);
			int recordsNbInt = Binary.byteArrayToInt(recordsNb);
			Log.i("info", "recordNb "+recordsNbInt);
			tables = Binary.cutByteArray(tables, Binary.INTBYTE);
			ArrayList<ArrayList<String>> recordsList = new ArrayList<ArrayList<String>>();
			ArrayList<Integer> syncTypeList = new ArrayList<Integer>();
			Log.i("info", "create recordslist");
			for (int k=0; k<recordsNbInt; k++)
			{	
				ArrayList<String> valueList = new ArrayList<String>();
				//Get type of synchronization
				byte[] syncType = Binary.byteArrayForType(tables);
				int syncTypeInt = Binary.byteArrayToType(syncType);
				syncTypeList.add(syncTypeInt);
				Log.i("info", "syncType "+k+" "+syncTypeInt);
				tables = Binary.cutByteArray(tables, Binary.TYPEBYTE);
				
				//Get local id
				byte[] localId = Binary.byteArrayForInt(tables);
				int localIdInt = Binary.byteArrayToInt(localId);
				valueList.add(String.valueOf(localIdInt));
				Log.i("info", "localId "+k+" "+localIdInt);
				tables = Binary.cutByteArray(tables, Binary.INTBYTE);
				
				//Get global id
				byte[] globalId = Binary.byteArrayForInt(tables);
				int globalIdInt = Binary.byteArrayToInt(globalId);
				Log.i("info", "globalId "+k+" "+globalIdInt);
				tables = Binary.cutByteArray(tables, Binary.INTBYTE);
				
				//Get each record's information				
				for (int l=0; l<fieldsNbInt; l++)
				{
					//Get length of value
					byte[] valueLength = Binary.byteArrayForInt(tables);
					int valueLengthInt = Binary.byteArrayToInt(valueLength);
					Log.i("info", "valuelength "+l+" "+valueLengthInt);
					tables = Binary.cutByteArray(tables, Binary.INTBYTE);
					
					//Get value
					byte[] value = Binary.byteArrayForValue(tables, valueLengthInt);
					String valueString = Binary.byteArrayToString(value);
					Log.i("info", "value "+l+" "+valueString);
					valueList.add(valueString);
					tables = Binary.cutByteArray(tables, valueLengthInt);
				}
				recordsList.add(valueList);
			}
			updateTable(tableIdInt, fieldList, syncTypeList, recordsList);
		}			
	}		
	
	public void updateTable(int tableId, ArrayList<Integer> fields, ArrayList<Integer> syncTypeList, 
			ArrayList<ArrayList<String>> records)
	{
		int syncTypeListSize = syncTypeList.size();
		for (int i=0; i<syncTypeListSize; i++)
		{
			switch (syncTypeList.get(i))
			{
				case ADDVALUE:
					addValues(tableId, fields, records.get(i));
					break;
				case UPDATEVALUE:
					updateValues(tableId, fields, records.get(i));
					break;
				case DELETEVALUE:
					deleteValues(tableId, fields, records.get(i));
					break;
			}
		}
		/*String insert = "insert into test (id, title) values('1', 'dfsdsfdsfds');";
		sqlite.execSQL(insert);*/
	}
	
	public void addValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<String> record)
	{
		Log.i("info", "add table value "+tablesMap.get(String.valueOf(tableId)));
		int fieldsNb = fieldsList.size();
		
		Log.i("info", "fields size "+fieldsList.size()+" record size "+record.size());
		
		String fields = "(ID, ";
		String values = "('"+record.get(0)+"\', ";
		Log.i("info", "fields "+fields+" values "+values);
		for (int i=0; i<fieldsNb; i++)
		{
			if (i == fieldsNb-1)
			{
				fields += fieldsMap.get(String.valueOf(fieldsList.get(i)));
				values += "\'"+record.get(i+1)+"\'";
				Log.i("info", "last fields "+fields+" values "+values);
			}
			else
			{
				fields += fieldsMap.get(String.valueOf(fieldsList.get(i)))+", ";
				values += "\'"+record.get(i+1)+"\'"+", ";
				Log.i("info", "else fields "+fields+" values "+values);
			}
		}
		fields += ")";
		values += ")";		
		String insert = "insert into "+tablesMap.get(String.valueOf(tableId))+" "+fields+" values"+values+";";
		Log.i("info", "query "+insert);
		sqlite.execSQL(insert);
	}
	
	public void updateValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<String> record)
	{	
		Log.i("info", "update table value "+tablesMap.get(String.valueOf(tableId)));
		int fieldsNb = fieldsList.size();							
		
		for (int i=0; i<fieldsNb; i++)
		{
			String update = "update "+tablesMap.get(String.valueOf(tableId))+"set "+
			fieldsMap.get(String.valueOf(fieldsList.get(i)))+"=\'"+record.get(i+1)+"\'"+
			" where ID=\'"+record.get(0)+"\';";
			sqlite.execSQL(update);
		}
	}
	
	public void deleteValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<String> record)
	{
		String delete = "delete from "+tablesMap.get(String.valueOf(tableId))+" where ID=\'"+record.get(0)+"\';";
		sqlite.execSQL(delete);
	}
	
	public void deleteTable()
	{
		sqlite.execSQL("DROP TABLE IF EXISTS CommandeProduit");
		sqlite.execSQL("DROP TABLE IF EXISTS PRODUIT");
		sqlite.execSQL("DROP TABLE IF EXISTS COMMANDE");
		sqlite.execSQL("DROP TABLE IF EXISTS CLIENT");
		sqlite.execSQL("DROP TABLE IF EXISTS Table_2");
		sqlite.execSQL("DROP TABLE IF EXISTS Table_3");
		sqlite.execSQL("DROP TABLE IF EXISTS Table_0");
		sqlite.execSQL("DROP TABLE IF EXISTS Table_1");
	}
	
	public void deleteDatabase()
	{
		
	}
	
	public static Cursor selectQuery(String table, String field)
	{
		Cursor result = sqlite.query(true, table, new String[] {field}, null, null, null, null, null);
		return result;
	}
	
	public static int getTableNb()
	{		
		return tablesMap.size();
	}
	
	public static Set<String> getTableIds()
	{
		return tablesMap.keySet();
	}
}
