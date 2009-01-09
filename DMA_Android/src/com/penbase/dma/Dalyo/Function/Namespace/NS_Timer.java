package com.penbase.dma.Dalyo.Function.Namespace;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.w3c.dom.Element;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

public class NS_Timer {
	private static HashMap<Integer, Timer> timerMap = new HashMap<Integer, Timer>(); 
	
	public static void Cancel(Element element) {
		int timerId = Integer.valueOf(String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_TIMERID, ScriptAttribute.PARAMETER_TYPE_INT)));
		timerMap.get(timerId).cancel();
	}
	
	public static int Start(Element element) {
		int timerId = 0;
		final String callback = String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_CALLBACK, ScriptAttribute.STRING));
		int interval = Integer.valueOf(String.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_INTERVAL, ScriptAttribute.PARAMETER_TYPE_INT)));
		Object delayed = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_DELAYED, ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		long delayedValue;
		if ((delayed == null) || (!((Boolean)delayed).booleanValue())) {
			delayedValue = 0;
		}
		else {
			delayedValue = interval;
		}
		Timer timer = new Timer();
		timerId = timer.hashCode();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Function.createFunction(callback);
			}
		};
		timerMap.put(timerId, timer);
		timer.scheduleAtFixedRate(task, delayedValue, interval*1000);
		return timerId;
	}
}
