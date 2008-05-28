package com.penbase.dma.Dalyo.Component.Custom;

import java.util.ArrayList;
import com.penbase.dma.Dalyo.Database;
import com.penbase.dma.View.ApplicationView;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ComboBox extends Spinner{
	private ArrayAdapter<String> spinnerArrayAdapter;
	private String currentItem;
	private ArrayList<String> labelList;
	private ArrayList<String> valueList;
	private boolean hasData = false;
	private ArrayList<String> itemsList;
	private ArrayList<String> keysList;
	private Context context;
	private ArrayList<String> itemInfos = new ArrayList<String>();		//tid, fid, value
	
	public ComboBox(Context context, ArrayList<String> labelList, ArrayList<String> valueList)
	{
		super(context);
		this.context = context;
		this.labelList = labelList;
		this.valueList = valueList;
		this.hasData = true;
		itemsList = new ArrayList<String>();
		keysList = new ArrayList<String>();
		getData();		
	}
	
	public ComboBox(Context context, ArrayList<String> il)
	{		
		super(context);
		spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, il);
		this.setAdapter(spinnerArrayAdapter);	
	}	
	
	public void setCurrentValue(final String formId)
	{
		Log.i("info", "setcurrentvalue");		
		itemInfos = valueList;
		this.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView parent, View v,
					int position, long id) 
			{
				currentItem = spinnerArrayAdapter.getItem(position).toString();
				if (formId != null)
				{
					if ((itemInfos.size() == 3) && (itemInfos.get(2) != null))
					{
						itemInfos.remove(2);
					}
					itemInfos.add(keysList.get(position));
					ApplicationView.getLayoutsMap().get(formId).refresh(itemInfos);
				}
			}

			@Override
			public void onNothingSelected(AdapterView arg0) {}			
		});	
	}
	
	public void getData()
	{
		Log.i("info", "combobox getdata");
		ArrayList<String> tables = new ArrayList<String>();
		tables.add(labelList.get(0));
		if (!tables.contains(valueList.get(0)))
		{
			tables.add(valueList.get(0));
		}
		
		ArrayList<ArrayList<String>> columns = new ArrayList<ArrayList<String>>();
		columns.add(labelList);
		columns.add(valueList);
		
		//<Label, Value>
		Cursor cursor = Database.selectQuery(tables, columns);		
		cursor.first();
    	for (int i=0; i<cursor.count(); i++)
    	{
    		itemsList.add(cursor.getString(0));
    		keysList.add(cursor.getString(1));
    		cursor.next();
    	}
		spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, itemsList);
		this.setAdapter(spinnerArrayAdapter);
	}
}
