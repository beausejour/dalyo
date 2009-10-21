package com.penbase.dma.Dalyo.Component.Custom.Doodle;

import com.penbase.dma.Dalyo.Component.DalyoComponent;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class DalyoDoodle extends Button implements DalyoComponent {
	private String mImageName;
	private Context mContext;
	private String mId;

	public DalyoDoodle(Context c, String i) {
		super(c);
		mContext = c;
		mId = i;
		mImageName = "";
		setText("Open Doodle");
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(mContext, DoodleActivity.class);
				myIntent.putExtra("ID", mId);
				mContext.startActivity(myIntent);
			}
		});
	}

	public void setImageName(String s) {
		mImageName = s;
	}

	public String getImageName() {
		return mImageName;
	}

	@Override
	public String getComponentLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getComponentValue() {
		return getImageName();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOnClickEvent(String functionName) {
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
}