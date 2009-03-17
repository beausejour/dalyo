package com.penbase.dma.Dalyo.Component.Custom.Dataview;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Displays database's data, each line represents a row of selected data and each column
 * represents all data of a given column of selected data
 */
public class DataView extends LinearLayout implements OnGestureListener {	
	private Paint mBorderPaint;
	private static float sFontSize;
	private static Typeface sFontType;
	private DataViewAdapter mAdapter;
	private Context mContext;
	private ArrayList<String> mPwidthList;
	private ArrayList<String> mLwidthList;
	private ArrayList<ArrayList<String>> mColumns = new ArrayList<ArrayList<String>>();
	private String mTableId;
	private int mCurrentPosition = -1;
	private HashMap<Integer, HashMap<Object, Object>> mRecords;
	private HashMap<Integer, String> mOnCalculateMap = new HashMap<Integer, String>();
	private ListView mListView;
	private GestureDetector mGestureDetector;
	private boolean mHasHeader;
	private HashMap<String, DecimalFormat> mFormatsMap = new HashMap<String, DecimalFormat>();
	
	public DataView(Context c, String tid) {
		super(c);
		mRecords = new HashMap<Integer, HashMap<Object, Object>>();
		mHasHeader = true;
		mBorderPaint = new Paint();
		mBorderPaint.setARGB(255, 0, 0, 0);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setStyle(Style.STROKE);
		this.mContext = c;
		this.mTableId = tid;
		this.mListView = new ListView(c);
		this.mGestureDetector = new GestureDetector(this);
    	this.mGestureDetector.setIsLongpressEnabled(false);
    	mAdapter = new DataViewAdapter();
		mPwidthList = new ArrayList<String>();
		mLwidthList = new ArrayList<String>();
		mListView.setItemsCanFocus(true);
		mListView.setCacheColorHint(Color.TRANSPARENT);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				mCurrentPosition = position;
				if (!((CustomLinearLayout) v).isHeader()) {
					v.setSelected(true);
				}
			}
		});
		
		mListView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (!arg1) {
					mCurrentPosition = -1;
				}
			}
		});
		mListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch (arg1.getAction()) {
					case MotionEvent.ACTION_UP:
						mListView.setVerticalScrollBarEnabled(false);
						break;
					default:
						mListView.setVerticalScrollBarEnabled(true);
						break;
				}
				return false;
			}
		});
	}
	
	/**
	 * Draws the DataView's border
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		RectF drawRect = new RectF();
	    drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
	    canvas.drawRoundRect(drawRect, 0, 0, mBorderPaint);
	}
	
	public void setText(float fs, Typeface ft) {
		sFontSize = fs;
		sFontType = ft;
	}
	
	public static float getTextSize() {
		return sFontSize;
	}
	
	public static Typeface getTextType() {
		return sFontType;
	}
	
	/**
	 * Saves the DataView's information (table id, field id, header name, 
	 * portrait's width and landscape's width)
	 * 
	 * @param list information's list
	 */
	public void setColumnInfo(ArrayList<ArrayList<String>> list) {
		int width = 0;
		if (list.size() > 0) {
			int listSize = list.size();
			ArrayList<String> headerList = new ArrayList<String>();
			for (int i=0; i<listSize; i++) {
				ArrayList<String> column = new ArrayList<String>();
				column.add(list.get(i).get(0));		//tid
				column.add(list.get(i).get(1));		//fid
				if (list.get(i).get(2).equals("")) {
					mHasHeader = false;
				}
				headerList.add(list.get(i).get(2));
				mPwidthList.add(list.get(i).get(3));
				mLwidthList.add(list.get(i).get(4));
				mColumns.add(column);
			}
			if (ApplicationView.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
				if (mHasHeader){
					CustomLinearLayout header = new CustomLinearLayout(mContext, headerList, mPwidthList, true);
					mListView.addHeaderView(header, null, false);
				}
				for (String s : mPwidthList) {
					width += Integer.valueOf(s);
				}
			} else {
				if (mHasHeader){
					CustomLinearLayout header = new CustomLinearLayout(mContext, headerList, mLwidthList, true);
					mListView.addHeaderView(header, null, false);
				}
				for (String s : mLwidthList) {
					width += Integer.valueOf(s);
				}
			}
			mListView.setAdapter(mAdapter);
		}
		this.addView(mListView, new LinearLayout.LayoutParams(width, LayoutParams.FILL_PARENT));
	}
	
	public void setOncalculate(HashMap<Integer, String> onc) {
		this.mOnCalculateMap = onc;
	}
	
	private ArrayList<String> getmPwidthList() {
		return mPwidthList;
	}
	
	private ArrayList<String> getmLwidthList() {
		return mLwidthList;
	}
	
	/**
	 * Refreshes DataView's data with the given filter and order
	 * 
	 * @param filter selection query's filter
	 * @param order selection query's order
	 */
	public void refresh(Object filter, Object order) {
		if (mTableId == null) {
			ApplicationView.errorDialog("Check your dataview setting");
		}
		else {
			if (mAdapter.getItems().size() > 1) {
				mAdapter.removeItems();
				mAdapter = new DataViewAdapter();
				mListView.setAdapter(mAdapter);
			}
			int columnNb = mColumns.size();
			ArrayList<String> tables = new ArrayList<String>();
			tables.add(mTableId);
			for (int i=0; i<columnNb; i++) {
				ArrayList<String> column = mColumns.get(i);
				if ((!tables.contains(column.get(0))) && ((!column.get(0).equals("")) && (!column.get(1).equals("")))) {
					tables.add(column.get(0));
				}
			}
			Cursor cursor = DatabaseAdapter.selectQuery(tables, null, filter, order, null);
			int count = cursor.getCount();
			if (count > 0) {
				cursor.moveToFirst();
				for (int i=0; i<count; i++) {
					String[] columnNames = cursor.getColumnNames();
					ArrayList<String> data = new ArrayList<String>();
					HashMap<Object, Object> record = new HashMap<Object, Object>();
					int mColumnsmRecordsize = columnNames.length;
					int mColumnsSize = mColumns.size();
					int calculateColumn = -1;
					for (int k=0; k<mColumnsSize; k++) {
						if (mOnCalculateMap.containsKey(k)) {
							calculateColumn = k;
							data.add("");
						} else {
							for (int j=0; j<mColumnsmRecordsize; j++) {
								String field = DatabaseAttribute.FIELD+mColumns.get(k).get(1);
								if (columnNames[j].equals(field)) {
									//Check numeric format
									Object cursorValue = DatabaseAdapter.getCursorValue(cursor, field);
									if (mFormatsMap.containsKey(mColumns.get(k).get(1))) {
										String formatResult = "";
										if (cursorValue.toString().indexOf(",") != -1) {
											formatResult = mFormatsMap.get(mColumns.get(k).get(1)).format(Double.parseDouble(cursorValue.toString().replace(",", ".")));
										} else {
											formatResult = mFormatsMap.get(mColumns.get(k).get(1)).format(Double.parseDouble(cursorValue.toString()));
											formatResult = formatResult.replace(",", ".");
										}
										data.add(formatResult);
									} else {
										data.add(cursorValue.toString());
									}
								}
								record.put(columnNames[j], DatabaseAdapter.getCursorValue(cursor, columnNames[j]).toString());
							}	
						}
					}
					if (calculateColumn != -1) {
						String value = Function.createCalculateFunction(mOnCalculateMap.get(calculateColumn), record).toString();
						data.remove(calculateColumn);
						data.add(calculateColumn, value);
					}
					CustomLinearLayout layout = null;
					if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						layout = new CustomLinearLayout(mContext, data, getmLwidthList(), false);
					} else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
						layout = new CustomLinearLayout(mContext, data, getmPwidthList(), false);
					}

					mAdapter.addItem(layout);
					mRecords.put(i, record);
					cursor.moveToNext();
				}
				mAdapter.notifyDataSetChanged();
			}
			DatabaseAdapter.closeCursor(cursor);
		}
	}
	
	/**
	 * @return the record the selected row
	 */
	public HashMap<Object, Object> getCurrentRecord() {
		if (mCurrentPosition < 1) {
			return null;
		} else {
			int position = mCurrentPosition;
			return mRecords.get(position);
		}
	}
	
	public void setCurrentPosition(int position) {
		mCurrentPosition = position;
	}
	
	public ListView getListView() {
		return mListView;
	}
	
	/**
	 * @param row 
	 * @param column
	 * @return the cell's value with give row and column
	 */
	public Object getCellValue(int row, int column) {
		String culumnName = DatabaseAttribute.FIELD+mColumns.get(column - 1).get(1);
		if (mRecords.size() > 0) {
			return mRecords.get(row - 1).get(culumnName);
		} else {
			return null;
		}
	}
	
	public int getColumnIndex(String fieldId) {
		int result = -1;
		int mColumnsNb = mColumns.size();
		int i = 0;
		while (i < mColumnsNb) {
			if (mColumns.get(i).get(1).equals(fieldId)) {
				result = i;
				i = mColumnsNb;
			}
		}
		if (result != -1) {
			result += 1;
		}
		return result;
	}
	
	public int getSelectedRow() {
		if (mCurrentPosition < 1) {
			return 0;
		} else {
			return mCurrentPosition;
		}
	}
	
	public int getRowCount() {
		return mRecords.size();
	}
	
	/**
	 * Sets the given column's value with the given decimals
	 * @param column which to be formated
	 * @param decimals numbers of decimal to display
	 */
	public void setNumericFormat(int column, int decimals) {
		String decimalString = "";
		if (decimals > 0) {
			decimalString = "0.";
			for (int i=0; i<decimals; i++) {
				decimalString += "0";
			}
		}
		mFormatsMap.put(mColumns.get(column - 1).get(1), new DecimalFormat(decimalString));
	}
	
	public void setSelectedRow(int row) {
		if (mListView.getChildCount() >= row) {
			mListView.getChildAt(mCurrentPosition).setSelected(false);
			mListView.getChildAt(row).setSelected(true);
		}
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	/**
	 * Catches on scroll event and horizontal scroll the DataView
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		int scrollWidth = mListView.getWidth() - this.getWidth();
		if ((this.getScrollX() >= 0) && (this.getScrollX() <= scrollWidth) && (scrollWidth > 0)) {
			int moveX = (int)distanceX;
			if (((moveX + this.getScrollX()) >= 0) && ((Math.abs(moveX) + Math.abs(this.getScrollX())) <= scrollWidth)) {
				this.scrollBy(moveX, 0);
			} else {
				if (distanceX >= 0) {
					this.scrollBy(scrollWidth - Math.max(Math.abs(moveX), Math.abs(this.getScrollX())), 0);
				} else {
					this.scrollBy(-Math.min(Math.abs(moveX), Math.abs(this.getScrollX())), 0);
				}
			}
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		super.dispatchTouchEvent(ev);
		return true;
	}
}