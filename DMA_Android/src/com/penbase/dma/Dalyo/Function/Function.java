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
import com.penbase.dma.Dalyo.Transaction;
import com.penbase.dma.Dalyo.Function.ReturnType.*;
import com.penbase.dma.Dalyo.Function.VoidType.*;
import com.penbase.dma.View.ApplicationView;
import com.penbase.dma.XmlElement.ScriptTag;
import com.penbase.dma.XmlElement.ScriptAttribute;

public class Function {
	private Document behaviorDocument = ApplicationView.behaviorDocument;
	private static Context context;
	private static HashMap<String, ArrayList<Object>> varMap;
	private HashMap<String, Integer> funcMap;
	private Record record;
	private Transaction transaction;		
	static Object test = null;
	
	public Function(Context c)
	{
		context = c;
		varMap = new HashMap<String, ArrayList<Object>>();
		funcMap = new HashMap<String, Integer>();
		createMaps();
	}
	
	/*
	 * This method need to be modified if we don't need global variables anymore in Dalyo studio
	 * Create 2 maps which contains variables and the positions of function
	 * */
	public void createMaps()
	{
		NodeList funcList = behaviorDocument.getElementsByTagName(ScriptTag.FUNCTION);		
		int functionSize = funcList.getLength();
		Log.i("info", "createmaps "+functionSize);
		for (int i=0; i<functionSize; i++)
		{
			Element funcElement = (Element) funcList.item(i);
			funcMap.put(funcElement.getAttribute(ScriptTag.NAME), i);			
			NodeList nodesList = funcElement.getChildNodes();
			int itemLen = nodesList.getLength();
			for (int j=0; j<itemLen; j++)
			{
				Element element = (Element) nodesList.item(j);
				if (element.getNodeName().equals(ScriptTag.SET))
				{
					addVariable(element);					
				}
			}
		}
	}	
	
	public void createFunction(String name, NodeList params)
	{
		NodeList funcList = behaviorDocument.getElementsByTagName(ScriptTag.FUNCTION);
		if (funcMap.containsKey(name))
		{
			Element funcElement = (Element) funcList.item(funcMap.get(name));
			if (funcElement.getAttribute(ScriptTag.NAME).equals(name))
			{
				NodeList nodesList = funcElement.getChildNodes();
				int itemLen = nodesList.getLength();

				for (int i=0; i<itemLen; i++)
				{
					Element element = (Element) nodesList.item(i);
					Log.i("info", "element name "+element.getNodeName()+" paramater number "+
							element.getElementsByTagName(ScriptTag.PARAMETER).getLength());
					if ((element.getNodeName().equals(ScriptTag.CALL)) && 
							(element.hasAttribute(ScriptTag.FUNCTION)))						
					{
						if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETSELECTEDRECORD))
						{
							//returnTypeFunction(element, params)
						}
						else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_USER)) &&
								(element.getElementsByTagName(ScriptTag.PARAMETER).getLength() > 1))
						{
							createFunction(element.getAttribute(ScriptTag.FUNCTION), 
									element.getElementsByTagName(ScriptTag.PARAMETER));
						}
						else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_USER)) &&
								(element.getElementsByTagName(ScriptTag.PARAMETER).getLength() == 0))
						{
							createFunction(element.getAttribute(ScriptTag.FUNCTION), null);
						}
						else
						{
							Log.i("info", "if loop function "+element.getAttribute(ScriptTag.FUNCTION)+
									" namespace "+element.getAttribute(ScriptTag.NAMESPACE));
							voidTypeFunction(element, params);
						}
					}
					else if (element.getNodeName().equals(ScriptTag.SET))
					{
						addVariable(element);
					}
					else if (element.getNodeName().equals(ScriptTag.IF))
					{
						Log.i("info", "if called");
						NodeList children = element.getChildNodes();
						int childrenLen = children.getLength();
						boolean conditionCheck = false;
						for (int j=0; j<childrenLen; j++)
						{
							Element child = (Element)children.item(j);							
							if (child.getNodeName().equals(ScriptTag.CONDITIONS))
							{
								NodeList conditions = child.getChildNodes();
								int conditionsLen = conditions.getLength();
								for (int k=0; k<conditionsLen; k++)
								{
									Element condition = (Element)conditions.item(k);
									if (condition.getNodeName().equals(ScriptTag.CONDITION))
									{
										NodeList conditionchildren = condition.getChildNodes();
										int conditionchildrenLen = conditionchildren.getLength();
										Object left = null;
										String operator = null;
										Object right = null;
										for (int l=0; l<conditionchildrenLen; l++)
										{
											Element conditionChild = (Element)conditionchildren.item(l);											
											if (conditionChild.getNodeName().equals(ScriptTag.LEFT))
											{
												NodeList leftchildren = conditionChild.getChildNodes();
												int leftchildrenLen = leftchildren.getLength();
												for (int m=0; m<leftchildrenLen; m++)
												{
													Element leftChild = (Element) leftchildren.item(m);
													if ((leftChild.getNodeName().equals(ScriptTag.CALL)) &&
															leftChild.hasAttribute(ScriptTag.FUNCTION))
													{
														left = returnTypeFunction(leftChild);
														Log.i("info", "left "+left);
													}
												}
											}
											else if (conditionChild.getNodeName().equals(ScriptTag.OPERATOR))
											{
												operator = conditionChild.getChildNodes().item(0).getNodeValue();
											}
											else if (conditionChild.getNodeName().equals(ScriptTag.RIGHT))
											{
												NodeList rightchildren = conditionChild.getChildNodes();
												int rightchildrenLen = rightchildren.getLength();
												for (int m=0; m<rightchildrenLen; m++)
												{
													Element rightChild = (Element) rightchildren.item(m);
													if (rightChild.getNodeName().equals(ScriptTag.KEYWOED))
													{
														right = getKeyWord(rightChild);
													}
												}
											}
										}
										conditionCheck = check(left, operator, right);
										Log.i("info", "conditionCheck "+conditionCheck);
									}
								}
							}
							else if (child.getNodeName().equals(ScriptTag.THEN))
							{
								if (conditionCheck)
								{
									Log.i("info", "pass to then");
									NodeList thenchildren = child.getChildNodes();
									int thenchildrenLen = thenchildren.getLength();
									Log.i("info", "thenchildrenLen "+thenchildrenLen);
									for (int k=0; k<thenchildrenLen; k++)
									{
										Element thenChild = (Element) thenchildren.item(k);
										if ((thenChild.getNodeName().equals(ScriptTag.CALL)) &&
												thenChild.hasAttribute(ScriptTag.FUNCTION))
										{
											Log.i("info", "call function in then");
											voidTypeFunction(thenChild, null);
										}
									} 
								}
							}
							else if (child.getNodeName().equals(ScriptTag.ELSE))
							{
								if (!conditionCheck)
								{
									Log.i("info", "pass to else");
									NodeList elsechildren = child.getChildNodes();
									int elsechildrenLen = elsechildren.getLength();
									for (int k=0; k<elsechildrenLen; k++)
									{
										Element elseChild = (Element) elsechildren.item(k);
										if ((elseChild.getNodeName().equals(ScriptTag.CALL)) &&
												elseChild.hasAttribute(ScriptTag.FUNCTION))
										{
											voidTypeFunction(elseChild, null);
										}
										else if (elseChild.getNodeName().equals(ScriptTag.SET))
										{
											Log.i("info", "call setvarable "+elseChild.getChildNodes().getLength());
											addVariable(elseChild);
											Log.i("info", "add variable "+elseChild.getAttribute(ScriptTag.NAME)+" type "+
													elseChild.getAttribute(ScriptTag.TYPE));
										}
									}
								}
							}
						}
					}
					/*
					 *	All the variables are global variables, if we need to set all the variable to be local, uncomment
					 *	the codes below to create variables locally
					 * */
					
					/*else if (element.getNodeName().equals(ScriptTag.SET))
					{
						if (element.hasChildNodes())
						{							
							varList.put(element.getAttribute(ScriptTag.NAME), 
									setVariable(element.getAttribute(ScriptTag.TYPE),
											element.getChildNodes().item(0).getNodeValue()));
						}
					}*/
				}
			}
		}
	}
	
	public static boolean check(Object left, String operator, Object right)
	{
		Log.i("info", "left "+left+" operator "+operator+" right "+right);
		boolean result = false;		
		switch (Integer.valueOf(operator))
		{
			case ScriptAttribute.EQUALS:				
				result = (left == right);
				break;
		}
		return result;
	}
	
	/*public static Object getConstValue(Object object)
	{
		Object result = null;
		if (object.equals("NULL"))
		{
			result = null;
		}
		return result;
	}*/
	
	public static Object returnTypeFunction(Element element)
	{
		Object result = null;
		if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_COMPONENT_CB)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETSELECTEDRECORD)))
		{
			Log.i("info", "call getSelectedRecord function");
			result = new GetSelectedRecord(element.getElementsByTagName(ScriptTag.PARAMETER), null).getValue();
			Log.i("info", "result in returntype "+result);
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETFIELDVALUE)))
		{
			Log.i("info", "call getfieldvalue function");
			result = new GetFieldValue(element.getElementsByTagName(ScriptTag.PARAMETER)).getValue();
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DATE)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_NOW)))
		{
			Log.i("info", "call getcurrentdate function");
			result = new GetCurrentDate(element.getElementsByTagName(ScriptTag.PARAMETER)).getDate();
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_NEWRECORD)))
		{
			Log.i("info", "call newrecord function");
			result = new CreateNewRecord(element.getElementsByTagName(ScriptTag.PARAMETER)).getRecord();
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_RUNTIME)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CONFIRM)))
		{
			Log.i("info", "call confirmdialog function");			
			ConfirmDialog confirmDialog = new ConfirmDialog(element.getElementsByTagName(ScriptTag.PARAMETER), context);
			confirmDialog.start();
			try 
			{
				confirmDialog.join();
			}
			catch (InterruptedException e) 
			{e.printStackTrace();}			
			result = confirmDialog.getValue();
			confirmDialog.stop();
		}
		return result;
	}
	
	public void voidTypeFunction(Element element, NodeList params)
	{
		if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ALERT)) ||
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ERROR)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_RUNTIME)))
		{
			Log.i("info", "call showalertdialog");
			new ShowMessage(context, element.getElementsByTagName(ScriptTag.PARAMETER), params);
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_NAVIGATE)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.FORM)))
		{
			new DisplayForm(element.getElementsByTagName(ScriptTag.PARAMETER), params);
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_RUNTIME)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SYNC)))
		{
			Log.i("info", "call sync function");
			new Synchronize(element.getElementsByTagName(ScriptTag.PARAMETER), params);
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_REFRESH)) &&
				((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_COMPONENT_DV)) ||
						(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_COMPONENT_CB))))			
		{
			Log.i("info", "call refresh function");
			new Refresh(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CLEAR)))
		{
			Log.i("info", "call clear function");
			new Clear(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_STARTNRECORD)))
		{
			Log.i("info", "call startnewrecord function");
			
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CANCELNRECORD)))
		{
			Log.i("info", "call cancelnewrecord function");
			//transaction.cancelTransaction();
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.FORM)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SETCURRENTRECORD)))
		{
			Log.i("info", "call setcurrentrecord function");
			new SetCurrentRecord(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_STARTTRANSACTION)))
		{
			Log.i("info", "call start transaction function");
			//START TRANSACTION
			transaction = new Transaction();
			transaction.startTransaction();
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CANCELTRANSACTION)))
		{
			Log.i("info", "call cancel transaction function");
			//CANCEL TRANSACTION
			//transaction.cancelTransaction();
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.LIST)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ADD)))
		{
			Log.i("info", "call add value function");
			new ListAddValue(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.FILTER)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ADDCRITERIA)))
		{
			Log.i("info", "call add criteria function");
			new AddCriteria(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.COMPONENT)) &&
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SETVALUE)))
		{
			Log.i("info", "call set value function");
			new SetValue(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
	}
	
	public static Object getVariableValue(String name)
	{
		Object result = null;
		if (varMap.containsKey(name))
		{
			ArrayList<Object> variable = varMap.get(name);
			Log.i("info", "value "+variable.get(1));
			result = variable.get(1);
		}		
		return result;
	}
	
	public static ArrayList<Object> setVariable(String type, Object value)
	{
		ArrayList<Object> array = new ArrayList<Object>();
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
			if ((element.getAttribute(ScriptTag.NAME).equals(name)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(type)))
			{
				if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE)
				{
					result = element.getChildNodes().item(0).getNodeValue();
				}
			}
		}
		return result;
	}
	
	public static HashMap<String, ArrayList<Object>> getVariablesMap()
	{
		return varMap;
	}
	
	public static void addVariable(Element element)
	{
		/*
		 * Each varable has a list of value, the two first values are its type and its default value,
		 * for the list, its added values are start with the third position
		 * */
		if (varMap.containsKey(element.getAttribute(ScriptTag.NAME)))
		{
			Log.i("info", "remove "+element.getAttribute(ScriptTag.NAME));
			varMap.remove(element.getAttribute(ScriptTag.NAME));
		}		
		if (!element.hasChildNodes())
		{
			Log.i("info", "variable hasn't childnodes");
			varMap.put(element.getAttribute(ScriptTag.NAME), 
					setVariable(element.getAttribute(ScriptTag.TYPE), null));							
		}
		else if ((element.getChildNodes().getLength() == 1) && (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE))
		{
			varMap.put(element.getAttribute(ScriptTag.NAME), 
					setVariable(element.getAttribute(ScriptTag.TYPE),
							element.getChildNodes().item(0).getNodeValue()));
		}
		else
		{
			Log.i("info", "variable has call function");
			Object value = null;
			NodeList children = element.getChildNodes();
			int childrenLen = children.getLength();
			for (int i=0; i<childrenLen; i++)
			{
				Element child = (Element) children.item(i);
				if ((child.getNodeName().equals(ScriptTag.CALL)) && child.hasAttribute(ScriptTag.FUNCTION))						
				{
					if (child.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_NEWRECORD))
					{
						value = returnTypeFunction(child);
					}				
				}
			}
			Log.i("info", "prepare to add in the var list");
			varMap.put(element.getAttribute(ScriptTag.NAME), 
					setVariable(element.getAttribute(ScriptTag.TYPE), value));								
		}
	}

	public static Object getKeyWord(Element element)
	{
		Object result = null;
		if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE)
		{
			if (element.getChildNodes().item(0).getNodeValue().equals(ScriptAttribute.CONST_NULL))
			{
				result = null;
			}
			else if (element.getChildNodes().item(0).getNodeValue().equals(ScriptAttribute.CONST_TRUE))
			{
				result = true;
			}
			else if (element.getChildNodes().item(0).getNodeValue().equals(ScriptAttribute.CONST_FALSE))
			{
				result = false;
			}
		}		
		return result;
	}
	
	public static Object getOperator(Object operator)
	{
		Object result = null;		
		switch (Integer.valueOf((String)operator))
		{
			case ScriptAttribute.EQUALS:				
				result = "=";
				break;
			case ScriptAttribute.NOTEQUALS:
				result = "!=";
				break;
		}		
		return result;
	}
}