package com.penbase.dma.Dalyo.Function.Custom;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.penbase.dma.View.ApplicationView;
import com.penbase.dma.XmlElement.XmlScriptAttribute;
import com.penbase.dma.XmlElement.XmlTag;

public class DisplayForm {
	public DisplayForm(NodeList items, NodeList params)
	{		
		int itemsLen = items.getLength();
		
		for (int i=0; i<itemsLen; i++)
		{
			Element element = (Element) items.item(i);
			if ((element.getNodeName().equals(XmlTag.TAG_SCRIPT_PARAMETER)) &&
					(element.hasAttribute(XmlTag.TAG_SCRIPT_NAME)) &&
					(element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(XmlScriptAttribute.PARAMETER_NAME_FORM)))
			{
				NodeList elements = element.getChildNodes();
				int eleLen = elements.getLength();
				for (int j=0; j<eleLen; j++)
				{
					Element elt = (Element) elements.item(j);
					if ((elt.getNodeName().equals(XmlTag.TAG_SCRIPT_ELEMENT)) &&
							elt.hasAttribute(XmlTag.TAG_SCRIPT_ELEMENT_ID))
					{						
						String id = elt.getAttribute(XmlTag.TAG_SCRIPT_ELEMENT_ID);
						ApplicationView.getCurrentView().setContentView(ApplicationView.getLayoutsMap().get(id));
					}
				}
			}
		}
	}
}
