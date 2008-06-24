package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.util.Log;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_List {
	public static Object GetListItem(NodeList params){
		Object value = null;
		Object list = getValue(params, ScriptAttribute.LIST, ScriptAttribute.LIST);
		int index = -1;
		if (getValue(params, ScriptAttribute.PARAMETER_NAME_INDEX, ScriptAttribute.PARAMETER_TYPE_INT) != null){
			index = Integer.valueOf(String.valueOf(getValue(params, ScriptAttribute.PARAMETER_NAME_INDEX, ScriptAttribute.PARAMETER_TYPE_INT)));
		}
		if (index != -1){
			value = ((ArrayList<?>)list).get(index-1);
		}
		return value;
	}
	
	public static int GetSize(NodeList params){
		int value = 0;
		Object list = getValue(params, ScriptAttribute.LIST, ScriptAttribute.LIST);
		if (list != null){
			value = ((ArrayList<?>)list).size();
		}
		return value;
	}
	
	public static void ListAddValue(NodeList params){
		String listName = null;
		int paramsLen = params.getLength();
		for (int i=0; i<paramsLen; i++){
			Element element = (Element) params.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) && 
					(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.LIST))){
				if (element.hasChildNodes()){
					Element var = (Element) element.getChildNodes().item(0);
					if (var.getNodeName().equals(ScriptTag.VAR)){
						listName = var.getAttribute(ScriptTag.NAME);
						Log.i("info", "listName "+listName);
					}
				}
			}
		}
		Object value = getValue(params, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		Function.getVariablesMap().get(listName).add(value);
		Log.i("info", "getvariablesMap "+Function.getVariablesMap().get(listName));
	}
	
	private static Object getValue(NodeList params, String name, String type){
		Object value = null;
		int paramsLen = params.getLength();
		for (int i=0; i<paramsLen; i++){
			Element element = (Element) params.item(i);
			if (element.getNodeName().equals(ScriptTag.PARAMETER)){
				if ((element.getAttribute(ScriptTag.NAME).equals(name)) &&
						(element.getAttribute(ScriptTag.TYPE).equals(type))){
					if (element.getChildNodes().getLength() == 1){
						if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE){
							value = element.getChildNodes().item(0).getNodeValue();
							Log.i("info", "index in listitem "+value);
						}
						else if (element.getChildNodes().item(0).getNodeType() == Node.ELEMENT_NODE){
							Element child = (Element)element.getChildNodes().item(0);
							if (child.getNodeName().equals(ScriptTag.VAR)){
								if (Function.getVariablesMap().get(child.getAttribute(ScriptTag.NAME)).size() > 1){
									value = Function.getVariablesMap().get(child.getAttribute(ScriptTag.NAME)).get(1);
									Log.i("info", "list in getsize "+value);
								}
							}
							else if (child.getNodeName().equals(ScriptTag.ELEMENT)){
								value = child.getAttribute(ScriptTag.ELEMENT_ID);
							}
							else if (child.getNodeName().equals(ScriptTag.CALL)){
								value = Function.returnTypeFunction(child);
							}
						}
					}
				}
			}
		}
		return value;
	}
}
