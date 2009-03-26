package com.penbase.dma.Dalyo.Function.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DalyoDate {
	private int mDay;
	private int mMonth;
	private int mYear;
	private int mHour;
	private int mMinute;
	private int mSecond;
	private Calendar mCalendar;
	
	public DalyoDate() {
		mCalendar = GregorianCalendar.getInstance();
		this.mDay = mCalendar.get(Calendar.DATE);
		this.mMonth = mCalendar.get(Calendar.MONTH)+1;
		this.mYear = mCalendar.get(Calendar.YEAR);
		this.mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		this.mMinute = mCalendar.get(Calendar.MINUTE);
		this.mSecond = mCalendar.get(Calendar.SECOND);
	}
	
	public Date toDate() {
		return new Date(this.toLong());
	}
	
	public String toString() {
		return mDay+"/"+mMonth+"/"+mYear+" "+mHour+":"+mMinute+":"+mSecond;
	}
	
	public int toInt() {
		return new Long(this.toLong()).intValue();
	}
	
	private long toLong() {
		return new GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute, mSecond).getTimeInMillis();
	}
	
	public DalyoDate addMinutes(int minutes) {
		mCalendar.add(Calendar.MINUTE, minutes);
		this.mMinute = mCalendar.get(Calendar.MINUTE);
		return this;
	}
}
