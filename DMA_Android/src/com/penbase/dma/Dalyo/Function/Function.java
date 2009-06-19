package com.penbase.dma.Dalyo.Function;

import android.content.Context;
import android.util.Log;

import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.GpsStatus;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Namespace.*;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

public class Function {
	private static Document sBehaviorDocument;
	private static Context sContext;
	private static HashMap<String, Object> sVarsMap;
	private static HashMap<String, ArrayList<String>> sFuncsMap;
	private static boolean sIsFirstTime;
	
	public Function(Context c, Document document) {
		sContext = c;
		sVarsMap = new HashMap<String, Object>();
		sFuncsMap = new HashMap<String, ArrayList<String>>();
		//parametersMap = new HashMap<String, String>();
		sBehaviorDocument = document;
		sIsFirstTime = true;
		long start = System.currentTimeMillis();
		createMaps();
		long end = System.currentTimeMillis();
		Log.i("info", "create variable used "+(end - start)+" ms");
	}
	
	/*
	 * This method need to be modified if we don't need global variables anymore in Dalyo studio
	 * Create 2 maps which contains variables and the positions of function
	 * */
	private void createMaps() {
		NodeList funcList = sBehaviorDocument.getElementsByTagName(ScriptTag.FUNCTION);
		int functionSize = funcList.getLength();
		HashMap<String, ArrayList<String>> functionsMap = sFuncsMap;
		for (int i=0; i<functionSize; i++) {
			Element funcElement = (Element) funcList.item(i);
			ArrayList<String> funcParams = new ArrayList<String>();
			funcParams.add(String.valueOf(i));
			funcParams.add(funcElement.getAttribute(ScriptTag.OUTPUT));
			functionsMap.put(funcElement.getAttribute(ScriptTag.NAME), funcParams);

			NodeList nodesList = funcElement.getChildNodes();
			int itemLen = nodesList.getLength();
			for (int j=0; j<itemLen; j++) {
				Element element = (Element) nodesList.item(j);
				if (element.getNodeName().equals(ScriptTag.SET)) {
					setVariable(element);
				}
			}
		}
		sIsFirstTime = false;
	}
	
	public static Object createFunction(String name) {
		//Log.i("info", "create function name "+name);
		Object result = null;
		HashMap<String, ArrayList<String>> functionsMap = sFuncsMap;
		NodeList funcList = sBehaviorDocument.getElementsByTagName(ScriptTag.FUNCTION);
		if (functionsMap.containsKey(name)) {
			final Element funcElement = (Element) funcList.item(Integer.valueOf(functionsMap.get(name).get(0)));
			final NodeList nodeList = funcElement.getChildNodes();
			int nodeLen = nodeList.getLength();
		
			for (int i=0; i<nodeLen; i++) {
				Element element = (Element) nodeList.item(i);
				result = distributeAction(element);
			}
		}
		return result;
	}
	
	private static Object distributeAction(Element element) {
		Object result = null;
		String elementName = element.getNodeName();
		if (elementName.equals(ScriptTag.CALL)) {
			result = Function.distributeCall(element);
		} else if (elementName.equals(ScriptTag.ELEMENT)) {
			result = element.getAttribute(ScriptTag.ELEMENT_ID);
		} else if (elementName.equals(ScriptTag.FOREACH)) {
			forEach(element);
		} else if (elementName.equals(ScriptTag.IF)) {
			result = ifCondition(element);
		} else if (elementName.equals(ScriptTag.KEYWORD)) {
			result = Function.getKeyWord(element);
		} else if (elementName.equals(ScriptTag.RETURN)) {
			result = getReturnValue(element);
		} else if (elementName.equals(ScriptTag.SET)) {
			setVariable(element);
		} else if (elementName.equals(ScriptTag.VAR)) {
			//use only one format to save all types of variale's value
			result = getVariableValue(element);
		}
		return result;
	}
	
	private static void forEach(Element element) {
		NodeList nodes = element.getChildNodes();
		int elementsNb = nodes.getLength();
		Object list = null;
		String cursorName = null;
		String cursorType = null;
		for (int i=0; i<elementsNb; i++) {
			Element child = (Element)nodes.item(i);
			NodeList childNodes = child.getChildNodes();
			if (child.getNodeName().equals(ScriptTag.LIST)) {
				list = distributeAction((Element)childNodes.item(0));
			} else if (child.getNodeName().equals(ScriptTag.CURSOR)) {
				cursorName = child.getAttribute(ScriptTag.NAME);
				cursorType = child.getAttribute(ScriptTag.TYPE);
				setVariable(child);
			} else if (child.getNodeName().equals(ScriptTag.DO)) {
				for (Object eachValue : (ArrayList<?>)list) {
					if (checkValueType(cursorType, eachValue)) {
						sVarsMap.put(cursorName, eachValue);
						int actionsNb = childNodes.getLength();
						for (int j=0; j<actionsNb; j++) {
							Element grandChild = (Element)childNodes.item(j);
							distributeAction(grandChild);
						}
					}
				}
			}
		}
	}
	
	private static boolean checkValueType(String type, Object value) {
		boolean result = true;
		if (type.equals("numeric")) {
			if (value.toString().indexOf(".") != -1) {
				try {
					Double.valueOf(value.toString());
				} catch (NumberFormatException nfe) {
					result = false;
					ApplicationView.errorDialog("Check your variable's type !");
				}
			} else {
				try {
					Integer.valueOf(value.toString());
				} catch (NumberFormatException nfe) {
					result = false;
					ApplicationView.errorDialog("Check your variable's type !");
				}
			}
		} else if (type.equals("string")) {
			if ((value.getClass().toString().contains("HashMap")) || (value.getClass().toString().contains("ArrayList"))) {
				result = false;
				ApplicationView.errorDialog("Check your variable's type !");
			}
		} else if (type.equals("list")) {
			if (!value.getClass().toString().contains("ArrayList")) {
				result = false;
				ApplicationView.errorDialog("Check your variable's type !");
			}
		}
		return result;
	}
	
	/**
	 * Finds out the result of boolean list
	 * @param boolList
	 * @return
	 */
	private static boolean checkConditions(boolean[] boolList) {
		boolean result = false;
		int size = boolList.length;
		int i = 0;
		int j = 0;
		int checked = 0;
		while (i < size) {
			if (!boolList[i]) {
				result = boolList[i];
				i = size;
			} else {
				if (boolList[i] == boolList[j]) {
					checked++;
					i++;
				} else {
					i = size;
				}
			}
		}
		if (checked == size) {
			result = true;
		}
		return result;
	}
	
	/**
	 * Compares the left value and the right value
	 * @param left
	 * @param operator
	 * @param right
	 * @return
	 */
	private static boolean checkCondition(Object left, Object operator, Object right) {
		boolean result = false;
		switch (Integer.valueOf(operator.toString())) {
			case ScriptAttribute.AND:
				if (left == right) {
					if (left.toString().equals(Constant.TRUE)) {
						result = true;
					}
				}
				break;
			case ScriptAttribute.EQUALS:
				if ((left instanceof Integer) || (right instanceof Integer)) {
					result = (Integer.valueOf(left.toString()) == (Integer.valueOf(right.toString())));
				} else if ((left == null) || (right == null)) {
					result = (left == right);
				} else {
					result = (left.equals(right));
				}
				break;
			case ScriptAttribute.GREATERTHAN:
				if ((left != null) && (right != null)) {
					result = (Integer.valueOf(left.toString()) > Integer.valueOf(right.toString()));	
				}
				break;
			case ScriptAttribute.GREATERTHANOREQUALS:
				if ((left != null) && (right != null)) {
					result = (Integer.valueOf(left.toString()) >= Integer.valueOf(right.toString()));	
				}
				break;
			case ScriptAttribute.NOTEQUALS:
				if ((left != null) && (right != null)) {
					if ((left instanceof String) || (right instanceof String)) {
						result = !(left.toString().equals(right.toString()));
					} else {
						result = (Integer.valueOf(left.toString()) != Integer.valueOf(right.toString()));	
					}
				} else {
					result = (left != right);	
				}
				break;
			case ScriptAttribute.OR:
				if (Boolean.getBoolean(left.toString()) || Boolean.getBoolean(right.toString())) {
					result = true;
				}
				break;
				
			case ScriptAttribute.LESSTHAN:
				if ((left != null) && (right != null)) {
					result = (Integer.valueOf(left.toString()) < Integer.valueOf(right.toString()));	
				}
				break;
			case ScriptAttribute.LESSTHANOREQUALS:
				if ((left != null) && (right != null)) {
					result = (Integer.valueOf(left.toString()) <= Integer.valueOf(right.toString()));	
				}
				break;
		}
		Log.i("info", "left "+left+" operator "+operator+" right "+right+" result "+result);
		return result;
	}
	
	private static Object ifCondition(Element element) {
		Object result = "";
		Log.i("info", "if called");
		
		Node child = element.getFirstChild();
		boolean conditionCheck = false;
		while (child != null) {
			String childName = child.getNodeName();
			if (childName.equals(ScriptTag.CONDITIONS)) {
				NodeList conditions = child.getChildNodes();
				int conditionsLen = conditions.getLength();
				boolean[] checkList = new boolean[conditionsLen];
				for (int k=0; k<conditionsLen; k++) {
					Element condition = (Element)conditions.item(k);
					if (condition.getNodeName().equals(ScriptTag.CONDITION)) {
						Object left = getValue(condition, ScriptTag.LEFT, null, null);
						Object operator = getValue(condition, ScriptTag.OPERATOR, null, null);
						Object right = getValue(condition, ScriptTag.RIGHT, null, null);
						checkList[k] = checkCondition(left, operator, right); 
					}
				}
				conditionCheck = checkConditions(checkList); 
			} else if (childName.equals(ScriptTag.THEN)) {
				if (conditionCheck) {
					Log.i("info", "pass to then");
					Node thenChild = child.getFirstChild();
					while (thenChild != null) {
						result = distributeAction((Element)thenChild);
						try {
							thenChild = thenChild.getNextSibling();	
						} catch (IndexOutOfBoundsException ioobe) {
							thenChild = null;
						}
					}
				}
			} else if (childName.equals(ScriptTag.ELSE)) {
				if (!conditionCheck) {
					Log.i("info", "pass to else");
					Node elseChild = child.getFirstChild();
					while (elseChild != null) {
						result = distributeAction((Element)elseChild);
						try {
							elseChild = elseChild.getNextSibling();	
						} catch (IndexOutOfBoundsException ioobe) {
							elseChild = null;
						}
					}
				}
			}
			try {
				child = child.getNextSibling();	
			} catch (IndexOutOfBoundsException ioobe) {
				child = null;
			}
		}
		
		
		/*NodeList nodes = element.getChildNodes();
		int elementLen = nodes.getLength();
		boolean conditionCheck = false;
		for (int i=0; i<elementLen; i++) {
			Element child = (Element)nodes.item(i);
			if (child.getNodeName().equals(ScriptTag.CONDITIONS)) {
				NodeList conditions = child.getChildNodes();
				int conditionsLen = conditions.getLength();
				boolean[] checkList = new boolean[conditionsLen];
				for (int k=0; k<conditionsLen; k++) {
					Element condition = (Element)conditions.item(k);
					if (condition.getNodeName().equals(ScriptTag.CONDITION)) {
						Object left = getValue(condition, ScriptTag.LEFT, null, null);
						Object operator = getValue(condition, ScriptTag.OPERATOR, null, null);
						Object right = getValue(condition, ScriptTag.RIGHT, null, null);
						checkList[k] = checkCondition(left, operator, right); 
					}
				}
				conditionCheck = checkConditions(checkList); 
			} else if (child.getNodeName().equals(ScriptTag.THEN)) {
				if (conditionCheck) {
					//Log.i("info", "pass to then");
					NodeList thenchildren = child.getChildNodes();
					int thenchildrenLen = thenchildren.getLength();
					for (int k=0; k<thenchildrenLen; k++) {
						Element thenChild = (Element) thenchildren.item(k);
						result = distributeAction(thenChild);
					} 
				}
			} else if (child.getNodeName().equals(ScriptTag.ELSE)) {
				if (!conditionCheck) {
					//Log.i("info", "pass to else");
					NodeList elsechildren = child.getChildNodes();
					int elsechildrenLen = elsechildren.getLength();
					for (int k=0; k<elsechildrenLen; k++) {
						Element elseChild = (Element) elsechildren.item(k);
						result = distributeAction(elseChild);
					}
				}
			}
		}*/
		return result;
	}
	
	public static Object createCalculateFunction(String name, HashMap<Object, Object> record) {
		Object result = null;
		HashMap<String, Object> varsMap = sVarsMap;
		NodeList funcList = sBehaviorDocument.getElementsByTagName(ScriptTag.FUNCTION);
		if (sFuncsMap.containsKey(name)) {
			Element funcElement = (Element) funcList.item(Integer.valueOf(sFuncsMap.get(name).get(0)));
			if (funcElement.getAttribute(ScriptTag.NAME).equals(name)) {
				NodeList nodesList = funcElement.getChildNodes();
				int itemLen = nodesList.getLength();
				for (int i=0; i<itemLen; i++) {
					Element element = (Element) nodesList.item(i);
					if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
							element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.RECORD)) {
						varsMap.put(element.getAttribute(ScriptTag.NAME), record);
					} else {
						distributeAction(element);
					}
				}
			}
		}
		if (result == null) {
			if (sFuncsMap.get(name).get(1).equals(ScriptAttribute.STRING)) {
				result = "";
			}
		}
		return result;
	}
	
	/**
	 * Checks element's name and namespace to call the right function
	 * @param element
	 * @return
	 */
	private static Object distributeCall(Element element) {
		Object result = null;
		if (!sIsFirstTime) {
			Log.i("info", "namespace "+element.getAttribute(ScriptTag.NAMESPACE)+" function name "+element.getAttribute(ScriptTag.FUNCTION));	
		}
		String namespace = element.getAttribute(ScriptTag.NAMESPACE);
		String function = element.getAttribute(ScriptTag.FUNCTION);
		if (namespace.equals(ScriptAttribute.COMPONENT)) {
			if (function.equals(ScriptAttribute.FUNCTION_GETLABEL)) {
				result = NS_Component.GetLabel(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETVALUE)) {
				result = NS_Component.GetValue(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_ISENABLED)) {
				result = NS_Component.IsEnabled(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_ISVISIBLE)) {
				result = NS_Component.IsVisible(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_RESET)) {
				NS_Component.ReSet(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SETENABLED)) {
				NS_Component.SetEnabled(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SETFOCUS)) {
				NS_Component.SetFocus(element);
			} else if ((function.equals(ScriptAttribute.FUNCTION_SETTEXT)) || 
					(function.equals(ScriptAttribute.FUNCTION_SETLABEL))) {
				NS_Component.SetText(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SETVISIBLE)) {
				NS_Component.SetVisible(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SETVALUE)) {
				NS_Component.SetValue(element);
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_COMPONENT_CHECK)) {
			if (function.equals(ScriptAttribute.FUNCTION_CHECK)) {
				NS_ComponentCheckbox.Check(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_ISCHECKED)) {
				result = NS_ComponentCheckbox.IsChecked(element);
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_COMPONENT_CB)) {
			if (function.equals(ScriptAttribute.FUNCTION_ADDITEM)) {
				NS_ComponentCombobox.AddItem(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_COUNT)) {
				result = NS_ComponentCombobox.Count(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETSELECTEDINDEX)) {
				result = NS_ComponentCombobox.GetSelectedIndex(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETSELECTEDRECORD)) {
				result = NS_ComponentCombobox.GetSelectedRecord(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_REFRESH)) {
				NS_ComponentCombobox.Refresh(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_REMOVEALLITEMS)) {
				NS_ComponentCombobox.RemoveAllItems(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SETSELECTEDINDEX)) {
				NS_ComponentCombobox.SetSelectedIndex(element);
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_COMPONENT_DV)) {
			if (function.equals(ScriptAttribute.FUNCTION_GETCELLVALUE)) {
				result = NS_ComponentDataview.GetCellValue(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETCOLUMNINDEX)) {
				result = NS_ComponentDataview.GetColumnIndex(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETROWCOUNT)) {
				result = NS_ComponentDataview.GetRowCount(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETSELECTEDRECORD)) {
				if (!sIsFirstTime) {
					result = NS_ComponentDataview.GetSelectedRecord(element);
				}
			} else if (function.equals(ScriptAttribute.FUNCTION_GETSELECTEDROW)) {
				result = NS_ComponentDataview.GetSelectedRow(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_REFRESH)) {
				NS_ComponentDataview.Refresh(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SETNUMERICFORMAT)) {
				NS_ComponentDataview.SetNumericFormat(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SETSELECTEDROW)) {
				NS_ComponentDataview.SetSelectedRow(element);
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_COMPONENT_NB)) {
			if (function.equals(ScriptAttribute.FUNCTION_SETMAX)) {
				NS_ComponentNumberBox.SetMax(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SETMIN)) {
				NS_ComponentNumberBox.SetMin(element);
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_COMPONENT_RB)) {
			if (function.equals(ScriptAttribute.FUNCTION_ISSELECTED)) {
				result = NS_ComponentRadioButton.IsSelected(element);
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_COMPONENT_TF)) {
			if (function.equals(ScriptAttribute.FUNCTION_GETTEXT)) {
				result = NS_ComponentTextField.GetText(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_ISEMPTY)) {
				result = NS_ComponentTextField.IsEmpty(element);
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_DATE)) {
			if (function.equals(ScriptAttribute.FUNCTION_ADDMINUTES)) {
				result = NS_Date.AddMinutes(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_FORMAT)) {
				result = NS_Date.Format(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_NOW)) {
				result = NS_Date.CurrentDate();
			} else if (function.equals(ScriptAttribute.FUNCTION_NOWHOUR)) {
				result = NS_Date.CurrentHour();
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_DB)) {
			if (function.equals(ScriptAttribute.FUNCTION_CANCELTRANSACTION)) {
				NS_Database.CancelTransaction();
			} else if (function.equals(ScriptAttribute.FUNCTION_EXPORT)) {
				if (!sIsFirstTime) {
					result = NS_Database.Export(element);
				}
			} else if (function.equals(ScriptAttribute.FUNCTION_IMPORT)) {
				if (!sIsFirstTime) {
					result =  NS_Database.Import(element);
				}
			} else if (function.equals(ScriptAttribute.FUNCTION_GETTABLEBYNAME)) {
				result = NS_Database.GetTableByName(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_STARTTRANSACTION)) {
				NS_Database.StartTransaction();
			} else if (function.equals(ScriptAttribute.FUNCTION_VALIDATETRANSACTION)) {
				NS_Database.ValidateTransaction();
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_DB_DATASET)) {
			if (function.equals(ScriptAttribute.FUNCTION_GETVALUE)) {
				result = NS_DatabaseDataset.GetValue(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SELECT)) {
				result = NS_DatabaseDataset.Select(element);
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_DB_FIELD)) {
			if (function.equals(ScriptAttribute.FUNCTION_GETFIELDNAME)) {
				result = NS_DatabaseField.GetFieldName(element);
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_DB_TABLE)) {
			if (function.equals(ScriptAttribute.FUNCTION_AVERAGE)) {
				result = NS_DatabaseTable.Average(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_CANCELNRECORD)) {

			} else if (function.equals(ScriptAttribute.FUNCTION_CLEAR)) {
				NS_DatabaseTable.Clear(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_COUNT)) {
				result = NS_DatabaseTable.Count(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_DELETERECORD)) {
				NS_DatabaseTable.DeleteRecord(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_DELETERECORDS)) {
				NS_DatabaseTable.DeleteRecords(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_EDITRECORD)) {
				NS_DatabaseTable.EditRecord(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETFIELDBYNAME)) {
				result = NS_DatabaseTable.GetFieldByName(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETFIELDS)) {
				result = NS_DatabaseTable.GetFields(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETFIELDVALUE)) {
				result = NS_DatabaseTable.GetFieldValue(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETFIELDVALUEBYPRIMARYKEY)) {
				result = NS_DatabaseTable.GetFieldValueByPrimaryKey(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETFILTEREDRECORD)) {
				result = NS_DatabaseTable.GetFilteredRecord(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETFILTEREDRECORDS)) {
				result = NS_DatabaseTable.GetFilteredRecords(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETRECORD)) {
				result = NS_DatabaseTable.GetRecord(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETRECORDS)) {
				result = NS_DatabaseTable.GetRecords(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_MAX)) {
				result = NS_DatabaseTable.Max(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_MIN)) {
				result = NS_DatabaseTable.Min(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_NEWRECORD)) {
				if (!sIsFirstTime) {
					result = NS_DatabaseTable.CreateNewRecord(element);
				}
			} else if (function.equals(ScriptAttribute.FUNCTION_STARTNEWRECORD)) {

			} else if (function.equals(ScriptAttribute.FUNCTION_SUM)) {
				result = NS_DatabaseTable.Sum(element);
			}
		} else if (namespace.equals(ScriptAttribute.FILTER)) {
			if (function.equals(ScriptAttribute.FUNCTION_ADDCRITERIA)) {
				NS_Filter.AddCriteria(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_CLEAR)) {
				NS_Filter.Clear(element);
			}
		} else if (namespace.equals(ScriptAttribute.FORM)) {
			if (function.equals(ScriptAttribute.FUNCTION_CLEAR)) {
				NS_Form.Clear(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETCURRENTFORM)) {
				result = NS_Form.GetCurrentForm(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_NAVIGATE)) {
				NS_Form.Navigate(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SETCURRENTRECORD)) {
				NS_Form.SetCurrentRecord(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SETTITLE)) {
				NS_Form.SetTitle(element);
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_GPS)) {
			if (function.equals(ScriptAttribute.FUNCTION_GETALTITUDE)) {
				result = NS_Gps.GetAltitude(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETLATITUDE)) {
				result = NS_Gps.GetLatitude(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETLOCATION)) {
				if (!sIsFirstTime) {
					result = NS_Gps.GetLocation();	
				}
			} else if (function.equals(ScriptAttribute.FUNCTION_GETLONGITUDE)) {
				result = NS_Gps.GetLogitude(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETSPEED)) {
				result = NS_Gps.GetSpeed(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETSTATUS)) {
				result = NS_Gps.GetStatus();
			} else if (function.equals(ScriptAttribute.FUNCTION_INIT)) {
				NS_Gps.Init(sContext);
			} else if (function.equals(ScriptAttribute.FUNCTION_STOP)) {
				NS_Gps.Stop();
			}
		} else if (namespace.equals(ScriptAttribute.LIST)) {
			if (function.equals(ScriptAttribute.FUNCTION_ADD)) {
				NS_List.AddValue(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_CLEAR)) {
				NS_List.Clear(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GET)) {
				result = NS_List.Get(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETSIZE)) {
				result = NS_List.Size(element);
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_MATH)) {
			if (function.equals(ScriptAttribute.FUNCTION_ABS)) {
				result = NS_Math.Abs(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_ADD)) {
				result = NS_Math.Sum(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_CEIL)) {
				result = NS_Math.Ceil(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_DIVISION)) {
				result = NS_Math.Division(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_FLOOR)) {
				result = NS_Math.Floor(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_FORMAT)) {
				result = NS_Math.Format(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_MULTIPLE)) {
				result = NS_Math.Multiple(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_PERCANTAGE)) {
				result = NS_Math.Percentage(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_RANDOM)) {
				result = NS_Math.Random(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_ROUND)) {
				result = NS_Math.Round(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SUB)) {
				result = NS_Math.Subtract(element);
			}
		} else if (namespace.equals(ScriptAttribute.OBJECT)) {
			if (function.equals(ScriptAttribute.FUNCTION_TOBOOLEAN)) {
				if (!sIsFirstTime) {
					result = NS_Object.ToBoolean(element);
				}
			} else if (function.equals(ScriptAttribute.FUNCTION_TODATE)) {
				if (!sIsFirstTime) {
					result = NS_Object.ToDate(element);
				}
			} else if (function.equals(ScriptAttribute.FUNCTION_TOINT)) {
				if (!sIsFirstTime) {
					result = NS_Object.ToInt(element);
				}
			} else if (function.equals(ScriptAttribute.FUNCTION_TONUMERIC)) {
				if (!sIsFirstTime) {
					result = NS_Object.ToNumeric(element);
				}
			} else if (function.equals(ScriptAttribute.FUNCTION_TOSTRING)) {
				result = NS_Object.ToString(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_TORECORD)) {
				result = NS_Object.ToRecord(element);
			}
		} else if (namespace.equals(ScriptAttribute.ORDER)) {
			if (function.equals(ScriptAttribute.FUNCTION_ADDCRITERIA)) {
				NS_Order.AddCriteria(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_CLEAR)) {
				//NS_Order.Clear(element);
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_RUNTIME)) {
			if ((function.equals(ScriptAttribute.FUNCTION_ALERT)) ||
					(function.equals(ScriptAttribute.FUNCTION_ERROR))) {
				NS_Runtime.Error(sContext, element);
			} else if (function.equals(ScriptAttribute.FUNCTION_EXIT)) {
				NS_Runtime.Exit(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_BROWSE)) {
				NS_Runtime.Browse(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_CONFIRM)) {
				ConfirmDialog confirmDialog = new ConfirmDialog(element.getElementsByTagName(ScriptTag.PARAMETER), sContext);
				confirmDialog.start();
				try{
					confirmDialog.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				result = confirmDialog.getValue();
				//Cancel the thread, because the stop() method is deprecated
				//confirmDialog = null;
			} else if (function.equals(ScriptAttribute.FUNCTION_GETAPPLICATIONVERSION)) {
				result = NS_Runtime.getApplicationVersion(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_GETCURRENTUSER)) {
				result = NS_Runtime.GetCurrentUser(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SETWAITCURSOR)) {
				NS_Runtime.SetWaitCursor(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_STARTAPP)) {
				NS_Runtime.StartApp(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SYNC)) {
				if (!sIsFirstTime) {
					Log.i("info", "not first time synchro");
					result = NS_Runtime.Synchronize(element);
				}
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_STRING)) {
			if (function.equals(ScriptAttribute.FUNCTION_CONCAT)) {
				result = NS_String.Concat(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_EXPLODE)) {
				result = NS_String.Explode(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_IMPLODE)) {
				result = NS_String.Implode(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_INDEXOF)) {
				if (!sIsFirstTime) {
					result = NS_String.Indexof(element);	
				}
			} else if (function.equals(ScriptAttribute.FUNCTION_LENGTH)) {
				result = NS_String.Length(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_REPLACE)) {
				result = NS_String.Replace(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_SUBSTRING)) {
				result = NS_String.Substring(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_TOLOWER)) {
				result = NS_String.ToLower(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_TOUPPER)) {
				result = NS_String.ToUpper(element);
			} else if (function.equals(ScriptAttribute.FUNCTION_TRIM)) {
				result = NS_String.Trim(element);
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_TIMER)) {
			if (function.equals(ScriptAttribute.FUNCTION_CANCEL)) {
				if (!sIsFirstTime) {
					NS_Timer.Cancel(element);	
				}
			} else if (function.equals(ScriptAttribute.FUNCTION_START)) {
				if (!sIsFirstTime) {
					result = NS_Timer.Start(element);
				}
			}
		} else if (namespace.equals(ScriptAttribute.NAMESPACE_USER)) {
			NodeList nodes = element.getChildNodes();
			if (nodes.getLength() > 0) {
				int childrenLen = nodes.getLength();
				for (int i=0; i<childrenLen; i++) {
					Element child = (Element)nodes.item(i);
					if (child.getNodeName().equals(ScriptTag.PARAMETER)) {
						setVariable(child);
					}
				}
			}
			result = createFunction(function);
		}
		return result;
	}
	
	/**
	 * Returns variable's value
	 * @param item
	 * @return
	 */
	private static Object getVariableValue(Element item) {
		Object result = null;
		HashMap<String, Object> varsMap = sVarsMap;
		if (varsMap.containsKey(item.getAttribute(ScriptTag.NAME))) {
			result = varsMap.get(item.getAttribute(ScriptTag.NAME));
		} else {
			Node parent = item.getParentNode();
			while (!parent.getNodeName().equals(ScriptTag.FUNCTION)) {
				parent = parent.getParentNode();
			}
			StringBuffer varName = new StringBuffer(((Element) parent).getAttribute(ScriptTag.NAME));
			varName.append("_");
			varName.append(item.getAttribute(ScriptTag.NAME));
			if (varsMap.containsKey(varName.toString())) {
				result = varsMap.get(varName.toString());
			}
		}
		return result;
	}
	
	private static void setVariable(Element element) {
		HashMap<String, Object> varsMap = sVarsMap;
		NodeList nodes = element.getChildNodes();
		int nodesLength = nodes.getLength();
		if (nodesLength == 0) {
			//Add an empty ArrayList for Filter, List, Order
			if ((element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.FILTER)) ||
					(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.LIST)) ||
					(element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.ORDER))) {
				//sVarsMap.put(element.getAttribute(ScriptTag.NAME), new ArrayList<Object>());
				varsMap.put(element.getAttribute(ScriptTag.NAME), new ArrayList<Object>());
			} else {
				//sVarsMap.put(element.getAttribute(ScriptTag.NAME), null);
				varsMap.put(element.getAttribute(ScriptTag.NAME), null);
			}
		} else if ((nodesLength == 1) && (nodes.item(0).getNodeType() == Node.TEXT_NODE)) {
			//sVarsMap.put(element.getAttribute(ScriptTag.NAME), nodes.item(0).getNodeValue());	
			varsMap.put(element.getAttribute(ScriptTag.NAME), nodes.item(0).getNodeValue());
		} else {
			Object value = getValue(element, nodes.item(0).getNodeName(), "", "");
			//sVarsMap.put(element.getAttribute(ScriptTag.NAME), value);
			varsMap.put(element.getAttribute(ScriptTag.NAME), value);
		}
	}

	public static Object getKeyWord(Element element)
	{
		Object result = null;
		Node child = element.getFirstChild();
		if (child.getNodeType() == Node.TEXT_NODE) {
			String nodeValue = child.getNodeValue();
			if (nodeValue.equals(ScriptAttribute.CONST_NULL)) {
				result = null;
			} else if (nodeValue.equals(ScriptAttribute.CONST_TRUE)) {
				result = true;
			} else if (nodeValue.equals(ScriptAttribute.CONST_FALSE)) {
				result = false;
			} else if (nodeValue.equals(ScriptAttribute.CONST_GPS_SIGNAL_OK)) {
				result = GpsStatus.GPS_SIGNAL_OK;
			} else if (nodeValue.equals(ScriptAttribute.CONST_EMPTY_STRING)) {
				result = Constant.EMPTY_STRING;
			}
		}
		return result;
	}
	
	public static Object getOperator(Object operator) {
		Object result = null;
		switch (Integer.valueOf((String)operator)) {
			case ScriptAttribute.EQUALS:
				result = "=";
				break;
			case ScriptAttribute.NOTEQUALS:
				result = "!=";
				break;
			case ScriptAttribute.AND:
				result = " AND ";
				break;
			case ScriptAttribute.OR:
				result = " OR ";
				break;
		}
		return result;
	}
	
	public static Context getContext() {
		return sContext;
	}
	
	private static String getReturnValue(Element element) {
		String result = "";
		Node child = element.getFirstChild();
		if (child.getNodeType() == Node.TEXT_NODE) {
			result = child.getNodeValue();
		} else {
			result = distributeAction((Element)child).toString();
		}
		return result;
	}
	
	public static Object getVariableName(Element element, String tag, String name, String type) {
		Object value = null;
		Node child = element.getFirstChild();
		while (child != null) {
			if ((child.getNodeName().equals(tag)) &&
					(((Element)child).getAttribute(ScriptTag.NAME).equals(name)) &&
					(((Element)child).getAttribute(ScriptTag.TYPE).equals(type))) {
				if (child.hasChildNodes()) {
					Node grandChild = child.getFirstChild();
					if (grandChild.getNodeType() == Node.ELEMENT_NODE) {
						if (grandChild.getNodeName().equals(ScriptTag.VAR)) {
							value = ((Element)grandChild).getAttribute(ScriptTag.NAME);
						}
					} else if (grandChild.getNodeType() == Node.TEXT_NODE) {
						value = grandChild.getNodeValue();
					}
				}
			}
			
			try {
				child = child.getNextSibling();	
			} catch (IndexOutOfBoundsException ioobe) {
				child = null;
			}
		}
		
		/*NodeList nodes = element.getChildNodes();
		int itemsLen = nodes.getLength();
		for (int i=0; i<itemsLen; i++) {
			Element child = (Element)nodes.item(i);
			NodeList childNodes = child.getChildNodes();
			int childNodesLength = childNodes.getLength();
			if ((child.getNodeName().equals(tag)) &&
					(child.getAttribute(ScriptTag.NAME).equals(name)) &&
					(child.getAttribute(ScriptTag.TYPE).equals(type))) {
				if (childNodesLength == 1) {
					if (childNodes.item(0).getNodeType() == Node.ELEMENT_NODE) {
						Element item = (Element)childNodes.item(0);
						if (item.getNodeName().equals(ScriptTag.VAR)) {
							value = item.getAttribute(ScriptTag.NAME);
						}
					} else if (childNodes.item(0).getNodeType() == Node.TEXT_NODE) {
						value = childNodes.item(0).getNodeValue();
					}
				}
			}
		}*/
		return value;
	}
	
	/**
	 * Returns values of function's parameter
	 * @param element
	 * @param tag
	 * @param name
	 * @param type
	 * @return
	 */
	public static Object getValue(Element element, String tag, String name, String type) {
		Object value = null;
		Node child = element.getFirstChild();
		while (child != null) {
			String childName = child.getNodeName();
			if ((childName.equals(tag)) && (name == null) && (type == null)) {
				Node firstChild = child.getFirstChild();
				if (firstChild != null) {
					if (firstChild.getNodeType() == Node.ELEMENT_NODE) {
						Element item = (Element)firstChild;
						value = distributeAction(item);
					} else if (firstChild.getNodeType() == Node.TEXT_NODE) {
						value = firstChild.getNodeValue();
					}
				}
			} else if ((childName.equals(tag)) && (name.equals("")) && (type.equals(""))) {
				//Set variable
				value = distributeAction((Element)child);
			} else if ((childName.equals(tag)) &&
					(((Element)child).getAttribute(ScriptTag.NAME).equals(name)) &&
					(((Element)child).getAttribute(ScriptTag.TYPE).equals(type))) {
				Node firstChild = child.getFirstChild();
				//Function parameters
				if (firstChild != null) {
					if (firstChild.getNodeType() == Node.ELEMENT_NODE) {
						Element item = (Element)firstChild;
						value = distributeAction(item);
					} else if (firstChild.getNodeType() == Node.TEXT_NODE) {
						value = firstChild.getNodeValue();
					}
				}
			}
			try {
				child = child.getNextSibling();	
			} catch (IndexOutOfBoundsException ioobe) {
				child = null;
			}
		}
		return value;
	}
	
	public static HashMap<String, Object> getVariablesMap() {
		return sVarsMap;
	}
}