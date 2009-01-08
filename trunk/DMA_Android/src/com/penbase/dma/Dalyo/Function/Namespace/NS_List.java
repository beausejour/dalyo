package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.ArrayList;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.Function.DateTime.Date;
import com.penbase.dma.Dalyo.Function.DateTime.Time;

public class NS_List {
	public static void AddValue(Element element) {
		String listName = String.valueOf(Function.getVariableName(element, ScriptTag.PARAMETER, ScriptAttribute.LIST, ScriptAttribute.LIST));
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value instanceof Date) {
			Function.addVariableValue(listName, ((Date)value).toString(), true);
		}
		else if (value instanceof Time) {
			Function.addVariableValue(listName, ((Time)value).toString(), true);
		}
		else {
			Function.addVariableValue(listName, value, true);
		}
	}
	
	public static void Clear(Element element) {
		String listName = String.valueOf(Function.getVariableName(element, ScriptTag.PARAMETER, ScriptAttribute.LIST, ScriptAttribute.LIST));
		Function.clearListItems(listName);
	}
	
	public static Object Get(Element element) {
		Object value = null;
		Object list = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.LIST, ScriptAttribute.LIST);
		int index = -1;
		if (Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_INDEX, ScriptAttribute.PARAMETER_TYPE_INT) != null) {
			index = Integer.valueOf(String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_INDEX, ScriptAttribute.PARAMETER_TYPE_INT)));
		}
		if ((index != -1) && (((ArrayList<?>) list).size() > 0)) {
			value = ((ArrayList<?>)list).get(index-1);
		}
		return value;
	}
	
	public static int Size(Element element) {
		int value = 0;
		Object list = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.LIST, ScriptAttribute.LIST);
		if (list != null) {
			value = ((ArrayList<?>)list).size();
		}
		return value;
	}	
}
