package com.penbase.dma.Dalyo.Function.Namespace;

import android.database.Cursor;

import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class NS_DatabaseDataset {
	@SuppressWarnings("unchecked")
	public static Object GetValue(Element element) {
		Object value = null;
		HashMap<Object, Object> dataset = (HashMap<Object, Object>)Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.DATASET, ScriptAttribute.DATASET);
		String fieldId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		if (dataset != null) {
			value = dataset.get(DatabaseAttribute.FIELD+fieldId);
		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public static Object Select(Element element) {
		ArrayList<Object> fieldsList = (ArrayList<Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FIELDS, ScriptAttribute.LIST);
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		Object order = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.ORDER, ScriptAttribute.ORDER);
		ArrayList<String> tables = new ArrayList<String>();
		ArrayList<ArrayList<String>> columns = new ArrayList<ArrayList<String>>();
		Set<String> tableIds = DatabaseAdapter.getTableIds();
		HashMap<String, ArrayList<String>> tablesMap = DatabaseAdapter.getTablesMap();
		for (Object field : fieldsList) {
			for (String tid : tableIds) {
				if (tablesMap.get(tid).contains(DatabaseAttribute.FIELD + field)) {
					ArrayList<String> column = new ArrayList<String>();
					column.add(tid);
					column.add(field.toString());
					columns.add(column);
					if (!tables.contains(tid)) {
						tables.add(tid);
					}
				}
			}
		}
		ArrayList<HashMap<Object, Object>> records = new ArrayList<HashMap<Object, Object>>();
		Cursor cursor = DatabaseAdapter.selectQuery(tables, columns, filter, order, null);
		while (cursor.moveToNext()) {
			HashMap<Object, Object> record = new HashMap<Object, Object>();
			String[] columnNames = cursor.getColumnNames();
			int columnsRecordSize = columnNames.length;
			for (int j=0; j<columnsRecordSize; j++) {
				String columnName = columnNames[j];
				record.put(columnName, DatabaseAdapter.getCursorValue(cursor, columnName));
			}
			records.add(record);
		}
		DatabaseAdapter.closeCursor(cursor);
		return records;
	}
}
