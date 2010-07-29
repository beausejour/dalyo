package com.penbase.dma.Dalyo.Component.Custom;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.penbase.dma.Dalyo.Component.DalyoComponent;

/**
 * Timefield object
 */
public class DalyoTimeField extends Button implements DalyoComponent, OnClickListener {
	private DalyoDateTimePickerDialog mDateTimeDialog;
	private String mDefaultValue;
	
	public DalyoTimeField(Context context, Typeface tf, float fs, String fc, String defaultValue) {
		super(context);
		mDateTimeDialog = new DalyoDateTimePickerDialog(context, this, false,
				true);
		mDefaultValue = defaultValue;
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
		mDateTimeDialog.setInitialValues(time);
		this.setText(mDateTimeDialog.getText());
	}

	@Override
	public String getComponentLabel() {
		return mDateTimeDialog.getText();
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
		setTime(mDefaultValue);
		mDateTimeDialog.initialMaxCalendar();
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
		setTime(label);
	}

	@Override
	public void setComponentText(String text) {
		setTime(text);
	}

	@Override
	public void setComponentValue(Object value) {
		try {
			int seconds = Integer.parseInt(value.toString());
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(seconds * 1000);
			int hour = calendar.get(Calendar.HOUR_OF_DAY) - 1;
			int minute = calendar.get(Calendar.MINUTE);
			StringBuffer time = new StringBuffer(String.valueOf(hour));
			time.append(":").append(minute);
			setTime(time.toString());
			mDateTimeDialog.initialMaxCalendar();
		} catch (NumberFormatException nfe) {
			
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

	@Override
	public void setDatabase(String tid, String fid) {
		// TODO Auto-generated method stub
		
	}
}
