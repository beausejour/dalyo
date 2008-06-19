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
import android.widget.DatePicker.OnDateSetListener;

public class DateField extends Button implements OnClickListener{
	private int year;
	private int month;
	private int day;
	private Context context;
	
	public DateField(Context c, Typeface tf, float fs, String defaultValue){
		super(c);
		this.context = c;
		if ((defaultValue != null) && (!defaultValue.equals(""))) {
			String displayDate = defaultValue.split(" ")[0];
			year = Integer.valueOf(displayDate.split("/")[2]);
			month = Integer.valueOf(displayDate.split("/")[1]) - 1;
			day = Integer.valueOf(displayDate.split("/")[0]);
			this.setText(displayDate);
		}
		else
		{
			Calendar calendar = Calendar.getInstance();
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH) - 1;
			day = calendar.get(Calendar.DAY_OF_MONTH);
		}
		this.setTypeface(tf);
		this.setTextSize(fs);
		this.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0){
		 new DatePickerDialog(context, new OnDateSetListener(){
			@Override
			public void dateSet(DatePicker arg0, int arg1, int arg2, int arg3){
				year = arg1;
				month = arg2+1;
				day = arg3;
				String newDate = day+"/"+month+"/"+year;
				Log.i("info", "current month "+month);
				DateField.this.setText(newDate);
			}
		 }, year, month, day, Calendar.MONDAY).show();	
	}
	
	public String getDate(){
		String date = day+"/"+month+"/"+year;
		return date;
	}
}
