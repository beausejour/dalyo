package com.penbase.dma.Dalyo.Component.Custom.Dataview;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;

public class CustomLinearLayout extends LinearLayout {
	private Context context;
	private ArrayList<String> dataList = new ArrayList<String>();
	private ArrayList<String> widthList = new ArrayList<String>();
	private boolean isHeader;
	
	public CustomLinearLayout(Context c, ArrayList<String> dl, ArrayList<String> wl, boolean ish) {
		super(c);
		this.context = c;
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.dataList = dl;
		this.isHeader = ish;
		this.widthList = wl;
		
		if (isHeader) {
			int size = dataList.size();
			for (int i=0; i<size; i++) {
				CustomTextView ctv = new CustomTextView(context);
				ctv.setText(dataList.get(i));
				ctv.setTypeface(Typeface.DEFAULT_BOLD);
				ctv.setGravity(Gravity.CENTER_VERTICAL);
				String fond = "#8F3600";
				ctv.setBackgroundColor(Color.parseColor(fond));
				int width = Integer.valueOf(widthList.get(i));
				this.addView(ctv, new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT));
			}
			setEnabled(false);
		}
		else {
			int size = dataList.size();
			for (int i=0; i<size; i++) {
				CustomTextView ctv = new CustomTextView(context);
				ctv.setText(dataList.get(i));
				//ctv.setTextSize(DataView.getTextSize());
				ctv.setTypeface(DataView.getTextType());
				ctv.setGravity(Gravity.CENTER_VERTICAL);
				int width = Integer.valueOf(widthList.get(i));
				//this.addView(ctv, new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT));
				this.addView(ctv, new LinearLayout.LayoutParams(width, 50));
			}
			setEnabled(true);
		}
	}	
	
	public ArrayList<String> getDataList() {
		return dataList;
	}
	
	public boolean isHeader() {
		return isHeader;
	}
	
	public ArrayList<String> getWidthList() {
		return widthList;
	}
}
