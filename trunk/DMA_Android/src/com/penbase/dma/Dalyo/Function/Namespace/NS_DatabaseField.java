package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_DatabaseField {
	public static String GetFieldName(Element element) {
		String fieldId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FIELD, ScriptAttribute.FIELD));
		return DatabaseAdapter.getFieldName(fieldId);
	}
}
