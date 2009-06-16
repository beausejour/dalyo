package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;

public class NS_String {
	public static String Concat(Element element) {
		StringBuffer result = new StringBuffer(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_A, ScriptAttribute.STRING).toString());
		result.append(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_B, ScriptAttribute.STRING).toString());
		return result.toString();
	}
	
	public static ArrayList<String> Explode(Element element) {
		String value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.STRING, ScriptAttribute.STRING).toString();
		String delimiter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_DELIMITER, ScriptAttribute.STRING).toString();
		return new ArrayList<String>(Arrays.asList(value.split(delimiter)));
	}
	
	@SuppressWarnings("unchecked")
	public static String Implode(Element element) {
		ArrayList<Object> list = (ArrayList<Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.LIST, ScriptAttribute.LIST);
		String glue = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_GLUE, ScriptAttribute.STRING).toString();
		StringBuffer stringBuffer = new StringBuffer("");
		int listSize = list.size();
		for (int i=0; i<listSize; i++) {
			stringBuffer.append(list.get(i).toString());
			if (i != listSize - 1) {
				stringBuffer.append(glue);
			}
		}
		return stringBuffer.toString();
	}
	
	public static int Indexof(Element element) {
		String value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.STRING, ScriptAttribute.STRING).toString();
		String token = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TOKEN, ScriptAttribute.STRING).toString();
		return value.indexOf(token) + 1;
	}
	
	public static int Length(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.STRING, ScriptAttribute.STRING);
		if (value == null) {
			return 0;
		} else {
			return value.toString().length();
		}
	}
	
	public static String Replace(Element element) {
		String source = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_SOURCE, ScriptAttribute.STRING).toString();
		String search = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_SEARCH, ScriptAttribute.STRING).toString();
		String replacement = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_REPLACEMENT, ScriptAttribute.STRING).toString();
		return source.replace(search, replacement);
	}
	
	public static String Substring(Element element) {
		String value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.STRING, ScriptAttribute.STRING).toString();
		int start = Integer.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_START, ScriptAttribute.PARAMETER_TYPE_INT).toString());
		int end = Integer.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_END, ScriptAttribute.PARAMETER_TYPE_INT).toString());
		return value.substring(start - 1, end - 1);
	}
	
	public static String ToLower(Element element) {
		String value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.STRING, ScriptAttribute.STRING).toString();
		return value.toLowerCase();
	}
	
	public static String ToUpper(Element element) {
		String value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.STRING, ScriptAttribute.STRING).toString();
		return value.toUpperCase();
	}
	
	public static String Trim(Element element) {
		String value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.STRING, ScriptAttribute.STRING).toString();
		return value.trim();
	}
}