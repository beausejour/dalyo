package com.penbase.dma.Dalyo.Component.Custom.Dataview;

import java.util.ArrayList;
import java.util.HashMap;
import com.penbase.dma.Dma;
import com.penbase.dma.Constant.DatabaseField;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;
import android.content.Context;
import android.content.res.Configuration;
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
	private HashMap<Integer, String> onCalculateMap = new HashMap<Integer, String>();
	
	public DataView(Context c, String tid){
		super(c);
		borderPaint = new Paint();
		borderPaint.setARGB(255, 0, 0, 0);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		this.context = c;
		this.tableId = tid;
		adapter = new DataViewAdapter();
		pwidthList = new ArrayList<String>();
		lwidthList = new ArrayList<String>();
		this.setItemsCanFocus(true);
		this.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				currentPosition = position;
				if (!((CustomLinearLayout) v).hasHeader()){
					v.setSelected(true);
				}
			}

		});
		//this.setAdapter(adapter);
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
			
			if (ApplicationView.getOrientation() == Configuration.ORIENTATION_PORTRAIT){
				CustomLinearLayout header = new CustomLinearLayout(context, headerList, pwidthList, true);
				this.addHeaderView(header, null, false);
			}
			else{
				CustomLinearLayout header = new CustomLinearLayout(context, headerList, lwidthList, true);
				adapter.addItem(header);
			}
			this.setAdapter(adapter);
		}
	}
	
	public void setOncalculate(HashMap<Integer, String> onc){
		this.onCalculateMap = onc;
	}
	
	private ArrayList<String> getPWidthList(){
		return pwidthList;
	}
	
	private ArrayList<String> getLWidthList(){
		return lwidthList;
	}	
	
	public void refresh(Object filter){
		if (tableId == null){
			ApplicationView.errorDialog("Check your dataview setting");
		}
		else{
			if (adapter.getItems().size() > 1){
				adapter.removeItems();
				adapter = new DataViewAdapter();
				this.setAdapter(adapter);
			}
			int columnNb = columns.size();
			ArrayList<String> tables = new ArrayList<String>();
			tables.add(tableId);
			for (int i=0; i<columnNb; i++){
				ArrayList<String> column = columns.get(i);
				if ((!tables.contains(column.get(0))) && ((!column.get(0).equals("")) && (!column.get(1).equals("")))){
					tables.add(column.get(0));
				}
			}
			Cursor cursor = DatabaseAdapter.selectQuery(tables, null, filter);
			records = new HashMap<Integer, HashMap<Object, Object>>();
			int count = cursor.getCount();
			if (count > 0){
				cursor.moveToFirst();
				for (int i=0; i<cursor.getCount(); i++){
					String[] columnNames = cursor.getColumnNames();
					ArrayList<String> data = new ArrayList<String>();
					HashMap<Object, Object> record = new HashMap<Object, Object>();
					int columnsRecordSize = columnNames.length;
					int columnsSize = columns.size();
					int calculateColumn = -1;
					for (int k=0; k<columnsSize; k++){
						if (onCalculateMap.containsKey(k)){
							calculateColumn = k;
							data.add("");
						}
						else{
							for (int j=0; j<columnsRecordSize; j++){
								String field = DatabaseField.FIELD+columns.get(k).get(1);
								if (columnNames[j].equals(field)){
									data.add(String.valueOf(DatabaseAdapter.getCursorValue(cursor, field)));
								}
								record.put(columnNames[j], cursor.getString(j));
							}	
						}
					}
					if (calculateColumn != -1){
						String value = String.valueOf(Function.createCalculateFunction(onCalculateMap.get(calculateColumn), record));
						data.remove(calculateColumn);
						data.add(calculateColumn, value);
					}
					CustomLinearLayout layout = new CustomLinearLayout(context, data, getPWidthList(), false);
					adapter.addItem(layout);
					records.put(i, record);
					cursor.moveToNext();
				}
				adapter.notifyDataSetChanged();
			}
			if (!cursor.isClosed()) {
				cursor.deactivate();
				cursor.close();
			}
		}
		Log.i("info", "items nb "+adapter.getCount());
	}
	
	public HashMap<Object, Object> getCurrentRecord(){
		if (currentPosition < 1){
			return null;
		}
		else{
			int position = currentPosition;
			return records.get(position-1);
		}
	}
	
	public void setCurrentPosition(int position){
		currentPosition = position;
	}
}
