package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.util.Log;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

public class NS_Component {
	public static Object GetValue(NodeList params){
		String componentId = String.valueOf(getValue(params, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		return ApplicationView.getComponents().get(componentId).getValue();
	}
	
	public static void SetValue(NodeList params){
		String componentId = String.valueOf(getValue(params, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		Object value = getValue(params, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value != null){
			ApplicationView.getComponents().get(componentId).setValue(value);
		}
	}
	
	public static void SetText(NodeList params){
		String componentId = String.valueOf(getValue(params, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		if (getValue(params, ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING) != null){
			String text = String.valueOf(getValue(params, ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING));
			ApplicationView.getComponents().get(componentId).setText(text);
		}
	}
	
	public static void SetEnabled(NodeList params){
		String componentId = String.valueOf(getValue(params, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		Object state =  getValue(params, ScriptAttribute.PARAMETER_NAME_ENABLE, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		ApplicationView.getComponents().get(componentId).setEnabled(((Boolean)state).booleanValue());
	}
	
	public static void SetVisible(NodeList params){
		String componentId = String.valueOf(getValue(params, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		Object state =  getValue(params, ScriptAttribute.PARAMETER_NAME_VISIBLE, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		ApplicationView.getComponents().get(componentId).setVisible(((Boolean)state).booleanValue());
	}
	
	private static Object getValue(NodeList params, String name, String type){
		Object value = null;
		int paramsLen = params.getLength();
		for (int i=0; i<paramsLen; i++){
			Element element = (Element)params.item(i);
			if (element.getNodeName().equals(ScriptTag.PARAMETER)){
				if ((element.getAttribute(ScriptTag.NAME).equals(name)) &&
						(element.getAttribute(ScriptTag.TYPE).equals(type))){
					if (element.getChildNodes().getLength() == 1){
						if (element.getChildNodes().item(0).getNodeType() == Node.ELEMENT_NODE){
							Element child = (Element)element.getChildNodes().item(0);
							if (child.getNodeName().equals(ScriptTag.ELEMENT)){
								value = child.getAttribute(ScriptTag.ELEMENT_ID);
							}
							else if (child.getNodeName().equals(ScriptTag.CALL)){
								String text = String.valueOf(Function.returnTypeFunction(child));
								if ((text != null) && (!text.equals("null"))){
									value = Function.returnTypeFunction(child);
								}
							}
							else if (child.getNodeName().equals(ScriptTag.KEYWORD)){
								value = Function.getKeyWord(child);
							}
						}
						else if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE){
							value = element.getChildNodes().item(0).getNodeValue();
						}
					}
				}
			}
		}
		return value;
	}
}
