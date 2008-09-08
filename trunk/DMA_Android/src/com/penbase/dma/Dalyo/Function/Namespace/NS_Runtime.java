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
import android.os.Handler;
import android.util.Log;
//import android.view.UIThreadUtilities;

public class NS_Runtime {
	private static boolean result = false;
	private static ProgressDialog syncProgressDialog = null;
	private static boolean showen = false; 
	private static Handler handler = new Handler();
	private static Runnable runnable = new Runnable() {
		public void run() {
			syncProgressDialog.dismiss();
		}
	}; 
	
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
			/*if (showen){
				if (UIThreadUtilities.isUIThread(ApplicationView.getLayoutsMap().get(ApplicationView.getCurrentFormId()))){
					showen = false;
				}
				UIThreadUtilities.runOnUIThread(ApplicationView.getCurrentView(), new Runnable(){
					@Override
					public void run() {
						syncProgressDialog = ProgressDialog.show(Function.getContext(), "Please wait...", 
								"Synchronizing application's data uithread...", true, false);
						new Thread(){
							public void run() {
								try {
									result = ApplicationView.getCurrentClient().launchImport(
											ApplicationListView.getApplicationsInfo().get("AppId"),
											ApplicationListView.getApplicationsInfo().get("DbId"), 
											ApplicationListView.getApplicationsInfo().get("Username"),
											ApplicationListView.getApplicationsInfo().get("Userpassword"));
								}
								catch(Exception e)
								{e.printStackTrace();}
								handler.post(runnable);
							}
						}.start();
					}
				});
			}*/
			/*else{
				syncProgressDialog = ProgressDialog.show(Function.getContext(), "Please wait...", "Synchronizing application's data non uithread...", true, false);
				new Thread(){
					public void run() {
						try {
							result = ApplicationView.getCurrentClient().launchImport(
									ApplicationListView.getApplicationsInfo().get("AppId"),
									ApplicationListView.getApplicationsInfo().get("DbId"), 
									ApplicationListView.getApplicationsInfo().get("Username"),
									ApplicationListView.getApplicationsInfo().get("Userpassword"));
							showen = true;
						}
						catch(Exception e)
						{e.printStackTrace();}
						syncProgressDialog.dismiss();
					}
				}.start();
			}*/
			syncProgressDialog = ProgressDialog.show(Function.getContext(), "Please wait...", "Synchronizing application's data...", true, false);
			new Thread(){
				public void run() {
					try {
						result = ApplicationView.getCurrentClient().launchImport(
								ApplicationListView.getApplicationsInfo().get("AppId"),
								ApplicationListView.getApplicationsInfo().get("DbId"), 
								ApplicationListView.getApplicationsInfo().get("Username"),
								ApplicationListView.getApplicationsInfo().get("Userpassword"));
						showen = true;
					}
					catch(Exception e)
					{e.printStackTrace();}
					syncProgressDialog.dismiss();
				}
			}.start();
			return result;
		}
		else{
			return ApplicationView.getCurrentClient().launchImport(
					ApplicationListView.getApplicationsInfo().get("AppId"),
					ApplicationListView.getApplicationsInfo().get("DbId"), 
					ApplicationListView.getApplicationsInfo().get("Username"),
					ApplicationListView.getApplicationsInfo().get("Userpassword"));
		}
	}
}
