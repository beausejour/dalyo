package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.util.Log;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_Math {
	public static Object Sum(NodeList params){
		Object left = getValue(params, ScriptAttribute.PARAMETER_NAME_A);
		Object right = getValue(params, ScriptAttribute.PARAMETER_NAME_B);
		if ((left.toString().indexOf(".") != -1) || (right.toString().indexOf(".") != -1)){
			return (Double.valueOf(left.toString()) + Double.valueOf(right.toString()));
		}
		else{
			return (Integer.valueOf(left.toString()) + Integer.valueOf(right.toString()));
		}
	}
	
	public static Object Subtract(NodeList params){
		Object left = getValue(params, ScriptAttribute.PARAMETER_NAME_A);
		Object right = getValue(params, ScriptAttribute.PARAMETER_NAME_B);
		if ((left.toString().indexOf(".") != -1) || (right.toString().indexOf(".") != -1)){
			return (Double.valueOf(left.toString()) - Double.valueOf(right.toString()));
		}
		else{
			return (Integer.valueOf(left.toString()) - Integer.valueOf(right.toString()));
		}
	}
	
	private static Object getValue(NodeList params, String name){
		int paramsLen = params.getLength();
		Object value = null;
		for(int i=0; i<paramsLen; i++){
			Element element = (Element) params.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.getAttribute(ScriptTag.NAME).equals(name)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.PARAMETER_TYPE_NUMERIC))){
				if (element.getChildNodes().getLength() == 1){
					Element child = (Element)element.getChildNodes().item(0);
					if (child.getNodeName().equals(ScriptTag.CALL)){
						Log.i("info", "Subtraction a call function  "+child.getAttribute(ScriptTag.FUNCTION));
						value = Function.returnTypeFunction(child);
					}
				}
			}
		}
		return value;
	}
}
