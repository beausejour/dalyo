package com.penbase.dma.Dalyo.Component.Custom.Dataview;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.penbase.dma.Dalyo.Component.DalyoComponent;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Displays database's data, each line represents a row of selected data and
 * each column represents all data of a given column of selected data
 */
public class DalyoDataView extends LinearLayout implements DalyoComponent,
		OnGestureListener {
	private Paint mBorderPaint;
	private float mFontSize;
	private Typeface mFontType;
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

	public DalyoDataView(Context c, String tid) {
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
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				if (mHasHeader) {
					setRowBackground(mCurrentPosition + 1, false);
				} else {
					setRowBackground(mCurrentPosition, false);
				}
				setCurrentPosition(position);
				setRowBackground(position, true);
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
		/*
		 * super.onDraw(canvas); RectF drawRect = new RectF(); drawRect.set(0,0,
		 * getMeasuredWidth(), getMeasuredHeight());
		 * canvas.drawRoundRect(drawRect, 0, 0, mBorderPaint);
		 */
	}

	public void setText(float fs, Typeface ft) {
		mFontSize = fs;
		mFontType = ft;
	}

	public float getTextSize() {
		return mFontSize;
	}

	public Typeface getTextType() {
		return mFontType;
	}

	public boolean hasHeader() {
		return mHasHeader;
	}

	public void setRowBackground(int position, boolean isSelected) {
		if (isSelected) {
			((CustomLinearLayout) mListView.getItemAtPosition(position))
					.setBackgroundColor(Color.rgb(255, 142, 0));
		} else {
			if (position > 0) {
				((CustomLinearLayout) mListView.getItemAtPosition(position))
						.setBackgroundColor(Color.TRANSPARENT);
			}
		}
	}

	/**
	 * Saves the DataView's information (table id, field id, header name,
	 * portrait's width and landscape's width)
	 * 
	 * @param list
	 *            information's list
	 */
	public void setColumnInfo(ArrayList<ArrayList<String>> lists) {
		int width = 0;
		int listsSize = lists.size();
		if (listsSize > 0) {
			ArrayList<String> headerList = new ArrayList<String>();
			for (int i = 0; i < listsSize; i++) {
				ArrayList<String> list = lists.get(i);
				ArrayList<String> column = new ArrayList<String>();
				column.add(list.get(0)); // tid
				column.add(list.get(1)); // fid
				if (list.get(2).equals("")) {
					mHasHeader = false;
				}
				headerList.add(list.get(2));
				mPwidthList.add(list.get(3));
				mLwidthList.add(list.get(4));
				mColumns.add(column);
			}
			if (ApplicationView.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
				if (mHasHeader) {
					CustomLinearLayout header = new CustomLinearLayout(
							mContext, this, headerList, mPwidthList, true);
					mListView.addHeaderView(header, null, false);
				}
				for (String s : mPwidthList) {
					width += Integer.valueOf(s);
				}
			} else {
				if (mHasHeader) {
					CustomLinearLayout header = new CustomLinearLayout(
							mContext, this, headerList, mLwidthList, true);
					mListView.addHeaderView(header, null, false);
				}
				for (String s : mLwidthList) {
					width += Integer.valueOf(s);
				}
			}
			mListView.setAdapter(mAdapter);
		}
		this.addView(mListView, new LinearLayout.LayoutParams(width,
				LayoutParams.FILL_PARENT));
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
	 * @param filter
	 *            selection query's filter
	 * @param order
	 *            selection query's order
	 */
	public void refresh(Object filter, Object order) {
		if (mTableId == null) {
			ApplicationView.errorDialog("Check your dataview setting");
		} else {
			if (mAdapter.getItems().size() > 1) {
				mAdapter.removeItems();
				mAdapter = new DataViewAdapter();
				mListView.setAdapter(mAdapter);
			}
			int columnNb = mColumns.size();
			ArrayList<String> tables = new ArrayList<String>();
			tables.add(mTableId);
			for (int i = 0; i < columnNb; i++) {
				ArrayList<String> column = mColumns.get(i);
				if ((!tables.contains(column.get(0)))
						&& ((!column.get(0).equals("")) && (!column.get(1)
								.equals("")))) {
					tables.add(column.get(0));
				}
			}
			Cursor cursor = DatabaseAdapter.selectQuery(tables, null, filter,
					order, null);
			String[] columnNames = cursor.getColumnNames();
			int i = 0;
			while (cursor.moveToNext()) {
				ArrayList<String> data = new ArrayList<String>();
				HashMap<Object, Object> record = new HashMap<Object, Object>();
				int mColumnsmRecordsize = columnNames.length;
				int mColumnsSize = mColumns.size();
				int calculateColumn = -1;
				for (int k = 0; k < mColumnsSize; k++) {
					if (mOnCalculateMap.containsKey(k)) {
						calculateColumn = k;
						data.add("");
					} else {
						for (int j = 0; j < mColumnsmRecordsize; j++) {
							String fieldId = mColumns.get(k).get(1);
							String field = DatabaseAttribute.FIELD + fieldId;
							String columnName = columnNames[j];
							if (columnName.equals(field)) {
								// Check numeric format
								Object cursorValue = DatabaseAdapter
										.getCursorValue(cursor, field);
								if (mFormatsMap.containsKey(fieldId)) {
									String formatResult = "";
									if (cursorValue.toString().indexOf(",") != -1) {
										formatResult = mFormatsMap
												.get(fieldId)
												.format(
														Double
																.parseDouble(cursorValue
																		.toString()
																		.replace(
																				",",
																				".")));
									} else {
										formatResult = mFormatsMap
												.get(fieldId)
												.format(
														Double
																.parseDouble(cursorValue
																		.toString()));
										formatResult = formatResult.replace(
												",", ".");
									}
									data.add(formatResult);
								} else {
									data.add(cursorValue.toString());
								}
							}
							record.put(columnName, DatabaseAdapter
									.getCursorValue(cursor, columnName)
									.toString());
						}
					}
				}
				if (calculateColumn != -1) {
					String value = Function.createCalculateFunction(
							mOnCalculateMap.get(calculateColumn), record)
							.toString();
					data.remove(calculateColumn);
					data.add(calculateColumn, value);
				}
				CustomLinearLayout layout = null;
				int orientation = mContext.getResources().getConfiguration().orientation;
				if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					layout = new CustomLinearLayout(mContext, this, data,
							getmLwidthList(), false);
				} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
					layout = new CustomLinearLayout(mContext, this, data,
							getmPwidthList(), false);
				}

				mAdapter.addItem(layout);
				mRecords.put(i, record);
				i++;
			}
			mAdapter.notifyDataSetChanged();
			DatabaseAdapter.closeCursor(cursor);
		}
	}

	/**
	 * @return the record the selected row
	 */
	public HashMap<Object, Object> getCurrentRecord() {
		if (mCurrentPosition < 0) {
			return null;
		} else {
			int position = mCurrentPosition;
			return mRecords.get(position);
		}
	}

	public void setCurrentPosition(int position) {
		if (mHasHeader) {
			mCurrentPosition = position - 1;
		} else {
			mCurrentPosition = position;
		}
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
		String culumnName = DatabaseAttribute.FIELD
				+ mColumns.get(column - 1).get(1);
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
		if (mCurrentPosition < 0) {
			return -1;
		} else {
			return mCurrentPosition;
		}
	}

	public int getRowCount() {
		return mRecords.size();
	}

	/**
	 * Sets the given column's value with the given decimals
	 * 
	 * @param column
	 *            which to be formated
	 * @param decimals
	 *            numbers of decimal to display
	 */
	public void setNumericFormat(int column, int decimals) {
		String decimalString = "";
		if (decimals > 0) {
			decimalString = "0.";
			for (int i = 0; i < decimals; i++) {
				decimalString += "0";
			}
		}
		mFormatsMap.put(mColumns.get(column - 1).get(1), new DecimalFormat(
				decimalString));
	}

	public void setSelectedRow(int row) {
		if (mListView.getCount() >= row) {
			if (mHasHeader) {
				setRowBackground(mCurrentPosition + 1, false);
			} else {
				setRowBackground(mCurrentPosition, false);
			}
			setCurrentPosition(row);
			setRowBackground(row, true);
		}
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	/**
	 * Catches on scroll event and horizontal scroll the DataView
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		int scrollWidth = mListView.getWidth() - this.getWidth();
		if ((this.getScrollX() >= 0) && (this.getScrollX() <= scrollWidth)
				&& (scrollWidth > 0)) {
			int moveX = (int) distanceX;
			if (((moveX + this.getScrollX()) >= 0)
					&& ((Math.abs(moveX) + Math.abs(this.getScrollX())) <= scrollWidth)) {
				this.scrollBy(moveX, 0);
			} else {
				if (distanceX >= 0) {
					this.scrollBy(scrollWidth
							- Math.max(Math.abs(moveX), Math.abs(this
									.getScrollX())), 0);
				} else {
					this.scrollBy(-Math.min(Math.abs(moveX), Math.abs(this
							.getScrollX())), 0);
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
		super.dispatchTouchEvent(ev);
		mGestureDetector.onTouchEvent(ev);
		return true;
	}

	@Override
	public String getComponentLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getComponentValue() {
		// TODO Auto-generated method stub
		return null;
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
	public void setOnClickEvent(final String functionName) {
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (hasHeader() && (getSelectedRow() != -1)) {
					setRowBackground(getSelectedRow() + 1, false);
				} else {
					if (getSelectedRow() != -1) {
						setRowBackground(getSelectedRow(), false);
					}
				}
				setCurrentPosition(position);
				setRowBackground(position, true);
				Function.createFunction(functionName);
			}
		});
	}

	@Override
	public void setOnChangeEvent(String functionName) {
		// TODO Auto-generated method stub
		
	}
}