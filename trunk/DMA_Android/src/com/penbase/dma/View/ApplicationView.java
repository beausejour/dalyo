package com.penbase.dma.View;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.penbase.dma.R;
import com.penbase.dma.Constant.DesignTag;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AbsoluteLayout;
import android.graphics.Color;

public class ApplicationView extends Activity {
	private static Menu currentMenu;
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
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
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
	public void onCreate(Bundle icicle) {	
		super.onCreate(icicle);
		ApplicationView.applicationView = this;
		database = new DatabaseAdapter(this, dbDoc, clientLogin+"_"+ApplicationListView.getApplicationName());
		resourcesFileMap = client.getResourceMap("ext");
		componentsMap = new HashMap<String, Component>();
		Log.i("info", "parsing function document");
		new Function(this, behaviorDocument);
		Log.i("info", "end of parsing");
		setContentView(R.layout.loading);
		loadingbar = ProgressDialog.show(this, "Please wait...", "Building application ...", true, false);
		loadingThread = new LoadingThread(handler);
        setTitle(ApplicationListView.getApplicationName());
		loadingThread.Start();
	}

	private void display() {
		NodeList generalInfo = designDoc.getElementsByTagName(DesignTag.DESIGN_S_G);
		final String startFormId = ((Element) generalInfo.item(0)).getAttribute(DesignTag.DESIGN_S_G_FID);
		if (onLoadFuncMap.containsKey(startFormId)) {
			layoutsMap.get(startFormId).onLoad(onLoadFuncMap.get(startFormId));
		}
		currentFormId = startFormId;
		setTitle(layoutsMap.get(startFormId).getTitle());
		setContentView(layoutsMap.get(startFormId));
		Log.i("info", "end of load first function");
	}

	public static void prepareData(int position, String login, String pwd) {
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

	private void generalSetup() {
		Node system = designDoc.getElementsByTagName(DesignTag.DESIGN_S).item(0);
		int childrenLen = system.getChildNodes().getLength();
		for (int i=0; i<childrenLen; i++) {
			Element child = (Element) system.getChildNodes().item(i);
			if (child.getNodeName().equals(DesignTag.DESIGN_S_G)) {
				if (child.hasAttribute(DesignTag.DESIGN_S_G_OS)) {
					String name = child.getAttribute(DesignTag.DESIGN_S_G_OS);
					Log.i("info", "on start function");
					Function.createFunction(name);
				}
			}
		}
		Log.i("info"," end of general setup");
	}
	
	private void createView() {
		generalSetup();
		layoutsMap = new HashMap<String, Form>();
		onLoadFuncMap = new HashMap<String, String>();
		NodeList formsList = designDoc.getElementsByTagName(DesignTag.DESIGN_F);
		int formsListLen = formsList.getLength();
		for (int i=0; i<formsListLen; i++) {
			Form form = new Form(this);
			Element formElt = (Element) formsList.item(i);
			String formId = formElt.getAttribute(DesignTag.DESIGN_F_ID);
			if (!formElt.getAttribute(DesignTag.COMPONENT_COMMON_TABLEID).equals("")) {
				form.setTableId(formElt.getAttribute(DesignTag.COMPONENT_COMMON_TABLEID));
			}
			
			//Check background of a form
			if (formElt.hasAttribute(DesignTag.DESIGN_F_BC)) {
				String backgourndColor = "#"+formElt.getAttribute(DesignTag.DESIGN_F_BC);
				form.setBackgroundColor(Color.parseColor(backgourndColor));
			}
			else {
				//Default background color is white
				form.setBackgroundColor(Color.WHITE);
			}
			
			//Check form's title
			if (formElt.hasAttribute(DesignTag.DESIGN_F_TITLE)) {
				String title = formElt.getAttribute(DesignTag.DESIGN_F_TITLE);
				form.setTitle(title);
			}
			
			layoutsMap.put(formId, form);
			
			NodeList formEltList = formElt.getChildNodes();
			int formEltListLen = formEltList.getLength();
			for (int j=0; j<formEltListLen; j++) {
				Element element = (Element) formEltList.item(j);
				if (element.getNodeName().equals(DesignTag.COMPONENT_MENUBAR)) {
					//Menu item name list and onclick event list
					ArrayList<String> menuItemNameList = new ArrayList<String>(); 
					ArrayList<String> menuItemOnClickList = new ArrayList<String>();
					
					NodeList menuList = element.getChildNodes();
					if (menuList.getLength() > 0) {
						int menuLen = menuList.getLength();
						for (int k=0; k<menuLen; k++) {
							Element menu = (Element) menuList.item(k);
							if (menu.getNodeName().equals(DesignTag.COMPONENT_MENU)) {
								NodeList menuItemList = menu.getChildNodes();
								if (menuItemList.getLength() > 0) {
									int menuItemLen = menuItemList.getLength();
									for (int l=0; l<menuItemLen; l++) {
										Element menuItem = (Element) menuItemList.item(l);
										if (menuItem.getNodeName().equals(DesignTag.COMPONENT_MENUITEM)) {
											if (menuItem.hasAttribute(DesignTag.COMPONENT_COMMON_NAME)) {
												menuItemNameList.add(menuItem.getAttribute(DesignTag.COMPONENT_COMMON_NAME));
												if (menuItem.hasAttribute(DesignTag.EVENT_ONCLICK)) {
													menuItemOnClickList.add(menuItem.getAttribute(DesignTag.EVENT_ONCLICK));
												}
												else {
													menuItemOnClickList.add("");
												}
											}
										}
									}
								}
								else {
									menuItemNameList.add(menu.getAttribute(DesignTag.COMPONENT_COMMON_NAME));
									if (menu.hasAttribute(DesignTag.EVENT_ONCLICK)) {
										menuItemOnClickList.add(menu.getAttribute(DesignTag.EVENT_ONCLICK));
									}
									else {
										menuItemOnClickList.add("");
									}
								}
							}
						}
					}
					form.setMenuItemNameList(menuItemNameList);
					form.setMenuItemOnClickList(menuItemOnClickList);
				}
				else if (element.getNodeName().equals(DesignTag.COMPONENT_NAVIBAR)) {
					
				}
				else {
					component = new Component(this, element.getNodeName());
					
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_ID)) {
						component.setId(element.getAttribute(DesignTag.COMPONENT_COMMON_ID));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_FONTSIZE)) {
						component.setFontSize(element.getAttribute(DesignTag.COMPONENT_COMMON_FONTSIZE));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_FONTTYPE)) {
						component.setFontType(element.getAttribute(DesignTag.COMPONENT_COMMON_FONTTYPE));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_ALIGN)) {
						component.setAlign(element.getAttribute(DesignTag.COMPONENT_COMMON_ALIGN));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_LABEL)) {
						component.setLabel(element.getAttribute(DesignTag.COMPONENT_COMMON_LABEL));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_TABLEID)) {
						component.setTableId(element.getAttribute(DesignTag.COMPONENT_COMMON_TABLEID));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_FIELDID)) {
						component.setFieldId(element.getAttribute(DesignTag.COMPONENT_COMMON_FIELDID));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_BACKGROUND)) {
						component.setBackGround(Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_BACKGROUND)));
						component.setExtension(resourcesFileMap.get(element.getAttribute(DesignTag.COMPONENT_COMMON_BACKGROUND)));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_TEXTFIELD_MULTI)) {
						component.setMultiLine(element.getAttribute(DesignTag.COMPONENT_TEXTFIELD_MULTI));
						component.setEditable(element.hasAttribute(DesignTag.COMPONENT_TEXTFIELD_EDIT));
					}
					
					if (element.getNodeName().equals(DesignTag.COMPONENT_CHECKBOX)) {
						if (element.hasAttribute(DesignTag.COMPONENT_CHECKBOX_CHECKED)) {
							component.setChecked(element.getAttribute(DesignTag.COMPONENT_CHECKBOX_CHECKED));
						}
					}
					else if (element.getNodeName().equals(DesignTag.COMPONENT_COMBOBOX)) {
						ArrayList<String> itemList = new ArrayList<String>();
						NodeList nodeItemList = element.getChildNodes();
						if (nodeItemList.getLength() > 0) {
							int itemLen = nodeItemList.getLength();
							for (int k=0; k<itemLen; k++) {
								Element item = (Element) nodeItemList.item(k);
								if ((item.getNodeName().equals(DesignTag.COMPONENT_COMBOBOX_ITEM)) &&
										(item.hasAttribute(DesignTag.COMPONENT_COMMON_VALUE))) {
									String value = item.getAttribute(DesignTag.COMPONENT_COMMON_VALUE);
									itemList.add(value);
								}
							}
							component.setItemList(itemList);
						}
						else if ((element.hasAttribute(DesignTag.COMPONENT_COMBOBOX_LABELTABLE)) &&
								(element.hasAttribute(DesignTag.COMPONENT_COMBOBOX_LABELFIELD)) &&
								(element.hasAttribute(DesignTag.COMPONENT_COMBOBOX_VALUETABLE)) &&
								(element.hasAttribute(DesignTag.COMPONENT_COMBOBOX_VALUEFIELD))) {
							ArrayList<String> labelList = new ArrayList<String>();
							labelList.add(element.getAttribute(DesignTag.COMPONENT_COMBOBOX_LABELTABLE));
							labelList.add(element.getAttribute(DesignTag.COMPONENT_COMBOBOX_LABELFIELD));
							
							ArrayList<String> valueList = new ArrayList<String>();
							valueList.add(element.getAttribute(DesignTag.COMPONENT_COMBOBOX_VALUETABLE));
							valueList.add(element.getAttribute(DesignTag.COMPONENT_COMBOBOX_VALUEFIELD));
							
							component.setLabelList(labelList);
							component.setValueList(valueList);
						}
					}
					else if (element.getNodeName().equals(DesignTag.COMPONENT_DATAVIEW)) {
						ArrayList<ArrayList<String>> columnInfos = new ArrayList<ArrayList<String>>();
						HashMap<Integer, String> onCalculateMap = new HashMap<Integer, String>();
						NodeList nodeItemList = element.getChildNodes();
						if (nodeItemList.getLength() > 0) {
							int nbColumn = nodeItemList.getLength();
							for (int k=0; k<nbColumn; k++) {
								Element column = (Element) element.getChildNodes().item(k);
								if (column.getNodeName().equals(DesignTag.COMPONENT_DATAVIEW_COLUMN)) {
									ArrayList<String> acolumn = new ArrayList<String>();
									acolumn.add(column.getAttribute(DesignTag.COMPONENT_COMMON_TABLEID));
									acolumn.add(column.getAttribute(DesignTag.COMPONENT_COMMON_FIELDID));
									acolumn.add(column.getAttribute(DesignTag.COMPONENT_DATAVIEW_COLUMN_HEADER));
									acolumn.add(column.getAttribute(DesignTag.COMPONENT_COMMON_PWIDTH));
									acolumn.add(column.getAttribute(DesignTag.COMPONENT_COMMON_LWIDTH));
									if (column.getAttribute(DesignTag.COMPONENT_DATAVIEW_COLUMN_CALC).equals("true")) {
										if (column.hasAttribute(DesignTag.EVENT_ONCALCULATE)) {
											onCalculateMap.put(k, column.getAttribute(DesignTag.EVENT_ONCALCULATE));
										}
										else {
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
					else if ((element.getNodeName().equals(DesignTag.COMPONENT_DATEFIELD)) ||
							(element.getNodeName().equals(DesignTag.COMPONENT_TIMEFIELD))) {
						if (element.hasAttribute(DesignTag.COMPONENT_COMMON_VALUE)) {
							component.setDateTimeValue(element.getAttribute(DesignTag.COMPONENT_COMMON_VALUE));
						}
					}
					else if (element.getNodeName().equals(DesignTag.COMPONENT_GAUGE)) {
						if (element.hasAttribute(DesignTag.COMPONENT_GAUGE_INIT)) {
							component.setInitValue(Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_GAUGE_INIT)));
						}
						if (element.hasAttribute(DesignTag.COMPONENT_GAUGE_MIN)) {
							if (!element.getAttribute(DesignTag.COMPONENT_GAUGE_MIN).equals("true") && !element.getAttribute(DesignTag.COMPONENT_GAUGE_MIN).equals("false")) {
								component.setMinValue(Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_GAUGE_MIN)));
							}
						}
						if (element.hasAttribute(DesignTag.COMPONENT_GAUGE_MAX)) {
							if (!element.getAttribute(DesignTag.COMPONENT_GAUGE_MAX).equals("true") && !element.getAttribute(DesignTag.COMPONENT_GAUGE_MAX).equals("false")) {
								component.setMaxValue(Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_GAUGE_MAX)));
							}
						}
					}
					
					if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						currentOrientation = Configuration.ORIENTATION_LANDSCAPE;
					}
					else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			        	currentOrientation = Configuration.ORIENTATION_PORTRAIT;
			        }
					
					component.setView();
					componentsMap.put(element.getAttribute(DesignTag.COMPONENT_COMMON_ID), component);
					
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_ENABLE)) {
						component.getView().setEnabled(false);
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_VISIBLE)) {
						component.getView().setVisibility(View.INVISIBLE);
					}
					
					if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
						component.getView().setLayoutParams(new AbsoluteLayout.LayoutParams(
								Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_LWIDTH)),
								Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_LHEIGHT)),
								Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_LCOORDX)),
								Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_LCOORDY))));
			        }
			        else {
			        	component.getView().setLayoutParams(new AbsoluteLayout.LayoutParams(
								Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_PWIDTH)),
								Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_PHEIGHT)),
								Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_PCOORDX)),
								Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_PCOORDY))));
			        }
					form.addView(component.getView());
					
					//Add onclick event
					if (element.hasAttribute(DesignTag.EVENT_ONCLICK)) {
						component.setOnclickFunction(element.getAttribute(DesignTag.EVENT_ONCLICK), component.getView());
					}
					
					//Add onchange event
					if (element.hasAttribute(DesignTag.EVENT_ONCHANGE)) {
						component.setOnchangeFunction(element.getAttribute(DesignTag.EVENT_ONCHANGE), component.getView());
					}
				}
			}
			//Add onload event in a hashmap for calling this function by changing form 
			onLoadFuncMap.put(formId, formElt.getAttribute(DesignTag.EVENT_ONLOAD));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		currentMenu = menu;
		boolean result = super.onCreateOptionsMenu(currentMenu);
		setCurrentFormId(currentFormId);
		return result;
	}

	public void quit() {
		this.finish();
	}

	public static int getOrientation() {
		return currentOrientation;
	}

	public static HashMap<String, Component> getComponents() {
		return componentsMap;
	}

	public static HashMap<String, Form> getLayoutsMap() {
		return layoutsMap;
	}

	public static DatabaseAdapter getDataBase() {
		return database;
	}

	public static DmaHttpClient getCurrentClient() {
		return client;
	}

	public static ApplicationView getCurrentView() {
		return applicationView;
	}

	public static HashMap<String, String> getOnLoadFuncMap() {
		return onLoadFuncMap;
	}

	public static String getCurrentFormId() {
		return currentFormId;
	}
	
	public static void setCurrentFormId(String id) {
		currentFormId = id;
		ArrayList<String> menuItemNameList = layoutsMap.get(currentFormId).getMenuItemNameList();
		if (currentMenu != null) {
			currentMenu.clear();
			int itemsSize = menuItemNameList.size();
			if (itemsSize > 0) {
				for (int i=0; i<itemsSize; i++) {
					currentMenu.add(Menu.NONE, i, Menu.NONE, menuItemNameList.get(i)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							ArrayList<String> menuItemOnClickList = layoutsMap.get(currentFormId).getMenuItemOnClickList();
							if (!menuItemOnClickList.get(item.getItemId()).equals("")) {
								Function.createFunction(menuItemOnClickList.get(item.getItemId()));
							}
							return false;
						}
					});
				}
			}
		}
	}
	
	public static void errorDialog(String message) {
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
		//Check if there is doodle image or picturebox
		getLayoutsMap().get(getCurrentFormId()).setPreview();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		database.closeDatabase();
	}
}
