package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;

import android.database.Cursor;

import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Database.Record;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_DatabaseTable {
	public static String Average(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		String fieldId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		Cursor cursor = DatabaseAdapter.selectQuery(tableId, fieldId, filter, "AVG");
		cursor.moveToFirst();
		String result = cursor.getString(0);
		cursor.moveToLast();
		DatabaseAdapter.closeCursor(cursor);
		return result; 
	}
	
	public static Integer Count(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		return Record.countRecord(tableId, filter);
	}
	
	public static void Clear(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		DatabaseAdapter.clearTable(tableId, filter);
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<Object, Object> CreateNewRecord(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		ArrayList<Object> fieldsList = (ArrayList<Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FIELDS, ScriptAttribute.LIST);
		ArrayList<Object> valuesList = (ArrayList<Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUES, ScriptAttribute.LIST);
		return new Record(tableId, fieldsList, valuesList).getRecord();
	}
	
	@SuppressWarnings("unchecked")
	public static void DeleteRecord(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		HashMap<Object, Object> record = (HashMap<Object, Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		Record.deleteRecord(tableId, record);
	}
	
	@SuppressWarnings("unchecked")
	public static void EditRecord(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		HashMap<Object, Object> record = (HashMap<Object, Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		ArrayList<Object> fieldList = (ArrayList<Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FIELDS, ScriptAttribute.LIST);
		ArrayList<Object> valueList = (ArrayList<Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUES, ScriptAttribute.LIST);
		Record.editRecord(tableId, record, fieldList, valueList);
	}
	
	@SuppressWarnings("unchecked")
	public static Object GetFieldValue(Element element) {
		Object value = null;
		String tableId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		HashMap<Object, Object> record = (HashMap<Object, Object>)Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		String fieldId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		if (record != null) {
			value = record.get(DatabaseAttribute.FIELD+fieldId);
		}
		return value;
	}
	
	public static Object GetFieldValueByPrimaryKey(Element element) {
		Object value = null;
		String tableId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		String keyField = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_KEYFIELD, ScriptAttribute.FIELD).toString();
		Object keyValue = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_KEYVALUE, ScriptAttribute.OBJECT);
		String field = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
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
		String tableId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		return getRecords(tableId, filter);
	}
	
	public static HashMap<Object, Object> GetRecord(Element element) {
		if (NS_DatabaseTable.GetRecords(element).size() > 0) {
			return NS_DatabaseTable.GetRecords(element).get(0);
		}
		else {
			return null;
		}
	}
	
	public static ArrayList<HashMap<Object, Object>> GetRecords(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		String fieldId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
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
		return records;
	}
	
	private static ArrayList<HashMap<Object, Object>> getRecords(String tableId, Object filter) {
		ArrayList<HashMap<Object, Object>> records = new ArrayList<HashMap<Object, Object>>();
		ArrayList<String> tables = new ArrayList<String>();
		tables.add(tableId);
		Cursor cursor = DatabaseAdapter.selectQuery(tables, null, filter, null, null);
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
