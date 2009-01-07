package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;

import android.util.Log;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_Filter {
	public static void AddCriteria(Element element) {
		String varName = String.valueOf(Function.getVariableName(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER));
		String field = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_ELEMENT, ScriptAttribute.OBJECT));
		String operator = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.OPERATOR, ScriptAttribute.OPERATOR));
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		Object link = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_LINK, ScriptAttribute.OPERATOR);
		Log.i("info", "field "+field+" operator "+operator+" value "+value+" link "+link);
		Function.addFilterValues(varName, field, operator, value, link);
	}
	
	public static void Clear(Element element) {
		String varName = String.valueOf(Function.getVariableName(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER));
		Function.clearFilterByName(varName);
	}
}
