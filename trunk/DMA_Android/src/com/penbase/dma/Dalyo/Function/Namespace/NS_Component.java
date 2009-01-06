package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

public class NS_Component {
	public static Object GetValue(Element element){
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		if (ApplicationView.getComponents().containsKey(componentId)) {
			return ApplicationView.getComponents().get(componentId).getValue();
		}
		else {
			return null;
		}
	}
	
	public static boolean IsEnabled(Element element){
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		return ApplicationView.getComponents().get(componentId).isEnabled();
	}
	
	public static boolean IsVisible(Element element){
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		return ApplicationView.getComponents().get(componentId).isVisible();
	}
	
	public static void ReSet(Element element){
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		ApplicationView.getComponents().get(componentId).reSet();
	}
	
	public static void SetEnabled(Element element){
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		Object state =  Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_ENABLE, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		ApplicationView.getComponents().get(componentId).setEnabled(((Boolean)state).booleanValue());
	}
	
	public static void SetText(Element element){
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		if (Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING) != null){
			String text = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING));
			ApplicationView.getComponents().get(componentId).setText(text);
		}
	}
	
	public static void SetValue(Element element){
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value != null){
			ApplicationView.getComponents().get(componentId).setValue(value);
		}
	}
	
	public static void SetVisible(Element element){
		String componentId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		Object state =  Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VISIBLE, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		ApplicationView.getComponents().get(componentId).setVisible(((Boolean)state).booleanValue());
	}
}
