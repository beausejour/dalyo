package com.penbase.dma.Dalyo.Function.ReturnType;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.penbase.dma.Dalyo.Database;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.XmlElement.ScriptAttribute;
import com.penbase.dma.XmlElement.ScriptTag;

public class GetFieldValue {
	private Object value = null;
	private String tableId = null;
	//private HashMap<String, Object> record = null;
	private Object record = null;
	private String fieldId = null;
	
	public GetFieldValue(NodeList params)
	{
		int paramsLen = params.getLength();		
		for (int i=0; i<paramsLen; i++)
		{
			Element element = (Element) params.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.hasAttribute(ScriptTag.NAME)))
			{
				if ((element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.TABLE)) &&
						(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.TABLE)))
				{
					if (element.hasChildNodes())
					{
						Element child = (Element) element.getChildNodes().item(0);
						if ((child.getNodeName().equals(ScriptTag.ELEMENT)) && 								
								(child.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.TABLE)))
						{
							Log.i("info", "element");
							tableId = child.getAttribute(ScriptTag.ELEMENT_ID);
						}
					}					
				}
				else if ((element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.RECORD)) &&
						(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.RECORD)))
				{
					if (element.hasChildNodes())
					{
						Element child = (Element) element.getChildNodes().item(0);
						if ((child.getNodeName().equals(ScriptTag.CALL)) &&
								(child.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETSELECTEDRECORD)) &&
								(child.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_COMPONENT_CB)))
						{
							Log.i("info", "vall function");
							record = new GetSelectedRecord(child.getElementsByTagName(ScriptTag.PARAMETER), null).getValue();
						}
						else if ((child.getNodeName().equals(ScriptTag.VAR)) && (child.hasAttribute(ScriptTag.NAME)))
						{
							record = Function.getVariablesMap().get(child.getAttribute(ScriptTag.NAME));
							Log.i("info", "variable for record");
						}
					}	
				}
				else if ((element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.FIELD)) &&
						(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.FIELD)))
				{
					if (element.hasChildNodes())
					{
						Element child = (Element) element.getChildNodes().item(0);
						if ((child.getNodeName().equals(ScriptTag.ELEMENT)) && 								
								(child.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.FIELD)))
						{
							Log.i("info", "field");
							fieldId = child.getAttribute(ScriptTag.ELEMENT_ID);
						}
					}						
				}
			}
		}
	}
	
	public Object getValue()
	{
		//Log.i("info", "getvalue in getfieldavalue "+record.containsKey(Database.FIELD+fieldId));
		if (record != null)
		{
			if (record instanceof HashMap)
			{
				value = ((HashMap<String, Object>) record).get(Database.FIELD+fieldId);
			}
		}
		Log.i("info", "value "+value);
		return value;
	}
}
