package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationListView;
import com.penbase.dma.View.ApplicationView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;


public class NS_Runtime {
	private static ProgressDialog syncProgressDialog;
	
	public static void Error(Context context, Element element) {
		String message = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING).toString();
		String title = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_CAPTION, ScriptAttribute.STRING).toString();
		Log.i("info", "message "+message+" title "+title);
		if (!title.equals(ScriptAttribute.CONST_NULL)) {
			new AlertDialog.Builder(context).setMessage(message).setTitle(title).show();
		}
		else {
			new AlertDialog.Builder(context).setMessage(message).show();
		}
	}
	
	public static boolean Synchronize(Element element) {
		Object type = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FACELESS, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		Log.i("info", "synchronize type "+type);
		boolean showProgress = false;
		boolean result = false;
		
		if ((type == null) || (((Boolean)type).booleanValue())) {
			//showProgress = true;    ProgressDialog has not done yet.
		}

		if (showProgress) {
			Log.i("info", "showprogress");
			syncProgressDialog = ProgressDialog.show(Function.getContext(), "Please wait...", "Synchronizing application's data...", true, false);
			
			
			boolean importResult = ApplicationView.getCurrentClient().launchImport(
					ApplicationListView.getApplicationsInfo().get("AppId"),
					ApplicationListView.getApplicationsInfo().get("DbId"), 
					ApplicationListView.getApplicationsInfo().get("Username"),
					ApplicationListView.getApplicationsInfo().get("Userpassword"));
			if (importResult) {
				result = ApplicationView.getCurrentClient().launchExport(
						ApplicationListView.getApplicationsInfo().get("AppId"),
						ApplicationListView.getApplicationsInfo().get("DbId"), 
						ApplicationListView.getApplicationsInfo().get("Username"),
						ApplicationListView.getApplicationsInfo().get("Userpassword"));
			}
			syncProgressDialog.dismiss();
			return result;
		}
		else {
			Log.i("info", "else ");
			boolean importResult = ApplicationView.getCurrentClient().launchImport(
					ApplicationListView.getApplicationsInfo().get("AppId"),
					ApplicationListView.getApplicationsInfo().get("DbId"), 
					ApplicationListView.getApplicationsInfo().get("Username"),
					ApplicationListView.getApplicationsInfo().get("Userpassword"));
			if (importResult) {
				result = ApplicationView.getCurrentClient().launchExport(
						ApplicationListView.getApplicationsInfo().get("AppId"),
						ApplicationListView.getApplicationsInfo().get("DbId"), 
						ApplicationListView.getApplicationsInfo().get("Username"),
						ApplicationListView.getApplicationsInfo().get("Userpassword"));
			}
			return result;
		}
	}
	
	//<c f="getCurrentUser" ns="runtime"/>
}
