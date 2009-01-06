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
	private static ProgressDialog importProgressDialog = null;
	private static ArrayList<String> tables;
	private static Object filters;
	
	public static void CancelTransaction(){
		DatabaseAdapter.rollbackTransaction();
	}
	
	public static void Export(Element element){
		
	}
	
	public static void Import(Element element){
		tables = (ArrayList<String>) Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TABLES, ScriptAttribute.LIST);
		filters = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FILTERS, ScriptAttribute.LIST);
		Object faceless = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FACELESS, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		if ((faceless == null) || (((Boolean)faceless).booleanValue())){
			//display progress dialog
			importProgressDialog = ProgressDialog.show(Function.getContext(), "Please wait...", "Synchronizing application's data non uithread...", true, false);
			new Thread(){
				public void run() {
					try {
						ApplicationView.getCurrentClient().filteredImport(
								ApplicationListView.getApplicationsInfo().get("AppId"),
								ApplicationListView.getApplicationsInfo().get("DbId"), 
								ApplicationListView.getApplicationsInfo().get("Username"),
								ApplicationListView.getApplicationsInfo().get("Userpassword"),
								tables, filters);
					}
					catch(Exception e)
					{e.printStackTrace();}
					importProgressDialog.dismiss();
				}
			}.start();
		}
		else{
			ApplicationView.getCurrentClient().filteredImport(
					ApplicationListView.getApplicationsInfo().get("AppId"),
					ApplicationListView.getApplicationsInfo().get("DbId"), 
					ApplicationListView.getApplicationsInfo().get("Username"),
					ApplicationListView.getApplicationsInfo().get("Userpassword"),
					tables, filters);
		}
	}
	
	public static void StartTransaction(){
		DatabaseAdapter.beginTransaction();
	}
	
	public static void ValidateTransaction(){
		DatabaseAdapter.commitTransaction();
	}
}
