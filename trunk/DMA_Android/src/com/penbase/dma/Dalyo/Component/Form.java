package com.penbase.dma.Dalyo.Component;

import java.util.HashMap;
import android.content.Context;
import android.util.Log;
import android.widget.AbsoluteLayout;
import com.penbase.dma.Dalyo.Component.Custom.TextField;
import com.penbase.dma.Dalyo.Component.Custom.TextZone;
import com.penbase.dma.Dalyo.Function.Function;

public class Form extends AbsoluteLayout{
	private Function function;
	private String tableId;
	private static String currentItem;
	
	public Form(Context context) 
	{		
		super(context);
		this.setLayoutParams(new AbsoluteLayout.LayoutParams(LayoutParams.FILL_PARENT, 
				LayoutParams.FILL_PARENT, 0, 0));
		function = new Function(context);
	}
	
	public void onLoad(String name)
	{
		function.createFunction(name, null);
	}
	
	public void setTableId(String tableId)
	{
		this.tableId = tableId;
	}
	
	public String getTableId()
	{
		return tableId;
	}
	
	public void setCurrentRecord(String item)
	{
		currentItem = item;
	}
	
	public static String getCurrentRecord()
	{
		return currentItem;
	}
	
	//public void refresh(ArrayList<String> itemInfos)
	public void refresh(HashMap<String, Object> record)
	{
		int viewLen = this.getChildCount();
		Log.i("info", "viewlen "+viewLen);
		
		for (int i=0; i<viewLen; i++)
		{
			if (this.getChildAt(i) instanceof TextField)
			{
				((TextField)this.getChildAt(i)).refresh(record);
			}
			else if (this.getChildAt(i) instanceof TextZone)
			{
				((TextZone)this.getChildAt(i)).refresh(record);
			}
		}
	}
}
