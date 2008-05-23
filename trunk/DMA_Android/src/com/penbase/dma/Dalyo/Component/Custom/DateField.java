package com.penbase.dma.Dalyo.Component.Custom;

import java.util.Calendar;
import android.app.DatePickerDialog;
import android.content.Context;
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
	
	public DateField(Context c) 
	{
		super(c);
		this.context = c;
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = day+"/"+month+"/"+year;
        this.setText(currentDate);

        this.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) 
	{
		 new DatePickerDialog(context, new OnDateSetListener()
		 {
			@Override
			public void dateSet(DatePicker arg0, int arg1, int arg2, int arg3) 
			{
				year = arg1;
				month = arg2+1;
				day = arg3;
				String newDate = day+"/"+month+"/"+year;
				Log.i("info", "current month "+month);
				DateField.this.setText(newDate);
			}
		 }, year, month, day, Calendar.MONDAY).show();	
	}
}
