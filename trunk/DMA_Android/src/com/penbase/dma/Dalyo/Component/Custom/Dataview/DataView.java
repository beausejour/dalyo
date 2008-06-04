package com.penbase.dma.Dalyo.Component.Custom.Dataview;

import java.util.ArrayList;
import com.penbase.dma.Dma;
import com.penbase.dma.Dalyo.Database;
import com.penbase.dma.View.ApplicationView;
import android.content.Context;
import android.database.Cursor;
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
	private ArrayList<ArrayList<String>> columns = new ArrayList<ArrayList<String>>();
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
		adapter = new DataViewAdapter(context);
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
			Log.i("info", "size "+list.size());
			int listSize = list.size();
			ArrayList<String> headerList = new ArrayList<String>();			
			for (int i=0; i<listSize; i++)
			{
				ArrayList<String> column = new ArrayList<String>();
				column.add(list.get(i).get(0));		//tid
				column.add(list.get(i).get(1));		//fid
				headerList.add(list.get(i).get(2));
				pwidthList.add(list.get(i).get(3));
				lwidthList.add(list.get(i).get(4));
				Log.i("info", "header "+list.get(i).get(2)+" pwidth "+list.get(i).get(3)+" lwidth "+list.get(i).get(4));
				columns.add(column);
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
	
	public void refresh(Object filter)
	{
		Log.i("info", "refresh in dataview "+filter);
		if (tableId == null)
		{
			new Dma().errorDialog("Check your dataview setting");
		}
		else
		{			
			int columnNb = columns.size();
			ArrayList<String> tables = new ArrayList<String>();
			tables.add(tableId);
			for (int i=0; i<columnNb; i++)
			{
				ArrayList<String> column = columns.get(i);
				
				if (!tables.contains(column.get(0)))
				{
					tables.add(column.get(0));
				}
			}
			Cursor cursor = Database.selectQuery(tables, columns, filter);
			Log.i("info", "cursor length "+cursor.count()+" columns size "+columns.size());
			cursor.first();
	    	for (int i=0; i<cursor.count(); i++)
	    	{
	    		ArrayList<String> data = new ArrayList<String>();
	    		for (int j=0; j<columns.size(); j++)
	    		{	    			
	    			data.add(cursor.getString(j));
	    			Log.i("info", "value in row "+i+" column "+j+" "+cursor.getString(j));
	    		}
	    		CustomLinearLayout layout = new CustomLinearLayout(context, data, getPWidthList(), false);
	    		Log.i("info", "add layout in dataview");
	            adapter.addItem(layout);
	            adapter.notifyDataSetChanged();
	    		cursor.next();
	    	}
		}
	}
}
