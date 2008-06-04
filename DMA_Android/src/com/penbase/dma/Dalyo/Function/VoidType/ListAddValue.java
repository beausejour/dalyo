package com.penbase.dma.Dalyo.Function.VoidType;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.penbase.dma.Dalyo.Database;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.XmlElement.ScriptAttribute;
import com.penbase.dma.XmlElement.ScriptTag;

public class ListAddValue {
	public ListAddValue(NodeList params)
	{
		int paramsLen = params.getLength();
		String listName = null;
		Object value = null;
		for (int i=0; i<paramsLen; i++)
		{
			Element element = (Element) params.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) && 
					(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.LIST)))
			{
				if (element.hasChildNodes())
				{
					Element var = (Element) element.getChildNodes().item(0);
					if (var.getNodeName().equals(ScriptTag.VAR))
					{
						listName = var.getAttribute(ScriptTag.NAME);
						Log.i("info", "listName "+listName);
					}
				}
			}
			else if ((element.getNodeName().equals(ScriptTag.PARAMETER)) && 
					(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.PARAMETER_TYPE_OBJECT)))
			{
				if (element.hasChildNodes())
				{
					Element elt = (Element) element.getChildNodes().item(0);
					if (elt.getNodeName().equals(ScriptTag.ELEMENT))
					{
						if (elt.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.FIELD))
						{
							value = Database.FIELD+elt.getAttribute(ScriptTag.ELEMENT_ID);
						}
					}
					else if ((elt.getNodeName().equals(ScriptTag.CALL)) && (elt.hasAttribute(ScriptTag.FUNCTION)))
					{
						if ((elt.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETFIELDVALUE)) ||
								(elt.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_NOW)))
						{
							value = Function.returnTypeFunction(elt);
						}						
					}
				}
			}
		}
		Function.getVariablesMap().get(listName).add(value);
		Log.i("info", "getvariablesMap "+Function.getVariablesMap().get(listName));
	}
}
