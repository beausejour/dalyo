package com.penbase.dma.View;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.*;
import com.penbase.dma.R;
import com.penbase.dma.Constant.XmlTag;
import com.penbase.dma.Dalyo.LoadingThread;
import com.penbase.dma.Dalyo.Component.Component;
import com.penbase.dma.Dalyo.Component.Form;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.graphics.Color;

public class ApplicationView extends Activity {
	private static ApplicationView applicationView;	
	private static DmaHttpClient client;
	private static HashMap<String, Form> layoutsMap;
	private static HashMap<String, String> onLoadFuncMap;	
	private static Document designDoc = null;
	private Component component;
	private static Document behaviorDocument = null;
	private static Document dbDoc = null;
	private static HashMap<String, String> resourcesFileMap;
	private static HashMap<String, Component> componentsMap;
	private static DatabaseAdapter database;
	private LoadingThread loadingThread = null;
	private ProgressDialog loadingbar;
	private static String clientLogin;
	private static String currentFormId;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch (msg.what){
				default:
					createView();
					loadingbar.dismiss();
					display();
					break;
			}
		}
	};
	private static int currentOrientation;

	@Override
	public void onCreate(Bundle icicle){	
		super.onCreate(icicle);
		ApplicationView.applicationView = this;
		database = new DatabaseAdapter(this, dbDoc, clientLogin+"_"+ApplicationListView.getApplicationName());
		resourcesFileMap = client.getResourceMap("ext");
		componentsMap = new HashMap<String, Component>();
		new Function(this, behaviorDocument);
		setTitle(ApplicationListView.getApplicationName());
		setContentView(R.layout.loading);
		loadingbar = ProgressDialog.show(this, "Please wait...", "Building application ...", true, false);
		loadingThread = new LoadingThread(handler);
		loadingThread.Start();
	}

	private void display(){
		NodeList generalInfo = designDoc.getElementsByTagName(XmlTag.DESIGN_S_G);
		final String startFormId = ((Element) generalInfo.item(0)).getAttribute(XmlTag.DESIGN_S_G_FID);
		if (onLoadFuncMap.containsKey(startFormId)){
			layoutsMap.get(startFormId).onLoad(onLoadFuncMap.get(startFormId));
		}
		setCurrentFormI(startFormId);
		setContentView(layoutsMap.get(startFormId));
	}

	public static void prepareData(int position, String login, String pwd){
		client = new DmaHttpClient();
		client.checkDownloadFile(position, login, pwd);
		clientLogin = login;
		behaviorDocument = client.getBehavior(ApplicationListView.getApplicationsInfo().get("AppId"),
				ApplicationListView.getApplicationsInfo().get("AppVer"),
				ApplicationListView.getApplicationsInfo().get("AppBuild"),
				ApplicationListView.getApplicationsInfo().get("SubId"), login, pwd);
		
		designDoc = client.getDesign(ApplicationListView.getApplicationsInfo().get("AppId"),
				ApplicationListView.getApplicationsInfo().get("AppVer"),
				ApplicationListView.getApplicationsInfo().get("AppBuild"),
				ApplicationListView.getApplicationsInfo().get("SubId"), login, pwd);
		
		client.getResources(ApplicationListView.getApplicationsInfo().get("AppId"),
				ApplicationListView.getApplicationsInfo().get("AppVer"),
				ApplicationListView.getApplicationsInfo().get("AppBuild"),
				ApplicationListView.getApplicationsInfo().get("SubId"),login, pwd);
		
		dbDoc = client.getDB(ApplicationListView.getApplicationsInfo().get("AppId"),
				ApplicationListView.getApplicationsInfo().get("AppVer"),
				ApplicationListView.getApplicationsInfo().get("AppBuild"),
				ApplicationListView.getApplicationsInfo().get("SubId"), login, pwd);
	}

	private void generalSetup(){
		Node system = designDoc.getElementsByTagName(XmlTag.DESIGN_S).item(0);
		int childrenLen = system.getChildNodes().getLength();
		for (int i=0; i<childrenLen; i++){
			Element child = (Element) system.getChildNodes().item(i);
			if (child.getNodeName().equals(XmlTag.DESIGN_S_G)){
				if (child.hasAttribute(XmlTag.DESIGN_S_G_OS)){
					String name = child.getAttribute(XmlTag.DESIGN_S_G_OS);
					Function.createFunction(name);
				}
			}
		}
	}
	
	private void createView(){
		generalSetup();
		layoutsMap = new HashMap<String, Form>();
		onLoadFuncMap = new HashMap<String, String>();
		NodeList formsList = designDoc.getElementsByTagName(XmlTag.DESIGN_F);
		int formsListLen = formsList.getLength();
		for (int i=0; i<formsListLen; i++){
			Form form = new Form(this);
			Element formElt = (Element) formsList.item(i);
			String formId = formElt.getAttribute(XmlTag.DESIGN_F_ID);
			if (!formElt.getAttribute(XmlTag.COMPONENT_COMMON_TABLEID).equals("")){
				form.setTableId(formElt.getAttribute(XmlTag.COMPONENT_COMMON_TABLEID));
			}
			
			//Check background of a form
			if (formElt.hasAttribute(XmlTag.DESIGN_F_BC)){
				String backgourndColor = "#"+formElt.getAttribute(XmlTag.DESIGN_F_BC);
				form.setBackgroundColor(Color.parseColor(backgourndColor));
			}
			else {
				//Default background color is white
				form.setBackgroundColor(Color.WHITE);
			}
			
			layoutsMap.put(formId, form);
			
			NodeList formEltList = formElt.getChildNodes();
			int formEltListLen = formEltList.getLength();
			for (int j=0; j<formEltListLen; j++){
				Element element = (Element) formEltList.item(j);
				if ((!element.getNodeName().equals(XmlTag.COMPONENT_MENUBAR)) &&
						(!element.getNodeName().equals(XmlTag.COMPONENT_NAVIBAR))){
					component = new Component(this, element.getNodeName());
					
					if (element.hasAttribute(XmlTag.COMPONENT_COMMON_ID)){
						component.setId(element.getAttribute(XmlTag.COMPONENT_COMMON_ID));
					}
					if (element.hasAttribute(XmlTag.COMPONENT_COMMON_FONTSIZE)){
						component.setFontSize(element.getAttribute(XmlTag.COMPONENT_COMMON_FONTSIZE));
					}
					if (element.hasAttribute(XmlTag.COMPONENT_COMMON_FONTTYPE)){
						component.setFontType(element.getAttribute(XmlTag.COMPONENT_COMMON_FONTTYPE));
					}
					if (element.hasAttribute(XmlTag.COMPONENT_COMMON_ALIGN)){
						component.setAlign(element.getAttribute(XmlTag.COMPONENT_COMMON_ALIGN));
					}
					if (element.hasAttribute(XmlTag.COMPONENT_COMMON_LABEL)){
						component.setName(element.getAttribute(XmlTag.COMPONENT_COMMON_LABEL));
					}
					if (element.hasAttribute(XmlTag.COMPONENT_COMMON_TABLEID)){
						component.setTableId(element.getAttribute(XmlTag.COMPONENT_COMMON_TABLEID));
					}
					if (element.hasAttribute(XmlTag.COMPONENT_COMMON_FIELDID)){
						component.setFieldId(element.getAttribute(XmlTag.COMPONENT_COMMON_FIELDID));
					}
					if (element.hasAttribute(XmlTag.COMPONENT_COMMON_BACKGROUND)){
						component.setBackGround(Integer.valueOf(element.getAttribute(XmlTag.COMPONENT_COMMON_BACKGROUND)));
						component.setExtension(resourcesFileMap.get(element.getAttribute(XmlTag.COMPONENT_COMMON_BACKGROUND)));
					}
					if (element.hasAttribute(XmlTag.COMPONENT_TEXTFIELD_MULTI)){
						component.setMultiLine(element.getAttribute(XmlTag.COMPONENT_TEXTFIELD_MULTI));
						component.setEditable(element.hasAttribute(XmlTag.COMPONENT_TEXTFIELD_EDIT));
					}
					if (element.hasAttribute(XmlTag.COMPONENT_GAUGE_INIT)){
						component.setInitValue(Integer.valueOf(element.getAttribute(XmlTag.COMPONENT_GAUGE_INIT)));
					}
					if (element.hasAttribute(XmlTag.COMPONENT_GAUGE_MIN)){
						component.setMinValue(Integer.valueOf(element.getAttribute(XmlTag.COMPONENT_GAUGE_MIN)));
					}
					if (element.hasAttribute(XmlTag.COMPONENT_GAUGE_MAX)){
						component.setMaxValue(Integer.valueOf(element.getAttribute(XmlTag.COMPONENT_GAUGE_MAX)));
					}
					
					if (element.getNodeName().equals(XmlTag.COMPONENT_CHECKBOX)){
						if (element.hasAttribute(XmlTag.COMPONENT_CHECKBOX_CHECKED)){
							component.setChecked(element.getAttribute(XmlTag.COMPONENT_CHECKBOX_CHECKED));
						}
					}
					else if (element.getNodeName().equals(XmlTag.COMPONENT_COMBOBOX)){
						ArrayList<String> itemList = new ArrayList<String>();
						NodeList nodeItemList = element.getChildNodes();
						if (nodeItemList.getLength() > 0){
							int itemLen = nodeItemList.getLength();
							for (int k=0; k<itemLen; k++){
								Element item = (Element) nodeItemList.item(k);
								if ((item.getNodeName().equals(XmlTag.COMPONENT_COMBOBOX_ITEM)) &&
										(item.hasAttribute(XmlTag.COMPONENT_COMMON_VALUE))){
									String value = item.getAttribute(XmlTag.COMPONENT_COMMON_VALUE);
									itemList.add(value);
								}
							}
							component.setItemList(itemList);
						}
						else if ((element.hasAttribute(XmlTag.COMPONENT_COMBOBOX_LABELTABLE)) &&
								(element.hasAttribute(XmlTag.COMPONENT_COMBOBOX_LABELFIELD)) &&
								(element.hasAttribute(XmlTag.COMPONENT_COMBOBOX_VALUETABLE)) &&
								(element.hasAttribute(XmlTag.COMPONENT_COMBOBOX_VALUEFIELD))){
							ArrayList<String> labelList = new ArrayList<String>();
							labelList.add(element.getAttribute(XmlTag.COMPONENT_COMBOBOX_LABELTABLE));
							labelList.add(element.getAttribute(XmlTag.COMPONENT_COMBOBOX_LABELFIELD));
							
							ArrayList<String> valueList = new ArrayList<String>();
							valueList.add(element.getAttribute(XmlTag.COMPONENT_COMBOBOX_VALUETABLE));
							valueList.add(element.getAttribute(XmlTag.COMPONENT_COMBOBOX_VALUEFIELD));
							
							component.setLabelList(labelList);
							component.setValueList(valueList);
						}
					}
					else if (element.getNodeName().equals(XmlTag.COMPONENT_DATAVIEW)){
						ArrayList<ArrayList<String>> columnInfos = new ArrayList<ArrayList<String>>();
						HashMap<Integer, String> onCalculateMap = new HashMap<Integer, String>();
						NodeList nodeItemList = element.getChildNodes();
						if (nodeItemList.getLength() > 0){
							int nbColumn = nodeItemList.getLength();
							for (int k=0; k<nbColumn; k++){
								Element column = (Element) element.getChildNodes().item(k);
								if (column.getNodeName().equals(XmlTag.COMPONENT_DATAVIEW_COLUMN)){
									ArrayList<String> acolumn = new ArrayList<String>();
									acolumn.add(column.getAttribute(XmlTag.COMPONENT_COMMON_TABLEID));
									acolumn.add(column.getAttribute(XmlTag.COMPONENT_COMMON_FIELDID));
									acolumn.add(column.getAttribute(XmlTag.COMPONENT_DATAVIEW_COLUMN_HEADER));
									acolumn.add(column.getAttribute(XmlTag.COMPONENT_COMMON_PWIDTH));
									acolumn.add(column.getAttribute(XmlTag.COMPONENT_COMMON_LWIDTH));
									if (column.getAttribute(XmlTag.COMPONENT_DATAVIEW_COLUMN_CALC).equals("true")){
										if (column.hasAttribute(XmlTag.EVENT_ONCALCULATE)){
											onCalculateMap.put(k, column.getAttribute(XmlTag.EVENT_ONCALCULATE));
										}
										else{
											onCalculateMap.put(k, "");
										}
									}
									columnInfos.add(acolumn);
								}
							}
						}
						component.setDataviewColumns(columnInfos);
						component.setDataviewOncalculate(onCalculateMap);
					}
					else if ((element.getNodeName().equals(XmlTag.COMPONENT_DATEFIELD)) ||
							(element.getNodeName().equals(XmlTag.COMPONENT_TIMEFIELD))){
						if (element.hasAttribute(XmlTag.COMPONENT_COMMON_VALUE)){
							component.setDateTimeValue(element.getAttribute(XmlTag.COMPONENT_COMMON_VALUE));
						}
					}
					if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						currentOrientation = Configuration.ORIENTATION_LANDSCAPE;
					}
					else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			        	currentOrientation = Configuration.ORIENTATION_PORTRAIT;
			        }
					
					component.setView();
					componentsMap.put(element.getAttribute(XmlTag.COMPONENT_COMMON_ID), component);
					
					if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
						component.getView().setLayoutParams(new AbsoluteLayout.LayoutParams(
								Integer.valueOf(element.getAttribute(XmlTag.COMPONENT_COMMON_LWIDTH)),
								Integer.valueOf(element.getAttribute(XmlTag.COMPONENT_COMMON_LHEIGHT)),
								Integer.valueOf(element.getAttribute(XmlTag.COMPONENT_COMMON_LCOORDX)),
								Integer.valueOf(element.getAttribute(XmlTag.COMPONENT_COMMON_LCOORDY))));
			        }
			        else {
			        	component.getView().setLayoutParams(new AbsoluteLayout.LayoutParams(
								Integer.valueOf(element.getAttribute(XmlTag.COMPONENT_COMMON_PWIDTH)),
								Integer.valueOf(element.getAttribute(XmlTag.COMPONENT_COMMON_PHEIGHT)),
								Integer.valueOf(element.getAttribute(XmlTag.COMPONENT_COMMON_PCOORDX)),
								Integer.valueOf(element.getAttribute(XmlTag.COMPONENT_COMMON_PCOORDY))));
			        }
					form.addView(component.getView());
					
					//Add onclick event
					if (element.hasAttribute(XmlTag.EVENT_ONCLICK)){
						Log.i("info", "onclick function name "+element.getAttribute(XmlTag.EVENT_ONCLICK));
						component.setOnclickFunction(element.getAttribute(XmlTag.EVENT_ONCLICK), component.getView());
					}
					
					//Add onchange event
					if (element.hasAttribute(XmlTag.EVENT_ONCHANGE)){
						component.setOnchangeFunction(element.getAttribute(XmlTag.EVENT_ONCHANGE), component.getView());
					}
				}
			}
			//Add onload event in a hashmap for calling this function by changing form 
			onLoadFuncMap.put(formId, formElt.getAttribute(XmlTag.EVENT_ONLOAD));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add("Quit");
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case 0:
				quit();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void quit() {
		this.finish();
	}

	public static int getOrientation(){
		return currentOrientation;
	}

	public static HashMap<String, Component> getComponents(){
		return componentsMap;
	}

	public static HashMap<String, Form> getLayoutsMap(){
		return layoutsMap;
	}

	public static DatabaseAdapter getDataBase(){
		return database;
	}

	public static DmaHttpClient getCurrentClient(){
		return client;
	}

	public static void refreshComponent(String componentId, Object filter){
		if (componentsMap.containsKey(componentId)){
			componentsMap.get(componentId).refreshComponentContent(filter);
		}
	}

	public static ApplicationView getCurrentView(){
		return applicationView;
	}

	public static HashMap<String, String> getOnLoadFuncMap(){
		return onLoadFuncMap;
	}

	public static String getCurrentFormId(){
		return currentFormId;
	}
	
	public static void setCurrentFormI(String id){
		currentFormId = id;
	}
	
	public static void errorDialog(String message){
		AlertDialog dialog = new AlertDialog.Builder(applicationView).create();
		dialog.setMessage(message);
		dialog.setTitle("Error");
		dialog.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				applicationView.finish();
			}
			
		});
		dialog.show();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		//Check if there is doodle image
		getLayoutsMap().get(getCurrentFormId()).setPreview();
		//Check if there is a picturebox
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		database.closeDatabase();
	}
}
