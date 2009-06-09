package com.penbase.dma.Dalyo.Component.Custom;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimeField extends Button implements OnClickListener {
	private int mHour;
	private int mMinute;
	private Context mContext;
	
	public TimeField(Context context, Typeface tf, float fs, String defaultValue) {
		super(context);
		this.mContext = context;
		if ((defaultValue != null) && (!defaultValue.equals(""))) {
			String displayTime = defaultValue.split(" ")[0];
			mHour = Integer.valueOf(displayTime.split(":")[0]);
			mMinute = Integer.valueOf(displayTime.split(":")[1]);
			this.setText(displayTime);
		} else {
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
		new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
                StringBuffer newTime = new StringBuffer(String.valueOf(mHour));
                newTime.append(":");
                newTime.append(mMinute);
				TimeField.this.setText(newTime.toString());
            }
        }, mHour, mMinute, false).show();
	}
	
	public String getTime() {
		StringBuffer time = new StringBuffer(String.valueOf(mHour));
		time.append(":");
		time.append(mMinute);
		return time.toString();
	}
	
	public void setTime(String time) {
		this.setText(time);
	}
}
