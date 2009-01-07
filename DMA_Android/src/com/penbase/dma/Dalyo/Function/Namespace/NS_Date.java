package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Dalyo.Function.DateTime.Date;
import com.penbase.dma.Dalyo.Function.DateTime.Time;

public class NS_Date {
	public static Object CurrentDate() {
		return new Date();
	}
	
	public static Object CurrentHour() {
		return new Time();
	}
}
