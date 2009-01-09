package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.Function.DateTime.Date;
import com.penbase.dma.Dalyo.Function.DateTime.Time;
import com.penbase.dma.View.ApplicationView;

public class NS_Object {
	public static boolean ToBoolean(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		return Boolean.valueOf(String.valueOf(value));
	}
	
	public static Integer ToInt(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value instanceof Date) {
			return ((Date) value).toInt();
		}
		else if (value instanceof Time) {
			return ((Time) value).toInt();
		}
		else if (String.valueOf(value).indexOf(".") != -1) {
			return Double.valueOf(String.valueOf(value)).intValue();
		}
		else {
			Integer result = null;
			try {
				result = Integer.valueOf(String.valueOf(value));
			}
			catch (NumberFormatException nfe) {
				ApplicationView.errorDialog("Check your variable's type (ToInt) !");
			}
			return result;
		}
	}
	
	public static Number ToNumeric(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value instanceof Date) {
			return ((Date) value).toInt();
		}
		else if (value instanceof Time) {
			return ((Time) value).toInt();
		}
		else if (String.valueOf(value).indexOf(".") != -1) {
			return Double.valueOf(String.valueOf(value));
		}
		else {
			Integer result = null;
			try {
				result = Integer.valueOf(String.valueOf(value));
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
		if (value instanceof Date) {
			return ((Date) value).toString();
		}
		else if (value instanceof Time) {
			return ((Time) value).toString();
		}
		else {
			return String.valueOf(value);
		}
	}
}
