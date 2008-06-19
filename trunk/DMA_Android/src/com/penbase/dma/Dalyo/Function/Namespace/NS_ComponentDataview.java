package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.util.Log;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

public class NS_ComponentDataview {
	public static void Refresh(NodeList params){
		String componentId = String.valueOf(getValue(params, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		Object filter = getValue(params, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		Object order = getValue(params, ScriptAttribute.ORDER, ScriptAttribute.ORDER);
		ApplicationView.refreshComponent(componentId, filter);
	}
	
	public static HashMap<Object, Object> GetSelectedRecord(NodeList params){
		String componentId = String.valueOf(getValue(params, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT));
		HashMap<Object, Object> record = null;
		if (ApplicationView.getComponents().containsKey(componentId)){
			record = ApplicationView.getComponents().get(componentId).getRecord();
			Log.i("info", "record in getselectedrecord "+record);
		}
		return record;
	}
	
	private static Object getValue(NodeList params, String name, String type){
		Object result = null;
		int paramsLen = params.getLength();
		for (int i=0; i<paramsLen; i++){
			Element element = (Element) params.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.getAttribute(ScriptTag.NAME).equals(name)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(type))){
				if (element.getChildNodes().getLength() == 1){
					Element item = (Element) element.getChildNodes().item(0);
					if ((item.getNodeName().equals(ScriptTag.ELEMENT)) &&
							(item.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.COMPONENT))){
						result = item.getAttribute(ScriptTag.ELEMENT_ID);
					}
					else if (item.getNodeName().equals(ScriptTag.VAR)){
						String varName = item.getAttribute(ScriptTag.NAME);
						result = Function.getVariablesMap().get(varName);
					}
					else if (item.getNodeName().equals(ScriptTag.KEYWOED)){
						result = Function.getKeyWord(item);
					}
				}
			}
		}
		return result;
	}
}
