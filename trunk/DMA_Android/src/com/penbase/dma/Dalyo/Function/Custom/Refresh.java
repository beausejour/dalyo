package com.penbase.dma.Dalyo.Function.Custom;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.penbase.dma.View.ApplicationView;
import com.penbase.dma.XmlElement.XmlScriptAttribute;
import com.penbase.dma.XmlElement.XmlTag;

public class Refresh {
	public Refresh(String type, NodeList params)
	{
		int paramsLen = params.getLength();
		if (type.equals(XmlScriptAttribute.NAMESPACE_COMPONENT_DV))
		{
			String componentId = null; 
			for (int i=0; i<paramsLen; i++)
			{
				Element element = (Element) params.item(i);
				if ((element.getNodeName().equals(XmlTag.TAG_SCRIPT_PARAMETER)) &&
						(element.hasAttribute(XmlTag.TAG_SCRIPT_NAME)) &&
						(element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(XmlScriptAttribute.PARAMETER_NAME_COMPONENT)))
				{
					int itemLen = element.getChildNodes().getLength();
					if (itemLen > 0)
					{
						for (int j=0; j<itemLen; j++)
						{
							Element item = (Element) element.getChildNodes().item(j);
							if ((item.getNodeName().equals(XmlTag.TAG_SCRIPT_ELEMENT)) && (item.hasAttribute(XmlTag.TAG_SCRIPT_TYPE)) &&
									(item.getAttribute(XmlTag.TAG_SCRIPT_TYPE).equals(XmlScriptAttribute.PARAMETER_TYPE_COMPONENT)))
							{
								componentId = item.getAttribute(XmlTag.TAG_SCRIPT_ELEMENT_ID);
								ApplicationView.refreshComponent(componentId);
							}	
						}
					}
				}
			}
		}		
	}
}
