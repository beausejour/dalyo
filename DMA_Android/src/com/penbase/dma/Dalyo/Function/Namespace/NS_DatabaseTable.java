package com.penbase.dma.Dalyo.Function.Namespace;

import android.content.ContentValues;
import android.database.Cursor;

import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Component.Form;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Database.Record;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class NS_DatabaseTable {
	private static HashMap<String, String> sTableIdValuesMap = null;

	public static String Average(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		String fieldId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		Cursor cursor = DatabaseAdapter.selectQuery(tableId, fieldId, filter,
				"AVG");
		cursor.moveToFirst();
		String result = cursor.getString(0);
		DatabaseAdapter.closeCursor(cursor);
		return result;
	}

	public static void CancelEditRecord(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		if (sTableIdValuesMap.containsKey(tableId)) {
			sTableIdValuesMap.remove(tableId);
		}
		DatabaseAdapter.rollbackTransaction();
	}

	public static Integer Count(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		return Record.countRecord(tableId, filter);
	}

	public static void Clear(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		DatabaseAdapter.clearTable(tableId, filter);
	}

	@SuppressWarnings("unchecked")
	public static HashMap<Object, Object> CreateNewRecord(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		ArrayList<Object> fieldsList = (ArrayList<Object>) Function.getValue(
				element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_FIELDS, ScriptAttribute.LIST);
		ArrayList<Object> valuesList = (ArrayList<Object>) Function.getValue(
				element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUES, ScriptAttribute.LIST);
		return new Record(tableId, fieldsList, valuesList).getRecord();
	}

	@SuppressWarnings("unchecked")
	public static void DeleteRecord(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		HashMap<Object, Object> record = (HashMap<Object, Object>) Function
				.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.RECORD,
						ScriptAttribute.RECORD);
		Record.deleteRecord(tableId, record);
	}

	public static void DeleteRecords(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		ArrayList<HashMap<Object, Object>> records = getRecords(tableId, filter);
		if (records.size() > 0) {
			for (HashMap<Object, Object> record : records) {
				Record.deleteRecord(tableId, record);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void EditRecord(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		HashMap<Object, Object> record = (HashMap<Object, Object>) Function
				.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.RECORD,
						ScriptAttribute.RECORD);
		ArrayList<Object> fieldList = (ArrayList<Object>) Function.getValue(
				element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_FIELDS, ScriptAttribute.LIST);
		ArrayList<Object> valueList = (ArrayList<Object>) Function.getValue(
				element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUES, ScriptAttribute.LIST);
		Record.editRecord(tableId, record, fieldList, valueList);
	}

	public static String GetFieldByName(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		String fieldName = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_FIELDNAME,
				ScriptAttribute.STRING).toString();
		ArrayList<String> checkFieldsId = new ArrayList<String>();
		HashMap<String, String> filedsName = DatabaseAdapter.getFieldsNameMap();
		Iterator<String> iterador = filedsName.keySet().iterator();
		while (iterador.hasNext()) {
			String key = iterador.next().toString();
			if (filedsName.get(key).equals(fieldName)) {
				checkFieldsId.add(key);
			}
		}

		ArrayList<String> fields = DatabaseAdapter.getTablesMap().get(tableId);
		ArrayList<Object> fieldsId = new ArrayList<Object>();
		if (fields.size() > 0) {
			for (String field : fields) {
				fieldsId.add(field.split("_")[1]);
			}
		}

		String fieldId = "";
		int fieldsIdSize = checkFieldsId.size();
		int i = 0;
		while (i < fieldsIdSize) {
			String fid = checkFieldsId.get(i);
			if (fieldsId.contains(fid)) {
				fieldId = fid;
				i = fieldsIdSize;
			}
			i++;
		}
		return fieldId;
	}

	public static ArrayList<Object> GetFields(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		ArrayList<String> fields = DatabaseAdapter.getTablesMap().get(tableId);
		ArrayList<Object> result = new ArrayList<Object>();
		if (fields.size() > 0) {
			for (String field : fields) {
				result.add(field.split("_")[1]);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Object GetFieldValue(Element element) {
		Object value = null;
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		HashMap<Object, Object> record = (HashMap<Object, Object>) Function
				.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.RECORD,
						ScriptAttribute.RECORD);
		String fieldId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		if (record != null) {
			value = record.get(DatabaseAttribute.FIELD + fieldId);
		}
		return value;
	}

	public static Object GetFieldValueByPrimaryKey(Element element) {
		Object value = null;
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		String keyField = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_KEYFIELD, ScriptAttribute.FIELD)
				.toString();
		Object keyValue = Function
				.getValue(element, ScriptTag.PARAMETER,
						ScriptAttribute.PARAMETER_NAME_KEYVALUE,
						ScriptAttribute.OBJECT);
		String field = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		ArrayList<Integer> fieldList = new ArrayList<Integer>();
		ArrayList<Object> valueList = new ArrayList<Object>();
		fieldList.add(Integer.valueOf(keyField));
		valueList.add(keyValue);
		Cursor cursor = DatabaseAdapter.selectQuery(tableId, fieldList,
				valueList);
		cursor.moveToFirst();
		String[] columnNames = cursor.getColumnNames();
		int columnsNb = columnNames.length;
		for (int i = 0; i < columnsNb; i++) {
			String fieldName = columnNames[i];
			if (fieldName.contains(DatabaseAttribute.FIELD)) {
				if (fieldName.split(DatabaseAttribute.FIELD)[1].equals(field)) {
					value = DatabaseAdapter.getCursorValue(cursor, fieldName);
				}
			}
		}
		DatabaseAdapter.closeCursor(cursor);
		return value;
	}

	public static HashMap<Object, Object> GetFilteredRecord(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		if (getRecords(tableId, filter).size() > 0) {
			return getRecords(tableId, filter).get(0);
		} else {
			return null;
		}
	}

	public static ArrayList<HashMap<Object, Object>> GetFilteredRecords(
			Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		return getRecords(tableId, filter);
	}

	public static HashMap<Object, Object> GetRecord(Element element) {
		if (NS_DatabaseTable.GetRecords(element).size() > 0) {
			return NS_DatabaseTable.GetRecords(element).get(0);
		} else {
			return null;
		}
	}

	public static ArrayList<HashMap<Object, Object>> GetRecords(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		String fieldId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		ArrayList<Integer> fieldList = new ArrayList<Integer>();
		fieldList.add(Integer.valueOf(fieldId));
		ArrayList<Object> valueList = new ArrayList<Object>();
		valueList.add(value);
		ArrayList<HashMap<Object, Object>> records = new ArrayList<HashMap<Object, Object>>();
		Cursor cursor = DatabaseAdapter.selectQuery(tableId, fieldList,
				valueList);
		String[] columnNames = cursor.getColumnNames();
		int columnsNb = columnNames.length;
		while (cursor.moveToNext()) {
			HashMap<Object, Object> record = new HashMap<Object, Object>();
			for (int j = 0; j < columnsNb; j++) {
				String columnName = columnNames[j];
				record.put(columnName, DatabaseAdapter.getCursorValue(cursor,
						columnName));
			}
			records.add(record);
		}
		DatabaseAdapter.closeCursor(cursor);
		return records;
	}

	public static boolean IsEditingRecord(Element element) {
		boolean result = false;
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		if (sTableIdValuesMap != null) {
			result = sTableIdValuesMap.containsKey(tableId);
		}
		return result;
	}

	public static String Max(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		String fieldId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		Cursor cursor = DatabaseAdapter.selectQuery(tableId, fieldId, filter,
				"MAX");
		cursor.moveToFirst();
		String result = cursor.getString(0);
		DatabaseAdapter.closeCursor(cursor);
		return result;
	}

	public static String Min(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		String fieldId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		Cursor cursor = DatabaseAdapter.selectQuery(tableId, fieldId, filter,
				"MIN");
		cursor.moveToFirst();
		String result = cursor.getString(0);
		DatabaseAdapter.closeCursor(cursor);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static void StartEditRecord(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		HashMap<Object, Object> record = (HashMap<Object, Object>) Function
				.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.RECORD,
						ScriptAttribute.RECORD);
		HashMap<String, Form> layoutsMap = ApplicationView.getLayoutsMap();
		Set<String> keys = layoutsMap.keySet();
		for (String key : keys) {
			layoutsMap.get(key).setRecordByTable(tableId, record);
		}
		if (sTableIdValuesMap == null) {
			sTableIdValuesMap = new HashMap<String, String>();
		}
		String tid = DatabaseAttribute.ID + tableId;
		if (record != null && record.containsKey(tid)) {
			sTableIdValuesMap.put(tableId, record.get(tid).toString());
			DatabaseAdapter.beginTransaction();
		}
	}

	public static String Sum(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		String fieldId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		Cursor cursor = DatabaseAdapter.selectQuery(tableId, fieldId, filter,
				"SUM");
		cursor.moveToFirst();
		String result = cursor.getString(0);
		DatabaseAdapter.closeCursor(cursor);
		return result;
	}

	public static void ValidateEditRecord(Element element) {
		String tableId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.TABLE, ScriptAttribute.TABLE).toString();
		if (sTableIdValuesMap.containsKey(tableId)) {
			ContentValues values = new ContentValues();
			HashMap<String, Form> layoutsMap = ApplicationView.getLayoutsMap();
			Set<String> keys = layoutsMap.keySet();
			for (String key : keys) {
				values.putAll(layoutsMap.get(key).validateEditRecord(tableId));
			}
			StringBuffer whereClause = new StringBuffer(DatabaseAttribute.ID);
			whereClause.append(tableId).append(" = \'").append(
					sTableIdValuesMap.get(tableId)).append("\'");
			DatabaseAdapter.updateRecord(tableId, values, whereClause
					.toString());
			DatabaseAdapter.commitTransaction();
			sTableIdValuesMap.remove(tableId);
		}
	}

	private static ArrayList<HashMap<Object, Object>> getRecords(
			String tableId, Object filter) {
		ArrayList<HashMap<Object, Object>> records = new ArrayList<HashMap<Object, Object>>();
		ArrayList<String> tables = new ArrayList<String>();
		tables.add(tableId);
		Cursor cursor = DatabaseAdapter.selectQuery(tables, null, filter, null,
				null);
		while (cursor.moveToNext()) {
			HashMap<Object, Object> record = new HashMap<Object, Object>();
			String[] columnNames = cursor.getColumnNames();
			int columnsRecordSize = columnNames.length;
			for (int j = 0; j < columnsRecordSize; j++) {
				String columnName = columnNames[j];
				record.put(columnName, DatabaseAdapter.getCursorValue(cursor,
						columnName));
			}
			records.add(record);
		}
		DatabaseAdapter.closeCursor(cursor);
		return records;
	}
}
