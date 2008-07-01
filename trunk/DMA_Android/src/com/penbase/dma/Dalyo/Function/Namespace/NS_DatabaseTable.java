package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.database.Cursor;
import android.util.Log;

import com.penbase.dma.Constant.DatabaseField;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Database.Record;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_DatabaseTable {
	public static HashMap<Object, Object> CreateNewRecord(NodeList params){
		String tableId = String.valueOf(getValue(params, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		ArrayList<Object> fieldsList = (ArrayList<Object>) getValue(params, ScriptAttribute.PARAMETER_NAME_FIELDS, ScriptAttribute.LIST);
		ArrayList<Object> valuesList = (ArrayList<Object>) getValue(params, ScriptAttribute.PARAMETER_NAME_VALUES, ScriptAttribute.LIST);
		Record record = new Record(tableId, fieldsList, valuesList);
		return record.getRecord();
	}
	
	public static void DeleteRecord(NodeList params){
		String tableId = String.valueOf(getValue(params, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		HashMap<Object, Object> record = (HashMap<Object, Object>) getValue(params, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		Record.deleteRecord(tableId, record);
	}
	
	public static void EditRecord(NodeList params){
		String tableId = String.valueOf(getValue(params, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		HashMap<Object, Object> record = (HashMap<Object, Object>)getValue(params, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		ArrayList<Object> fieldList = (ArrayList<Object>) getValue(params, ScriptAttribute.PARAMETER_NAME_FIELDS, ScriptAttribute.LIST);
		ArrayList<Object> valueList = (ArrayList<Object>) getValue(params, ScriptAttribute.PARAMETER_NAME_VALUES, ScriptAttribute.LIST);
		Record.editRecord(tableId, record, fieldList, valueList);
	}
	
	public static void Clear(NodeList params){
		String tableId = String.valueOf(getValue(params, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		Object filter = getValue(params, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		DatabaseAdapter.clearTable(tableId);
	}
	
	public static Object GetFieldValue(NodeList params){
		Object value = null;
		String tableId = String.valueOf(getValue(params, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		HashMap<Object, Object> record = (HashMap<Object, Object>)getValue(params, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		String fieldId = String.valueOf(getValue(params, ScriptAttribute.FIELD, ScriptAttribute.FIELD));
		if (record != null){
			value = record.get(DatabaseField.FIELD+fieldId);
		}
		Log.i("info", "value "+value);
		return value;
	}
	
	public static ArrayList<HashMap<Object, Object>> GetFilteredRecords(NodeList params){
		String tableId = String.valueOf(getValue(params, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		Object filter = getValue(params, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		return getRecords(tableId, filter);
	}
	
	public static ArrayList<HashMap<Object, Object>> GetRecords(NodeList params){
		/*
		 *     									<p n="table" t="table"><elt id="0" t="table"/></p>
    									<p n="field" t="field"><elt id="0" t="field"/></p>
    									<p n="value" t="object">
    										<c f="getFieldValue" ns="database.table">
    											<p n="table" t="table"><elt id="1" t="table"/></p>
    											<p n="record" t="record"><v n="CurrentIntervention"/></p>
    											<p n="field" t="field"><elt id="10" t="field"/></p>
    										</c>
    									</p>*/
		String tableId = String.valueOf(getValue(params, ScriptAttribute.TABLE, ScriptAttribute.TABLE));
		String fieldId = String.valueOf(getValue(params, ScriptAttribute.FIELD, ScriptAttribute.FIELD));
		Object value = getValue(params, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		Log.i("info", "tableid "+tableId+" fieldid "+fieldId+" value "+value);
		ArrayList<Integer> fieldList = new ArrayList<Integer>();
		fieldList.add(Integer.valueOf(fieldId));
		ArrayList<Object> valueList = new ArrayList<Object>();
		valueList.add(value);
		ArrayList<HashMap<Object, Object>> records = new ArrayList<HashMap<Object, Object>>();
		Cursor cursor = DatabaseAdapter.selectQuery(tableId, fieldList, valueList);
		cursor.first();
		int cursorLen = cursor.count();
		String[] columnNames = cursor.getColumnNames();
		int columnsNb = columnNames.length;
		for (int i=0; i<cursorLen; i++){
			HashMap<Object, Object> record = new HashMap<Object, Object>();
			for (int j=0; j<columnsNb; j++){
				record.put(columnNames[j], DatabaseAdapter.getCursorValue(cursor, columnNames[j]));
			}
			records.add(record);
			cursor.next();
		}
		Log.i("info", "records "+records);
		return records;
	}
	
	private static ArrayList<HashMap<Object, Object>> getRecords(String tableId, Object filter){
		ArrayList<HashMap<Object, Object>> records = new ArrayList<HashMap<Object, Object>>();
		ArrayList<String> tables = new ArrayList<String>();
		tables.add(tableId);
		Cursor cursor = DatabaseAdapter.selectQuery(tables, null, filter);
		cursor.first();
		for (int i=0; i<cursor.count(); i++){
			HashMap<Object, Object> record = new HashMap<Object, Object>();
			String[] columnNames = cursor.getColumnNames();
			int columnsRecordSize = columnNames.length;
			for (int j=0; j<columnsRecordSize; j++){
				record.put(columnNames[j], DatabaseAdapter.getCursorValue(cursor, columnNames[j]));
			}
			records.add(record);
			cursor.next();
		}
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