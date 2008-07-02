package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.util.Log;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_Object {
	public static Object ToNumeric(NodeList params){
		return getValue(params);
	}
	
	public static Integer ToInt(NodeList params){
		if (String.valueOf(getValue(params)).indexOf(".") != -1){
			return Double.valueOf(String.valueOf(getValue(params))).intValue();
		}
		else{
			return Integer.valueOf(String.valueOf(getValue(params)));
		}
	}
	
	public static Object ToRecord(NodeList params){
		return getValue(params);
	}
	
	public static String ToString(NodeList params){
		return String.valueOf(getValue(params));
	}
	
	private static Object getValue(NodeList params){
		Object value = null;
		Element element = (Element) params.item(0);
		if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
				(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.PARAMETER_NAME_VALUE)) &&
				(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.OBJECT))){
			Log.i("info", "element has "+element.getChildNodes().getLength()+" children");
			if (element.getChildNodes().getLength() == 1){
				Element child = (Element)element.getChildNodes().item(0);
				if (child.getNodeName().equals(ScriptTag.CALL)){
					Log.i("info", "toint call function "+child.getAttribute(ScriptTag.FUNCTION));
					value = Function.returnTypeFunction(child);
					Log.i("info", "value of toint "+value);
				}
				else if (child.getNodeName().equals(ScriptTag.VAR)){
					value = Function.getVariablesMap().get(child.getAttribute(ScriptTag.NAME)).get(1);
					Log.i("info", "get variable value in NS_Object "+value);
				}
			}
		}
		return value;
	}
}
