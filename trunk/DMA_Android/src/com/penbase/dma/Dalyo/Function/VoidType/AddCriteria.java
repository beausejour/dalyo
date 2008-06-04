package com.penbase.dma.Dalyo.Function.VoidType;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.util.Log;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.XmlElement.ScriptAttribute;
import com.penbase.dma.XmlElement.ScriptTag;

public class AddCriteria {
	String varName = null;
	String lField = null;
	String operator = null;
	Object value = null;
	Object link = null;
	/*	Criteria in variable map: varName, type, null, number of conditions (3 for only one condition, 
	 *  4 for condition plus link), lfield, oeprator, value, nbCondition/link 
	 * */
	
	public AddCriteria(NodeList params)
	{
		int paramsLen = params.getLength();
		for (int i=0; i<paramsLen; i++)
		{
			Log.i("info", "add criteria loop "+i);
			Element element = (Element) params.item(i);
			if ((element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.FILTER)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.FILTER)))				
			{
				if (element.getChildNodes().getLength() == 1)
				{
					Element child = (Element) element.getChildNodes().item(0);
					if (child.getNodeName().equals(ScriptTag.VAR))
					{
						Log.i("info", "add criteria add var");
						varName = child.getAttribute(ScriptTag.NAME);
					}					 
				}
			}
			else if ((element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.PARAMETER_NAME_ELEMENT)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.PARAMETER_TYPE_OBJECT)))				
			{
				//elt field
				if (element.getChildNodes().getLength() == 1)
				{
					Element child = (Element) element.getChildNodes().item(0);
					if ((child.getNodeName().equals(ScriptTag.ELEMENT)) && 
							(child.hasAttribute(ScriptTag.TYPE)))
					{
						Log.i("info", "add criteria setup field");
						lField = child.getAttribute(ScriptTag.ELEMENT_ID);
					}					 
				}
			}
			else if ((element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.OPERATOR)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.OPERATOR)))			
			{
				//operator
				if ((element.getChildNodes().getLength() == 1) && 
						(element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE))
				{
					Log.i("info", "add criteria setup operator");
					operator = element.getChildNodes().item(0).getNodeValue();
				}
			}
			else if ((element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.PARAMETER_NAME_VALUE)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.PARAMETER_TYPE_OBJECT)))				
			{
				//object value
				if (element.getChildNodes().getLength() == 1)
				{
					Element child = (Element) element.getChildNodes().item(0);
					if ((child.getNodeName().equals(ScriptTag.CALL)) && 
							(child.hasAttribute(ScriptTag.FUNCTION))) 							
					{
						Log.i("info", "add criteria call function");
						value = Function.returnTypeFunction(child);
					}
				}
			}
			else if ((element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.PARAMETER_NAME_LINK)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.OPERATOR)))				
			{
				//link
				if (element.getChildNodes().getLength() == 1)
				{
					Element child = (Element) element.getChildNodes().item(0);
					if (child.getNodeName().equals(ScriptTag.KEYWOED)) 						 						
					{
						Log.i("info", "add criteria setup keyword");
						link = Function.getKeyWord(child);
					}
				}
			}
		}		
		
		//Function.getVariablesMap().get(varName).add("4");
		Function.getVariablesMap().get(varName).add(lField);
		Function.getVariablesMap().get(varName).add(operator);
		Function.getVariablesMap().get(varName).add(value);
		Function.getVariablesMap().get(varName).add(link);
	}
}
