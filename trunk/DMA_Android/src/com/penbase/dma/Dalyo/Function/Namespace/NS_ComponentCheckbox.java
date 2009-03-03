package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;

import android.widget.CheckBox;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

public class NS_ComponentCheckbox {
	public static void Check(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object state =  Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_STATE, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		((CheckBox)ApplicationView.getComponents().get(componentId).getView()).setChecked(((Boolean)state).booleanValue());
	}
	
	public static boolean IsChecked(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		return ((CheckBox)ApplicationView.getComponents().get(componentId).getView()).isChecked();
	}
}
