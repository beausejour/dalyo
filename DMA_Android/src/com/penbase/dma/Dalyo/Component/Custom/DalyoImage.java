package com.penbase.dma.Dalyo.Component.Custom;

import com.penbase.dma.Dalyo.Component.DalyoComponent;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

/**
 * Image object
 */
public class DalyoImage extends ImageView implements DalyoComponent {
	private String mImagePath;

	public DalyoImage(Context context) {
		super(context);
	}

	public void setInitialImage(String path) {
		mImagePath = path;
		setImageDrawable(Drawable.createFromPath(path));
	}
	
	@Override
	public void setComponentValue(Object value) {
		if (value != null) {
			mImagePath = value.toString();
			Drawable d = Drawable.createFromPath(mImagePath);
			setImageDrawable(d);	
		}
	}

	@Override
	public String getComponentLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getComponentValue() {
		return mImagePath;
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
		setImageDrawable(Drawable.createFromPath(mImagePath));
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
