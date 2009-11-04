package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.Function.DateTime.DalyoDate;
import com.penbase.dma.Dalyo.Function.DateTime.Time;

import org.w3c.dom.Element;

import java.text.SimpleDateFormat;

public class NS_Date {
	public static Object AddDays(Element element) {
		Object date = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.DATE, ScriptAttribute.DATE);
		int days = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_DAYS,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		return ((DalyoDate) date).addDays(days);
	}
	
	public static Object AddHours(Element element) {
		Object date = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.DATE, ScriptAttribute.DATE);
		int hours = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_HOURS,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		return ((DalyoDate) date).addHours(hours);
	}

	public static Object AddMinutes(Element element) {
		Object date = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.DATE, ScriptAttribute.DATE);
		int minutes = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_MINUTES,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		return ((DalyoDate) date).addMinutes(minutes);
	}
	
	public static Object AddMonths(Element element) {
		Object date = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.DATE, ScriptAttribute.DATE);
		int months = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_MONTHS,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		return ((DalyoDate) date).addMonths(months);
	}

	public static Object AddYears(Element element) {
		Object date = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.DATE, ScriptAttribute.DATE);
		int years = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_YEARS,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		return ((DalyoDate) date).addYears(years);
	}
	
	public static Object CreateDate(Element element) {
		int year = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_YEAR,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		int month = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_MONTH,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		int day = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_DAY,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		int hours = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_HOURS,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		int minutes = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_MINUTES,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		int seconds = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_SECONDS,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		return new DalyoDate(year, month, day, hours, minutes, seconds);
	}
	
	public static Object CurrentDate() {
		return new DalyoDate();
	}
	
	public static int CurrentDayInMonth() {
		return new DalyoDate().currentDayInMonth();
	}

	public static Object CurrentHour() {
		return new Time();
	}
	
	public static int CurrentMonth() {
		return new DalyoDate().currentMonth();
	}
	
	public static int CurrentYear() {
		return new DalyoDate().currentYear();
	}

	public static String Format(Element element) {
		Object date = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.DATE, ScriptAttribute.DATE);
		String format = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_FORMAT, ScriptAttribute.STRING)
				.toString();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(((DalyoDate)date).getDate());
	}
	
	public static Object GetDayName(Element element) {
		Object date = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.DATE, ScriptAttribute.DATE);
		return ((DalyoDate) date).getDayName();
	}
	
	public static Object GetDaysInMonth(Element element) {
		int year = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_YEAR,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		int month = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_MONTH,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		return DalyoDate.getDaysInMonth(year, month);
	}
}
