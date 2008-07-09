package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_Math {
	public static Object Subtract(Element element){
		Object left = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_A, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object right = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_B, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		if ((left.toString().indexOf(".") != -1) || (right.toString().indexOf(".") != -1)){
			return (Double.valueOf(left.toString()) - Double.valueOf(right.toString()));
		}
		else{
			return (Integer.valueOf(left.toString()) - Integer.valueOf(right.toString()));
		}
	}
	
	public static Object Sum(Element element){
		Object left = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_A, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object right = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_B, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		if ((left.toString().indexOf(".") != -1) || (right.toString().indexOf(".") != -1)){
			return (Double.valueOf(left.toString()) + Double.valueOf(right.toString()));
		}
		else{
			return (Integer.valueOf(left.toString()) + Integer.valueOf(right.toString()));
		}
	}
}
