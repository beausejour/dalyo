package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

public class NS_Form {
	public static void Navigate(NodeList items){
		String formId = String.valueOf(getValue(items, ScriptAttribute.FORM, ScriptAttribute.FORM));
		if (ApplicationView.getOnLoadFuncMap().containsKey(formId)){
			ApplicationView.getLayoutsMap().get(formId).onLoad(ApplicationView.getOnLoadFuncMap().get(formId));
		}
		ApplicationView.setCurrentFormI(formId);
		ApplicationView.getCurrentView().setContentView(ApplicationView.getLayoutsMap().get(formId));
	}
	
	public static void SetCurrentRecord(NodeList params){
		String formId = String.valueOf(getValue(params, ScriptAttribute.FORM, ScriptAttribute.FORM));
		HashMap<Object, Object> record = (HashMap<Object, Object>) getValue(params, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		if (ApplicationView.getLayoutsMap().containsKey(formId)){
			ApplicationView.getLayoutsMap().get(formId).setRecord(formId, record);
		}
	}
	
	private static Object getValue(NodeList items, String name, String type){
		Object value = null;
		int itemsLen = items.getLength();
		for (int i=0; i<itemsLen; i++){
			Element element = (Element) items.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.getAttribute(ScriptTag.NAME).equals(name)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(type))){
				NodeList elements = element.getChildNodes();
				if (element.getChildNodes().getLength() == 1){
					Element elt = (Element) elements.item(0);
					if ((elt.getNodeName().equals(ScriptTag.ELEMENT)) &&
							elt.hasAttribute(ScriptTag.ELEMENT_ID)){
						value = elt.getAttribute(ScriptTag.ELEMENT_ID);
					}
					else if (elt.getNodeName().equals(ScriptTag.CALL)){
						value = Function.returnTypeFunction(elt);
					}
					else if (elt.getNodeName().equals(ScriptTag.VAR)){
						value = Function.getVariablesMap().get(elt.getAttribute(ScriptTag.NAME)).get(1);
						Log.i("info", "get variable value in NS_Object "+value);
					}
				}
			}
		}
		return value;
	}
}
