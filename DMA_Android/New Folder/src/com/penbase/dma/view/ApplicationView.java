package com.penbase.dma.view;

import java.util.ArrayList;
import org.w3c.dom.*;
import com.penbase.dma.DmaHttpClient;
import com.penbase.dma.R;
import com.penbase.dma.Dalyo.Database;
import com.penbase.dma.Dalyo.Component.Component;
import com.penbase.dma.xml.XmlTag;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.util.Log;
import android.view.*;
import android.view.Menu.Item;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.graphics.Color;


public class ApplicationView extends Activity {
	private static DmaHttpClient client;
	private ArrayList<AbsoluteLayout> layoutList;
	public static final int BACK_ID = Menu.FIRST;
	public static final int NEXT_ID = Menu.FIRST+1;
	
	private static Document designDoc = null;
	private static Component component = null;
	private static Document behaviorDocument = null;
	private static Document dbDoc = null;
	
	private Context context = ApplicationView.this;
	
	public static final android.view.IWindowManager windowService = android.view.IWindowManager.Stub.asInterface(
			android.os.ServiceManager.getService("window"));
	
	@Override
	protected void onCreate(Bundle icicle) {	
		super.onCreate(icicle);
		
		NodeList generalInfo = designDoc.getElementsByTagName(XmlTag.TAG_DESIGN_S_G);
		String startFormId = ((Element) generalInfo.item(0)).getAttribute(XmlTag.TAG_DESIGN_S_G_FID);
		
		Log.i("info", "startid "+startFormId);		
		
		Database database = new Database(this, dbDoc);

		//Get the first form
		//setContentView(formList.get(Integer.valueOf(startFormId)-1));		
		
		Log.i("info", "context oncreate "+context);
		createView();

		setContentView(layoutList.get(0));
	}	
	
	public static void prepareData(String login, String pwd)
	{
		client = new DmaHttpClient();
		
		behaviorDocument = client.getBehavior(ApplicationListView.applicationInfos.get("AppId"),
				ApplicationListView.applicationInfos.get("AppVer"),
				ApplicationListView.applicationInfos.get("AppBuild"),
				ApplicationListView.applicationInfos.get("SubId"), 
				ApplicationListView.applicationInfos.get("DbId"),login, pwd);
		
		designDoc = client.getDesign(ApplicationListView.applicationInfos.get("AppId"),
				ApplicationListView.applicationInfos.get("AppVer"),
				ApplicationListView.applicationInfos.get("AppBuild"),
				ApplicationListView.applicationInfos.get("SubId"), login, pwd);
		
		client.getResources(ApplicationListView.applicationInfos.get("AppId"),
				ApplicationListView.applicationInfos.get("AppVer"),
				ApplicationListView.applicationInfos.get("AppBuild"),
				ApplicationListView.applicationInfos.get("SubId"), 
				ApplicationListView.applicationInfos.get("DbId"),login, pwd);
		
		dbDoc = client.getDB(ApplicationListView.applicationInfos.get("AppId"),
				ApplicationListView.applicationInfos.get("AppVer"),
				ApplicationListView.applicationInfos.get("AppBuild"),
				ApplicationListView.applicationInfos.get("SubId"), login, pwd);
	}
	
	public void createView()
	{
		layoutList = new ArrayList<AbsoluteLayout>();
		NodeList formsList = designDoc.getElementsByTagName(XmlTag.TAG_DESIGN_F);
		int formsListLen = formsList.getLength();
		for (int i=0; i<formsListLen; i++)
		{
			Log.i("info", "Form "+i);
			AbsoluteLayout absLayout = new AbsoluteLayout(this);
			absLayout.setLayoutParams(new AbsoluteLayout.LayoutParams(LayoutParams.FILL_PARENT, 
					LayoutParams.FILL_PARENT, 0, 0));

			Element form = (Element) formsList.item(i);
			
			if (form.hasAttribute(XmlTag.TAG_DESIGN_F_BC))
			{
				String backgourndColor = "#"+form.getAttribute(XmlTag.TAG_DESIGN_F_BC);
				absLayout.setBackgroundColor(Color.parseColor(backgourndColor));
			}
			
			NodeList formEltList = form.getChildNodes();
			Log.i("info", "childnode size "+formEltList.getLength());						
			
			int formEltListLen = formEltList.getLength();
			for (int j=0; j<formEltListLen; j++)
			{
				Log.i("info", "Form "+i+" element "+j);
				Element element = (Element) formEltList.item(j);
				
				if ((!element.getNodeName().equals(XmlTag.TAG_COMPONENT_MENUBAR)) &&
						(!element.getNodeName().equals(XmlTag.TAG_COMPONENT_NAVIBAR)))
				{
					if ((element.hasAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE)) && 
							(element.hasAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE)) &&
							(!element.getNodeName().equals(XmlTag.TAG_COMPONENT_COMBOBOX)) &&
							(!element.getNodeName().equals(XmlTag.TAG_COMPONENT_CHECKBOX)) &&
							(!element.getNodeName().equals(XmlTag.TAG_COMPONENT_DATAVIEW)))
					{
						component = new Component(this, element.getNodeName(),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_LABEL),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE), 
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE));
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
					else if (element.getNodeName().equals(XmlTag.TAG_COMPONENT_IMAGE))
					{
						if (element.hasAttribute(XmlTag.TAG_COMPONENT_IMAGE_BACKGROUND))
						{
							component = new Component(this, element.getNodeName(),
									element.getAttribute(XmlTag.TAG_COMPONENT_IMAGE_BACKGROUND));							
						}
						else
						{
							component = new Component(this, element.getNodeName(), null);
						}
					}
					else if (element.getNodeName().equals(XmlTag.TAG_COMPONENT_COMBOBOX))
					{					
						ArrayList<String> itemList = new ArrayList<String>();
						NodeList nodeItemList = element.getChildNodes(); 
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
						}

						component = new Component(this, element.getNodeName(),
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID), itemList,
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTSIZE), 
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_FONTTYPE));
						
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
						        	acolumn.add(column.getAttribute(XmlTag.TAG_COMPONENT_DATAVIEW_TABLEID));
						        	acolumn.add(column.getAttribute(XmlTag.TAG_COMPONENT_DATAVIEW_FIELDID));
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
								element.getAttribute(XmlTag.TAG_COMPONENT_DATAVIEW_TABLEID));
					}
					else
					{
						Log.i("info", "else");
						component = new Component(this, element.getNodeName(), 
								element.getAttribute(XmlTag.TAG_COMPONENT_COMMON_ID),"", "normal", "normal");
					}										
										
					//windowService.setOrientation(1);
					//windowService.setOrientation(0);
					if (getOrientation() == 0)		//Orientation vertical
					{							
						Log.i("info", "orientation 1 "+getOrientation());
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
						Log.i("info", "orientation 0 "+getOrientation());
					}					
										
					absLayout.addView(component.getView());
				}
			}
			layoutList.add(absLayout);
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
        		setContentView(layoutList.get(0));
	            return true;
        	case NEXT_ID:
        		setContentView(layoutList.get(1));
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
}
