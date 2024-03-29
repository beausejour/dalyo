package com.penbase.dma.Dalyo.Function.Namespace;

import android.os.Handler;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class NS_Timer {
	private static HashMap<Integer, Timer> mTimerMap = new HashMap<Integer, Timer>();

	public static void Cancel(Element element) {
		if (mTimerMap.size() > 0) {
			Object object = Function.getValue(element, ScriptTag.PARAMETER,
					ScriptAttribute.PARAMETER_NAME_TIMERID,
					ScriptAttribute.PARAMETER_TYPE_INT);
			if (object != null) {
				int timerId = Integer.valueOf(object.toString());
				mTimerMap.get(timerId).cancel();
			}
		}
	}

	public static void cancelAll() {
		if (mTimerMap.size() > 0) {
			Set<Integer> keys = mTimerMap.keySet();
			for (int timerId : keys) {
				mTimerMap.get(timerId).cancel();
			}
		}
	}

	public static int Start(Element element) {
		int timerId = 0;
		final String callback = Function
				.getValue(element, ScriptTag.PARAMETER,
						ScriptAttribute.PARAMETER_NAME_CALLBACK,
						ScriptAttribute.STRING).toString();
		int interval = Integer.valueOf(Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_INTERVAL,
				ScriptAttribute.PARAMETER_TYPE_INT).toString());
		Object delayed = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_DELAYED,
				ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		long delayedValue;
		if ((delayed == null) || (!Boolean.parseBoolean(delayed.toString()))) {
			delayedValue = 0;
		} else {
			delayedValue = interval * 1000;
		}
		final Handler handler = new Handler();
		final Runnable timerRunnable = new Runnable() {
			@Override
			public void run() {
				Function.createFunction(callback);
			}
		};
		Timer timer = new Timer();
		timerId = timer.hashCode();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				handler.post(timerRunnable);
			}
		};
		mTimerMap.put(timerId, timer);
		timer.scheduleAtFixedRate(task, delayedValue, interval * 1000);
		return timerId;
	}
}
