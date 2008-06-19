package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.database.Cursor;
import android.util.Log;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Database.Record;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_DatabaseTable {
	public static HashMap<Object, Object> CreateNewRecord(NodeList params){
		String tableId = String.valueOf(getValue(params, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		Log.i("info", "tableId "+tableId);
		ArrayList<Object> fieldsList = (ArrayList<Object>) getValue(params, ScriptAttribute.PARAMETER_NAME_FIELDS, ScriptAttribute.LIST);
		Log.i("info", "fieldsList "+fieldsList);
		ArrayList<Object> valuesList = (ArrayList<Object>) getValue(params, ScriptAttribute.PARAMETER_NAME_VALUES, ScriptAttribute.LIST);
		Log.i("info", "valuesList "+valuesList);
		Record record = new Record(tableId, fieldsList, valuesList);
		Log.i("info", "record "+record);
		return record.getRecord();
	}
	
	public static void DeleteRecord(NodeList params){
		String tableId = String.valueOf(getValue(params, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		Log.i("info", "tableId "+tableId);
		HashMap<Object, Object> record = (HashMap<Object, Object>) getValue(params, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		Log.i("info", "record "+record);
		Record.deleteRecord(tableId, record);
	}
	
	public static void EditRecord(NodeList params){
		String tableId = String.valueOf(getValue(params, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		Log.i("info", "tableId "+tableId);
		HashMap<Object, Object> record = (HashMap<Object, Object>)getValue(params, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		Log.i("info", "record "+record);
		ArrayList<Object> fieldList = (ArrayList<Object>) getValue(params, ScriptAttribute.PARAMETER_NAME_FIELDS, ScriptAttribute.LIST);
		Log.i("info", "fieldList "+fieldList);
		ArrayList<Object> valueList = (ArrayList<Object>) getValue(params, ScriptAttribute.PARAMETER_NAME_VALUES, ScriptAttribute.LIST);
		Log.i("info", "valuesList "+valueList);
		Record.editRecord(tableId, record, fieldList, valueList);
	}
	
	public static void Clear(NodeList params){
		String tableId = String.valueOf(getValue(params, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		Log.i("info", "tableId "+tableId);
		Object filter = getValue(params, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		DatabaseAdapter.clearTable(tableId);
	}
	
	public static Object GetFieldValue(NodeList params){
		Object value = null;
		String tableId = String.valueOf(getValue(params, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		Log.i("info", "tableId "+tableId);
		HashMap<Object, Object> record = (HashMap<Object, Object>)getValue(params, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		Log.i("info", "record "+record);
		String fieldId = String.valueOf(getValue(params, ScriptAttribute.FIELD, ScriptAttribute.FIELD));
		Log.i("info", "fid "+fieldId);
		if (record != null){
			Log.i("info", "record in getvalue "+record);
			value = record.get(DatabaseAdapter.FIELD+fieldId);
		}
		return value;
	}
	
	public static ArrayList<HashMap<Object, Object>> GetFilteredRecords(NodeList params){
		String tableId = String.valueOf(getValue(params, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		Object filter = getValue(params, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		return getRecords(tableId, filter);
	}
		
	private static ArrayList<HashMap<Object, Object>> getRecords(String tableId, Object filter){
		ArrayList<HashMap<Object, Object>> records = new ArrayList<HashMap<Object, Object>>();
		ArrayList<String> tables = new ArrayList<String>();
		tables.add(tableId);
		Log.i("info", "getrecords in getfilteredrecords "+filter);
		Cursor cursor = DatabaseAdapter.selectQuery(tables, null, filter);
		cursor.first();
		Log.i("info", "cursor count "+cursor.count());
		for (int i=0; i<cursor.count(); i++){
			HashMap<Object, Object> record = new HashMap<Object, Object>();
			String[] columnNames = cursor.getColumnNames();
			int columnsRecordSize = columnNames.length;
			for (int j=0; j<columnsRecordSize; j++){
				record.put(columnNames[j], cursor.getString(j));
				Log.i("info", "each record "+columnNames[j]+" "+cursor.getString(j));
			}
			records.add(record);
		}
		cursor.next();
		return records; 
	}
	
	private static Object getValue(NodeList params, String name, String type){
		Object value = null;
		int paramsLen = params.getLength();
		for (int i=0; i<paramsLen; i++){
			Element element = (Element) params.item(i);
			if (element.getNodeName().equals(ScriptTag.PARAMETER)){
				if ((element.getAttribute(ScriptTag.NAME).equals(name)) &&
						(element.getAttribute(ScriptTag.TYPE).equals(type))){
					if (element.getChildNodes().getLength() == 1){
						Element child = (Element)element.getChildNodes().item(0);
						if (child.getNodeName().equals(ScriptTag.ELEMENT)){
							value = child.getAttribute(ScriptTag.ELEMENT_ID);
						}
						else if (child.getNodeName().equals(ScriptTag.VAR)){
							if ((name.equals(ScriptAttribute.RECORD)) && type.equals(ScriptAttribute.RECORD) &&
									(Function.getVariablesMap().get(child.getAttribute(ScriptTag.NAME)).size() > 1)) {
								value = Function.getVariablesMap().get(child.getAttribute(ScriptTag.NAME)).get(1);
							}
							else{
								value = Function.getVariablesMap().get(child.getAttribute(ScriptTag.NAME));
							}
						}
						if (child.getNodeName().equals(ScriptTag.CALL)){
							value = Function.returnTypeFunction(child);
						}
					}
				}
			}
		}
		return value;
	}
}
