package com.penbase.dma.Dalyo.Component.Custom.Doodle;

import com.penbase.dma.Dalyo.Component.DalyoComponent;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

public class DalyoDoodle extends Button implements DalyoComponent {
	private String mImagePath;
	private Context mContext;
	private String mId;

	public DalyoDoodle(Context context, String i) {
		super(context);
		mContext = context;
		mId = i;
		mImagePath = null;
		setText("Doodle");
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(mContext, DoodleActivity.class);
				myIntent.putExtra("ID", mId);
				mContext.startActivity(myIntent);
			}
		});
	}

	public void setImagePath(String s) {
		mImagePath = s;
	}

	public String getImagePath() {
		return mImagePath;
	}

	@Override
	public String getComponentLabel() {
		return getText().toString();
	}

	@Override
	public Object getComponentValue() {
		return getImagePath();
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
		setBackgroundDrawable(null);
		setText("Doodle");
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
		if (value != null) {
			mImagePath = value.toString();
			Drawable d = Drawable.createFromPath(mImagePath);
			setBackgroundDrawable(d);
			setText(null);
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

	@Override
	public void setDatabase(String tid, String fid) {
		// TODO Auto-generated method stub
		
	}
}
