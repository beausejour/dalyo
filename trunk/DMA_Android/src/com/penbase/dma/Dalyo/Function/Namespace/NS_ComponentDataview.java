package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.HashMap;
import org.w3c.dom.Element;
import android.util.Log;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

public class NS_ComponentDataview {
	public static HashMap<Object, Object> GetSelectedRecord(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		HashMap<Object, Object> record = null;
		Log.i("info", "getselectedrecord in dataview");
		if (ApplicationView.getComponents().containsKey(componentId)) {
			record = ApplicationView.getComponents().get(componentId).getRecord();
			Log.i("info", "record in getselectedrecord "+record);
		}
		return record;
	}
	
	public static void Refresh(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		if (filter != null) {
			Log.i("info", "filter in dataview refresh "+filter.toString());	
		}
		//Order is not implemented yet
		Object order = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.ORDER, ScriptAttribute.ORDER);
		ApplicationView.refreshComponent(componentId, filter);
	}
}
