package com.penbase.dma.Dalyo.Function.DateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Date {
	private int day;
	private int month;
	private int year;
	private int hour;
	private int minute;
	private int second;
	private Calendar calendar;
	
	public Date() {
		calendar = Calendar.getInstance();
		this.day = calendar.get(Calendar.DATE);
		this.month = calendar.get(Calendar.MONTH)+1;
		this.year = calendar.get(Calendar.YEAR);
		this.hour = calendar.get(Calendar.HOUR_OF_DAY);
		this.minute = calendar.get(Calendar.MINUTE);
		this.second = calendar.get(Calendar.SECOND);
	}
	
	public String toString() {
		return day+"/"+month+"/"+year+" "+hour+":"+minute+":"+second;
	}
	
	public int toInt() {
		return new Long(new GregorianCalendar(year, month, day, hour, minute, second).getTimeInMillis()).intValue();
	}
	
	public Date addMinutes(int minutes) {
		calendar.add(Calendar.MINUTE, minutes);
		this.minute = calendar.get(Calendar.MINUTE);
		return this;
	}
}
