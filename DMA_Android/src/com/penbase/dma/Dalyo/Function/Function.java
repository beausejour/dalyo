package com.penbase.dma.Dalyo.Function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.penbase.dma.Dma;
import com.penbase.dma.View.ApplicationListView;
import com.penbase.dma.View.ApplicationView;
import com.penbase.dma.XmlElement.XmlScriptAttribute;
import com.penbase.dma.XmlElement.XmlTag;

public class Function {
	private Document behaviorDocument = ApplicationView.behaviorDocument;
	private Context context;
	private HashMap<String, ArrayList<String>> varMap;
	private HashMap<String, Integer> funcMap;
	private ProgressDialog syncProgressDialog;
	
	public Function(Context c)
	{
		this.context = c;
		varMap = new HashMap<String, ArrayList<String>>();
		funcMap = new HashMap<String, Integer>();
		createMaps();		
	}
	
	/*
	 * This method need to be modified if we don't need global variables anymore in Dalyo studio
	 * Create 2 maps which contains variables and the positions of function
	 * */
	public void createMaps()
	{
		NodeList funcList = behaviorDocument.getElementsByTagName(XmlTag.TAG_SCRIPT_FUNCTION);
		int functionSize = funcList.getLength();
		for (int i=0; i<functionSize; i++)
		{
			Element funcElement = (Element) funcList.item(i);
			funcMap.put(funcElement.getAttribute(XmlTag.TAG_SCRIPT_NAME), i);			
			NodeList nodesList = funcElement.getChildNodes();
			int itemLen = nodesList.getLength();
			for (int j=0; j<itemLen; j++)
			{
				Element element = (Element) nodesList.item(j);
				if (element.getNodeName().equals(XmlTag.TAG_SCRIPT_SET))
				{
					if (element.hasChildNodes())
					{
						varMap.put(element.getAttribute(XmlTag.TAG_SCRIPT_NAME), 
								setVariable(element.getAttribute(XmlTag.TAG_SCRIPT_TYPE),
										element.getChildNodes().item(0).getNodeValue()));
					}
				}
			}
		}
	}	
	
	public void createFunction(String name, NodeList params)
	{
		NodeList funcList = behaviorDocument.getElementsByTagName(XmlTag.TAG_SCRIPT_FUNCTION);
		if (funcMap.containsKey(name))
		{
			Element funcElement = (Element) funcList.item(funcMap.get(name));
			if (funcElement.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(name))
			{
				NodeList nodesList = funcElement.getChildNodes();
				int itemLen = nodesList.getLength();
				Log.i("info", "itemlen "+nodesList.getLength());

				for (int j=0; j<itemLen; j++)
				{
					Element element = (Element) nodesList.item(j);
					Log.i("info", "element name "+element.getNodeName()+" paramater number "+
							element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER).getLength());
					if ((element.getNodeName().equals(XmlTag.TAG_SCRIPT_CALL)) && 
							(element.hasAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION)))
					{
						Log.i("info", "if loop");
						if ((element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_ALERT)) ||
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_ERROR)) &&
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE).equals(XmlScriptAttribute.NAMESPACE_RUNTIME)))
						{
							Log.i("info", "call showalertdialog");
							ShowAlertDialog(element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER), params);
						}
						else if ((element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_NAVIGATE)) &&
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE).equals(XmlScriptAttribute.NAMESPACE_FORM)))
						{
							ChangeContentView(element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER), params);
						}
						else if ((element.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE).equals(XmlScriptAttribute.NAMESPACE_USER)) &&
								(element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER).getLength() > 0))
						{
							createFunction(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION), 
									element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER));
						}
						else if ((element.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE).equals(XmlScriptAttribute.NAMESPACE_RUNTIME)) &&
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_SYNC)))
						{
							Log.i("info", "call sync function");
							Synchronize(element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER), params);
						}
					}
					/*
					 *	All the variables are global variables, if we need to set all the variable to be local, uncomment
					 *	the codes below to create variables locally
					 * */
					
					/*else if (element.getNodeName().equals(XmlTag.TAG_SCRIPT_SET))
					{
						if (element.hasChildNodes())
						{							
							varList.put(element.getAttribute(XmlTag.TAG_SCRIPT_NAME), 
									setVariable(element.getAttribute(XmlTag.TAG_SCRIPT_TYPE),
											element.getChildNodes().item(0).getNodeValue()));
						}
					}*/
				}
			}
		}
	}
	
	public Object getVariableValue(String name)
	{
		Object result = null;
		if (varMap.containsKey(name))
		{
			ArrayList<String> variable = varMap.get(name);
			Log.i("info", "value "+variable.get(1));
			result = variable.get(1);
		}		
		return result;
	}
	
	public ArrayList<String> setVariable(String type, String value)
	{
		ArrayList<String> array = new ArrayList<String>();
		array.add(type);
		array.add(value);
		return array;
	}
	
	public void ChangeContentView(NodeList items, NodeList params)
	{		
		int itemsLen = items.getLength();
		
		for (int i=0; i<itemsLen; i++)
		{
			Element element = (Element) items.item(i);
			if ((element.getNodeName().equals(XmlTag.TAG_SCRIPT_PARAMETER)) &&
					(element.hasAttribute(XmlTag.TAG_SCRIPT_NAME)) &&
					(element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(XmlScriptAttribute.PARAMETER_FORM)))
			{
				NodeList elements = element.getChildNodes();
				int eleLen = elements.getLength();
				for (int j=0; j<eleLen; j++)
				{
					Element elt = (Element) elements.item(j);
					if ((elt.getNodeName().equals(XmlTag.TAG_SCRIPT_ELEMENT)) &&
							elt.hasAttribute(XmlTag.TAG_SCRIPT_ELEMENT_ID))
					{
						//int id = Integer.valueOf(elt.getAttribute(XmlTag.TAG_SCRIPT_ELEMENT_ID));
						String id = elt.getAttribute(XmlTag.TAG_SCRIPT_ELEMENT_ID);
						ApplicationView.applicationView.setContentView(ApplicationView.getLayoutsMap().get(id));
					}
				}
			}
		}
	}
	
	public String getDialogValue(Element element, String name, NodeList newParams)
	{
		String result = "";
		String type = element.getAttribute(XmlTag.TAG_SCRIPT_TYPE);
		if (element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(name))
		{
			NodeList params = element.getChildNodes();
			int paramsLen = params.getLength();	
			for (int j=0; j<paramsLen; j++)
			{
				if (params.item(j).getNodeType() == Node.ELEMENT_NODE)
				{
					Element param = (Element)params.item(j);
					
					if (param.getNodeName().equals(XmlTag.TAG_SCRIPT_VAR))
					{
						if (getVariableValue(param.getAttribute(XmlTag.TAG_SCRIPT_NAME)) != null)
						{
							result = (String) getVariableValue(param.getAttribute(XmlTag.TAG_SCRIPT_NAME));						
						}
						else if (getParamValue(newParams, param.getAttribute(XmlTag.TAG_SCRIPT_NAME), type) != null)
						{
							result = (String) getParamValue(newParams, param.getAttribute(XmlTag.TAG_SCRIPT_NAME), type);
						}
						else if (param.getNodeName().equals(XmlTag.TAG_SCRIPT_KEYWOED))
						{
							//check keyword
							result = param.getChildNodes().item(0).getNodeValue();
						}
					}	
				}						
				else if (params.item(j).getNodeType() == Node.TEXT_NODE)
				{
					result = params.item(j).getNodeValue();
				}				
			}
		}

		return result;
	}
	
	public void ShowAlertDialog(NodeList items, NodeList newParams)
	{
		int itemsLen = items.getLength();
		String message = "";
		String title = "";
		
		for (int i=0; i<itemsLen; i++)
		{
			Element element = (Element)items.item(i);
			if ((element.getNodeName().equals(XmlTag.TAG_SCRIPT_PARAMETER)) &&
					(element.hasAttribute(XmlTag.TAG_SCRIPT_NAME)))
			{
				if (element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(XmlScriptAttribute.PARAMETER_TEXT))
				{
					message = getDialogValue(element, XmlScriptAttribute.PARAMETER_TEXT, newParams);
				}
				else if (element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(XmlScriptAttribute.PARAMETER_CAPTION))
				{
					title = getDialogValue(element, XmlScriptAttribute.PARAMETER_CAPTION, newParams);
				}
			}
		}
		
		if (!title.equals(XmlScriptAttribute.CONST_NULL))
		{
			new AlertDialog.Builder(context).setMessage(message).setTitle(title).show();
		}
		else
		{
			new AlertDialog.Builder(context).setMessage(message).show();			
		}
	}
	
	public Object getParamValue(NodeList params, String name, String type)
	{		
		Object result = null;
		int paramLen = params.getLength();
		
		for (int i=0; i<paramLen; i++)
		{
			Element element = (Element) params.item(i);			
			if ((element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(name)) &&
					(element.getAttribute(XmlTag.TAG_SCRIPT_TYPE).equals(type)))
			{
				if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE)
				{
					result = element.getChildNodes().item(0).getNodeValue();
				}
			}
		}
		return result;
	}
	
	public void Synchronize(NodeList items, NodeList globalParams)
	{
		int itemLen = items.getLength();
		for (int i=0; i<itemLen; i++)
		{
			Element element = (Element)items.item(i);
			if ((element.getNodeName().equals(XmlTag.TAG_SCRIPT_PARAMETER)) &&
					(element.hasAttribute(XmlTag.TAG_SCRIPT_NAME)))
			{
				if (element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(XmlScriptAttribute.PARAMETER_FACELESS))
				{
					NodeList params = element.getChildNodes();
					int paramsLen = params.getLength();
					for (int j=0; j<paramsLen; j++)
					{
						Element keyword = (Element) params.item(j);
						if (keyword.getNodeName().equals(XmlTag.TAG_SCRIPT_KEYWOED))
						{
							Log.i("info", "call sync function");
							Synchronize(keyword.getChildNodes().item(0).getNodeValue());
						}
					}
				}
			}
		}
	}
	
	public void Synchronize(String type)
	{
		if (!type.equals(XmlScriptAttribute.CONST_FALSE))
		{
			try 
			{
				byte[] result = ApplicationView.getCurrentClient().launchImport(
						ApplicationListView.getApplicationsInfo().get("AppId"),
						ApplicationListView.getApplicationsInfo().get("DbId"), 
						ApplicationListView.getApplicationsInfo().get("Username"),
						ApplicationListView.getApplicationsInfo().get("Userpassword"));
				ApplicationView.getDataBase().syncTable(result);
				ApplicationView.refreshComponent();
			}
			catch (IOException e) 
			{e.printStackTrace();}
			catch (InterruptedException e) 
			{e.printStackTrace();}
		}
	}
}