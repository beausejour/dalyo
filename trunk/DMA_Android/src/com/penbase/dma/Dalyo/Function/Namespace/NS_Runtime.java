package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
import android.view.UIThreadUtilities;

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
	
	public static void Alert(Context context, NodeList params, NodeList newParams){
		String message = String.valueOf(getValue(params, ScriptAttribute.PARAMETER_NAME_TEXT, ScriptAttribute.STRING, newParams));
		String title = String.valueOf(getValue(params, ScriptAttribute.PARAMETER_NAME_CAPTION, ScriptAttribute.STRING, newParams));
		if (!title.equals(ScriptAttribute.CONST_NULL)){
			new AlertDialog.Builder(context).setMessage(message).setTitle(title).show();
		}
		else{
			new AlertDialog.Builder(context).setMessage(message).show();
		}
	}
	
	public static boolean Synchronize(NodeList items){
		Object type = getValue(items, ScriptAttribute.PARAMETER_NAME_FACELESS, ScriptAttribute.PARAMETER_TYPE_BOOLEAN, null);
		Log.i("info", "synchronize type "+type);
		boolean showProgress = false;
		if ((type == null) || (((Boolean)type).booleanValue())){
			showProgress = true;
		}

		if (showProgress){
			if (showen){
				if (UIThreadUtilities.isUIThread(ApplicationView.getLayoutsMap().get(ApplicationView.getCurrentFormId()))){
					showen = false;
				}
				UIThreadUtilities.runOnUIThread(ApplicationView.getCurrentView(), new Runnable(){
					@Override
					public void run() {
						syncProgressDialog = ProgressDialog.show(Function.getContext(), "Please wait...", 
								"Synchronizing application's data...", true, false);
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
			}
			else{
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
			}
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
	
	private static Object getValue(NodeList items, String name, String type, NodeList newParams){
		Object value = null;
		int itemsLen = items.getLength();
		for (int i=0; i<itemsLen; i++){
			Element element = (Element) items.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.getAttribute(ScriptTag.NAME).equals(name)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(type))){
				NodeList elements = element.getChildNodes();
				if (element.getChildNodes().getLength() == 1){
					if (element.getChildNodes().item(0).getNodeType() == Node.ELEMENT_NODE){
						Element elt = (Element) elements.item(0);
						if (elt.getNodeName().equals(ScriptTag.KEYWOED)){
							value = Function.getKeyWord(elt);
						}
						else if (elt.getNodeName().equals(ScriptTag.VAR)){
							if (Function.getVariableValue(elt.getAttribute(ScriptTag.NAME)) != null){
								value = (String) Function.getVariableValue(elt.getAttribute(ScriptTag.NAME));
							}
							else if (Function.getParamValue(newParams, elt.getAttribute(ScriptTag.NAME), type) != null){
								value = (String) Function.getParamValue(newParams, elt.getAttribute(ScriptTag.NAME), type);
							}
							else if (elt.getNodeName().equals(ScriptTag.KEYWOED)){
								value = Function.getKeyWord(elt);
							}
						}
					}
					else if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE){
						value = element.getChildNodes().item(0).getNodeValue();
					}
				}
			}
		}
		return value;
	}
}
