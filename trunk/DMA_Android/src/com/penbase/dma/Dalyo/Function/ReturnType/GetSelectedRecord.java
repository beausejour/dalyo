package com.penbase.dma.Dalyo.Function.ReturnType;

import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.penbase.dma.View.ApplicationView;
import com.penbase.dma.XmlElement.ScriptTag;
import com.penbase.dma.XmlElement.ScriptAttribute;

public class GetSelectedRecord {
	String componentId = null;
	HashMap<String, Object> record = null;
	
	public GetSelectedRecord(NodeList params, String formId)
	{
		int paramsLen = params.getLength();		
		for (int i=0; i<paramsLen; i++)
		{
			Element element = (Element) params.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.hasAttribute(ScriptTag.NAME)) &&
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.COMPONENT)) &&
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.COMPONENT)))
			{
				int itemLen = element.getChildNodes().getLength();
				if (itemLen > 0)
				{
					for (int j=0; j<itemLen; j++)
					{
						Element item = (Element) element.getChildNodes().item(j);
						if ((item.getNodeName().equals(ScriptTag.ELEMENT)) && (item.hasAttribute(ScriptTag.TYPE)) &&
								(item.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.COMPONENT)))
						{							
							componentId = item.getAttribute(ScriptTag.ELEMENT_ID);
							if (formId != null)
							{
								ApplicationView.getComponents().get(componentId).setRecord(formId);
							}
							else
							{
								record = ApplicationView.getComponents().get(componentId).getRecord(); 
							}
						}	
					}
				}
			}
		}		
	}
	
	public HashMap<String, Object> getValue()
	{
		return record;
	}
}
