package com.penbase.dma.Dalyo.Component.Custom.Dataview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Displays the header of DataView or a row of DataView
 */
public class CustomLinearLayout extends LinearLayout {
	private Context mContext;
	private ArrayList<String> mDataList;
	private ArrayList<String> mWidthList;
	private boolean mIsHeader;
	private DalyoDataView mDataview;
	
	public CustomLinearLayout(Context c, DalyoDataView dv, ArrayList<String> dl, ArrayList<String> wl, boolean ish) {
		super(c);
		this.mContext = c;
		this.mDataview = dv;
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.mDataList = new ArrayList<String>(dl);
		this.mIsHeader = ish;
		this.mWidthList = new ArrayList<String>(wl);
		
		if (mIsHeader) {
			int size = mDataList.size();
			for (int i=0; i<size; i++) {
				CustomTextView ctv = new CustomTextView(mContext);
				ctv.setText(mDataList.get(i));
				ctv.setTypeface(Typeface.DEFAULT_BOLD);
				ctv.setGravity(Gravity.CENTER);
				String fond = "#8F3600";
				ctv.setBackgroundColor(Color.parseColor(fond));
				int width = Integer.valueOf(mWidthList.get(i));
				this.addView(ctv, new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT));
			}
			setEnabled(false);
		} else {
			int size = mDataList.size();
			for (int i=0; i<size; i++) {
				CustomTextView ctv = new CustomTextView(mContext);
				ctv.setText(mDataList.get(i));
				ctv.setTextSize(mDataview.getTextSize());
				ctv.setTypeface(mDataview.getTextType());
				ctv.setGravity(Gravity.CENTER_VERTICAL);
				int width = Integer.valueOf(mWidthList.get(i));
				this.addView(ctv, new LinearLayout.LayoutParams(width, 50));
			}
			setEnabled(true);
		}
	}	
	
	public ArrayList<String> getDataList() {
		return mDataList;
	}
	
	public boolean isHeader() {
		return mIsHeader;
	}
	
	public ArrayList<String> getWidthList() {
		return mWidthList;
	}
}
