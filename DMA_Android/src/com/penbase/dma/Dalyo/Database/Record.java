package com.penbase.dma.Dalyo.Database;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;

public class Record {
	private HashMap<Object, Object> mCurrentRecord = null;
	
	public Record(String tableId, ArrayList<Object> fList, ArrayList<Object> vList) {
		if ((fList != null) && (vList != null) && (fList.size() == vList.size())) {
			Cursor cursorAllRows = DatabaseAdapter.selectQuery(tableId, null, null);
			int newId = cursorAllRows.getCount()+1;
			ArrayList<Integer> fieldList = new ArrayList<Integer>();
			ArrayList<Object> valueList = new ArrayList<Object>();
			valueList.add(newId);
			int size = fList.size();
			for (int i=0; i<size; i++) {
				fieldList.add(Integer.valueOf((fList.get(i)).toString()));
				valueList.add(vList.get(i));
			}
			DatabaseAdapter.addQuery(Integer.valueOf(tableId), fieldList, valueList);
			mCurrentRecord = new HashMap<Object, Object>();
			valueList.remove(0);
			Cursor cursor = DatabaseAdapter.selectQuery(tableId, fieldList, valueList);
			cursor.moveToFirst();
			int cursorCount = cursor.getCount();
			for (int i=0; i<cursorCount; i++) {
				String[] columnNames = cursor.getColumnNames();
				int columnsRecordSize = columnNames.length;
				for (int j=0; j<columnsRecordSize; j++) {
					mCurrentRecord.put(columnNames[j], cursor.getString(j));
				}
				cursor.moveToNext();
			}
			DatabaseAdapter.closeCursor(cursor);
		}
	}
	
	public static void editRecord(String tableId, HashMap<Object, Object> record, ArrayList<Object> fList, ArrayList<Object> vList) {
		ArrayList<Integer> fieldList = new ArrayList<Integer>();
		ArrayList<Object> valueList = new ArrayList<Object>();
		if (fList.size() == vList.size()) {
			int size = fList.size();
			for (int i=0; i<size; i++) {
				fieldList.add(Integer.valueOf(fList.get(i).toString()));
				valueList.add(vList.get(i));
			}
			DatabaseAdapter.updateQuery(tableId, fieldList, valueList, record);
		}
		else {
			//exception
		}
	}
	
	public static void deleteRecord(String tableId, HashMap<Object, Object> record) {
		DatabaseAdapter.deleteQuery(tableId, record);
	}
	
	public HashMap<Object, Object> getRecord() {
		return mCurrentRecord;
	}
	
	public static int countRecord(String table, Object filter) {
		ArrayList<String> tables = new ArrayList<String>();
		tables.add(table);
		Cursor cursor = DatabaseAdapter.selectQuery(tables, null, filter, null, null);
		int result = cursor.getCount();
		DatabaseAdapter.closeCursor(cursor);
		return result;
	}
}
