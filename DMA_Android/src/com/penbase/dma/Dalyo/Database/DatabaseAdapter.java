package com.penbase.dma.Dalyo.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.penbase.dma.Common;
import com.penbase.dma.Binary.Binary;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Constant.DatabaseTag;
import com.penbase.dma.Dalyo.Function.Function;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class DatabaseAdapter {
	private Document mDbDocument;
	private static SQLiteDatabase sSqlite = null;
	private static HashMap<String, ArrayList<String>> sTablesMap = null;
	private static HashMap<String, String> sTablesNameMap = null;
	private static HashMap<String, String> sFieldsTypeMap = null;
	private static HashMap<String, String> sFieldsNameMap = null;
	private static HashMap<String, String> sFieldsPKMap = null;
	private static ArrayList<ArrayList<String>> sForeignKeyList = null;
	private String mDbPath = null;
	private static ArrayList<ArrayList<Object>> sBlobRecords = null;
	private static HashMap<String, String> sTablesSyncMap = null;
	private static HashMap<String, Boolean> sFieldsSyncMap = null;
	private boolean mWillCreateBlobtable = false;

	public DatabaseAdapter(Document d, String databasePath) {
		this.mDbDocument = d;
		this.mDbPath = databasePath;

		// {tid, [tablename, fieldnames...]}
		sTablesMap = new HashMap<String, ArrayList<String>>();

		sTablesNameMap = new HashMap<String, String>();
		sFieldsTypeMap = new HashMap<String, String>();
		sFieldsNameMap = new HashMap<String, String>();
		sFieldsPKMap = new HashMap<String, String>();
		sForeignKeyList = new ArrayList<ArrayList<String>>();
		sBlobRecords = new ArrayList<ArrayList<Object>>();
		sTablesSyncMap = new HashMap<String, String>();
		sFieldsSyncMap = new HashMap<String, Boolean>();
		if (mDbDocument.getElementsByTagName(DatabaseTag.TABLE).getLength() > 0) {
			createDatabase(mDbPath);
		}
	}

	private void createDatabase(String databasePath) throws SQLException {
		// closeDatabase();
		try {
			if (!isDatabaseExists(databasePath)) {
				sSqlite = SQLiteDatabase.openOrCreateDatabase(databasePath,
						null);
				createTable();
			} else {
				checkDatabase();
				sSqlite = SQLiteDatabase.openOrCreateDatabase(databasePath,
						null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isDatabaseExists(String databasePath) {
		File dbFile = new File(databasePath);
		return dbFile.exists();
	}

	/**
	 * Check database setting
	 * 
	 * @return
	 */
	private boolean checkDatabase() {
		boolean result = true;
		NodeList tableList = mDbDocument
				.getElementsByTagName(DatabaseTag.TABLE);
		int tableLen = tableList.getLength();
		int i = 0;
		while (i < tableLen) {
			Element table = (Element) tableList.item(i);
			String tableSync = table.getAttribute(DatabaseTag.TABLE_SYNC);
			String tableId = table.getAttribute(DatabaseTag.TABLE_ID);
			String tableName = table.getAttribute(DatabaseTag.TABLE_NAME);
			sTablesNameMap.put(tableName, tableId);
			sTablesSyncMap.put(tableId, tableSync);
			NodeList fieldList = table.getChildNodes();
			int fieldLen = fieldList.getLength();
			if (fieldLen > 0) {
				int j = 0;
				ArrayList<String> tableElements = new ArrayList<String>();
				while (j < fieldLen) {
					Element field = (Element) fieldList.item(j);
					String fieldId = field.getAttribute(DatabaseTag.FIELD_ID);
					String fieldType = field
							.getAttribute(DatabaseTag.FIELD_TYPE);
					String fieldSize = field
							.getAttribute(DatabaseTag.FIELD_SIZE);
					String fieldName = field
							.getAttribute(DatabaseTag.FIELD_NAME);
					String fieldSync = field
							.getAttribute(DatabaseTag.FIELD_SYNC);
					sFieldsNameMap.put(fieldId, fieldName);
					sFieldsTypeMap.put(fieldId, fieldType);
					sFieldsSyncMap.put(DatabaseAttribute.FIELD + fieldId,
							Boolean.valueOf(fieldSync));
					tableElements.add(DatabaseAttribute.FIELD + fieldId);
					if (fieldType.equals(DatabaseAttribute.VARCHAR)) {
						fieldType = fieldType + "(" + fieldSize + ")";
					}
					StringBuffer fieldTypeValue = new StringBuffer(fieldType);
					if (field.hasAttribute(DatabaseTag.FIELD_PK)) {
						sFieldsPKMap.put(tableId, fieldId);
						fieldTypeValue.append(" UNIQUE, ");
					} else if ((field
							.hasAttribute(DatabaseTag.FIELD_FOREIGNTABLE))
							&& (field
									.hasAttribute(DatabaseTag.FIELD_FOREIGNFIELD))) {
						ArrayList<String> fk = new ArrayList<String>();
						String foreignTableId = field
								.getAttribute(DatabaseTag.FIELD_FOREIGNTABLE);
						String foreignFieldId = field
								.getAttribute(DatabaseTag.FIELD_FOREIGNFIELD);
						fieldTypeValue.append(foreignTableId).append(" ")
								.append(foreignFieldId);

						fk.add(tableId);
						fk.add(fieldId);
						fk.add(foreignTableId);
						fk.add(foreignFieldId);
						sForeignKeyList.add(fk);
					}
					j++;
				}
				sTablesMap.put(tableId, tableElements);
			}
			i++;
		}
		return result;
	}

	private void createTable() {
		NodeList tableList = mDbDocument
				.getElementsByTagName(DatabaseTag.TABLE);
		int tableLen = tableList.getLength();
		for (int i = 0; i < tableLen; i++) {
			StringBuffer createquery = new StringBuffer(
					"CREATE TABLE IF NOT EXISTS ");
			Element table = (Element) tableList.item(i);
			String tableSync = table.getAttribute(DatabaseTag.TABLE_SYNC);
			String tableId = table.getAttribute(DatabaseTag.TABLE_ID);
			String tableOriginalName = table
					.getAttribute(DatabaseTag.TABLE_NAME);
			sTablesNameMap.put(tableOriginalName, tableId);
			sTablesSyncMap.put(tableId, tableSync);
			String tableName = DatabaseAttribute.TABLE + tableId;
			ArrayList<String> tableElements = new ArrayList<String>();
			String id = DatabaseAttribute.ID + tableId;
			String gid = DatabaseAttribute.GID + tableId;
			createquery.append(tableName).append(" (").append(id).append(
					" VARCHAR(255), ").append(gid);
			createquery.append(" VARCHAR(255), ").append(
					DatabaseAttribute.STATE).append(" INTEGER, ");
			NodeList fieldList = table.getChildNodes();
			int fieldLen = fieldList.getLength();
			ArrayList<ArrayList<String>> foreignKeyTable = new ArrayList<ArrayList<String>>();
			HashMap<String, ArrayList<String>> systemFields = new HashMap<String, ArrayList<String>>();
			if (fieldLen > 0) {
				for (int j = 0; j < fieldLen; j++) {
					// foreignKey has 4 elements (tid, fid, ftid, ffid)
					ArrayList<String> foreignKey = new ArrayList<String>();
					Element field = (Element) fieldList.item(j);
					String fieldId = field.getAttribute(DatabaseTag.FIELD_ID);
					String fieldName = field
							.getAttribute(DatabaseTag.FIELD_NAME);
					sFieldsNameMap.put(fieldId, fieldName);
					String fieldNewName = DatabaseAttribute.FIELD + fieldId;
					tableElements.add(fieldNewName);
					String fieldType = field
							.getAttribute(DatabaseTag.FIELD_TYPE);
					String fieldSize = field
							.getAttribute(DatabaseTag.FIELD_SIZE);
					sFieldsTypeMap.put(fieldId, fieldType);
					if (fieldType.equals(DatabaseAttribute.VARCHAR)) {
						fieldType = fieldType + "(" + fieldSize + ")";
					} else if (fieldType.equals(DatabaseAttribute.BLOB)) {
						if (!mWillCreateBlobtable) {
							sSqlite.execSQL(Constant.CREATE_BLOBTABLE);
							mWillCreateBlobtable = true;
						}
					}
					String fieldSync = field
							.getAttribute(DatabaseTag.FIELD_SYNC);
					sFieldsSyncMap
							.put(fieldNewName, Boolean.valueOf(fieldSync));
					StringBuffer fieldTypeValue = new StringBuffer(fieldType);
					if (field.hasAttribute(DatabaseTag.FIELD_PK)) {
						sFieldsPKMap.put(tableId, fieldId);
						createquery.append(fieldNewName).append(" ").append(
								fieldType).append(" UNIQUE, ");
						fieldTypeValue.append(" UNIQUE, ");
					} else if ((field
							.hasAttribute(DatabaseTag.FIELD_FOREIGNTABLE))
							&& (field
									.hasAttribute(DatabaseTag.FIELD_FOREIGNFIELD))) {
						String foreignTableId = field
								.getAttribute(DatabaseTag.FIELD_FOREIGNTABLE);
						String foreignFieldId = field
								.getAttribute(DatabaseTag.FIELD_FOREIGNFIELD);
						fieldTypeValue.append(foreignTableId).append(" ")
								.append(foreignFieldId);
						createquery.append(fieldNewName).append(" ").append(
								fieldType).append(", ");
						foreignKey.add(tableId);
						foreignKey.add(fieldId);
						foreignKey.add(foreignTableId);
						foreignKey.add(foreignFieldId);
						sForeignKeyList.add(foreignKey);
						foreignKeyTable.add(foreignKey);
					} else {
						createquery.append(fieldNewName).append(" ").append(
								fieldType).append(", ");
					}

					// check system fields setting
					if ((field.hasAttribute(DatabaseTag.FIELD_SYSTABLE))
							&& (field.hasAttribute(DatabaseTag.FIELD_SYSFIELD))) {
						ArrayList<String> systemField = new ArrayList<String>();
						systemField.add(field
								.getAttribute(DatabaseTag.FIELD_SYSTABLE));
						systemField.add(field
								.getAttribute(DatabaseTag.FIELD_SYSFIELD));
						systemFields.put(fieldId, systemField);
					}
				}
			}

			sTablesMap.put(tableId, tableElements);

			String createSql = createquery.substring(0,
					createquery.length() - 2);
			createSql += ");";

			sSqlite.execSQL(createSql);

			if (systemFields.size() > 0) {
				// insert system fields value (contacts, events, tasks)
				HashMap<String, ArrayList<String>> contactFields = new HashMap<String, ArrayList<String>>();
				HashMap<String, ArrayList<String>> eventFields = new HashMap<String, ArrayList<String>>();
				HashMap<String, ArrayList<String>> taskFields = new HashMap<String, ArrayList<String>>();
				for (String key : systemFields.keySet()) {
					if (Integer.valueOf(systemFields.get(key).get(0)) == DatabaseAttribute.CONTACT) {
						contactFields.put(key, systemFields.get(key));
					}
					// else for event and task
				}

				if (contactFields.size() > 0) {
					// contact object
					// contactFields tableId sqlite
					new Contact(tableId, contactFields);
				}

				if (eventFields.size() > 0) {
					// event object
				}

				if (taskFields.size() > 0) {
					// task object
				}
			}
		}
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
		for (int i = 0; i < tableNbInt; i++) {
			// Get table's id
			byte[] tableId = new byte[Constant.INTBYTE];
			bis.read(tableId, 0, tableId.length);
			int tableIdInt = Binary.byteArrayToInt(tableId);
			bos.write(tableId, 0, tableId.length);

			// Get filed's number
			byte[] fieldsNb = new byte[Constant.INTBYTE];
			bis.read(fieldsNb, 0, fieldsNb.length);
			int fieldsNbInt = Binary.byteArrayToInt(fieldsNb);

			// Get fields's ids
			byte[] fields = new byte[Constant.INTBYTE * fieldsNbInt];
			bis.read(fields, 0, fields.length);
			ArrayList<Integer> fieldList = new ArrayList<Integer>();
			ByteArrayInputStream bisFields = new ByteArrayInputStream(fields);
			for (int j = 0; j < fieldsNbInt; j++) {
				byte[] field = new byte[Constant.INTBYTE];
				bisFields.read(field, 0, field.length);
				fieldList.add(Binary.byteArrayToInt(field));
			}

			// Get number of records
			byte[] recordsNb = new byte[Constant.INTBYTE];
			bis.read(recordsNb, 0, recordsNb.length);
			int recordsNbInt = Binary.byteArrayToInt(recordsNb);
			bos.write(recordsNb, 0, recordsNb.length);
			ArrayList<ArrayList<Object>> recordsList = new ArrayList<ArrayList<Object>>();
			ArrayList<Integer> syncTypeList = new ArrayList<Integer>();
			for (int k = 0; k < recordsNbInt; k++) {
				ArrayList<Object> valueList = new ArrayList<Object>();
				// Get type of synchronization
				byte[] syncType = new byte[Constant.TYPEBYTE];
				bis.read(syncType, 0, syncType.length);
				int syncTypeInt = Binary.byteArrayToType(syncType);
				syncTypeList.add(syncTypeInt);
				bos.write(syncType, 0, syncType.length);

				// Get local id
				byte[] localId = new byte[Constant.INTBYTE];
				bis.read(localId, 0, localId.length);
				String localIdString = String
						.valueOf(tableIdInt + "" + (k + 1));
				valueList.add(localIdString);
				bos.write(Binary.intToByteArray(k + 1), 0, Binary
						.intToByteArray(k + 1).length);

				// Get global id
				byte[] globalId = new byte[Constant.INTBYTE];
				bis.read(globalId, 0, globalId.length);
				int globalIdInt = Binary.byteArrayToInt(globalId);
				valueList.add(String.valueOf(globalIdInt));
				bos.write(globalId, 0, globalId.length);

				if (syncTypeInt != DatabaseAttribute.DELETEVALUE) {
					// Get each record's information
					for (int l = 0; l < fieldsNbInt; l++) {
						String valueType = sFieldsTypeMap.get(String
								.valueOf(fieldList.get(l)));
						// Get length of value
						byte[] valueLength = new byte[Constant.INTBYTE];
						bis.read(valueLength, 0, valueLength.length);
						int valueLengthInt = Binary.byteArrayToInt(valueLength);
						// Get value
						byte[] value = new byte[valueLengthInt];
						bis.read(value, 0, value.length);
						Object valueObject = Binary.byteArrayToObject(value,
								valueType);
						valueList.add(valueObject);

						// Check if there is a blob data
						if (valueType.equals(DatabaseAttribute.BLOB)) {
							ArrayList<Object> blobData = new ArrayList<Object>();
							blobData.add(tableIdInt);
							blobData.add(fieldList.get(l));
							blobData.add(valueObject);
							sBlobRecords.add(blobData);
						}
					}
					recordsList.add(valueList);
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

	public void saveBlobData(String fileName, File file) {
		ContentValues values = new ContentValues();
		values.put(Constant.BLOBFILE, fileName);
		try {
			values.put(Constant.BLOBDATA, Common.getBytesFromFile(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		sSqlite.insert(Constant.BLOBTABLE, null, values);
	}

	public void saveBlobData(byte[] data, int index) {
		ContentValues values = new ContentValues();
		values
				.put(Constant.BLOBFILE, sBlobRecords.get(index).get(2)
						.toString());
		values.put(Constant.BLOBDATA, data);
		sSqlite.insert(Constant.BLOBTABLE, null, values);
	}

	public byte[] getBlobdata(String fileName) {
		byte[] result = null;
		StringBuffer selection = new StringBuffer("File = \"");
		selection.append(fileName);
		selection.append("\"");
		Cursor cursor = sSqlite.query(Constant.BLOBTABLE,
				new String[] { Constant.BLOBDATA }, selection.toString(), null,
				null, null, null);
		cursor.moveToFirst();
		result = cursor.getBlob(0);
		cursor.close();
		return result;
	}

	@SuppressWarnings("unchecked")
	public byte[] syncExportTable(ArrayList<String> tables, Object filters) {
		if (sBlobRecords.size() > 0) {
			sBlobRecords.clear();
		}
		int tableNbInt = 0;

		ArrayList<String> keys = null;
		if (tables == null) {
			keys = getExportTableId();
		} else {
			keys = tables;
		}

		HashMap<String, ArrayList<HashMap<Object, Object>>> tidMap = new HashMap<String, ArrayList<HashMap<Object, Object>>>();
		int count = 0;
		for (String key : keys) {
			String table = DatabaseAttribute.TABLE + key;
			StringBuffer selection = new StringBuffer("STATE != ");
			selection.append(DatabaseAttribute.SYNCHRONIZED);

			if ((filters != null)
					&& (((ArrayList<Object>) filters).get(count) != null)) {
				ArrayList<Object> filter = (ArrayList<Object>) ((ArrayList<Object>) filters)
						.get(count);
				if (count == 0) {
					selection.append(" AND ");
				} else {
					selection.append(Function.getLinkOperator(filter.get(3)));
				}
				selection.append(Function.getCompareClauseWithOperator(
						DatabaseAttribute.FIELD + filter.get(0).toString(),
						filter.get(1).toString(), filter.get(2).toString()));
			}

			Cursor cursor = sSqlite.query(table, null, selection.toString(),
					null, null, null, null);
			String[] columns = cursor.getColumnNames();
			if (cursor.getCount() > 0) {
				ArrayList<HashMap<Object, Object>> records = new ArrayList<HashMap<Object, Object>>();
				while (cursor.moveToNext()) {
					HashMap<Object, Object> record = new HashMap<Object, Object>();
					int columnsNb = columns.length;
					boolean checkSyncType = true;
					for (int column = 0; column < columnsNb; column++) {
						String columnName = columns[column];
						record.put(columnName, getCursorValue(cursor,
								columnName));
						if ((columnName.equals(DatabaseAttribute.STATE))
								&& (Integer.valueOf(getCursorValue(cursor,
										columnName).toString()) == DatabaseAttribute.SYNCHRONIZED)) {
							checkSyncType = false;
						} else if (sFieldsSyncMap.containsKey(columnName)
								&& !sFieldsSyncMap.get(columnName)) {
							checkSyncType = false;
						}
					}
					if (checkSyncType) {
						records.add(record);
					}
				}
				tidMap.put(key, records);
				tableNbInt += 1;
			}
			DatabaseAdapter.closeCursor(cursor);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// tableNb
		byte[] tableNb = Binary.intToByteArray(tableNbInt);
		bos.write(tableNb, 0, tableNb.length);
		Set<String> tidKeys = tidMap.keySet();
		for (String tidKey : tidKeys) {
			// tableId
			int tableIdInt = Integer.valueOf(tidKey);
			byte[] tableId = Binary.intToByteArray(tableIdInt);
			bos.write(tableId, 0, tableId.length);

			// fieldsNb
			ArrayList<String> fields = sTablesMap.get(tidKey);
			int fieldsNbInt = fields.size();
			byte[] fieldsNb = Binary.intToByteArray(fieldsNbInt);
			bos.write(fieldsNb, 0, fieldsNb.length);

			for (int j = 0; j < fieldsNbInt; j++) {
				int fieldInt = Integer.valueOf(fields.get(j).split("_")[1]);
				byte[] field = Binary.intToByteArray(fieldInt);
				bos.write(field, 0, field.length);
			}
			// recordsNb
			int recordsNbInt = tidMap.get(tidKey).size();
			byte[] recordsNb = Binary.intToByteArray(recordsNbInt);
			bos.write(recordsNb, 0, recordsNb.length);
			for (int k = 0; k < recordsNbInt; k++) {
				HashMap<Object, Object> record = tidMap.get(tidKey).get(k);
				// syncType
				int syncTypeInt = Integer.valueOf(record.get(
						DatabaseAttribute.STATE).toString());
				byte[] syncType = Binary.typeToByteArray(syncTypeInt);
				bos.write(syncType, 0, syncType.length);

				// local id
				int localIdInt = Integer.valueOf(record.get(
						DatabaseAttribute.ID + tidKey).toString());
				byte[] localId = Binary.intToByteArray(localIdInt);
				bos.write(localId, 0, localId.length);

				// global id is
				int globalIdInt = Integer.valueOf(record.get(
						DatabaseAttribute.GID + tidKey).toString());
				byte[] globalId = Binary.intToByteArray(globalIdInt);
				bos.write(globalId, 0, globalId.length);

				// value
				if (syncTypeInt != DatabaseAttribute.DELETEVALUE) {
					for (int fid = 0; fid < fieldsNbInt; fid++) {
						String valueType = sFieldsTypeMap.get(fields.get(fid)
								.split("_")[1]);
						byte[] value = null;
						byte[] valueLenth = null;

						if (record.get(fields.get(fid)) == null) {
							int serverVersion = getServerVersion();
							if (serverVersion == 1) {
								value = Binary.objectToByteArray(record
										.get(fields.get(fid)), valueType);
								int valueLengthInt = value.length;
								valueLenth = Binary
										.intToByteArray(valueLengthInt);
							} else if (serverVersion == 2) {
								value = Binary.stringToByteArray(null);
								valueLenth = Binary.intToByteArray(-1);
							}
						} else {
							value = Binary.objectToByteArray(record.get(fields
									.get(fid)), valueType);
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
		for (int i = 0; i < tableNbInt; i++) {
			// Get table's id
			byte[] tableId = new byte[Constant.INTBYTE];
			bis.read(tableId, 0, tableId.length);
			int tableIdInt = Binary.byteArrayToInt(tableId);
			// Get number of records
			byte[] recordsNb = new byte[Constant.INTBYTE];
			bis.read(recordsNb, 0, recordsNb.length);
			int recordsNbInt = Binary.byteArrayToInt(recordsNb);
			for (int k = 0; k < recordsNbInt; k++) {
				// Get type of synchronization
				byte[] syncType = new byte[Constant.TYPEBYTE];
				bis.read(syncType, 0, syncType.length);
				int syncTypeInt = Binary.byteArrayToType(syncType);
				// Get local id
				byte[] localId = new byte[Constant.INTBYTE];
				bis.read(localId, 0, localId.length);
				int localIdInt = Binary.byteArrayToInt(localId);
				// Get global id
				byte[] globalId = new byte[Constant.INTBYTE];
				bis.read(globalId, 0, globalId.length);
				int globalIdInt = Binary.byteArrayToInt(globalId);
				updateGid(tableIdInt, localIdInt, globalIdInt);
			}
		}
	}

	private static void updateGid(int tableId, int lid, int gid) {
		String table = DatabaseAttribute.TABLE + tableId;
		ContentValues values = new ContentValues();
		values.put(DatabaseAttribute.GID + tableId, String.valueOf(gid));
		// update sync type at the same time
		values.put(DatabaseAttribute.STATE, DatabaseAttribute.SYNCHRONIZED);
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

	private void updateTable(int tableId, ArrayList<Integer> fields,
			ArrayList<Integer> syncTypeList,
			ArrayList<ArrayList<Object>> records) {
		int syncTypeListSize = syncTypeList.size();
		for (int i = 0; i < syncTypeListSize; i++) {
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
	 * 
	 * @param tableId
	 * @param fieldsList
	 * @param record
	 */
	private void insertValues(int tableId, ArrayList<Integer> fieldsList,
			ArrayList<Object> record) {
		Cursor cursorAllRows = selectQuery(String.valueOf(tableId), null, null);
		int newId = cursorAllRows.getCount() + 1;
		DatabaseAdapter.closeCursor(cursorAllRows);
		StringBuffer idValue = new StringBuffer(tableId);
		idValue.append("").append(newId);
		ContentValues values = new ContentValues();
		values.put(DatabaseAttribute.ID + tableId, idValue.toString());
		values.put(DatabaseAttribute.GID + tableId, (String) record.get(1));
		values.put(DatabaseAttribute.STATE, DatabaseAttribute.SYNCHRONIZED);
		int fieldsNb = fieldsList.size();
		for (int i = 0; i < fieldsNb; i++) {
			values.put(DatabaseAttribute.FIELD + fieldsList.get(i),
					(String) record.get(i + 2));
		}
		String tableName = DatabaseAttribute.TABLE + tableId;
		sSqlite.insert(tableName, null, values);
	}

	private void updateValues(int tableId, ArrayList<Integer> fieldsList,
			ArrayList<Object> record) {
		int fieldsNb = fieldsList.size();
		String tableName = DatabaseAttribute.TABLE + tableId;
		StringBuffer newValue = new StringBuffer("");
		for (int i = 0; i < fieldsNb; i++) {
			if (i == fieldsNb - 1) {
				newValue.append(DatabaseAttribute.STATE).append("=");
				newValue.append(DatabaseAttribute.SYNCHRONIZED).append(" ,");
				newValue.append(DatabaseAttribute.FIELD).append(
						fieldsList.get(i));
				newValue.append("=\'").append(record.get(i + 2)).append("\'");
			} else {
				newValue.append(DatabaseAttribute.FIELD).append(
						fieldsList.get(i));
				newValue.append("=\'").append(record.get(i + 2)).append("\', ");
			}
		}
		StringBuffer update = new StringBuffer("UPDATE ");
		update.append(tableName).append(" SET ");
		update.append(newValue.toString()).append(" WHERE ");
		update.append(DatabaseAttribute.GID).append(tableId);
		update.append("=\'").append(record.get(1)).append("\';");
		sSqlite.execSQL(update.toString());
	}

	private void deleteValues(int tableId, ArrayList<Integer> fieldsList,
			ArrayList<Object> record) {
		StringBuffer selectionString = new StringBuffer(DatabaseAttribute.GID);
		selectionString.append(tableId);
		selectionString.append("=\'");
		selectionString.append(record.get(1));
		selectionString.append("\'");
		// Check if table has blob data
		DatabaseAdapter.deleteBlobFiles(String.valueOf(tableId),
				selectionString.toString());
		StringBuffer delete = new StringBuffer("DELETE FROM ");
		delete.append(DatabaseAttribute.TABLE).append(tableId)
				.append(" WHERE ").append(selectionString).append(";");
		sSqlite.execSQL(delete.toString());
	}

	/**
	 * Generates primary key for KEY and INTEGER
	 * 
	 * @param type
	 * @param idValue
	 * @return
	 */
	private static Object generatePK(String type, Object idValue) {
		Object result = null;
		if (type.equals(DatabaseAttribute.KEY)) {
			result = KeyGenerator.getKeyGenerated();
		} else if (type.equals(DatabaseAttribute.INTEGER)) {
			result = idValue;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static void clearTable(String tableId, Object filters) {
		StringBuffer selectionString = new StringBuffer();
		if (filters != null) {
			int filtersNb = ((ArrayList<Object>) filters).size();
			for (int i = 0; i < filtersNb; i++) {
				ArrayList<Object> filter = (ArrayList<Object>) ((ArrayList<Object>) filters)
						.get(i);
				if (i != 0) {
					// TODO Add link
				}
				selectionString.append(Function.getCompareClauseWithOperator(
						DatabaseAttribute.FIELD + filter.get(0).toString(),
						filter.get(1).toString(), filter.get(2).toString()));
			}
		}

		// Check if table has blob data
		DatabaseAdapter.deleteBlobFiles(tableId, selectionString.toString());

		StringBuffer delete = new StringBuffer("DELETE FROM ");
		delete.append(DatabaseAttribute.TABLE).append(tableId);
		if (selectionString.length() == 0) {
			delete.append(";");
			sSqlite.execSQL(delete.toString());
		} else {
			delete.append(" WHERE ").append(selectionString.toString()).append(
					";");
			sSqlite.execSQL(delete.toString());
		}
	}

	private static void deleteBlobFiles(String tableId, String selectionString) {
		Cursor cursor = sSqlite.query(DatabaseAttribute.TABLE + tableId, null,
				selectionString, null, null, null, null);
		if (cursor.getCount() != 0) {
			String[] columnsNames = cursor.getColumnNames();
			ArrayList<Integer> blobColumnArray = new ArrayList<Integer>();
			int columnsNameNb = columnsNames.length;
			for (int i = 0; i < columnsNameNb; i++) {
				if (columnsNames[i].contains(DatabaseAttribute.FIELD)) {
					if (sFieldsTypeMap.get(columnsNames[i].split("_")[1])
							.equals(DatabaseAttribute.BLOB)) {
						blobColumnArray.add(i);
					}
				}
			}
			int blobColumnArraySize = blobColumnArray.size();
			if (blobColumnArraySize > 0) {
				while (cursor.moveToNext()) {
					for (int j = 0; j < blobColumnArraySize; j++) {
						String imageName = (String) getCursorValue(cursor,
								columnsNames[blobColumnArray.get(j)]);
						StringBuffer selection = new StringBuffer("File = \"");
						selection.append(imageName);
						selection.append("\"");
						sSqlite.delete(Constant.BLOBTABLE,
								selection.toString(), null);
					}
				}
			}
		}
		DatabaseAdapter.closeCursor(cursor);
	}

	public static Cursor selectQuery(String tableId,
			ArrayList<Integer> fieldList, ArrayList<Object> valueList) {
		String table = DatabaseAttribute.TABLE + tableId;
		String selection = createSelectionString(fieldList, valueList);
		return sSqlite.query(table, null, selection, null, null, null, null);
	}

	public static Cursor selectQuery(String tableId, String fieldId,
			Object filter, String type) {
		StringBuffer projectionIn = new StringBuffer(type);
		projectionIn.append("(");
		projectionIn.append(DatabaseAttribute.FIELD);
		projectionIn.append(fieldId);
		projectionIn.append(")");
		ArrayList<String> tables = new ArrayList<String>();
		tables.add(tableId);
		String selection = createSelectionString(tables, filter);
		return sSqlite.query(DatabaseAttribute.TABLE + tableId,
				new String[] { projectionIn.toString() }, selection, null,
				null, null, null);
	}

	@SuppressWarnings("unchecked")
	public static Cursor selectQuery(ArrayList<String> tables,
			ArrayList<ArrayList<String>> columns, Object filter, Object order,
			Object distinct) {
		String table = createTableString(tables);
		String[] projectionIn = createProjectionStrings(columns);
		String selection = createSelectionString(tables, filter);
		String orderBy = null;
		if (order != null && ((ArrayList<Object>) order).size() > 0) {
			String filedId = ((ArrayList<Object>) order).get(0).toString();
			orderBy = DatabaseAttribute.FIELD + filedId;
			if (!((ArrayList<Object>) order).get(1).toString().equals(
					Constant.TRUE)) {
				orderBy += " DESC";
			}
		}
		boolean isDisctinct = false;
		if (distinct != null) {
			if (distinct.toString().equals(Constant.TRUE)) {
				isDisctinct = true;
			}
		}
		SQLiteDatabase sqlite = sSqlite;
		return sqlite.query(isDisctinct, table, projectionIn, selection, null,
				null, null, orderBy, null);
	}

	/**
	 * Adds values and check primary key
	 * 
	 * @param tableId
	 * @param fieldsList
	 * @param record
	 */
	public static void addQuery(int tableId, ArrayList<Integer> fieldsList,
			ArrayList<Object> record) {
		// record has 1 more element than fieldsList, the first element of
		// record is the new ID
		int fieldsNb = fieldsList.size();
		String id = DatabaseAttribute.ID + tableId;
		String gid = DatabaseAttribute.GID + tableId;
		String idValue = String.valueOf(tableId + "" + record.get(0));
		StringBuffer fields = new StringBuffer("(");
		fields.append(id).append(", ").append(gid).append(", ");
		fields.append(DatabaseAttribute.STATE).append(", ");
		StringBuffer values = new StringBuffer("('");
		values.append(idValue).append("\', 0, ").append(
				DatabaseAttribute.ADDVALUE).append(", ");
		for (int i = 0; i < fieldsNb; i++) {
			if ((sFieldsPKMap.containsKey(String.valueOf(tableId)))
					&& (sFieldsPKMap.get(String.valueOf(tableId))
							.equals(fieldsList.get(i)))
					&& (record.get(i + 1) == null)) {
				record.remove(i + 1);
				record.add(i + 1, generatePK(sFieldsTypeMap.get(fieldsList
						.get(i)), record.get(0)));
			}

			fields.append(DatabaseAttribute.FIELD).append(fieldsList.get(i));
			values.append("\'").append(record.get(i + 1)).append("\'");

			if (i != fieldsNb - 1) {
				fields.append(", ");
				values.append(", ");
			}
		}
		if ((!fieldsList.contains(sFieldsPKMap.get(String.valueOf(tableId))))
				&& (sFieldsPKMap.containsKey(String.valueOf(tableId)))) {
			fields.append(", ").append(DatabaseAttribute.FIELD).append(
					sFieldsPKMap.get(String.valueOf(tableId)));
			values.append(", \'");
			values.append(generatePK(sFieldsTypeMap.get(sFieldsPKMap.get(String
					.valueOf(tableId))), record.get(0)));
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
		sSqlite.execSQL(insert.toString());
	}

	public static void updateQuery(String tableId,
			ArrayList<Integer> fieldList, ArrayList<Object> valueList,
			HashMap<Object, Object> record) {
		// Check if state is Synchronized, update to updatevalue. Otherwise,
		// don't change state's value.
		String table = DatabaseAttribute.TABLE + tableId;
		int newState = DatabaseAttribute.ADDVALUE;
		if ((record.containsKey(DatabaseAttribute.STATE))
				&& (record.get(DatabaseAttribute.STATE) != null)) {
			if (Integer.valueOf(record.get(DatabaseAttribute.STATE).toString()) == DatabaseAttribute.SYNCHRONIZED) {
				newState = DatabaseAttribute.UPDATEVALUE;
			}
			record.remove(DatabaseAttribute.STATE);
		}
		ContentValues values = new ContentValues();
		values.put(DatabaseAttribute.STATE, newState);
		int listSize = fieldList.size();
		for (int i = 0; i < listSize; i++) {
			values.put(DatabaseAttribute.FIELD + fieldList.get(i).toString(),
					valueList.get(i).toString());
			record.remove(DatabaseAttribute.FIELD + fieldList.get(i));
		}
		String whereClause = createWhereClause(tableId, record);
		sSqlite.update(table, values, whereClause, null);
	}

	public static void updateRecord(String tableId, ContentValues values,
			String whereClause) {
		sSqlite.update(DatabaseAttribute.TABLE + tableId, values, whereClause,
				null);
	}

	public static void deleteQuery(String tableId,
			HashMap<Object, Object> record) {
		if (record.containsKey(DatabaseAttribute.STATE)) {
			String table = DatabaseAttribute.TABLE + tableId;
			String whereClause = createWhereClause(tableId, record);
			if (Integer.valueOf(record.get(DatabaseAttribute.STATE).toString()) == DatabaseAttribute.SYNCHRONIZED) {
				ContentValues values = new ContentValues();
				values.put(DatabaseAttribute.STATE,
						DatabaseAttribute.DELETEVALUE);
				sSqlite.update(table, values, whereClause, null);
			} else {
				sSqlite.delete(table, whereClause, null);
			}
		}
	}

	public static ArrayList<Integer> getImportTableId() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		HashMap<String, String> tablesSyncMap = sTablesSyncMap;
		Set<String> idSet = tablesSyncMap.keySet();
		for (String id : idSet) {
			String syncType = tablesSyncMap.get(id);
			if (syncType.contains(DatabaseTag.IMPORT)) {
				result.add(Integer.valueOf(id));
			}
		}
		return result;
	}

	private ArrayList<String> getExportTableId() {
		ArrayList<String> result = new ArrayList<String>();
		HashMap<String, String> tablesSyncMap = sTablesSyncMap;
		Set<String> idSet = tablesSyncMap.keySet();
		for (String id : idSet) {
			String syncType = tablesSyncMap.get(id);
			if (syncType.contains(DatabaseTag.EXPORT)) {
				result.add(id);
			}
		}
		return result;
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
		if (!sSqlite.inTransaction()) {
			sSqlite.beginTransaction();
		}
	}

	public static void rollbackTransaction() {
		if (sSqlite.inTransaction()) {
			sSqlite.endTransaction();
		}
	}

	public static void commitTransaction() {
		if (sSqlite.inTransaction()) {
			sSqlite.setTransactionSuccessful();
			sSqlite.endTransaction();
		}
	}

	public static void cleanTables() {
		Set<String> keys = sTablesMap.keySet();
		SQLiteDatabase sqlite = sSqlite;
		for (String key : keys) {
			String table = DatabaseAttribute.TABLE + key;
			String id = DatabaseAttribute.ID + key;
			String[] projectionIn = new String[] { id, DatabaseAttribute.STATE };
			Cursor deleteCursor = sqlite.query(table, projectionIn, null, null,
					null, null, null);
			while (deleteCursor.moveToNext()) {
				StringBuffer whereClause = new StringBuffer(id);
				whereClause.append(" = \'").append(
						deleteCursor.getString(deleteCursor
								.getColumnIndexOrThrow(id))).append("\'");
				if (deleteCursor.getInt(deleteCursor
						.getColumnIndexOrThrow(DatabaseAttribute.STATE)) == DatabaseAttribute.DELETEVALUE) {
					sqlite.delete(table, whereClause.toString(), null);
				}
			}
			DatabaseAdapter.closeCursor(deleteCursor);
		}
	}

	public static Object getCursorValue(Cursor cursor, String field) {
		if (cursor.getCount() > 0) {
			if (field.indexOf(DatabaseAttribute.FIELD) != -1) {
				String fieldId = Integer.valueOf(field.split("_")[1])
						.toString();
				if (sFieldsTypeMap.get(fieldId).equals(
						DatabaseAttribute.INTEGER)) {
					return cursor.getInt(cursor.getColumnIndexOrThrow(field));
				} else if (sFieldsTypeMap.get(fieldId).equals(
						DatabaseAttribute.DOUBLE)) {
					return cursor
							.getDouble(cursor.getColumnIndexOrThrow(field));
				} else {
					String value = cursor.getString(cursor
							.getColumnIndexOrThrow(field));
					if (value == null) {
						return "";
					} else {
						return value;
					}
				}
			} else {
				return cursor.getString(cursor.getColumnIndexOrThrow(field));
			}
		} else {
			return null;
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

	private static String createWhereClause(String tableId,
			HashMap<Object, Object> record) {
		StringBuffer result = new StringBuffer("");
		String tid = DatabaseAttribute.ID + tableId;
		if (record.containsKey(tid)) {
			result.append(tid).append(" = \'");
			result.append(record.get(tid)).append("\'");
		}
		return result.toString();
	}

	private static String createTableString(ArrayList<String> tables) {
		StringBuffer result = new StringBuffer("");
		int size = tables.size();
		for (int i = 0; i < size; i++) {
			if (i != size - 1) {
				result.append(DatabaseAttribute.TABLE).append(tables.get(i))
						.append(", ");
			} else {
				result.append(DatabaseAttribute.TABLE).append(tables.get(i));
			}
		}
		return result.toString();
	}

	private static String[] createProjectionStrings(
			ArrayList<ArrayList<String>> columns) {
		if (columns == null) {
			return null;
		} else {
			int size = columns.size();
			String[] result = new String[size];
			for (int i = 0; i < size; i++) {
				ArrayList<String> column = columns.get(i);
				StringBuffer value = new StringBuffer(DatabaseAttribute.TABLE);
				value.append(column.get(0)).append(".").append(
						DatabaseAttribute.FIELD).append(column.get(1));
				result[i] = value.toString();
			}
			return result;
		}
	}

	@SuppressWarnings("unchecked")
	private static String createSelectionString(ArrayList<String> tables,
			Object filters) {
		StringBuffer result = new StringBuffer("");
		int tablesSize = tables.size();
		for (int i = 0; i < tablesSize; i++) {
			if (i != 0) {
				result.append(" AND ");
			}
			result.append(DatabaseAttribute.TABLE).append(tables.get(i))
					.append(".STATE != ").append(DatabaseAttribute.DELETEVALUE);
		}
		if (!createSelectionFKString(tables).equals("")) {
			result.append(" AND ").append(createSelectionFKString(tables));
		}
		if (filters != null) {
			ArrayList<Object> filtersList = (ArrayList<Object>) filters;
			int filtersNb = filtersList.size();
			for (int i = 0; i < filtersNb; i++) {
				ArrayList<Object> filter = (ArrayList<Object>)filtersList.get(i);
				if (i == 0) {
					result.append(" AND ");
				} else {
					result.append(Function.getLinkOperator(filter.get(3)));
				}
				String compareClause = Function.getCompareClauseWithOperator(
						DatabaseAttribute.FIELD + filter.get(0).toString(),
						filter.get(1).toString(), filter.get(2).toString());
				result.append(compareClause);
			}
		}
		return result.toString();
	}

	private static String createSelectionString(ArrayList<Integer> fieldList,
			ArrayList<Object> valueList) {
		if ((fieldList == null) && (valueList == null)) {
			return null;
		} else {
			StringBuffer result = new StringBuffer("");
			int size = fieldList.size();
			for (int i = 0; i < size; i++) {
				if (i == 0) {
					result.append("STATE != ").append(
							DatabaseAttribute.DELETEVALUE).append(" AND ");
					result.append(DatabaseAttribute.FIELD).append(
							fieldList.get(i));
					result.append(" = \'").append(valueList.get(i))
							.append("\'");
				} else {
					result.append(" AND ").append(DatabaseAttribute.FIELD)
							.append(fieldList.get(i)).append(" = \'");
					result.append(valueList.get(i)).append("\'");
				}
			}
			return result.toString();
		}
	}

	private static String createSelectionFKString(ArrayList<String> tables) {
		StringBuffer result = new StringBuffer("");
		ArrayList<ArrayList<String>> foreignKeyList = sForeignKeyList;
		int size = foreignKeyList.size();
		if (tables.size() > 1) {
			for (int i = 0; i < size; i++) {
				ArrayList<String> foreignKey = foreignKeyList.get(i);
				if ((tables.contains(foreignKey.get(0)))
						&& (tables.contains(foreignKey.get(2)))) {
					if (result.length() > 0) {
						result.append("AND ");
					}
					result.append(DatabaseAttribute.TABLE).append(
							foreignKey.get(0)).append(".");
					result.append(DatabaseAttribute.FIELD).append(
							foreignKey.get(1)).append(" = ");
					result.append(DatabaseAttribute.TABLE).append(
							foreignKey.get(2)).append(".");
					result.append(DatabaseAttribute.FIELD).append(
							foreignKey.get(3));
				}
			}
		}
		return result.toString();
	}

	private int getServerVersion() {
		// Modify this method when the server has immigrate to spv2
		return 1;
	}

	public void closeDatabase() {
		if ((sSqlite != null) && (sSqlite.isOpen())) {
			if (sSqlite.inTransaction()) {
				sSqlite.endTransaction();
			}
			sSqlite.close();
		}
		sSqlite = null;
		sTablesMap = null;
		sTablesNameMap = null;
		sFieldsTypeMap = null;
		sFieldsNameMap = null;
		sFieldsPKMap = null;
		sForeignKeyList = null;
		sBlobRecords = null;
		sTablesSyncMap = null;
		sFieldsSyncMap = null;
	}
}