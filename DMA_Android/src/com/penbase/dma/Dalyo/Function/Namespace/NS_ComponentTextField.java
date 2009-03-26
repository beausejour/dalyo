package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Component.Custom.TextField;
import com.penbase.dma.Dalyo.Component.Custom.TextZone;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Element;

public class NS_ComponentTextField {
	public static String GetText(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		if (ApplicationView.getComponents().get(componentId).getView() instanceof TextField) {
			return ((TextField)ApplicationView.getComponents().get(componentId).getView()).getValue();
		} else {
			return ((TextZone)ApplicationView.getComponents().get(componentId).getView()).getValue();
		}
	}
	
	public static boolean IsEmpty(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		if (ApplicationView.getComponents().get(componentId).getView() instanceof TextField) {
			return ((TextField)ApplicationView.getComponents().get(componentId).getView()).isEmpty();
		} else {
			return ((TextZone)ApplicationView.getComponents().get(componentId).getView()).isEmpty();
		}
	}
}
