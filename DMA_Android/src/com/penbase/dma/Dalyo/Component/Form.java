package com.penbase.dma.Dalyo.Component;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;

import com.penbase.dma.R;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Dalyo.Component.Custom.Barcode;
import com.penbase.dma.Dalyo.Component.Custom.ComboBox;
import com.penbase.dma.Dalyo.Component.Custom.TextField;
import com.penbase.dma.Dalyo.Component.Custom.TextZone;
import com.penbase.dma.Dalyo.Component.Custom.Doodle.DoodleView;
import com.penbase.dma.Dalyo.Component.Custom.PictureBox.PictureBoxView;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationListView;

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
			if (mLayout.getChildAt(i) instanceof TextField) {
				((TextField)mLayout.getChildAt(i)).clear();
			} else if (mLayout.getChildAt(i) instanceof TextZone) {
				((TextZone)mLayout.getChildAt(i)).clear();
			} else if (mLayout.getChildAt(i) instanceof PictureBoxView) {
				((PictureBoxView)mLayout.getChildAt(i)).clear();
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
			if (mLayout.getChildAt(i) instanceof ComboBox) {
				((ComboBox)mLayout.getChildAt(i)).setCurrentValue(formId, record);
			} else if (mLayout.getChildAt(i) instanceof TextField) {
				((TextField)mLayout.getChildAt(i)).refresh(record);
			} else if (mLayout.getChildAt(i) instanceof TextZone) {
				((TextZone)mLayout.getChildAt(i)).refresh(record);
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
				if (mLayout.getChildAt(i) instanceof TextField) {
					((TextField)mLayout.getChildAt(i)).refresh(record);
				} else if (mLayout.getChildAt(i) instanceof TextZone) {
					((TextZone)mLayout.getChildAt(i)).refresh(record);
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
			if (mLayout.getChildAt(i) instanceof DoodleView) {
				String fileName = ((DoodleView)mLayout.getChildAt(i)).getImageName();
				if (!fileName.equals("")) {
					StringBuffer path = new StringBuffer(Constant.PACKAGENAME);
					path.append(ApplicationListView.getApplicationName());
					path.append("/");
					path.append(fileName);
					if (new File(path.toString()).exists()) {
						((DoodleView)mLayout.getChildAt(i)).setText("");
						((DoodleView)mLayout.getChildAt(i)).setBackgroundDrawable(Drawable.createFromPath(path.toString()));
					} else {
						((DoodleView)mLayout.getChildAt(i)).setText("Open Doodle");
					}	
				}
			} else if (mLayout.getChildAt(i) instanceof PictureBoxView) {
				String fileName = ((PictureBoxView)mLayout.getChildAt(i)).getPhotoName();
				if (!fileName.equals("")) {
					StringBuffer path = new StringBuffer(Constant.PACKAGENAME);
					path.append(ApplicationListView.getApplicationName());
					path.append("/");
					path.append(fileName);
					((BitmapDrawable)((PictureBoxView)mLayout.getChildAt(i)).getDrawable()).getBitmap().recycle();
					if (new File(path.toString()).exists()) {
						((PictureBoxView)mLayout.getChildAt(i)).setImageDrawable(Drawable.createFromPath(path.toString()));
					} else {
						((PictureBoxView)mLayout.getChildAt(i)).setImageResource(R.drawable.camera);	
					}
					((PictureBoxView)mLayout.getChildAt(i)).setScaleType(ScaleType.FIT_XY);
				}
			} else if (mLayout.getChildAt(i) instanceof Barcode) {
				StringBuffer path = new StringBuffer(Constant.PACKAGENAME);
				path.append(ApplicationListView.getApplicationName());
				path.append("/barcode_");
				path.append(((Barcode)mLayout.getChildAt(i)).getTag().toString());
				path.append("_tmp.jpg");
				((BitmapDrawable)((Barcode)mLayout.getChildAt(i)).getDrawable()).getBitmap().recycle();
				if (new File(path.toString()).exists()) {
					((Barcode)mLayout.getChildAt(i)).setImageDrawable(Drawable.createFromPath(path.toString()));	
				} else {
					((Barcode)mLayout.getChildAt(i)).setImageResource(R.drawable.barcode);	
				}
				((Barcode)mLayout.getChildAt(i)).setScaleType(ScaleType.FIT_XY);
			}
		}
	}
}
