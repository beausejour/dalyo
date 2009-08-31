package com.penbase.dma.Dalyo.Function.Namespace;

import android.util.Log;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Element;

public class NS_Component {
	public static String GetLabel(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		return ApplicationView.getComponents().get(componentId).getLabel();
	}
	
	public static Object GetValue(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		if (ApplicationView.getComponents().containsKey(componentId)) {
			return ApplicationView.getComponents().get(componentId).getValue();
		} else {
			return null;
		}
	}
	
	public static boolean IsEnabled(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		return ApplicationView.getComponents().get(componentId).isEnabled();
	}
	
	public static boolean IsVisible(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		return ApplicationView.getComponents().get(componentId).isVisible();
	}
	
	public static void ReSet(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		ApplicationView.getComponents().get(componentId).reSet();
	}
	
	public static void SetEnabled(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object state =  Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_ENABLE, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		ApplicationView.getComponents().get(componentId).setEnabled(((Boolean)state).booleanValue());
	}
	
	public static void SetFocus(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		ApplicationView.getComponents().get(componentId).setFocus();
	}
	
	public static void SetText(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		if (Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING) != null) {
			String text = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING).toString();
			ApplicationView.getComponents().get(componentId).setText(text);
		}
	}
	
	public static void SetValue(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value != null) {
			ApplicationView.getComponents().get(componentId).setValue(value);
		}
	}
	
	public static void SetVisible(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object state =  Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VISIBLE, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		ApplicationView.getComponents().get(componentId).setVisible(((Boolean)state).booleanValue());
	}
}
