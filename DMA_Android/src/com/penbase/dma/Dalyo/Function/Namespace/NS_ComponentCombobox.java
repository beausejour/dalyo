package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.HashMap;
import org.w3c.dom.Element;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

public class NS_ComponentCombobox {
	public static void Refresh(Element element){
		//order and distinct are not implemented yet
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		Object order = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.ORDER, ScriptAttribute.ORDER);
		Object distinct = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_DISTINCT, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		ApplicationView.refreshComponent(componentId, filter);
	}
	
	public static HashMap<Object, Object> GetSelectedRecord(Element element){
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		HashMap<Object, Object> record = null;
		if (ApplicationView.getComponents().containsKey(componentId)){
			record = ApplicationView.getComponents().get(componentId).getRecord();
		}
		return record;
	}
}
