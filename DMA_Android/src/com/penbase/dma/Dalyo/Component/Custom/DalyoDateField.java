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
 * Displays a DatePickerDialog when it was clicked
 */
public class DalyoDateField extends Button implements OnClickListener,
		DalyoComponent {
	private DalyoDateTimePickerDialog mDateTimeDialog;
	private String mDefaultValue;

	public DalyoDateField(Context c, Typeface tf, float fs, String fc,
			boolean hasTime, String defaultValue) {
		super(c);
		mDateTimeDialog = new DalyoDateTimePickerDialog(c, this, true,
				hasTime);
		mDefaultValue = defaultValue;
		mDateTimeDialog.setInitialValues(defaultValue);
		this.setTypeface(tf);
		this.setTextSize(fs);
		if (fc != null) {
			this.setTextColor(Color.parseColor("#" + fc));
		}
		setText(mDateTimeDialog.getText());
		this.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		mDateTimeDialog.show();
	}

	public String getDate() {
		return getText().toString();
	}

	public void setDate(String date) {
		mDateTimeDialog.setInitialValues(date);
		this.setText(mDateTimeDialog.getText());
	}

	@Override
	public String getComponentLabel() {
		return mDateTimeDialog.getText();
	}

	@Override
	public Object getComponentValue() {
		return getDate();
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
		setDate(mDefaultValue);
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
		setDate(label);
	}

	@Override
	public void setComponentText(String text) {
		setDate(text);
	}

	@Override
	public void setComponentValue(Object value) {
		try {
			int seconds = Integer.parseInt(value.toString());
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(seconds * 1000);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			StringBuffer date = new StringBuffer();
			if (day == 0) {
				date.append("1");
			} else {
				date.append(day);
			}
			date.append("/").append(calendar.get(Calendar.MONTH) + 1);
			date.append("/").append(calendar.get(Calendar.YEAR));
			setDate(date.toString());
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
}
