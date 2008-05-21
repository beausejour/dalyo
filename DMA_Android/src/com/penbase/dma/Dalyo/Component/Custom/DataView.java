package com.penbase.dma.Dalyo.Component.Custom;

import java.util.ArrayList;

import com.penbase.dma.view.ApplicationView;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DataView extends ListView{	
	private Paint borderPaint;
	private static float fontSize;
	private static Typeface fontType;
	private DataViewAdapter adapter;
	private Context context;
	private ArrayList<String> pwidthList;
	private ArrayList<String> lwidthList;
	private ArrayList<ArrayList<String>> columnInfos = new ArrayList<ArrayList<String>>();
	private String tableId;
	
	public DataView(Context c, String tid) 
	{
		super(c);		
		borderPaint = new Paint();
		borderPaint.setARGB(255, 255, 255, 255);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		this.context = c;
		this.tableId = tid;
		adapter = new DataViewAdapter(context, tid);
		pwidthList = new ArrayList<String>();
		lwidthList = new ArrayList<String>();		
		
		this.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView arg0, View arg1, int arg2,
					long arg3) 
			{
				if (!((CustomLinearLayout) arg1).hasHeader())
				{
					arg1.setSelected(true);
				}
			}			
		});
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		RectF drawRect = new RectF();
	    drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
	    canvas.drawRoundRect(drawRect, 0, 0, borderPaint); 
	}
	
	public void setText(float fs, Typeface ft)
	{
		fontSize = fs;
		fontType = ft;
	}
	
	public static float getTextSize()
	{
		return fontSize;
	}
	
	public static Typeface getTextType()
	{
		return fontType;
	}
	
	public void setColumnInfo(ArrayList<ArrayList<String>> list)
	{
		if (list.size() > 0)
		{
			this.columnInfos = list;
			Log.i("info", "size "+list.size());
			int listSize = list.size();
			ArrayList<String> headerList = new ArrayList<String>();
			
			for (int i=0; i<listSize; i++)
			{
				headerList.add(list.get(i).get(2));
				pwidthList.add(list.get(i).get(3));
				lwidthList.add(list.get(i).get(4));
				Log.i("info", "header "+list.get(i).get(2)+" pwidth "+list.get(i).get(3)+" lwidth "+list.get(i).get(4));
			}
	        
			if (ApplicationView.getOrientation() == 0)
			{
				CustomLinearLayout header = new CustomLinearLayout(context, headerList, pwidthList, true);
		        adapter.addItem(header);				
			}
			else
			{
		        CustomLinearLayout header = new CustomLinearLayout(context, headerList, lwidthList, true);
		        adapter.addItem(header);
			}			
			
	        this.setAdapter(adapter);	        
		}
	}
	
	public ArrayList<String> getPWidthList()
	{
		return pwidthList;
	}
	
	public ArrayList<String> getLWidthList()
	{
		return lwidthList;
	}
	
	public void addData()
	{
		ArrayList<String> data4 = new ArrayList<String>();
        data4.add("data9");
        data4.add("data10");
        CustomLinearLayout layout4 = new CustomLinearLayout(context, data4, getPWidthList(), false);
        adapter.addItem(layout4);
        adapter.notifyDataSetChanged();
	}
	
	public void refresh()
	{
		if (tableId.equals(""))
		{
			new AlertDialog.Builder(context).setMessage("Check your dataview setting").setTitle("Error").show();
		}
		else
		{
			
		}
	}
}
