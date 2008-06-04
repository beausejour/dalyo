package com.penbase.dma.View;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.*;
import com.penbase.dma.R;
import com.penbase.dma.Dalyo.Database;
import com.penbase.dma.Dalyo.LoadingThread;
import com.penbase.dma.Dalyo.Component.Component;
import com.penbase.dma.Dalyo.Component.Form;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;
import com.penbase.dma.XmlElement.XmlTag;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.view.Menu.Item;
import android.widget.*;
import android.graphics.Color;


public class ApplicationView extends Activity { 		
	private static ApplicationView applicationView;	
	private static DmaHttpClient client;
	private static HashMap<String, Form> layoutsMap;
	private static HashMap<String, String> onLoadFuncMap;
	public static final int BACK_ID = Menu.FIRST;
	public static final int NEXT_ID = Menu.FIRST+1;	
	public static Document designDoc = null;
	public static Component component = null;
	public static Document behaviorDocument = null;
	public static Document dbDoc = null;		
	private static HashMap<String, String> resourcesFileMap;
	private static HashMap<String, Component> componentsMap;	
	public static final android.view.IWindowManager windowService = android.view.IWindowManager.Stub.asInterface(
			android.os.ServiceManager.getService("window"));	
	private static Database database;
	private LoadingThread loadingThread = null;		
	private ProgressDialog loadingbar;
	
	private Handler handler = new Handler()
	{
        @Override
        public void handleMessage(Message msg) 
        {
            switch (msg.what) 
            {
                 default:
                	 createView();
                	 loadingbar.dismiss();
                	 display();
                     break;
            }
        }
	};	
	
	@Override
	protected void onCreate(Bundle icicle) 
	{	
		super.onCreate(icicle);
		ApplicationView.applicationView = this;
		
		database = new Database(this, dbDoc);
		resourcesFileMap = client.getResourceMap("ext");		
		componentsMap = new HashMap<String, Component>();
		setTitle(ApplicationListView.applicationName);
        setContentView(R.layout.loading);	
        loadingbar = ProgressDialog.show(this, "Please wait...", "Building application ...", true, true);
        loadingThread = new LoadingThread(handler);
        loadingThread.Start();
	}
	
	public void display()
	{
		NodeList generalInfo = designDoc.getElementsByTagName(XmlTag.TAG_DESIGN_S_G);
		final String startFormId = ((Element) generalInfo.item(0)).getAttribute(XmlTag.TAG_DESIGN_S_G_FID);
		setContentView(layoutsMap.get(startFormId));		
	}
	
	public static void prepareData(int position, String login, String pwd)
	{
		client = new DmaHttpClient();		
		client.checkDownloadFile(position, login, pwd);

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
	
	public void createView()
	{		
		layoutsMap = new HashMap<String, Form>();
		onLoadFuncMap = new HashMap<String, String>();
		NodeList formsList = designDoc.getElementsByTagName(XmlTag.TAG_DESIGN_F);
		int formsListLen = formsList.getLength();
		for (int i=0; i<formsListLen; i++)
		{
			Form form = new Form(this);
			Element formElt = (Element) formsList.item(i);
			String formId = formElt.getAttribute(XmlTag.TAG_DESIGN_F_ID);
			Log.i("info", "Form "+i+" formid "+formId);
			
			if (!formElt.getAttribute(XmlTag.TAG_COMPONENT_COMMON_TABLEID).equals(""))
			{
				form.setTableId(formElt.getAttribute(XmlTag.TAG_COMPONENT_COMMON_TABLEID));
			}
			
			//Check background of a form
			if (formElt.hasAttribute(XmlTag.TAG_DESIGN_F_BC))
			{
				String backgourndColor = "#"+formElt.getAttribute(XmlTag.TAG_DESIGN_F_BC);
				form.setBackgroundColor(Color.parseColor(backgourndColor));
			}			
			
			NodeList formEltList = formElt.getChildNodes();					
			int formEltListLen = formEltList.getLength();
			for (int j=0; j<formEltListLen; j++)
			{
				//Log.i("info", "Form "+i+" element "+j);
				Element element = (Element) formEltList.item(j);
				
				if ((!element.getNodeName().equals(XmlTag.TAG_COMPONENT_MENUBAR)) &&
						(!element.getNodeName().equals(XmlTag.TAG_COMPONENT_NAVIBAR)))
				{
					if ((element.hasAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE)) && 
							(element.hasAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE)) &&
							(!element.getNodeName().equals(XmlTag.TAG_COMPONENT_COMBOBOX)) &&
							(!element.getNodeName().equals(XmlTag.TAG_COMPONENT_CHECKBOX)) &&
							(!element.getNodeName().equals(XmlTag.TAG_COMPONENT_DATAVIEW)) &&
							(!element.getNodeName().equals(XmlTag.TAG_COMPONENT_BUTTON)) &&
							(!element.getNodeName().equals(XmlTag.TAG_COMPONENT_LABEL)) &&
							(!element.getNodeName().equals(XmlTag.TAG_COMPONENT_TEXTFIELD)))
					{
						component = new Component(this, element.getNodeName(),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_LABEL),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE), 
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE));
					}
					else if (element.getNodeName().equals(XmlTag.TAG_COMPONENT_LABEL))
					{
						component = new Component(this, element.getNodeName(),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_LABEL),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE), 
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ALIGN), 
								element.hasAttribute(XmlTag.TAG_COMPONENT_COMMON_ALIGN));
					}
					else if (element.getNodeName().equals(XmlTag.TAG_COMPONENT_TEXTFIELD))
					{
						Log.i("info", "editable "+element.hasAttribute(XmlTag.TAG_COMPONENT_TEXTFIELD_EDIT));
						if ((element.hasAttribute(XmlTag.TAG_COMPONENT_COMMON_TABLEID)) &&
								(element.hasAttribute(XmlTag.TAG_COMPONENT_COMMON_FIELDID)))
						{
							component = new Component(this, element.getNodeName(),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE), 
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ALIGN), 
									element.hasAttribute(XmlTag.TAG_COMPONENT_COMMON_ALIGN),
									element.getAttribute(XmlTag.TAG_COMPONENT_TEXTFIELD_MULTI),
									element.hasAttribute(XmlTag.TAG_COMPONENT_TEXTFIELD_EDIT),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_TABLEID), 
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FIELDID));
						}
						else
						{
							component = new Component(this, element.getNodeName(),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE), 
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ALIGN), 
									element.hasAttribute(XmlTag.TAG_COMPONENT_COMMON_ALIGN),
									element.getAttribute(XmlTag.TAG_COMPONENT_TEXTFIELD_MULTI),
									element.hasAttribute(XmlTag.TAG_COMPONENT_TEXTFIELD_EDIT),
									null, null);
						}
					}
					else if (element.getNodeName().equals(XmlTag.TAG_COMPONENT_CHECKBOX))
					{
						component = new Component(this, element.getNodeName(),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_LABEL),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE), 
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE),
								element.getAttribute(XmlTag.TAG_COMPONENT_CHECKBOX_CHECKED));
					}
					else if (element.getNodeName().equals(XmlTag.TAG_COMPONENT_BUTTON))
					{					
						if (element.hasAttribute(XmlTag.TAG_COMPONENT_COMMON_BACKGROUND))
						{					
							component = new Component(this, element.getNodeName(),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_LABEL),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE), 
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE),
									Integer.valueOf(element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_BACKGROUND)),
									resourcesFileMap.get(element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_BACKGROUND)));							
						}
						else
						{						
							component = new Component(this, element.getNodeName(),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_LABEL),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE), 
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE), 0, null);
						}
					}
					else if (element.getNodeName().equals(XmlTag.TAG_COMPONENT_IMAGE))														
					{
						if (element.hasAttribute(XmlTag.TAG_COMPONENT_COMMON_BACKGROUND))
						{
							component = new Component(this, element.getNodeName(),
									Integer.valueOf(element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_BACKGROUND)),
									resourcesFileMap.get(element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_BACKGROUND)));
						}
						else
						{
							component = new Component(this, element.getNodeName(), 0, null);
						}
					}
					else if (element.getNodeName().equals(XmlTag.TAG_COMPONENT_COMBOBOX))
					{					
						ArrayList<String> itemList = new ArrayList<String>();
						NodeList nodeItemList = element.getChildNodes();
						Log.i("info", "item size "+nodeItemList.getLength());
						if (nodeItemList.getLength() > 0)
						{
							int itemLen = nodeItemList.getLength();
							for (int k=0; k<itemLen; k++)
							{
								Element item = (Element) nodeItemList.item(k);													
								
								if ((item.getNodeName().equals(XmlTag.TAG_COMPONENT_COMBOBOX_ITEM)) && 
										(item.hasAttribute(XmlTag.TAG_COMPONENT_COMBOBOX_ITEM_VALUE)))
								{
									String value = item.getAttribute(XmlTag.TAG_COMPONENT_COMBOBOX_ITEM_VALUE);
									itemList.add(value);
								}
							}
							component = new Component(this, element.getNodeName(),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID), itemList,
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE), 
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE));
						}
						else if ((element.hasAttribute(XmlTag.TAG_COMPONENT_COMBOBOX_LABELTABLE)) &&
								(element.hasAttribute(XmlTag.TAG_COMPONENT_COMBOBOX_LABELFIELD)) &&
								(element.hasAttribute(XmlTag.TAG_COMPONENT_COMBOBOX_VALUETABLE)) &&
								(element.hasAttribute(XmlTag.TAG_COMPONENT_COMBOBOX_VALUEFIELD)))
						{
							ArrayList<String> labelList = new ArrayList<String>();
							labelList.add(element.getAttribute(XmlTag.TAG_COMPONENT_COMBOBOX_LABELTABLE));
							labelList.add(element.getAttribute(XmlTag.TAG_COMPONENT_COMBOBOX_LABELFIELD));
							
							ArrayList<String> valueList = new ArrayList<String>();
							valueList.add(element.getAttribute(XmlTag.TAG_COMPONENT_COMBOBOX_VALUETABLE));
							valueList.add(element.getAttribute(XmlTag.TAG_COMPONENT_COMBOBOX_VALUEFIELD));
							
							Log.i("info", "comboox valueList size "+valueList.size()+" labelList size "+labelList.size());
							component = new Component(this, element.getNodeName(),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID), labelList, valueList,
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE), 
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE));
						}
						else
						{
							component = new Component(this, element.getNodeName(),
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID), itemList,
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE), 
									element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE));
						}
					}
					else if (element.getNodeName().equals(XmlTag.TAG_COMPONENT_DATAVIEW))
					{
						ArrayList<ArrayList<String>> columnInfos = new ArrayList<ArrayList<String>>();
						NodeList nodeItemList = element.getChildNodes();						
						
						if (nodeItemList.getLength() > 0)
						{
							int nbColumn = nodeItemList.getLength();
							for (int k=0; k<nbColumn; k++)
					        {
					        	Element column = (Element) element.getChildNodes().item(k);
					        	if (column.getNodeName().equals(XmlTag.TAG_COMPONENT_DATAVIEW_COLUMN))
					        	{
						        	ArrayList<String> acolumn = new ArrayList<String>();
						        	acolumn.add(column.getAttribute(XmlTag.TAG_COMPONENT_COMMON_TABLEID));
						        	acolumn.add(column.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FIELDID));
						        	acolumn.add(column.getAttribute(XmlTag.TAG_COMPONENT_DATAVIEW_COLUMN_HEADER));
						        	acolumn.add(column.getAttribute(XmlTag.TAG_COMPONENT_COMMON_PWIDTH));
						        	acolumn.add(column.getAttribute(XmlTag.TAG_COMPONENT_COMMON_LWIDTH));
						        	acolumn.add(column.getAttribute(XmlTag.TAG_COMPONENT_DATAVIEW_COLUMN_CALC));
						        	columnInfos.add(acolumn);
					        	}
					        }
						}
						
						component = new Component(this, element.getNodeName(), columnInfos,
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE), 
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_TABLEID));
					}
					else
					{
						Log.i("info", "else");
						component = new Component(this, element.getNodeName(), 
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID),"", "normal", "normal");
						Log.i("info", "create else ok");
					}
					
					componentsMap.put(element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID), component);
					
					//Add onclick event
					if (element.hasAttribute(XmlTag.TAG_EVENT_ONCLICK))
					{
						Log.i("info", "click event");
						component.setOnclickFunction(element.getAttribute(XmlTag.TAG_EVENT_ONCLICK), component.getView());
					}
					
					//Add onchange event
					if (element.hasAttribute(XmlTag.TAG_EVENT_ONCHANGE))
					{
						Log.i("info", "onchange event");
						component.setOnchangeFunction(element.getAttribute(XmlTag.TAG_EVENT_ONCHANGE), component.getView());
					}
					
					//windowService.setOrientation(1);
					//windowService.setOrientation(0);
					if (getOrientation() == 0)		//Orientation vertical
					{
						component.getView().setLayoutParams(new AbsoluteLayout.LayoutParams(
								Integer.valueOf(element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_PWIDTH)),
								Integer.valueOf(element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_PHEIGHT)),
								Integer.valueOf(element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_PCOORDX)),
								Integer.valueOf(element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_PCOORDY))));
					}
					else if (getOrientation() == 1)	//Orientation hrizontal  
					{							
						component.getView().setLayoutParams(new AbsoluteLayout.LayoutParams(
								Integer.valueOf(element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_LWIDTH)),
								Integer.valueOf(element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_LHEIGHT)),
								Integer.valueOf(element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_LCOORDX)),
								Integer.valueOf(element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_LCOORDY))));						
					}					
					form.addView(component.getView());
					Log.i("info", "after put");
				}
			}			
			//Add onload event in a hashmap for calling this function by changing form 
			onLoadFuncMap.put(formId, formElt.getAttribute(XmlTag.TAG_EVENT_ONLOAD));			
			layoutsMap.put(formId, form);
			Log.i("info", "add layout ok");
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	 boolean result = super.onCreateOptionsMenu(menu);
    	 menu.add(0, BACK_ID, R.string.back);
    	 menu.add(0, NEXT_ID, R.string.next);
         return result;
    }
    
    @Override
    public boolean onOptionsItemSelected(Item item) {
        switch (item.getId()) 
        {
        	case BACK_ID:
        		//setContentView(layoutList.get(0));
	            return true;
        	case NEXT_ID:
        		//setContentView(layoutList.get(1));
	            return true;        
        }       
        return super.onOptionsItemSelected(item);
    }
    
    public static final int getOrientation()
    {
    	int orientation = 0;
    	try
    	{
    		if (windowService.getOrientation() == 0)
    		{
    			orientation = 0;
    		}
    		else if (windowService.getOrientation() == 1)
    		{
    			orientation = 1;
    		}
    	}
    	catch (DeadObjectException e) 
		{
			e.printStackTrace();
		}
		return orientation;    	
    }
    
    public static HashMap<String, Component> getComponents()
    {
    	return componentsMap;
    }
    
    public static HashMap<String, Form> getLayoutsMap()
    {
    	return layoutsMap;
    }
    
    public static Database getDataBase()
    {
    	return database;
    }
    
    public static DmaHttpClient getCurrentClient()
    {
    	return client;
    }
    
    public static void refreshComponent(String componentId, Object filter)
    {
    	int size = componentsMap.size();
    	Log.i("info", "sizeof "+size);
    	if (componentsMap.containsKey(componentId))
    	{
    		componentsMap.get(componentId).refreshComponentContent(filter);
    	}
    }
    
    public static ApplicationView getCurrentView()
    {
    	return applicationView;
    }
    
    public static HashMap<String, String> getOnLoadFuncMap()
    {
    	return onLoadFuncMap;
    }
}
