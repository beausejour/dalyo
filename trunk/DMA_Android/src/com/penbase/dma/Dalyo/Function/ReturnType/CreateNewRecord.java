package com.penbase.dma.Dalyo.Function.ReturnType;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.penbase.dma.Dalyo.Record;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.XmlElement.ScriptAttribute;
import com.penbase.dma.XmlElement.ScriptTag;

public class CreateNewRecord {
	String tableId = null;
	ArrayList fieldsList = null;
	ArrayList valuesList = null;
	Record record = null;
	
	public CreateNewRecord(NodeList params)
	{
		int paramsLen = params.getLength();
		for (int i=0; i<paramsLen; i++)
		{
			Element element = (Element) params.item(i);
			if (element.getNodeName().equals(ScriptTag.PARAMETER))
			{
				if ((element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.TABLE)) &&
						(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.TABLE)))
				{
					if (element.getChildNodes().getLength() == 1)
					{
						Element child = (Element)element.getChildNodes().item(0);
						if (child.getNodeName().equals(ScriptTag.ELEMENT))
						{
							tableId = child.getAttribute(ScriptTag.ELEMENT_ID);
						}
					}
				}
				else if ((element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.PARAMETER_NAME_FIELDS)) &&
						(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.LIST)))
				{
					if (element.getChildNodes().getLength() == 1)
					{
						Element child = (Element)element.getChildNodes().item(0);
						if (child.getNodeName().equals(ScriptTag.VAR))
						{
							fieldsList = Function.getVariablesMap().get(child.getAttribute(ScriptTag.NAME));
						}
					}
				}
				else if ((element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.PARAMETER_NAME_VALUES)) &&
						(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.LIST)))
				{
					if (element.getChildNodes().getLength() == 1)
					{
						Element child = (Element)element.getChildNodes().item(0);
						if (child.getNodeName().equals(ScriptTag.VAR))
						{
							valuesList = Function.getVariablesMap().get(child.getAttribute(ScriptTag.NAME));
						}
					}
				}
			}
		}
		record = new Record(tableId, fieldsList, valuesList);
	}
	
	public Record getRecord()
	{
		return record;
	}
}
