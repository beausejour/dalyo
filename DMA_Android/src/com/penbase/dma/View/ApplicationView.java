package com.penbase.dma.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.graphics.Color;

import com.penbase.dma.R;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DesignTag;
import com.penbase.dma.Dalyo.Component.Component;
import com.penbase.dma.Dalyo.Component.DalyoComponent;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Generic object for Dalyo application, it builds application's UI, database
 * etc.
 */
public class ApplicationView extends Activity {
	private static Menu sCurrentMenu = null;
	private static ApplicationView sApplicationView = null;
	private static DmaHttpClient sClient = null;
	private static HashMap<String, Form> sLayoutsMap = null;
	private static HashMap<String, String> sOnLoadFuncMap = null;
	private static Document sBehaviorDocument = null;
	private static Document sDbDoc = null;
	private static HashMap<String, Component> sComponentsMap = null;
	private static DatabaseAdapter sDatabase = null;
	private ProgressDialog mLoadingDialog;
	private static String sCurrentFormId = null;
	private static String sApplicationVersion = null;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mLoadingDialog.setMessage(sResources
						.getText(R.string.buildingapplication));
				parseXml();
				break;
			case 1:
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
	private static String sApplicationId = null;
	private static String sUsername = null;
	private static Resources sResources = null;
	private static HashMap<String, String> sApplicationInfos = null;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		sResources = getResources();
		ApplicationView.sApplicationView = this;
		sComponentsMap = new HashMap<String, Component>();
		mLoadingDialog = ProgressDialog.show(this, sResources
				.getText(R.string.waiting), sResources
				.getText(R.string.preparingapplication), true, true);
		Intent intent = getIntent();
		sApplicationInfos = (HashMap<String, String>) intent
				.getSerializableExtra("APPLICATION");
		sApplicationId = sApplicationInfos.get("AppId");
		sUsername = sApplicationInfos.get("Username");
		setTitle(sApplicationInfos.get("Title"));
		deleteTempDirectory();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					prepareData();
					mHandler.sendEmptyMessage(0);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * Prepares necessary xml document data
	 * 
	 * @throws FileNotFoundException
	 */
	private void prepareData() throws FileNotFoundException {
		String pwd = sApplicationInfos.get("Userpassword");
		sClient = new DmaHttpClient(this, sUsername, sApplicationId);
		sClient.checkXmlFiles();

		String urlRequest = sClient.generateRegularUrlRequest(sApplicationId,
				sApplicationInfos.get("AppVer"), sApplicationInfos
						.get("AppBuild"), sApplicationInfos.get("SubId"),
				sUsername, pwd);
		sClient.getResource(urlRequest);
		sDesignReader = sClient.getDesignReader(urlRequest);
		sBehaviorDocument = sClient.getBehavior(urlRequest);
		sDbDoc = sClient.getDB(urlRequest);
	}

	private void parseXml() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer databasePath = new StringBuffer(
						Constant.APPPACKAGE);
				databasePath.append(Constant.USERDIRECTORY);
				databasePath.append(sUsername).append("/");
				databasePath.append(sApplicationId).append("/");
				databasePath.append(Constant.APPDB);
				sDatabase = new DatabaseAdapter(sDbDoc, databasePath.toString());
				new Function(ApplicationView.this, sBehaviorDocument);
				mHandler.sendEmptyMessage(1);
			}
		}).start();
	}

	/**
	 * Delete temporary image folder (barcode, doodle etc.)
	 */
	private void deleteTempDirectory() {
		StringBuffer tempFilePath = new StringBuffer(Constant.APPPACKAGE);
		tempFilePath.append(Constant.USERDIRECTORY);
		tempFilePath.append(sUsername).append("/");
		tempFilePath.append(sApplicationId).append("/");
		tempFilePath.append(Constant.TEMPDIRECTORY);
		File tempDirectory = new File(tempFilePath.toString());
		if (tempDirectory.exists()) {
			deleteDirectory(tempDirectory);
		}
	}

	private boolean deleteDirectory(File directory) {
		if (directory.isDirectory()) {
			String[] children = directory.list();
			int length = children.length;
			for (int i = 0; i < length; i++) {
				boolean success = deleteDirectory(new File(directory,
						children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return directory.delete();
	}

	/**
	 * Displays first form of application
	 */
	private void display() {
		if (mOnLoadFunctionName != null) {
			Function.createFunction(mOnLoadFunctionName);
		}
		sCurrentFormId = mStartFormId;
		if (sOnLoadFuncMap.containsKey(mStartFormId)) {
			sLayoutsMap.get(mStartFormId).onLoad(
					sOnLoadFuncMap.get(mStartFormId));
		}
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
		} catch (ParserConfigurationException e) {
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

	public static HashMap<String, String> getApplicationsInfo() {
		return sApplicationInfos;
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
		ArrayList<String> menuItemNameList = sLayoutsMap.get(sCurrentFormId)
				.getMenuItemNameList();
		if (sCurrentMenu != null) {
			sCurrentMenu.clear();
			int itemsSize = menuItemNameList.size();
			if (itemsSize > 0) {
				for (int i = 0; i < itemsSize; i++) {
					sCurrentMenu.add(Menu.NONE, i, Menu.NONE,
							menuItemNameList.get(i))
							.setOnMenuItemClickListener(
									new OnMenuItemClickListener() {
										@Override
										public boolean onMenuItemClick(
												MenuItem item) {
											ArrayList<String> menuItemOnClickList = sLayoutsMap
													.get(sCurrentFormId)
													.getMenuItemOnClickList();
											String menuId = menuItemOnClickList
													.get(item.getItemId());
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
		dialog.setTitle(sResources.getText(R.string.error));
		dialog.setButton(sResources.getText(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// sApplicationView.finish();
					}

				});
		dialog.show();
	}

	public static String getApplicationVersion() {
		return sApplicationVersion;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		sDatabase.closeDatabase();
		sCurrentMenu = null;
		sApplicationView = null;
		sClient = null;
		sLayoutsMap = null;
		sOnLoadFuncMap = null;
		sBehaviorDocument = null;
		sDbDoc = null;
		sComponentsMap = null;
		sDatabase = null;
		sCurrentFormId = null;
		sApplicationVersion = null;
		sDesignReader = null;
		sApplicationId = null;
		sUsername = null;
		sApplicationInfos = null;
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

		public void startElement(String namespaceURI, String tagName,
				String qName, Attributes atts) throws SAXException {
			if (tagName.equals(DesignTag.DESIGN_S)) {
				isInSystemNode = true;
			} else if (tagName.equals(DesignTag.DESIGN_F)) {
				isInFormNode = true;
				form = new Form(ApplicationView.this);
				formId = atts.getValue(DesignTag.DESIGN_F_ID);
				formOnload = atts.getValue(DesignTag.EVENT_ONLOAD);
				String tableId = atts
						.getValue(DesignTag.COMPONENT_COMMON_TABLEID);
				if (tableId != null) {
					form.setTableId(tableId);
				}
				
				String isModal = atts.getValue(DesignTag.DESIGN_F_M);
				form.setModal(isModal);

				// Check background of a form
				String formBackgroundColor = atts
						.getValue(DesignTag.DESIGN_F_BC);
				if (formBackgroundColor != null) {
					form.setBackgroundColor(Color.parseColor("#"
							+ formBackgroundColor));
				} else {
					// Default background color is white
					form.setBackgroundColor(Color.WHITE);
				}

				form.setDimension(Integer.valueOf(atts.getValue(DesignTag.DESIGN_F_W)),
						Integer.valueOf(atts.getValue(DesignTag.DESIGN_F_H)));
				
				// Check form's title
				String formTitle = atts.getValue(DesignTag.DESIGN_F_TITLE);
				if (formTitle != null) {
					form.setTitle(formTitle);
				}
				sLayoutsMap.put(formId, form);
			} else if (isInSystemNode) {
				if (tagName.equals(DesignTag.DESIGN_S_G)) {
					String onStartFunction = atts
							.getValue(DesignTag.DESIGN_S_G_OS);
					if (onStartFunction != null) {
						mOnLoadFunctionName = onStartFunction;
					}
					mStartFormId = atts.getValue(DesignTag.DESIGN_S_G_FID);
					sApplicationVersion = atts
							.getValue(DesignTag.DESIGN_S_G_PV);
				}
			} else if (isInFormNode) {
				if (isInMenuBarNode) {
					if (isInMenuNode) {
						if (tagName.equals(DesignTag.COMPONENT_MENUITEM)) {
							menuItemNameList.add(atts
									.getValue(DesignTag.COMPONENT_COMMON_NAME));
							String onClick = atts
									.getValue(DesignTag.EVENT_ONCLICK);
							if (onClick != null) {
								menuItemOnClickList.add(onClick);
							} else {
								menuItemOnClickList.add("");
							}
						}
					}
					if (tagName.equals(DesignTag.COMPONENT_MENU)) {
						if (atts.getValue(DesignTag.EVENT_ONCLICK) != null) {
							menuItemNameList.add(atts
									.getValue(DesignTag.COMPONENT_COMMON_NAME));
							String onClick = atts
									.getValue(DesignTag.EVENT_ONCLICK);
							if (onClick != null) {
								menuItemOnClickList.add(onClick);
							} else {
								menuItemOnClickList.add("");
							}
						} else {
							isInMenuNode = true;
						}
					}
				} else if (isInComboBox) {
					String itemValue = atts
							.getValue(DesignTag.COMPONENT_COMMON_VALUE);
					if (tagName.equals(DesignTag.COMPONENT_COMBOBOX_ITEM)
							&& itemValue != null) {
						isInComboBoxItem = true;
						comboboxItemList.add(itemValue);
					}
				} else if (isInDataview) {
					if (tagName.equals(DesignTag.COMPONENT_DATAVIEW_COLUMN)) {
						isInDataviewColumn = true;
						ArrayList<String> acolumn = new ArrayList<String>();

						acolumn.add(atts
								.getValue(DesignTag.COMPONENT_COMMON_TABLEID));
						acolumn.add(atts
								.getValue(DesignTag.COMPONENT_COMMON_FIELDID));
						acolumn
								.add(atts
										.getValue(DesignTag.COMPONENT_DATAVIEW_COLUMN_HEADER));
						acolumn.add(atts
								.getValue(DesignTag.COMPONENT_COMMON_PWIDTH));
						acolumn.add(atts
								.getValue(DesignTag.COMPONENT_COMMON_LWIDTH));

						if (atts.getValue(
								DesignTag.COMPONENT_DATAVIEW_COLUMN_CALC)
								.equals(Constant.TRUE)) {
							String onCalculate = atts
									.getValue(DesignTag.EVENT_ONCALCULATE);
							if (onCalculate != null) {
								onCalculateMap.put(column, onCalculate);
							} else {
								onCalculateMap.put(column, "");
							}
						}
						columnInfos.add(acolumn);
						column += 1;
					}
				}

				// Menu bar
				if (tagName.equals(DesignTag.COMPONENT_MENUBAR)) {
					isInMenuBarNode = true;
					// Menu item name list and on-click event list
					menuItemNameList = new ArrayList<String>();
					menuItemOnClickList = new ArrayList<String>();
				} else if (tagName.equals(DesignTag.COMPONENT_NAVIBAR)) {
					// Navigation bar
				} else if (!tagName.equals(DesignTag.COMPONENT_MENU)
						&& !tagName.equals(DesignTag.COMPONENT_MENUITEM)
						&& !isInDataviewColumn && !isInComboBoxItem) {
					// Graphic component
					component = new Component(ApplicationView.this, tagName);
					String id = atts.getValue(DesignTag.COMPONENT_COMMON_ID);
					if (id != null) {
						component.setId(id);
					}
					String fontColor = atts
							.getValue(DesignTag.COMPONENT_COMMON_FONTCOLOR);
					if (fontColor != null) {
						component.setFontColor(fontColor);
					}
					String backgroundColor = atts
							.getValue(DesignTag.COMPONENT_COMMON_BACKGROUNDCOLOR);
					if (backgroundColor != null) {
						component.setBackgroundColor(backgroundColor);
					}
					String fontSize = atts
							.getValue(DesignTag.COMPONENT_COMMON_FONTSIZE);
					if (fontSize != null) {
						component.setFontSize(fontSize);
					}
					String fontType = atts
							.getValue(DesignTag.COMPONENT_COMMON_FONTTYPE);
					if (fontType != null) {
						component.setFontType(fontType);
					}
					String align = atts
							.getValue(DesignTag.COMPONENT_COMMON_ALIGN);
					if (align != null) {
						component.setAlign(align);
					}
					String textAlign = atts
							.getValue(DesignTag.COMPONENT_COMMON_TEXTALIGN);
					if (textAlign != null) {
						component.setAlign(textAlign);
					}
					String label = atts
							.getValue(DesignTag.COMPONENT_COMMON_LABEL);
					if (label != null) {
						component.setLabel(label);
					}
					String tableId = atts
							.getValue(DesignTag.COMPONENT_COMMON_TABLEID);
					if (tableId != null) {
						component.setTableId(tableId);
					}
					String fieldId = atts
							.getValue(DesignTag.COMPONENT_COMMON_FIELDID);
					if (fieldId != null) {
						component.setFieldId(fieldId);
					}
					String background = atts
							.getValue(DesignTag.COMPONENT_COMMON_BACKGROUND);
					if (background != null) {
						component.setBackGround(background);
					}
					String defaultValue = atts.getValue(DesignTag.COMPONENT_COMMON_VALUE);
					if (defaultValue != null) {
						component.setDefaultValue(defaultValue);
					}

					if (tagName.equals(DesignTag.COMPONENT_COMBOBOX)) {
						String labelTable = atts
								.getValue(DesignTag.COMPONENT_COMBOBOX_LABELTABLE);
						String labelField = atts
								.getValue(DesignTag.COMPONENT_COMBOBOX_LABELFIELD);
						String valueTable = atts
								.getValue(DesignTag.COMPONENT_COMBOBOX_VALUETABLE);
						String valueField = atts
								.getValue(DesignTag.COMPONENT_COMBOBOX_VALUEFIELD);
						isInComboBox = true;
						if ((labelTable != null) && (labelField != null)
								&& (valueTable != null) && (valueField != null)) {
							ArrayList<String> labelList = new ArrayList<String>();
							labelList.add(labelTable);
							labelList.add(labelField);

							ArrayList<String> valueList = new ArrayList<String>();
							valueList.add(valueTable);
							valueList.add(valueField);

							component.setLabelList(labelList);
							component.setValueList(valueList);
							comboboxItemList = new ArrayList<String>();
						} else {
							//isInComboBox = true;
							comboboxItemList = new ArrayList<String>();
						}
						String bullet = atts
								.getValue(DesignTag.COMPONENT_COMBOBOX_BULLET);
						if (bullet != null) {
							component.setBullet(bullet);
						}
					} else if (tagName.equals(DesignTag.COMPONENT_DATAVIEW)) {
						isInDataview = true;
						columnInfos = new ArrayList<ArrayList<String>>();
						onCalculateMap = new HashMap<Integer, String>();
					} else if (tagName.equals(DesignTag.COMPONENT_DATEFIELD)
							|| tagName.equals(DesignTag.COMPONENT_TIMEFIELD)) {
						String dateTime = atts
								.getValue(DesignTag.COMPONENT_DATEFIELD_DATETIME);
						if (dateTime != null) {
							component.setDateTime(dateTime);
						}
					} else if ((tagName.equals(DesignTag.COMPONENT_GAUGE))
							|| (tagName.equals(DesignTag.COMPONENT_NUMBERBOX))) {
						String minValue = atts
								.getValue(DesignTag.COMPONENT_MIN);
						if (minValue != null) {
							component.setMinValue(Integer.valueOf(minValue));
						}
						String maxValue = atts
								.getValue(DesignTag.COMPONENT_MAX);
						if (maxValue != null) {
							component.setMaxValue(Integer.valueOf(maxValue));
						}
						String stepValue = atts.getValue(DesignTag.COMPONENT_STEP);
						if (stepValue != null) {
							component.setStepValue(Integer.valueOf(stepValue));
						}
					} else if (tagName.equals(DesignTag.COMPONENT_TEXTFIELD)
							|| tagName.equals(DesignTag.COMPONENT_TEXTZONE)) {
						String multiple = atts
								.getValue(DesignTag.COMPONENT_TEXTFIELD_MULTI);
						if (multiple != null) {
							component.setMultiLine(multiple);
						}
						String trigger = atts
								.getValue(DesignTag.COMPONENT_TEXTFIELD_TRIGGER);
						if (trigger != null) {
							component.setTrigger(trigger);
						}
						if (atts.getValue(DesignTag.COMPONENT_TEXTFIELD_EDIT) != null) {
							component.setEditable(true);
						} else {
							component.setEditable(false);
						}
						String password = atts.getValue(DesignTag.COMPONENT_TEXTFIELD_PASSWORD);
						if (password != null) {
							component.setPassword(true);
						}
						component
								.setTextFilter(atts
										.getValue(DesignTag.COMPONENT_TEXTFIELD_TEXTFILTER));
					}
					int orientation = ApplicationView.this.getResources()
							.getConfiguration().orientation;
					if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
						sCurrentOrientation = Configuration.ORIENTATION_LANDSCAPE;
					} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
						sCurrentOrientation = Configuration.ORIENTATION_PORTRAIT;
					}

					component.setView();
					sComponentsMap.put(id, component);

					DalyoComponent dalyoComponent = component
							.getDalyoComponent();
					if (atts.getValue(DesignTag.COMPONENT_COMMON_ENABLE) != null) {
						dalyoComponent.setComponentEnabled(false);
					}
					if (atts.getValue(DesignTag.COMPONENT_COMMON_VISIBLE) != null) {
						dalyoComponent.setComponentVisible(false);
					}
					int componentDefaultHeight = dalyoComponent
							.getMinimumHeight();
					int componentDefaultWidth = dalyoComponent
							.getMinimumWidth();
					if (sCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
						int width = Integer.valueOf(atts
								.getValue(DesignTag.COMPONENT_COMMON_LWIDTH));
						int height = Integer.valueOf(atts
								.getValue(DesignTag.COMPONENT_COMMON_LHEIGHT));
						if (width < componentDefaultWidth) {
							width = componentDefaultWidth;
						}
						if (height < componentDefaultHeight) {
							height = componentDefaultHeight;
						}
						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
								width, height);
						layoutParams.leftMargin = Integer.valueOf(atts
								.getValue(DesignTag.COMPONENT_COMMON_LCOORDX));
						layoutParams.topMargin = Integer.valueOf(atts
								.getValue(DesignTag.COMPONENT_COMMON_LCOORDY));
						((View) dalyoComponent).setLayoutParams(layoutParams);
					} else {
						int width = Integer.valueOf(atts
								.getValue(DesignTag.COMPONENT_COMMON_PWIDTH));
						int height = Integer.valueOf(atts
								.getValue(DesignTag.COMPONENT_COMMON_PHEIGHT));
						if (width < componentDefaultWidth) {
							width = componentDefaultWidth;
						}
						if (height < componentDefaultHeight) {
							height = componentDefaultHeight;
						}
						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
								width, height);
						layoutParams.leftMargin = Integer.valueOf(atts
								.getValue(DesignTag.COMPONENT_COMMON_PCOORDX));
						layoutParams.topMargin = Integer.valueOf(atts
								.getValue(DesignTag.COMPONENT_COMMON_PCOORDY));
						((View) dalyoComponent).setLayoutParams(layoutParams);
					}
					form.addSubView((View) dalyoComponent);

					// Add on-click event
					String onClick = atts.getValue(DesignTag.EVENT_ONCLICK);
					if (onClick != null) {
						dalyoComponent.setOnClickEvent(onClick);
					}

					// Add on-change event
					String onChange = atts.getValue(DesignTag.EVENT_ONCHANGE);
					if (onChange != null) {
						dalyoComponent.setOnChangeEvent(onChange);
					}
				}
			}
		}

		@Override
		public void endElement(String namespaceURI, String tagName, String qName)
				throws SAXException {
			if (tagName.equals(DesignTag.DESIGN_S)) {
				isInSystemNode = false;
			} else if (tagName.equals(DesignTag.DESIGN_F)) {
				isInFormNode = false;
				// Add onload event in a hashmap for calling this function by
				// changing form
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
		public void characters(char ch[], int start, int length) {
		}
	}
}
