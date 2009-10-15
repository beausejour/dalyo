package com.penbase.dma.Dalyo.Component.Custom;

import com.penbase.dma.Dalyo.Component.DalyoComponent;
import com.penbase.dma.Dalyo.Function.Function;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

public class DalyoButton extends Button implements DalyoComponent {

	public DalyoButton(Context context) {
		super(context);
	}

	public void setBackgroundImageByPath(String path) {
		if (path.length() > 0) {
			Drawable drawable = Drawable.createFromPath(path);
			setBackgroundDrawable(drawable);
			setText(null);
		}
	}
	
	@Override
	public String getComponentLabel() {
		return getText().toString();
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
		setText(text);
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
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Function.createFunction(functionName);
			}
		});
	}

	@Override
	public void setOnChangeEvent(String functionName) {
		// TODO Auto-generated method stub
		
	}
}
