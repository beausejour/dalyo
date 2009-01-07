package com.penbase.dma.Dalyo.Component.Custom;

import java.util.Calendar;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;

public class DateField extends Button implements OnClickListener{
	private int mYear;
	private int mMonth;
	private int mDay;
	private Context context;
	
	public DateField(Context c, Typeface tf, float fs, String defaultValue){
		super(c);
		this.context = c;
		if ((defaultValue != null) && (!defaultValue.equals(""))) {
			String displayDate = defaultValue.split(" ")[0];
			mYear = Integer.valueOf(displayDate.split("/")[2]);
			mMonth = Integer.valueOf(displayDate.split("/")[1]) - 1;
			mDay = Integer.valueOf(displayDate.split("/")[0]);
			this.setText(displayDate);
		}
		else
		{
			Calendar calendar = Calendar.getInstance();
			mYear = calendar.get(Calendar.YEAR);
			mMonth = calendar.get(Calendar.MONTH) - 1;
			mDay = calendar.get(Calendar.DAY_OF_MONTH);
		}
		this.setTypeface(tf);
		this.setTextSize(fs);
		this.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0){
		new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear + 1;
                mDay = dayOfMonth;
                String newDate = mDay+"/"+mMonth+"/"+mYear;
				Log.i("info", "current month "+mMonth);
				DateField.this.setText(newDate);
            }
        }, mYear, mMonth, mDay).show();
	}
	
	public String getDate(){
		String date = mDay+"/"+mMonth+"/"+mYear;
		return date;
	}
	
	public void setDate(String date){
		this.setText(date);
	}
}
