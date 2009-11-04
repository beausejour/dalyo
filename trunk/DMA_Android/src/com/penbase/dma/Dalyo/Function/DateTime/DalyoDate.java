package com.penbase.dma.Dalyo.Function.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.util.MonthDisplayHelper;

import com.penbase.dma.View.ApplicationView;

public class DalyoDate {
	private int mDay;
	private int mMonth;
	private int mYear;
	private int mHour;
	private int mMinute;
	private int mSecond;
	private Calendar mCalendar;
	private Locale mLocale;

	public DalyoDate() {
		mLocale = ApplicationView.getCurrentView().getResources().getConfiguration().locale;
		mCalendar = GregorianCalendar.getInstance(mLocale);
		refreshCalendar();
	}
	
	public DalyoDate(int year, int month, int day) {
		mLocale = ApplicationView.getCurrentView().getResources().getConfiguration().locale;
		mCalendar = GregorianCalendar.getInstance(mLocale);
		mCalendar.set(Calendar.YEAR, year);
		mCalendar.set(Calendar.MONTH, month - 1);
		mCalendar.set(Calendar.DAY_OF_MONTH, day);
		refreshCalendar();
	}
	
	public DalyoDate(int year, int month, int day, int hours,
			int minutes, int seconds) {
		mLocale = ApplicationView.getCurrentView().getResources().getConfiguration().locale;
		mCalendar = GregorianCalendar.getInstance(mLocale);
		mCalendar.set(Calendar.YEAR, year);
		mCalendar.set(Calendar.MONTH, month - 1);
		mCalendar.set(Calendar.DAY_OF_MONTH, day);
		mCalendar.set(Calendar.HOUR_OF_DAY, hours);
		mCalendar.set(Calendar.MINUTE, minutes);
		mCalendar.set(Calendar.SECOND, seconds);
		refreshCalendar();
	}

	public Date toDate() {
		return new Date(this.toLong());
	}

	public String toString() {
		StringBuffer result = new StringBuffer(String.valueOf(mDay));
		result.append("/").append(mMonth);
		result.append("/").append(mYear);
		result.append(" ").append(mHour);
		result.append(":").append(mMinute);
		result.append(":").append(mSecond);
		return result.toString();
	}

	public int toInt() {
		return new Long(this.toLong()).intValue();
	}

	private long toLong() {
		return new GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute,
				mSecond).getTimeInMillis();
	}

	public DalyoDate addYears(int years) {
		mCalendar.add(Calendar.YEAR, years);
		refreshCalendar();
		return this;
	}

	public DalyoDate addMonths(int months) {
		mCalendar.add(Calendar.MONTH, months);
		refreshCalendar();
		return this;
	}

	public DalyoDate addDays(int days) {
		mCalendar.add(Calendar.DAY_OF_MONTH, days);
		refreshCalendar();
		return this;
	}

	public DalyoDate addHours(int hours) {
		mCalendar.add(Calendar.HOUR_OF_DAY, hours);
		refreshCalendar();
		return this;
	}

	public DalyoDate addMinutes(int minutes) {
		mCalendar.add(Calendar.MINUTE, minutes);
		refreshCalendar();
		return this;
	}
	
	public int currentDayInMonth() {
		return mDay;
	}
	
	public int currentMonth() {
		return mMonth;
	}
	
	public int currentYear() {
		return mYear;
	}
	
	public String getDayName() {
		String dayName = "EEEE";
		SimpleDateFormat sdf = new SimpleDateFormat(dayName);
		return sdf.format(mCalendar.getTime());
	}
	
	public Date getDate() {
		return mCalendar.getTime();
	}

	public static ArrayList<DalyoDate> getDaysInMonth(int year, int month) {
		MonthDisplayHelper mdh = new MonthDisplayHelper(year, month - 1);
		int days = mdh.getNumberOfDaysInMonth();
		ArrayList<DalyoDate> result = new ArrayList<DalyoDate>();
		for (int i=0; i<days; i++) {
			result.add(new DalyoDate(year, month, i + 1));
		}
		return result;
	}
	
	private void refreshCalendar() {
		this.mDay = mCalendar.get(Calendar.DATE);
		this.mMonth = mCalendar.get(Calendar.MONTH) + 1;
		this.mYear = mCalendar.get(Calendar.YEAR);
		this.mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		this.mMinute = mCalendar.get(Calendar.MINUTE);
		this.mSecond = mCalendar.get(Calendar.SECOND);
	}
}
