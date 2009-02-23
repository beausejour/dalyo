package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.ArrayList;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.Function.DateTime.DalyoDate;
import com.penbase.dma.Dalyo.Function.DateTime.Time;

public class NS_List {
	@SuppressWarnings("unchecked")
	public static void AddValue(Element element) {
		String listName = Function.getVariableName(element, ScriptTag.PARAMETER, ScriptAttribute.LIST, ScriptAttribute.LIST).toString();
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value instanceof DalyoDate) {
			((ArrayList<Object>)Function.getVariablesMap().get(listName)).add(((DalyoDate)value).toString());
		}
		else if (value instanceof Time) {
			((ArrayList<Object>)Function.getVariablesMap().get(listName)).add(((Time)value).toString());
		}
		else {
			((ArrayList<Object>)Function.getVariablesMap().get(listName)).add(value);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void Clear(Element element) {
		String listName = Function.getVariableName(element, ScriptTag.PARAMETER, ScriptAttribute.LIST, ScriptAttribute.LIST).toString();
		((ArrayList<Object>)Function.getVariablesMap().get(listName)).clear();
	}
	
	public static Object Get(Element element) {
		Object value = null;
		Object list = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.LIST, ScriptAttribute.LIST);
		int index = -1;
		if (Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_INDEX, ScriptAttribute.PARAMETER_TYPE_INT) != null) {
			index = Integer.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_INDEX, ScriptAttribute.PARAMETER_TYPE_INT).toString());
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
