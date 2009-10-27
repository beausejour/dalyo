package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;

import org.w3c.dom.Element;

public class NS_DatabaseField {
	public static String GetFieldName(Element element) {
		String fieldId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		return DatabaseAdapter.getFieldName(fieldId);
	}
}
