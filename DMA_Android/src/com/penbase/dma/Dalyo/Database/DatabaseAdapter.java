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
	private Document mDbDocument;
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
		closeDatabase();
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
		StringBuffer filePath = new StringBuffer(Constant.PACKAGENAME);
		filePath.append("databases/").append(database);
		File dbFile = new File(filePath.toString());
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
						} else if ((field.hasAttribute(DatabaseTag.FIELD_FOREIGNTABLE)) 
								&& (field.hasAttribute(DatabaseTag.FIELD_FOREIGNFIELD))) {
							ArrayList<String> fk = new ArrayList<String>();
							String foreignTableId = field.getAttribute(DatabaseTag.FIELD_FOREIGNTABLE);
							String foreignFieldId = field.getAttribute(DatabaseTag.FIELD_FOREIGNFIELD);
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
			StringBuffer createquery = new StringBuffer("CREATE TABLE IF NOT EXISTS ");
			Element table = (Element) tableList.item(i);
			//String typeSync = table.getAttribute(DatabaseTag.TABLE_SYNC);
			String tableId = table.getAttribute(DatabaseTag.TABLE_ID);
			String tableOriginalName = table.getAttribute(DatabaseTag.TABLE_NAME);
			sTablesNameMap.put(tableOriginalName, tableId);
			editorTablePref.putString(tableId, "");
			String tableName = DatabaseAttribute.TABLE+tableId;
			ArrayList<String> tableElements = new ArrayList<String>();
			String id = DatabaseAttribute.ID+tableId;
			String gid = DatabaseAttribute.GID+tableId;
			createquery.append(tableName).append(" (").append(id).append(" VARCHAR(255), ").append(gid);
			createquery.append(" VARCHAR(255), ").append(DatabaseAttribute.STATE).append(" INTEGER, ");
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
						createquery.append(fieldNewName).append(" ").append(fieldType).append(" UNIQUE, ");
						fieldTypeValue += " UNIQUE, ";
					} else if ((field.hasAttribute(DatabaseTag.FIELD_FOREIGNTABLE)) &&
							(field.hasAttribute(DatabaseTag.FIELD_FOREIGNFIELD))) {
						String foreignTableId = field.getAttribute(DatabaseTag.FIELD_FOREIGNTABLE);
						String foreignFieldId = field.getAttribute(DatabaseTag.FIELD_FOREIGNFIELD);
						fieldTypeValue += foreignTableId+" "+foreignFieldId;
						createquery.append(fieldNewName).append(" ").append(fieldType).append(", ");
						foreignKey.add(tableId);
						foreignKey.add(fieldId);
						foreignKey.add(foreignTableId);
						foreignKey.add(foreignFieldId);
						sForeignKeyList.add(foreignKey);	
						foreignKeyTable.add(foreignKey);
					} else {
						createquery.append(fieldNewName).append(" ").append(fieldType).append(", ");
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
			
			String createSql = createquery.substring(0, createquery.length()-2);
			createSql += ");";
			
			Log.i("info", "query "+createSql);
			sSqlite.execSQL(createSql);
			
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
					recordsList.add(valueList);
					/*if (syncTypeInt != DatabaseAttribute.SYNCHRONIZED) {
						recordsList.add(valueList);
					}*/
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
		StringBuffer filePath = new StringBuffer(Constant.PACKAGENAME);
		filePath.append(ApplicationListView.getApplicationName()).append("/");
		filePath.append(sBlobRecords.get(index).get(2).toString());
		File file = new File(filePath.toString());
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

		HashMap<String, ArrayList<HashMap<Object, Object>>> tidMap = 
			new HashMap<String, ArrayList<HashMap<Object, Object>>>();
		int count = 0;
		for (String key : keys) {
			String table = DatabaseAttribute.TABLE+key;
			StringBuffer selection = new StringBuffer("STATE != ");
			selection.append(DatabaseAttribute.SYNCHRONIZED);
			
			if ((filters != null) && (((ArrayList<Object>)filters).get(count) != null)) {
				ArrayList<Object> filter = (ArrayList<Object>) ((ArrayList<Object>)filters).get(count);
				if (count == 0) {
					selection.append(" AND ");
				} else {
					selection.append(Function.getOperator(filter.get(3)).toString());
				}
				selection.append(DatabaseAttribute.FIELD);
				selection.append(filter.get(0).toString());
				selection.append(" ");
				selection.append(Function.getOperator(filter.get(1)));
				selection.append(" ");
				selection.append("\'");
				selection.append(filter.get(2));
				selection.append("\'");
			}
			
			Cursor cursor = sSqlite.query(table, null, selection.toString(), null, null, null, null);
			
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
		StringBuffer whereClause = new StringBuffer(DatabaseAttribute.ID);
		whereClause.append(tableId);
		whereClause.append("=\'");
		if (tableId == 0) {
			StringBuffer idValue = new StringBuffer(tableId);
			idValue.append("").append(lid);
			whereClause.append(idValue.toString());
			whereClause.append("\'");
		} else {
			whereClause.append(lid);
			whereClause.append("\'");			 
		}
		sSqlite.update(table, values, whereClause.toString(), null);
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
		StringBuffer idValue = new StringBuffer(tableId);
		idValue.append("").append(newId);
		ContentValues values = new ContentValues();
		values.put(DatabaseAttribute.ID+tableId, idValue.toString());
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
		StringBuffer newValue = new StringBuffer("");
		for (int i=0; i<fieldsNb; i++) {
			if (i == fieldsNb-1) {
				newValue.append(DatabaseAttribute.STATE);
				newValue.append("=");
				newValue.append(DatabaseAttribute.SYNCHRONIZED);
				newValue.append(" ,");
				newValue.append(DatabaseAttribute.FIELD);
				newValue.append(fieldsList.get(i));
				newValue.append("=\'");
				newValue.append(record.get(i+2));
				newValue.append("\'");
			} else {
				newValue.append(DatabaseAttribute.FIELD);
				newValue.append(fieldsList.get(i));
				newValue.append("=\'");
				newValue.append(record.get(i+2));
				newValue.append("\', ");
			}
		}
		StringBuffer update = new StringBuffer("UPDATE ");
		update.append(tableName);
		update.append(" SET ");
		update.append(newValue.toString());
		update.append(" WHERE ");
		update.append(DatabaseAttribute.GID);
		update.append(tableId);
		update.append("=\'");
		update.append(record.get(1));
		update.append("\';");
		Log.i("info", "update "+update.toString());
		sSqlite.execSQL(update.toString());
	}
	
	private void deleteValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record) {
		StringBuffer selectionString = new StringBuffer(DatabaseAttribute.GID);
		selectionString.append(tableId);
		selectionString.append("=\'");
		selectionString.append(record.get(1));
		selectionString.append("\'");
		//Check if table has blob data
		DatabaseAdapter.deleteBlobFiles(String.valueOf(tableId), selectionString.toString());
		StringBuffer delete = new StringBuffer("DELETE FROM ");
		delete.append(DatabaseAttribute.TABLE).append(tableId).append(" WHERE ").append(selectionString).append(";");
		//String delete = "DELETE FROM "+DatabaseAttribute.TABLE+tableId+" WHERE "+DatabaseAttribute.GID+tableId+"=\'"+record.get(1)+"\';";
		sSqlite.execSQL(delete.toString());
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
		StringBuffer selectionString = new StringBuffer();
		if (filters != null) {
			int filtersNb = ((ArrayList<Object>)filters).size();
			for (int i=0; i<filtersNb; i++) {
				ArrayList<Object> filter = (ArrayList<Object>) ((ArrayList<Object>)filters).get(i);
				if (i != 0) {
					//Add link
				}
				selectionString.append(DatabaseAttribute.FIELD).append(filter.get(0).toString());
				selectionString.append(" ").append(Function.getOperator(filter.get(1))).append(" ");
				selectionString.append("\'").append(filter.get(2)).append("\'");
			}
		}
		
		//Check if table has blob data
		DatabaseAdapter.deleteBlobFiles(tableId, selectionString.toString());
		
		StringBuffer delete = new StringBuffer("DELETE FROM ");
		delete.append(DatabaseAttribute.TABLE).append(tableId);
		if (selectionString.length() > 0) {
			delete.append(";");
			sSqlite.execSQL(delete.toString());
		} else {
			delete.append(" WHERE ").append(selectionString.toString()).append(";");
			sSqlite.execSQL(delete.toString());
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
		StringBuffer projectionIn = new StringBuffer(type);
		projectionIn.append("(");
		projectionIn.append(DatabaseAttribute.FIELD);
		projectionIn.append(fieldId);
		projectionIn.append(")");
		ArrayList<String> tables = new ArrayList<String>();
		tables.add(tableId);
		String selection = createSelectionString(tables, filter);
		result = sSqlite.query(DatabaseAttribute.TABLE + tableId, new String[]{projectionIn.toString()}, selection, null, null, null, null);
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
		StringBuffer fields = new StringBuffer("(");
		fields.append(id).append(", ").append(gid).append(", ");
		fields.append(DatabaseAttribute.STATE).append(", ");
		StringBuffer values = new StringBuffer("('");
		values.append(idValue).append("\', 0, ").append(DatabaseAttribute.ADDVALUE).append(", ");
		for (int i=0; i<fieldsNb; i++) {
			if ((sFieldsPKMap.containsKey(String.valueOf(tableId))) && 
					(sFieldsPKMap.get(String.valueOf(tableId)).equals(fieldsList.get(i))) &&
					(record.get(i+1) == null)) {
				Log.i("info", "in case of filed has pk field, generate its value");
				record.remove(i+1);
				record.add(i+1, generatePK(sFieldsTypeMap.get(fieldsList.get(i)), record.get(0)));
			}
			
			fields.append(DatabaseAttribute.FIELD).append(fieldsList.get(i));
			values.append("\'").append(record.get(i+1)).append("\'");
			
			if (i != fieldsNb-1) {
				fields.append(", ");
				values.append(", ");
			}
		}
		if ((!fieldsList.contains(sFieldsPKMap.get(String.valueOf(tableId)))) &&
				(sFieldsPKMap.containsKey(String.valueOf(tableId)))) {
			Log.i("info", "in case of field list hasn't pk field, generate its value");
			fields.append(", ").append(DatabaseAttribute.FIELD).append(sFieldsPKMap.get(String.valueOf(tableId)));
			values.append(", \'");
			values.append(generatePK(sFieldsTypeMap.get(sFieldsPKMap.get(String.valueOf(tableId))), record.get(0)));
			values.append("\'");
		}
		fields.append(")");
		values.append(")");
		StringBuffer tableName = new StringBuffer(DatabaseAttribute.TABLE);
		tableName.append(tableId);
		StringBuffer insert = new StringBuffer("INSERT INTO ");
		insert.append(tableName.toString());
		insert.append(" ").append(fields.toString());
		insert.append(" VALUES").append(values.toString()).append(";");
		Log.i("info", "query "+insert.toString());
		sSqlite.execSQL(insert.toString());
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
				StringBuffer whereClause = new StringBuffer(id);
				whereClause.append(" = \'").append(result.getString(result.getColumnIndexOrThrow(id))).append("\'");
				if (result.getInt(result.getColumnIndexOrThrow(DatabaseAttribute.STATE)) == DatabaseAttribute.DELETEVALUE) {
					Log.i("info", "delete value");
					sSqlite.delete(table, whereClause.toString(), null);					
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
		StringBuffer result = new StringBuffer("");
		if (record.containsKey(DatabaseAttribute.ID+tableId)) {
			result.append(DatabaseAttribute.ID).append(tableId).append(" = \'");
			result.append(record.get(DatabaseAttribute.ID+tableId)).append("\'");
		}
		return result.toString();
	}
	
	private static String createTableString(ArrayList<String> tables) {
		StringBuffer result = new StringBuffer("");
		int size = tables.size();
		for (int i=0; i<size; i++) {
			if (i != size-1) {
				result.append(DatabaseAttribute.TABLE).append(tables.get(i)).append(", ");
			} else {
				result.append(DatabaseAttribute.TABLE).append(tables.get(i));
			}	
		}
		return result.toString();
	}
	
	private static String[] createProjectionStrings(ArrayList<ArrayList<String>> columns) {
		if (columns == null) {
			return null;
		} else {
			int size = columns.size();
			String[] result = new String[size];
			for (int i=0; i<size; i++) {
				ArrayList<String> column = columns.get(i);
				StringBuffer value = new StringBuffer(DatabaseAttribute.TABLE);
				value.append(column.get(0)).append(".").append(DatabaseAttribute.FIELD).append(column.get(1));
				result[i] = value.toString();
			}
			return result;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static String createSelectionString(ArrayList<String> tables, Object filters) {
		StringBuffer result = new StringBuffer("");
		int tablesSize = tables.size();
		for (int i=0; i<tablesSize; i++) {
			if (i != 0) {
				result.append(" AND ");
			}
			result.append(DatabaseAttribute.TABLE).append(tables.get(i)).append(".STATE != ").append(DatabaseAttribute.DELETEVALUE);
		}
		if (!createSelectionFKString(tables).equals("")) {
			result.append(" AND ").append(createSelectionFKString(tables));
		}
		if (filters != null) {
			int filtersNb = ((ArrayList<Object>)filters).size();
			for (int i=0; i<filtersNb; i++) {
				ArrayList<Object> filter = (ArrayList<Object>) ((ArrayList<Object>)filters).get(i);
				if (i == 0) {
					result.append(" AND ");
				} else {
					result.append(Function.getOperator(filter.get(3)).toString());
				}
				result.append(DatabaseAttribute.FIELD).append(filter.get(0).toString());
				result.append(" ").append(Function.getOperator(filter.get(1))).append(" ");
				result.append("\'").append(filter.get(2)).append("\'");
			}
		}
		return result.toString();
	}
	
	private static String createSelectionString(ArrayList<Integer> fieldList, ArrayList<Object> valueList) {
		if ((fieldList == null) && (valueList == null)) {
			return null;
		} else {
			StringBuffer result = new StringBuffer("");
			int size = fieldList.size();
			for (int i=0; i<size; i++) {
				if (i == 0) {
					result.append("STATE != ").append(DatabaseAttribute.DELETEVALUE).append(" AND ");
					result.append(DatabaseAttribute.FIELD).append(fieldList.get(i));
					result.append(" = \'").append(valueList.get(i)).append("\'");
				} else {
					result.append(" AND ").append(DatabaseAttribute.FIELD).append(fieldList.get(i)).append(" = \'");
					result.append(valueList.get(i)).append("\'");
				}
			}
			return result.toString();
		}		
	}
	
	private static String createSelectionFKString(ArrayList<String> tables) {
		StringBuffer result = new StringBuffer("");
		int size = sForeignKeyList.size();
		if (tables.size() > 1) {
			for (int i=0; i<size; i++) {
				ArrayList<String> foreignKey = sForeignKeyList.get(i);
				if ((tables.contains(foreignKey.get(0))) && (tables.contains(foreignKey.get(2)))) {
					if (result.length() > 0) {
						result.append("AND ");
					}
					result.append(DatabaseAttribute.TABLE).append(foreignKey.get(0)).append(".");
					result.append(DatabaseAttribute.FIELD).append(foreignKey.get(1)).append(" = ");
					result.append(DatabaseAttribute.TABLE).append(foreignKey.get(2)).append(".");
					result.append(DatabaseAttribute.FIELD).append(foreignKey.get(3));
				}
			}
		}		
		return result.toString();
	}
}
