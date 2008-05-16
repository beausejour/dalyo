package com.penbase.dma.Dalyo.Function;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import com.penbase.dma.view.ApplicationView;
import com.penbase.dma.xml.XmlScriptAttribute;
import com.penbase.dma.xml.XmlTag;

public class Function {
	private Document behaviorDocument = ApplicationView.behaviorDocument;
	private Context context;
	private HashMap<String, ArrayList<String>> varList;
	
	public Function(Context c)
	{
		this.context = c;
		varList = new HashMap<String, ArrayList<String>>();
		createVaraible();
	}
	
	/*
	 * This method need to be deleted if we don't need global variables anymore in Dalyo studio
	 * */
	public void createVaraible()
	{
		NodeList funcList = behaviorDocument.getElementsByTagName(XmlTag.TAG_SCRIPT_FUNCTION);
		int functionSize = funcList.getLength();
		for (int i=0; i<functionSize; i++)
		{
			Element funcElement = (Element) funcList.item(i);
			NodeList nodesList = funcElement.getChildNodes();
			int itemLen = nodesList.getLength();
			for (int j=0; j<itemLen; j++)
			{
				Element element = (Element) nodesList.item(j);
				if (element.getNodeName().equals(XmlTag.TAG_SCRIPT_SET))
				{
					if (element.hasChildNodes())
					{
						varList.put(element.getAttribute(XmlTag.TAG_SCRIPT_NAME), 
								setVariable(element.getAttribute(XmlTag.TAG_SCRIPT_TYPE),
										element.getChildNodes().item(0).getNodeValue()));
					}
				}
			}
		}
	}
	
	public void createFunction(String name)
	{
		NodeList funcList = behaviorDocument.getElementsByTagName(XmlTag.TAG_SCRIPT_FUNCTION);
		int functionSize = funcList.getLength();
		for (int i=0; i<functionSize; i++)
		{
			Element funcElement = (Element) funcList.item(i);
			if (funcElement.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(name))
			{
				NodeList nodesList = funcElement.getChildNodes();
				int itemLen = nodesList.getLength();
				Log.i("info", "itemlen "+nodesList.getLength());

				for (int j=0; j<itemLen; j++)
				{
					Element element = (Element) nodesList.item(j);
					Log.i("info", "element name "+element.getNodeName());
					if ((element.getNodeName().equals(XmlTag.TAG_SCRIPT_CALL)) && 
							(element.hasAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION)))
					{
						if ((element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_ALERT)) ||
								(element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_ERROR)))
						{
							Log.i("info", "call showalertdialog");
							showAlertDialog(element.getChildNodes());
						}
						else if (element.getAttribute(XmlTag.TAG_SCRIPT_CALL_FUNCTION).equals(XmlScriptAttribute.FUNCTION_NAVIGATE))
						{
							changeContentView(element.getChildNodes());
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
		Log.i("info", "get valeu of "+name+" "+varList.size());
		
		if (varList.containsKey(name))
		{
			ArrayList<String> variable = varList.get(name);
			Log.i("info", "value "+variable.get(1));
			return variable.get(1);
		}
		else
		{
			return null;
		}
	}
	
	public ArrayList<String> setVariable(String type, String value)
	{
		ArrayList<String> array = new ArrayList<String>();
		array.add(type);
		array.add(value);
		return array;
	}
	
	public void changeContentView(NodeList items)
	{
		ApplicationView.applicationView.setContentView(ApplicationView.layoutList.get(1));
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
						int id = Integer.valueOf(elt.getAttribute(XmlTag.TAG_SCRIPT_ELEMENT_ID));
						ApplicationView.applicationView.setContentView(ApplicationView.layoutList.get(id-1));
					}					
				}
			}
		}
	}
	
	public void showAlertDialog(NodeList items)
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
					NodeList params = element.getChildNodes();
					int paramsLen = params.getLength();	
					for (int j=0; j<paramsLen; j++)
					{
						if (params.item(j).getNodeType() == Node.ELEMENT_NODE)
						{
							Element param = (Element)params.item(j);
							
							if (param.getNodeName().equals(XmlTag.TAG_SCRIPT_VAR))
							{							
								message = (String) getVariableValue(param.getAttribute(XmlTag.TAG_SCRIPT_NAME));
							}	
						}						
						else if (params.item(j).getNodeType() == Node.TEXT_NODE)
						{
							message = params.item(j).getNodeValue();
						}											
					}
				}
				else if (element.getAttribute(XmlTag.TAG_SCRIPT_NAME).equals(XmlScriptAttribute.PARAMETER_CAPTION))
				{
					NodeList params = element.getChildNodes();
					int paramsLen = params.getLength();
					
					for (int j=0; j<paramsLen; j++)
					{						
						Element param = (Element)params.item(j);
						if (param.getNodeName().equals(XmlTag.TAG_SCRIPT_VAR))
						{							
							title = (String) getVariableValue(param.getAttribute(XmlTag.TAG_SCRIPT_NAME));
							Log.i("info", "caption "+title+" "+param.getAttribute(XmlTag.TAG_SCRIPT_NAME));
						}
						else if (param.getNodeName().equals(XmlTag.TAG_SCRIPT_KEYWOED))
						{
							//check keyword
							title = param.getChildNodes().item(0).getNodeValue();
						}											
					}					
				}
			}
		}
		
		if (!title.equals("NULL"))
		{
			new AlertDialog.Builder(context).setMessage(message).setTitle(title).show();
		}
		else
		{
			new AlertDialog.Builder(context).setMessage(message).show();			
		}
	}
}
