package com.penbase.dma.Dalyo.Function.DateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Time {
	private int mDay;
	private int mMonth;
	private int mYear;
	private int mHour;
	private int mMinute;
	private int mSecond;
	
	public Time() {
		Calendar calendar = Calendar.getInstance();
		this.mHour = calendar.get(Calendar.HOUR_OF_DAY);
		this.mMinute = calendar.get(Calendar.MINUTE);
		this.mSecond = calendar.get(Calendar.SECOND);
		this.mDay = calendar.get(Calendar.DATE);
		this.mMonth = calendar.get(Calendar.MONTH)+1;
		this.mYear = calendar.get(Calendar.YEAR);
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer(String.valueOf(mHour));
		result.append(":").append(mMinute);
		result.append(":").append(mSecond);
		return result.toString();
	}
	
	public int toInt() {
		return new Long(new GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute, mSecond).getTimeInMillis()).intValue();
	}
}
