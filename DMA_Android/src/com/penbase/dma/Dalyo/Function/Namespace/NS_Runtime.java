package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.penbase.dma.R;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationListView;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Element;

public class NS_Runtime {
	private static ProgressDialog syncProgressDialog;
	private static boolean syncResult = false;
	private static boolean sSyncEnd = false;

	public static void Browse(Element element) {
		String url = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_URL, ScriptAttribute.STRING)
				.toString();
		ApplicationView.getCurrentView().startActivityForResult(
				new Intent(Intent.ACTION_VIEW, Uri.parse(url)), 0);
	}

	public static void Close(Element element) {
		ApplicationView.getCurrentView().quit();
	}
	
	public static void Error(Context context, Element element, boolean isError) {
		Object message = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING);
		Object title = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_CAPTION, ScriptAttribute.STRING);
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		if (isError) {
			alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		} else {
			alertDialog.setIcon(android.R.drawable.ic_dialog_info);	
		}
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
		ApplicationListView.quit();
	}

	public static String getApplicationVersion(Element element) {
		return ApplicationView.getApplicationVersion();
	}
	
	public static String getCurrentCulture() {
		return ApplicationView.getCurrentView().getResources().getConfiguration().locale.toString();
	}

	public static String GetCurrentUser() {
		return ApplicationView.getUsername();
	}

	
	public static void Hide(Element element) {
		ApplicationView.getCurrentView().moveTaskToBack(true);
	}
	
	public static void SetWaitCursor(Element element) {
		Object willShow = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_SHOW,
				ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		String text = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING)
				.toString();
		boolean value = false;
		if (willShow != null) {
			value = Boolean.parseBoolean(willShow.toString());
		}
		if (value) {
			Context context = Function.getContext();
			syncProgressDialog = ProgressDialog.show(context, context
					.getText(R.string.waiting), text, true, true);
		} else {
			if (syncProgressDialog.isShowing()) {
				syncProgressDialog.dismiss();
			}
		}
	}

	public static void StartApp(Element element) {
		String path = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_PATH, ScriptAttribute.STRING)
				.toString();
		if (path.contains("http://")) {
			ApplicationView.getCurrentView().startActivityForResult(
					new Intent(Intent.ACTION_VIEW, Uri.parse(path)), 0);
		} else if (path.contains("tel:")) {
			ApplicationView.getCurrentView().startActivityForResult(
					new Intent(Intent.ACTION_DIAL, Uri.parse(path)), 0);
		} else {
			// Different types of file
		}
	}

	public static boolean Synchronize(Element element) {
		final HashMap<String, String> applicationInfos = ApplicationView
				.getApplicationsInfo();
		Object type = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_FACELESS,
				ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		// boolean showProgress = false;

		if ((type == null) || Boolean.parseBoolean(type.toString())) {
			// showProgress = true; ProgressDialog has not done yet.
		}

		/*Context context = Function.getContext();
		syncProgressDialog = ProgressDialog.show(context, context
				.getText(R.string.waiting), context
				.getText(R.string.synchronisation), true, false);
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean importResult = ApplicationView.getCurrentClient()
						.importData(applicationInfos.get("AppId"),
								applicationInfos.get("DbId"),
								applicationInfos.get("Username"),
								applicationInfos.get("Userpassword"), null,
								null);
				if (importResult) {
					syncResult = ApplicationView.getCurrentClient().exportData(
							applicationInfos.get("AppId"),
							applicationInfos.get("DbId"),
							applicationInfos.get("Username"),
							applicationInfos.get("Userpassword"), null, null);
				}
				syncProgressDialog.dismiss();
			}
		}).start();*/
		
		ApplicationView.getCurrentView().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Context context = Function.getContext();
				syncProgressDialog = ProgressDialog.show(context, context
						.getText(R.string.waiting), context
						.getText(R.string.synchronisation), true, false);
			}
		});

		Thread syncThread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean importResult = ApplicationView.getCurrentClient()
						.importData(applicationInfos.get("AppId"),
								applicationInfos.get("DbId"),
								applicationInfos.get("Username"),
								applicationInfos.get("Userpassword"), null,
								null);
				if (importResult) {
					syncResult = ApplicationView.getCurrentClient().exportData(
							applicationInfos.get("AppId"),
							applicationInfos.get("DbId"),
							applicationInfos.get("Username"),
							applicationInfos.get("Userpassword"), null, null);
				}
				sSyncEnd = true;
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
				if (syncProgressDialog.isShowing()) {
					syncProgressDialog.dismiss();
				}
			}
		});
		return syncResult;
	}
}
