package com.penbase.dma.Dalyo.Function;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.content.Context;
import android.util.Log;

import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.GpsStatus;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Namespace.*;
import com.penbase.dma.View.ApplicationView;

public class Function {
	private static Document behaviorDocument;
	private static Context context;
	private static HashMap<String, Object> varsMap;
	private static HashMap<String, ArrayList<String>> funcsMap;
	private static boolean first = true;
	private static HashMap<String, String> parametersMap;
	
	public Function(Context c, Document document){
		context = c;
		varsMap = new HashMap<String, Object>();
		funcsMap = new HashMap<String, ArrayList<String>>();
		parametersMap = new HashMap<String, String>();
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
			funcsMap.put(funcElement.getAttribute(ScriptTag.NAME), funcParams);
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
	
	public static void createFunction(String name){
		Log.i("info", "createFunction");
		NodeList funcList = behaviorDocument.getElementsByTagName(ScriptTag.FUNCTION);
		if (funcsMap.containsKey(name)){
			final Element funcElement = (Element) funcList.item(Integer.valueOf(funcsMap.get(name).get(0)));
			final NodeList nodeList = funcElement.getChildNodes();
			int nodeLen = nodeList.getLength();
		
			for (int i=0; i<nodeLen; i++){
				Element element = (Element) nodeList.item(i);
				if (element.getNodeName().equals(ScriptTag.PARAMETER)){
					//save parameters
					String paramName = funcElement.getAttribute(ScriptTag.NAME)+"_"+element.getAttribute(ScriptTag.NAME);
					if (parametersMap.containsKey(paramName)){
						varsMap.put(paramName, parametersMap.get(paramName));
					}
				}
				else {
					distributeAction(element);
				}
			}
		}
	}
	
	private static Object distributeAction(Element element) {
		Object result = null;
		if (element.getNodeName().equals(ScriptTag.CALL)){
			result = Function.distributeCall(element);
		}
		else if (element.getNodeName().equals(ScriptTag.ELEMENT)){
			result = element.getAttribute(ScriptTag.ELEMENT_ID);
		}
		else if (element.getNodeName().equals(ScriptTag.FOREACH)){
			forEach(element);
		}
		else if (element.getNodeName().equals(ScriptTag.IF)){
			Log.i("info", "there is if condition in then");
			result = ifCondition(element);
		}
		else if (element.getNodeName().equals(ScriptTag.KEYWORD)){
			result = Function.getKeyWord(element);
		}
		else if (element.getNodeName().equals(ScriptTag.RETURN)){
			Log.i("info", "return value");
			result = getReturnValue(element);
		}
		else if (element.getNodeName().equals(ScriptTag.SET)){
			setVariable(element);
		}
		else if (element.getNodeName().equals(ScriptTag.VAR)){
			//use only one format to save all types of variale's value
			result = getVariableValue(element);
		}
		return result;
	}
	
	private static void forEach(Element element) {
		int elementsNb = element.getChildNodes().getLength();
		ArrayList<?> list = null;
		String cursorName = null;
		String cursorType = null;
		for (int i=0; i<elementsNb; i++) {
			Element child = (Element)element.getChildNodes().item(i);
			if (child.getNodeName().equals(ScriptTag.LIST)) {
				list = (ArrayList<?>)getVariableValue(((Element)child.getChildNodes().item(0)));
			}
			else if (child.getNodeName().equals(ScriptTag.CURSOR)) {
				cursorName = child.getAttribute(ScriptTag.NAME);
				cursorType = child.getAttribute(ScriptTag.TYPE);
				setVariable(child);
			}
			else if (child.getNodeName().equals(ScriptTag.DO)) {
				for (Object eachValue : list) {
					if (checkValueType(cursorType, eachValue)) {
						addVariableValue(cursorName, eachValue, false);
						int actionsNb = child.getChildNodes().getLength();
						for (int j=0; j<actionsNb; j++) {
							Element grandChild = (Element)child.getChildNodes().item(j);
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
			if (String.valueOf(value).indexOf(".") != -1){
				try {
					Double.valueOf(String.valueOf(value));
				}
				catch (NumberFormatException nfe) {
					result = false;
					ApplicationView.errorDialog("Check your variable's type !");
				}
			}
			else{
				try {
					Integer.valueOf(String.valueOf(value));
				}
				catch (NumberFormatException nfe) {
					result = false;
					ApplicationView.errorDialog("Check your variable's type !");
				}
			}
		}
		else if (type.equals("string")) {
			if ((value.getClass().toString().contains("HashMap")) || (value.getClass().toString().contains("ArrayList"))) {
				result = false;
				ApplicationView.errorDialog("Check your variable's type !");
			}
		}
		else if (type.equals("list")) {
			if (!value.getClass().toString().contains("ArrayList")) {
				result = false;
				ApplicationView.errorDialog("Check your variable's type !");
			}
		}
		return result;
	}
	
	//Find out the result of boolean list
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
	
	//Compare the left value and the right value
	private static boolean checkCondition(Object left, Object operator, Object right){
		boolean result = false;
		switch (Integer.valueOf(String.valueOf(operator))){
			case ScriptAttribute.AND:
				if (left == right) {
					if (String.valueOf(left).equals(Constant.TRUE)) {
						result = true;
					}
				}
				break;
			case ScriptAttribute.EQUALS:
				if ((left instanceof Integer) || (right instanceof Integer)){
					result = (Integer.valueOf(String.valueOf(left)) == (Integer.valueOf(String.valueOf(right))));
				}
				else if ((left == null) || (right == null)){
					result = (left == right);
				}
				else{
					result = (left.equals(right));
				}
				break;
			case ScriptAttribute.GREATERTHAN:
				if ((left != null) && (right != null)){
					result = (Integer.valueOf(String.valueOf(left)) > Integer.valueOf(String.valueOf(right)));	
				}
				break;
			case ScriptAttribute.NOTEQUALS:
				result = (left != right);
				break;
			case ScriptAttribute.OR:
				if (Boolean.getBoolean(String.valueOf(left)) || Boolean.getBoolean(String.valueOf(right))) {
					result = true;
				}
				break;
		}
		Log.i("info", "left "+left+" operator "+operator+" right "+right+" result "+result);
		return result;
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
						Object left = getValue(condition, ScriptTag.LEFT, null, null);
						Object operator = getValue(condition, ScriptTag.OPERATOR, null, null);
						Object right = getValue(condition, ScriptTag.RIGHT, null, null);
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
						result = distributeAction(thenChild);
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
						result = distributeAction(elseChild);
					}
				}
			}
		}
		return result;
	}
	
	public static Object createCalculateFunction(String name, HashMap<Object, Object> record){
		Object result = null;
		NodeList funcList = behaviorDocument.getElementsByTagName(ScriptTag.FUNCTION);
		if (funcsMap.containsKey(name)){
			Element funcElement = (Element) funcList.item(Integer.valueOf(funcsMap.get(name).get(0)));
			if (funcElement.getAttribute(ScriptTag.NAME).equals(name)){
				NodeList nodesList = funcElement.getChildNodes();
				int itemLen = nodesList.getLength();
				for (int i=0; i<itemLen; i++){
					Element element = (Element) nodesList.item(i);
					if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
							element.getAttribute(ScriptTag.TYPE).equals(ScriptAttribute.RECORD)){
						varsMap.put(element.getAttribute(ScriptTag.NAME), record);
					}
					else {
						distributeAction(element);
					}
				}
			}
		}
		if (result == null){
			if (funcsMap.get(name).get(1).equals(ScriptAttribute.STRING)){
				result = "";
			}
		}
		return result;
	}
	
	//Check element's name and namespace to call the right function 
	public static Object distributeCall(Element element){
		Object result = null;
		Log.i("info", "function name "+element.getAttribute(ScriptTag.FUNCTION));
		if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.COMPONENT)){
			if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETVALUE)){
				result = NS_Component.GetValue(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ISENABLED)){
				result = NS_Component.IsEnabled(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ISVISIBLE)){
				result = NS_Component.IsVisible(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_RESET)){
				NS_Component.ReSet(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SETENABLED)){
				NS_Component.SetEnabled(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SETTEXT)){
				NS_Component.SetText(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SETVISIBLE)){
				NS_Component.SetVisible(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SETVALUE)){
				NS_Component.SetValue(element);
			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_COMPONENT_CB)){
			if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETSELECTEDRECORD)){
				result = NS_ComponentCombobox.GetSelectedRecord(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_REFRESH)){
				NS_ComponentCombobox.Refresh(element);
			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_COMPONENT_DV)){
			if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETSELECTEDRECORD)){
				result = NS_ComponentDataview.GetSelectedRecord(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_REFRESH)){
				NS_ComponentDataview.Refresh(element);
			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DATE)){
			if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_NOW)){
				result = NS_Date.GetCurrentDate();
			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB)){
			if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CANCELTRANSACTION)){
				NS_Database.CancelTransaction();
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_EXPORT)){
				NS_Database.Export(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_IMPORT)){
				NS_Database.Import(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_STARTTRANSACTION)){
				NS_Database.StartTransaction();
			}

			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_VALIDATETRANSACTION)){
				NS_Database.ValidateTransaction();
			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_DB_TABLE)){
			if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CANCELNRECORD)){

			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CLEAR)){
				NS_DatabaseTable.Clear(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_COUNT)){
				result = NS_DatabaseTable.Count(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_NEWRECORD)){
				if (!first){
					result = NS_DatabaseTable.CreateNewRecord(element);
				}
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_DELETERECORD)){
				NS_DatabaseTable.DeleteRecord(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_EDITRECORD)){
				NS_DatabaseTable.EditRecord(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETFIELDVALUE)){
				result = NS_DatabaseTable.GetFieldValue(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETFILTEREDRECORDS)){
				result = NS_DatabaseTable.GetFilteredRecords(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETRECORDS)){
				result = NS_DatabaseTable.GetRecords(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_STARTNEWRECORD)){

			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.FILTER)){
			if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ADDCRITERIA)){
				NS_Filter.AddCriteria(element);
			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.FORM)){
			if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_NAVIGATE)){
				NS_Form.Navigate(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SETCURRENTRECORD)){
				NS_Form.SetCurrentRecord(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SETTITLE)){
				NS_Form.SetTitle(element);
			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_GPS)){
			if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETLATITUDE)){
				result = NS_Gps.GetLatitude(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETLOCATION)){
				result = NS_Gps.GetLocation();
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETLONGITUDE)){
				result = NS_Gps.GetLogitude(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETSTATUS)){
				result = NS_Gps.GetStatus();
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_INIT)){
				NS_Gps.Init(context);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_STOP)){
				NS_Gps.Stop();
			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.LIST)){
			if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GET)){
				result = NS_List.GetListItem(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_GETSIZE)){
				result = NS_List.GetSize(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ADD)){
				NS_List.ListAddValue(element);
			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_MATH)){
			if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SUB)){
				result = NS_Math.Subtract(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ADD)){
				result = NS_Math.Sum(element);
			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.OBJECT)){
			if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_TOINT)){
				if (!first){
					result = NS_Object.ToInt(element);
				}
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_TONUMERIC)){
				if (!first){
					result = NS_Object.ToNumeric(element);
				}
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_TOSTRING)){
				result = NS_Object.ToString(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_TORECORD)){
				result = NS_Object.ToRecord(element);
			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_RUNTIME)){
			if ((element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ALERT)) ||
					(element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_ERROR))){
				NS_Runtime.Alert(context, element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CONFIRM)){
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
				//confirmDialog = null;
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_SYNC)){
				if (!first){
					result = NS_Runtime.Synchronize(element);
				}
			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_TIMER)){
			if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_CANCEL)){
				NS_Timer.Cancel(element);
			}
			else if (element.getAttribute(ScriptTag.FUNCTION).equals(ScriptAttribute.FUNCTION_START)){
				if (!first){
					result = NS_Timer.Start(element);
				}
			}
		}
		else if (element.getAttribute(ScriptTag.NAMESPACE).equals(ScriptAttribute.NAMESPACE_USER)){
			if (element.getChildNodes().getLength() > 0){
				int childrenLen = element.getChildNodes().getLength();
				for (int i=0; i<childrenLen; i++){
					Element child = (Element)element.getChildNodes().item(i);
					if ((child.getNodeName().equals(ScriptTag.PARAMETER)) && (child.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE)){
						parametersMap.put(element.getAttribute(ScriptTag.FUNCTION)+"_"+child.getAttribute(ScriptTag.NAME), child.getChildNodes().item(0).getNodeValue());
					}
				}
				createFunction(element.getAttribute(ScriptTag.FUNCTION));
			}
			else{
				createFunction(element.getAttribute(ScriptTag.FUNCTION));
			}
		}
		return result;
	}
	
	//Return variable's value
	public static Object getVariableValue(Element item){
		Object result = null;
		if (varsMap.containsKey(item.getAttribute(ScriptTag.NAME))){
			result = varsMap.get(item.getAttribute(ScriptTag.NAME));
		}
		else{
			Node parent = item.getParentNode();
			while (!parent.getNodeName().equals(ScriptTag.FUNCTION)){
				parent = parent.getParentNode();
			}
			String varName = ((Element) parent).getAttribute(ScriptTag.NAME)+"_"+item.getAttribute(ScriptTag.NAME);
			if (varsMap.containsKey(varName)){
				result = varsMap.get(varName);
			}
		}
		return result;
	}
	
	public static void addFilterValues(String name, String field, String operator, Object value, Object link){
		if (varsMap.containsKey(name)){
			if (varsMap.get(name) == null) {
				varsMap.remove(name);
				varsMap.put(name, new ArrayList<Object>());
			}
			((ArrayList<Object>) varsMap.get(name)).add(field);
			((ArrayList<Object>) varsMap.get(name)).add(operator);
			((ArrayList<Object>) varsMap.get(name)).add(value);
			((ArrayList<Object>) varsMap.get(name)).add(link);
		}
	}
	
	public static void addVariableValue(String name, Object value, boolean isList) {
		if (varsMap.containsKey(name)){
			if (varsMap.get(name) == null) {
				if (isList) {
					varsMap.remove(name);
					varsMap.put(name, new ArrayList<Object>());
				}
			}
			if (isList) {
				((ArrayList<Object>) varsMap.get(name)).add(value);
			}
			else if (!isList) {
				varsMap.put(name, value);
			}
		}
	}
	
	private static void setVariable(Element element){
		/*
		 * Each varable has a list of value, the two first values are its type and its default value,
		 * for the list, its added values which start at the third position
		 * */
		if (varsMap.containsKey(element.getAttribute(ScriptTag.NAME))){
			varsMap.remove(element.getAttribute(ScriptTag.NAME));
		}
		if (!element.hasChildNodes()){
			Log.i("info", "add variable "+element.getAttribute(ScriptTag.NAME));
			varsMap.put(element.getAttribute(ScriptTag.NAME), null);
		}
		else if ((element.getChildNodes().getLength() == 1) && (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE)){
			Log.i("info", "1 child "+element.getChildNodes().item(0).getNodeValue());
			varsMap.put(element.getAttribute(ScriptTag.NAME), element.getChildNodes().item(0).getNodeValue());	
		}
		else{
			Log.i("info", "element "+element.getNodeName());
			Object value = getValue(element, element.getChildNodes().item(0).getNodeName(), "", "");
			Log.i("info", "prepare to add "+element.getAttribute(ScriptTag.NAME)+" in the var list its value is "+value);
			varsMap.put(element.getAttribute(ScriptTag.NAME), value);
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
			else if (element.getChildNodes().item(0).getNodeValue().equals(ScriptAttribute.CONST_EMPTY_STRING)){
				result = "";
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
	
	private static String getReturnValue(Element element){
		String result = "";
		if (element.getChildNodes().getLength() > 0){
			if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE){
				result = element.getChildNodes().item(0).getNodeValue();
			}
		}
		return result;
	}
	
	public static Object getVariableName(Element element, String tag, String name, String type){
		Object value = null;
		int itemsLen = element.getChildNodes().getLength();
		for (int i=0; i<itemsLen; i++){
			Element child = (Element) element.getChildNodes().item(i);
			if ((child.getNodeName().equals(tag)) &&
					(child.getAttribute(ScriptTag.NAME).equals(name)) &&
					(child.getAttribute(ScriptTag.TYPE).equals(type))){
				if (child.getChildNodes().getLength() == 1){
					if (child.getChildNodes().item(0).getNodeType() == Node.ELEMENT_NODE){
						Element item = (Element) child.getChildNodes().item(0);
						if (item.getNodeName().equals(ScriptTag.VAR)){
							value = item.getAttribute(ScriptTag.NAME);
							Log.i("info", "value of variable "+value);
						}
					}
					else if (child.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE){
						
					}
				}
			}
		}
		return value;
	}
	
	//Return values of function's parameter
	public static Object getValue(Element element, String tag, String name, String type){
		Object value = null;
		int itemsLen = element.getChildNodes().getLength();
		for (int i=0; i<itemsLen; i++){
			Element child = (Element) element.getChildNodes().item(i);
			//If condition elements
			if ((child.getNodeName().equals(tag)) && (name == null) && (type == null)){
				Log.i("info", "child.getChildNodes().getLength() "+child.getChildNodes().getLength());
				if (child.getChildNodes().getLength() == 1){
					Log.i("info", "check string "+child.getChildNodes().toString());
					if (child.getChildNodes().item(0).getNodeType() == Node.ELEMENT_NODE){
						Element item = (Element) child.getChildNodes().item(0);
						value = distributeAction(item);
					}
					else if (child.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE){
						value = child.getChildNodes().item(0).getNodeValue();
					}
				}
			}
			//Set variable
			else if ((child.getNodeName().equals(tag)) && (name.equals("")) && (type.equals(""))){
				value = distributeAction(child);
			}
			//Function parameters
			else if ((child.getNodeName().equals(tag)) &&
					(child.getAttribute(ScriptTag.NAME).equals(name)) &&
					(child.getAttribute(ScriptTag.TYPE).equals(type))){
				if (child.getChildNodes().getLength() == 1){
					if (child.getChildNodes().item(0).getNodeType() == Node.ELEMENT_NODE){
						Element item = (Element) child.getChildNodes().item(0);
						value = distributeAction(item);
					}
					else if (child.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE){
						value = child.getChildNodes().item(0).getNodeValue();
					}
				}
			}
		}
		return value;
	}
}