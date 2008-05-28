package com.penbase.dma.Dalyo.Function.Custom;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.app.AlertDialog;
import android.content.Context;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.XmlElement.XmlScriptAttribute;
import com.penbase.dma.XmlElement.XmlTag;

public class ShowMessage {
	public ShowMessage(Context context, NodeList items, NodeList newParams)
	{
		int itemsLen = items.getLength();
		String message = "";
		String title = "";
		
		for (int i=0; i<itemsLen; i++)
		{
			Element element = (Element)items.item(i);
			if ((element.getNodeName().equals(XmlTag.TAG_SCRIPT_PARAMETER)) &&
					(element.hasAttribute(XmlTag.TAG_SCRIPT_NAME)))
			{
				if (element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(XmlScriptAttribute.PARAMETER_NAME_TEXT))
				{
					message = getDialogValue(element, XmlScriptAttribute.PARAMETER_NAME_TEXT, newParams);
				}
				else if (element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(XmlScriptAttribute.PARAMETER_NAME_CAPTION))
				{
					title = getDialogValue(element, XmlScriptAttribute.PARAMETER_NAME_CAPTION, newParams);
				}
			}
		}
		
		if (!title.equals(XmlScriptAttribute.CONST_NULL))
		{
			new AlertDialog.Builder(context).setMessage(message).setTitle(title).show();
		}
		else
		{
			new AlertDialog.Builder(context).setMessage(message).show();			
		}
	}
	
	public String getDialogValue(Element element, String name, NodeList newParams)
	{
		String result = "";
		String type = element.getAttribute(XmlTag.TAG_SCRIPT_TYPE);
		if (element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(name))
		{
			NodeList params = element.getChildNodes();
			int paramsLen = params.getLength();	
			for (int j=0; j<paramsLen; j++)
			{
				if (params.item(j).getNodeType() == Node.ELEMENT_NODE)
				{
					Element param = (Element)params.item(j);
					
					if (param.getNodeName().equals(XmlTag.TAG_SCRIPT_VAR))
					{
						if (Function.getVariableValue(param.getAttribute(XmlTag.TAG_SCRIPT_NAME)) != null)
						{
							result = (String) Function.getVariableValue(param.getAttribute(XmlTag.TAG_SCRIPT_NAME));						
						}
						else if (Function.getParamValue(newParams, param.getAttribute(XmlTag.TAG_SCRIPT_NAME), type) != null)
						{
							result = (String) Function.getParamValue(newParams, param.getAttribute(XmlTag.TAG_SCRIPT_NAME), type);
						}
						else if (param.getNodeName().equals(XmlTag.TAG_SCRIPT_KEYWOED))
						{
							//check keyword
							result = param.getChildNodes().item(0).getNodeValue();
						}
					}	
				}						
				else if (params.item(j).getNodeType() == Node.TEXT_NODE)
				{
					result = params.item(j).getNodeValue();
				}				
			}
		}

		return result;
	}
}
