package com.penbase.dma.Dalyo.Component.Custom;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.LinearLayout;

public class CustomLinearLayout extends LinearLayout {
	private Context context;	
	private ArrayList<String> dataList = new ArrayList<String>();
	private ArrayList<String> widthList = new ArrayList<String>();
	private boolean isHeader;
	
	public CustomLinearLayout(Context c, ArrayList<String> dl, ArrayList<String> wl, boolean ish) 
	{
		super(c);		
		this.context = c;
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.dataList = dl;
		this.isHeader = ish;
		this.widthList = wl;		
		
		if (isHeader)
		{
			for (int i=0; i<dataList.size(); i++)
			{
				Log.i("info", "dataList.get(i) "+dataList.get(i));
				CustomTextView ctv = new CustomTextView(context);
				ctv.setText(dataList.get(i));
				ctv.setTypeface(Typeface.DEFAULT_BOLD);
				int width = Integer.valueOf(widthList.get(i));				
				this.addView(ctv, new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT));
			}			
			setFocusable(false);
	        setFocusableInTouchMode(false);
	        setClickable(false);
	        setSelected(false);
	        setEnabled(false);
		}
		else
		{
			for (int i=0; i<dataList.size(); i++)
			{				
				CustomTextView ctv = new CustomTextView(context);
				ctv.setText(dataList.get(i));				
				ctv.setTextSize(DataView.getTextSize());				
				ctv.setTypeface(DataView.getTextType());
				int width = Integer.valueOf(widthList.get(i));				
				this.addView(ctv, new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT));
			}
			setFocusable(true);
	        setFocusableInTouchMode(true);
		}
	}	
	
	public ArrayList<String> getDataList()
	{
		return dataList;
	}
	
	public boolean hasHeader()
	{
		return isHeader;
	}
	
	public ArrayList<String> getWidthList()
	{
		return widthList;
	}
}
