package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.penbase.dma.Dalyo.Component.DalyoComponent;

public class DalyoCheckBox extends CheckBox implements DalyoComponent {

	public DalyoCheckBox(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getComponentLabel() {
		// TODO Auto-generated method stub
		return null;
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
}
