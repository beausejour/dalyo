package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Component.Custom.DalyoRadiobutton;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Element;

public class NS_ComponentRadioButton {
	public static boolean IsSelected(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		return ((DalyoRadiobutton)ApplicationView.getComponents().get(componentId).getDalyoComponent()).isSelected();
	}
}
