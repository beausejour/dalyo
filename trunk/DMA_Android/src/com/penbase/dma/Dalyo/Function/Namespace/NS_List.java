package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.Function.DateTime.DalyoDate;
import com.penbase.dma.Dalyo.Function.DateTime.Time;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class NS_List {
	@SuppressWarnings("unchecked")
	public static void AddValue(Element element) {
		String listName = Function
				.getVariableName(element, ScriptTag.PARAMETER,
						ScriptAttribute.LIST, ScriptAttribute.LIST).toString();
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		ArrayList<Object> objectList = ((ArrayList<Object>) Function
				.getVariablesMap().get(listName));
		if (value instanceof DalyoDate) {
			objectList.add(((DalyoDate) value).toString());
		} else if (value instanceof Time) {
			objectList.add(((Time) value).toString());
		} else {
			objectList.add(value);
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean Contains(Element element) {
		String listName = Function
				.getVariableName(element, ScriptTag.PARAMETER,
						ScriptAttribute.LIST, ScriptAttribute.LIST).toString();
		Object object = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.OBJECT, ScriptAttribute.OBJECT);
		return ((ArrayList<Object>) Function.getVariablesMap().get(listName))
				.contains(object);
	}

	@SuppressWarnings("unchecked")
	public static void Clear(Element element) {
		String listName = Function
				.getVariableName(element, ScriptTag.PARAMETER,
						ScriptAttribute.LIST, ScriptAttribute.LIST).toString();
		((ArrayList<Object>) Function.getVariablesMap().get(listName)).clear();
	}

	public static Object Get(Element element) {
		Object value = null;
		Object list = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.LIST, ScriptAttribute.LIST);
		int index = -1;
		Object indexObject = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_INDEX,
				ScriptAttribute.PARAMETER_TYPE_INT);
		if (indexObject != null) {
			index = Integer.valueOf(indexObject.toString());
		}
		if ((index != -1) && (((ArrayList<?>) list).size() > 0)) {
			value = ((ArrayList<?>) list).get(index - 1);
		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public static void Intersect(Element element) {
		String listName1 = Function
		.getVariableName(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_LIST1, ScriptAttribute.LIST).toString();
		String listName2 = Function
		.getVariableName(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_LIST2, ScriptAttribute.LIST).toString();
		HashMap<String, Object> variablesMap = Function.getVariablesMap();
		ArrayList<Object> list1 = ((ArrayList<Object>) variablesMap.get(listName1));
		ArrayList<Object> list2 = ((ArrayList<Object>) variablesMap.get(listName2));
		list1.retainAll(list2);
	}

	@SuppressWarnings("unchecked")
	public static void Remove(Element element) {
		String listName = Function
				.getVariableName(element, ScriptTag.PARAMETER,
						ScriptAttribute.LIST, ScriptAttribute.LIST).toString();
		Object object = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.OBJECT, ScriptAttribute.OBJECT);
		if (object != null) {
			((ArrayList<Object>) Function.getVariablesMap().get(listName))
					.remove(object);
		}
	}

	public static int Size(Element element) {
		int value = 0;
		Object list = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.LIST, ScriptAttribute.LIST);
		if (list != null) {
			value = ((ArrayList<?>) list).size();
		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public static void Union(Element element) {
		String listName1 = Function
		.getVariableName(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_LIST1, ScriptAttribute.LIST).toString();
		String listName2 = Function
		.getVariableName(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_LIST2, ScriptAttribute.LIST).toString();
		HashMap<String, Object> variablesMap = Function.getVariablesMap();
		ArrayList<Object> list1 = ((ArrayList<Object>) variablesMap.get(listName1));
		ArrayList<Object> list2 = ((ArrayList<Object>) variablesMap.get(listName2));
		list1.addAll(list2);
	}
}
