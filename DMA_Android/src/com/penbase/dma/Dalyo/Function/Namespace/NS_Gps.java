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

	public static Object CreateLocation(Element element) {
		double latitude = 0;
		double longitude = 0;
		double altitude = 0;
		double speed = 0;
		Object latitudeObject = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_LATITUDE,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object longitudeObject = Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_LONGITUDE,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object altitudeObject = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_ALTITUDE,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object speedObject = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_SPEED,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		if (latitudeObject != null) {
			latitude = Double.parseDouble(latitudeObject.toString());
		}
		if (longitudeObject != null) {
			longitude = Double.parseDouble(longitudeObject.toString());
		}
		if (altitudeObject != null) {
			altitude = Double.parseDouble(altitudeObject.toString());
		}
		if (speedObject != null) {
			speed = Double.parseDouble(speedObject.toString());
		}
		return Gps.createLocation(latitude, longitude, altitude, speed);
	}

	public static Object GetAltitude(Element element) {
		Location location = (Location) Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.LOCATION,
				ScriptAttribute.LOCATION);
		if (location != null) {
			return location.getAltitude();
		} else {
			return null;
		}
	}

	public static Object GetDistance(Element element) {
		Location location1 = (Location) Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_LOCATION1,
				ScriptAttribute.LOCATION);
		Location location2 = (Location) Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_LOCATION2,
				ScriptAttribute.LOCATION);
		return location1.distanceTo(location2);
	}

	public static Object GetLatitude(Element element) {
		Location location = (Location) Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.LOCATION,
				ScriptAttribute.LOCATION);
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
		Location location = (Location) Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.LOCATION,
				ScriptAttribute.LOCATION);
		if (location != null) {
			return location.getLongitude();
		} else {
			return null;
		}
	}

	public static Object GetSpeed(Element element) {
		Location location = (Location) Function.getValue(element,
				ScriptTag.PARAMETER, ScriptAttribute.LOCATION,
				ScriptAttribute.LOCATION);
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
		if (gps != null) {
			gps.stop();
		}
	}
}
