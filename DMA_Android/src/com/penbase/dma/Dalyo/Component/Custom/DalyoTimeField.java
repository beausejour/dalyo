package com.penbase.dma.Dalyo.Component.Custom;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

import com.penbase.dma.Dalyo.Component.DalyoComponent;

public class DalyoTimeField extends Button implements DalyoComponent, OnClickListener {
	private int mHour;
	private int mMinute;
	private Context mContext;
	
	public DalyoTimeField(Context context, Typeface tf, float fs, String defaultValue) {
		super(context);
		this.mContext = context;
		if ((defaultValue != null) && (!defaultValue.equals(""))) {
			String displayTime = defaultValue.split(" ")[0];
			mHour = Integer.valueOf(displayTime.split(":")[0]);
			mMinute = Integer.valueOf(displayTime.split(":")[1]);
			this.setText(displayTime);
		} else {
			Calendar calendar = Calendar.getInstance();
			mHour = calendar.get(Calendar.HOUR);
			mMinute = calendar.get(Calendar.MINUTE);
		}
		this.setOnClickListener(this);
		this.setTypeface(tf);
		this.setTextSize(fs);
	}

	@Override
	public void onClick(View arg0)  {
		new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
                StringBuffer newTime = new StringBuffer(String.valueOf(mHour));
                newTime.append(":");
                newTime.append(mMinute);
				DalyoTimeField.this.setText(newTime.toString());
            }
        }, mHour, mMinute, false).show();
	}
	
	public String getTime() {
		StringBuffer time = new StringBuffer(String.valueOf(mHour));
		time.append(":");
		time.append(mMinute);
		return time.toString();
	}
	
	public void setTime(String time) {
		this.setText(time);
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
