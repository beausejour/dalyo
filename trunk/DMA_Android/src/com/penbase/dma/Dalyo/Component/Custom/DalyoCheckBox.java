package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.penbase.dma.Dalyo.Component.DalyoComponent;
import com.penbase.dma.Dalyo.Function.Function;

public class DalyoCheckBox extends CheckBox implements DalyoComponent {

	public DalyoCheckBox(Context context) {
		super(context);
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
	public void setOnChangeEvent(final String functionName) {
		setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Function.createFunction(functionName);
			}
		});
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
}
