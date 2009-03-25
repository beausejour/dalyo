package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.ArrayList;

import org.w3c.dom.Element;

import android.app.ProgressDialog;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationListView;
import com.penbase.dma.View.ApplicationView;

public class NS_Database {
	private static boolean exportResult = false;
	private static boolean importResult = false;
	
	public static void CancelTransaction() {
		DatabaseAdapter.rollbackTransaction();
	}
	
	@SuppressWarnings("unchecked")
	public static boolean Export(Element element) {
		final ArrayList<String> tables = (ArrayList<String>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TABLES, ScriptAttribute.LIST);
		final Object filters = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FILTERS, ScriptAttribute.LIST);
		Object faceless = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FACELESS, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		
		if ((faceless == null) || (((Boolean)faceless).booleanValue())) {
			//display progress dialog
			final ProgressDialog exportProgressDialog = ProgressDialog.show(Function.getContext(), "Please wait...", "Importing application's data...", true, false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						exportResult = ApplicationView.getCurrentClient().exportData(
								ApplicationListView.getApplicationsInfo().get("AppId"),
								ApplicationListView.getApplicationsInfo().get("DbId"), 
								ApplicationListView.getApplicationsInfo().get("Username"),
								ApplicationListView.getApplicationsInfo().get("Userpassword"),
								tables, filters);
					} catch(Exception e) {
						e.printStackTrace();
					}
					exportProgressDialog.dismiss();
				}
			}).start();
		}
		else {
			exportResult = ApplicationView.getCurrentClient().exportData(
					ApplicationListView.getApplicationsInfo().get("AppId"),
					ApplicationListView.getApplicationsInfo().get("DbId"), 
					ApplicationListView.getApplicationsInfo().get("Username"),
					ApplicationListView.getApplicationsInfo().get("Userpassword"),
					tables, filters);
		}
		return exportResult;
	}
	
	public static String GetTableByName(Element element) {
		String tableName = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TABLENAME, ScriptAttribute.STRING).toString();
		//return table id
		return DatabaseAdapter.getTableIdByName(tableName);
	}
	
	@SuppressWarnings("unchecked")
	public static boolean Import(Element element) {
		final ArrayList<String> tables = (ArrayList<String>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TABLES, ScriptAttribute.LIST);
		final Object filters = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FILTERS, ScriptAttribute.LIST);
		Object faceless = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FACELESS, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		if ((faceless == null) || (((Boolean)faceless).booleanValue())) {
			//display progress dialog
			final ProgressDialog importProgressDialog = ProgressDialog.show(Function.getContext(), "Please wait...", "Importing application's data...", true, false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						importResult = ApplicationView.getCurrentClient().importData(
								ApplicationListView.getApplicationsInfo().get("AppId"),
								ApplicationListView.getApplicationsInfo().get("DbId"), 
								ApplicationListView.getApplicationsInfo().get("Username"),
								ApplicationListView.getApplicationsInfo().get("Userpassword"),
								tables, filters);
					} catch(Exception e) {
						e.printStackTrace();
					}
					importProgressDialog.dismiss();
				}				
			}).start();
		}
		else {
			importResult = ApplicationView.getCurrentClient().importData(
					ApplicationListView.getApplicationsInfo().get("AppId"),
					ApplicationListView.getApplicationsInfo().get("DbId"), 
					ApplicationListView.getApplicationsInfo().get("Username"),
					ApplicationListView.getApplicationsInfo().get("Userpassword"),
					tables, filters);
		}
		return importResult;
	}
	
	public static void StartTransaction() {
		DatabaseAdapter.beginTransaction();
	}
	
	public static void ValidateTransaction() {
		DatabaseAdapter.commitTransaction();
	}
}
