package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.ArrayList;
import org.w3c.dom.Element;

import android.util.Log;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_List {
	public static Object GetListItem(Element element){
		Object value = null;
		Object list = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.LIST, ScriptAttribute.LIST);
		int index = -1;
		if (Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_INDEX, ScriptAttribute.PARAMETER_TYPE_INT) != null){
			index = Integer.valueOf(String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_INDEX, ScriptAttribute.PARAMETER_TYPE_INT)));
		}
		if ((index != -1) && (((ArrayList<?>) list).size() > 0)){
			value = ((ArrayList<?>)list).get(index-1);
		}
		return value;
	}
	
	public static int GetSize(Element element){
		int value = 0;
		Object list = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.LIST, ScriptAttribute.LIST);
		if (list != null){
			value = ((ArrayList<?>)list).size();
		}
		return value;
	}
	
	public static void ListAddValue(Element element){
		String listName = String.valueOf(Function.getVariableName(element, ScriptTag.PARAMETER, ScriptAttribute.LIST, ScriptAttribute.LIST));
		Object value = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		Log.i("info", "listname "+listName+" value "+value);
		Function.addVariableValue(listName, value, true);
	}
}
