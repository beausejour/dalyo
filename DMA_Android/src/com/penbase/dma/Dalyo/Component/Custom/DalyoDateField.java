 package com.penbase.dma.Dalyo.Component.Custom;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

import com.penbase.dma.Dalyo.Component.DalyoComponent;

/**
 * Displays a DatePickerDialog when it was clicked
 */
public class DalyoDateField extends Button implements OnClickListener, DalyoComponent {
	private int mYear;
	private int mMonth;
	private int mDay;
	private Context mContext;
	
	public DalyoDateField(Context c, Typeface tf, float fs, String defaultValue) {
		super(c);
		this.mContext = c;
		if ((defaultValue != null) && (!defaultValue.equals(""))) {
			String displayDate = defaultValue.split(" ")[0];
			mYear = Integer.valueOf(displayDate.split("/")[2]);
			mMonth = Integer.valueOf(displayDate.split("/")[1]);
			mDay = Integer.valueOf(displayDate.split("/")[0]);
			this.setText(displayDate);
		} else {
			Calendar calendar = Calendar.getInstance();
			mYear = calendar.get(Calendar.YEAR);
			mMonth = calendar.get(Calendar.MONTH);
			mDay = calendar.get(Calendar.DAY_OF_MONTH);
		}
		this.setTypeface(tf);
		this.setTextSize(fs);
		this.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear + 1;
                mDay = dayOfMonth;
                StringBuffer newDate = new StringBuffer(String.valueOf(mDay));
                newDate.append("/");
                newDate.append(mMonth);
                newDate.append("/");
                newDate.append(mYear);
				DalyoDateField.this.setText(newDate.toString());
            }
        }, mYear, mMonth - 1, mDay).show();
	}
	
	public String getDate() {
		StringBuffer date = new StringBuffer(String.valueOf(mDay));
		date.append("/");
		date.append(mMonth);
		date.append("/");
		date.append(mYear);
		return date.toString();
	}
	
	public void setDate(String date) {
		this.setText(date);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setComponentLabel(String label) {
		requestFocus();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOnClickEvent(String functionName) {
		// TODO Auto-generated method stub
		
	}
}
