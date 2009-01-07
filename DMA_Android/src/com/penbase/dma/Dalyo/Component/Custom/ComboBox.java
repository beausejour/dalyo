package com.penbase.dma.Dalyo.Component.Custom;

import java.util.ArrayList;
import java.util.HashMap;
import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ComboBox extends Spinner {
	private ArrayAdapter<String> spinnerArrayAdapter;
	private String currentValue = null;
	private int currentPosition = -1;
	private ArrayList<String> labelList;
	private ArrayList<String> valueList;
	private ArrayList<String> itemsList;
	private Context context;
	private String formId;
	private HashMap<Integer, HashMap<Object, Object>> records = new HashMap<Integer, HashMap<Object, Object>>();
	private String funcName;
	
	public ComboBox(Context context, ArrayList<String> labelList, ArrayList<String> valueList){
		super(context);
		this.context = context;
		this.labelList = labelList;
		this.valueList = valueList;
	}
	
	public ComboBox(Context context, ArrayList<String> il){
		super(context);
		spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, il);
		this.setAdapter(spinnerArrayAdapter);	
	}
	
	public void setCurrentValue(String fid, HashMap<Object, Object> record){
		formId = fid;
		if ((formId != null) && (record != null)){
			ApplicationView.getLayoutsMap().get(formId).refresh(record);
		}
	}
	
	public void getData(Object filter){
		ArrayList<String> tables = new ArrayList<String>();
		tables.add(labelList.get(0));
		if (!tables.contains(valueList.get(0))){
			tables.add(valueList.get(0));
		}		
		itemsList = new ArrayList<String>();
		Cursor cursor = DatabaseAdapter.selectQuery(tables, null, filter);
		cursor.moveToFirst();
		int cursorCount = cursor.getCount();
		for (int i=0; i<cursorCount; i++){
			String[] columnNames = cursor.getColumnNames();
			HashMap<Object, Object> record = new HashMap<Object, Object>();
			int columnsSize = columnNames.length;
			for (int j=0; j<columnsSize; j++){
				if (columnNames[j].equals(DatabaseAttribute.FIELD+labelList.get(1))){
					itemsList.add(cursor.getString(j));
				}
				record.put(columnNames[j], cursor.getString(j));
			}
			records.put(i, record);
			cursor.moveToNext();
		}
		if (!cursor.isClosed()) {
			cursor.deactivate();
			cursor.close();
		}
		spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, itemsList);
		this.setAdapter(spinnerArrayAdapter);
	}
	
	public Object getCurrentValue(){
		return currentValue;
	}
	
	public void setOnChangeFunction(String name) {
		this.funcName = name;
		this.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Function.createFunction(funcName);
				currentValue = spinnerArrayAdapter.getItem(position).toString();
				currentPosition = position;
				Log.i("info", "currentposition "+currentPosition+" formId "+formId);
				if (formId != null){
					setCurrentValue(formId, getCurrentRecord());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});
	}
	
	public HashMap<Object, Object> getCurrentRecord(){
		if (currentPosition == -1){
			return null;
		}
		else{
			return records.get(currentPosition);
		}
	}
	
	public void removeAllItems() {
		spinnerArrayAdapter.clear();
	}
}
