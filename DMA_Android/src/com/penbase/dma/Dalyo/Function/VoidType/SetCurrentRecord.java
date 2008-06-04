package com.penbase.dma.Dalyo.Function.VoidType;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.util.Log;

import com.penbase.dma.Dalyo.Function.ReturnType.GetSelectedRecord;
import com.penbase.dma.XmlElement.ScriptTag;
import com.penbase.dma.XmlElement.ScriptAttribute;

public class SetCurrentRecord {
	public SetCurrentRecord(NodeList params)
	{
		int paramsLen = params.getLength();		
		String formId = null; 
		for (int i=0; i<paramsLen; i++)
		{
			Element element = (Element) params.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.hasAttribute(ScriptTag.NAME)) &&
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.FORM)) &&
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.FORM)))
			{
				int itemLen = element.getChildNodes().getLength();
				if (itemLen > 0)
				{
					for (int j=0; j<itemLen; j++)
					{
						Element item = (Element) element.getChildNodes().item(j);
						if ((item.getNodeName().equals(ScriptTag.ELEMENT)) && (item.hasAttribute(ScriptTag.TYPE)) &&
								(item.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.FORM)))
						{
							formId = item.getAttribute(ScriptTag.ELEMENT_ID);
							Log.i("info", "formId in setcurrentvalue "+formId);
						}	
					}
				}
			}
			else if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.hasAttribute(ScriptTag.NAME)) &&
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.RECORD)) &&
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.RECORD)))
			{				
				int itemLen = element.getChildNodes().getLength();
				Log.i("info", "check name record "+itemLen);
				if (itemLen > 0)
				{
					for (int j=0; j<itemLen; j++)
					{
						Element item = (Element) element.getChildNodes().item(j);
						if ((item.getNodeName().equals(ScriptTag.CALL)) &&
								(item.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_COMPONENT_CB)) &&
								(item.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETSELECTEDRECORD)))
						{
							new GetSelectedRecord(item.getElementsByTagName(ScriptTag.PARAMETER), formId);
						}
					}
				}
			}
		}
	}
}
