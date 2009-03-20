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
	private ArrayList<String> mDataList = new ArrayList<String>();
	private ArrayList<String> mWidthList = new ArrayList<String>();
	private boolean mIsHeader;
	
	public CustomLinearLayout(Context c, ArrayList<String> dl, ArrayList<String> wl, boolean ish) {
		super(c);
		this.mContext = c;
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.mDataList = dl;
		this.mIsHeader = ish;
		this.mWidthList = wl;
		
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
				ctv.setTextSize(DataView.getTextSize());
				ctv.setTypeface(DataView.getTextType());
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
