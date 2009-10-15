package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Component.Custom.Dataview.DalyoDataView;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Element;

import java.util.HashMap;

public class NS_ComponentDataview {
	public static Object GetCellValue(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object row = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_ROW, ScriptAttribute.PARAMETER_TYPE_INT);
		Object col = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_COLUMN, ScriptAttribute.PARAMETER_TYPE_INT);
		return ((DalyoDataView)ApplicationView.getComponents().get(componentId).getDalyoComponent()).getCellValue(Integer.valueOf(row.toString()), Integer.valueOf(col.toString()));
	}
	
	public static int GetColumnIndex(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		String fieldId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FIELD, ScriptAttribute.FIELD).toString();
		return ((DalyoDataView)ApplicationView.getComponents().get(componentId).getDalyoComponent()).getColumnIndex(fieldId);
	}

	public static int GetRowCount(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		return ((DalyoDataView)ApplicationView.getComponents().get(componentId).getDalyoComponent()).getRowCount();
	}
	
	public static HashMap<Object, Object> GetSelectedRecord(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		return ((DalyoDataView)ApplicationView.getComponents().get(componentId).getDalyoComponent()).getCurrentRecord();
	}
	
	public static int GetSelectedRow(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		return ((DalyoDataView)ApplicationView.getComponents().get(componentId).getDalyoComponent()).getSelectedRow();
	}
	
	public static void Refresh(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object filter = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.FILTER, ScriptAttribute.FILTER);
		Object order = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.ORDER, ScriptAttribute.ORDER);
		((DalyoDataView)ApplicationView.getComponents().get(componentId).getDalyoComponent()).refresh(filter, order);
	}

	public static void SetNumericFormat(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object col = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_COLUMN, ScriptAttribute.PARAMETER_TYPE_INT);
		Object decimal = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_DECIMALS, ScriptAttribute.PARAMETER_TYPE_INT);
		((DalyoDataView)ApplicationView.getComponents().get(componentId).getDalyoComponent()).setNumericFormat(Integer.valueOf(col.toString()), Integer.valueOf(decimal.toString()));
	}
	
	public static void SetSelectedRow(Element element) {
		String componentId = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.COMPONENT, ScriptAttribute.COMPONENT).toString();
		Object row = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_ROW, ScriptAttribute.PARAMETER_TYPE_INT);
		((DalyoDataView)ApplicationView.getComponents().get(componentId).getDalyoComponent()).setSelectedRow(Integer.valueOf(row.toString()));
	}
}
