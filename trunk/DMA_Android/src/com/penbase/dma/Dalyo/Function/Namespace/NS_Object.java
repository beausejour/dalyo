package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.Date;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.Function.DateTime.DalyoDate;
import com.penbase.dma.Dalyo.Function.DateTime.Time;
import com.penbase.dma.View.ApplicationView;

public class NS_Object {
	public static boolean ToBoolean(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		return Boolean.valueOf(value.toString());
	}
	
	public static Date ToDate(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		return ((DalyoDate)value).toDate();
	}
	
	public static Integer ToInt(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value instanceof DalyoDate) {
			return ((DalyoDate) value).toInt();
		}
		else if (value instanceof Time) {
			return ((Time) value).toInt();
		}
		else if (value.toString().indexOf(".") != -1) {
			return Double.valueOf(value.toString()).intValue();
		}
		else {
			Integer result = null;
			try {
				result = Integer.valueOf(value.toString());
			}
			catch (NumberFormatException nfe) {
				ApplicationView.errorDialog("Check your variable's type (ToInt) !");
			}
			return result;
		}
	}
	
	public static Number ToNumeric(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value instanceof DalyoDate) {
			return ((DalyoDate) value).toInt();
		}
		else if (value instanceof Time) {
			return ((Time) value).toInt();
		}
		else if (value.toString().indexOf(".") != -1) {
			return Double.valueOf(value.toString());
		}
		else {
			Integer result = null;
			try {
				result = Integer.valueOf(value.toString());
			}
			catch (NumberFormatException nfe) {
				ApplicationView.errorDialog("Check your variable's type (ToNumeric) !");
			}
			return result;
		}
	}
	
	public static Object ToRecord(Element element) {
		return Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
	}
	
	public static String ToString(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value instanceof DalyoDate) {
			return ((DalyoDate) value).toString();
		}
		else if (value instanceof Time) {
			return ((Time) value).toString();
		}
		else if (value == null) {
			return "";
		}
		else {
			return value.toString();
		}
	}
}
