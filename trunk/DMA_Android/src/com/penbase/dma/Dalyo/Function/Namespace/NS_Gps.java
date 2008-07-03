package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Gps;
import com.penbase.dma.Dalyo.Function.Function;
import android.content.Context;
import android.location.Location;

public class NS_Gps {
	private static Gps gps;
	
	public static int GetStatus(){
		return gps.GetStatus();
	}
	
	public static Object GetLogitude(NodeList params){
		Location location = (Location)getValue(params, ScriptAttribute.LOCATION, ScriptAttribute.LOCATION);
		return gps.getLogitude(location);
	}
	
	public static Object GetLatitude(NodeList params){
		Location location = (Location)getValue(params, ScriptAttribute.LOCATION, ScriptAttribute.LOCATION);
		return gps.getLatitude(location);
	}
	
	public static void Init(Context context){
		gps = new Gps(context);
	}
	
	public static void Stop(){
		gps.stop();
	}
	
	public static Location getLocation(){
		return gps.getLocation();
	}
	
	private static Object getValue(NodeList params, String name, String type){
		Object value = null;
		int paramsLen = params.getLength();
		for (int i=0; i<paramsLen; i++){
			Element element = (Element)params.item(i);
			if (element.getNodeName().equals(ScriptTag.PARAMETER)){
				if ((element.getAttribute(ScriptTag.NAME).equals(name)) &&
						(element.getAttribute(ScriptTag.TYPE).equals(type))){
					if (element.getChildNodes().getLength() == 1){
						if (element.getChildNodes().item(0).getNodeType() == Node.ELEMENT_NODE){
							Element child = (Element)element.getChildNodes().item(0);
							if (child.getNodeName().equals(ScriptTag.VAR)){
								value = Function.getVariableValue(child.getAttribute(ScriptTag.NAME));
							}
						}
					}
				}
			}
		}
		return value;
	}
}
