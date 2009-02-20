package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.ArrayList;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_Filter {
	@SuppressWarnings("unchecked")
	public static void AddCriteria(Element element) {
		String varName = Function.getVariableName(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER).toString();
		String field = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_ELEMENT, ScriptAttribute.OBJECT).toString();
		String operator = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.OPERATOR, ScriptAttribute.OPERATOR).toString();
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		Object link = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_LINK, ScriptAttribute.OPERATOR);
		ArrayList<Object> filter = new ArrayList<Object>();
		filter.add(field);
		filter.add(operator);
		filter.add(value);
		filter.add(link);
		((ArrayList<Object>)Function.getVariablesMap().get(varName)).add(filter);
	}
	
	@SuppressWarnings("unchecked")
	public static void Clear(Element element) {
		String varName = Function.getVariableName(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER).toString();
		((ArrayList<Object>)Function.getVariablesMap().get(varName)).clear();
	}
}
