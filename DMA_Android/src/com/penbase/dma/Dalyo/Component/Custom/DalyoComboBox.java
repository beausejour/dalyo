package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.penbase.dma.R;
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
	private ComboboxAdapter mAdapter;
	private int mCurrentPosition = -1;
	private ArrayList<String> mLabelList = null;
	private ArrayList<String> mValueList = null;
	private ArrayList<String> mItemsList = null;
	private Context mContext;
	private String mFormId;
	private HashMap<Integer, HashMap<Object, Object>> mRecords;
	private String mFontColor = null;
	private float mFontSize;
	private Typeface mFontType;
	private Drawable mBulletDrawable = null;
	private String mOnChangeFunctionName = null;

	public DalyoComboBox(Context context, ArrayList<String> labelList,
			ArrayList<String> valueList) {
		super(context);
		this.mContext = context;
		this.mLabelList = labelList;
		this.mValueList = valueList;
		mRecords = new HashMap<Integer, HashMap<Object, Object>>();
		setOnItemSelectedListener();
	}

	public DalyoComboBox(Context context) {
		super(context);
		this.mContext = context;
		setOnItemSelectedListener();
	}
	
	private void setOnItemSelectedListener() {
		this.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				mCurrentPosition = position;
				if (mFormId != null) {
					setCurrentValue(mFormId, getCurrentRecord());
				}
				if (mOnChangeFunctionName != null) {
					Function.createFunction(mOnChangeFunctionName);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	public void setBulletPath(String path) {
		mBulletDrawable = Drawable.createFromPath(path);
	}

	public void setFont(float size, Typeface type, String color) {
		mFontSize = size;
		mFontType = type;
		mFontColor = color;
	}

	public void setItemList(ArrayList<String> il) {
		if (mLabelList == null && mValueList == null) {
			this.mLabelList = new ArrayList<String>();
			this.mValueList = new ArrayList<String>();
			this.mItemsList = il;
			for (String s : il) {
				mLabelList.add(s);
				mValueList.add(s);
			}
			mAdapter = new ComboboxAdapter(mContext, il);
			this.setAdapter(mAdapter);
		}
	}

	public void setCurrentValue(String fid, HashMap<Object, Object> record) {
		mFormId = fid;
		if ((mFormId != null) && (record != null)) {
			ApplicationView.getLayoutsMap().get(mFormId).refresh(record);
		}
	}

	/**
	 * Sends a sql query with new parameters
	 * 
	 * @param filter
	 *            column filter condition
	 * @param order
	 *            order by a column
	 * @param distinct
	 *            if distinct same value
	 */
	public void refresh(Object filter, Object order, Object distinct) {
		if (mRecords != null) {
			ArrayList<String> tables = new ArrayList<String>();
			tables.add(mLabelList.get(0));
			if (!tables.contains(mValueList.get(0))) {
				tables.add(mValueList.get(0));
			}
			mItemsList = new ArrayList<String>();
			Cursor cursor = DatabaseAdapter.selectQuery(tables, null, filter,
					order, distinct);
			String[] columnNames = cursor.getColumnNames();
			int i = 0;
			while (cursor.moveToNext()) {
				HashMap<Object, Object> record = new HashMap<Object, Object>();
				int columnsSize = columnNames.length;
				for (int j = 0; j < columnsSize; j++) {
					String columnName = columnNames[j];
					String value = cursor.getString(j);
					if (columnName.equals(DatabaseAttribute.FIELD
							+ mLabelList.get(1))) {
						mItemsList.add(value);
					}
					record.put(columnName, value);
				}
				mRecords.put(i, record);
				i++;
			}
			DatabaseAdapter.closeCursor(cursor);
			mAdapter = new ComboboxAdapter(mContext, mItemsList);
			this.setAdapter(mAdapter);	
		}
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
		mAdapter.clear();
		mAdapter.notifyDataSetChanged();
	}

	public void addItem(String label, String value) {
		mItemsList.add(label);
		mLabelList.add(label);
		mValueList.add(value);
		mAdapter.notifyDataSetChanged();
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
		int selection = index - 1;
		if (mItemsList.size() > selection) {
			setSelection(selection);
		}
	}

	@Override
	public String getComponentLabel() {
		return getLabel();
	}

	@Override
	public Object getComponentValue() {
		return getLabel();
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
		if (mCurrentPosition > 0) {
			setSelection(0);
		}
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

	}

	@Override
	public void setComponentText(String text) {

	}

	@Override
	public void setComponentValue(Object value) {
		if (mItemsList.contains(value)) {
			setSelection(mItemsList.indexOf(value));
		}
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
		mOnChangeFunctionName = functionName;
	}

	@Override
	public void setOnClickEvent(final String functionName) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getMinimumHeight() {
		return getSuggestedMinimumHeight();
	}

	@Override
	public int getMinimumWidth() {
		return getSuggestedMinimumWidth();
	}

	private class ComboboxAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<String> mItems;

		public ComboboxAdapter(Context context, ArrayList<String> items) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mItems = items;
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.combobox, parent,
						false);
				holder = new ViewHolder();
				holder.text = (TextView) convertView
						.findViewById(R.id.itemtext);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.text.setText(mItems.get(position));
			holder.text.setTextSize(mFontSize);
			holder.text.setTypeface(mFontType);
			if (mFontColor != null) {
				holder.text.setTextColor(Color.parseColor("#" + mFontColor));
			} else {
				holder.text.setTextColor(Color.BLACK);
			}
			if (mBulletDrawable != null) {
				holder.text.setCompoundDrawablesWithIntrinsicBounds(
						mBulletDrawable, null, null, null);
			}
			return convertView;
		}
		
		public void clear() {
			mItems.clear();
		}
	}

	private static class ViewHolder {
		TextView text;
	}
}
