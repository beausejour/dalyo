package com.penbase.dma.Dalyo.Function.Namespace;

import java.math.BigDecimal;
import java.util.Random;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_Math {
	public static int Ceil(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		return Double.valueOf(Math.ceil(Double.valueOf(String.valueOf(value)))).intValue();
	}
	
	public static double Division(Element element) {
		Object left = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_A, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object right = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_B, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		return Double.valueOf(String.valueOf(left)) / Double.valueOf(String.valueOf(right));
	}
	
	public static Object Multiple(Element element) {
		Object left = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_A, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object right = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_B, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		if ((left.toString().indexOf(".") != -1) || (right.toString().indexOf(".") != -1)) {
			return (Double.valueOf(left.toString()) * Double.valueOf(right.toString()));
		}
		else{
			return (Integer.valueOf(left.toString()) * Integer.valueOf(right.toString()));
		}
	}
	
	public static int Random(Element element) {
		Object max = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_MAX, ScriptAttribute.PARAMETER_TYPE_INT);
		Object min = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_MIN, ScriptAttribute.PARAMETER_TYPE_INT);
		if ((max == null) && (min == null)) {
			return new Random().nextInt();
		}
		else if (min == null) {
			return new Random().nextInt(Integer.valueOf(String.valueOf(max)));
		}
		else {
			return Double.valueOf(Integer.valueOf(String.valueOf(min)) * Math.random()).intValue(); 
		}
	}
	
	public static double Round(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		int decimal = Integer.valueOf(String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_DECIMALS, ScriptAttribute.PARAMETER_TYPE_INT)));
		BigDecimal bd = new BigDecimal(Double.valueOf(String.valueOf(value)));
		bd = bd.setScale(decimal, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	
	public static Object Subtract(Element element) {
		Object left = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_A, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object right = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_B, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		if ((left.toString().indexOf(".") != -1) || (right.toString().indexOf(".") != -1)) {
			return (Double.valueOf(left.toString()) - Double.valueOf(right.toString()));
		}
		else{
			return (Integer.valueOf(left.toString()) - Integer.valueOf(right.toString()));
		}
	}
	
	public static Object Sum(Element element) {
		Object left = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_A, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object right = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_B, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		if ((left.toString().indexOf(".") != -1) || (right.toString().indexOf(".") != -1)) {
			return (Double.valueOf(left.toString()) + Double.valueOf(right.toString()));
		}
		else{
			return (Integer.valueOf(left.toString()) + Integer.valueOf(right.toString()));
		}
	}
}
