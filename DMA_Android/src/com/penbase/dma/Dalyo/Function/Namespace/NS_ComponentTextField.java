package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Component.Custom.TextField;
import com.penbase.dma.Dalyo.Component.Custom.TextZone;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

public class NS_ComponentTextField {
	public static String GetText(Element element) {
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		if (ApplicationView.getComponents().get(componentId).getView() instanceof TextField) {
			return ((TextField)ApplicationView.getComponents().get(componentId).getView()).getValue();
		}
		else {
			return ((TextZone)ApplicationView.getComponents().get(componentId).getView()).getValue();
		}
	}
	
	//<c f="isEmpty" ns="component.textfield"><p n="component" t="component"><elt id="61" t="component"/></p></c>
	public static boolean IsEmpty(Element element) {
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		if (ApplicationView.getComponents().get(componentId).getView() instanceof TextField) {
			return ((TextField)ApplicationView.getComponents().get(componentId).getView()).isEmpty();
		}
		else {
			return ((TextZone)ApplicationView.getComponents().get(componentId).getView()).isEmpty();
		}
	}
}
