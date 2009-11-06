package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

import org.w3c.dom.Element;

import java.util.ArrayList;

public class NS_Order {
	@SuppressWarnings("unchecked")
	public static void AddCriteria(Element element) {
		Object order = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.ORDER, ScriptAttribute.ORDER);
		String field = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		Object ascending = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_ASCENDING,
				ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		((ArrayList<Object>) order).add(field);
		((ArrayList<Object>) order).add(ascending);
	}
	
	@SuppressWarnings("unchecked")
	public static void Clear(Element element) {
		Object order = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.ORDER, ScriptAttribute.ORDER);
		if (order != null) {
			((ArrayList<Object>) order).clear();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static int Size(Element element) {
		int result = 0;
		Object order = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.ORDER, ScriptAttribute.ORDER);
		if (order != null) {
			result = ((ArrayList<Object>) order).size() / 2;
		}
		return result;
	}
}
