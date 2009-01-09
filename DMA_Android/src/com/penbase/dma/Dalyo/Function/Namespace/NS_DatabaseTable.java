package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;

import android.database.Cursor;
import android.util.Log;

import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Database.Record;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_DatabaseTable {
	public static Integer Count(Element element) {
		String tableId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		return Record.countRecord(tableId, filter);
	}
	
	public static void Clear(Element element) {
		String tableId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		DatabaseAdapter.clearTable(tableId);
	}
	
	public static HashMap<Object, Object> CreateNewRecord(Element element) {
		String tableId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		ArrayList<Object> fieldsList = (ArrayList<Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FIELDS, ScriptAttribute.LIST);
		ArrayList<Object> valuesList = (ArrayList<Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUES, ScriptAttribute.LIST);
		Log.i("info", "flist "+fieldsList+" vlist "+valuesList);
		return new Record(tableId, fieldsList, valuesList).getRecord();
	}
	
	public static void DeleteRecord(Element element) {
		String tableId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		HashMap<Object, Object> record = (HashMap<Object, Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		Record.deleteRecord(tableId, record);
	}
	
	public static void EditRecord(Element element) {
		String tableId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		HashMap<Object, Object> record = (HashMap<Object, Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		ArrayList<Object> fieldList = (ArrayList<Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FIELDS, ScriptAttribute.LIST);
		ArrayList<Object> valueList = (ArrayList<Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUES, ScriptAttribute.LIST);
		Record.editRecord(tableId, record, fieldList, valueList);
	}
	
	public static Object GetFieldValue(Element element) {
		Object value = null;
		String tableId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		HashMap<Object, Object> record = (HashMap<Object, Object>)Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		String fieldId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FIELD, ScriptAttribute.FIELD));
		if (record != null) {
			value = record.get(DatabaseAttribute.FIELD+fieldId);
		}
		return value;
	}
	
	public static Object GetFieldValueByPrimaryKey(Element element) {
		Object value = null;
		String tableId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		String keyField = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_KEYFIELD, ScriptAttribute.FIELD));
		Object keyValue = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_KEYVALUE, ScriptAttribute.OBJECT);
		String field = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FIELD, ScriptAttribute.FIELD));
		ArrayList<Integer> fieldList = new ArrayList<Integer>();
		ArrayList<Object> valueList = new ArrayList<Object>();
		fieldList.add(Integer.valueOf(keyField));
		valueList.add(keyValue);
		Cursor cursor = DatabaseAdapter.selectQuery(tableId, fieldList, valueList);
		cursor.moveToFirst();
		String[] columnNames = cursor.getColumnNames();
		int columnsNb = columnNames.length;
		for (int i=0; i<columnsNb; i++) {
			if (columnNames[i].contains(DatabaseAttribute.FIELD)) {
				String fieldName = columnNames[i];
				if (fieldName.split(DatabaseAttribute.FIELD)[1].equals(field)) {
					value = DatabaseAdapter.getCursorValue(cursor, fieldName);
				}
			}
		}
		DatabaseAdapter.closeCursor(cursor);
		return value;
	}
	
	public static ArrayList<HashMap<Object, Object>> GetFilteredRecords(Element element) {
		String tableId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		return getRecords(tableId, filter);
	}
	
	public static ArrayList<HashMap<Object, Object>> GetRecords(Element element) {
		String tableId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		Log.i("info", "tableid "+tableId);
		String fieldId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FIELD, ScriptAttribute.FIELD));
		Log.i("info", "tableid "+fieldId);
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		Log.i("info", "tableid "+tableId+" fieldid "+fieldId+" value "+value);
		ArrayList<Integer> fieldList = new ArrayList<Integer>();
		fieldList.add(Integer.valueOf(fieldId));
		ArrayList<Object> valueList = new ArrayList<Object>();
		valueList.add(value);
		ArrayList<HashMap<Object, Object>> records = new ArrayList<HashMap<Object, Object>>();
		Cursor cursor = DatabaseAdapter.selectQuery(tableId, fieldList, valueList);
		cursor.moveToFirst();
		int cursorLen = cursor.getCount();
		String[] columnNames = cursor.getColumnNames();
		int columnsNb = columnNames.length;
		for (int i=0; i<cursorLen; i++) {
			HashMap<Object, Object> record = new HashMap<Object, Object>();
			for (int j=0; j<columnsNb; j++) {
				record.put(columnNames[j], DatabaseAdapter.getCursorValue(cursor, columnNames[j]));
			}
			records.add(record);
			cursor.moveToNext();
		}
		DatabaseAdapter.closeCursor(cursor);
		Log.i("info", "records "+records);
		return records;
	}
	
	private static ArrayList<HashMap<Object, Object>> getRecords(String tableId, Object filter) {
		ArrayList<HashMap<Object, Object>> records = new ArrayList<HashMap<Object, Object>>();
		ArrayList<String> tables = new ArrayList<String>();
		tables.add(tableId);
		Cursor cursor = DatabaseAdapter.selectQuery(tables, null, filter);
		cursor.moveToFirst();
		int cursorCount = cursor.getCount(); 
		for (int i=0; i<cursorCount; i++) {
			HashMap<Object, Object> record = new HashMap<Object, Object>();
			String[] columnNames = cursor.getColumnNames();
			int columnsRecordSize = columnNames.length;
			for (int j=0; j<columnsRecordSize; j++) {
				record.put(columnNames[j], DatabaseAdapter.getCursorValue(cursor, columnNames[j]));
			}
			records.add(record);
			cursor.moveToNext();
		}
		DatabaseAdapter.closeCursor(cursor);
		return records; 
	}
}
