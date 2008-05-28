package com.penbase.dma.Dalyo.Function.Custom;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.util.Log;
import com.penbase.dma.XmlElement.XmlScriptAttribute;
import com.penbase.dma.XmlElement.XmlTag;

public class SetCurrentRecord {
	public SetCurrentRecord(NodeList params)
	{
		int paramsLen = params.getLength();
		
		String formId = null; 
		for (int i=0; i<paramsLen; i++)
		{
			Element element = (Element) params.item(i);
			if ((element.getNodeName().equals(XmlTag.TAG_SCRIPT_PARAMETER)) &&
					(element.hasAttribute(XmlTag.TAG_SCRIPT_NAME)) &&
					(element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(XmlScriptAttribute.PARAMETER_NAME_FORM)) &&
					(element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(XmlScriptAttribute.PARAMETER_TYPE_FORM)))
			{
				int itemLen = element.getChildNodes().getLength();
				if (itemLen > 0)
				{
					for (int j=0; j<itemLen; j++)
					{
						Element item = (Element) element.getChildNodes().item(j);
						if ((item.getNodeName().equals(XmlTag.TAG_SCRIPT_ELEMENT)) && (item.hasAttribute(XmlTag.TAG_SCRIPT_TYPE)) &&
								(item.getAttribute(XmlTag.TAG_SCRIPT_TYPE).equals(XmlScriptAttribute.PARAMETER_TYPE_FORM)))
						{
							formId = item.getAttribute(XmlTag.TAG_SCRIPT_ELEMENT_ID);
							Log.i("info", "formId in setcurrentvalue "+formId);
						}	
					}
				}
			}
			else if ((element.getNodeName().equals(XmlTag.TAG_SCRIPT_PARAMETER)) &&
					(element.hasAttribute(XmlTag.TAG_SCRIPT_NAME)) &&
					(element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(XmlScriptAttribute.PARAMETER_NAME_RECORD)) &&
					(element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(XmlScriptAttribute.PARAMETER_TYPE_RECORD)))
			{				
				int itemLen = element.getChildNodes().getLength();
				Log.i("info", "check name record "+itemLen);
				if (itemLen > 0)
				{
					for (int j=0; j<itemLen; j++)
					{
						Element item = (Element) element.getChildNodes().item(j);
						if ((item.getNodeName().equals(XmlTag.TAG_SCRIPT_CALL)) &&
								(item.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE).equals(XmlScriptAttribute.NAMESPACE_COMPONENT_CB)) &&
								(item.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_GETSELECTEDRECORD)))
						{
							new GetSelectedRecord(item.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER), formId);
						}
					}
				}
			}
		}
	}
}
