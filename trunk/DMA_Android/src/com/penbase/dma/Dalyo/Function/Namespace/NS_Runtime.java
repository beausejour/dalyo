package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationListView;
import com.penbase.dma.View.ApplicationView;
import android.app.AlertDialog;
import android.content.Context;

public class NS_Runtime {
	public static void Alert(Context context, NodeList params, NodeList newParams){
		String message = getValue(params, ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING, newParams);
		String title = getValue(params, ScriptAttribute.PARAMETER_NAME_CAPTION, ScriptAttribute.STRING, newParams);
		if (!title.equals(ScriptAttribute.CONST_NULL)){
			new AlertDialog.Builder(context).setMessage(message).setTitle(title).show();
		}
		else{
			new AlertDialog.Builder(context).setMessage(message).show();
		}
	}
	
	public static void Synchronize(NodeList items, NodeList globalParams){
		String type = getValue(items, ScriptAttribute.PARAMETER_NAME_FACELESS, ScriptAttribute.PARAMETER_TYPE_BOOLEAN, globalParams);
		if (!type.equals(ScriptAttribute.CONST_FALSE)){
			ApplicationView.getCurrentClient().launchImport(
					ApplicationListView.getApplicationsInfo().get("AppId"),
					ApplicationListView.getApplicationsInfo().get("DbId"), 
					ApplicationListView.getApplicationsInfo().get("Username"),
					ApplicationListView.getApplicationsInfo().get("Userpassword"));
		}
	}
	
	private static String getValue(NodeList items, String name, String type, NodeList newParams){
		String value = null;
		int itemsLen = items.getLength();
		for (int i=0; i<itemsLen; i++){
			Element element = (Element) items.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.getAttribute(ScriptTag.NAME).equals(name)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(type))){
				NodeList elements = element.getChildNodes();
				if (element.getChildNodes().getLength() == 1){
					if (element.getChildNodes().item(0).getNodeType() == Node.ELEMENT_NODE){
						Element elt = (Element) elements.item(0);
						if (elt.getNodeName().equals(ScriptTag.KEYWOED)){
							value = elt.getChildNodes().item(0).getNodeValue();
						}
						else if (elt.getNodeName().equals(ScriptTag.VAR)){
							if (Function.getVariableValue(elt.getAttribute(ScriptTag.NAME)) != null){
								value = (String) Function.getVariableValue(elt.getAttribute(ScriptTag.NAME));
							}
							else if (Function.getParamValue(newParams, elt.getAttribute(ScriptTag.NAME), type) != null){
								value = (String) Function.getParamValue(newParams, elt.getAttribute(ScriptTag.NAME), type);
							}
							else if (elt.getNodeName().equals(ScriptTag.KEYWOED)){
								value = elt.getChildNodes().item(0).getNodeValue();
							}
						}
					}
					else if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE){
						value = element.getChildNodes().item(0).getNodeValue();
					}
				}
			}
		}
		return value;
	}
}
