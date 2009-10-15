package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Component.Custom.DalyoComboBox;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Element;

import java.util.HashMap;

public class NS_ComponentCombobox {
	public static void AddItem(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		Object label = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_LABEL, ScriptAttribute.OBJECT);
		((DalyoComboBox)ApplicationView.getComponents().get(componentId).getDalyoComponent()).addItem(label.toString(), value.toString());
	}
	
	public static int Count(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		return ((DalyoComboBox)ApplicationView.getComponents().get(componentId).getDalyoComponent()).count();
	}
	
	public static int GetSelectedIndex(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		return ((DalyoComboBox)ApplicationView.getComponents().get(componentId).getDalyoComponent()).getSelectedIndex();
	}
	
	
	public static HashMap<Object, Object> GetSelectedRecord(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		return ((DalyoComboBox)ApplicationView.getComponents().get(componentId).getDalyoComponent()).getCurrentRecord();
	}
	
	public static void SetSelectedIndex(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		int index = Integer.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_INDEX, ScriptAttribute.PARAMETER_TYPE_INT).toString());
		((DalyoComboBox)ApplicationView.getComponents().get(componentId).getDalyoComponent()).setSelectedIndex(index);
	}
	
	public static void Refresh(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		Object order = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.ORDER, ScriptAttribute.ORDER);
		Object distinct = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_DISTINCT, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		((DalyoComboBox)ApplicationView.getComponents().get(componentId).getDalyoComponent()).refresh(filter, order, distinct);
	}
	
	public static void RemoveAllItems(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		((DalyoComboBox)ApplicationView.getComponents().get(componentId).getDalyoComponent()).removeAllItems();
	}	
}
