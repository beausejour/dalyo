package com.penbase.dma.Dalyo.Function;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.content.Context;
import android.util.Log;

import com.penbase.dma.Dalyo.Record;
import com.penbase.dma.Dalyo.Function.Custom.Clear;
import com.penbase.dma.Dalyo.Function.Custom.DisplayForm;
import com.penbase.dma.Dalyo.Function.Custom.GetSelectedRecord;
import com.penbase.dma.Dalyo.Function.Custom.Refresh;
import com.penbase.dma.Dalyo.Function.Custom.SetCurrentRecord;
import com.penbase.dma.Dalyo.Function.Custom.ShowMessage;
import com.penbase.dma.Dalyo.Function.Custom.Synchronize;
import com.penbase.dma.View.ApplicationView;
import com.penbase.dma.XmlElement.XmlScriptAttribute;
import com.penbase.dma.XmlElement.XmlTag;

public class Function {
	private Document behaviorDocument = ApplicationView.behaviorDocument;
	private Context context;
	private static HashMap<String, ArrayList<String>> varMap;
	private HashMap<String, Integer> funcMap;
	private Record record;
	
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

				for (int i=0; i<itemLen; i++)
				{
					Element element = (Element) nodesList.item(i);
					Log.i("info", "element name "+element.getNodeName()+" paramater number "+
							element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER).getLength());
					if ((element.getNodeName().equals(XmlTag.TAG_SCRIPT_CALL)) && 
							(element.hasAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION)))
					{
						Log.i("info", "if loop function "+element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION)+
								" namespace "+element.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE));
						if ((element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_ALERT)) ||
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_ERROR)) &&
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE).equals(XmlScriptAttribute.NAMESPACE_RUNTIME)))
						{
							Log.i("info", "call showalertdialog");
							new ShowMessage(context, element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER), params);
						}
						else if ((element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_NAVIGATE)) &&
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE).equals(XmlScriptAttribute.NAMESPACE_FORM)))
						{
							new DisplayForm(element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER), params);
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
							new Synchronize(element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER), params);
						}
						else if ((element.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE).equals(XmlScriptAttribute.NAMESPACE_COMPONENT_DV)) &&
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_REFRESH)))
						{
							Log.i("info", "call refresh function");
							new Refresh(XmlScriptAttribute.NAMESPACE_COMPONENT_DV, element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER));
						}
						else if ((element.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE).equals(XmlScriptAttribute.NAMESPACE_DB_TABLE)) &&
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_CLEAR)))
						{
							Log.i("info", "call clear function");
							new Clear(element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER));
						}
						else if ((element.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE).equals(XmlScriptAttribute.NAMESPACE_DB_TABLE)) &&
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_STARTNRECORD)))
						{
							Log.i("info", "call startnewrecord function");
							record = new Record();
						}
						else if ((element.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE).equals(XmlScriptAttribute.NAMESPACE_DB_TABLE)) &&
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_CANCELNRECORD)))
						{
							Log.i("info", "call cancelnewrecord function");
							
						}
						else if ((element.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE).equals(XmlScriptAttribute.NAMESPACE_FORM)) &&
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_SETCURRENTRECORD)))
						{
							Log.i("info", "call setcurrentrecord function");
							new SetCurrentRecord(element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER));
						}
						else if ((element.getAttribute(XmlTag.TAG_SCRIPT_CALL_NAMESPACE).equals(XmlScriptAttribute.NAMESPACE_COMPONENT_CB)) &&
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_GETSELECTEDRECORD)))
						{
							Log.i("info", "call getSelectedRecord function");
							new GetSelectedRecord(element.getElementsByTagName(XmlTag.TAG_SCRIPT_PARAMETER), null);
						}
					}
					else if (element.getNodeName().equals(XmlTag.TAG_SCRIPT_IF))
					{
						
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
	
	public static Object getVariableValue(String name)
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
	
	public static Object getParamValue(NodeList params, String name, String type)
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
}