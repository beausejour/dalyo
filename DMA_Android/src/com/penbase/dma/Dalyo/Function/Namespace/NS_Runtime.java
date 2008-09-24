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
import android.os.Message;
import android.util.Log;


public class NS_Runtime {
	private static Object result = null;
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
	
	public static Object Synchronize(Element element){
		Object type = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FACELESS, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		Log.i("info", "synchronize type "+type);
		boolean showProgress = false;
		
		if ((type == null) || (((Boolean)type).booleanValue())){
			//showProgress = true;    ProgressDialog has not done yet.
		}

		if (showProgress){
			Log.i("info", "showprogress");
			syncProgressDialog = ProgressDialog.show(Function.getContext(), "Please wait...", "Synchronizing application's data...", true, false);
			//alertDialog.show();
			
			
			result = ApplicationView.getCurrentClient().launchImport(
					ApplicationListView.getApplicationsInfo().get("AppId"),
					ApplicationListView.getApplicationsInfo().get("DbId"), 
					ApplicationListView.getApplicationsInfo().get("Username"),
					ApplicationListView.getApplicationsInfo().get("Userpassword"));
			syncProgressDialog.dismiss();
			
			/*Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.i("info", "run sync thread");
					result = ApplicationView.getCurrentClient().launchImport(
							ApplicationListView.getApplicationsInfo().get("AppId"),
							ApplicationListView.getApplicationsInfo().get("DbId"), 
							ApplicationListView.getApplicationsInfo().get("Username"),
							ApplicationListView.getApplicationsInfo().get("Userpassword"));
					handler.post(new Runnable() {
						public void run() {
							syncProgressDialog.dismiss();
						}
					});
				}
			});
			thread.setPriority(Thread.MAX_PRIORITY);
			thread.setDaemon(true);
			thread.start();
			Log.i("info", "check daemon "+thread.isDaemon());*/
			/*while (thread.isAlive()) {
				//Log.i("info", "thread is alive");
			}*/
			/*if (!thread.isAlive()) {
				handler.post(new Runnable() {
					public void run() {
						syncProgressDialog.dismiss();
					}
				});
			}*/
			
			/*Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.i("info", "thread run");
					result = ApplicationView.getCurrentClient().launchImport(
							ApplicationListView.getApplicationsInfo().get("AppId"),
							ApplicationListView.getApplicationsInfo().get("DbId"), 
							ApplicationListView.getApplicationsInfo().get("Username"),
							ApplicationListView.getApplicationsInfo().get("Userpassword"));
					//handler.sendEmptyMessage(0);
					synchronized (object) {
						object.notifyAll();
					}
				}
			});
			
			
			thread.start();
			synchronized (object) {
				try {
					object.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}*/
			Log.i("info", "sync thread finishes");
			//handler.sendEmptyMessage(0);
			
			//ApplicationView.getLayoutsMap().get(ApplicationView.getCurrentFormId()).removeView(button);
			
			return result;
			
			/*new Thread() {
				public void run() {
					Log.i("info", "run newthread "+Thread.currentThread().getName());
					Looper.prepare();
					result = ApplicationView.getCurrentClient().launchImport(
							ApplicationListView.getApplicationsInfo().get("AppId"),
							ApplicationListView.getApplicationsInfo().get("DbId"), 
							ApplicationListView.getApplicationsInfo().get("Username"),
							ApplicationListView.getApplicationsInfo().get("Userpassword"));
					synchronized (object) {
						object.notifyAll();
					}
				}
			}.start();
			
			synchronized (object) {
				try {
					object.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			return result;*/
			
			
			/*HandlerThread newThread = new HandlerThread("SynchronizeThread", Thread.MAX_PRIORITY) {
				public void run() {
					
				}
			};

			newThread.start();
			synchronized (object) {
				try {
					object.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				Log.i("info", "end of synchronized");
				
			}*/

			//handler.sendEmptyMessage(0);
			//return result;
			/*Thread thread = new Thread(new Runnable() {
				public void run() {
					Log.i("info", "thread run");
					result = ApplicationView.getCurrentClient().launchImport(
							ApplicationListView.getApplicationsInfo().get("AppId"),
							ApplicationListView.getApplicationsInfo().get("DbId"), 
							ApplicationListView.getApplicationsInfo().get("Username"),
							ApplicationListView.getApplicationsInfo().get("Userpassword"));
					//syncProgressDialog.dismiss();
					handler.sendEmptyMessage(0);
				}
			});
			thread.start();
			try {
				thread.join();
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return result;*/
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
