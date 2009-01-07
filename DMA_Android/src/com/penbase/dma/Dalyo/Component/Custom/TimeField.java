package com.penbase.dma.Dalyo.Component.Custom;

import java.util.Calendar;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;

public class TimeField extends Button implements OnClickListener {
	private int mHour;
	private int mMinute;
	private Context context;
	
	public TimeField(Context context, Typeface tf, float fs, String defaultValue) {
		super(context);
		this.context = context;
		if ((defaultValue != null) && (!defaultValue.equals(""))) {
			String displayTime = defaultValue.split(" ")[0];
			mHour = Integer.valueOf(displayTime.split(":")[0]);
			mMinute = Integer.valueOf(displayTime.split(":")[1]);
			this.setText(displayTime);
		}
		else {
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
		new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
                String newTime = mHour+":"+mMinute;
				TimeField.this.setText(newTime);
            }
        }, mHour, mMinute, false).show();
	}
	
	public String getTime(){
		String time = mHour+":"+mMinute;
		return time;
	}
	
	public void setTime(String time) {
		this.setText(time);
	}
}
