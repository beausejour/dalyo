package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Gps;
import com.penbase.dma.Dalyo.Function.Function;
import android.content.Context;
import android.location.Location;

public class NS_Gps {
	private static Gps gps;
	
	public static Object GetLatitude(Element element){
		Location location = (Location)Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.LOCATION, ScriptAttribute.LOCATION);
		return gps.getLatitude(location);
	}
	
	public static Location GetLocation(){
		return gps.getLocation();
	}
	
	public static Object GetLogitude(Element element){
		Location location = (Location)Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.LOCATION, ScriptAttribute.LOCATION);
		return gps.getLogitude(location);
	}
	
	public static int GetStatus(){
		return gps.GetStatus();
	}
	
	public static void Init(Context context){
		gps = new Gps(context);
	}
	
	public static void Stop(){
		gps.stop();
	}
	

}
