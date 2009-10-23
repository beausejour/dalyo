package com.penbase.dma.Dalyo.Component.Custom;

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

	public DalyoDateField(Context c, Typeface tf, float fs, String fc,
			boolean hasTime, String defaultValue) {
		super(c);
		mDateTimeDialog = new DalyoDateTimePickerDialog(c, this, true,
				hasTime);
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
		this.setText(date);
		mDateTimeDialog.setInitialValues(date);
	}

	@Override
	public String getComponentLabel() {
		// TODO Auto-generated method stub
		return null;
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
		
	}

	@Override
	public void setComponentText(String text) {
		setDate(text);
	}

	@Override
	public void setComponentValue(Object value) {
		setDate(value.toString());
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
