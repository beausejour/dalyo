package com.penbase.dma.Dalyo.Function;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.content.Context;
import android.util.Log;

import com.penbase.dma.Constant.GpsStatus;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import com.penbase.dma.Dalyo.Function.Namespace.*;

public class Function {
	private static Document behaviorDocument;
	private static Context context;
	private static HashMap<String, ArrayList<Object>> varMap;
	//private static HashMap<String, Integer> funcMap;
	private static HashMap<String, ArrayList<String>> funcMap;
	private static boolean first = true;
	
	public Function(Context c, Document document){
		context = c;
		varMap = new HashMap<String, ArrayList<Object>>();
	//	funcMap = new HashMap<String, Integer>();
		funcMap = new HashMap<String, ArrayList<String>>();
		behaviorDocument = document;
		createMaps();
	}
	
	/*
	 * This method need to be modified if we don't need global variables anymore in Dalyo studio
	 * Create 2 maps which contains variables and the positions of function
	 * */
	private void createMaps(){
		NodeList funcList = behaviorDocument.getElementsByTagName(ScriptTag.FUNCTION);
		int functionSize = funcList.getLength();
		for (int i=0; i<functionSize; i++){
			Element funcElement = (Element) funcList.item(i);
			ArrayList<String> funcParams = new ArrayList<String>();
			funcParams.add(String.valueOf(i));
			funcParams.add(funcElement.getAttribute(ScriptTag.OUTPUT));
			//funcMap.put(funcElement.getAttribute(ScriptTag.NAME), i);
			funcMap.put(funcElement.getAttribute(ScriptTag.NAME), funcParams);
			NodeList nodesList = funcElement.getChildNodes();
			int itemLen = nodesList.getLength();
			for (int j=0; j<itemLen; j++){
				Element element = (Element) nodesList.item(j);
				if (element.getNodeName().equals(ScriptTag.SET)){
					setVariable(element);
				}
			}
		}
		first = false;
	}	
	
	private static boolean checkConditions(boolean[] boolList){
		boolean result = false;
		int size = boolList.length;
		int i = 0;
		int j = 0;
		int checked = 0;
		while (i < size){
			if (!boolList[i]){
				result = boolList[i];
				i = size;
			}
			else{
				if (boolList[i] == boolList[j]){
					checked++;
					i++;
				}
				else{
					i = size;
				}
			}
		}
		if (checked == size){
			result = true;
		}
		return result;
	}
	
	private static boolean checkCondition(Object left, String operator, Object right){
		boolean result = false;
		switch (Integer.valueOf(operator)){
			case ScriptAttribute.EQUALS:
				if ((left instanceof Integer) || (right instanceof Integer)){
					Log.i("info", "integer");
					result = (Integer.valueOf(String.valueOf(left)) == (Integer.valueOf(String.valueOf(right))));
				}
				else if ((left == null) || (right == null)){
					result = (left == right);
				}
				else{
					Log.i("info", "string");
					result = (left.equals(right));
				}
				break;
			case ScriptAttribute.NOTEQUALS:
				result = (left != right);
				break;
			case ScriptAttribute.GREATERTHAN:
				result = (Integer.valueOf(String.valueOf(left)) > Integer.valueOf(String.valueOf(right)));
				break;
		}
		Log.i("info", "left "+left+" operator "+operator+" right "+right+" result "+result);
		return result;
	}
	
	private static ArrayList<Object> addVariable(String type, Object value){
		ArrayList<Object> array = new ArrayList<Object>();
		array.add(type);
		array.add(value);
		return array;
	}
	
	private static void voidTypeFunction(Element element, NodeList params){
		if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_USER)) &&
				(element.getElementsByTagName(ScriptTag.PARAMETER).getLength() == 0)){
			Log.i("info", "call user defined function"+element.getAttribute(ScriptTag.FUNCTION));
			createFunction(element.getAttribute(ScriptTag.FUNCTION), null);
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ALERT)) ||
				(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ERROR)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_RUNTIME))){
			Log.i("info", "call showalertdialog");
			NS_Runtime.Alert(context, element.getElementsByTagName(ScriptTag.PARAMETER), params);
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SYNC)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_RUNTIME))){
			Log.i("info", "call sync function");
			NS_Runtime.Synchronize(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_REFRESH)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_COMPONENT_DV))){
			Log.i("info", "call dataview refresh function");
			NS_ComponentDataview.Refresh(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_REFRESH)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_COMPONENT_CB))){
			Log.i("info", "call combobox refresh function");
			NS_ComponentCombobox.Refresh(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CLEAR)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE))){
			Log.i("info", "call clear function");
			NS_DatabaseTable.Clear(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_STARTNEWRECORD)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE))){
			Log.i("info", "call startnewrecord function");
			
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CANCELNRECORD)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE))){
			Log.i("info", "call cancelnewrecord function");
			
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_EDITRECORD)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE))){
			Log.i("info", "call edit record function");
			NS_DatabaseTable.EditRecord(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_DELETERECORD)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE))){
			Log.i("info", "call delete record function");
			NS_DatabaseTable.DeleteRecord(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_NEWRECORD)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE))){
			Log.i("info", "call newrecord function");
			if (!first){
				Log.i("info", "not the first time");
				NS_DatabaseTable.CreateNewRecord(element.getElementsByTagName(ScriptTag.PARAMETER));
			}
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_NAVIGATE)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.FORM))){
			NS_Form.Navigate(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SETCURRENTRECORD)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.FORM))){
			Log.i("info", "call setcurrentrecord function");
			NS_Form.SetCurrentRecord(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_STARTTRANSACTION)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB))){
			Log.i("info", "call start transaction function");
			DatabaseAdapter.startTransaction();
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CANCELTRANSACTION)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB))){
			Log.i("info", "call cancel transaction function");
			DatabaseAdapter.cancelTransaction();
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_VALIDATETRANSACTION)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB))){
			Log.i("info", "call VALIDATE transaction function");
			DatabaseAdapter.validateTransaction();
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ADD)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.LIST))){
			Log.i("info", "call add value function");
			NS_List.ListAddValue(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ADDCRITERIA)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.FILTER))){
			Log.i("info", "call add criteria function");
			NS_Filter.AddCriteria(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SETVALUE)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.COMPONENT))){
			Log.i("info", "call set value function");
			NS_Component.SetValue(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SETTEXT)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.COMPONENT))){
			Log.i("info", "call SETTEXT function");
			NS_Component.SetText(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SETENABLED)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.COMPONENT))){
			Log.i("info", "call setenable function");
			NS_Component.SetEnabled(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CANCEL)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_TIMER))){
			Log.i("info", "call cancel timer fucntion");
			NS_Timer.Cancel(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_INIT)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_GPS))){
			Log.i("info", "call gps init fucntion");
			NS_Gps.Init(context);
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_STOP)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_GPS))){
			Log.i("info", "call gps stop fucntion");
			NS_Gps.Stop();
		}
	}
	
	private static Object ifCondition(Element element){
		Object result = "";
		Log.i("info", "if called");
		int elementLen = element.getChildNodes().getLength();
		boolean conditionCheck = false;
		for (int i=0; i<elementLen; i++){
			Element child = (Element) element.getChildNodes().item(i);
			if (child.getNodeName().equals(ScriptTag.CONDITIONS)){
				NodeList conditions = child.getChildNodes();
				int conditionsLen = conditions.getLength();
				boolean[] checkList = new boolean[conditionsLen];
				for (int k=0; k<conditionsLen; k++){
					Element condition = (Element)conditions.item(k);
					if (condition.getNodeName().equals(ScriptTag.CONDITION)){
						NodeList conditionchildren = condition.getChildNodes();
						int conditionchildrenLen = conditionchildren.getLength();
						Object left = null;
						String operator = null;
						Object right = null;
						for (int l=0; l<conditionchildrenLen; l++){
							Element conditionChild = (Element)conditionchildren.item(l);
							if (conditionChild.getNodeName().equals(ScriptTag.LEFT)){
								NodeList leftchildren = conditionChild.getChildNodes();
								int leftchildrenLen = leftchildren.getLength();
								for (int m=0; m<leftchildrenLen; m++){
									Element leftChild = (Element) leftchildren.item(m);
									if (leftChild.getNodeName().equals(ScriptTag.CALL)){
										left = returnTypeFunction(leftChild);
										Log.i("info", "left return value "+left);
									}
									else if ((leftChild.getNodeName().equals(ScriptTag.VAR)) &&
											leftChild.hasAttribute(ScriptTag.NAME)){
										left = getVariableValue(leftChild.getAttribute(ScriptTag.NAME)); 
										Log.i("info", "left variable "+left);
									}
								}
							}
							else if (conditionChild.getNodeName().equals(ScriptTag.OPERATOR)){
								operator = conditionChild.getChildNodes().item(0).getNodeValue();
								Log.i("info", "operator "+operator);
							}
							else if (conditionChild.getNodeName().equals(ScriptTag.RIGHT)){
								NodeList rightchildren = conditionChild.getChildNodes();
								int rightchildrenLen = rightchildren.getLength();
								Log.i("info", "right children length "+rightchildrenLen);
								for (int m=0; m<rightchildrenLen; m++){
									if (rightchildren.item(m).getNodeType() == Node.ELEMENT_NODE){
										Element rightChild = (Element) rightchildren.item(m);
										Log.i("info", "rightchild node type "+rightChild.getNodeType()+" it has child "+
												rightChild.getChildNodes().getLength());
										if (rightChild.getNodeName().equals(ScriptTag.KEYWORD)){
											right = getKeyWord(rightChild);
											Log.i("info", "right has keyword "+right);
										}
										else if (rightChild.getNodeName().equals(ScriptTag.VAR)){
											right = getVariableValue(rightChild.getAttribute(ScriptTag.NAME));
											Log.i("info", "right return value "+right);
										}
									}
									else if (rightchildren.item(m).getNodeType() == Node.TEXT_NODE){
										right = rightchildren.item(m).getNodeValue();
										Log.i("info", "right is a value "+right);
									}
								}
							}
						}
						checkList[k] = checkCondition(left, operator, right); 
					}
				}
				conditionCheck = checkConditions(checkList); 
			}
			else if (child.getNodeName().equals(ScriptTag.THEN)){
				if (conditionCheck){
					Log.i("info", "pass to then");
					NodeList thenchildren = child.getChildNodes();
					int thenchildrenLen = thenchildren.getLength();
					for (int k=0; k<thenchildrenLen; k++){
						Element thenChild = (Element) thenchildren.item(k);
						if (thenChild.getNodeName().equals(ScriptTag.CALL)){
							Log.i("info", "call function in then "+thenChild.getAttribute(ScriptTag.FUNCTION));
							voidTypeFunction(thenChild, null);
						}
						else if (thenChild.getNodeName().equals(ScriptTag.SET)){
							setVariable(thenChild);
						}
						else if (thenChild.getNodeName().equals(ScriptTag.IF)){
							Log.i("info", "there is if condition in then");
							ifCondition(thenChild);
						}
						else if (thenChild.getNodeName().equals(ScriptTag.RETURN)){
							Log.i("info", "return value");
							result = getReturnValue(thenChild);
						}
					} 
				}
			}
			else if (child.getNodeName().equals(ScriptTag.ELSE)){
				if (!conditionCheck){
					Log.i("info", "pass to else");
					NodeList elsechildren = child.getChildNodes();
					int elsechildrenLen = elsechildren.getLength();
					for (int k=0; k<elsechildrenLen; k++){
						Element elseChild = (Element) elsechildren.item(k);
						if ((elseChild.getNodeName().equals(ScriptTag.CALL)) &&
								(elseChild.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_NEWRECORD))){
							returnTypeFunction(elseChild);
						}
						else if (elseChild.getNodeName().equals(ScriptTag.CALL)){
							voidTypeFunction(elseChild, null);
						}
						else if (elseChild.getNodeName().equals(ScriptTag.SET)){
							Log.i("info", "call setvarable "+elseChild.getChildNodes().getLength());
							setVariable(elseChild);
							Log.i("info", "add variable "+elseChild.getAttribute(ScriptTag.NAME)+" type "+
									elseChild.getAttribute(ScriptTag.TYPE));
						}
						else if (elseChild.getNodeName().equals(ScriptTag.IF)){
							ifCondition(elseChild);
						}
						else if (elseChild.getNodeName().equals(ScriptTag.RETURN)){
							Log.i("info", "return value");
							result = getReturnValue(elseChild);
						}
					}
				}
			}
		}
		return result;
	}
	
	public static Object createCalculateFunction(String name, HashMap<Object, Object> record){
		Object result = null;
		NodeList funcList = behaviorDocument.getElementsByTagName(ScriptTag.FUNCTION);
		if (funcMap.containsKey(name)){
			Element funcElement = (Element) funcList.item(Integer.valueOf(funcMap.get(name).get(0)));
			if (funcElement.getAttribute(ScriptTag.NAME).equals(name)){
				NodeList nodesList = funcElement.getChildNodes();
				int itemLen = nodesList.getLength();
				for (int i=0; i<itemLen; i++){
					Element element = (Element) nodesList.item(i);
					if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
							element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.RECORD)){
						varMap.put(element.getAttribute(ScriptTag.NAME), 
								addVariable(ScriptAttribute.RECORD, record));
					}
					else if (element.getNodeName().equals(ScriptTag.IF)){
						result = String.valueOf(ifCondition(element));
					}
				}
			}
		}
		if (result == null){
			if (funcMap.get(name).get(1).equals(ScriptAttribute.STRING)){
				result = "";
			}
		}
		return result;
	}
	
	public static void createFunction(String name, NodeList params){
		NodeList funcList = behaviorDocument.getElementsByTagName(ScriptTag.FUNCTION);
		if (funcMap.containsKey(name)){
			Element funcElement = (Element) funcList.item(Integer.valueOf(funcMap.get(name).get(0)));
			if (funcElement.getAttribute(ScriptTag.NAME).equals(name)){
				NodeList nodesList = funcElement.getChildNodes();
				int itemLen = nodesList.getLength();
				for (int i=0; i<itemLen; i++){
					Element element = (Element) nodesList.item(i);
					if (element.getNodeName().equals(ScriptTag.CALL)){
						if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_USER)) &&
								(element.getElementsByTagName(ScriptTag.PARAMETER).getLength() > 1)){
							Log.i("info", "first condition");
							createFunction(element.getAttribute(ScriptTag.FUNCTION), 
									element.getElementsByTagName(ScriptTag.PARAMETER));
						}
						else if ((element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_USER)) &&
								(element.getElementsByTagName(ScriptTag.PARAMETER).getLength() == 0)){
							Log.i("info", "second condition");
							createFunction(element.getAttribute(ScriptTag.FUNCTION), null);
						}
						else{
							Log.i("info", "third condition");
							voidTypeFunction(element, params);
						}
					}
					else if (element.getNodeName().equals(ScriptTag.SET)){
						setVariable(element);
					}
					else if (element.getNodeName().equals(ScriptTag.IF)){
						ifCondition(element);
					}
					/*
					 *	All the variables are global variables, if we need to set all the variable to be local,
					 *	uncomment these codes to create variables locally
					 * */
					/*else if (element.getNodeName().equals(ScriptTag.SET)){
						if (element.hasChildNodes()){
							varList.put(element.getAttribute(ScriptTag.NAME), 
									setVariable(element.getAttribute(ScriptTag.TYPE),
											element.getChildNodes().item(0).getNodeValue()));
						}
					}*/
				}
			}
		}
	}
		
	public static Object returnTypeFunction(Element element){
		Object result = null;
		if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETSELECTEDRECORD)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_COMPONENT_DV))){
			Log.i("info", "call dataview getSelectedRecord function");
			result = NS_ComponentDataview.GetSelectedRecord(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETSELECTEDRECORD)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_COMPONENT_CB))){
			Log.i("info", "call combobox getSelectedRecord function");
			result = NS_ComponentCombobox.GetSelectedRecord(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETFIELDVALUE)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE))){
			Log.i("info", "call getfieldvalue function");
			result = NS_DatabaseTable.GetFieldValue(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETFILTEREDRECORDS)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE))){
			Log.i("info", "call getfilteredrecords function");
			result = NS_DatabaseTable.GetFilteredRecords(element.getElementsByTagName(ScriptTag.PARAMETER));
			Log.i("info", "getfilteredrecords "+result);
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETRECORDS)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE))){
			Log.i("info", "call getrecords function");
			result = NS_DatabaseTable.GetRecords(element.getElementsByTagName(ScriptTag.PARAMETER));
			Log.i("info", "getrecords "+result);
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_NEWRECORD)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE))){
			Log.i("info", "call newrecord function");
			result = NS_DatabaseTable.CreateNewRecord(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_NOW)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DATE))){
			Log.i("info", "call getcurrentdate function");
			result = NS_Date.GetCurrentDate();
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CONFIRM)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_RUNTIME))){
			Log.i("info", "call confirmdialog function");
			ConfirmDialog confirmDialog = new ConfirmDialog(element.getElementsByTagName(ScriptTag.PARAMETER), context);
			confirmDialog.start();
			try{
				confirmDialog.join();
			}
			catch (InterruptedException e){
				e.printStackTrace();
			}
			result = confirmDialog.getValue();
			//Cancel the thread, because the stop() method is deprecated
			confirmDialog = null;
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SYNC)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_RUNTIME))){
			Log.i("info", "call sync function");
			if (!first){
				Log.i("info", "not the first time");
				result = NS_Runtime.Synchronize(element.getElementsByTagName(ScriptTag.PARAMETER));
			}
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_TONUMERIC)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.OBJECT))){
			Log.i("info", "call tonumeric fucntion");
			result = NS_Object.ToNumeric(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_TOSTRING)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.OBJECT))){
			Log.i("info", "call TOSTRING fucntion");
			result = NS_Object.ToString(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_TOINT)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.OBJECT))){
			Log.i("info", "call toint fucntion");
			result = NS_Object.ToInt(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_TORECORD)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.OBJECT))){
			Log.i("info", "call torecord fucntion");
			result = NS_Object.ToRecord(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETSIZE)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.LIST))){
			Log.i("info", "call list size fucntion");
			result = NS_List.GetSize(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GET)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.LIST))){
			Log.i("info", "call get list item fucntion");
			result = NS_List.GetListItem(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETVALUE)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.COMPONENT))){
			Log.i("info", "call getvalue fucntion");
			result = NS_Component.GetValue(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ADD)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_MATH))){
			Log.i("info", "call sum function");
			result = NS_Math.Sum(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SUB)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_MATH))){
			Log.i("info", "call sub function");
			result = NS_Math.Subtract(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_START)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_TIMER))){
			Log.i("info", "call start timer fucntion");
			if (!first){
				Log.i("info", "not the first time");
				result = NS_Timer.Start(element.getElementsByTagName(ScriptTag.PARAMETER));
			}
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETLOCATION)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_GPS))){
			Log.i("info", "call getlocation fucntion");
			result = NS_Gps.getLocation();
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETLATITUDE)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_GPS))){
			Log.i("info", "call getlatitude fucntion");
			result = NS_Gps.GetLatitude(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETLONGITUDE)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_GPS))){
			Log.i("info", "call get logitude fucntion");
			result = NS_Gps.GetLogitude(element.getElementsByTagName(ScriptTag.PARAMETER));
		}
		else if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETSTATUS)) &&
				(element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_GPS))){
			Log.i("info", "call get status fucntion");
			result = NS_Gps.GetStatus();
		}
		return result;
	}
	
	public static Object getVariableValue(String name){
		Object result = null;
		if (varMap.containsKey(name)){
			Log.i("info", "varMap has "+name);
			ArrayList<Object> variable = varMap.get(name);
			Log.i("info", "value "+variable.get(1));
			result = variable.get(1);
		}
		return result;
	}

	public static Object getParamValue(NodeList params, String name, String type){
		Object result = null;
		int paramLen = params.getLength();
		
		for (int i=0; i<paramLen; i++){
			Element element = (Element) params.item(i);
			if ((element.getAttribute(ScriptTag.NAME).equals(name)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(type))){
				if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE){
					result = element.getChildNodes().item(0).getNodeValue();
				}
			}
		}
		return result;
	}
	
	public static HashMap<String, ArrayList<Object>> getVariablesMap(){
		return varMap;
	}
	
	private static void setVariable(Element element){
		/*
		 * Each varable has a list of value, the two first values are its type and its default value,
		 * for the list, its added values are start with the third position
		 * */
		if (varMap.containsKey(element.getAttribute(ScriptTag.NAME))){
			varMap.remove(element.getAttribute(ScriptTag.NAME));
		}
		if (!element.hasChildNodes()){
			varMap.put(element.getAttribute(ScriptTag.NAME), 
					addVariable(element.getAttribute(ScriptTag.TYPE), null));
		}
		else if ((element.getChildNodes().getLength() == 1) && (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE)){
			varMap.put(element.getAttribute(ScriptTag.NAME), 
					addVariable(element.getAttribute(ScriptTag.TYPE),
							element.getChildNodes().item(0).getNodeValue()));
		}
		else{
			Object value = null;
			NodeList children = element.getChildNodes();
			int childrenLen = children.getLength();
			if (childrenLen == 1){
				Element child = (Element) children.item(0);
				if (child.getNodeName().equals(ScriptTag.CALL)){
					Log.i("info", "call function "+child.getAttribute(ScriptTag.FUNCTION));
					value = returnTypeFunction(child);
				}
				else if (child.getNodeName().equals(ScriptTag.ELEMENT)){
					Log.i("info", "set element id "+child.getAttribute(ScriptTag.ELEMENT_ID));
					value = child.getAttribute(ScriptTag.ELEMENT_ID);
				}
				else if (child.getNodeName().equals(ScriptTag.KEYWORD)){
					value = getKeyWord(child);
				}
			}
			Log.i("info", "prepare to add "+element.getAttribute(ScriptTag.NAME)+" in the var list its value is "+value);
			varMap.put(element.getAttribute(ScriptTag.NAME), 
					addVariable(element.getAttribute(ScriptTag.TYPE), value));
		}
	}

	public static Object getKeyWord(Element element)
	{
		Object result = null;
		if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE){
			if (element.getChildNodes().item(0).getNodeValue().equals(ScriptAttribute.CONST_NULL)){
				result = null;
			}
			else if (element.getChildNodes().item(0).getNodeValue().equals(ScriptAttribute.CONST_TRUE)){
				result = true;
			}
			else if (element.getChildNodes().item(0).getNodeValue().equals(ScriptAttribute.CONST_FALSE)){
				result = false;
			}
			else if (element.getChildNodes().item(0).getNodeValue().equals(ScriptAttribute.CONST_GPS_SIGNAL_OK)){
				result = GpsStatus.GPS_SIGNAL_OK;
			}
		}
		return result;
	}
	
	public static Object getOperator(Object operator){
		Object result = null;
		switch (Integer.valueOf((String)operator)){
			case ScriptAttribute.EQUALS:
				result = "=";
				break;
			case ScriptAttribute.NOTEQUALS:
				result = "!=";
				break;
		}
		return result;
	}
	
	public static Context getContext(){
		return context;
	}
	
	public static String getReturnValue(Element element){
		String result = "";
		if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE){
			result = element.getChildNodes().item(0).getNodeValue();
		}
		return result;
	}
}