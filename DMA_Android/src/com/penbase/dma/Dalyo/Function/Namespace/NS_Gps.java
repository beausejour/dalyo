package com.penbase.dma.Dalyo.Function.Namespace;

import android.content.Context;
import android.location.Location;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Gps;
import com.penbase.dma.Dalyo.Function.Function;

import org.w3c.dom.Element;

public class NS_Gps {
	private static Gps gps;
    
	public static Object GetAltitude(Element element) {
		Location location = (Location)Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.LOCATION, ScriptAttribute.LOCATION);
		if (location != null) {
			return location.getAltitude();
		} else {
			return null;
		}
	}
	
	public static Object GetLatitude(Element element) {
		Location location = (Location)Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.LOCATION, ScriptAttribute.LOCATION);
		if (location != null) {
			return location.getLatitude();
		} else {
			return null;
		}
	}
	
	public static Location GetLocation() {
		return gps.getLocation();
	}
	
	public static Object GetLogitude(Element element) {
		Location location = (Location)Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.LOCATION, ScriptAttribute.LOCATION);
		if (location != null) {
			return location.getLongitude();
		} else {
			return null;
		}
	}
	
	public static Object GetSpeed(Element element) {
		Location location = (Location)Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.LOCATION, ScriptAttribute.LOCATION);
		if (location != null) {
			return location.getSpeed();
		} else {
			return null;
		}
	}
	
	public static int GetStatus() {
		return gps.getStatus();
	}
	
	public static void Init(Context context) {
		gps = new Gps(context);
	}
	
	public static void Stop() {
		gps.stop();
	}
}
