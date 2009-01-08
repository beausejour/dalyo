package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.Function.DateTime.Date;
import com.penbase.dma.Dalyo.Function.DateTime.Time;

public class NS_Date {
	public static Object AddMinutes(Element element) {
		Object date = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.DATE, ScriptAttribute.DATE);
		int minutes = Integer.valueOf(String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_MINUTES, ScriptAttribute.PARAMETER_TYPE_INT)));
		return ((Date)date).addMinutes(minutes);
	}
	
	public static Object CurrentDate() {
		return new Date();
	}
	
	public static Object CurrentHour() {
		return new Time();
	}
}
