package com.penbase.dma.Dalyo.Component.Custom.Dataview;

import java.util.ArrayList;
import java.util.HashMap;
import com.penbase.dma.Dma;
import com.penbase.dma.Constant.DatabaseField;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
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
	private int currentPosition = -1;
	private HashMap<Integer, HashMap<Object, Object>> records;
	private boolean adapterChanged = false;
	
	public DataView(Context c, String tid){
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
		this.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView parent, View v, int position, long id){
				Log.i("info", "item position in dataview "+position);
				currentPosition = position;
				if (!((CustomLinearLayout) v).hasHeader()){
					v.setSelected(true);
				}
			}
		});
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		RectF drawRect = new RectF();
	    drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
	    canvas.drawRoundRect(drawRect, 0, 0, borderPaint);
	}
	
	public void setText(float fs, Typeface ft){
		fontSize = fs;
		fontType = ft;
	}
	
	public static float getTextSize(){
		return fontSize;
	}
	
	public static Typeface getTextType(){
		return fontType;
	}
	
	public void setColumnInfo(ArrayList<ArrayList<String>> list){
		if (list.size() > 0){
			int listSize = list.size();
			ArrayList<String> headerList = new ArrayList<String>();
			for (int i=0; i<listSize; i++){
				ArrayList<String> column = new ArrayList<String>();
				column.add(list.get(i).get(0));		//tid
				column.add(list.get(i).get(1));		//fid
				headerList.add(list.get(i).get(2));
				pwidthList.add(list.get(i).get(3));
				lwidthList.add(list.get(i).get(4));
				columns.add(column);
			}
			
			if (ApplicationView.getOrientation() == 0){
				CustomLinearLayout header = new CustomLinearLayout(context, headerList, pwidthList, true);
				adapter.addItem(header);
			}
			else{
				CustomLinearLayout header = new CustomLinearLayout(context, headerList, lwidthList, true);
				adapter.addItem(header);
			}
			this.setAdapter(adapter);
		}
	}
	
	private ArrayList<String> getPWidthList(){
		return pwidthList;
	}
	
	private ArrayList<String> getLWidthList(){
		return lwidthList;
	}	
	
	public void refresh(Object filter){
		if (tableId == null){
			new Dma().errorDialog("Check your dataview setting");
		}
		else{
			CustomLinearLayout header = (CustomLinearLayout) adapter.getItem(0);
			if (adapter.getItems().size() > 1){
				adapter.removeItems();
				adapter = new DataViewAdapter(context);
				adapter.addItem(header);
				this.setAdapter(adapter);
				adapterChanged = true;
			}
			int columnNb = columns.size();
			ArrayList<String> tables = new ArrayList<String>();
			tables.add(tableId);
			for (int i=0; i<columnNb; i++){
				ArrayList<String> column = columns.get(i);
				
				if (!tables.contains(column.get(0))){
					tables.add(column.get(0));
				}
			}
			Cursor cursor = DatabaseAdapter.selectQuery(tables, null, filter);
			records = new HashMap<Integer, HashMap<Object, Object>>();
			int count = cursor.count();
			if (count > 0){
				cursor.first();
				for (int i=0; i<cursor.count(); i++){
					String[] columnNames = cursor.getColumnNames();
					ArrayList<String> data = new ArrayList<String>();
					HashMap<Object, Object> record = new HashMap<Object, Object>();
					int columnsRecordSize = columnNames.length;
					int columnsSize = columns.size();
					for (int k=0; k<columnsSize; k++){
						for (int j=0; j<columnsRecordSize; j++){
							if (columnNames[j].equals(DatabaseField.FIELD+columns.get(k).get(1))){
								Log.i("info", "add data in customlinearlayout "+cursor.getString(j));
								data.add(cursor.getString(j));
							}
							record.put(columnNames[j], cursor.getString(j));
						}
					}
					CustomLinearLayout layout = new CustomLinearLayout(context, data, getPWidthList(), false);
					adapter.addItem(layout);
					adapter.notifyDataSetChanged();
					records.put(i, record);
					cursor.next();
				}
			}
		}
	}
	
	public HashMap<Object, Object> getCurrentRecord(){
		Log.i("info", "currentposition "+currentPosition+" adapterChanged "+adapterChanged);
		if (currentPosition < 1){
			return null;
		}
		else{
			int position = currentPosition;
			return records.get(position-1);	
		}
	}
}
