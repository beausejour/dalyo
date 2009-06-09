package com.penbase.dma.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.graphics.Color;

import com.penbase.dma.Constant.DesignTag;
import com.penbase.dma.Dalyo.Component.Component;
import com.penbase.dma.Dalyo.Component.Form;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Generic object for Dalyo application, it builds application's UI, database etc.
 */
public class ApplicationView extends Activity {
	private static Menu sCurrentMenu;
	private static ApplicationView sApplicationView;
	private static DmaHttpClient sClient;
	private static HashMap<String, Form> sLayoutsMap;
	private static HashMap<String, String> sOnLoadFuncMap;	
	private static Document sDesignDoc = null;
	private Component mComponent;
	private static Document sBehaviorDocument = null;
	private static Document sDbDoc = null;
	private static HashMap<String, String> sResourcesFileMap;
	private static HashMap<String, Component> sComponentsMap;
	private static DatabaseAdapter sDatabase;
	private ProgressDialog mLoadingDialog;
	private static String sClientLogin;
	private static String sCurrentFormId;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				default:
					createView();
					display();
					break;
			}
		}
	};
	private static int sCurrentOrientation;

	@Override
	public void onCreate(Bundle icicle) {	
		super.onCreate(icicle);
		ApplicationView.sApplicationView = this;
		sResourcesFileMap = sClient.getResourceMap("ext");
		sComponentsMap = new HashMap<String, Component>();
		mLoadingDialog = ProgressDialog.show(this, "Please wait...", "Building application ...", true, false);
		setTitle(ApplicationListView.getApplicationName());
		new Thread(new Runnable() {
			@Override
			public void run() {
				sDatabase = new DatabaseAdapter(ApplicationView.this, sDbDoc, sClientLogin+"_"+ApplicationListView.getApplicationName());
				new Function(ApplicationView.this, sBehaviorDocument);
				mHandler.sendEmptyMessage(0);	
			}
		}).start();
	}

	/**
	 * Displays first form of application
	 */
	private void display() {
		generalSetup();
		NodeList generalInfo = sDesignDoc.getElementsByTagName(DesignTag.DESIGN_S_G);
		final String startFormId = ((Element) generalInfo.item(0)).getAttribute(DesignTag.DESIGN_S_G_FID);
		if (sOnLoadFuncMap.containsKey(startFormId)) {
			sLayoutsMap.get(startFormId).onLoad(sOnLoadFuncMap.get(startFormId));
		}
		sCurrentFormId = startFormId;
		setTitle(sLayoutsMap.get(startFormId).getTitle());
		mLoadingDialog.dismiss();
		setContentView(sLayoutsMap.get(startFormId));
	}

	/**
	 * Prepares necessary xml data
	 * @param position Position of application
	 * @param login Login name
	 * @param pwd Password
	 */
	public static void prepareData(int position, String login, String pwd) {
		sClient = new DmaHttpClient();
		sClient.checkXmlFiles();
		sClientLogin = login;
		String urlRequest = sClient.generateRegularUrlRequest(ApplicationListView.getApplicationsInfo().get("AppId"),
				ApplicationListView.getApplicationsInfo().get("AppVer"),
				ApplicationListView.getApplicationsInfo().get("AppBuild"),
				ApplicationListView.getApplicationsInfo().get("SubId"), login, pwd);
		sBehaviorDocument = sClient.getBehavior(urlRequest);
		sDesignDoc = sClient.getDesign(urlRequest);
		sClient.getResources(urlRequest);
		sDbDoc = sClient.getDB(urlRequest);
	}

	/**
	 * Sets up on start function
	 */
	private void generalSetup() {
		Node system = sDesignDoc.getElementsByTagName(DesignTag.DESIGN_S).item(0);
		int childrenLen = system.getChildNodes().getLength();
		for (int i=0; i<childrenLen; i++) {
			Element child = (Element) system.getChildNodes().item(i);
			if (child.getNodeName().equals(DesignTag.DESIGN_S_G)) {
				if (child.hasAttribute(DesignTag.DESIGN_S_G_OS)) {
					String name = child.getAttribute(DesignTag.DESIGN_S_G_OS);
					Function.createFunction(name);
				}
			}
		}
	}
	
	/**
	 * Parses application's interface and construct all its forms
	 */
	private void createView() {
		sLayoutsMap = new HashMap<String, Form>();
		sOnLoadFuncMap = new HashMap<String, String>();
		NodeList formsList = sDesignDoc.getElementsByTagName(DesignTag.DESIGN_F);
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
			} else {
				//Default background color is white
				form.setBackgroundColor(Color.WHITE);
			}
			
			//Check form's title
			if (formElt.hasAttribute(DesignTag.DESIGN_F_TITLE)) {
				String title = formElt.getAttribute(DesignTag.DESIGN_F_TITLE);
				form.setTitle(title);
			}
			
			sLayoutsMap.put(formId, form);
			
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
												} else {
													menuItemOnClickList.add("");
												}
											}
										}
									}
								} else {
									menuItemNameList.add(menu.getAttribute(DesignTag.COMPONENT_COMMON_NAME));
									if (menu.hasAttribute(DesignTag.EVENT_ONCLICK)) {
										menuItemOnClickList.add(menu.getAttribute(DesignTag.EVENT_ONCLICK));
									} else {
										menuItemOnClickList.add("");
									}
								}
							}
						}
					}
					form.setMenuItemNameList(menuItemNameList);
					form.setMenuItemOnClickList(menuItemOnClickList);
				} else if (element.getNodeName().equals(DesignTag.COMPONENT_NAVIBAR)) {
					//Navigation bar
				} else {
					mComponent = new Component(this, element.getNodeName());
					
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_ID)) {
						mComponent.setId(element.getAttribute(DesignTag.COMPONENT_COMMON_ID));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_FONTSIZE)) {
						mComponent.setFontSize(element.getAttribute(DesignTag.COMPONENT_COMMON_FONTSIZE));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_FONTTYPE)) {
						mComponent.setFontType(element.getAttribute(DesignTag.COMPONENT_COMMON_FONTTYPE));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_ALIGN)) {
						mComponent.setAlign(element.getAttribute(DesignTag.COMPONENT_COMMON_ALIGN));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_LABEL)) {
						mComponent.setLabel(element.getAttribute(DesignTag.COMPONENT_COMMON_LABEL));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_TABLEID)) {
						mComponent.setTableId(element.getAttribute(DesignTag.COMPONENT_COMMON_TABLEID));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_FIELDID)) {
						mComponent.setFieldId(element.getAttribute(DesignTag.COMPONENT_COMMON_FIELDID));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_BACKGROUND)) {
						mComponent.setBackGround(Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_BACKGROUND)));
						mComponent.setExtension(sResourcesFileMap.get(element.getAttribute(DesignTag.COMPONENT_COMMON_BACKGROUND)));
					}
					if (element.hasAttribute(DesignTag.COMPONENT_TEXTFIELD_MULTI)) {
						mComponent.setMultiLine(element.getAttribute(DesignTag.COMPONENT_TEXTFIELD_MULTI));
						mComponent.setEditable(element.hasAttribute(DesignTag.COMPONENT_TEXTFIELD_EDIT));
					}
					
					if (element.getNodeName().equals(DesignTag.COMPONENT_CHECKBOX)) {
						if (element.hasAttribute(DesignTag.COMPONENT_CHECKBOX_CHECKED)) {
							mComponent.setChecked(element.getAttribute(DesignTag.COMPONENT_CHECKBOX_CHECKED));
						}
					} else if (element.getNodeName().equals(DesignTag.COMPONENT_COMBOBOX)) {
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
							mComponent.setItemList(itemList);
						} else if ((element.hasAttribute(DesignTag.COMPONENT_COMBOBOX_LABELTABLE)) &&
								(element.hasAttribute(DesignTag.COMPONENT_COMBOBOX_LABELFIELD)) &&
								(element.hasAttribute(DesignTag.COMPONENT_COMBOBOX_VALUETABLE)) &&
								(element.hasAttribute(DesignTag.COMPONENT_COMBOBOX_VALUEFIELD))) {
							ArrayList<String> labelList = new ArrayList<String>();
							labelList.add(element.getAttribute(DesignTag.COMPONENT_COMBOBOX_LABELTABLE));
							labelList.add(element.getAttribute(DesignTag.COMPONENT_COMBOBOX_LABELFIELD));
							
							ArrayList<String> valueList = new ArrayList<String>();
							valueList.add(element.getAttribute(DesignTag.COMPONENT_COMBOBOX_VALUETABLE));
							valueList.add(element.getAttribute(DesignTag.COMPONENT_COMBOBOX_VALUEFIELD));
							
							mComponent.setLabelList(labelList);
							mComponent.setValueList(valueList);
						}
					} else if (element.getNodeName().equals(DesignTag.COMPONENT_DATAVIEW)) {
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
										} else {
											onCalculateMap.put(k, "");
										}
									}
									columnInfos.add(acolumn);
								}
							}
						}
						mComponent.setDataviewColumns(columnInfos);
						mComponent.setDataviewOncalculate(onCalculateMap);
					} else if ((element.getNodeName().equals(DesignTag.COMPONENT_DATEFIELD)) ||
							(element.getNodeName().equals(DesignTag.COMPONENT_TIMEFIELD))) {
						if (element.hasAttribute(DesignTag.COMPONENT_COMMON_VALUE)) {
							mComponent.setDateTimeValue(element.getAttribute(DesignTag.COMPONENT_COMMON_VALUE));
						}
					} else if ((element.getNodeName().equals(DesignTag.COMPONENT_GAUGE)) ||
							(element.getNodeName().equals(DesignTag.COMPONENT_NUMBERBOX))) {
						if (element.hasAttribute(DesignTag.COMPONENT_INIT)) {
							mComponent.setInitValue(Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_INIT)));
						}
						if (element.hasAttribute(DesignTag.COMPONENT_MIN)) {
							mComponent.setMinValue(Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_MIN)));
						}
						if (element.hasAttribute(DesignTag.COMPONENT_MAX)) {
							mComponent.setMaxValue(Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_MAX)));
						}
					}
					
					if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						sCurrentOrientation = Configuration.ORIENTATION_LANDSCAPE;
					} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
						sCurrentOrientation = Configuration.ORIENTATION_PORTRAIT;
			        }
					
					mComponent.setView();
					sComponentsMap.put(element.getAttribute(DesignTag.COMPONENT_COMMON_ID), mComponent);
					
					View componentView = mComponent.getView();
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_ENABLE)) {
						componentView.setEnabled(false);
					}
					if (element.hasAttribute(DesignTag.COMPONENT_COMMON_VISIBLE)) {
						componentView.setVisibility(View.INVISIBLE);
					}
					
					if (sCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
								Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_LWIDTH)),
								Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_LHEIGHT)));
						layoutParams.leftMargin = Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_LCOORDX));
						layoutParams.topMargin = Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_LCOORDY));
						componentView.setLayoutParams(layoutParams);
			        } else {
			        	RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
								Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_PWIDTH)),
								Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_PHEIGHT)));
						layoutParams.leftMargin = Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_PCOORDX));
						layoutParams.topMargin = Integer.valueOf(element.getAttribute(DesignTag.COMPONENT_COMMON_PCOORDY));
						componentView.setLayoutParams(layoutParams);
			        }
					form.addSubView(componentView);
					
					//Add onclick event
					if (element.hasAttribute(DesignTag.EVENT_ONCLICK)) {
						mComponent.setOnclickFunction(element.getAttribute(DesignTag.EVENT_ONCLICK), componentView);
					}
					
					//Add onchange event
					if (element.hasAttribute(DesignTag.EVENT_ONCHANGE)) {
						mComponent.setOnchangeFunction(element.getAttribute(DesignTag.EVENT_ONCHANGE), componentView);
					}
				}
			}
			//Add onload event in a hashmap for calling this function by changing form 
			sOnLoadFuncMap.put(formId, formElt.getAttribute(DesignTag.EVENT_ONLOAD));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		sCurrentMenu = menu;
		boolean result = super.onCreateOptionsMenu(sCurrentMenu);
		setCurrentFormId(sCurrentFormId);
		return result;
	}

	public void quit() {
		this.finish();
	}

	public static int getOrientation() {
		return sCurrentOrientation;
	}

	public static HashMap<String, Component> getComponents() {
		return sComponentsMap;
	}

	public static HashMap<String, Form> getLayoutsMap() {
		return sLayoutsMap;
	}

	public static DatabaseAdapter getDataBase() {
		return sDatabase;
	}

	public static DmaHttpClient getCurrentClient() {
		return sClient;
	}

	public static ApplicationView getCurrentView() {
		return sApplicationView;
	}

	public static HashMap<String, String> getOnLoadFuncMap() {
		return sOnLoadFuncMap;
	}

	public static String getCurrentFormId() {
		return sCurrentFormId;
	}
	
	public static void setCurrentFormId(String id) {
		sCurrentFormId = id;
		ArrayList<String> menuItemNameList = sLayoutsMap.get(sCurrentFormId).getMenuItemNameList();
		if (sCurrentMenu != null) {
			sCurrentMenu.clear();
			int itemsSize = menuItemNameList.size();
			if (itemsSize > 0) {
				for (int i=0; i<itemsSize; i++) {
					sCurrentMenu.add(Menu.NONE, i, Menu.NONE, menuItemNameList.get(i)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							ArrayList<String> menuItemOnClickList = sLayoutsMap.get(sCurrentFormId).getMenuItemOnClickList();
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
		AlertDialog dialog = new AlertDialog.Builder(sApplicationView).create();
		dialog.setMessage(message);
		dialog.setTitle("Error");
		dialog.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sApplicationView.finish();
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
		sDatabase.closeDatabase();
	}
}
