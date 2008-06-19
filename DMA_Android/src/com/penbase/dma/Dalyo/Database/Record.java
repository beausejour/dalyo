package com.penbase.dma.Dalyo.Database;

import java.util.ArrayList;
import java.util.HashMap;
import android.database.Cursor;
import android.util.Log;

public class Record {
	private HashMap<Object, Object> currentRecord = null;
	public Record(String tableId, ArrayList<Object> fList, ArrayList<Object> vList){
		if ((fList.size() > 2) && (fList.size() == vList.size())){
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
			Log.i("info", "add value in DB fieldlist "+fieldList+" valuelist "+valueList);
			DatabaseAdapter.addQuery(Integer.valueOf(tableId), fieldList, valueList);
			currentRecord = new HashMap<Object, Object>();
			valueList.remove(0);
			Cursor cursor = DatabaseAdapter.selectQuery(tableId, fieldList, valueList);
			Log.i("info", "there should have only one record "+cursor.count());
			cursor.first();
			for (int i=0; i<cursor.count(); i++){
				String[] columnNames = cursor.getColumnNames();
				int columnsRecordSize = columnNames.length;
				for (int j=0; j<columnsRecordSize; j++){
					currentRecord.put(columnNames[j], cursor.getString(j));
				}
			}
			cursor.next();
		}
	}
	
	public Record(String tableId){}
	
	public HashMap<Object, Object> getRecord(){
		return currentRecord;
	}
	
	public static void editRecord(String tableId, HashMap<Object, Object> record, ArrayList<Object> fList, ArrayList<Object> vList){
		Log.i("info", "edit record");
		ArrayList<Integer> fieldList = new ArrayList<Integer>();
		ArrayList<Object> valueList = new ArrayList<Object>();
		int size = fList.size();
		for (int i=2; i<size; i++){
			fieldList.add(Integer.valueOf(String.valueOf(fList.get(i))));
			valueList.add(vList.get(i));
		}
		Log.i("info", "tableid "+tableId+" record initial "+record+" fieldList "+fieldList+" valuelist "+valueList);
		DatabaseAdapter.updateQuery(tableId, fieldList, valueList, record);
	}
	
	public static void deleteRecord(String tableId, HashMap<Object, Object> record){
		Log.i("info", "delete record");
		DatabaseAdapter.deleteQuery(tableId, record);
	}
}
