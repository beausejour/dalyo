package com.penbase.dma.Dalyo.Component.Custom;

import java.util.ArrayList;
import java.util.HashMap;

import com.penbase.dma.Dalyo.Database;
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
	private HashMap<Integer, HashMap<String, Object>> records = new HashMap<Integer, HashMap<String, Object>>();
	
	public ComboBox(Context context, ArrayList<String> labelList, ArrayList<String> valueList)
	{
		super(context);
		this.context = context;
		this.labelList = labelList;
		this.valueList = valueList;
		this.setOnItemSelectedListener(this);
	}
	
	public ComboBox(Context context, ArrayList<String> il)
	{
		super(context);
		spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, il);
		this.setAdapter(spinnerArrayAdapter);	
	}	
	
	public void setCurrentValue(String formId)
	{
		this.formId = formId;
		Log.i("info", "setcurrentvalue "+formId+" records size "+records.size());		
		if ((formId != null) && (records.size() > 0))
		{
			ApplicationView.getLayoutsMap().get(formId).refresh(records.get(currentPosition));
		}
	}
	
	public void getData(Object filter)
	{
		Log.i("info", "combobox getdata");
		ArrayList<String> tables = new ArrayList<String>();
		tables.add(labelList.get(0));
		if (!tables.contains(valueList.get(0)))
		{
			tables.add(valueList.get(0));
		}
		
		Log.i("info", "call selectQuery");
		itemsList = new ArrayList<String>();
		Cursor cursor = Database.selectQuery(tables, null, filter);
		cursor.first();
    	for (int i=0; i<cursor.count(); i++)
    	{
    		String[] columnNames = cursor.getColumnNames();
    		HashMap<String, Object> record = new HashMap<String, Object>();
    		int mapSize = columnNames.length;
    		for (int j=0; j<mapSize; j++)
    		{
    			if (columnNames[j].equals(Database.FIELD+labelList.get(1)))
    			{
    				itemsList.add(cursor.getString(j));
    			}
    			record.put(columnNames[j], cursor.getString(j));
    		}
    		records.put(i, record);    		
    		cursor.next();
    	}
		spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, itemsList);
		this.setAdapter(spinnerArrayAdapter);
	}

	@Override
	public void onItemSelected(AdapterView parent, View v, int position, long id) 
	{
		currentValue = spinnerArrayAdapter.getItem(position).toString();		
		currentPosition = position;		
		if (formId != null)
		{
			setCurrentValue(formId);
		}
	}

	@Override
	public void onNothingSelected(AdapterView arg0) {}
	
	public Object getCurrentValue()
	{
		return currentValue;
	}
	
	public HashMap<String, Object> getCurrentRecord()
	{
		if (currentPosition == -1)
		{
			return null;
		}
		else
		{
			return records.get(currentPosition);
		}		
	}
}
