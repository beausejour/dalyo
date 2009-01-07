package com.penbase.dma.Dalyo.Function.DateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Date {
	private int day;
	private int month;
	private int year;
	
	public Date() {
		Calendar calendar = Calendar.getInstance();
		this.day = calendar.get(Calendar.DATE);
		this.month = calendar.get(Calendar.MONTH)+1;
		this.year = calendar.get(Calendar.YEAR);
	}
	
	public String toString() {
		return day+"/"+month+"/"+year;
	}
	
	public int toInt() {
		return new Long(new GregorianCalendar(year, month, day).getTimeInMillis()).intValue();
	}
}
