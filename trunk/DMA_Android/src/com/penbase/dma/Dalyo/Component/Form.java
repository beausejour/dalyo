package com.penbase.dma.Dalyo.Component;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;

import com.penbase.dma.R;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Dalyo.BarcodeReader.Barcode;
import com.penbase.dma.Dalyo.Component.Custom.ComboBox;
import com.penbase.dma.Dalyo.Component.Custom.TextField;
import com.penbase.dma.Dalyo.Component.Custom.TextZone;
import com.penbase.dma.Dalyo.Component.Custom.Doodle.DoodleView;
import com.penbase.dma.Dalyo.Component.Custom.PictureBox.PictureBoxView;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A form of application
 */
public class Form extends ScrollView {
	private String mTableId;
	private String mTitle;
	private ArrayList<String> mMenuItemNameList;
	private ArrayList<String> mMenuItemOnClickList;
	private RelativeLayout mLayout;
	
	public Form(Context context) {		
		super(context);
		mMenuItemNameList = new ArrayList<String>();
		mMenuItemOnClickList = new ArrayList<String>();
		mLayout = new RelativeLayout(context);
		addView(mLayout);
	}
	
	public void addSubView(View view) {
		mLayout.addView(view);
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
			if (view instanceof TextField) {
				((TextField)view).clear();
			} else if (view instanceof TextZone) {
				((TextZone)view).clear();
			} else if (view instanceof PictureBoxView) {
				((PictureBoxView)view).clear();
			}
		}
	}
	
	/**
	 * Binds a record to its subviews
	 * @param formId
	 * @param record An HashMap {column name = value}
	 */
	public void setRecord(String formId, HashMap<Object, Object> record) {
		int viewLen = mLayout.getChildCount();
		for (int i=0; i<viewLen; i++) {
			View view = mLayout.getChildAt(i);
			if (view instanceof ComboBox) {
				((ComboBox)view).setCurrentValue(formId, record);
			} else if (view instanceof TextField) {
				((TextField)view).refresh(record);
			} else if (view instanceof TextZone) {
				((TextZone)view).refresh(record);
			}
		}
	}
	
	/**
	 * Refreshes subview's value with a given record
	 * @param record
	 */
	public void refresh(HashMap<Object, Object> record) {
		if (record.size() > 0) {
			int viewLen = mLayout.getChildCount();
			for (int i=0; i<viewLen; i++) {
				View view = mLayout.getChildAt(i);
				if (view instanceof TextField) {
					((TextField)view).refresh(record);
				} else if (view instanceof TextZone) {
					((TextZone)view).refresh(record);
				}
			}
		}
	}
	
	/**
	 * Sets its subviews' preview, only for Doodle and PictureBox
	 */
	public void setPreview() {
		int viewLen = mLayout.getChildCount();
		StringBuffer path = new StringBuffer(DmaHttpClient.getFilesPath());
		path.append(Constant.TEMPDIRECTORY);
		int start = path.length();
		for (int i=0; i<viewLen; i++) {
			View view = mLayout.getChildAt(i);
			if (view instanceof DoodleView) {
				String fileName = ((DoodleView)view).getImageName();
				if (!fileName.equals("")) {
					path.delete(start, path.length());
					path.append(fileName);
					if (new File(path.toString()).exists()) {
						((DoodleView)view).setText("");
						((DoodleView)view).setBackgroundDrawable(Drawable.createFromPath(path.toString()));
					} else {
						((DoodleView)view).setText("Doodle");
					}	
				}
			} else if (view instanceof PictureBoxView) {
				String fileName = ((PictureBoxView)view).getPhotoName();
				if (!fileName.equals("")) {
					path.delete(start, path.length());
					path.append(fileName);
					if (new File(path.toString()).exists()) {
						((PictureBoxView)view).setImageDrawable(Drawable.createFromPath(path.toString()));
						((PictureBoxView)view).setScaleType(ScaleType.FIT_XY);
					} else {
						((PictureBoxView)view).setImageResource(R.drawable.camera);	
					}
				}
			} else if (view instanceof Barcode) {
				String fileName = ((Barcode)view).getImageName();
				if (!fileName.equals("")) {
					path.delete(start, path.length());
					path.append(fileName);
					if (new File(path.toString()).exists()) {
						((Barcode)view).setImageDrawable(Drawable.createFromPath(path.toString()));
						((Barcode)view).setScaleType(ScaleType.FIT_XY);
					} else {
						((Barcode)view).setImageResource(R.drawable.barcode);	
					}
				}
			}
		}
	}
}
