package com.penbase.dma.Dalyo.Function.Namespace;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationListView;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Element;

public class NS_Runtime {
	private static ProgressDialog syncProgressDialog;
	private static boolean syncResult = false;
	
	public static void Browse(Element element) {
		String url = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_URL, ScriptAttribute.STRING).toString();
		ApplicationView.getCurrentView().startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(url)), 0);
	}
	
	public static void Error(Context context, Element element) {
		Object message = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING);
		Object title = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_CAPTION, ScriptAttribute.STRING);
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		if (message == null) {
			alertDialog.setMessage("");
		} else {
			alertDialog.setMessage(message.toString());
		}
		if (title == null) {
			alertDialog.setTitle("");
		} else {
			alertDialog.setTitle(title.toString());
		}
		alertDialog.show();
	}
	
	public static void Exit(Element element) {
		ApplicationView.getCurrentView().quit();
		NS_Timer.cancelAll();
		NS_Gps.Stop();
	}
	
	public static String getApplicationVersion(Element element) {
		return ApplicationView.getApplicationVersion();
	}
	
	public static String GetCurrentUser(Element element) {
		SharedPreferences prefs = ApplicationView.getCurrentView().getSharedPreferences(Constant.PREFNAME, Context.MODE_PRIVATE);
		return prefs.getString("Username", "");
	}
	
	public static void SetWaitCursor(Element element) {
		Object willShow = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_SHOW, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		String text = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING).toString();
		if ((Boolean)willShow) {
			syncProgressDialog.dismiss();
		} else {
			if (syncProgressDialog.isShowing()) {
				syncProgressDialog = ProgressDialog.show(Function.getContext(), "Please wait...", text, true, false);
			}
		}
	}
	
	public static void StartApp(Element element) {
		String path = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_PATH, ScriptAttribute.STRING).toString();
		if (path.contains("http://")) {
			ApplicationView.getCurrentView().startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(path)), 0);
		} else if (path.contains("tel:")) {
			ApplicationView.getCurrentView().startActivityForResult(new Intent(Intent.ACTION_DIAL, Uri.parse(path)), 0);
		} else {
			//Different types of file
		}
	}
	
	public static boolean Synchronize(Element element) {
		Object type = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FACELESS, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		Log.i("info", "synchronize type "+type);
		boolean showProgress = false;
		
		if ((type == null) || (((Boolean)type).booleanValue())) {
			//showProgress = true;    ProgressDialog has not done yet.
		}
		
		ApplicationView.getCurrentView().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				syncProgressDialog = ProgressDialog.show(Function.getContext(), "Please wait...", "Synchronizing application's data...", true, false);
			}
		});
		
		Thread syncThread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean importResult = ApplicationView.getCurrentClient().importData(
						ApplicationListView.getApplicationsInfo().get("AppId"),
						ApplicationListView.getApplicationsInfo().get("DbId"), 
						ApplicationListView.getApplicationsInfo().get("Username"),
						ApplicationListView.getApplicationsInfo().get("Userpassword"), null, null);
				if (importResult) {
					syncResult = ApplicationView.getCurrentClient().exportData(
							ApplicationListView.getApplicationsInfo().get("AppId"),
							ApplicationListView.getApplicationsInfo().get("DbId"), 
							ApplicationListView.getApplicationsInfo().get("Username"),
							ApplicationListView.getApplicationsInfo().get("Userpassword"), null, null);
				}
			}
		});
		syncThread.start();
		try {
			syncThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ApplicationView.getCurrentView().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				syncProgressDialog.dismiss();
			}
		});
		return syncResult;
		/*if (showProgress) {
			syncProgressDialog = ProgressDialog.show(Function.getContext(), "Please wait...", "Synchronizing application's data...", true, false);
			
			boolean importResult = ApplicationView.getCurrentClient().importData(
					ApplicationListView.getApplicationsInfo().get("AppId"),
					ApplicationListView.getApplicationsInfo().get("DbId"), 
					ApplicationListView.getApplicationsInfo().get("Username"),
					ApplicationListView.getApplicationsInfo().get("Userpassword"), null, null);
			if (importResult) {
				result = ApplicationView.getCurrentClient().exportData(
						ApplicationListView.getApplicationsInfo().get("AppId"),
						ApplicationListView.getApplicationsInfo().get("DbId"), 
						ApplicationListView.getApplicationsInfo().get("Username"),
						ApplicationListView.getApplicationsInfo().get("Userpassword"), null, null);
			}
			syncProgressDialog.dismiss();
			return result;
		} else {
			boolean importResult = ApplicationView.getCurrentClient().importData(
					ApplicationListView.getApplicationsInfo().get("AppId"),
					ApplicationListView.getApplicationsInfo().get("DbId"), 
					ApplicationListView.getApplicationsInfo().get("Username"),
					ApplicationListView.getApplicationsInfo().get("Userpassword"), null, null);
			if (importResult) {
				result = ApplicationView.getCurrentClient().exportData(
						ApplicationListView.getApplicationsInfo().get("AppId"),
						ApplicationListView.getApplicationsInfo().get("DbId"), 
						ApplicationListView.getApplicationsInfo().get("Username"),
						ApplicationListView.getApplicationsInfo().get("Userpassword"), null, null);
			}
			return result;
		}*/
	}
}
