package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationListView;
import com.penbase.dma.View.ApplicationView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;


public class NS_Runtime {
	private static ProgressDialog syncProgressDialog;
	
	public static void Browse(Element element) {
		String url = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_URL, ScriptAttribute.STRING).toString();
		ApplicationView.getCurrentView().startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(url)), 0);
	}
	
	public static void Error(Context context, Element element) {
		String message = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING).toString();
		String title = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_CAPTION, ScriptAttribute.STRING).toString();
		if (!title.equals(ScriptAttribute.CONST_NULL)) {
			new AlertDialog.Builder(context).setMessage(message).setTitle(title).show();
		}
		else {
			new AlertDialog.Builder(context).setMessage(message).show();
		}
	}
	
	public static void Exit(Element element) {
		ApplicationView.getCurrentView().quit();
	}
	
	public static String GetCurrentUser(Element element) {
		SharedPreferences prefs = ApplicationView.getCurrentView().getSharedPreferences(Constant.PREFNAME, Context.MODE_PRIVATE);
		return prefs.getString("Username", "");
	}
	
	public static void StartApp(Element element) {
		String path = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_PATH, ScriptAttribute.STRING).toString();
		if (path.contains("http://")) {
			ApplicationView.getCurrentView().startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(path)), 0);
		}
		else if (path.contains("tel:")) {
			ApplicationView.getCurrentView().startActivityForResult(new Intent(Intent.ACTION_DIAL, Uri.parse(path)), 0);
		}
		else {
			//Different types of file
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
