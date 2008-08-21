package com.penbase.dma.Dalyo.Component.Custom;

import java.util.ArrayList;
import java.util.HashMap;
import com.penbase.dma.Constant.DatabaseField;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.View.ApplicationView;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class ComboBox extends Spinner implements OnItemSelectedListener{
	private ArrayAdapter<String> spinnerArrayAdapter;
	private String currentValue = null;
	private int currentPosition = -1;
	private ArrayList<String> labelList;
	private ArrayList<String> valueList;
	private ArrayList<String> itemsList;
	private Context context;
	private String formId;
	private HashMap<Integer, HashMap<Object, Object>> records = new HashMap<Integer, HashMap<Object, Object>>();
	
	public ComboBox(Context context, ArrayList<String> labelList, ArrayList<String> valueList){
		super(context);
		this.context = context;
		this.labelList = labelList;
		this.valueList = valueList;
		this.setOnItemSelectedListener(this);
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
		for (int i=0; i<cursor.getCount(); i++){
			String[] columnNames = cursor.getColumnNames();
			HashMap<Object, Object> record = new HashMap<Object, Object>();
			int columnsSize = columnNames.length;
			for (int j=0; j<columnsSize; j++){
				if (columnNames[j].equals(DatabaseField.FIELD+labelList.get(1))){
					itemsList.add(cursor.getString(j));
				}
				record.put(columnNames[j], cursor.getString(j));
			}
			records.put(i, record);
			cursor.moveToNext();
		}
		spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, itemsList);
		this.setAdapter(spinnerArrayAdapter);
	}

	@Override
	public void onItemSelected(AdapterView parent, View v, int position, long id){
		currentValue = spinnerArrayAdapter.getItem(position).toString();
		currentPosition = position;
		Log.i("info", "currentposition "+currentPosition+" formId "+formId);
		if (formId != null){
			setCurrentValue(formId, getCurrentRecord());
		}
	}

	@Override
	public void onNothingSelected(AdapterView arg0) {}
	
	public Object getCurrentValue(){
		return currentValue;
	}
	
	public HashMap<Object, Object> getCurrentRecord(){
		if (currentPosition == -1){
			return null;
		}
		else{
			return records.get(currentPosition);
		}
	}
}
