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
	private static boolean result = false;
	private static ProgressDialog syncProgressDialog;
	
	public static void Alert(Context context, Element element){
		String message = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING));
		String title = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_CAPTION, ScriptAttribute.STRING));
		Log.i("info", "message "+message+" title "+title);
		if (!title.equals(ScriptAttribute.CONST_NULL)){
			new AlertDialog.Builder(context).setMessage(message).setTitle(title).show();
		}
		else{
			new AlertDialog.Builder(context).setMessage(message).show();
		}
	}
	
	public static boolean Synchronize(Element element){
		Object type = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FACELESS, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		Log.i("info", "synchronize type "+type);
		boolean showProgress = false;
		if ((type == null) || (((Boolean)type).booleanValue())){
			showProgress = true;
		}

		if (showProgress){
			Log.i("info", "showprogress");
			syncProgressDialog = ProgressDialog.show(Function.getContext(), "Please wait...", "Synchronizing application's data...", true, false);
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.i("info", "thread run");
					result = ApplicationView.getCurrentClient().launchImport(
							ApplicationListView.getApplicationsInfo().get("AppId"),
							ApplicationListView.getApplicationsInfo().get("DbId"), 
							ApplicationListView.getApplicationsInfo().get("Username"),
							ApplicationListView.getApplicationsInfo().get("Userpassword"));
					syncProgressDialog.dismiss();
				}
			});
			thread.start();
			try {
				thread.join();
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return result;
		}
		else{
			Log.i("info", "else ");
			return ApplicationView.getCurrentClient().launchImport(
					ApplicationListView.getApplicationsInfo().get("AppId"),
					ApplicationListView.getApplicationsInfo().get("DbId"), 
					ApplicationListView.getApplicationsInfo().get("Username"),
					ApplicationListView.getApplicationsInfo().get("Userpassword"));
		}
	}
}
