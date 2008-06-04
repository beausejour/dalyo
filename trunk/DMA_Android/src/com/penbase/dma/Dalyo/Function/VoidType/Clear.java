package com.penbase.dma.Dalyo.Function.VoidType;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.penbase.dma.Dalyo.Database;
import com.penbase.dma.XmlElement.ScriptTag;
import com.penbase.dma.XmlElement.ScriptAttribute;

public class Clear {
	public Clear(NodeList params)
	{
		int paramsLen = params.getLength();		
		String tableId = null; 
		for (int i=0; i<paramsLen; i++)
		{
			Element element = (Element) params.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.hasAttribute(ScriptTag.NAME)) &&
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.TABLE)))
			{
				int itemLen = element.getChildNodes().getLength();
				if (itemLen > 0)
				{
					for (int j=0; j<itemLen; j++)
					{
						Element item = (Element) element.getChildNodes().item(j);
						if ((item.getNodeName().equals(ScriptTag.ELEMENT)) && (item.hasAttribute(ScriptTag.TYPE)) &&
								(item.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.TABLE)))
						{
							tableId = item.getAttribute(ScriptTag.ELEMENT_ID);
							Database.clearTable(tableId);
						}	
					}
				}
			}
		}
	}
}
