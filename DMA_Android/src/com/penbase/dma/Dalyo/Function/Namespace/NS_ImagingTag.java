package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_ImagingTag {
	
	/*
	 * <c f="startCapture" ns="imaging.tag"><p n="type" t="string">DATAMATRIX</p><p n="confirm" t="boolean"><kw>NULL</kw></p></c>
	 * */
	public static String StartCapture(Element element) {
		String type = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TYPE, ScriptAttribute.STRING).toString();
		//DATAMATRIX QRCODE
		Object confirm =  Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_CONFIRM, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		//((Boolean)confirm)
		//TODO lauch capturn activity and send intent values
		return "";
	}
}
