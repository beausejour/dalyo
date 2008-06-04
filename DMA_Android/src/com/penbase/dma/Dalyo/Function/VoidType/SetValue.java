package com.penbase.dma.Dalyo.Function.VoidType;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.penbase.dma.View.ApplicationView;
import com.penbase.dma.XmlElement.ScriptAttribute;
import com.penbase.dma.XmlElement.ScriptTag;

public class SetValue {
	String componentId;
	Object value = null;
	public SetValue(NodeList params)
	{
		int paramsLen = params.getLength();
		for (int i=0; i<paramsLen; i++)
		{
			Element element = (Element)params.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.COMPONENT)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.COMPONENT)))				
			{
				if (element.getChildNodes().getLength() == 1)
				{
					Element child = (Element)element.getChildNodes().item(0);
					componentId = child.getAttribute(ScriptTag.ELEMENT_ID);
				}
			}
			else if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.PARAMETER_NAME_VALUE)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.PARAMETER_TYPE_OBJECT)))
			{			
				if (element.getChildNodes().getLength() == 1)
				{
					if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE)
					{
						value = element.getChildNodes().item(0).getNodeValue();
					}				
				}
			}
		}
		ApplicationView.getComponents().get(componentId).setValue(value);
	}
}
