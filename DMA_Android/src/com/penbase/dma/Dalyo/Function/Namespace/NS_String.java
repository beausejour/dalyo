package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_String {
	public static String Concat(Element element) {
		String left = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_A, ScriptAttribute.STRING).toString();
		String right = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_B, ScriptAttribute.STRING).toString();
		return left+right;
	}
	
	public static int Length(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.STRING, ScriptAttribute.STRING);
		if (value == null) {
			return 0;
		}
		else {
			return value.toString().length();
		}
	}
}