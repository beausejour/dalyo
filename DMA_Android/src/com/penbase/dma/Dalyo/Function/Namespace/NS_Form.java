package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.HashMap;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

public class NS_Form {
	public static void Clear(Element element) {
		String formId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FORM, ScriptAttribute.FORM).toString();
		ApplicationView.getLayoutsMap().get(formId).clear();
	}
	
	public static String GetCurrentForm(Element element) {
		return ApplicationView.getCurrentFormId();
	}
	
	public static void Navigate(Element element) {
		String formId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FORM, ScriptAttribute.FORM).toString();
		if (ApplicationView.getOnLoadFuncMap().containsKey(formId)) {
			ApplicationView.getLayoutsMap().get(formId).onLoad(ApplicationView.getOnLoadFuncMap().get(formId));
		}
		ApplicationView.setCurrentFormId(formId);
		ApplicationView.getCurrentView().setTitle(ApplicationView.getLayoutsMap().get(formId).getTitle());
		ApplicationView.getCurrentView().setContentView(ApplicationView.getLayoutsMap().get(formId));
	}
	
	public static void SetCurrentRecord(Element element) {
		String formId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FORM, ScriptAttribute.FORM).toString();
		HashMap<Object, Object> record = (HashMap<Object, Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		if (ApplicationView.getLayoutsMap().containsKey(formId)) {
			ApplicationView.getLayoutsMap().get(formId).setRecord(formId, record);
		}
	}
	
	public static void SetTitle(Element element) {
		String formId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FORM, ScriptAttribute.FORM).toString();
		String title = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TITLE, ScriptAttribute.STRING).toString();
		ApplicationView.getLayoutsMap().get(formId).setTitle(title);
	}
}
