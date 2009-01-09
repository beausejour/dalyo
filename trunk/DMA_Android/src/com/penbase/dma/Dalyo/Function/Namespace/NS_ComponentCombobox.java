package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.HashMap;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Component.Custom.ComboBox;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

public class NS_ComponentCombobox {
	public static void AddItem(Element element) {
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		Object label = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_LABEL, ScriptAttribute.OBJECT);
		//
		/*boolean bold = Boolean.valueOf(String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_BOLD, ScriptAttribute.PARAMETER_TYPE_BOOLEAN)));
		boolean italic = Boolean.valueOf(String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_ITALIC, ScriptAttribute.PARAMETER_TYPE_BOOLEAN)));
		boolean underline = Boolean.valueOf(String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_UNDERLINE, ScriptAttribute.PARAMETER_TYPE_BOOLEAN)));*/
		((ComboBox)ApplicationView.getComponents().get(componentId).getView()).addItem(String.valueOf(label), String.valueOf(value));
	}
	
	public static int Count(Element element) {
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		return ((ComboBox)ApplicationView.getComponents().get(componentId).getView()).count();
	}
	
	public static int GetSelectedIndex(Element element) {
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		return ((ComboBox)ApplicationView.getComponents().get(componentId).getView()).getSelectedIndex();
	}
	
	
	public static HashMap<Object, Object> GetSelectedRecord(Element element) {
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		HashMap<Object, Object> record = null;
		if (ApplicationView.getComponents().containsKey(componentId)) {
			record = ApplicationView.getComponents().get(componentId).getRecord();
		}
		return record;
	}
	
	public static void SetSelectedIndex(Element element) {
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		int index = Integer.valueOf(String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_INDEX, ScriptAttribute.PARAMETER_TYPE_INT)));
		((ComboBox)ApplicationView.getComponents().get(componentId).getView()).setSelectedIndex(index);
	}
	
	public static void Refresh(Element element) {
		//order and distinct are not implemented yet
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		Object order = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.ORDER, ScriptAttribute.ORDER);
		Object distinct = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_DISTINCT, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		ApplicationView.refreshComponent(componentId, filter);
	}
	
	public static void RemoveAllItems(Element element) {
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		((ComboBox)ApplicationView.getComponents().get(componentId).getView()).removeAllItems();
	}	
}
