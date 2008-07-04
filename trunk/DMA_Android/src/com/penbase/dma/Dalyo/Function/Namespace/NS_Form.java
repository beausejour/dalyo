package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.HashMap;
import org.w3c.dom.Element;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

public class NS_Form {
	public static void Navigate(Element element){
		String formId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FORM, ScriptAttribute.FORM));
		if (ApplicationView.getOnLoadFuncMap().containsKey(formId)){
			ApplicationView.getLayoutsMap().get(formId).onLoad(ApplicationView.getOnLoadFuncMap().get(formId));
		}
		ApplicationView.setCurrentFormI(formId);
		ApplicationView.getCurrentView().setContentView(ApplicationView.getLayoutsMap().get(formId));
	}
	
	public static void SetCurrentRecord(Element element){
		String formId = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FORM, ScriptAttribute.FORM));
		HashMap<Object, Object> record = (HashMap<Object, Object>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.RECORD, ScriptAttribute.RECORD);
		if (ApplicationView.getLayoutsMap().containsKey(formId)){
			ApplicationView.getLayoutsMap().get(formId).setRecord(formId, record);
		}
	}
}
