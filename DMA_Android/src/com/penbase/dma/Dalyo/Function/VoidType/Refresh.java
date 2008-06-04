package com.penbase.dma.Dalyo.Function.VoidType;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;
import com.penbase.dma.XmlElement.ScriptTag;
import com.penbase.dma.XmlElement.ScriptAttribute;

public class Refresh {
	String componentId = null;
	Object filter = null;
	
	public Refresh(NodeList params)
	{
		int paramsLen = params.getLength();				
		for (int i=0; i<paramsLen; i++)
		{
			Element element = (Element) params.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&						
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
						}							
					}
				}
			}
			else if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&						
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.FILTER)))
			{
				if ((element.getChildNodes().getLength() == 1) &&
						(element.getChildNodes().item(0).getNodeName().equals(ScriptTag.VAR)))
				{
					Element child = (Element) element.getChildNodes().item(0);
					String varName = child.getAttribute(ScriptTag.NAME);
					filter = Function.getVariablesMap().get(varName);
				}
			}
			else if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&						
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.ORDER)))
			{
				
			}
			else if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&						
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.PARAMETER_NAME_DISTINCT)))
			{
				
			}
		}
		ApplicationView.refreshComponent(componentId, filter);		
	}
}
