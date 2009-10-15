package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Component.DalyoComponent;
import com.penbase.dma.Dalyo.Component.Custom.DalyoTextField;
import com.penbase.dma.Dalyo.Component.Custom.DalyoTextZone;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Element;

public class NS_ComponentTextField {
	public static String GetText(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		DalyoComponent component = ApplicationView.getComponents().get(componentId).getDalyoComponent();
		if (component instanceof DalyoTextField) {
			return ((DalyoTextField) component).getValue();
		} else {
			return ((DalyoTextZone) component).getValue();
		}
	}
	
	public static boolean IsEmpty(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		DalyoComponent component = ApplicationView.getComponents().get(componentId).getDalyoComponent();
		if (component instanceof DalyoTextField) {
			return ((DalyoTextField) component).isEmpty();
		} else {
			return ((DalyoTextZone) component).isEmpty();
		}
	}
}
