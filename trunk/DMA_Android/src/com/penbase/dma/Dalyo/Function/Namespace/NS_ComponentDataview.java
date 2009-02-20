package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.HashMap;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

public class NS_ComponentDataview {
	public static HashMap<Object, Object> GetSelectedRecord(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		HashMap<Object, Object> record = null;
		if (ApplicationView.getComponents().containsKey(componentId)) {
			record = ApplicationView.getComponents().get(componentId).getRecord();
		}
		return record;
	}
	
	public static void Refresh(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		Object order = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.ORDER, ScriptAttribute.ORDER);
		if (ApplicationView.getComponents().containsKey(componentId)) {
			ApplicationView.getComponents().get(componentId).refreshComponentContent(filter, order, null);
		}
	}
}
