package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Displays configured items or retrieved data from databases
 */
public class ComboBox extends Spinner {
	private ArrayAdapter<String> mSpinnerArrayAdapter;
	private int mCurrentPosition = -1;
	private ArrayList<String> mLabelList;
	private ArrayList<String> mValueList;
	private ArrayList<String> mItemsList;
	private Context mContext;
	private String mFormId;
	private HashMap<Integer, HashMap<Object, Object>> mRecords = new HashMap<Integer, HashMap<Object, Object>>();
	private String mFuncName;
	
	public ComboBox(Context context, ArrayList<String> labelList, ArrayList<String> valueList) {
		super(context);
		this.mContext = context;
		this.mLabelList = labelList;
		this.mValueList = valueList;
	}
	
	public ComboBox(Context context, ArrayList<String> il) {
		super(context);
		this.mLabelList = new ArrayList<String>();
		this.mValueList = new ArrayList<String>();
		this.mItemsList = il;
		for (String s : il) {
			mLabelList.add(s);
			mValueList.add(s);
		}
		mSpinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, il);
		this.setAdapter(mSpinnerArrayAdapter);	
	}
	
	public void setCurrentValue(String fid, HashMap<Object, Object> record) {
		mFormId = fid;
		if ((mFormId != null) && (record != null)) {
			ApplicationView.getLayoutsMap().get(mFormId).refresh(record);
		}
	}
	
	/**
	 * Sends a sql query with new parameters
	 * @param filter column filter condition
	 * @param order order by a column
	 * @param distinct if distinct same value
	 */
	public void refresh(Object filter, Object order, Object distinct) {
		ArrayList<String> tables = new ArrayList<String>();
		tables.add(mLabelList.get(0));
		if (!tables.contains(mValueList.get(0))) {
			tables.add(mValueList.get(0));
		}		
		mItemsList = new ArrayList<String>();
		Cursor cursor = DatabaseAdapter.selectQuery(tables, null, filter, order, distinct);
		cursor.moveToFirst();
		int cursorCount = cursor.getCount();
		for (int i=0; i<cursorCount; i++) {
			String[] columnNames = cursor.getColumnNames();
			HashMap<Object, Object> record = new HashMap<Object, Object>();
			int columnsSize = columnNames.length;
			for (int j=0; j<columnsSize; j++) {
				if (columnNames[j].equals(DatabaseAttribute.FIELD+mLabelList.get(1))) {
					mItemsList.add(cursor.getString(j));
				}
				record.put(columnNames[j], cursor.getString(j));
			}
			mRecords.put(i, record);
			cursor.moveToNext();
		}
		DatabaseAdapter.closeCursor(cursor);
		mSpinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mItemsList);
		this.setAdapter(mSpinnerArrayAdapter);
	}
	
	public Object getCurrentValue() {
		return mValueList.get(mCurrentPosition);
	}
	
	/**
	 * Calls a given function when user change the current item
	 * @param name function name which will be called
	 */
	public void setOnChangeFunction(String name) {
		this.mFuncName = name;
		this.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				mCurrentPosition = position;
				if (mFormId != null) {
					setCurrentValue(mFormId, getCurrentRecord());
				}
				Function.createFunction(mFuncName);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});
	}
	
	public HashMap<Object, Object> getCurrentRecord() {
		if (mCurrentPosition == -1) {
			return null;
		} else {
			return mRecords.get(mCurrentPosition);
		}
	}
	
	public void removeAllItems() {
		mSpinnerArrayAdapter.clear();
	}
	
	public void addItem(String label, String value) {
		mItemsList.add(label);
		mLabelList.add(label);
		mValueList.add(value);
	}
	
	public String getValue() {
		if (mCurrentPosition == -1) {
			return mValueList.get(0);
		} else {
			return mValueList.get(mCurrentPosition);
		}
	}
	
	public String getLabel() {
		if (mCurrentPosition == -1) {
			return mLabelList.get(0);
		} else {
			return mLabelList.get(mCurrentPosition);
		}
	}
	
	public int count() {
		return mItemsList.size();
	}
	
	public int getSelectedIndex() {
		if (mCurrentPosition == -1) {
			return 1;
		} else {
			return mCurrentPosition + 1;
		}
	}
	
	public void setSelectedIndex(int index) {
		this.setSelection(index - 1);
	}
}
