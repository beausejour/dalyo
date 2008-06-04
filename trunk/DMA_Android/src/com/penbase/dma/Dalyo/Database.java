package com.penbase.dma.Dalyo;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.penbase.dma.Binary.Binary;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.XmlElement.XmlTag;
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
	
	private static HashMap<String, ArrayList<String>> tablesMap;
	private HashMap<String, String> fieldsMap;	
	public static final String TABLE = "Table_";
	public static final String FIELD = "Field_";
	private static final int ADDVALUE = 1;
	private static final int UPDATEVALUE = 2;
	private static final int DELETEVALUE = 3;
	private static ArrayList<ArrayList<String>> foreignKeyList;
	
	public Database(Context c, Document d)
	{
		this.context = c;
		this.dbDocument = d;
		tablesMap = new HashMap<String, ArrayList<String>>();		//{tid, [tablename, fieldnames...]}
		fieldsMap = new HashMap<String, String>();
		foreignKeyList = new ArrayList<ArrayList<String>>();
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
		    String tableName = TABLE+tableId;
		    Log.i("info", "value of i "+i+" element name "+tableName);
		    ArrayList<String> tableElements = new ArrayList<String>();
		    tableElements.add(tableName);
		    //tablesMap.put(tableId, tableName);		    
		    createquery += tableName+" (ID VARCHAR(255), ";
		    
		    NodeList fieldList = table.getChildNodes();
		    int fieldLen = fieldList.getLength();
		    
		    //foreignKey has 4 elements (tid, fid, ftid, ffid)
		    ArrayList<String> foreignKey = new ArrayList<String>();		    
		    
		    ArrayList<ArrayList<String>> foreignKeyTable = new ArrayList<ArrayList<String>>();
		    if (fieldLen > 0)
		    {		    	
		    	for (int j=0; j<fieldLen; j++)
		    	{
		    		Element field = (Element) fieldList.item(j);
		    		String fieldId = field.getAttribute(XmlTag.TAG_FIELD_ID);
		    		String fieldName = "Field_"+fieldId;		    		
		    		tableElements.add(fieldName);
		    		String fieldType = field.getAttribute(XmlTag.TAG_FIELD_TYPE);
		    		String fieldSize = field.getAttribute(XmlTag.TAG_FIELD_SIZE);
		    		fieldsMap.put(fieldId, fieldType);
		    		
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
		    			
		    			createquery += fieldName+" "+fieldType+", ";
		    			
		    			foreignKey.add(tableId);
		    			foreignKey.add(fieldId);
		    			foreignKey.add(foreignTableId);
		    			foreignKey.add(foreignFieldId);
		    			foreignKeyList.add(foreignKey);	
		    			
		    			foreignKeyTable.add(foreignKey);
		    		}
		    		else
		    		{
		    			createquery += fieldName+" "+fieldType+", ";
		    		}
		    	}
		    }
		    
		    tablesMap.put(tableId, tableElements);
		    
		    int fksSize = foreignKeyTable.size();
		    if (fksSize > 0)
		    {		    	
		    	for (int k=0; k<fksSize; k++)
		    	{		    				    	
		    		createquery += " FOREIGN KEY ("+FIELD+foreignKeyTable.get(k).get(1)+") REFERENCES "+
		    		TABLE+foreignKeyTable.get(k).get(2)+"("+FIELD+foreignKeyTable.get(k).get(3)+"), ";
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
			ArrayList<ArrayList<Object>> recordsList = new ArrayList<ArrayList<Object>>();
			ArrayList<Integer> syncTypeList = new ArrayList<Integer>();
			Log.i("info", "create recordslist");
			for (int k=0; k<recordsNbInt; k++)
			{
				//ArrayList<Object> valueList = new ArrayList<String>();
				ArrayList<Object> valueList = new ArrayList<Object>();
				//Get type of synchronization
				byte[] syncType = Binary.byteArrayForType(tables);
				int syncTypeInt = Binary.byteArrayToType(syncType);
				syncTypeList.add(syncTypeInt);
				Log.i("info", "syncType "+k+" "+syncTypeInt);
				tables = Binary.cutByteArray(tables, Binary.TYPEBYTE);
				
				//Get local id
				byte[] localId = Binary.byteArrayForInt(tables);
				int localIdInt = Binary.byteArrayToInt(localId);
				//valueList.add(String.valueOf(localIdInt));
				valueList.add(String.valueOf(k));
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
					Object valueObject = Binary.byteArrayToObject(value, fieldsMap.get(String.valueOf(fieldList.get(l))));					
					valueList.add(valueObject);
					tables = Binary.cutByteArray(tables, valueLengthInt);
				}
				recordsList.add(valueList);
			}
			updateTable(tableIdInt, fieldList, syncTypeList, recordsList);
		}			
	}		
	
	public void updateTable(int tableId, ArrayList<Integer> fields, ArrayList<Integer> syncTypeList, 
			ArrayList<ArrayList<Object>> records)
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
	
	public void addValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record)
	{
		Log.i("info", "add table value "+tablesMap.get(String.valueOf(tableId)).get(0));
		int fieldsNb = fieldsList.size();
		
		Log.i("info", "fields size "+fieldsList.size()+" record size "+record.size());
		
		String fields = "(ID, ";
		String values = "('"+record.get(0)+"\', ";
		Log.i("info", "fields "+fields+" values "+values);
		for (int i=0; i<fieldsNb; i++)
		{
			if (i == fieldsNb-1)
			{
				fields += FIELD+fieldsList.get(i);
				values += "\'"+record.get(i+1)+"\'";
				Log.i("info", "last fields "+fields+" values "+values);
			}
			else
			{
				fields += FIELD+fieldsList.get(i)+", ";
				values += "\'"+record.get(i+1)+"\'"+", ";
				Log.i("info", "else fields "+fields+" values "+values);
			}
		}
		fields += ")";
		values += ")";		
		String insert = "insert into "+tablesMap.get(String.valueOf(tableId)).get(0)+" "+fields+" values"+values+";";
		Log.i("info", "query "+insert);
		sqlite.execSQL(insert);
	}
	
	public void updateValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record)
	{	
		Log.i("info", "update table value "+tablesMap.get(String.valueOf(tableId)).get(0));
		int fieldsNb = fieldsList.size();							
		
		for (int i=0; i<fieldsNb; i++)
		{
			String update = "update "+tablesMap.get(String.valueOf(tableId)).get(0)+"set "+
			FIELD+fieldsList.get(i)+"=\'"+record.get(i+1)+"\'"+
			" where ID=\'"+record.get(0)+"\';";
			sqlite.execSQL(update);
		}
	}
	
	public void deleteValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record)
	{
		String delete = "delete from "+tablesMap.get(String.valueOf(tableId)).get(0)+" where ID=\'"+record.get(0)+"\';";
		sqlite.execSQL(delete);
	}
	
	public static void clearTable(String tableId)
	{
		sqlite.execSQL("DELETE FROM "+TABLE+tableId);
		Log.i("info", "clear table "+tableId);
	}	
	
	public void deleteTable()
	{
		sqlite.execSQL("DROP TABLE IF EXISTS Table_2");
		sqlite.execSQL("DROP TABLE IF EXISTS Table_3");
		sqlite.execSQL("DROP TABLE IF EXISTS Table_0");
		sqlite.execSQL("DROP TABLE IF EXISTS Table_1");
	}
	
	public void deleteDatabase()
	{
		
	}
	
	/*public static Cursor selectAllQuery(ArrayList<String> tables)
	{
		Cursor result = null;
		String table = createTableString(tables);
		Log.i("info", "table "+table);
		String selection = createSelectionFKString(tables);
		Log.i("info", "selection "+selection);
		result = sqlite.query(table, null, null, null, null, null, null);
		return result;
	}*/
	
	public static Cursor selectQuery(ArrayList<String> tables, ArrayList<ArrayList<String>> columns, Object filter)
	{
    	//String table = "COMMANDE, CLIENT";
    	//String[] projectionIn = new String[]{"CLIENT.nom", "COMMANDE.ID", "COMMANDE.date"};
    	//String selection = "COMMANDE.ID = CLIENT.ID AND COMMANDE.date = CLIENT.nom";    	
		
		Cursor result = null;
		String table = createTableString(tables);
		Log.i("info", "table "+table);
		String[] projectionIn = createProjectionStrings(columns);
		Log.i("info", "projectionIn "+projectionIn);
		String selection = createSelectionString(filter);
		/*String selection = createSelectionFKString(tables);*/
		Log.i("info", "selection "+selection);
		result = sqlite.query(table, projectionIn, selection, null, null, null, null);
		return result;
	}
	
	public static String createTableString(ArrayList<String> tables)
	{
		String result = "";
		int size = tables.size();
		for (int i=0; i<size; i++)
		{
			if (i != size-1)
			{
				result += TABLE+tables.get(i)+", ";
			}
			else
			{
				result += TABLE+tables.get(i);
			}
		}
		return result;
	}
	
	public static String[] createProjectionStrings(ArrayList<ArrayList<String>> columns)
	{
		Log.i("info","createprojectionstrings "+columns);
		if (columns == null)
		{
			Log.i("info", "it seems that never pass here");
			return null;
		}
		else
		{
			int size = columns.size();
			String[] result = new String[size];
			Log.i("info", "columns size "+size);
			for (int i=0; i<size; i++)
			{
				ArrayList<String> column = columns.get(i);
				result[i] = TABLE+column.get(0)+"."+FIELD+column.get(1);
			}
			return result;
		}
	}
	
	public static String createSelectionString(Object filter)
	{
		if (filter == null)
		{
			return null;
		}
		else
		{
			String result = "";
			int filterSize = ((ArrayList)filter).size();
			if (filterSize > 2)
			{
				String field = (String) FIELD+((ArrayList)filter).get(2);
				Set<String> keySet = tablesMap.keySet();
				for (String s : keySet)
				{
					if (tablesMap.get(s).contains(field))
					{
						result += tablesMap.get(s).get(0)+"."+field;
					}
				}
				Object operator = Function.getOperator(((ArrayList)filter).get(3));
				Log.i("info", "operator "+operator);
				result += " "+operator+" ";
				Object value = ((ArrayList)filter).get(4);
				result += value;
			}
			return result;	
		}		
	}
	
	/*public static String createSelectionFKString(ArrayList<String> tables)
	{
		String result = "";
		int size = foreignKeyList.size();
		Log.i("info", "foreignkeylist size "+size);
		if (tables.size() > 1)
		{
			for (int i=0; i<size; i++)
			{
				ArrayList<String> foreignKey = foreignKeyList.get(i);
				if ((tables.contains(foreignKey.get(0))) && (tables.contains(foreignKey.get(2))))
				{
					if (i != size-1)
					{
						result += TABLE+foreignKey.get(0)+"."+FIELD+foreignKey.get(1)+" = "+
						TABLE+foreignKey.get(2)+"."+FIELD+foreignKey.get(3)+" AND "; 
					}
					else
					{
						result += TABLE+foreignKey.get(0)+"."+FIELD+foreignKey.get(1)+" = "+
						TABLE+foreignKey.get(2)+"."+FIELD+foreignKey.get(3);
					}
					Log.i("info", "result "+i+" "+result);
				}
			}
		}		
		return result;
	}*/
	
	
	public static int getTableNb()
	{		
		return tablesMap.size();
	}
	
	public static Set<String> getTableIds()
	{
		return tablesMap.keySet();
	}
	
	public boolean hasForeignKey(String fid)
	{
		boolean result = false;
		int size = foreignKeyList.size();
		int i=0;
		while (i<size)
		{
			if (fid.equals(foreignKeyList.get(i).get(0)))
			{
				result = true;
				i = size;
			}
			i++;
		}
		
		return result;
	}
	
	public ArrayList<String> getForeignKey(String fid)
	{
		ArrayList<String> result = new ArrayList<String>();
		int size = foreignKeyList.size();
		int i=0;
		while (i<size)
		{
			if (fid.equals(foreignKeyList.get(i).get(0)))
			{
				result.add(foreignKeyList.get(i).get(1));
				result.add(foreignKeyList.get(i).get(2));
				i = size;
			}
			i++;
		}
		return result;
	}
}
