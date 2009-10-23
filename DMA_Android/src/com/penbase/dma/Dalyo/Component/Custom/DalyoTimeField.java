package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.penbase.dma.Dalyo.Component.DalyoComponent;

public class DalyoTimeField extends Button implements DalyoComponent, OnClickListener {
	private DalyoDateTimePickerDialog mDateTimeDialog;
	
	public DalyoTimeField(Context context, Typeface tf, float fs, String fc, String defaultValue) {
		super(context);
		mDateTimeDialog = new DalyoDateTimePickerDialog(context, this, false,
				true);
		mDateTimeDialog.setInitialValues(defaultValue);
		this.setOnClickListener(this);
		this.setTypeface(tf);
		this.setTextSize(fs);
		if (fc != null) {
			this.setTextColor(Color.parseColor("#" + fc));
		}
		setText(mDateTimeDialog.getText());
	}

	@Override
	public void onClick(View arg0)  {
		mDateTimeDialog.show();
	}
	
	public String getTime() {
		return getText().toString();
	}
	
	public void setTime(String time) {
		this.setText(time);
		mDateTimeDialog.setInitialValues(time);
	}

	@Override
	public String getComponentLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getComponentValue() {
		return getTime();
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
		setTime(text);
	}

	@Override
	public void setComponentValue(Object value) {
		setTime(value.toString());
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
		mDateTimeDialog.setOnChangeFunction(functionName);
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
