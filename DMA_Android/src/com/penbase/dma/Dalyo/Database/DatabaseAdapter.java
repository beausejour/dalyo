package com.penbase.dma.Dalyo.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.penbase.dma.Binary.Binary;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Constant.DatabaseTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;
import com.penbase.dma.View.ApplicationListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DatabaseAdapter {
	//private final Document dbDocument;
	private Document mDbDocument;
	//private final Context context;
	private Context mContext;
	private static SQLiteDatabase sSqlite = null;
	private static HashMap<String, ArrayList<String>> sTablesMap;
	private static HashMap<String, String> sTablesNameMap;
	private static HashMap<String, String> sFieldsTypeMap;
	private static HashMap<String, String> sFieldsNameMap;
	private static HashMap<String, String> sFieldsPKMap;
	private static ArrayList<ArrayList<String>> sForeignKeyList;
	private String mDbName = null;
	private String mTABLEPREF = "TablePrefFile";
	private String mFIELDPREF = "FieldPrefFile";
	private static boolean STARTTRANSACTION = false;
	private static  ArrayList<ArrayList<Object>> sBlobRecords;
	
	public DatabaseAdapter(Context c, Document d, String database) {
		this.mContext = c;
		this.mDbDocument = d;
		this.mDbName = database;
		this.mTABLEPREF = mDbName+"_"+mTABLEPREF;
		this.mFIELDPREF = mDbName+"_"+mFIELDPREF;
		sTablesMap = new HashMap<String, ArrayList<String>>();		//{tid, [tablename, fieldnames...]}
		sTablesNameMap = new HashMap<String, String>();
		sFieldsTypeMap = new HashMap<String, String>();
		sFieldsNameMap = new HashMap<String, String>();
		sFieldsPKMap = new HashMap<String, String>();
		sForeignKeyList = new ArrayList<ArrayList<String>>();
		sBlobRecords = new ArrayList<ArrayList<Object>>();
		if (mDbDocument.getElementsByTagName(DatabaseTag.TABLE).getLength() > 0) {
			createDatabase(mDbName);
		}
	}
	
	private void createDatabase(String database) throws SQLException{
		Log.i("info", "dbName "+mDbName);
		try{
			if (!databaseExists(database)) {
				Log.i("info", "the database doesn't exist");
				sSqlite = mContext.openOrCreateDatabase(database, 0, null);
				createTable();
			} else if (!checkDatabaseExists()) {
				Log.i("info", "the database isn't the same");
				sSqlite = mContext.openOrCreateDatabase(database, 0, null);
				createTable();
			} else {
				Log.i("info", "the database have nothing to change");
				sSqlite = mContext.openOrCreateDatabase(database, 0, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeDatabase() {
		if ((sSqlite != null) && (sSqlite.isOpen())) {
			sSqlite.close();
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
		SharedPreferences tablePref = mContext.getSharedPreferences(mTABLEPREF, Context.MODE_PRIVATE);
		SharedPreferences fieldPref = mContext.getSharedPreferences(mFIELDPREF, Context.MODE_PRIVATE);
		NodeList tableList = mDbDocument.getElementsByTagName(DatabaseTag.TABLE);
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
			} else {
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
						} else if ((field.hasAttribute(DatabaseTag.FIELD_FORIEIGNTABLE)) 
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
						} else {
							j++;
						}
					}
					tsMap.put(tableId, tableElements);
				}
				i++;
			}
		}
		if (result) {
			sTablesMap = tsMap;
			sTablesNameMap = tnMap;
			sFieldsNameMap = fNMap;
			sFieldsTypeMap = fsMap;
			sFieldsPKMap = fsPkMap;
			sForeignKeyList = fkList;
		}
		return result;
	}
	
	private void createTable() {
		SharedPreferences.Editor editorTablePref = mContext.getSharedPreferences(mTABLEPREF, Context.MODE_PRIVATE).edit();
		SharedPreferences.Editor editorFieldPref = mContext.getSharedPreferences(mFIELDPREF, Context.MODE_PRIVATE).edit();
		NodeList tableList = mDbDocument.getElementsByTagName(DatabaseTag.TABLE);
		int tableLen = tableList.getLength();
		for (int i=0; i<tableLen; i++) {
			String createquery = "CREATE TABLE IF NOT EXISTS ";
			Element table = (Element) tableList.item(i);
			String typeSync = table.getAttribute(DatabaseTag.TABLE_SYNC);
			String tableId = table.getAttribute(DatabaseTag.TABLE_ID);
			String tableOriginalName = table.getAttribute(DatabaseTag.TABLE_NAME);
			sTablesNameMap.put(tableOriginalName, tableId);
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
					sFieldsNameMap.put(fieldId, fieldName);
					String fieldNewName = DatabaseAttribute.FIELD+fieldId;
					tableElements.add(fieldNewName);
					String fieldType = field.getAttribute(DatabaseTag.FIELD_TYPE);
					String fieldSize = field.getAttribute(DatabaseTag.FIELD_SIZE);
					sFieldsTypeMap.put(fieldId, fieldType);
					if (fieldType.equals(DatabaseAttribute.VARCHAR)) {
						fieldType = fieldType+"("+fieldSize+")";
					}
					String fieldSync = field.getAttribute(DatabaseTag.FIELD_SYNC);
					String fieldTypeValue = fieldType;
					if (field.hasAttribute(DatabaseTag.FIELD_PK)) {
						sFieldsPKMap.put(tableId, fieldId);
						createquery += fieldNewName+" "+fieldType+" UNIQUE, ";
						fieldTypeValue += " UNIQUE, ";
					} else if ((field.hasAttribute(DatabaseTag.FIELD_FORIEIGNTABLE)) &&
							(field.hasAttribute(DatabaseTag.FIELD_FORIEIGNFIELD))) {
						String foreignTableId = field.getAttribute(DatabaseTag.FIELD_FORIEIGNTABLE);
						String foreignFieldId = field.getAttribute(DatabaseTag.FIELD_FORIEIGNFIELD);
						fieldTypeValue += foreignTableId+" "+foreignFieldId;
						createquery += fieldNewName+" "+fieldType+", ";
						foreignKey.add(tableId);
						foreignKey.add(fieldId);
						foreignKey.add(foreignTableId);
						foreignKey.add(foreignFieldId);
						sForeignKeyList.add(foreignKey);	
						foreignKeyTable.add(foreignKey);
					} else {
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
			
			sTablesMap.put(tableId, tableElements);
			
			createquery = createquery.substring(0, createquery.length()-2);
			createquery += ");";
			
			Log.i("info", "query "+createquery);
			sSqlite.execSQL(createquery);
			
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
		mContext.deleteDatabase(database);
	}

	public byte[] syncImportTable(byte[] bytes) {
		if (sBlobRecords.size() > 0) {
			sBlobRecords.clear();
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] tableNb = new byte[Constant.INTBYTE];
		bis.read(tableNb, 0, tableNb.length);
		bos.write(tableNb, 0, tableNb.length);
		int tableNbInt = Binary.byteArrayToInt(tableNb);
		for (int i=0; i<tableNbInt; i++) {
			//Get table's id
			byte[] tableId = new byte[Constant.INTBYTE];
			bis.read(tableId, 0, tableId.length);
			int tableIdInt = Binary.byteArrayToInt(tableId);
			bos.write(tableId, 0, tableId.length);
			
			//Get filed's number
			byte[] fieldsNb = new byte[Constant.INTBYTE];
			bis.read(fieldsNb, 0, fieldsNb.length);
			int fieldsNbInt = Binary.byteArrayToInt(fieldsNb);
			
			//Get fields's ids
			byte[] fields = new byte[Constant.INTBYTE*fieldsNbInt];
			bis.read(fields, 0, fields.length);
			ArrayList<Integer> fieldList = new ArrayList<Integer>();
			ByteArrayInputStream bisFields = new ByteArrayInputStream(fields);
			for (int j=0; j<fieldsNbInt; j++) {
				byte[] field = new byte[Constant.INTBYTE];
				bisFields.read(field, 0, field.length);
				fieldList.add(Binary.byteArrayToInt(field));
			}

			//Get number of records
			byte[] recordsNb = new byte[Constant.INTBYTE];
			bis.read(recordsNb, 0, recordsNb.length);
			int recordsNbInt = Binary.byteArrayToInt(recordsNb);
			bos.write(recordsNb, 0, recordsNb.length);
			ArrayList<ArrayList<Object>> recordsList = new ArrayList<ArrayList<Object>>();
			ArrayList<Integer> syncTypeList = new ArrayList<Integer>();
			for (int k=0; k<recordsNbInt; k++) {
				ArrayList<Object> valueList = new ArrayList<Object>();
				//Get type of synchronization
				byte[] syncType = new byte[Constant.TYPEBYTE];
				bis.read(syncType, 0, syncType.length);
				int syncTypeInt = Binary.byteArrayToType(syncType);
				syncTypeList.add(syncTypeInt);
				bos.write(syncType, 0, syncType.length);
				
				//Get local id
				byte[] localId = new byte[Constant.INTBYTE];
				bis.read(localId, 0, localId.length);
				String localIdString = String.valueOf(tableIdInt+""+(k+1));
				valueList.add(localIdString);
				bos.write(Binary.intToByteArray(k+1), 0, Binary.intToByteArray(k+1).length);
				
				//Get global id
				byte[] globalId = new byte[Constant.INTBYTE];
				bis.read(globalId, 0, globalId.length);
				int globalIdInt = Binary.byteArrayToInt(globalId);
				valueList.add(String.valueOf(globalIdInt));
				bos.write(globalId, 0, globalId.length);
				
				if (syncTypeInt != DatabaseAttribute.DELETEVALUE) {
					//Get each record's information
					for (int l=0; l<fieldsNbInt; l++) {
						String valueType = sFieldsTypeMap.get(String.valueOf(fieldList.get(l)));
						//Get length of value
						byte[] valueLength = new byte[Constant.INTBYTE];
						bis.read(valueLength, 0, valueLength.length);
						int valueLengthInt = Binary.byteArrayToInt(valueLength);
						//Get value
						byte[] value = new byte[valueLengthInt];
						bis.read(value, 0, value.length);
						Object valueObject = Binary.byteArrayToObject(value, valueType);
						valueList.add(valueObject);
						
						//Check if there is a blob data
						if (valueType.equals(DatabaseAttribute.BLOB)) {
							ArrayList<Object> blobData = new ArrayList<Object>();
							blobData.add(tableIdInt);
							blobData.add(fieldList.get(l));
							blobData.add(valueObject);
							sBlobRecords.add(blobData);
						}
					}
					if (syncTypeInt != DatabaseAttribute.SYNCHRONIZED) {
						recordsList.add(valueList);
					}
				}
			}
			updateTable(tableIdInt, fieldList, syncTypeList, recordsList);
		}
		byte[] result = bos.toByteArray();
		try {
			bos.close();
			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void saveBlobData(byte[] data, int index) {
		File file = new File(Constant.PACKAGENAME+ApplicationListView.getApplicationName()+"/"+sBlobRecords.get(index).get(2).toString());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(data);
			Log.i("info", "blob saved "+file.getPath());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public byte[] syncExportTable(ArrayList<String> tables, Object filters) {
		if (sBlobRecords.size() > 0) {
			sBlobRecords.clear();
		}
		Log.i("info", "syncExportTable ");
		int tableNbInt = 0;
		Set<String> keys = null;
		if (tables == null) {
			keys = sTablesMap.keySet();
		} else {
			keys = new HashSet<String>(tables);
		}
		//Set<String> keys = tablesMap.keySet();
		HashMap<String, ArrayList<HashMap<Object, Object>>> tidMap = 
			new HashMap<String, ArrayList<HashMap<Object, Object>>>();
		int count = 0;
		for (String key : keys) {
			String table = DatabaseAttribute.TABLE+key;
			//String selection = table+".STATE != "+DatabaseField.SYNCHRONIZED;
			String selection = "STATE != "+DatabaseAttribute.SYNCHRONIZED;
			Log.i("info", "selection "+selection);
			
			if ((filters != null) && (((ArrayList<Object>)filters).get(count) != null)) {
				ArrayList<Object> filter = (ArrayList<Object>) ((ArrayList<Object>)filters).get(count);
				if (count == 0) {
					selection +=  " AND ";
				} else {
					selection += Function.getOperator(filter.get(3)).toString();
				}
				selection += DatabaseAttribute.FIELD+filter.get(0).toString();
				selection += " "+Function.getOperator(filter.get(1))+" ";
				selection += "\'"+filter.get(2)+"\'";
			}
			
			Cursor cursor = sSqlite.query(table, null, selection, null, null, null, null);
			
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
			ArrayList<String> fields = sTablesMap.get(tidKey);
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
						String valueType = sFieldsTypeMap.get(fields.get(fid).split("_")[1]);
						byte[] value = null;
						byte[] valueLenth = null;
						
						if (record.get(fields.get(fid)) == null) {
							if (DmaHttpClient.getServerInfo() == 1) {
								value = Binary.objectToByteArray(record.get(fields.get(fid)), valueType);
								int valueLengthInt = value.length;
								valueLenth = Binary.intToByteArray(valueLengthInt);
							} else if (DmaHttpClient.getServerInfo() == 2) {
								value = Binary.stringToByteArray(null);
								Log.i("info", "synchronized value "+value);
								valueLenth = Binary.intToByteArray(-1);
							}
						} else {
							value = Binary.objectToByteArray(record.get(fields.get(fid)), valueType);
							int valueLengthInt = value.length;
							valueLenth = Binary.intToByteArray(valueLengthInt);
							if (valueType.equals(DatabaseAttribute.BLOB)) {
								ArrayList<Object> blobData = new ArrayList<Object>();
								blobData.add(fields.get(fid).split("_")[1]);
								blobData.add(record.get(fields.get(fid)));
								sBlobRecords.add(blobData);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void updateIds(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		byte[] tableNb = new byte[Constant.INTBYTE];
		bis.read(tableNb, 0, tableNb.length);
		int tableNbInt = Binary.byteArrayToInt(tableNb);
		for (int i=0; i<tableNbInt; i++) {
			//Get table's id
			byte[] tableId = new byte[Constant.INTBYTE];
			bis.read(tableId, 0, tableId.length);
			int tableIdInt = Binary.byteArrayToInt(tableId);
			//Get number of records
			byte[] recordsNb = new byte[Constant.INTBYTE];
			bis.read(recordsNb, 0, recordsNb.length);
			int recordsNbInt = Binary.byteArrayToInt(recordsNb);
			for (int k=0; k<recordsNbInt; k++) {
				//Get type of synchronization
				byte[] syncType = new byte[Constant.TYPEBYTE];
				bis.read(syncType, 0, syncType.length);
				int syncTypeInt = Binary.byteArrayToType(syncType);
				//Get local id
				byte[] localId = new byte[Constant.INTBYTE];
				bis.read(localId, 0, localId.length);
				int localIdInt = Binary.byteArrayToInt(localId);
				//Get global id
				byte[] globalId = new byte[Constant.INTBYTE];
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
		} else {
			whereClause = DatabaseAttribute.ID+tableId+"=\'"+lid+"\'";
		}
		sSqlite.update(table, values, whereClause, null);
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
	
	/**
	 * Adds values and don't check primary key
	 * @param tableId
	 * @param fieldsList
	 * @param record
	 */
	private void insertValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record) {
		Log.i("info", "add table value "+DatabaseAttribute.TABLE+tableId+" record "+record);
		Cursor cursorAllRows = selectQuery(String.valueOf(tableId), null, null);
		int newId = cursorAllRows.getCount()+1;
		DatabaseAdapter.closeCursor(cursorAllRows);
		String idValue = tableId+""+newId;
		ContentValues values = new ContentValues();
		values.put(DatabaseAttribute.ID+tableId, idValue);
		values.put(DatabaseAttribute.GID+tableId, (String)record.get(1));
		values.put(DatabaseAttribute.STATE, DatabaseAttribute.SYNCHRONIZED);
		int fieldsNb = fieldsList.size();
		for (int i=0; i<fieldsNb; i++) {
			//check field's type
			/*if (fieldsTypeMap.get(fieldsList.get(i).toString()).equals(DatabaseAttribute.BLOB)) {
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
			} else {
				values.put(DatabaseAttribute.FIELD+fieldsList.get(i), (String)record.get(i+2));
			}*/
			values.put(DatabaseAttribute.FIELD+fieldsList.get(i), (String)record.get(i+2));
		}
		String tableName = DatabaseAttribute.TABLE+tableId;
		long insertResult = sSqlite.insert(tableName, null, values);
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
			} else {
				newValue += DatabaseAttribute.FIELD+fieldsList.get(i)+"=\'"+record.get(i+2)+"\', ";
			}
		}
		String update = "UPDATE "+tableName+" SET "+newValue+" WHERE "+DatabaseAttribute.GID+tableId+"=\'"+record.get(1)+"\';";
		Log.i("info", "update "+update);
		sSqlite.execSQL(update);
	}
	
	private void deleteValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record) {
		String selectionString = DatabaseAttribute.GID+tableId+"=\'"+record.get(1)+"\'";
		//Check if table has blob data
		DatabaseAdapter.deleteBlobFiles(String.valueOf(tableId), selectionString);
		String delete = "DELETE FROM "+DatabaseAttribute.TABLE+tableId+" WHERE "+selectionString+";";
		//String delete = "DELETE FROM "+DatabaseAttribute.TABLE+tableId+" WHERE "+DatabaseAttribute.GID+tableId+"=\'"+record.get(1)+"\';";
		sSqlite.execSQL(delete);
	}
	
	/**
	 * Generates primary key for KEY and INTEGER
	 * @param type
	 * @param idValue
	 * @return
	 */
	private static Object generatePK(String type, Object idValue) {
		Object result = null;
		if (type.equals(DatabaseAttribute.KEY)) {
			result = KeyGenerator.getKeyGenerated();
		} else if (type.equals(DatabaseAttribute.INTEGER)) {
			Log.i("info", "idValue "+idValue);
			result = idValue;
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static void clearTable(String tableId, Object filters) {
		String selectionString = null;
		if (filters != null) {
			int filtersNb = ((ArrayList<Object>)filters).size();
			for (int i=0; i<filtersNb; i++) {
				ArrayList<Object> filter = (ArrayList<Object>) ((ArrayList<Object>)filters).get(i);
				if (i != 0) {
					//Add link
				}
				selectionString += DatabaseAttribute.FIELD+filter.get(0).toString();
				selectionString += " "+Function.getOperator(filter.get(1))+" ";
				selectionString += "\'"+filter.get(2)+"\'";
			}
		}
		
		//Check if table has blob data
		DatabaseAdapter.deleteBlobFiles(tableId, selectionString);
		
		if (selectionString == null) {
			sSqlite.execSQL("DELETE FROM "+DatabaseAttribute.TABLE+tableId+";");
		} else {
			sSqlite.execSQL("DELETE FROM "+DatabaseAttribute.TABLE+tableId+" WHERE "+selectionString+";");
		}
	}
	
	private static void deleteBlobFiles(String tableId, String selectionString) {
		Cursor cursor = sSqlite.query(DatabaseAttribute.TABLE+tableId, null, selectionString, null, null, null, null);
		if (cursor.getCount() != 0) {
			String[] columnsNames = cursor.getColumnNames();
			ArrayList<Integer> blobColumnArray = new ArrayList<Integer>();
			int columnsNameNb = columnsNames.length;
			for (int i=0; i<columnsNameNb; i++) {
				if (columnsNames[i].contains(DatabaseAttribute.FIELD)) {
					if (sFieldsTypeMap.get(columnsNames[i].split("_")[1]).equals(DatabaseAttribute.BLOB)) {
						blobColumnArray.add(i);
					}
				}
			}
			int blobColumnArraySize = blobColumnArray.size();
			if (blobColumnArraySize > 0) {
				int cursorCount = cursor.getCount();
				cursor.moveToFirst();
				for (int i=0; i<cursorCount; i++) {
					for (int j=0; j<blobColumnArraySize; j++) {
						String imageName = (String)getCursorValue(cursor, columnsNames[blobColumnArray.get(j)]);
						//Delete the image
						new File(Constant.PACKAGENAME+ApplicationListView.getApplicationName()+"/"+imageName).delete();
					}
					cursor.moveToNext();
				}
			}
		}
		DatabaseAdapter.closeCursor(cursor);
	}
	
	public static Cursor selectQuery(String tableId, ArrayList<Integer> fieldList, ArrayList<Object> valueList) {
		Cursor result = null;
		String table = DatabaseAttribute.TABLE+tableId;
		String selection = createSelectionString(fieldList, valueList);
		result = sSqlite.query(table, null, selection, null, null, null, null);
		return result;
	}
	
	public static Cursor selectQuery(String tableId, String fieldId, Object filter, String type) {
		Cursor result = null;
		String projectionIn = type + "(" + DatabaseAttribute.FIELD + fieldId +")";
		ArrayList<String> tables = new ArrayList<String>();
		tables.add(tableId);
		String selection = createSelectionString(tables, filter);
		result = sSqlite.query(DatabaseAttribute.TABLE + tableId, new String[]{projectionIn}, selection, null, null, null, null);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Cursor selectQuery(ArrayList<String> tables, ArrayList<ArrayList<String>> columns, Object filter, Object order, Object distinct) {
		Cursor result = null;
		String table = createTableString(tables);
		String[] projectionIn = createProjectionStrings(columns);
		String selection = createSelectionString(tables, filter);
		String orderBy = null;
		if (order != null) {
			String filedId = ((ArrayList<Object>)order).get(0).toString();
			orderBy = DatabaseAttribute.FIELD + filedId;
			if (!((ArrayList<Object>)order).get(1).toString().equals("true")) {
				orderBy += " DESC";
			}
		}
		boolean isDisctinct = false;
		if (distinct != null) {
			if (distinct.toString().equals("true")) {
				isDisctinct = true;
			}
		}
		result = sSqlite.query(isDisctinct, table, projectionIn, selection, null, null, null, orderBy, null);
		return result;
	}

	/**
	 * Adds values and check primary key
	 * @param tableId
	 * @param fieldsList
	 * @param record
	 */
	public static void addQuery(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record) {
		//record has 1 more element than fieldsList, the first element of record is the new ID
		int fieldsNb = fieldsList.size();
		String id = DatabaseAttribute.ID+tableId;
		String gid = DatabaseAttribute.GID+tableId;
		String idValue = String.valueOf(tableId+""+record.get(0));
		String fields = "("+id+", "+gid+", "+DatabaseAttribute.STATE+", ";
		String values = "('"+idValue+"\', 0, "+DatabaseAttribute.ADDVALUE+", ";
		for (int i=0; i<fieldsNb; i++) {
			if ((sFieldsPKMap.containsKey(String.valueOf(tableId))) && 
					(sFieldsPKMap.get(String.valueOf(tableId)).equals(fieldsList.get(i))) &&
					(record.get(i+1) == null)) {
				Log.i("info", "in case of filed has pk field, generate its value");
				record.remove(i+1);
				record.add(i+1, generatePK(sFieldsTypeMap.get(fieldsList.get(i)), record.get(0)));
			}
			
			fields += DatabaseAttribute.FIELD+fieldsList.get(i);
			values += "\'"+record.get(i+1)+"\'";
			
			if (i != fieldsNb-1) {
				fields += ", ";
				values += ", ";
			}
		}
		if ((!fieldsList.contains(sFieldsPKMap.get(String.valueOf(tableId)))) &&
				(sFieldsPKMap.containsKey(String.valueOf(tableId)))) {
			Log.i("info", "in case of field list hasn't pk field, generate its value");
			fields += ", "+DatabaseAttribute.FIELD+sFieldsPKMap.get(String.valueOf(tableId));
			values += ", \'"+generatePK(sFieldsTypeMap.get(sFieldsPKMap.get(String.valueOf(tableId))), record.get(0))+"\'";
		}
		fields += ")";
		values += ")";
		String tableName = DatabaseAttribute.TABLE+tableId;
		String insert = "INSERT INTO "+tableName+" "+fields+" VALUES"+values+";";
		Log.i("info", "query "+insert);
		sSqlite.execSQL(insert);
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
		sSqlite.update(table, values, whereClause, null);
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
				sSqlite.update(table, values, whereClause, null);
			} else {
				Log.i("info", "whereclause "+whereClause);
				sSqlite.delete(table, whereClause, null);
			}
		}
	}
	
	public static int getTableNb() {
		return sTablesMap.size();
	}
	
	public static Set<String> getTableIds() {
		return sTablesMap.keySet();
	}
	
	public static HashMap<String, ArrayList<String>> getTablesMap() {
		return sTablesMap;
	}
	
	public static String getFieldName(String fieldId) {
		String result = "";
		if (sFieldsNameMap.containsKey(fieldId)) {
			result = sFieldsNameMap.get(fieldId);
		}
		return result;
	}
	
	public static String getTableIdByName(String name) {
		return sTablesNameMap.get(name);
	}
	
	public static HashMap<String, String> getFieldsNameMap() {
		return sFieldsNameMap;
	}
	
	public static HashMap<String, String> getFieldsTypeMap() {
		return sFieldsTypeMap;
	}
	
	public static void beginTransaction() {
		STARTTRANSACTION = true;
		sSqlite.execSQL("BEGIN TRANSACTION;");
	}
	
	public static void rollbackTransaction() {
		if (STARTTRANSACTION) {
			sSqlite.execSQL("ROLLBACK TRANSACTION;");
			STARTTRANSACTION = false;
		}
	}
	
	public static void commitTransaction() {
		if (STARTTRANSACTION) {
			sSqlite.execSQL("COMMIT TRANSACTION;");
			STARTTRANSACTION = false;
		}
	}
	
	public static boolean hasStartTransaction() {
		return STARTTRANSACTION;
	}
	
	public static void cleanTables() {
		Log.i("info", "cleantables");
		Set<String> keys = sTablesMap.keySet();
		for (String key : keys) {
			String table = DatabaseAttribute.TABLE+key;
			Log.i("info", "table "+table);
			String id = DatabaseAttribute.ID+key;
			String[] projectionIn = new String[]{id, DatabaseAttribute.STATE};
			Cursor result = sSqlite.query(table, projectionIn, null, null, null, null, null);
			result.moveToFirst();
			int count = result.getCount();
			for (int i=0; i<count; i++) {
				String whereClause = id+" = \'"+result.getString(result.getColumnIndexOrThrow(id))+"\'";
				if (result.getInt(result.getColumnIndexOrThrow(DatabaseAttribute.STATE)) == DatabaseAttribute.DELETEVALUE) {
					Log.i("info", "delete value");
					sSqlite.delete(table, whereClause, null);					
				}
				result.moveToNext();
			}
			DatabaseAdapter.closeCursor(result);
		}
	}
	
	public static Object getCursorValue(Cursor cursor, String field) {
		if (field.indexOf(DatabaseAttribute.FIELD) != -1) {
			String fieldId = Integer.valueOf(field.split("_")[1]).toString();
			if (sFieldsTypeMap.get(fieldId).equals(DatabaseAttribute.INTEGER)) {
				return cursor.getInt(cursor.getColumnIndexOrThrow(field));
			} else if (sFieldsTypeMap.get(fieldId).equals(DatabaseAttribute.DOUBLE)) {
				return cursor.getDouble(cursor.getColumnIndexOrThrow(field));
			} else {
				String value = cursor.getString(cursor.getColumnIndexOrThrow(field));
				if (value == null) {
					return "";
				} else {
					return value;
				}
				//return cursor.getString(cursor.getColumnIndexOrThrow(field));
			}
		} else {
			return cursor.getString(cursor.getColumnIndexOrThrow(field));
		}
	}
	
	public static ArrayList<ArrayList<Object>> getBlobRecords() {
		return sBlobRecords;
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
		String result = "";
		int size = tables.size();
		for (int i=0; i<size; i++) {
			if (i != size-1) {
				result += DatabaseAttribute.TABLE+tables.get(i)+", ";
			} else {
				result += DatabaseAttribute.TABLE+tables.get(i);
			}	
		}
		return result;
	}
	
	private static String[] createProjectionStrings(ArrayList<ArrayList<String>> columns) {
		if (columns == null) {
			return null;
		} else {
			int size = columns.size();
			String[] result = new String[size];
			for (int i=0; i<size; i++) {
				ArrayList<String> column = columns.get(i);
				result[i] = DatabaseAttribute.TABLE+column.get(0)+"."+DatabaseAttribute.FIELD+column.get(1);
			}
			return result;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static String createSelectionString(ArrayList<String> tables, Object filters) {
		String result = "";
		int tablesSize = tables.size();
		for (int i=0; i<tablesSize; i++) {
			if (i != 0) {
				result +=  " AND ";
			}
			result +=  DatabaseAttribute.TABLE+tables.get(i)+".STATE != "+DatabaseAttribute.DELETEVALUE;
		}
		if (!createSelectionFKString(tables).equals("")) {
			result += " AND "+createSelectionFKString(tables);	
		}
		if (filters != null) {
			int filtersNb = ((ArrayList<Object>)filters).size();
			for (int i=0; i<filtersNb; i++) {
				ArrayList<Object> filter = (ArrayList<Object>) ((ArrayList<Object>)filters).get(i);
				if (i == 0) {
					result +=  " AND ";
				} else {
					result += Function.getOperator(filter.get(3)).toString();
				}
				result += DatabaseAttribute.FIELD+filter.get(0).toString();
				result += " "+Function.getOperator(filter.get(1))+" ";
				result += "\'"+filter.get(2)+"\'";
			}
		}
		return result;
	}
	
	private static String createSelectionString(ArrayList<Integer> fieldList, ArrayList<Object> valueList) {
		if ((fieldList == null) && (valueList == null)) {
			return null;
		} else {
			String result = "";
			int size = fieldList.size();
			for (int i=0; i<size; i++) {
				if (i == 0) {
					result += "STATE != "+DatabaseAttribute.DELETEVALUE+" AND "+DatabaseAttribute.FIELD+fieldList.get(i)+" = \'"+valueList.get(i)+"\'";
				} else {
					result += " AND "+DatabaseAttribute.FIELD+fieldList.get(i)+" = \'"+valueList.get(i)+"\'";
				}
			}
			return result;
		}		
	}
	
	private static String createSelectionFKString(ArrayList<String> tables) {
		String result = "";
		int size = sForeignKeyList.size();
		if (tables.size() > 1) {
			for (int i=0; i<size; i++) {
				ArrayList<String> foreignKey = sForeignKeyList.get(i);
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
