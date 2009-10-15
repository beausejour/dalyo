package com.penbase.dma.Dalyo.Component.Custom;

import com.penbase.dma.Dalyo.Component.DalyoComponent;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

public class DalyoLabel extends TextView implements DalyoComponent {
	public DalyoLabel(Context context, Typeface tf, float fs) {
		super(context);
		this.setTypeface(tf);
		this.setTextSize(fs);
		this.setTextColor(Color.BLACK);
	}

	@Override
	public String getComponentLabel() {
		return getText().toString();
	}

	@Override
	public Object getComponentValue() {
		return getText();
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
		setText(value.toString());
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
}
