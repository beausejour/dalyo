package com.penbase.dma.Dalyo.Component.Custom;

import java.util.Calendar;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeSetListener;

public class TimeField extends Button implements OnClickListener{
	private int hour;
	private int minute;
	private Context context;
	
	public TimeField(Context context) 
	{
		super(context);
		this.context = context;
		Calendar calendar = Calendar.getInstance();
		hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        String currentTime = hour+":"+minute;
        this.setText(currentTime);
		this.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) 
	{		
		new TimePickerDialog(context, new OnTimeSetListener()
		{
			@Override
			public void timeSet(TimePicker arg0, int arg1, int arg2) 
			{
				hour = arg1;
				minute = arg2;
				String newTime = hour+":"+minute;
				TimeField.this.setText(newTime);
			}					
		}, "Set the time", hour, minute, false).show();
	}

}
