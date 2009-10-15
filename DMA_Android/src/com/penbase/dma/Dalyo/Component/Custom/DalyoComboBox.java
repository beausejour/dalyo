package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Dalyo.Component.DalyoComponent;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Displays configured items or retrieved data from databases
 */
public class DalyoComboBox extends Spinner implements DalyoComponent {
	private ArrayAdapter<String> mSpinnerArrayAdapter;
	private int mCurrentPosition = -1;
	private ArrayList<String> mLabelList;
	private ArrayList<String> mValueList;
	private ArrayList<String> mItemsList;
	private Context mContext;
	private String mFormId;
	private HashMap<Integer, HashMap<Object, Object>> mRecords = new HashMap<Integer, HashMap<Object, Object>>();
	private String mFuncName;
	
	public DalyoComboBox(Context context, ArrayList<String> labelList, ArrayList<String> valueList) {
		super(context);
		this.mContext = context;
		this.mLabelList = labelList;
		this.mValueList = valueList;
	}
	
	public DalyoComboBox(Context context) {
		super(context);
		this.mContext = context;
	}
	
	public void setItemList(ArrayList<String> il) {
		this.mLabelList = new ArrayList<String>();
		this.mValueList = new ArrayList<String>();
		this.mItemsList = il;
		for (String s : il) {
			mLabelList.add(s);
			mValueList.add(s);
		}
		mSpinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, il);
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
		String[] columnNames = cursor.getColumnNames();
		int i = 0;
		while (cursor.moveToNext()) {
			HashMap<Object, Object> record = new HashMap<Object, Object>();
			int columnsSize = columnNames.length;
			for (int j=0; j<columnsSize; j++) {
				String columnName = columnNames[j];
				String value = cursor.getString(j);
				if (columnName.equals(DatabaseAttribute.FIELD+mLabelList.get(1))) {
					mItemsList.add(value);
				}
				record.put(columnName, value);
			}
			mRecords.put(i, record);
			i++;
		}
		DatabaseAdapter.closeCursor(cursor);
		mSpinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mItemsList);
		this.setAdapter(mSpinnerArrayAdapter);
	}
	
	public Object getCurrentValue() {
		return mValueList.get(mCurrentPosition);
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

	@Override
	public String getComponentLabel() {
		return getLabel();
	}

	@Override
	public Object getComponentValue() {
		return getValue();
	}

	@Override
	public boolean isComponentEnabled() {
		return isEnabled();
	}

	@Override
	public boolean isComponentVisible() {
		if (getVisibility() == View.VISIBLE) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void resetComponent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setComponentEnabled(boolean enable) {
		setEnabled(enable);
	}

	@Override
	public void setComponentFocus() {
		requestFocus();
	}

	@Override
	public void setComponentLabel(String label) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setComponentText(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setComponentValue(Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setComponentVisible(boolean visible) {
		if (visible) {
			setVisibility(View.VISIBLE);
		} else {
			setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void setOnChangeEvent(String functionName) {
		this.mFuncName = functionName;
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

	@Override
	public void setOnClickEvent(String functionName) {
		// TODO Auto-generated method stub
		
	}
}
