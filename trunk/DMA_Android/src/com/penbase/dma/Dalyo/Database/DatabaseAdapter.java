package com.penbase.dma.Dalyo.Database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.penbase.dma.Binary.Binary;
import com.penbase.dma.Constant.DatabaseFieldType;
import com.penbase.dma.Constant.XmlTag;
import com.penbase.dma.Dalyo.Function.Function;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseAdapter {
	private final Document dbDocument;
	private final Context context;
	private static SQLiteDatabase sqlite = null;
	private int DbId;
	private static HashMap<String, ArrayList<String>> tablesMap;
	private static HashMap<String, String> fieldsMap;
	private static HashMap<String, String> fieldsPKMap;
	public static final String TABLE = "Table_";
	public static final String FIELD = "Field_";
	public static final String ID = "ID_";
	private static final int ADDVALUE = 1;
	private static final int UPDATEVALUE = 2;
	private static final int DELETEVALUE = 3;
	private static ArrayList<ArrayList<String>> foreignKeyList;
	private ArrayList<Integer> localIds;
	private ArrayList<Integer> globalIds;
	private static HashMap<Integer, ArrayList<HashMap<Integer, HashMap<String, Object>>>> recordsOperated;
	private String dbName = null;
	private String TABLEPREF = "TablePrefFile";
	private String FIELDPREF = "FieldPrefFile";
	
	public DatabaseAdapter(Context c, Document d, String database){
		this.context = c;
		this.dbDocument = d;
		this.dbName = database;
		this.TABLEPREF = dbName+"_"+TABLEPREF;
		this.FIELDPREF = dbName+"_"+FIELDPREF;
		tablesMap = new HashMap<String, ArrayList<String>>();		//{tid, [tablename, fieldnames...]}
		fieldsMap = new HashMap<String, String>();
		fieldsPKMap = new HashMap<String, String>();
		foreignKeyList = new ArrayList<ArrayList<String>>();
		this.localIds = new ArrayList<Integer>();
		this.globalIds = new ArrayList<Integer>();
		recordsOperated = new HashMap<Integer, ArrayList<HashMap<Integer, HashMap<String, Object>>>>();
		createDatabase(dbName);
	}
	
	private void createDatabase(String database) throws SQLException{
		try{
			if (!databaseExists(database)){
				Log.i("info", "the database doesn't exist");
				context.createDatabase(database, 1, 0, null);
				sqlite = context.openDatabase(database, null);
				createTable();
			}
			else if (!checkDatabaseExists()){
				Log.i("info", "the database isn't the same");
				deleteDatabase(database);
				context.createDatabase(database, 1, 0, null);
				sqlite = context.openDatabase(database, null);
				createTable();
			}
			else{
				Log.i("info", "the database have nothing to change");
				context.createDatabase(database, 1, 0, null);
				sqlite = context.openDatabase(database, null);
			}
		}
		catch (FileNotFoundException e){
			e.printStackTrace();}
	}
	
	private boolean databaseExists(String database){
		File dbFile = new File("data/data/com.penbase.dma/databases/"+database);
		return dbFile.exists();
	}
	
	private boolean checkDatabaseExists(){
		boolean result = true;
		SharedPreferences tablePref = context.getSharedPreferences(TABLEPREF, Context.MODE_PRIVATE);
		SharedPreferences fieldPref = context.getSharedPreferences(FIELDPREF, Context.MODE_PRIVATE);
		NodeList tableList = dbDocument.getElementsByTagName(XmlTag.TABLE);
		int tableLen = tableList.getLength();
		int i = 0;
		while (i < tableLen){
			Element table = (Element) tableList.item(i);
			String tableId = table.getAttribute(XmlTag.TABLE_ID);
			Log.i("info", "tablepref "+tablePref.getString(tableId, "null"));
			if (!tablePref.getString(tableId, "null").equals("")){
				Log.i("info", "table not ok");
				result = false;
				i = tableLen;
			}
			else{
				NodeList fieldList = table.getChildNodes();
				int fieldLen = fieldList.getLength();
				if (fieldLen > 0){
					int j = 0;
					while (j < fieldLen){
						Element field = (Element) fieldList.item(j);
						String fieldId = field.getAttribute(XmlTag.FIELD_ID);
						String fieldType = field.getAttribute(XmlTag.FIELD_TYPE);
						String fieldSize = field.getAttribute(XmlTag.FIELD_SIZE);
						if (fieldType.equals("VARCHAR")){
							fieldType = fieldType+"("+fieldSize+")";
						}
						String fieldTypeValue = fieldType;
						if (field.hasAttribute(XmlTag.FIELD_PK)){
							if (field.hasAttribute(XmlTag.FIELD_PK_AUTO)){
								fieldTypeValue += " PRIMARY KEY AUTOINCREMENT, ";
							}
							else{
								fieldTypeValue += " PRIMARY KEY, ";
							}
						}
						
						else if ((field.hasAttribute(XmlTag.FIELD_FORIEIGNTABLE)) 
								&& (field.hasAttribute(XmlTag.FIELD_FORIEIGNFIELD))){
							String foreignTableId = field.getAttribute(XmlTag.FIELD_FORIEIGNTABLE);
							String foreignFieldId = field.getAttribute(XmlTag.FIELD_FORIEIGNFIELD);
							fieldTypeValue += foreignTableId+" "+foreignFieldId;
						}
						Log.i("info", "field "+fieldId+" "+fieldPref.getString(fieldId, "null")+" actual "+fieldTypeValue);
						if (!fieldPref.getString(fieldId, "null").equals(fieldTypeValue)){
							Log.i("info", "field not ok");
							result = false;
							j = fieldLen;
							i = tableLen;
						}
						else{
							j++;
						}
					}
				}
				i++;
			}
		}
		return result;
	}
	
	private void createTable(){
		SharedPreferences.Editor editorTablePref = context.getSharedPreferences(TABLEPREF, Context.MODE_PRIVATE).edit();
		SharedPreferences.Editor editorFieldPref = context.getSharedPreferences(FIELDPREF, Context.MODE_PRIVATE).edit();
		Element tagID = (Element)dbDocument.getElementsByTagName(XmlTag.DB).item(0);
		DbId = Integer.valueOf(tagID.getAttribute(XmlTag.DB_ID));
		NodeList tableList = dbDocument.getElementsByTagName(XmlTag.TABLE);
		int tableLen = tableList.getLength();
		for (int i=0; i<tableLen; i++){
			String createquery = "CREATE TABLE IF NOT EXISTS ";
			Element table = (Element) tableList.item(i);
			String typeSync = table.getAttribute(XmlTag.TABLE_SYNC);
			String tableId = table.getAttribute(XmlTag.TABLE_ID);
			editorTablePref.putString(tableId, "");
			String tableName = TABLE+tableId;
			Log.i("info", "value of i "+i+" element name "+tableName);
			ArrayList<String> tableElements = new ArrayList<String>();
			String id = ID+tableId;
			createquery += tableName+" ("+id+" VARCHAR(255), ";
			NodeList fieldList = table.getChildNodes();
			int fieldLen = fieldList.getLength();
			ArrayList<ArrayList<String>> foreignKeyTable = new ArrayList<ArrayList<String>>();
			if (fieldLen > 0){
				for (int j=0; j<fieldLen; j++){
					//foreignKey has 4 elements (tid, fid, ftid, ffid)
					ArrayList<String> foreignKey = new ArrayList<String>();	
					Element field = (Element) fieldList.item(j);
					String fieldId = field.getAttribute(XmlTag.FIELD_ID);
					String fieldName = "Field_"+fieldId;
					tableElements.add(fieldName);
					String fieldType = field.getAttribute(XmlTag.FIELD_TYPE);
					String fieldSize = field.getAttribute(XmlTag.FIELD_SIZE);
					fieldsMap.put(fieldId, fieldType);
					if (fieldType.equals("VARCHAR")){
						fieldType = fieldType+"("+fieldSize+")";
					}
					String fieldSync = field.getAttribute(XmlTag.FIELD_SYNC);
					String fieldTypeValue = fieldType;
					if (field.hasAttribute(XmlTag.FIELD_PK)){
						fieldsPKMap.put(tableId, fieldId);
						if (field.hasAttribute(XmlTag.FIELD_PK_AUTO)){
							createquery += fieldName+" "+fieldType+" PRIMARY KEY AUTOINCREMENT, ";
							fieldTypeValue += " PRIMARY KEY AUTOINCREMENT, ";
						}
						else{
							createquery += fieldName+" "+fieldType+" PRIMARY KEY, ";
							fieldTypeValue += " PRIMARY KEY, ";
						}
					}
					
					else if ((field.hasAttribute(XmlTag.FIELD_FORIEIGNTABLE)) 
							&& (field.hasAttribute(XmlTag.FIELD_FORIEIGNFIELD))){
						String foreignTableId = field.getAttribute(XmlTag.FIELD_FORIEIGNTABLE);
						String foreignFieldId = field.getAttribute(XmlTag.FIELD_FORIEIGNFIELD);
						fieldTypeValue += foreignTableId+" "+foreignFieldId;
						createquery += fieldName+" "+fieldType+", ";
						foreignKey.add(tableId);
						foreignKey.add(fieldId);
						foreignKey.add(foreignTableId);
						foreignKey.add(foreignFieldId);
						Log.i("info", "foreignKeyList add "+foreignKey);
						foreignKeyList.add(foreignKey);	
						foreignKeyTable.add(foreignKey);
					}
					else{
						createquery += fieldName+" "+fieldType+", ";
					}
					editorFieldPref.putString(fieldId, fieldTypeValue);
				}
			}
			
			tablesMap.put(tableId, tableElements);
			int fksSize = foreignKeyTable.size();
			if (fksSize > 0){
				for (int k=0; k<fksSize; k++){
					createquery += " FOREIGN KEY ("+FIELD+foreignKeyTable.get(k).get(1)+") REFERENCES "+
					TABLE+foreignKeyTable.get(k).get(2)+"("+FIELD+foreignKeyTable.get(k).get(3)+"), ";
				}
			}
			
			createquery = createquery.substring(0, createquery.length()-2);
			createquery += ");";
			
			Log.i("info", "query "+createquery);
			sqlite.execSQL(createquery);
		}
		editorTablePref.commit();
		editorFieldPref.commit();
	}
	
	private void deleteDatabase(String database){
		context.deleteDatabase(database);
	}

	public byte[] syncImportTable(byte[] bytes){
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] tableNb = new byte[Binary.INTBYTE];
		bis.read(tableNb, 0, tableNb.length);
		bos.write(tableNb, 0, tableNb.length);
		int nb = Binary.byteArrayToInt(tableNb);
		for (int i=0; i<nb; i++){
			//Get table's id
			byte[] tableId = new byte[Binary.INTBYTE];
			bis.read(tableId, 0, tableId.length);
			int tableIdInt = Binary.byteArrayToInt(tableId);
			bos.write(tableId, 0, tableId.length);
			
			//Get filed's number
			byte[] fieldsNb = new byte[Binary.INTBYTE];
			bis.read(fieldsNb, 0, fieldsNb.length);
			int fieldsNbInt = Binary.byteArrayToInt(fieldsNb);
			
			//Get fields's ids
			byte[] fields = new byte[Binary.INTBYTE*fieldsNbInt];
			bis.read(fields, 0, fields.length);
			ArrayList<Integer> fieldList = new ArrayList<Integer>();
			ByteArrayInputStream bisFields = new ByteArrayInputStream(fields);
			for (int j=0; j<fieldsNbInt; j++){
				byte[] field = new byte[Binary.INTBYTE];
				bisFields.read(field, 0, field.length);
				fieldList.add(Binary.byteArrayToInt(field));
				Log.i("info", "field "+j+" "+Binary.byteArrayToInt(field));
			}

			//Get number of records
			byte[] recordsNb = new byte[Binary.INTBYTE];
			bis.read(recordsNb, 0, recordsNb.length);
			int recordsNbInt = Binary.byteArrayToInt(recordsNb);
			bos.write(recordsNb, 0, recordsNb.length);
			ArrayList<ArrayList<Object>> recordsList = new ArrayList<ArrayList<Object>>();
			ArrayList<Integer> syncTypeList = new ArrayList<Integer>();
			Log.i("info", "recordsNbInt "+recordsNbInt);
			for (int k=0; k<recordsNbInt; k++){
				ArrayList<Object> valueList = new ArrayList<Object>();
				//Get type of synchronization
				byte[] syncType = new byte[Binary.TYPEBYTE];
				bis.read(syncType, 0, syncType.length);
				int syncTypeInt = Binary.byteArrayToType(syncType);
				syncTypeList.add(syncTypeInt);
				Log.i("info", "syncType "+k+" "+syncTypeInt+" "+syncType.length);
				bos.write(syncType, 0, syncType.length);
				
				//Get local id
				byte[] localId = new byte[Binary.INTBYTE];
				bis.read(localId, 0, localId.length);
				int localIdInt = Binary.byteArrayToInt(localId);
				localIds.add(localIdInt);
				valueList.add(String.valueOf(k+1));
				bos.write(Binary.intToByteArray(k+1), 0, Binary.intToByteArray(k+1).length);
				
				//Get global id
				byte[] globalId = new byte[Binary.INTBYTE];
				bis.read(globalId, 0, globalId.length);
				int globalIdInt = Binary.byteArrayToInt(globalId);
				bos.write(globalId, 0, globalId.length);
				globalIds.add(globalIdInt);
				Log.i("info", "globalId "+k+" "+globalIdInt);
				
				//Get each record's information
				for (int l=0; l<fieldsNbInt; l++){
					//Get length of value
					byte[] valueLength = new byte[Binary.INTBYTE];
					bis.read(valueLength, 0, valueLength.length);
					int valueLengthInt = Binary.byteArrayToInt(valueLength);
					Log.i("info", "valuelength "+l+" "+valueLengthInt);
					
					//Get value
					byte[] value = new byte[valueLengthInt];
					bis.read(value, 0, value.length);
					Object valueObject = Binary.byteArrayToObject(value, fieldsMap.get(String.valueOf(fieldList.get(l))));
					valueList.add(valueObject);
				}
				recordsList.add(valueList);
			}
			updateTable(tableIdInt, fieldList, syncTypeList, recordsList);
		}
		byte[] result = bos.toByteArray();
		try {
			bos.close();
			bis.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
//tbalenb, [tableid, fieldsNb, [field1, field2...], recordsNb, [synctype, lid, gid, [valueLength, value]] ]
	public byte[] syncExportTable(){
		Log.i("info", "recordsOperated "+recordsOperated);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//tableNb
		int tableNbInt = recordsOperated.size();
		byte[] tableNb = Binary.intToByteArray(tableNbInt);
		Log.i("info", "tableNbInt "+tableNbInt);
		Log.i("info", "tableNb length "+tableNb.length);
		bos.write(tableNb, 0, tableNb.length);
		Set<Integer> keySet = recordsOperated.keySet();
		for (Integer key : keySet){
			//tableId
			Log.i("info", "key "+key);
			int tableIdInt = key;
			byte[] tableId = Binary.intToByteArray(tableIdInt);
			Log.i("info", "tableIdInt "+tableIdInt);
			Log.i("info", "tableId length "+tableId.length);
			bos.write(tableId, 0, tableId.length);
			//fieldsNb
			ArrayList<String> fields = tablesMap.get(String.valueOf(key));
			int fieldsNbInt = fields.size();
			byte[] fieldsNb = Binary.intToByteArray(fieldsNbInt);
			Log.i("info", "fieldsNbInt "+fieldsNbInt);
			Log.i("info", "fieldsNb length "+fieldsNb.length);
			bos.write(fieldsNb, 0, fieldsNb.length);
			for (int j=0; j<fieldsNbInt; j++){
				int fieldInt = Integer.valueOf(fields.get(j).split("_")[1]);
				byte[] field = Binary.intToByteArray(fieldInt);
				Log.i("info", "fieldInt "+fieldInt);
				Log.i("info", "field length "+field.length);
				bos.write(field, 0, field.length);
			}
			//recordsNb  
			int recordsNbInt = recordsOperated.get(key).size();
			byte[] recordsNb = Binary.intToByteArray(recordsNbInt);
			Log.i("info", "recordsNbInt "+recordsNbInt);
			Log.i("info", "recordsNb length "+recordsNb.length);
			bos.write(recordsNb, 0, recordsNb.length);
			for (int k=0; k<recordsNbInt; k++){
				HashMap<Integer, HashMap<String, Object>> record = recordsOperated.get(key).get(k);
				//syncType
				int syncTypeInt = (Integer)record.keySet().toArray()[0];
				byte[] syncType = Binary.typeToByteArray(syncTypeInt);
				Log.i("info", "syncTypeInt "+syncTypeInt);
				Log.i("info", "syncType length "+syncType.length);
				bos.write(syncType, 0, syncType.length);
				//local id
				int localIdInt = Integer.valueOf(String.valueOf(record.get(syncTypeInt).get(ID+tableIdInt)));
				byte[] localId = Binary.intToByteArray(localIdInt);
				Log.i("info", "localIdInt "+localIdInt);
				Log.i("info", "localId length "+localId.length);
				bos.write(localId, 0, localId.length);
				//global id is 0
				int globalIdInt = 0;
				byte[] globalId = Binary.intToByteArray(globalIdInt);
				Log.i("info", "globalIdInt "+globalIdInt);
				Log.i("info", "globalId length "+globalId.length);
				bos.write(globalId, 0, globalId.length);
				//value 
				HashMap<String, Object> values = record.get(syncTypeInt);
				Log.i("info", "values "+values);
				for (int j=0; j<fieldsNbInt; j++){
					String valueType = fieldsMap.get(fields.get(j).split("_")[1]);
					Log.i("info", "field "+fields.get(j));
					byte[] value = Binary.objectToByteArray(values.get(fields.get(j)), valueType);
					int valueLengthInt = value.length;
					byte[] valueLenth = Binary.intToByteArray(valueLengthInt);
					Log.i("info", "valueType "+valueType+" value "+values.get(fields.get(j)));
					Log.i("info", "value length "+value.length);
					Log.i("info", "valueLengthInt "+valueLengthInt);
					Log.i("info", "valueLenth length "+valueLenth.length);
					bos.write(valueLenth, 0, valueLenth.length);
					bos.write(value, 0, value.length);
				}
			}
		}
		return bos.toByteArray();
	}
	
	private void updateTable(int tableId, ArrayList<Integer> fields, ArrayList<Integer> syncTypeList,
			ArrayList<ArrayList<Object>> records){
		int syncTypeListSize = syncTypeList.size();
		for (int i=0; i<syncTypeListSize; i++){
			switch (syncTypeList.get(i)){
				case ADDVALUE:
					insertValues(tableId, fields, records.get(i));
					break;
				case UPDATEVALUE:
					updateValues(tableId, fields, records.get(i));
					break;
				case DELETEVALUE:
					deleteValues(tableId, fields, records.get(i));
					break;
			}
		}
	}
	
	private void deleteTable(){
		
	}
	
	//Add values and don't check primary key
	private void insertValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record){
		Log.i("info", "add table value "+TABLE+tableId);
		Cursor cursorAllRows = selectQuery(String.valueOf(tableId), null, null);
		int newId = cursorAllRows.count()+1;
		int fieldsNb = fieldsList.size();
		String fields = "("+ID+tableId+", ";
		String values = "('"+newId+"\', ";
		for (int i=0; i<fieldsNb; i++){
			if (i == fieldsNb-1){
				fields += FIELD+fieldsList.get(i);
				values += "\'"+record.get(i+1)+"\'";
			}
			else{
				fields += FIELD+fieldsList.get(i)+", ";
				values += "\'"+record.get(i+1)+"\'"+", ";
			}
		}
		fields += ")";
		values += ")";
		String tableName = TABLE+tableId;
		String insert = "insert into "+tableName+" "+fields+" values"+values+";";
		Log.i("info", "query "+insert);
		sqlite.execSQL(insert);
	}
	
	private void updateValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record){
		Log.i("info", "update table value "+TABLE+tableId);
		int fieldsNb = fieldsList.size();
		
		for (int i=0; i<fieldsNb; i++){
			String tableName = TABLE+tableId;
			String update = "update "+tableName+" set "+
			FIELD+fieldsList.get(i)+"=\'"+record.get(i+1)+"\'"+
			" where "+ID+tableId+"=\'"+record.get(0)+"\';";
			sqlite.execSQL(update);
		}
	}
	
	private void deleteValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record){
		String delete = "delete from "+tablesMap.get(String.valueOf(tableId)).get(0)+" where "+ID+tableId+"=\'"+record.get(0)+"\';";
		sqlite.execSQL(delete);
	}
	
	//Generate primary key for KEY and INTEGER
	private static Object generatePK(String type, Object idValue){
		Object result = null;
		if (type.equals(DatabaseFieldType.KEY)){
			result = KeyGenerator.getKeyGenerated();
		}
		else if (type.equals(DatabaseFieldType.INTEGER)){
			result = idValue;
		}
		return result;
	}
	
	public static void clearTable(String tableId){
		sqlite.execSQL("DELETE FROM "+TABLE+tableId);
		Log.i("info", "clear table "+tableId);
	}
	
	public static Cursor selectQuery(String tableId, ArrayList<Integer> fieldList, ArrayList<Object> valueList){
		Cursor result = null;
		String table = TABLE+tableId;
		Log.i("info", "tableid "+table);
		String selection = createSelectionString(fieldList, valueList);
		Log.i("info", "selection "+selection);
		result = sqlite.query(table, null, selection, null, null, null, null);
		return result;
	}
	
	public static Cursor selectQuery(ArrayList<String> tables, ArrayList<ArrayList<String>> columns, Object filter){
		//String table = "COMMANDE, CLIENT";
		//String[] projectionIn = new String[]{"CLIENT.nom", "COMMANDE.ID", "COMMANDE.date"};
		//String selection = "COMMANDE.ID = CLIENT.ID AND COMMANDE.date = CLIENT.nom";
		
		Cursor result = null;
		String table = createTableString(tables);
		Log.i("info", "table "+table);
		String[] projectionIn = createProjectionStrings(columns);
		Log.i("info", "projectionIn "+projectionIn);
		String selection = createSelectionString(tables, filter);
		Log.i("info", "selection selectQuery "+selection);
		result = sqlite.query(table, projectionIn, selection, null, null, null, null);
		Log.i("info", "result "+result);
		return result;
	}

	//Add values and check primary key
	public static void addQuery(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record){
		int fieldsNb = fieldsList.size();
		HashMap<String, Object> compareRecord = new HashMap<String, Object>();
		String id = ID+tableId;
		compareRecord.put(id, record.get(0));
		String fields = "("+id+", ";
		String values = "('"+record.get(0)+"\', ";
		for (int i=0; i<fieldsNb; i++){
			if ((fieldsPKMap.containsKey(String.valueOf(tableId))) && 
					(fieldsPKMap.get(String.valueOf(tableId)).equals(fieldsList.get(i))) &&
					(record.get(i+1) == null)){
				Log.i("info", "in case of filed has pk field, generate its value");
				record.remove(i+1);
				record.add(i+1, generatePK(fieldsMap.get(fieldsList.get(i)), record.get(0)));
			}
			if (i == fieldsNb-1){
				fields += FIELD+fieldsList.get(i);
				values += "\'"+record.get(i+1)+"\'";
				Log.i("info", "last fields "+fields+" values "+values);
			}
			else{
				fields += FIELD+fieldsList.get(i)+", ";
				values += "\'"+record.get(i+1)+"\'"+", ";
				Log.i("info", "else fields "+fields+" values "+values);
			}
			compareRecord.put(FIELD+fieldsList.get(i), record.get(i+1));
		}
		if ((!fieldsList.contains(fieldsPKMap.get(String.valueOf(tableId)))) &&
				(fieldsPKMap.containsKey(String.valueOf(tableId)))){
			Log.i("info", "in case of field list hasn't pk field, generate its value");
			fields += ", "+FIELD+fieldsPKMap.get(String.valueOf(tableId));
			values += ", \'"+generatePK(fieldsMap.get(fieldsPKMap.get(String.valueOf(tableId))), record.get(0))+"\'";
			compareRecord.put(FIELD+fieldsPKMap.get(String.valueOf(tableId)), 
					generatePK(fieldsMap.get(fieldsPKMap.get(String.valueOf(tableId))), record.get(0)));
		}
		Log.i("info", "compareRecord "+compareRecord);
		addRecordOperated(tableId, ADDVALUE, compareRecord);
		fields += ")";
		values += ")";
		String tableName = TABLE+tableId;
		String insert = "insert into "+tableName+" "+fields+" values"+values+";";
		Log.i("info", "query "+insert);
		sqlite.execSQL(insert);
	}
	
	public static void updateQuery(String tableId, ArrayList<Integer> fieldList, ArrayList<Object> valueList, HashMap<Object, Object> record){
		Log.i("info", "updateQuery Record "+record);
		HashMap<String, Object> compareRecord = new HashMap<String, Object>();
		String id = ID+tableId;
		compareRecord.put(id, String.valueOf(record.get(id)));
		String table = DatabaseAdapter.TABLE+tableId;
		Log.i("info", "table "+table);
		ContentValues values = new ContentValues();
		int listSize = fieldList.size();
		for (int i=0; i<listSize; i++){
			values.put(FIELD+String.valueOf(fieldList.get(i)), String.valueOf(valueList.get(i)));
			compareRecord.put(FIELD+String.valueOf(fieldList.get(i)), String.valueOf(valueList.get(i)));
			record.remove(FIELD+fieldList.get(i));
		}
		addRecordOperated(Integer.valueOf(tableId), UPDATEVALUE, compareRecord);
		Log.i("info", "values "+values);
		String whereClause = createWhereClause(tableId, record);
		Log.i("info", "whereclause "+whereClause);
		sqlite.update(table, values, whereClause, null);
	}
	
	public static void deleteQuery(String tableId, HashMap<Object, Object> record){
		Log.i("info", "deletequery table "+tableId+" record "+record);
		HashMap<String, Object> compareRecord = new HashMap<String, Object>();
		String id = ID+tableId;
		compareRecord.put(id, String.valueOf(record.get(id)));
		addRecordOperated(Integer.valueOf(tableId), DELETEVALUE, compareRecord);
		String table = TABLE+tableId;
		String whereClause = createWhereClause(tableId, record);
		Log.i("info", "whereclause "+whereClause);
		sqlite.delete(table, whereClause, null); 
	}
	
	public static int getTableNb(){
		return tablesMap.size();
	}
	
	public static Set<String> getTableIds(){
		return tablesMap.keySet();
	}
	
	public static void startTransaction(){
		sqlite.execSQL("BEGIN TRANSACTION;");
	}
	
	public static void cancelTransaction(){
		sqlite.execSQL("ROLLBACK TRANSACTION;");
	}
	
	public static void validateTransaction(){
		sqlite.execSQL("COMMIT TRANSACTION;");
	}
	
	public static HashMap<Integer, ArrayList<HashMap<Integer, HashMap<String, Object>>>> getRecordsOperated(){
		return recordsOperated;
	}

	public static void clearRecordsOperated(){
		if (recordsOperated.size() > 0){
			recordsOperated.clear();
		}
	}
	
	private static String createWhereClause(String tableId, HashMap<Object, Object> record){
		String result = "";
		HashMap<Object, Object> newRecord = new HashMap<Object, Object>();
		Iterator<Object> iterator = record.keySet().iterator();
		while (iterator.hasNext()){
			Object key = iterator.next();
			if ((tablesMap.get(tableId).contains(key)) &&
					(record.get(key) != null)){
				newRecord.put(key, record.get(key));
			}
		}
		int newRecordSize = newRecord.keySet().size();
		Iterator<Object> newIterator = newRecord.keySet().iterator();
		int check = 1;
		while (newIterator.hasNext()){
			Object key = newIterator.next();
			if (check == newRecordSize){
				result += String.valueOf(key)+"=\'"+String.valueOf(newRecord.get(key))+"\'";
			}
			else{
				result += String.valueOf(key)+"=\'"+String.valueOf(newRecord.get(key))+"\' AND "; 
			}
			check++;
		}
		return result;
	}
	
	private static String createTableString(ArrayList<String> tables){
		String result = "";
		int size = tables.size();
		for (int i=0; i<size; i++){
			if (i != size-1){
				result += TABLE+tables.get(i)+", ";
			}
			else{
				result += TABLE+tables.get(i);
			}
		}
		return result;
	}
	
	private static String[] createProjectionStrings(ArrayList<ArrayList<String>> columns){
		if (columns == null){
			return null;
		}
		else{
			int size = columns.size();
			String[] result = new String[size];
			for (int i=0; i<size; i++){
				ArrayList<String> column = columns.get(i);
				result[i] = TABLE+column.get(0)+"."+FIELD+column.get(1);
			}
			return result;
		}
	}
	
	private static String createSelectionString(ArrayList<String> tables, Object filter){
		String result = "";
		if (filter == null){
			if (!createSelectionFKString(tables).equals("")){
				Log.i("info", "tables "+tables+" has fk");
				if (result.equals("")){
					result += createSelectionFKString(tables);	
				}
				else{
					result += " AND "+createSelectionFKString(tables);
				}
			}
			return result;
		}
		else{
			int filterSize = ((ArrayList<?>)filter).size();
			if (filterSize > 2){
				int filterNb = (filterSize - 2) / 4;
				Log.i("info", "check how many filters "+filterNb);
				for (int i=0; i<filterNb; i++){
					//Check the link is not implemented yet
					if (i == 0){
						String field = (String)FIELD+((ArrayList<?>)filter).get(4*i+2);
						Set<String> keySet = tablesMap.keySet();
						for (String s : keySet){
							if (tablesMap.get(s).contains(field)){
								String tableName = TABLE+s;
								result += tableName+"."+field;
							}
						}
						Object operator = Function.getOperator(((ArrayList<?>)filter).get(4*i+3));
						Log.i("info", "operator "+operator);
						result += " "+operator+" ";
						Object value = "\'"+((ArrayList<?>)filter).get(4*i+4)+"\'";
						result += value; 
					}
					else{
						result += " AND ";
						String field = (String) FIELD+((ArrayList<?>)filter).get(4*i+2);
						Set<String> keySet = tablesMap.keySet();
						for (String s : keySet){
							if (tablesMap.get(s).contains(field)){
								String tableName = TABLE+s;
								result += tableName+"."+field;
							}
						}
						Object operator = Function.getOperator(((ArrayList<?>)filter).get(4*i+3));
						Log.i("info", "operator "+operator);
						result += " "+operator+" ";
						Object value = "\'"+((ArrayList<?>)filter).get(4*i+4)+"\'";
						result += value;
					}
					Log.i("info", "result in createSelectionString "+result);
				}
				if (!createSelectionFKString(tables).equals("")){
					Log.i("info", "tables "+tables+" has fk");
					if (result.equals("")){
						result += createSelectionFKString(tables);	
					}
					else{
						result += " AND "+createSelectionFKString(tables);
					}
				}
			}
			return result;	
		}
	}
	
	private static String createSelectionString(ArrayList<Integer> fieldList, ArrayList<Object> valueList){
		if ((fieldList == null) && (valueList == null)){
			return null;
		}
		else{
			String result = "";
			int size = fieldList.size();
			for (int i=0; i<size; i++){
				if (i == 0){
					result += FIELD+fieldList.get(i)+" = \'"+valueList.get(i)+"\'";
				}
				else{
					result += " AND "+FIELD+fieldList.get(i)+" = \'"+valueList.get(i)+"\'";
				}
			}
			return result;
		}		
	}
	
	private static String createSelectionFKString(ArrayList<String> tables){
		String result = "";
		int size = foreignKeyList.size();
		if (tables.size() > 1){
			for (int i=0; i<size; i++){
				ArrayList<String> foreignKey = foreignKeyList.get(i);
				if ((tables.contains(foreignKey.get(0))) && (tables.contains(foreignKey.get(2)))){
					if (result != ""){
						result += "AND "+TABLE+foreignKey.get(0)+"."+FIELD+foreignKey.get(1)+" = "+
						TABLE+foreignKey.get(2)+"."+FIELD+foreignKey.get(3);
					}
					else{
						result += TABLE+foreignKey.get(0)+"."+FIELD+foreignKey.get(1)+" = "+
						TABLE+foreignKey.get(2)+"."+FIELD+foreignKey.get(3);
					}					
					Log.i("info", "result "+i+" "+result);
				}
			}
		}		
		return result;
	}
	
	//Check if we need to add the new record in recordOperated, if checkOperated() return true, add record in recordsOperated; otherwise, do nothing
	private static boolean checkOperated(int tableId, int type, HashMap<String, Object> record){
		boolean result = true;
		ArrayList<HashMap<Integer, HashMap<String, Object>>> tableRecordOperated = recordsOperated.get(tableId);
		ArrayList<Integer> deleteKeys = new ArrayList<Integer>();
		int tableRecordsOperatedSize = tableRecordOperated.size();
		for (int i=0; i<tableRecordsOperatedSize; i++){
			HashMap<Integer, HashMap<String, Object>> recordOperated = tableRecordOperated.get(i);
			Set<Integer> keys = recordOperated.keySet();
			if (keys.size() == 1){
				for (Integer key : keys){
					HashMap<String, Object> containRecord = recordOperated.get(key);
					if ((containRecord.keySet().equals(record.keySet())) && (containRecord.values().containsAll(record.values()))){
						deleteKeys.add(i);
						if (type == DELETEVALUE){
							result = false;
						}
					}
					else if (!containRecord.keySet().equals(record.keySet())){
						Set<String> recordKeys = record.keySet();
						for (String recordKey : recordKeys){
							if (!containRecord.keySet().contains(recordKey)){
								containRecord.put(recordKey, record.get(recordKey));
							}
						}
						result = false;
					}
				}
			}
		}
		int deleteKeysSize = deleteKeys.size();
		for (int i=0; i<deleteKeysSize; i++){
			tableRecordOperated.remove(deleteKeys.get(i));
		}
		return result;
	}
	
	private static void addRecordOperated(int tableId, int type, HashMap<String, Object> compareRecord){
		if (recordsOperated.containsKey(tableId)){
			if (checkOperated(tableId, type, compareRecord)){
				HashMap<Integer, HashMap<String, Object>> recordOperated = new HashMap<Integer, HashMap<String, Object>>();
				recordOperated.put(type, compareRecord);
				recordsOperated.get(tableId).add(recordOperated);
			}
		}
		else{
			ArrayList<HashMap<Integer, HashMap<String, Object>>> recordOperatedList = new ArrayList<HashMap<Integer, HashMap<String, Object>>>();
			HashMap<Integer, HashMap<String, Object>> recordOperated = new HashMap<Integer, HashMap<String, Object>>();
			recordOperated.put(type, compareRecord);
			recordOperatedList.add(recordOperated);
			recordsOperated.put(tableId, recordOperatedList);
		}
	}
}
