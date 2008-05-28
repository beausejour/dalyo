package com.penbase.dma.Dalyo.Component.Custom;

import java.util.ArrayList;

import com.penbase.dma.Dalyo.Database;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.widget.EditText;

public class TextZone extends EditText{
	private String tableId;
	private String fieldId;

	public TextZone(Context context, Typeface tf, float fs) 
	{
		super(context);			
		this.setTypeface(tf);
		this.setTextSize(fs);
	}
	
	public void setTableId(String tid)
	{
		this.tableId = tid;
	}
	
	public void setFieldId(String fid)
	{
		this.fieldId = fid;
	}
	
	public String getTableId()
	{
		return tableId;
	}
	
	public String getFieldId()
	{
		return fieldId;
	}
	
	public void refresh(ArrayList<String> itemInfo)
	{
		ArrayList<String> tables = new ArrayList<String>();		
		if (itemInfo.get(0).equals(getTableId()))
		{
			tables.add(itemInfo.get(0));
		}
		else
		{
			tables.add(itemInfo.get(0));
			tables.add(getTableId());
		}
		
		ArrayList<ArrayList<String>> columns = new ArrayList<ArrayList<String>>();
		ArrayList<String> column = new ArrayList<String>();
		column.add(getTableId());
		column.add(getFieldId());
		columns.add(column);
				
		Cursor cursor = Database.selectQuery(tables, columns, itemInfo);
		cursor.first();
    	for (int i=0; i<cursor.count(); i++)
    	{
    		TextZone.this.setText(cursor.getString(0));
    		cursor.next();
    	} 
	}
}
