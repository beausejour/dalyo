package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Component.Custom.DalyoNumberBox;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Element;

public class NS_ComponentNumberBox {
	public static void SetMax(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		((DalyoNumberBox)ApplicationView.getComponents().get(componentId).getDalyoComponent()).setMaxValue(Integer.valueOf(value.toString()));
	}
	
	public static void SetMin(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		((DalyoNumberBox)ApplicationView.getComponents().get(componentId).getDalyoComponent()).setMinValue(Integer.valueOf(value.toString()));
	}
}
