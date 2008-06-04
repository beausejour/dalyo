package com.penbase.dma.Dalyo.Function.VoidType;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.penbase.dma.View.ApplicationView;
import com.penbase.dma.XmlElement.ScriptTag;
import com.penbase.dma.XmlElement.ScriptAttribute;
import com.penbase.dma.XmlElement.XmlTag;

public class DisplayForm {
	public DisplayForm(NodeList items, NodeList params)
	{		
		int itemsLen = items.getLength();
		
		for (int i=0; i<itemsLen; i++)
		{
			Element element = (Element) items.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.hasAttribute(ScriptTag.NAME)) &&
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.FORM)))
			{
				NodeList elements = element.getChildNodes();
				int eleLen = elements.getLength();
				for (int j=0; j<eleLen; j++)
				{
					Element elt = (Element) elements.item(j);
					if ((elt.getNodeName().equals(ScriptTag.ELEMENT)) &&
							elt.hasAttribute(ScriptTag.ELEMENT_ID))
					{						
						String id = elt.getAttribute(ScriptTag.ELEMENT_ID);
						Log.i("info", "onload function name "+ApplicationView.getOnLoadFuncMap().get(id));
						ApplicationView.getLayoutsMap().get(id).onLoad(ApplicationView.getOnLoadFuncMap().get(id));
						ApplicationView.getCurrentView().setContentView(ApplicationView.getLayoutsMap().get(id));
					}
				}
			}
		}
	}
}
