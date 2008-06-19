package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_Filter {
	/*	Criteria in variable map: varName, type, null, [lfield, oeprator, value, link]* 
	 * */
	public static void AddCriteria(NodeList params){
		String varName = String.valueOf(getValue(params, ScriptAttribute.FILTER, ScriptAttribute.FILTER));
		String lField = String.valueOf(getValue(params, ScriptAttribute.PARAMETER_NAME_ELEMENT, ScriptAttribute.OBJECT));
		String operator = String.valueOf(getValue(params, ScriptAttribute.OPERATOR, ScriptAttribute.OPERATOR));
		Object value = getValue(params, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		Object link = getValue(params, ScriptAttribute.PARAMETER_NAME_LINK, ScriptAttribute.OPERATOR);

		Function.getVariablesMap().get(varName).add(lField);
		Function.getVariablesMap().get(varName).add(operator);
		Function.getVariablesMap().get(varName).add(value);
		Function.getVariablesMap().get(varName).add(link);
	}
	
	private static Object getValue(NodeList params, String name, String type){
		Object value = null;
		int paramsLen = params.getLength();
		for (int i=0; i<paramsLen; i++){
			Element element = (Element) params.item(i);
			if ((element.getAttribute(ScriptTag.NAME).equals(name)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(type))){
				if (element.getChildNodes().getLength() == 1){
					if (element.getChildNodes().item(0).getNodeType() == Node.ELEMENT_NODE){
						Element child = (Element) element.getChildNodes().item(0);
						if (child.getNodeName().equals(ScriptTag.VAR)){
							if (name.equals(ScriptAttribute.FILTER)){
								value = child.getAttribute(ScriptTag.NAME);
							}
							else if (name.equals(ScriptAttribute.PARAMETER_NAME_VALUE)){
								value = Function.getVariablesMap().get(child.getAttribute(ScriptTag.NAME)).get(1);
							}
						}
						else if ((child.getNodeName().equals(ScriptTag.ELEMENT)) && 
								(child.hasAttribute(ScriptTag.TYPE))){
							value = child.getAttribute(ScriptTag.ELEMENT_ID);
						}
						else if (child.getNodeName().equals(ScriptTag.CALL)){
							value = Function.returnTypeFunction(child);
						}
						else if (child.getNodeName().equals(ScriptTag.KEYWOED)){
							value = Function.getKeyWord(child);
						}
					}
					else if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE){
						value = element.getChildNodes().item(0).getNodeValue();
					}
				}
			}
		}
		return value;
	}
}
