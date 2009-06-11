package com.penbase.dma.Dalyo.Component.Custom;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Displays a DatePickerDialog when it was clicked
 */
public class DateField extends Button implements OnClickListener{
	private int mYear;
	private int mMonth;
	private int mDay;
	private Context mContext;
	
	public DateField(Context c, Typeface tf, float fs, String defaultValue) {
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
				DateField.this.setText(newDate.toString());
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
}
