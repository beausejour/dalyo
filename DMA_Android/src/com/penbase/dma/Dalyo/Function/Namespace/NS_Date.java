package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.Calendar;

public class NS_Date {
	public static Object GetCurrentDate(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DATE)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR);
	}
}
