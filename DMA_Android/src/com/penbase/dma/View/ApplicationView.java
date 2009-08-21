package com.penbase.dma.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.penbase.dma.Dma;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DesignTag;
import com.penbase.dma.Dalyo.Component.Component;
import com.penbase.dma.Dalyo.Component.Form;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;

import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Generic object for Dalyo application, it builds application's UI, database etc.
 */
public class ApplicationView extends Activity {
	private static Menu sCurrentMenu;
	private static ApplicationView sApplicationView;
	private static DmaHttpClient sClient;
	private static HashMap<String, Form> sLayoutsMap;
	private static HashMap<String, String> sOnLoadFuncMap;
	private static Document sBehaviorDocument = null;
	private static Document sDbDoc = null;
	private static HashMap<String, Component> sComponentsMap;
	private static DatabaseAdapter sDatabase;
	private ProgressDialog mLoadingDialog;
	private static String sCurrentFormId;
	private static String sApplicationVersion;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					createView();
					display();
					break;
			}
		}
	};
	private static int sCurrentOrientation;
	private static Reader sDesignReader = null;
	private String mOnLoadFunctionName = null;
	private String mStartFormId = null;
	private static String sApplicationId;
	private static String sUsername;

	@Override
	public void onCreate(Bundle icicle) {	
		super.onCreate(icicle);
		ApplicationView.sApplicationView = this;
		sComponentsMap = new HashMap<String, Component>();
		if (Dma.sLocale.contains(Constant.FRENCH)) {
			mLoadingDialog = ProgressDialog.show(this, "Veuillez patienter...", "Chargement en cours ...", true, false);
		} else {
			mLoadingDialog = ProgressDialog.show(this, "Please wait...", "Building application ...", true, false);
		}
		Intent intent = getIntent();
		sApplicationId = intent.getStringExtra("ID");
		sUsername = intent.getStringExtra("USERNAME");
		setTitle(intent.getStringExtra("TITLE"));
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer databaseName = new StringBuffer(sUsername);
				databaseName.append("_");
				databaseName.append(sApplicationId);
				sDatabase = new DatabaseAdapter(ApplicationView.this, sDbDoc, databaseName.toString());
				new Function(ApplicationView.this, sBehaviorDocument);
				mHandler.sendEmptyMessage(0);	
			}
		}).start();
	}

	/**
	 * Prepares necessary xml data
	 * @param login Login name
	 * @param pwd Password
	 * @throws FileNotFoundException 
	 */
	public static void prepareData(String id, String login, String pwd) throws FileNotFoundException {
		sClient = new DmaHttpClient(login, id);
		sClient.checkXmlFiles();
		HashMap<String, String> applciationsInfo = ApplicationListView.getApplicationsInfo();
		String urlRequest = sClient.generateRegularUrlRequest(applciationsInfo.get("AppId"),
				applciationsInfo.get("AppVer"),
				applciationsInfo.get("AppBuild"),
				applciationsInfo.get("SubId"), login, pwd);
		sClient.getResource(urlRequest);
		sDesignReader = sClient.getDesignReader(urlRequest);
		sBehaviorDocument = sClient.getBehavior(urlRequest);
		sDbDoc = sClient.getDB(urlRequest);
	}
	
	/**
	 * Displays first form of application
	 */
	private void display() {
		if (mOnLoadFunctionName != null) {
			Function.createFunction(mOnLoadFunctionName);	
		}
		if (sOnLoadFuncMap.containsKey(mStartFormId)) {
			sLayoutsMap.get(mStartFormId).onLoad(sOnLoadFuncMap.get(mStartFormId));
		}
		sCurrentFormId = mStartFormId;
		setTitle(sLayoutsMap.get(mStartFormId).getTitle());
		mLoadingDialog.dismiss();
		setContentView(sLayoutsMap.get(mStartFormId));
	}
	
	/**
	 * Parses application's interface and construct all its forms
	 */
	private void createView() {
		sLayoutsMap = new HashMap<String, Form>();
		sOnLoadFuncMap = new HashMap<String, String>();
		SAXParserFactory spFactory = SAXParserFactory.newInstance();
    	SAXParser saxParser;
		try {
			saxParser = spFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
	    	EventsHandler eventsHandler = new EventsHandler();
	    	xmlReader.setContentHandler(eventsHandler);
	    	xmlReader.parse(new InputSource(sDesignReader));
		}
    	 catch (ParserConfigurationException e) {
 			e.printStackTrace();
 		} catch (SAXException e) {
 			e.printStackTrace();
 		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
							String menuId = menuItemOnClickList.get(item.getItemId());
							if (!menuId.equals("")) {
								Function.createFunction(menuId);
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
				//sApplicationView.finish();
			}
			
		});
		dialog.show();
	}
	
	public static String getApplicationVersion() {
		return sApplicationVersion;
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
	
	public Handler getHandler() {
		return mHandler;
	}
	
	public static String getApplicationId() {
		return sApplicationId;
	}
	
	public static String getUsername() {
		return sUsername;
	}
	
	private class EventsHandler extends DefaultHandler {
		boolean isInSystemNode = false;
		boolean isInFormNode = false;
		boolean isInMenuBarNode = false;
		boolean isInMenuNode = false;
		boolean isInComboBox = false;
		boolean isInComboBoxItem = false;
		boolean isInDataview = false;
		boolean isInDataviewColumn = false;
		Form form = null;
		String formId = null;
		String formOnload = null;
		
		Component component = null;
		
		ArrayList<String> menuItemNameList = null;
		ArrayList<String> menuItemOnClickList = null;
		
		ArrayList<String> comboboxItemList = null;
		
		ArrayList<ArrayList<String>> columnInfos = null;
		HashMap<Integer, String> onCalculateMap = null;
		int column = 0;
		
		@Override
		public void startDocument() throws SAXException {
        }
        
        @Override
        public void endDocument() throws SAXException {
        }
        
        public void startElement(String namespaceURI, String tagName, String qName, Attributes atts) throws SAXException {
        	if (tagName.equals(DesignTag.DESIGN_S)) {
        		isInSystemNode = true;
        	} else if (tagName.equals(DesignTag.DESIGN_F)) {
				isInFormNode = true;
    			form = new Form(ApplicationView.this);
    			formId = atts.getValue(DesignTag.DESIGN_F_ID);
    			formOnload = atts.getValue(DesignTag.EVENT_ONLOAD);
    			String tableId = atts.getValue(DesignTag.COMPONENT_COMMON_TABLEID);
    			if (!tableId.equals("")) {
    				form.setTableId(tableId);
    			}
    			
    			//Check background of a form
    			if (atts.getValue(DesignTag.DESIGN_F_BC) != null) {
    				String backgourndColor = "#"+atts.getValue(DesignTag.DESIGN_F_BC);
    				form.setBackgroundColor(Color.parseColor(backgourndColor));
    			} else {
    				//Default background color is white
    				form.setBackgroundColor(Color.WHITE);
    			}
    			
    			//Check form's title
    			if (atts.getValue(DesignTag.DESIGN_F_TITLE) != null) {
    				String title = atts.getValue(DesignTag.DESIGN_F_TITLE);
    				form.setTitle(title);
    			}
    			sLayoutsMap.put(formId, form);
    		} else if (isInSystemNode) {
    			if (tagName.equals(DesignTag.DESIGN_S_G)) {
    				if (atts.getValue(DesignTag.DESIGN_S_G_OS) != null) {
    					mOnLoadFunctionName = atts.getValue(DesignTag.DESIGN_S_G_OS);
    				}
    				mStartFormId = atts.getValue(DesignTag.DESIGN_S_G_FID);
    				sApplicationVersion = atts.getValue(DesignTag.DESIGN_S_G_PV);
    			}
    		} else if (isInFormNode) {
    			if (isInMenuBarNode) {
    				if (isInMenuNode) {
    					if (tagName.equals(DesignTag.COMPONENT_MENUITEM)) {
    						menuItemNameList.add(atts.getValue(DesignTag.COMPONENT_COMMON_NAME));
    						if (atts.getValue(DesignTag.EVENT_ONCLICK) != null) {
    							menuItemOnClickList.add(atts.getValue(DesignTag.EVENT_ONCLICK));
    						} else {
    							menuItemOnClickList.add("");
    						}
    					}
    				}
    				if (tagName.equals(DesignTag.COMPONENT_MENU)) {
    					if (atts.getValue(DesignTag.EVENT_ONCLICK) != null) {
    						menuItemNameList.add(atts.getValue(DesignTag.COMPONENT_COMMON_NAME));
    						if (atts.getValue(DesignTag.EVENT_ONCLICK) != null) {
    							menuItemOnClickList.add(atts.getValue(DesignTag.EVENT_ONCLICK));
    						} else {
    							menuItemOnClickList.add("");
    						}
    					} else {
    						isInMenuNode = true;
    					}
    				}
    			} else if (isInComboBox) {
    				if (tagName.equals(DesignTag.COMPONENT_COMBOBOX_ITEM) && 
    						atts.getValue(DesignTag.COMPONENT_COMMON_VALUE) != null) {
    					isInComboBoxItem = true;
						comboboxItemList.add(atts.getValue(DesignTag.COMPONENT_COMMON_VALUE));
    				}
    			} else if (isInDataview) {
    				if (tagName.equals(DesignTag.COMPONENT_DATAVIEW_COLUMN)) {
    					isInDataviewColumn = true;
    					ArrayList<String> acolumn = new ArrayList<String>();
    					
						acolumn.add(atts.getValue(DesignTag.COMPONENT_COMMON_TABLEID));
						acolumn.add(atts.getValue(DesignTag.COMPONENT_COMMON_FIELDID));
						acolumn.add(atts.getValue(DesignTag.COMPONENT_DATAVIEW_COLUMN_HEADER));
						acolumn.add(atts.getValue(DesignTag.COMPONENT_COMMON_PWIDTH));
						acolumn.add(atts.getValue(DesignTag.COMPONENT_COMMON_LWIDTH));
						
						if (atts.getValue(DesignTag.COMPONENT_DATAVIEW_COLUMN_CALC).equals("true")) {
							if (atts.getValue(DesignTag.EVENT_ONCALCULATE) != null) {
								onCalculateMap.put(column, atts.getValue(DesignTag.EVENT_ONCALCULATE));
							} else {
								onCalculateMap.put(column, "");
							}
						}
						columnInfos.add(acolumn);
						column += 1;
    				}
    			}
    			
    			//Menu bar
    			if (tagName.equals(DesignTag.COMPONENT_MENUBAR)) {
    				isInMenuBarNode = true;
    				//Menu item name list and on-click event list
					menuItemNameList = new ArrayList<String>(); 
					menuItemOnClickList = new ArrayList<String>();
    			} else if (tagName.equals(DesignTag.COMPONENT_NAVIBAR)) {
    				//Navigation bar
    			} else if (!tagName.equals(DesignTag.COMPONENT_MENU) &&
    					!tagName.equals(DesignTag.COMPONENT_MENUITEM) &&
    					!isInDataviewColumn && !isInComboBoxItem){
    				//Graphic component
    				component = new Component(ApplicationView.this, tagName);
    				if (atts.getValue(DesignTag.COMPONENT_COMMON_ID) != null) {
    					component.setId(atts.getValue(DesignTag.COMPONENT_COMMON_ID));
    				}
    				if (atts.getValue(DesignTag.COMPONENT_COMMON_FONTSIZE) != null) {
    					component.setFontSize(atts.getValue(DesignTag.COMPONENT_COMMON_FONTSIZE));
    				}
    				if (atts.getValue(DesignTag.COMPONENT_COMMON_FONTTYPE) != null) {
    					component.setFontType(atts.getValue(DesignTag.COMPONENT_COMMON_FONTTYPE));
    				}
    				if (atts.getValue(DesignTag.COMPONENT_COMMON_ALIGN) != null) {
    					component.setAlign(atts.getValue(DesignTag.COMPONENT_COMMON_ALIGN));
    				}
    				if (atts.getValue(DesignTag.COMPONENT_COMMON_LABEL) != null) {
    					component.setLabel(atts.getValue(DesignTag.COMPONENT_COMMON_LABEL));
    				}
    				if (atts.getValue(DesignTag.COMPONENT_COMMON_TABLEID) != null) {
    					component.setTableId(atts.getValue(DesignTag.COMPONENT_COMMON_TABLEID));
    				}
    				if (atts.getValue(DesignTag.COMPONENT_COMMON_FIELDID) != null) {
    					component.setFieldId(atts.getValue(DesignTag.COMPONENT_COMMON_FIELDID));
    				}
    				if (atts.getValue(DesignTag.COMPONENT_COMMON_BACKGROUND) != null) {
    					component.setBackGround(atts.getValue(DesignTag.COMPONENT_COMMON_BACKGROUND));	
    				}
    				
    				if (tagName.equals(DesignTag.COMPONENT_CHECKBOX)) {
    					if (atts.getValue(DesignTag.COMPONENT_CHECKBOX_CHECKED) != null) {
    						component.setChecked(atts.getValue(DesignTag.COMPONENT_CHECKBOX_CHECKED));
    					}
    				} else if (tagName.equals(DesignTag.COMPONENT_COMBOBOX)) {
    					if ((atts.getValue(DesignTag.COMPONENT_COMBOBOX_LABELTABLE) != null) &&
								(atts.getValue(DesignTag.COMPONENT_COMBOBOX_LABELFIELD) != null) &&
								(atts.getValue(DesignTag.COMPONENT_COMBOBOX_VALUETABLE) != null) &&
								(atts.getValue(DesignTag.COMPONENT_COMBOBOX_VALUEFIELD) != null)) {
							ArrayList<String> labelList = new ArrayList<String>();
							labelList.add(atts.getValue(DesignTag.COMPONENT_COMBOBOX_LABELTABLE));
							labelList.add(atts.getValue(DesignTag.COMPONENT_COMBOBOX_LABELFIELD));
							
							ArrayList<String> valueList = new ArrayList<String>();
							valueList.add(atts.getValue(DesignTag.COMPONENT_COMBOBOX_VALUETABLE));
							valueList.add(atts.getValue(DesignTag.COMPONENT_COMBOBOX_VALUEFIELD));
							
							component.setLabelList(labelList);
							component.setValueList(valueList);
						} else {
    						isInComboBox = true;
        					comboboxItemList = new ArrayList<String>();	
    					}
    				} else if (tagName.equals(DesignTag.COMPONENT_DATAVIEW)) {
    					isInDataview = true;
    					columnInfos = new ArrayList<ArrayList<String>>();
						onCalculateMap = new HashMap<Integer, String>();
    				} else if (tagName.equals(DesignTag.COMPONENT_DATEFIELD) ||
    						tagName.equals(DesignTag.COMPONENT_TIMEFIELD)) {
    					if (atts.getValue(DesignTag.COMPONENT_COMMON_VALUE) != null) {
    						component.setDateTimeValue(atts.getValue(DesignTag.COMPONENT_COMMON_VALUE));
    					}
    				} else if ((tagName.equals(DesignTag.COMPONENT_GAUGE)) ||
							(tagName.equals(DesignTag.COMPONENT_NUMBERBOX))) {
    					if (atts.getValue(DesignTag.COMPONENT_INIT) != null) {
    						component.setInitValue(Integer.valueOf(atts.getValue(DesignTag.COMPONENT_INIT)));
    					}
						if (atts.getValue(DesignTag.COMPONENT_MIN) != null) {
							component.setMinValue(Integer.valueOf(atts.getValue(DesignTag.COMPONENT_MIN)));
						}
						if (atts.getValue(DesignTag.COMPONENT_MAX) != null) {
							component.setMaxValue(Integer.valueOf(atts.getValue(DesignTag.COMPONENT_MAX)));
						}
    				} else if (tagName.equals(DesignTag.COMPONENT_TEXTFIELD)) {
    					if (atts.getValue(DesignTag.COMPONENT_TEXTFIELD_MULTI) != null) {
							component.setMultiLine(atts.getValue(DesignTag.COMPONENT_TEXTFIELD_MULTI));
						}
    					if (atts.getValue(DesignTag.COMPONENT_TEXTFIELD_EDIT) != null) {
    						component.setEditable(true);
    					} else {
    						component.setEditable(false);
    					}
						component.setTextFilter(atts.getValue(DesignTag.COMPONENT_TEXTFIELD_TEXTFILTER));
    				}
    				if (ApplicationView.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						sCurrentOrientation = Configuration.ORIENTATION_LANDSCAPE;
					} else if (ApplicationView.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
						sCurrentOrientation = Configuration.ORIENTATION_PORTRAIT;
			        }
					
					component.setView();
					sComponentsMap.put(atts.getValue(DesignTag.COMPONENT_COMMON_ID), component);
					
					View componentView = component.getView();
					if (atts.getValue(DesignTag.COMPONENT_COMMON_ENABLE) != null) {
						componentView.setEnabled(false);
					}
					if (atts.getValue(DesignTag.COMPONENT_COMMON_VISIBLE) != null) {
						componentView.setVisibility(View.INVISIBLE);
					}
					if (sCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
								Integer.valueOf(atts.getValue(DesignTag.COMPONENT_COMMON_LWIDTH)),
								Integer.valueOf(atts.getValue(DesignTag.COMPONENT_COMMON_LHEIGHT)));
						layoutParams.leftMargin = Integer.valueOf(atts.getValue(DesignTag.COMPONENT_COMMON_LCOORDX));
						layoutParams.topMargin = Integer.valueOf(atts.getValue(DesignTag.COMPONENT_COMMON_LCOORDY));
						componentView.setLayoutParams(layoutParams);
			        } else {
			        	RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
								Integer.valueOf(atts.getValue(DesignTag.COMPONENT_COMMON_PWIDTH)),
								Integer.valueOf(atts.getValue(DesignTag.COMPONENT_COMMON_PHEIGHT)));
						layoutParams.leftMargin = Integer.valueOf(atts.getValue(DesignTag.COMPONENT_COMMON_PCOORDX));
						layoutParams.topMargin = Integer.valueOf(atts.getValue(DesignTag.COMPONENT_COMMON_PCOORDY));
						componentView.setLayoutParams(layoutParams);
			        }
					form.addSubView(componentView);
					
					//Add on-click event
					if (atts.getValue(DesignTag.EVENT_ONCLICK) != null) {
						component.setOnclickFunction(atts.getValue(DesignTag.EVENT_ONCLICK), componentView);
					}
					
					//Add on-change event
					if (atts.getValue(DesignTag.EVENT_ONCHANGE) != null) {
						component.setOnchangeFunction(atts.getValue(DesignTag.EVENT_ONCHANGE), componentView);
					}
    			}
    		}
        }
        
        @Override
        public void endElement(String namespaceURI, String tagName, String qName) throws SAXException{
        	if (tagName.equals(DesignTag.DESIGN_S)) {
        		isInSystemNode = false;
        	} else if (tagName.equals(DesignTag.DESIGN_F)) {
    			isInFormNode = false;
    			//Add onload event in a hashmap for calling this function by changing form 
    			sOnLoadFuncMap.put(formId, formOnload);
    		} else if (tagName.equals(DesignTag.COMPONENT_MENUBAR)) {
    			isInMenuBarNode = false;
    			form.setMenuItemNameList(menuItemNameList);
				form.setMenuItemOnClickList(menuItemOnClickList);
    		} else if (tagName.equals(DesignTag.COMPONENT_MENU)) {
    			isInMenuNode = false;
    		} else if (tagName.equals(DesignTag.COMPONENT_COMBOBOX)) {
    			isInComboBox = false;
    			component.setItemList(comboboxItemList);
    		} else if (tagName.equals(DesignTag.COMPONENT_COMBOBOX_ITEM)) {
    			isInComboBoxItem = false;
    		} else if (tagName.equals(DesignTag.COMPONENT_DATAVIEW)) {
    			isInDataview = false;
    			component.setDataviewColumns(columnInfos);
				component.setDataviewOncalculate(onCalculateMap);
    		} else if (tagName.equals(DesignTag.COMPONENT_DATAVIEW_COLUMN)) {
    			isInDataviewColumn = false;
    		}
        }
        
        @Override
        public void characters(char ch[], int start, int length){
        }
	}
}
