package com.penbase.dma.Dalyo.Database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.penbase.dma.Binary.Binary;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DatabaseField;
import com.penbase.dma.Constant.XmlTag;
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
	private static HashMap<String, String> fieldsMap;
	private static HashMap<String, String> fieldsPKMap;
	private static ArrayList<ArrayList<String>> foreignKeyList;
	private String dbName = null;
	private String TABLEPREF = "TablePrefFile";
	private String FIELDPREF = "FieldPrefFile";
	private static boolean STARTTRANSACTION = false;
	private static  ArrayList<ArrayList<Object>> blobRecords;
	
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
		blobRecords = new ArrayList<ArrayList<Object>>();
		createDatabase(dbName);
	}
	
	private void createDatabase(String database) throws SQLException{
		try{
			if (!databaseExists(database)){
				Log.i("info", "the database doesn't exist");
				sqlite = context.openOrCreateDatabase(database, 0, null);
				createTable();
			}
			else if (!checkDatabaseExists()){
				Log.i("info", "the database isn't the same");
				sqlite = context.openOrCreateDatabase(database, 0, null);
				createTable();
			}
			else{
				Log.i("info", "the database have nothing to change");
				sqlite = context.openOrCreateDatabase(database, 0, null);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private boolean databaseExists(String database){
		File dbFile = new File(Constant.packageName+"databases/"+database);
		return dbFile.exists();
	}
	
	private boolean checkDatabaseExists(){
		boolean result = true;
		HashMap<String, ArrayList<String>> tsMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, String> fsMap = new HashMap<String, String>();
		HashMap<String, String> fsPkMap = new HashMap<String, String>();
		ArrayList<ArrayList<String>> fkList = new ArrayList<ArrayList<String>>();
		SharedPreferences tablePref = context.getSharedPreferences(TABLEPREF, Context.MODE_PRIVATE);
		SharedPreferences fieldPref = context.getSharedPreferences(FIELDPREF, Context.MODE_PRIVATE);
		NodeList tableList = dbDocument.getElementsByTagName(XmlTag.TABLE);
		int tableLen = tableList.getLength();
		int i = 0;
		while (i < tableLen){
			Element table = (Element) tableList.item(i);
			String tableId = table.getAttribute(XmlTag.TABLE_ID);
			if (!tablePref.getString(tableId, "null").equals("")){
				result = false;
				i = tableLen;
			}
			else{
				NodeList fieldList = table.getChildNodes();
				int fieldLen = fieldList.getLength();
				if (fieldLen > 0){
					int j = 0;
					ArrayList<String> tableElements = new ArrayList<String>();
					while (j < fieldLen){
						Element field = (Element) fieldList.item(j);
						String fieldId = field.getAttribute(XmlTag.FIELD_ID);
						String fieldType = field.getAttribute(XmlTag.FIELD_TYPE);
						String fieldSize = field.getAttribute(XmlTag.FIELD_SIZE);
						String fieldName = DatabaseField.FIELD+fieldId;
						fsMap.put(fieldId, fieldType);
						tableElements.add(fieldName);
						if (fieldType.equals("VARCHAR")){
							fieldType = fieldType+"("+fieldSize+")";
						}
						String fieldTypeValue = fieldType;
						if (field.hasAttribute(XmlTag.FIELD_PK)){
							fsPkMap.put(tableId, fieldId);
							if (field.hasAttribute(XmlTag.FIELD_PK_AUTO)){
								fieldTypeValue += " PRIMARY KEY AUTOINCREMENT, ";
							}
							else{
								fieldTypeValue += " PRIMARY KEY, ";
							}
						}
						else if ((field.hasAttribute(XmlTag.FIELD_FORIEIGNTABLE)) 
								&& (field.hasAttribute(XmlTag.FIELD_FORIEIGNFIELD))){
							ArrayList<String> fk = new ArrayList<String>();
							String foreignTableId = field.getAttribute(XmlTag.FIELD_FORIEIGNTABLE);
							String foreignFieldId = field.getAttribute(XmlTag.FIELD_FORIEIGNFIELD);
							fieldTypeValue += foreignTableId+" "+foreignFieldId;
							fk.add(tableId);
							fk.add(fieldId);
							fk.add(foreignTableId);
							fk.add(foreignFieldId);
							fkList.add(fk);
							
						}
						if (!fieldPref.getString(fieldId, "null").equals(fieldTypeValue)){
							result = false;
							j = fieldLen;
							i = tableLen;
						}
						else{
							j++;
						}
					}
					tsMap.put(tableId, tableElements);
				}
				i++;
			}
		}
		if (result){
			tablesMap = tsMap;
			fieldsMap = fsMap;
			fieldsPKMap = fsPkMap;
			foreignKeyList = fkList;
		}
		return result;
	}
	
	private void createTable(){
		SharedPreferences.Editor editorTablePref = context.getSharedPreferences(TABLEPREF, Context.MODE_PRIVATE).edit();
		SharedPreferences.Editor editorFieldPref = context.getSharedPreferences(FIELDPREF, Context.MODE_PRIVATE).edit();
		NodeList tableList = dbDocument.getElementsByTagName(XmlTag.TABLE);
		int tableLen = tableList.getLength();
		for (int i=0; i<tableLen; i++){
			String createquery = "CREATE TABLE IF NOT EXISTS ";
			Element table = (Element) tableList.item(i);
			String typeSync = table.getAttribute(XmlTag.TABLE_SYNC);
			String tableId = table.getAttribute(XmlTag.TABLE_ID);
			editorTablePref.putString(tableId, "");
			String tableName = DatabaseField.TABLE+tableId;
			ArrayList<String> tableElements = new ArrayList<String>();
			String id = DatabaseField.ID+tableId;
			String gid = DatabaseField.GID+tableId;
			createquery += tableName+" ("+id+" VARCHAR(255), "+gid+" VARCHAR(255), "+DatabaseField.STATE+" INTEGER, ";
			NodeList fieldList = table.getChildNodes();
			int fieldLen = fieldList.getLength();
			ArrayList<ArrayList<String>> foreignKeyTable = new ArrayList<ArrayList<String>>();
			if (fieldLen > 0){
				for (int j=0; j<fieldLen; j++){
					//foreignKey has 4 elements (tid, fid, ftid, ffid)
					ArrayList<String> foreignKey = new ArrayList<String>();	
					Element field = (Element) fieldList.item(j);
					String fieldId = field.getAttribute(XmlTag.FIELD_ID);
					String fieldName = DatabaseField.FIELD+fieldId;
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
					
					else if ((field.hasAttribute(XmlTag.FIELD_FORIEIGNTABLE)) &&
							(field.hasAttribute(XmlTag.FIELD_FORIEIGNFIELD))){
						String foreignTableId = field.getAttribute(XmlTag.FIELD_FORIEIGNTABLE);
						String foreignFieldId = field.getAttribute(XmlTag.FIELD_FORIEIGNFIELD);
						fieldTypeValue += foreignTableId+" "+foreignFieldId;
						createquery += fieldName+" "+fieldType+", ";
						foreignKey.add(tableId);
						foreignKey.add(fieldId);
						foreignKey.add(foreignTableId);
						foreignKey.add(foreignFieldId);
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
					createquery += " FOREIGN KEY ("+DatabaseField.FIELD+foreignKeyTable.get(k).get(1)+") REFERENCES "+
					DatabaseField.TABLE+foreignKeyTable.get(k).get(2)+"("+DatabaseField.FIELD+foreignKeyTable.get(k).get(3)+"), ";
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
		int tableNbInt = Binary.byteArrayToInt(tableNb);
		for (int i=0; i<tableNbInt; i++){
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
			}

			//Get number of records
			byte[] recordsNb = new byte[Binary.INTBYTE];
			bis.read(recordsNb, 0, recordsNb.length);
			int recordsNbInt = Binary.byteArrayToInt(recordsNb);
			bos.write(recordsNb, 0, recordsNb.length);
			ArrayList<ArrayList<Object>> recordsList = new ArrayList<ArrayList<Object>>();
			ArrayList<Integer> syncTypeList = new ArrayList<Integer>();
			for (int k=0; k<recordsNbInt; k++){
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
				for (int l=0; l<fieldsNbInt; l++){
					//Get length of value
					byte[] valueLength = new byte[Binary.INTBYTE];
					bis.read(valueLength, 0, valueLength.length);
					int valueLengthInt = Binary.byteArrayToInt(valueLength);
					//Get value
					byte[] value = new byte[valueLengthInt];
					bis.read(value, 0, value.length);
					Object valueObject = Binary.byteArrayToObject(value, fieldsMap.get(String.valueOf(fieldList.get(l))));
					valueList.add(valueObject);
				}
				if (syncTypeInt != DatabaseField.SYNCHRONIZED) {
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
	
	public byte[] syncExportTable(){
		Log.i("info", "syncExportTable ");
		int tableNbInt = 0;
		Set<String> keys = tablesMap.keySet();
		Log.i("info", "tablesmap keys "+keys.toString());
		HashMap<String, ArrayList<HashMap<Object, Object>>> tidMap = 
			new HashMap<String, ArrayList<HashMap<Object, Object>>>();
		for (String key : keys){
			String table = DatabaseField.TABLE+key;
			//String selection = table+".STATE != "+DatabaseField.SYNCHRONIZED;
			String selection = "STATE != "+DatabaseField.SYNCHRONIZED;
			Log.i("info", "selection "+selection);
			Cursor cursor = sqlite.query(table, null, selection, null, null, null, null);
			//Cursor cursor = selectQuery(key, null, null);
			Log.i("info", "cursor count "+cursor.getCount());
			if (cursor.getCount() > 0){
				int cursorCount = cursor.getCount();
				ArrayList<HashMap<Object, Object>> records = new ArrayList<HashMap<Object, Object>>();
				cursor.moveToFirst();
				for (int i=0; i<cursorCount; i++){
					HashMap<Object, Object> record = new HashMap<Object, Object>();
					String[] columns = cursor.getColumnNames();
					int columnsNb = columns.length;
					boolean checkSyncType = true;
					for (int column=0; column<columnsNb; column++){
						Log.i("info", "column name "+columns[column]+" its value "+getCursorValue(cursor, columns[column]));
						record.put(columns[column], getCursorValue(cursor, columns[column]));
						if ((columns[column].equals(DatabaseField.STATE)) && (Integer.valueOf(String.valueOf(getCursorValue(cursor, columns[column]))) == DatabaseField.SYNCHRONIZED)) {
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
			if (!cursor.isClosed()) {
				cursor.deactivate();
				cursor.close();
			}
		}
		Log.i("info", "tidmap "+tidMap.toString());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//tableNb
		byte[] tableNb = Binary.intToByteArray(tableNbInt);
		bos.write(tableNb, 0, tableNb.length);
		Set<String> tidKeys = tidMap.keySet();
		for (String tidKey : tidKeys){
			//tableId
			int tableIdInt = Integer.valueOf(tidKey);
			byte[] tableId = Binary.intToByteArray(tableIdInt);
			bos.write(tableId, 0, tableId.length);
			
			//fieldsNb
			ArrayList<String> fields = tablesMap.get(tidKey);
			int fieldsNbInt = fields.size();
			byte[] fieldsNb = Binary.intToByteArray(fieldsNbInt);
			bos.write(fieldsNb, 0, fieldsNb.length);

			for (int j=0; j<fieldsNbInt; j++){
				int fieldInt = Integer.valueOf(fields.get(j).split("_")[1]);
				byte[] field = Binary.intToByteArray(fieldInt);
				bos.write(field, 0, field.length);
			}
			//recordsNb
			int recordsNbInt = tidMap.get(tidKey).size();
			Log.i("info", "export record nb "+recordsNbInt);
			byte[] recordsNb = Binary.intToByteArray(recordsNbInt);
			bos.write(recordsNb, 0, recordsNb.length);
			for (int k=0; k<recordsNbInt; k++){
				HashMap<Object, Object> record = tidMap.get(tidKey).get(k);
				//syncType
				int syncTypeInt = Integer.valueOf(String.valueOf(record.get(DatabaseField.STATE)));
				byte[] syncType = Binary.typeToByteArray(syncTypeInt);
				bos.write(syncType, 0, syncType.length);

				//local id
				int localIdInt = Integer.valueOf(String.valueOf(record.get(DatabaseField.ID+tidKey)));
				byte[] localId = Binary.intToByteArray(localIdInt);
				bos.write(localId, 0, localId.length);

				//global id is
				int globalIdInt = Integer.valueOf(String.valueOf(record.get(DatabaseField.GID+tidKey)));
				byte[] globalId = Binary.intToByteArray(globalIdInt);
				bos.write(globalId, 0, globalId.length);

				//value
				if (syncTypeInt != DatabaseField.DELETEVALUE){
					for (int fid=0; fid<fieldsNbInt; fid++){
						String valueType = fieldsMap.get(fields.get(fid).split("_")[1]);
						Log.i("info", "valueType "+valueType);
						byte[] value = null;
						byte[] valueLenth = null;
						
						if (record.get(fields.get(fid)) == null){
							if (DmaHttpClient.getServerInfo() == 1){
								value = Binary.objectToByteArray(record.get(fields.get(fid)), valueType);
								int valueLengthInt = value.length;
								valueLenth = Binary.intToByteArray(valueLengthInt);
							}
							else if (DmaHttpClient.getServerInfo() == 2){
								value = Binary.stringToByteArray(null);
								Log.i("info", "synchronized value "+value);
								valueLenth = Binary.intToByteArray(-1);
							}
						}
						else {
							value = Binary.objectToByteArray(record.get(fields.get(fid)), valueType);
							Log.i("info", "value "+value);
							int valueLengthInt = value.length;
							Log.i("info", "value length "+valueLengthInt);
							valueLenth = Binary.intToByteArray(valueLengthInt);
							if (valueType.equals("BLOB")) {
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
		return bos.toByteArray();
	}
	
	public static void updateIds(byte[] bytes){
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		byte[] tableNb = new byte[Binary.INTBYTE];
		bis.read(tableNb, 0, tableNb.length);
		int tableNbInt = Binary.byteArrayToInt(tableNb);
		for (int i=0; i<tableNbInt; i++){
			//Get table's id
			byte[] tableId = new byte[Binary.INTBYTE];
			bis.read(tableId, 0, tableId.length);
			int tableIdInt = Binary.byteArrayToInt(tableId);
			//Get number of records
			byte[] recordsNb = new byte[Binary.INTBYTE];
			bis.read(recordsNb, 0, recordsNb.length);
			int recordsNbInt = Binary.byteArrayToInt(recordsNb);
			for (int k=0; k<recordsNbInt; k++){
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
	
	private static void updateGid(int tableId, int lid, int gid){
		Log.i("info", "updategid tableId "+tableId+" lid "+lid+" gid "+gid);
		String table = DatabaseField.TABLE+tableId;
		ContentValues values = new ContentValues();
		values.put(DatabaseField.GID+tableId, String.valueOf(gid));
		values.put(DatabaseField.STATE, DatabaseField.SYNCHRONIZED);   //update sync type at the same time 
		String whereClause = "";
		if (tableId == 0){
			String idValue = tableId+""+lid;
			whereClause = DatabaseField.ID+tableId+"=\'"+idValue+"\'";
		}
		else{
			whereClause = DatabaseField.ID+tableId+"=\'"+lid+"\'";
		}
		sqlite.update(table, values, whereClause, null);
	}
	
	private void updateTable(int tableId, ArrayList<Integer> fields, ArrayList<Integer> syncTypeList,
			ArrayList<ArrayList<Object>> records){
		int syncTypeListSize = syncTypeList.size();
		for (int i=0; i<syncTypeListSize; i++){
			switch (syncTypeList.get(i)){
				case DatabaseField.ADDVALUE:
					insertValues(tableId, fields, records.get(i));
					break;
				case DatabaseField.UPDATEVALUE:
					updateValues(tableId, fields, records.get(i));
					break;
				case DatabaseField.DELETEVALUE:
					deleteValues(tableId, fields, records.get(i));
					break;
			}
		}
	}
	
	//Add values and don't check primary key
	private void insertValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record){
		Log.i("info", "add table value "+DatabaseField.TABLE+tableId+" record "+record);
		Cursor cursorAllRows = selectQuery(String.valueOf(tableId), null, null);
		int newId = cursorAllRows.getCount()+1;
		if (!cursorAllRows.isClosed()) {
			Log.i("info", "cursor is not closed");
			cursorAllRows.deactivate();
			cursorAllRows.close();
		}
		Log.i("info", "calculated new id "+newId);
		String idValue = tableId+""+newId;
		Log.i("info", "idvalue is "+idValue);
		ContentValues values = new ContentValues();
		values.put(DatabaseField.ID+tableId, idValue);
		values.put(DatabaseField.GID+tableId, (String)record.get(1));
		values.put(DatabaseField.STATE, DatabaseField.SYNCHRONIZED);
		int fieldsNb = fieldsList.size();
		for (int i=0; i<fieldsNb; i++){
			//check field's type
			if (fieldsMap.get(String.valueOf(fieldsList.get(i))).equals("BLOB")) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(bos);
					oos.writeObject(record.get(i+2));
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				values.put(DatabaseField.FIELD+fieldsList.get(i), bos.toByteArray());
			}
			else {
				values.put(DatabaseField.FIELD+fieldsList.get(i), (String)record.get(i+2));
			}
		}
		String tableName = DatabaseField.TABLE+tableId;
		long insertResult = sqlite.insert(tableName, null, values);
		Log.i("info", "insertresult "+insertResult);
	}
	
	private void updateValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record){
		Log.i("info", "update table value "+DatabaseField.TABLE+tableId+" fieldList "+fieldsList+" record "+record);
		int fieldsNb = fieldsList.size();
		String tableName = DatabaseField.TABLE+tableId;
		//String newValue = DatabaseField.STATE+"="+DatabaseField.SYNCHRONIZED+" AND "+DatabaseField.GID+tableId+"="+record.get(1);
		String newValue = "";
		for (int i=0; i<fieldsNb; i++){
			if (i == fieldsNb-1){
				newValue += DatabaseField.STATE+"="+DatabaseField.SYNCHRONIZED+" ,"+DatabaseField.FIELD+fieldsList.get(i)+"=\'"+record.get(i+2)+"\'";
			}
			else{
				newValue += DatabaseField.FIELD+fieldsList.get(i)+"=\'"+record.get(i+2)+"\', ";
			}
		}
		String update = "UPDATE "+tableName+" SET "+newValue+" WHERE "+DatabaseField.GID+tableId+"=\'"+record.get(1)+"\';";
		Log.i("info", "update "+update);
		sqlite.execSQL(update);
	}
	
	private void deleteValues(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record){
		String delete = "DELETE FROM "+DatabaseField.TABLE+tableId+" WHERE "+DatabaseField.GID+tableId+"=\'"+record.get(1)+"\';";
		sqlite.execSQL(delete);
	}
	
	//Generate primary key for KEY and INTEGER
	private static Object generatePK(String type, Object idValue){
		Object result = null;
		if (type.equals(DatabaseField.KEY)){
			result = KeyGenerator.getKeyGenerated();
		}
		else if (type.equals(DatabaseField.INTEGER)){
			Log.i("info", "idValue "+idValue);
			result = idValue;
		}
		return result;
	}
	
	public static void clearTable(String tableId){
		//Check if table has blob data
		Cursor cursor = sqlite.query(DatabaseField.TABLE+tableId, null, null, null, null, null, null);
		if (cursor.getCount() != 0) {
			String[] columnsNames = cursor.getColumnNames();
			boolean hasBlob = false;
			int check = 0;
			while (check < columnsNames.length) {
				if (columnsNames[check].contains(DatabaseField.FIELD)) {
					if (fieldsMap.get(columnsNames[check].split("_")[1]).equals("BLOB")) {
						hasBlob = true;
						check = columnsNames.length;
					}
				}
				check ++;
			}
			if (hasBlob) {
				int cursorCount = cursor.getCount();
				cursor.moveToFirst();
				for (int i=0; i<cursorCount; i++){
					String[] columns = cursor.getColumnNames();
					int columnsNb = columns.length;
					for (int column=0; column<columnsNb; column++){
						if (columnsNames[column].contains(DatabaseField.FIELD)) {
							if (fieldsMap.get(columns[column].split("_")[1]).equals("BLOB")) {
								String imageName = (String)getCursorValue(cursor, columns[column]);
								//Delete the image
								new File(Constant.packageName+imageName).delete();
							}
						}
					}
					cursor.moveToNext();
				}
				if (!cursor.isClosed()) {
					cursor.deactivate();
					cursor.close();
				}	
			}
			
		}
		sqlite.execSQL("DELETE FROM "+DatabaseField.TABLE+tableId);
	}
	
	public static Cursor selectQuery(String tableId, ArrayList<Integer> fieldList, ArrayList<Object> valueList){
		Cursor result = null;
		String table = DatabaseField.TABLE+tableId;
		Log.i("info", "tableid "+table);
		String selection = createSelectionString(fieldList, valueList);
		Log.i("info", "selection "+selection);
		result = sqlite.query(table, null, selection, null, null, null, null);
		return result;
	}
	
	public static Cursor selectQuery(ArrayList<String> tables, ArrayList<ArrayList<String>> columns, Object filter){
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
	public static void addQuery(int tableId, ArrayList<Integer> fieldsList, ArrayList<Object> record){
		Log.i("info", "record "+record);
		int fieldsNb = fieldsList.size();
		String id = DatabaseField.ID+tableId;
		String gid = DatabaseField.GID+tableId;
		String idValue = String.valueOf(tableId+""+record.get(0));
		String fields = "("+id+", "+gid+", "+DatabaseField.STATE+", ";
		String values = "('"+idValue+"\', 0, "+DatabaseField.ADDVALUE+", ";
		for (int i=0; i<fieldsNb; i++){
			if ((fieldsPKMap.containsKey(String.valueOf(tableId))) && 
					(fieldsPKMap.get(String.valueOf(tableId)).equals(fieldsList.get(i))) &&
					(record.get(i+1) == null)){
				Log.i("info", "in case of filed has pk field, generate its value");
				record.remove(i+1);
				record.add(i+1, generatePK(fieldsMap.get(fieldsList.get(i)), record.get(0)));
			}
			if (i == fieldsNb-1){
				fields += DatabaseField.FIELD+fieldsList.get(i);
				values += "\'"+record.get(i+1)+"\'";
			}
			else{
				fields += DatabaseField.FIELD+fieldsList.get(i)+", ";
				values += "\'"+record.get(i+1)+"\'"+", ";
			}
		}
		if ((!fieldsList.contains(fieldsPKMap.get(String.valueOf(tableId)))) &&
				(fieldsPKMap.containsKey(String.valueOf(tableId)))){
			Log.i("info", "in case of field list hasn't pk field, generate its value");
			fields += ", "+DatabaseField.FIELD+fieldsPKMap.get(String.valueOf(tableId));
			values += ", \'"+generatePK(fieldsMap.get(fieldsPKMap.get(String.valueOf(tableId))), record.get(0))+"\'";
		}
		fields += ")";
		values += ")";
		String tableName = DatabaseField.TABLE+tableId;
		String insert = "INSERT INTO "+tableName+" "+fields+" VALUES"+values+";";
		Log.i("info", "query "+insert);
		sqlite.execSQL(insert);
	}
	
	public static void updateQuery(String tableId, ArrayList<Integer> fieldList, ArrayList<Object> valueList, HashMap<Object, Object> record){
		Log.i("info", "updateQuery Record "+record+" fieldList "+fieldList+" valueList "+valueList);
		//Check if state is Synchronized, update to updatevalue. Otherwise, don't change state's value.
		String table = DatabaseField.TABLE+tableId;
		int newState = DatabaseField.ADDVALUE;
		if ((record.containsKey(DatabaseField.STATE)) && (record.get(DatabaseField.STATE) != null)){
			if (Integer.valueOf(String.valueOf(record.get(DatabaseField.STATE))) == DatabaseField.SYNCHRONIZED){
				newState = DatabaseField.UPDATEVALUE;
			}
			record.remove(DatabaseField.STATE);
		}
		ContentValues values = new ContentValues();
		values.put(DatabaseField.STATE, newState);
		int listSize = fieldList.size();
		for (int i=0; i<listSize; i++){
			values.put(DatabaseField.FIELD+String.valueOf(fieldList.get(i)), String.valueOf(valueList.get(i)));
			record.remove(DatabaseField.FIELD+fieldList.get(i));
		}
		Log.i("info", "values "+values);
		String whereClause = createWhereClause(tableId, record);
		Log.i("info", "whereclause "+whereClause);
		sqlite.update(table, values, whereClause, null);
	}
	
	public static void deleteQuery(String tableId, HashMap<Object, Object> record){
		Log.i("info", "deletequery table "+tableId+" record "+record);
		if (record.containsKey(DatabaseField.STATE)){
			String table = DatabaseField.TABLE+tableId;
			String whereClause = createWhereClause(tableId, record);
			if (Integer.valueOf(String.valueOf(record.get(DatabaseField.STATE))) == DatabaseField.SYNCHRONIZED){
				ContentValues values = new ContentValues();
				values.put(DatabaseField.STATE, DatabaseField.DELETEVALUE);
				Log.i("info", "whereclause "+whereClause+" values "+values);
				sqlite.update(table, values, whereClause, null);
			}
			else{
				Log.i("info", "whereclause "+whereClause);
				sqlite.delete(table, whereClause, null);
			}
		}
	}
	
	public static int getTableNb(){
		return tablesMap.size();
	}
	
	public static Set<String> getTableIds(){
		return tablesMap.keySet();
	}
	
	public static void beginTransaction(){
		STARTTRANSACTION = true;
		//sqlite.beginTransaction();
		//sqlite.execSQL("BEGIN TRANSACTION;");
	}
	
	public static void rollbackTransaction(){
		STARTTRANSACTION = false;
	//	sqlite.endTransaction();
		//sqlite.execSQL("ROLLBACK TRANSACTION;");
	}
	
	public static void commitTransaction(){
		STARTTRANSACTION = false;
		/*sqlite.setTransactionSuccessful();
		sqlite.endTransaction();*/
		//sqlite.execSQL("COMMIT TRANSACTION;");
	}
	
	public static boolean hasStartTransaction(){
		return STARTTRANSACTION;
	}
	
	public static void cleanTables(){
		Log.i("info", "cleantables");
		Set<String> keys = tablesMap.keySet();
		for (String key : keys){
			String table = DatabaseField.TABLE+key;
			Log.i("info", "table "+table);
			String id = DatabaseField.ID+key;
			String[] projectionIn = new String[]{id, DatabaseField.STATE};
			Cursor result = sqlite.query(table, projectionIn, null, null, null, null, null);
			result.moveToFirst();
			int count = result.getCount();
			for (int i=0; i<count; i++){
				String whereClause = id+" = \'"+result.getString(result.getColumnIndexOrThrow(id))+"\'";
				if (result.getInt(result.getColumnIndexOrThrow(DatabaseField.STATE)) == DatabaseField.DELETEVALUE) {
					Log.i("info", "delete value");
					sqlite.delete(table, whereClause, null);					
				}
				result.moveToNext();
			}
			if (!result.isClosed()) {
				Log.i("info", "cursor is not closed");
				result.deactivate();
				result.close();
			}
			else {
				Log.i("info", "cursor is closed");
			}
		}
	}
	
	public static Object getCursorValue(Cursor cursor, String field){
		if (field.indexOf(DatabaseField.FIELD) != -1){
			String fieldId = field.split("_")[1];
			if (fieldsMap.get(fieldId).equals(DatabaseField.INTEGER)){
				return cursor.getInt(cursor.getColumnIndexOrThrow(field));
			}
			else if (fieldsMap.get(fieldId).equals(DatabaseField.DOUBLE)){
				return cursor.getDouble(cursor.getColumnIndexOrThrow(field));
			}
			else{
				return cursor.getString(cursor.getColumnIndexOrThrow(field));
			}
		}
		else{
			return cursor.getString(cursor.getColumnIndexOrThrow(field));
		}
	}
	
	public static ArrayList<ArrayList<Object>> getBlobRecords() {
		return blobRecords;
	}
	
	private static String createWhereClause(String tableId, HashMap<Object, Object> record){
		String result = "";
		if (record.containsKey(DatabaseField.ID+tableId)){
			result += DatabaseField.ID+tableId+" = \'"+record.get(DatabaseField.ID+tableId)+"\'";
		}
		return result;
	}
	
	private static String createTableString(ArrayList<String> tables){
		Log.i("info", "tables "+tables);
		String result = "";
		int size = tables.size();
		for (int i=0; i<size; i++){
			if (i != size-1){
				result += DatabaseField.TABLE+tables.get(i)+", ";
			}
			else{
				result += DatabaseField.TABLE+tables.get(i);
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
				result[i] = DatabaseField.TABLE+column.get(0)+"."+DatabaseField.FIELD+column.get(1);
			}
			return result;
		}
	}
	
	private static String createSelectionString(ArrayList<String> tables, Object filter){
		String result = "";
		if (filter == null){
			int tablesSize = tables.size();
			for (int i=0; i<tablesSize; i++){
				if (i == 0){
					result +=  DatabaseField.TABLE+tables.get(i)+".STATE != "+DatabaseField.DELETEVALUE;
				}
				else{
					result +=  " AND "+DatabaseField.TABLE+tables.get(i)+".STATE != "+DatabaseField.DELETEVALUE;
				}
			}
			if (!createSelectionFKString(tables).equals("")){
				Log.i("info", "filter null "+tables.size());
				result += " AND "+createSelectionFKString(tables);	
			}
			return result;
		}
		else{
			int filterSize = ((ArrayList<?>)filter).size();
			int filterNb = filterSize / 4;
			Log.i("info", "check how many filters "+filterNb+" tables "+tables.toString()+" filter "+filter.toString());
			for (int i=0; i<filterNb; i++){
				//Link is considerated as AND
				if (i == 0){
					String field = (String)DatabaseField.FIELD+((ArrayList<?>)filter).get(4*i);
					Set<String> keySet = tablesMap.keySet();
					String tableName = null;
					for (String s : keySet){
						if (tablesMap.get(s).contains(field)){
							tableName = DatabaseField.TABLE+s;
							result += tableName+"."+field;
						}
					}
					Object operator = Function.getOperator(((ArrayList<?>)filter).get(4*i+1));
					Log.i("info", "operator "+operator);
					result += " "+operator+" ";
					Object value = "\'"+((ArrayList<?>)filter).get(4*i+2)+"\'";
					result += value+" AND "+tableName+".STATE != "+DatabaseField.DELETEVALUE;
				}
				else{
					result += " AND ";
					String field = (String) DatabaseField.FIELD+((ArrayList<?>)filter).get(4*i);
					Set<String> keySet = tablesMap.keySet();
					String tableName = null;
					for (String s : keySet){
						if (tablesMap.get(s).contains(field)){
							tableName = DatabaseField.TABLE+s;
							result += tableName+"."+field;
						}
					}
					Object operator = Function.getOperator(((ArrayList<?>)filter).get(4*i+1));
					Log.i("info", "operator "+operator);
					result += " "+operator+" ";
					Object value = "\'"+((ArrayList<?>)filter).get(4*i+2)+"\'";
					result += value+" AND "+tableName+".STATE != "+DatabaseField.DELETEVALUE;
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
					result += "STATE != "+DatabaseField.DELETEVALUE+" AND "+DatabaseField.FIELD+fieldList.get(i)+" = \'"+valueList.get(i)+"\'";
				}
				else{
					result += " AND "+DatabaseField.FIELD+fieldList.get(i)+" = \'"+valueList.get(i)+"\'";
				}
			}
			return result;
		}		
	}
	
	private static String createSelectionFKString(ArrayList<String> tables){
		Log.i("info", "fk string tables "+tables+" foreignKeyList "+foreignKeyList.toString());
		String result = "";
		int size = foreignKeyList.size();
		if (tables.size() > 1){
			for (int i=0; i<size; i++){
				ArrayList<String> foreignKey = foreignKeyList.get(i);
				if ((tables.contains(foreignKey.get(0))) && (tables.contains(foreignKey.get(2)))){
					if (result != ""){
						result += "AND ";
					}
					result += DatabaseField.TABLE+foreignKey.get(0)+"."+DatabaseField.FIELD+foreignKey.get(1)+" = "+
					DatabaseField.TABLE+foreignKey.get(2)+"."+DatabaseField.FIELD+foreignKey.get(3);
					Log.i("info", "result "+i+" "+result);
				}
			}
		}		
		return result;
	}
}
