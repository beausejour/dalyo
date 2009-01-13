package com.penbase.dma.Dalyo.Database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.penbase.dma.Binary.Binary;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Constant.DatabaseTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;

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
	private static HashMap<String, ArrayList<String>> tablesMap;
	private static HashMap<String, String> tablesNameMap;
	private static HashMap<String, String> fieldsTypeMap;
	private static HashMap<String, String> fieldsNameMap;
	private static HashMap<String, String> fieldsPKMap;
	private static ArrayList<ArrayList<String>> foreignKeyList;
	private String dbName = null;
	private String TABLEPREF = "TablePrefFile";
	private String FIELDPREF = "FieldPrefFile";
	private static boolean STARTTRANSACTION = false;
	private static  ArrayList<ArrayList<Object>> blobRecords;
	
	public DatabaseAdapter(Context c, Document d, String database) {
		this.context = c;
		this.dbDocument = d;
		this.dbName = database;
		this.TABLEPREF = dbName+"_"+TABLEPREF;
		this.FIELDPREF = dbName+"_"+FIELDPREF;
		tablesMap = new HashMap<String, ArrayList<String>>();		//{tid, [tablename, fieldnames...]}
		tablesNameMap = new HashMap<String, String>();
		fieldsTypeMap = new HashMap<String, String>();
		fieldsNameMap = new HashMap<String, String>();
		fieldsPKMap = new HashMap<String, String>();
		foreignKeyList = new ArrayList<ArrayList<String>>();
		blobRecords = new ArrayList<ArrayList<Object>>();
		if (dbDocument.getElementsByTagName(DatabaseTag.TABLE).getLength() > 0) {
			createDatabase(dbName);
		}
	}
	
	private void createDatabase(String database) throws SQLException{
		try{
			if (!databaseExists(database)) {
				Log.i("info", "the database doesn't exist");
				sqlite = context.openOrCreateDatabase(database, 0, null);
				createTable();
			}
			else if (!checkDatabaseExists()) {
				Log.i("info", "the database isn't the same");
				sqlite = context.openOrCreateDatabase(database, 0, null);
				createTable();
			}
			else {
				Log.i("info", "the database have nothing to change");
				sqlite = context.openOrCreateDatabase(database, 0, null);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeDatabase() {
		if ((sqlite != null) && (sqlite.isOpen())) {
			sqlite.close();
		}
	}
	
	private boolean databaseExists(String database) {
		File dbFile = new File(Constant.PACKAGENAME+"databases/"+database);
		return dbFile.exists();
	}
	
	private boolean checkDatabaseExists() {
		boolean result = true;
		HashMap<String, ArrayList<String>> tsMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, String> tnMap = new HashMap<String, String>();
		HashMap<String, String> fsMap = new HashMap<String, String>();
		HashMap<String, String> fNMap = new HashMap<String, String>();
		HashMap<String, String> fsPkMap = new HashMap<String, String>();
		ArrayList<ArrayList<String>> fkList = new ArrayList<ArrayList<String>>();
		SharedPreferences tablePref = context.getSharedPreferences(TABLEPREF, Context.MODE_PRIVATE);
		SharedPreferences fieldPref = context.getSharedPreferences(FIELDPREF, Context.MODE_PRIVATE);
		NodeList tableList = dbDocument.getElementsByTagName(DatabaseTag.TABLE);
		int tableLen = tableList.getLength();
		int i = 0;
		while (i < tableLen) {
			Element table = (Element) tableList.item(i);
			String tableId = table.getAttribute(DatabaseTag.TABLE_ID);
			String tableName = table.getAttribute(DatabaseTag.TABLE_NAME);
			tnMap.put(tableName, tableId);
			if (!tablePref.getString(tableId, "null").equals("")) {
				result = false;
				i = tableLen;
			}
			else {
				NodeList fieldList = table.getChildNodes();
				int fieldLen = fieldList.getLength();
				if (fieldLen > 0) {
					int j = 0;
					ArrayList<String> tableElements = new ArrayList<String>();
					while (j < fieldLen) {
						Element field = (Element) fieldList.item(j);
						String fieldId = field.getAttribute(DatabaseTag.FIELD_ID);
						String fieldType = field.getAttribute(DatabaseTag.FIELD_TYPE);
						String fieldSize = field.getAttribute(DatabaseTag.FIELD_SIZE);
						String fieldName = field.getAttribute(DatabaseTag.FIELD_NAME);
						fNMap.put(fieldId, fieldName);
						fsMap.put(fieldId, fieldType);
						tableElements.add(DatabaseAttribute.FIELD+fieldId);
						if (fieldType.equals(DatabaseAttribute.VARCHAR)) {
							fieldType = fieldType+"("+fieldSize+")";
						}
						String fieldTypeValue = fieldType;
						if (field.hasAttribute(DatabaseTag.FIELD_PK)) {
							fsPkMap.put(tableId, fieldId);
							fieldTypeValue += " UNIQUE, ";
						}
						else if ((field.hasAttribute(DatabaseTag.FIELD_FORIEIGNTABLE)) 
								&& (field.hasAttribute(DatabaseTag.FIELD_FORIEIGNFIELD))) {
							ArrayList<String> fk = new ArrayList<String>();
							String foreignTableId = field.getAttribute(DatabaseTag.FIELD_FORIEIGNTABLE);
							String foreignFieldId = field.getAttribute(DatabaseTag.FIELD_FORIEIGNFIELD);
							fieldTypeValue += foreignTableId+" "+foreignFieldId;
							fk.add(tableId);
							fk.add(fieldId);
							fk.add(foreignTableId);
							fk.add(foreignFieldId);
							fkList.add(fk);
							
						}
						if (!fieldPref.getString(fieldId, "null").equals(fieldTypeValue)) {
							result = false;
							j = fieldLen;
							i = tableLen;
						}
						else {
							j++;
						}
					}
					tsMap.put(tableId, tableElements);
				}
				i++;
			}
		}
		if (result) {
			tablesMap = tsMap;
			tablesNameMap = tnMap;
			fieldsNameMap = fNMap;
			fieldsTypeMap = fsMap;
			fieldsPKMap = fsPkMap;
			foreignKeyList = fkList;
		}
		return result;
	}
	
	private void createTable() {
		SharedPreferences.Editor editorTablePref = context.getSharedPreferences(TABLEPREF, Context.MODE_PRIVATE).edit();
		SharedPreferences.Editor editorFieldPref = context.getSharedPreferences(FIELDPREF, Context.MODE_PRIVATE).edit();
		NodeList tableList = dbDocument.getElementsByTagName(DatabaseTag.TABLE);
		int tableLen = tableList.getLength();
		for (int i=0; i<tableLen; i++) {
			String createquery = "CREATE TABLE IF NOT EXISTS ";
			Element table = (Element) tableList.item(i);
			String typeSync = table.getAttribute(DatabaseTag.TABLE_SYNC);
			String tableId = table.getAttribute(DatabaseTag.TABLE_ID);
			String tableOriginalName = table.getAttribute(DatabaseTag.TABLE_NAME);
			tablesNameMap.put(tableOriginalName, tableId);
			editorTablePref.putString(tableId, "");
			String tableName = DatabaseAttribute.TABLE+tableId;
			ArrayList<String> tableElements = new ArrayList<String>();
			String id = DatabaseAttribute.ID+tableId;
			String gid = DatabaseAttribute.GID+tableId;
			createquery += tableName+" ("+id+" VARCHAR(255), "+gid+" VARCHAR(255), "+DatabaseAttribute.STATE+" INTEGER, ";
			NodeList fieldList = table.getChildNodes();
			int fieldLen = fieldList.getLength();
			ArrayList<ArrayList<String>> foreignKeyTable = new ArrayList<ArrayList<String>>();
			HashMap<String, ArrayList<String>> systemFields = new HashMap<String, ArrayList<String>>();
			if (fieldLen > 0) {
				for (int j=0; j<fieldLen; j++) {
					//foreignKey has 4 elements (tid, fid, ftid, ffid)
					ArrayList<String> foreignKey = new ArrayList<String>();	
					Element field = (Element) fieldList.item(j);
					String fieldId = field.getAttribute(DatabaseTag.FIELD_ID);
					String fieldName = field.getAttribute(DatabaseTag.FIELD_NAME);
					fieldsNameMap.put(fieldId, fieldName);
					String fieldNewName = DatabaseAttribute.FIELD+fieldId;
					tableElements.add(fieldNewName);
					String fieldType = field.getAttribute(DatabaseTag.FIELD_TYPE);
					String fieldSize = field.getAttribute(DatabaseTag.FIELD_SIZE);
					fieldsTypeMap.put(fieldId, fieldType);
					if (fieldType.equals(DatabaseAttribute.VARCHAR)) {
						fieldType = fieldType+"("+fieldSize+")";
					}
					String fieldSync = field.getAttribute(DatabaseTag.FIELD_SYNC);
					String fieldTypeValue = fieldType;
					if (field.hasAttribute(DatabaseTag.FIELD_PK)) {
						fieldsPKMap.put(tableId, fieldId);
						createquery += fieldNewName+" "+fieldType+" UNIQUE, ";
						fieldTypeValue += " UNIQUE, ";
					}
					
					else if ((field.hasAttribute(DatabaseTag.FIELD_FORIEIGNTABLE)) &&
							(field.hasAttribute(DatabaseTag.FIELD_FORIEIGNFIELD))) {
						String foreignTableId = field.getAttribute(DatabaseTag.FIELD_FORIEIGNTABLE);
						String foreignFieldId = field.getAttribute(DatabaseTag.FIELD_FORIEIGNFIELD);
						fieldTypeValue += foreignTableId+" "+foreignFieldId;
						createquery += fieldNewName+" "+fieldType+", ";
						foreignKey.add(tableId);
						foreignKey.add(fieldId);
						foreignKey.add(foreignTableId);
						foreignKey.add(foreignFieldId);
						foreignKeyList.add(foreignKey);	
						foreignKeyTable.add(foreignKey);
					}
					else {
						createquery += fieldNewName+" "+fieldType+", ";
					}
					
					//check system fields setting
					if ((field.hasAttribute(DatabaseTag.FIELD_SYSTABLE)) && (field.hasAttribute(DatabaseTag.FIELD_SYSFIELD))) {
						ArrayList<String> systemField = new ArrayList<String>();
						systemField.add(field.getAttribute(DatabaseTag.FIELD_SYSTABLE));
						systemField.add(field.getAttribute(DatabaseTag.FIELD_SYSFIELD));
						systemFields.put(fieldId, systemField);
					}
					
					editorFieldPref.putString(fieldId, fieldTypeValue);
				}
			}
			
			tablesMap.put(tableId, tableElements);
			
			createquery = createquery.substring(0, createquery.length()-2);
			createquery += ");";
			
			Log.i("info", "query "+createquery);
			sqlite.execSQL(createquery);
			
			if (systemFields.size() > 0) {
				//insert system fields value (contacts, events, tasks)
				HashMap<String, ArrayList<String>> contactFields = new HashMap<String, ArrayList<String>>();
				HashMap<String, ArrayList<String>> eventFields = new HashMap<String, ArrayList<String>>();
				HashMap<String, ArrayList<String>> taskFields = new HashMap<String, ArrayList<String>>();
				for (String key : systemFields.keySet()) {
					if (Integer.valueOf(systemFields.get(key).get(0)) == DatabaseAttribute.CONTACT) {
						contactFields.put(key, systemFields.get(key));
					}
					//else for event and task
				}
				
				if (contactFields.size() > 0) {
					//contact object
					//contactFields tableId sqlite
					new Contact(tableId, contactFields);
				}
				
				if (eventFields.size() > 0) {
					//event object
				}
				
				if (taskFields.size() > 0) {
					//task object
				}
			}
		}
		editorTablePref.commit();
		editorFieldPref.commit();
	}
	
	private void deleteDatabase(String database) {
		context.deleteDatabase(database);
	}

	public byte[] syncImportTable(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] tableNb = new byte[Binary.INTBYTE];
		bis.read(tableNb, 0, tableNb.length);
		bos.write(tableNb, 0, tableNb.length);
		int tableNbInt = Binary.byteArrayToInt(tableNb);
		for (int i=0; i<tableNbInt; i++) {
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
			for (int j=0; j<fieldsNbInt; j++) {
				byte[] field = new byte[Binary.INTBYTE];
				bisFields.read(field, 0, field.length);
				fieldList.add(Binary.byteArrayToInt(field));
			}

			//Get number of records
			byte[] recordsNb = new byte[Binary.INTBYTE];
			bis.read(recordsNb, 0, recordsNb.length);
			int recordsNbInt = Binary.byteArrayToInt(recordsNb);
			bos.write(recordsNb, 0, recordsNb.length);
			ArrayList<ArrayList<Object>> recordsList = new ArrayList<ArrayList<Object>>();
			ArrayList<Integer> syncTypeList = new ArrayList<Integer>();
			for (int k=0; k<recordsNbInt; k++) {
				ArrayList<Object> valueList = new ArrayList<Object>();
				//Get type of synchronization
				byte[] syncType = new byte[Binary.TYPEBYTE];
				bis.read(syncType, 0, syncType.length);
				int syncTypeInt = Binary.byteArrayToType(syncType);
				syncTypeList.add(syncTypeInt);
				bos.write(syncType, 0, syncType.length);
				
				//Get local id
				byte[] localId = new byte[Binary.INTBYTE];
				bis.read(localId, 0, localId.length);
				String localIdString = String.valueOf(tableIdInt+""+(k+1));
				valueList.add(localIdString);
				bos.write(Binary.intToByteArray(k+1), 0, Binary.intToByteArray(k+1).length);
				
				//Get global id
				byte[] globalId = new byte[Binary.INTBYTE];
				bis.read(globalId, 0, globalId.length);
				int globalIdInt = Binary.byteArrayToInt(globalId);
				valueList.add(String.valueOf(globalIdInt));
				bos.write(globalId, 0, globalId.length);
				//Get each record's information
				for (int l=0; l<fieldsNbInt; l++) {
					//Get length of value
					byte[] valueLength = new byte[Binary.INTBYTE];
					bis.read(valueLength, 0, valueLength.length);
					int valueLengthInt = Binary.byteArrayToInt(valueLength);
					//Get value
					byte[] value = new byte[valueLengthInt];
					bis.read(value, 0, value.length);
					Object valueObject = Binary.byteArrayToObject(value, fieldsTypeMap.get(String.valueOf(fieldList.get(l))));
					valueList.add(valueObject);
				}
				if (syncTypeInt != DatabaseAttribute.SYNCHRONIZED) {
					recordsList.add(valueList);
				}
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
	
	public byte[] syncExportTable() {
		Log.i("info", "syncExportTable ");
		int tableNbInt = 0;
		Set<String> keys = tablesMap.keySet();
		Log.i("info", "tablesmap keys "+keys.toString());
		HashMap<String, ArrayList<HashMap<Object, Object>>> tidMap = 
			new HashMap<String, ArrayList<HashMap<Object, Object>>>();
		for (String key : keys) {
			String table = DatabaseAttribute.TABLE+key;
			//String selection = table+".STATE != "+DatabaseField.SYNCHRONIZED;
			String selection = "STATE != "+DatabaseAttribute.SYNCHRONIZED;
			Log.i("info", "selection "+selection);
			Cursor cursor = sqlite.query(table, null, selection, null, null, null, null);
			//Cursor cursor = selectQuery(key, null, null);
			Log.i("info", "cursor count "+cursor.getCount());
			if (cursor.getCount() > 0) {
				int cursorCount = cursor.getCount();
				ArrayList<HashMap<Object, Object>> records = new ArrayList<HashMap<Object, Object>>();
				cursor.moveToFirst();
				for (int i=0; i<cursorCount; i++) {
					HashMap<Object, Object> record = new HashMap<Object, Object>();
					String[] columns = cursor.getColumnNames();
					int columnsNb = columns.length;
					boolean checkSyncType = true;
					for (int column=0; column<columnsNb; column++) {
						Log.i("info", "column name "+columns[column]+" its value "+getCursorValue(cursor, columns[column]));
						record.put(columns[column], getCursorValue(cursor, columns[column]));
						if ((columns[column].equals(DatabaseAttribute.STATE)) && (Integer.valueOf(getCursorValue(cursor, columns[column]).toString()) == DatabaseAttribute.SYNCHRONIZED)) {
							checkSyncType = false;
						}
					}
					if (checkSyncType) {
						records.add(record);
					}
					cursor.moveToNext();
				}
				tidMap.put(key, records);
				tableNbInt += 1;
			}
			DatabaseAdapter.closeCursor(cursor);
		}
		Log.i("info", "tidmap "+tidMap.toString());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//tableNb
		byte[] tableNb = Binary.intToByteArray(tableNbInt);
		bos.write(tableNb, 0, tableNb.length);
		Set<String> tidKeys = tidMap.keySet();
		for (String tidKey : tidKeys) {
			//tableId
			int tableIdInt = Integer.valueOf(tidKey);
			byte[] tableId = Binary.intToByteArray(tableIdInt);
			bos.write(tableId, 0, tableId.length);
			
			//fieldsNb
			ArrayList<String> fields = tablesMap.get(tidKey);
			int fieldsNbInt = fields.size();
			byte[] fieldsNb = Binary.intToByteArray(fieldsNbInt);
			bos.write(fieldsNb, 0, fieldsNb.length);

			for (int j=0; j<fieldsNbInt; j++) {
				int fieldInt = Integer.valueOf(fields.get(j).split("_")[1]);
				byte[] field = Binary.intToByteArray(fieldInt);
				bos.write(field, 0, field.length);
			}
			//recordsNb
			int recordsNbInt = tidMap.get(tidKey).size();
			Log.i("info", "export record nb "+recordsNbInt);
			byte[] recordsNb = Binary.intToByteArray(recordsNbInt);
			bos.write(recordsNb, 0, recordsNb.length);
			for (int k=0; k<recordsNbInt; k++) {
				HashMap<Object, Object> record = tidMap.get(tidKey).get(k);
				//syncType
				int syncTypeInt = Integer.valueOf(record.get(DatabaseAttribute.STATE).toString());
				byte[] syncType = Binary.typeToByteArray(syncTypeInt);
				bos.write(syncType, 0, syncType.length);

				//local id
				int localIdInt = Integer.valueOf(record.get(DatabaseAttribute.ID+tidKey).toString());
				byte[] localId = Binary.intToByteArray(localIdInt);
				bos.write(localId, 0, localId.length);

				//global id is
				int globalIdInt = Integer.valueOf(record.get(DatabaseAttribute.GID+tidKey).toString());
				byte[] globalId = Binary.intToByteArray(globalIdInt);
				bos.write(globalId, 0, globalId.length);

				//value
				if (syncTypeInt != DatabaseAttribute.DELETEVALUE) {
					for (int fid=0; fid<fieldsNbInt; fid++) {
						String valueType = fieldsTypeMap.get(fields.get(fid).split("_")[1]);
						Log.i("info", "valueType "+valueType);
						byte[] value = null;
						byte[] valueLenth = null;
						
						if (record.get(fields.get(fid)) == null) {
							if (DmaHttpClient.getServerInfo() == 1) {
								value = Binary.objectToByteArray(record.get(fields.get(fid)), valueType);
								int valueLengthInt = value.length;
								valueLenth = Binary.intToByteArray(valueLengthInt);
							}
							else if (DmaHttpClient.getServerInfo() == 2) {
								value = Binary.stringToByteArray(null);
								Log.i("info", "synchronized value "+value);
								valueLenth = Binary.intToByteArray(-1);
							}
						}
						else {
							value = Binary.objectToByteArray(record.get(fields.get(fid)), valueType);
							int valueLengthInt = value.length;
							valueLenth = Binary.intToByteArray(valueLengthInt);
							if (valueType.equals(DatabaseAttribute.BLOB)) {
								ArrayList<Object> blobData = new ArrayList<Object>();
								blobData.add(fields.get(fid).split("_")[1]);
								blobData.add(record.get(fields.get(fid)));
								blobRecords.add(blobData);
							}
						}
						bos.write(valueLenth, 0, valueLenth.length);
						bos.write(value, 0, value.length);
					}
				}
			}
		}
		byte[] result = bos.toByteArray();
		try {
			bos.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void updateIds(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		byte[] tableNb = new byte[Binary.INTBYTE];
		bis.read(tableNb, 0, tableNb.length);
		int tableNbInt = Binary.byteArrayToInt(tableNb);
		for (int i=0; i<tableNbInt; i++) {
			//Get table's id
			byte[] tableId = new byte[Binary.INTBYTE];
			bis.read(tableId, 0, tableId.length);
			int tableIdInt = Binary.byteArrayToInt(tableId);
			//Get number of records
			byte[] recordsNb = new byte[Binary.INTBYTE];
			bis.read(recordsNb, 0, recordsNb.length);
			int recordsNbInt = Binary.byteArrayToInt(recordsNb);
			for (int k=0; k<recordsNbInt; k++) {
				//Get type of synchronization
				byte[] syncType = new byte[Binary.TYPEBYTE];
				bis.read(syncType, 0, syncType.length);
				int syncTypeInt = Binary.byteArrayToType(syncType);
				//Get local id
				byte[] localId = new byte[Binary.INTBYTE];
				bis.read(localId, 0, localId.length);
				int localIdInt = Binary.byteArrayToInt(localId);
				//Get global id
				byte[] globalId = new byte[Binary.INTBYTE];
				bis.read(globalId, 0, globalId.length);
				int globalIdInt = Binary.byteArrayToInt(globalId);
				updateGid(tableIdInt, localIdInt, globalIdInt);
			}
		}
	}
	
	private static void updateGid(int tableId, int lid, int gid) {
		Log.i("info", "updategid tableId "+tableId+" lid "+lid+" gid "+gid);
		String table = DatabaseAttribute.TABLE+tableId;
		ContentValues values = new ContentValues();
		values.put(DatabaseAttribute.GID+tableId, String.valueOf(gid));
		values.put(DatabaseAttribute.STATE, DatabaseAttribute.SYNCHRONIZED);   //update sync type at the same time 
		String whereClause = "";
		if (tableId == 0) {
			String idValue = tableId+""+lid;
			whereClause = DatabaseAttribute.ID+tableId+"=\'"+idValue+"\'";
		}
		else {
			whereClause = DatabaseAttribute.ID+tableId+"=\'"+lid+"\'";
		}
		sqlite.update(table, values, whereClause, null);
	}
	
	private void updateTable(int tableId, ArrayList<Integer> fields, ArrayList<Integer> syncTypeList,
			ArrayList<ArrayList<Object>> records) {
		int syncTypeListSize = syncTypeList.size();
		for (int i=0; i<syncTypeListSize; i++) {
			switch (syncTypeList.get(i)) {
				case DatabaseAttribute.ADDVALUE:
					insertValues(tableId, fields, records.get(i));
					break;
				case DatabaseAttribute.UPDATEVALUE:
					updateValues(tableId, fields, records.get(i));
					break;
				case DatabaseAttribute.DELETEVALUE:
					deleteValues(tableId, fields, records.get(i));
					break;
			}
		}
	}
	
	//Add values and don't check primary key
	private void insertValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record) {
		Log.i("info", "add table value "+DatabaseAttribute.TABLE+tableId+" record "+record);
		Cursor cursorAllRows = selectQuery(String.valueOf(tableId), null, null);
		int newId = cursorAllRows.getCount()+1;
		DatabaseAdapter.closeCursor(cursorAllRows);
		Log.i("info", "calculated new id "+newId);
		String idValue = tableId+""+newId;
		Log.i("info", "idvalue is "+idValue);
		ContentValues values = new ContentValues();
		values.put(DatabaseAttribute.ID+tableId, idValue);
		values.put(DatabaseAttribute.GID+tableId, (String)record.get(1));
		values.put(DatabaseAttribute.STATE, DatabaseAttribute.SYNCHRONIZED);
		int fieldsNb = fieldsList.size();
		for (int i=0; i<fieldsNb; i++) {
			//check field's type
			if (fieldsTypeMap.get(fieldsList.get(i).toString()).equals(DatabaseAttribute.BLOB)) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(bos);
					oos.writeObject(record.get(i+2));
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				values.put(DatabaseAttribute.FIELD+fieldsList.get(i), bos.toByteArray());
			}
			else {
				values.put(DatabaseAttribute.FIELD+fieldsList.get(i), (String)record.get(i+2));
			}
		}
		String tableName = DatabaseAttribute.TABLE+tableId;
		long insertResult = sqlite.insert(tableName, null, values);
		Log.i("info", "insertresult "+insertResult);
	}
	
	private void updateValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record) {
		Log.i("info", "update table value "+DatabaseAttribute.TABLE+tableId+" fieldList "+fieldsList+" record "+record);
		int fieldsNb = fieldsList.size();
		String tableName = DatabaseAttribute.TABLE+tableId;
		//String newValue = DatabaseField.STATE+"="+DatabaseField.SYNCHRONIZED+" AND "+DatabaseField.GID+tableId+"="+record.get(1);
		String newValue = "";
		for (int i=0; i<fieldsNb; i++) {
			if (i == fieldsNb-1) {
				newValue += DatabaseAttribute.STATE+"="+DatabaseAttribute.SYNCHRONIZED+" ,"+DatabaseAttribute.FIELD+fieldsList.get(i)+"=\'"+record.get(i+2)+"\'";
			}
			else {
				newValue += DatabaseAttribute.FIELD+fieldsList.get(i)+"=\'"+record.get(i+2)+"\', ";
			}
		}
		String update = "UPDATE "+tableName+" SET "+newValue+" WHERE "+DatabaseAttribute.GID+tableId+"=\'"+record.get(1)+"\';";
		Log.i("info", "update "+update);
		sqlite.execSQL(update);
	}
	
	private void deleteValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record) {
		String delete = "DELETE FROM "+DatabaseAttribute.TABLE+tableId+" WHERE "+DatabaseAttribute.GID+tableId+"=\'"+record.get(1)+"\';";
		sqlite.execSQL(delete);
	}
	
	//Generate primary key for KEY and INTEGER
	private static Object generatePK(String type, Object idValue) {
		Object result = null;
		if (type.equals(DatabaseAttribute.KEY)) {
			result = KeyGenerator.getKeyGenerated();
		}
		else if (type.equals(DatabaseAttribute.INTEGER)) {
			Log.i("info", "idValue "+idValue);
			result = idValue;
		}
		return result;
	}
	
	public static void clearTable(String tableId) {
		//Check if table has blob data
		Cursor cursor = sqlite.query(DatabaseAttribute.TABLE+tableId, null, null, null, null, null, null);
		if (cursor.getCount() != 0) {
			String[] columnsNames = cursor.getColumnNames();
			boolean hasBlob = false;
			int check = 0;
			while (check < columnsNames.length) {
				if (columnsNames[check].contains(DatabaseAttribute.FIELD)) {
					if (fieldsTypeMap.get(columnsNames[check].split("_")[1]).equals(DatabaseAttribute.BLOB)) {
						hasBlob = true;
						check = columnsNames.length;
					}
				}
				check ++;
			}
			if (hasBlob) {
				int cursorCount = cursor.getCount();
				cursor.moveToFirst();
				for (int i=0; i<cursorCount; i++) {
					String[] columns = cursor.getColumnNames();
					int columnsNb = columns.length;
					for (int column=0; column<columnsNb; column++) {
						if (columnsNames[column].contains(DatabaseAttribute.FIELD)) {
							if (fieldsTypeMap.get(columns[column].split("_")[1]).equals(DatabaseAttribute.BLOB)) {
								String imageName = (String)getCursorValue(cursor, columns[column]);
								//Delete the image
								new File(Constant.PACKAGENAME+imageName).delete();
							}
						}
					}
					cursor.moveToNext();
				}
				DatabaseAdapter.closeCursor(cursor);
			}
		}
		sqlite.execSQL("DELETE FROM "+DatabaseAttribute.TABLE+tableId);
	}
	
	public static Cursor selectQuery(String tableId, ArrayList<Integer> fieldList, ArrayList<Object> valueList) {
		Cursor result = null;
		String table = DatabaseAttribute.TABLE+tableId;
		Log.i("info", "tableid "+table);
		String selection = createSelectionString(fieldList, valueList);
		Log.i("info", "selection "+selection);
		result = sqlite.query(table, null, selection, null, null, null, null);
		return result;
	}
	
	public static Cursor selectQuery(ArrayList<String> tables, ArrayList<ArrayList<String>> columns, Object filter) {
		Cursor result = null;
		String table = createTableString(tables);
		Log.i("info", "table "+table);
		String[] projectionIn = createProjectionStrings(columns);
		Log.i("info", "projectionIn "+projectionIn);
		String selection = createSelectionString(tables, filter);
		Log.i("info", "selection selectQuery "+selection);
		result = sqlite.query(table, projectionIn, selection, null, null, null, null);
		return result;
	}

	//Add values and check primary key
	public static void addQuery(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record) {
		//record has 1 more element than fieldsList, the first element of record is the new ID
		int fieldsNb = fieldsList.size();
		String id = DatabaseAttribute.ID+tableId;
		String gid = DatabaseAttribute.GID+tableId;
		String idValue = String.valueOf(tableId+""+record.get(0));
		String fields = "("+id+", "+gid+", "+DatabaseAttribute.STATE+", ";
		String values = "('"+idValue+"\', 0, "+DatabaseAttribute.ADDVALUE+", ";
		for (int i=0; i<fieldsNb; i++) {
			if ((fieldsPKMap.containsKey(String.valueOf(tableId))) && 
					(fieldsPKMap.get(String.valueOf(tableId)).equals(fieldsList.get(i))) &&
					(record.get(i+1) == null)) {
				Log.i("info", "in case of filed has pk field, generate its value");
				record.remove(i+1);
				record.add(i+1, generatePK(fieldsTypeMap.get(fieldsList.get(i)), record.get(0)));
			}
			
			fields += DatabaseAttribute.FIELD+fieldsList.get(i);
			values += "\'"+record.get(i+1)+"\'";
			
			if (i != fieldsNb-1) {
				fields += ", ";
				values += ", ";
			}
		}
		if ((!fieldsList.contains(fieldsPKMap.get(String.valueOf(tableId)))) &&
				(fieldsPKMap.containsKey(String.valueOf(tableId)))) {
			Log.i("info", "in case of field list hasn't pk field, generate its value");
			fields += ", "+DatabaseAttribute.FIELD+fieldsPKMap.get(String.valueOf(tableId));
			values += ", \'"+generatePK(fieldsTypeMap.get(fieldsPKMap.get(String.valueOf(tableId))), record.get(0))+"\'";
		}
		fields += ")";
		values += ")";
		String tableName = DatabaseAttribute.TABLE+tableId;
		String insert = "INSERT INTO "+tableName+" "+fields+" VALUES"+values+";";
		Log.i("info", "query "+insert);
		sqlite.execSQL(insert);
	}
	
	public static void updateQuery(String tableId, ArrayList<Integer> fieldList, ArrayList<Object> valueList, HashMap<Object, Object> record) {
		Log.i("info", "updateQuery Record "+record+" fieldList "+fieldList+" valueList "+valueList);
		//Check if state is Synchronized, update to updatevalue. Otherwise, don't change state's value.
		String table = DatabaseAttribute.TABLE+tableId;
		int newState = DatabaseAttribute.ADDVALUE;
		if ((record.containsKey(DatabaseAttribute.STATE)) && (record.get(DatabaseAttribute.STATE) != null)) {
			if (Integer.valueOf(record.get(DatabaseAttribute.STATE).toString()) == DatabaseAttribute.SYNCHRONIZED) {
				newState = DatabaseAttribute.UPDATEVALUE;
			}
			record.remove(DatabaseAttribute.STATE);
		}
		ContentValues values = new ContentValues();
		values.put(DatabaseAttribute.STATE, newState);
		int listSize = fieldList.size();
		for (int i=0; i<listSize; i++) {
			values.put(DatabaseAttribute.FIELD+fieldList.get(i).toString(), valueList.get(i).toString());
			record.remove(DatabaseAttribute.FIELD+fieldList.get(i));
		}
		Log.i("info", "values "+values);
		String whereClause = createWhereClause(tableId, record);
		Log.i("info", "whereclause "+whereClause);
		sqlite.update(table, values, whereClause, null);
	}
	
	public static void deleteQuery(String tableId, HashMap<Object, Object> record) {
		Log.i("info", "deletequery table "+tableId+" record "+record);
		if (record.containsKey(DatabaseAttribute.STATE)) {
			String table = DatabaseAttribute.TABLE+tableId;
			String whereClause = createWhereClause(tableId, record);
			if (Integer.valueOf(record.get(DatabaseAttribute.STATE).toString()) == DatabaseAttribute.SYNCHRONIZED) {
				ContentValues values = new ContentValues();
				values.put(DatabaseAttribute.STATE, DatabaseAttribute.DELETEVALUE);
				Log.i("info", "whereclause "+whereClause+" values "+values);
				sqlite.update(table, values, whereClause, null);
			}
			else {
				Log.i("info", "whereclause "+whereClause);
				sqlite.delete(table, whereClause, null);
			}
		}
	}
	
	public static int getTableNb() {
		return tablesMap.size();
	}
	
	public static Set<String> getTableIds() {
		return tablesMap.keySet();
	}
	
	public static String getFieldName(String fieldId) {
		String result = "";
		if (fieldsNameMap.containsKey(fieldId)) {
			result = fieldsNameMap.get(fieldId);
		}
		return result;
	}
	
	public static String getTableIdByName(String name) {
		return tablesNameMap.get(name);
	}
	
	public static HashMap<String, String> getFieldsNameMap() {
		return fieldsNameMap;
	}
	
	public static void beginTransaction() {
		STARTTRANSACTION = true;
		sqlite.execSQL("BEGIN TRANSACTION;");
	}
	
	public static void rollbackTransaction() {
		STARTTRANSACTION = false;
		sqlite.execSQL("ROLLBACK TRANSACTION;");
	}
	
	public static void commitTransaction() {
		STARTTRANSACTION = false;
		sqlite.execSQL("COMMIT TRANSACTION;");
	}
	
	public static boolean hasStartTransaction() {
		return STARTTRANSACTION;
	}
	
	public static void cleanTables() {
		Log.i("info", "cleantables");
		Set<String> keys = tablesMap.keySet();
		for (String key : keys) {
			String table = DatabaseAttribute.TABLE+key;
			Log.i("info", "table "+table);
			String id = DatabaseAttribute.ID+key;
			String[] projectionIn = new String[]{id, DatabaseAttribute.STATE};
			Cursor result = sqlite.query(table, projectionIn, null, null, null, null, null);
			result.moveToFirst();
			int count = result.getCount();
			for (int i=0; i<count; i++) {
				String whereClause = id+" = \'"+result.getString(result.getColumnIndexOrThrow(id))+"\'";
				if (result.getInt(result.getColumnIndexOrThrow(DatabaseAttribute.STATE)) == DatabaseAttribute.DELETEVALUE) {
					Log.i("info", "delete value");
					sqlite.delete(table, whereClause, null);					
				}
				result.moveToNext();
			}
			DatabaseAdapter.closeCursor(result);
		}
	}
	
	public static Object getCursorValue(Cursor cursor, String field) {
		if (field.indexOf(DatabaseAttribute.FIELD) != -1) {
			String fieldId = Integer.valueOf(field.split("_")[1]).toString();
			if (fieldsTypeMap.get(fieldId).equals(DatabaseAttribute.INTEGER)) {
				return cursor.getInt(cursor.getColumnIndexOrThrow(field));
			}
			else if (fieldsTypeMap.get(fieldId).equals(DatabaseAttribute.DOUBLE)) {
				return cursor.getDouble(cursor.getColumnIndexOrThrow(field));
			}
			else if (fieldsTypeMap.get(fieldId).equals(DatabaseAttribute.BLOB)) {
				String result = null;
				if (cursor.isNull(cursor.getColumnIndex(field))) {
					result = "";
				}
				else {
					try {
						result = new String(cursor.getBlob(cursor.getColumnIndexOrThrow(field)), "UTF-8");
					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} 
					catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
				return result;
			}
			else {
				return cursor.getString(cursor.getColumnIndexOrThrow(field));
			}
		}
		else {
			return cursor.getString(cursor.getColumnIndexOrThrow(field));
		}
	}
	
	public static ArrayList<ArrayList<Object>> getBlobRecords() {
		return blobRecords;
	}
	
	public static void closeCursor(Cursor cursor) {
		if (cursor != null) {
			cursor.close(); 
		}
	}
	
	private static String createWhereClause(String tableId, HashMap<Object, Object> record) {
		String result = "";
		if (record.containsKey(DatabaseAttribute.ID+tableId)) {
			result += DatabaseAttribute.ID+tableId+" = \'"+record.get(DatabaseAttribute.ID+tableId)+"\'";
		}
		return result;
	}
	
	private static String createTableString(ArrayList<String> tables) {
		Log.i("info", "tables "+tables);
		String result = "";
		int size = tables.size();
		for (int i=0; i<size; i++) {
			if (i != size-1) {
				result += DatabaseAttribute.TABLE+tables.get(i)+", ";
			}
			else {
				result += DatabaseAttribute.TABLE+tables.get(i);
			}	
		}
		return result;
	}
	
	private static String[] createProjectionStrings(ArrayList<ArrayList<String>> columns) {
		if (columns == null) {
			return null;
		}
		else {
			int size = columns.size();
			String[] result = new String[size];
			for (int i=0; i<size; i++) {
				ArrayList<String> column = columns.get(i);
				result[i] = DatabaseAttribute.TABLE+column.get(0)+"."+DatabaseAttribute.FIELD+column.get(1);
			}
			return result;
		}
	}
	
	private static String createSelectionString(ArrayList<String> tables, Object filter) {
		String result = "";
		if (filter == null) {
			int tablesSize = tables.size();
			for (int i=0; i<tablesSize; i++) {
				if (i == 0) {
					result +=  DatabaseAttribute.TABLE+tables.get(i)+".STATE != "+DatabaseAttribute.DELETEVALUE;
				}
				else {
					result +=  " AND "+DatabaseAttribute.TABLE+tables.get(i)+".STATE != "+DatabaseAttribute.DELETEVALUE;
				}
			}
			if (!createSelectionFKString(tables).equals("")) {
				result += " AND "+createSelectionFKString(tables);	
			}
			return result;
		}
		else {
			int filterSize = ((ArrayList<?>)filter).size();
			int filterNb = filterSize / 4;
			for (int i=0; i<filterNb; i++) {
				//Link is considered as AND
				if (i == 0) {
					String field = (String)DatabaseAttribute.FIELD+((ArrayList<?>)filter).get(4*i);
					Set<String> keySet = tablesMap.keySet();
					String tableName = null;
					for (String s : keySet) {
						if (tablesMap.get(s).contains(field)) {
							tableName = DatabaseAttribute.TABLE+s;
							result += tableName+"."+field;
						}
					}
					Object operator = Function.getOperator(((ArrayList<?>)filter).get(4*i+1));
					result += " "+operator+" ";
					Object value = "\'"+((ArrayList<?>)filter).get(4*i+2)+"\'";
					result += value+" AND "+tableName+".STATE != "+DatabaseAttribute.DELETEVALUE;
				}
				else {
					result += " AND ";
					String field = (String) DatabaseAttribute.FIELD+((ArrayList<?>)filter).get(4*i);
					Set<String> keySet = tablesMap.keySet();
					String tableName = null;
					for (String s : keySet) {
						if (tablesMap.get(s).contains(field)) {
							tableName = DatabaseAttribute.TABLE+s;
							result += tableName+"."+field;
						}
					}
					Object operator = Function.getOperator(((ArrayList<?>)filter).get(4*i+1));
					result += " "+operator+" ";
					Object value = "\'"+((ArrayList<?>)filter).get(4*i+2)+"\'";
					result += value+" AND "+tableName+".STATE != "+DatabaseAttribute.DELETEVALUE;
				}
			}
			if (!createSelectionFKString(tables).equals("")) {
				if (result.equals("")) {
					result += createSelectionFKString(tables);	
				}
				else {
					result += " AND "+createSelectionFKString(tables);
				}
			}
			return result;	
		}
	}
	
	private static String createSelectionString(ArrayList<Integer> fieldList, ArrayList<Object> valueList) {
		if ((fieldList == null) && (valueList == null)) {
			return null;
		}
		else {
			String result = "";
			int size = fieldList.size();
			for (int i=0; i<size; i++) {
				if (i == 0) {
					result += "STATE != "+DatabaseAttribute.DELETEVALUE+" AND "+DatabaseAttribute.FIELD+fieldList.get(i)+" = \'"+valueList.get(i)+"\'";
				}
				else {
					result += " AND "+DatabaseAttribute.FIELD+fieldList.get(i)+" = \'"+valueList.get(i)+"\'";
				}
			}
			return result;
		}		
	}
	
	private static String createSelectionFKString(ArrayList<String> tables) {
		String result = "";
		int size = foreignKeyList.size();
		if (tables.size() > 1) {
			for (int i=0; i<size; i++) {
				ArrayList<String> foreignKey = foreignKeyList.get(i);
				if ((tables.contains(foreignKey.get(0))) && (tables.contains(foreignKey.get(2)))) {
					if (result != "") {
						result += "AND ";
					}
					result += DatabaseAttribute.TABLE+foreignKey.get(0)+"."+DatabaseAttribute.FIELD+foreignKey.get(1)+" = "+
					DatabaseAttribute.TABLE+foreignKey.get(2)+"."+DatabaseAttribute.FIELD+foreignKey.get(3);
				}
			}
		}		
		return result;
	}
}
