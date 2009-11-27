package com.penbase.dma.Dalyo.Component;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;

import com.penbase.dma.R;
import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Dalyo.BarcodeReader.BarcodeComponent;
import com.penbase.dma.Dalyo.Component.Custom.DalyoComboBox;
import com.penbase.dma.Dalyo.Component.Custom.DalyoLabel;
import com.penbase.dma.Dalyo.Component.Custom.DalyoTextField;
import com.penbase.dma.Dalyo.Component.Custom.DalyoTextZone;
import com.penbase.dma.Dalyo.Component.Custom.Doodle.DalyoDoodle;
import com.penbase.dma.Dalyo.Component.Custom.PictureBox.DalyoPictureBox;
import com.penbase.dma.Dalyo.Function.Function;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A form of application
 */
public class Form extends ScrollView {
	private String mTableId;
	private String mTitle;
	private boolean mIsModal;
	private ArrayList<String> mMenuItemNameList;
	private ArrayList<String> mMenuItemOnClickList;
	private RelativeLayout mLayout;
	private int mWidth;
	private int mHeight;
	
	public Form(Context context) {		
		super(context);
		mMenuItemNameList = new ArrayList<String>();
		mMenuItemOnClickList = new ArrayList<String>();
		mLayout = new RelativeLayout(context);
		addView(mLayout);
	}
	
	/**
	 * Sets form's dimension
	 * @param width dimension's width
	 * @param height dimension's height
	 */
	public void setDimension(int width, int height) {
		mWidth = width;
		mHeight = height;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(mWidth, mHeight);
	}

	/**
	 * Adds sub view
	 * @param view a sub view
	 */
	public void addSubView(View view) {
		mLayout.addView(view);
	}
	
	/**
	 * Sets modal form
	 * @param modal boolean value
	 */
	public void setModal(String modal) {
		mIsModal = Boolean.parseBoolean(modal);
	}
	
	/**
	 * @return if form is modal
	 */
	public boolean isModal() {
		return mIsModal;
	}
	
	/**
	 * Calls the given function before display this form
	 * @param name Function name
	 */
	public void onLoad(String name) {
		Function.createFunction(name);
	}
	
	public ArrayList<String> getMenuItemNameList() {
		return mMenuItemNameList;
	}
	
	public void setMenuItemNameList(ArrayList<String> nameList) {
		mMenuItemNameList = nameList;
	}
	
	public ArrayList<String> getMenuItemOnClickList() {
		return mMenuItemOnClickList;
	}
	
	public void setMenuItemOnClickList(ArrayList<String> onClickList) {
		mMenuItemOnClickList = onClickList;
	}
	
	public void setTableId(String tableId) {
		this.mTableId = tableId;
	}
	
	public String getTableId() {
		return mTableId;
	}
	
	public void setTitle(String t) {
		this.mTitle = t;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	/**
	 * Clears its subview's value
	 */
	public void clear() {
		int viewLen = mLayout.getChildCount();
		for (int i=0; i<viewLen; i++) {
			View view = mLayout.getChildAt(i);
			if (view instanceof DalyoTextField) {
				((DalyoTextField)view).clear();
			} else if (view instanceof DalyoTextZone) {
				((DalyoTextZone)view).clear();
			} else if (view instanceof DalyoPictureBox) {
				((DalyoPictureBox)view).clear();
			}
		}
	}
	
	/**
	 * Binds a record to its subviews by form id
	 * @param formId identification of form
	 * @param record An HashMap {column name = value}
	 */
	public void setRecordByForm(String formId, HashMap<Object, Object> record) {
		if (record != null) {
			int viewLen = mLayout.getChildCount();
			for (int i=0; i<viewLen; i++) {
				View view = mLayout.getChildAt(i);
				if (view instanceof DalyoComboBox) {
					((DalyoComboBox)view).setCurrentValue(formId, record);
				} else if (view instanceof DalyoTextField) {
					((DalyoTextField)view).refresh(record);
				} else if (view instanceof DalyoTextZone) {
					((DalyoTextZone)view).refresh(record);
				}
			}	
		}
	}
	
	/**
	 * Binds a record to its subviews by table id
	 * @param tableId identification of table
	 * @param record An HashMap {column name = value}
	 */
	public void setRecordByTable(String tableId, HashMap<Object, Object> record) {
		if (record != null) {
			int viewCount = mLayout.getChildCount();
			for (int i=0; i<viewCount; i++) {
				View view = mLayout.getChildAt(i);
				if (view instanceof DalyoLabel) {
					DalyoLabel label = (DalyoLabel)view;
					String labelTableId = label.getTableId();
					if (labelTableId != null && labelTableId.equals(tableId)) {
						label.refresh(record);
					}
				} else if (view instanceof DalyoTextField) {
					DalyoTextField textField = (DalyoTextField)view;
					String textFieldTableId = textField.getTableId();
					if (textFieldTableId != null && textFieldTableId.equals(tableId)) {
						textField.refresh(record);
					}
				} else if (view instanceof DalyoTextZone) {
					DalyoTextZone textZone = (DalyoTextZone)view;
					String textZoneTableId = textZone.getTableId();
					if (textZoneTableId != null && textZoneTableId.equals(tableId)) {
						textZone.refresh(record);
					}
				}
			}	
		}
	}
	
	/**
	 * Refreshes subview's value with a given record
	 * @param record An HashMap {column name = value}
	 */
	public void refresh(HashMap<Object, Object> record) {
		if (record.size() > 0) {
			int viewLen = mLayout.getChildCount();
			for (int i=0; i<viewLen; i++) {
				View view = mLayout.getChildAt(i);
				if (view instanceof DalyoTextField) {
					((DalyoTextField)view).refresh(record);
				} else if (view instanceof DalyoTextZone) {
					((DalyoTextZone)view).refresh(record);
				}
			}
		}
	}
	
	/**
	 * Sets its subviews' preview, only for Doodle and PictureBox
	 */
	public void setPreview() {
		int viewLen = mLayout.getChildCount();
		for (int i=0; i<viewLen; i++) {
			View view = mLayout.getChildAt(i);
			if (view instanceof DalyoDoodle) {
				DalyoDoodle doodle = (DalyoDoodle)view;
				String filePath = doodle.getImagePath();
				if (!filePath.equals("")) {
					if (new File(filePath).exists()) {
						doodle.setText("");
						doodle.setBackgroundDrawable(Drawable.createFromPath(filePath));
					} else {
						doodle.setText("Doodle");
					}	
				}
			} else if (view instanceof DalyoPictureBox) {
				DalyoPictureBox pictureBox = (DalyoPictureBox)view;
				String filePath = pictureBox.getPhotoPath();
				if (!filePath.equals("")) {
					if (new File(filePath).exists()) {
						((BitmapDrawable)pictureBox.getDrawable()).getBitmap().recycle();
						pictureBox.setImageDrawable(Drawable.createFromPath(filePath));
						pictureBox.setScaleType(ScaleType.FIT_XY);
					} else {
						pictureBox.setImageResource(R.drawable.camera);	
					}
				}
			} else if (view instanceof BarcodeComponent) {
				BarcodeComponent barcode = (BarcodeComponent)view;
				String filePath = barcode.getImagePath();
				if (!filePath.equals("")) {
					if (new File(filePath).exists()) {
						((BitmapDrawable)barcode.getDrawable()).getBitmap().recycle();
						barcode.setImageDrawable(Drawable.createFromPath(filePath));
						barcode.setScaleType(ScaleType.FIT_XY);
					} else {
						barcode.setImageResource(R.drawable.barcode);	
					}
				}
			}
		}
	}
	
	/**
	 * Validates form's new record by saving subview's value 
	 * @param tableId identification of table
	 * @return An HashMap {field id = value}
	 */
	public HashMap<String, String> validateNewRecord(String tableId) {
		HashMap<String, String> values = new HashMap<String, String>();
		int viewCount = mLayout.getChildCount();
		for (int i=0; i<viewCount; i++) {
			View view = mLayout.getChildAt(i);
			if (view instanceof DalyoLabel) {
				DalyoLabel label = (DalyoLabel)view;
				String labelTableId = label.getTableId();
				if (labelTableId != null && labelTableId.equals(tableId)) {
					String labelFid = label.getFieldId();
					if (labelFid != null) {
						values.put(labelFid, label.getComponentLabel());
					}
				}
			} else if (view instanceof DalyoTextField) {
				DalyoTextField textField = (DalyoTextField)view;
				String textFieldTableId = textField.getTableId();
				if (textFieldTableId != null && textFieldTableId.equals(tableId)) {
					String textFieldFid = textField.getFieldId();
					if (textFieldFid != null) {
						values.put(textFieldFid, textField.getComponentLabel());
					}
				}
			} else if (view instanceof DalyoTextZone) {
				DalyoTextZone textZone = (DalyoTextZone)view;
				String textZoneTableId = textZone.getTableId();
				if (textZoneTableId != null && textZoneTableId.equals(tableId)) {
					String textZoneFid = textZone.getFieldId();
					if (textZoneFid != null) {
						values.put(textZoneFid, textZone.getComponentLabel());
					}
				}
			}
		}
		return values;	
	}
	
	/**
	 * Saves subview's value in a ContentValue
	 * @param tableId identification of table
	 * @return a ContentValue
	 */
	public ContentValues validateEditRecord(String tableId) {
		ContentValues values = new ContentValues();
		int viewCount = mLayout.getChildCount();
		for (int i=0; i<viewCount; i++) {
			View view = mLayout.getChildAt(i);
			if (view instanceof DalyoLabel) {
				DalyoLabel label = (DalyoLabel)view;
				String labelTableId = label.getTableId();
				if (labelTableId != null && labelTableId.equals(tableId)) {
					String labelFid = label.getFieldId();
					if (labelFid != null) {
						values.put(DatabaseAttribute.FIELD + labelFid, label.getComponentLabel());
					}
				}
			} else if (view instanceof DalyoTextField) {
				DalyoTextField textField = (DalyoTextField)view;
				String textFieldTableId = textField.getTableId();
				if (textFieldTableId != null && textFieldTableId.equals(tableId)) {
					String textFieldFid = textField.getFieldId();
					if (textFieldFid != null) {
						values.put(DatabaseAttribute.FIELD + textFieldFid, textField.getComponentLabel());
					}
				}
			} else if (view instanceof DalyoTextZone) {
				DalyoTextZone textZone = (DalyoTextZone)view;
				String textZoneTableId = textZone.getTableId();
				if (textZoneTableId != null && textZoneTableId.equals(tableId)) {
					String textZoneFid = textZone.getFieldId();
					if (textZoneFid != null) {
						values.put(DatabaseAttribute.FIELD + textZoneFid, textZone.getComponentLabel());
					}
				}
			}
		}
		return values;
	}
}
