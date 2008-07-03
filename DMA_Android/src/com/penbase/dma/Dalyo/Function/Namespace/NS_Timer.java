package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_Timer {
	private static HashMap<Integer, Timer> timerMap = new HashMap<Integer, Timer>(); 
	
	public static int Start(NodeList params){
		int timerId = 0;
		final String callback = String.valueOf(getValue(params, ScriptAttribute.PARAMETER_NAME_CALLBACK, ScriptAttribute.STRING));
		int interval = Integer.valueOf(String.valueOf(getValue(params, ScriptAttribute.PARAMETER_NAME_INTERVAL, ScriptAttribute.PARAMETER_TYPE_INT)));
		Object delayed = getValue(params, ScriptAttribute.PARAMETER_NAME_DELAYED, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		long delayedValue;
		if ((delayed == null) || (!((Boolean)delayed).booleanValue())){
			delayedValue = 0;
		}
		else{
			delayedValue = interval;
		}
		Timer timer = new Timer();
		timerId = timer.hashCode();
		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				Function.createFunction(callback, null);
			}
		};
		timerMap.put(timerId, timer);
		timer.scheduleAtFixedRate(task, delayedValue, interval*1000);
		return timerId;
	}
	
	public static void Cancel(NodeList params){
		int timerId = Integer.valueOf(String.valueOf(getValue(params, ScriptAttribute.PARAMETER_NAME_TIMERID, ScriptAttribute.PARAMETER_TYPE_INT)));
		timerMap.get(timerId).cancel();
	}
	
	private static Object getValue(NodeList params, String name, String type){
		Object value = null;
		int paramsLen = params.getLength();
		for (int i=0; i<paramsLen; i++){
			Element element = (Element) params.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.getAttribute(ScriptTag.NAME).equals(name)) &&
					(element.getAttribute(ScriptTag.TYPE).equals(type))){
				if (element.getChildNodes().getLength() == 1){
					if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE){
						value = element.getChildNodes().item(0).getNodeValue();
					}
					else if (element.getChildNodes().item(0).getNodeType() == Node.ELEMENT_NODE){
						Element child = (Element)element.getChildNodes().item(0);
						if (child.getNodeName().equals(ScriptTag.KEYWORD)){
							value = Function.getKeyWord(child);
						}
						else if (child.getNodeName().equals(ScriptTag.VAR)){
							value = Function.getVariableValue(child.getAttribute(ScriptTag.NAME));
						}
					}
				}
			}
		}
		return value;
	}
}
