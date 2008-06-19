package com.penbase.dma.Dalyo.Component.Custom;

import java.util.Calendar;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeSetListener;

public class TimeField extends Button implements OnClickListener {
	private int hour;
	private int minute;
	private Context context;
	
	public TimeField(Context context, Typeface tf, float fs, String defaultValue) {
		super(context);
		this.context = context;
		if ((defaultValue != null) && (!defaultValue.equals(""))) {
			String displayTime = defaultValue.split(" ")[0];
			Log.i("info", "displayTime.split(:)[0] "+displayTime.split(":")[0]);
			hour = Integer.valueOf(displayTime.split(":")[0]);
			minute = Integer.valueOf(displayTime.split(":")[1]);
			this.setText(displayTime);
		}
		else {
			Calendar calendar = Calendar.getInstance();
			hour = calendar.get(Calendar.HOUR);
			minute = calendar.get(Calendar.MINUTE);
		}
		this.setOnClickListener(this);
		this.setTypeface(tf);
		this.setTextSize(fs);
	}

	@Override
	public void onClick(View arg0)  {
		new TimePickerDialog(context, new OnTimeSetListener() {
			@Override
			public void timeSet(TimePicker arg0, int arg1, int arg2) {
				hour = arg1;
				minute = arg2;
				String newTime = hour+":"+minute;
				TimeField.this.setText(newTime);
			}
		}, "Set the time", hour, minute, false).show();
	}
	
	public String getTime(){
		String time = hour+":"+minute;
		return time;
	}
}
