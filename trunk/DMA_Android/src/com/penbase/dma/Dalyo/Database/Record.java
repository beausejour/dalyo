package com.penbase.dma.Dalyo.Database;

import java.util.ArrayList;
import java.util.HashMap;
import android.database.Cursor;
import android.util.Log;

public class Record {
	private HashMap<Object, Object> currentRecord = null;
	public Record(String tableId, ArrayList<Object> fList, ArrayList<Object> vList){
		if ((fList != null) && (vList != null) && (fList.size() > 2) && (fList.size() == vList.size())){
			Cursor cursorAllRows = DatabaseAdapter.selectQuery(tableId, null, null);
			int newId = cursorAllRows.count()+1;
			ArrayList<Integer> fieldList = new ArrayList<Integer>();
			ArrayList<Object> valueList = new ArrayList<Object>();
			valueList.add(newId);
			int size = fList.size();
			for (int i=2; i<size; i++){
				fieldList.add(Integer.valueOf(String.valueOf(fList.get(i))));
				valueList.add(vList.get(i));
			}
			DatabaseAdapter.addQuery(Integer.valueOf(tableId), fieldList, valueList);
			Log.i("info", "record has added in db");
			currentRecord = new HashMap<Object, Object>();
			valueList.remove(0);
			Cursor cursor = DatabaseAdapter.selectQuery(tableId, fieldList, valueList);
			cursor.first();
			for (int i=0; i<cursor.count(); i++){
				String[] columnNames = cursor.getColumnNames();
				int columnsRecordSize = columnNames.length;
				for (int j=0; j<columnsRecordSize; j++){
					currentRecord.put(columnNames[j], cursor.getString(j));
				}
				cursor.next();
			}
			Log.i("info", "currentRecord in constructor "+currentRecord);
		}
	}
	
	public Record(String tableId){}
	
	public HashMap<Object, Object> getRecord(){
		Log.i("info", "getrecord "+currentRecord);
		return currentRecord;
	}
	
	public static void editRecord(String tableId, HashMap<Object, Object> record, ArrayList<Object> fList, ArrayList<Object> vList){
		ArrayList<Integer> fieldList = new ArrayList<Integer>();
		ArrayList<Object> valueList = new ArrayList<Object>();
		if (fList.size() == vList.size()){
			int size = fList.size();
			for (int i=2; i<size; i++){
				fieldList.add(Integer.valueOf(String.valueOf(fList.get(i))));
				valueList.add(vList.get(i));
			}
			DatabaseAdapter.updateQuery(tableId, fieldList, valueList, record);
		}
		else{
			//exception
		}
	}
	
	public static void deleteRecord(String tableId, HashMap<Object, Object> record){
		DatabaseAdapter.deleteQuery(tableId, record);
	}
}
