package com.penbase.dma.Dalyo.Function.Namespace;

import android.app.Dialog;
import android.view.ViewGroup;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Component.Form;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Element;

import java.util.HashMap;

public class NS_Form {
	public static void Clear(Element element) {
		String formId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FORM, ScriptAttribute.FORM).toString();
		ApplicationView.getLayoutsMap().get(formId).clear();
	}

	public static String GetCurrentForm(Element element) {
		return ApplicationView.getCurrentFormId();
	}

	public static void Navigate(Element element) {
		String formId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FORM, ScriptAttribute.FORM).toString();
		HashMap<String, Form> layoutsMap = ApplicationView.getLayoutsMap();
		if (layoutsMap.containsKey(formId)) {
			layoutsMap.get(formId).onLoad(
					ApplicationView.getOnLoadFuncMap().get(formId));
		}
		ApplicationView.setCurrentFormId(formId);
		ApplicationView currentView = ApplicationView.getCurrentView();
		currentView.setTitle(layoutsMap.get(formId).getTitle());
		currentView.setContentView(layoutsMap.get(formId));
	}

	@SuppressWarnings("unchecked")
	public static void SetCurrentRecord(Element element) {
		String formId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FORM, ScriptAttribute.FORM).toString();
		HashMap<Object, Object> record = (HashMap<Object, Object>) Function
				.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.RECORD,
						ScriptAttribute.RECORD);
		HashMap<String, Form> layoutsMap = ApplicationView.getLayoutsMap();
		if (layoutsMap.containsKey(formId)) {
			layoutsMap.get(formId).setRecordByForm(formId, record);
		}
	}

	public static void SetTitle(Element element) {
		String formId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FORM, ScriptAttribute.FORM).toString();
		String title = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_TITLE, ScriptAttribute.STRING)
				.toString();
		ApplicationView.getLayoutsMap().get(formId).setTitle(title);
		if (formId.equals(ApplicationView.getCurrentFormId())) {
			ApplicationView.getCurrentView().setTitle(title);
		}
	}
	
	public static void ShowDialog(Element element) {
		String formId = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.FORM, ScriptAttribute.FORM).toString();
		Form form = ApplicationView.getLayoutsMap().get(formId);
		if (form.isModal()) {
			Dialog dialog = new Dialog(ApplicationView.getCurrentView());
			ViewGroup parent = (ViewGroup) form.getParent();
			if (parent != null) {
				parent.removeView(form);
			}
			dialog.setContentView(form);
			dialog.setTitle(form.getTitle());
			dialog.show();
		}
	}
}
