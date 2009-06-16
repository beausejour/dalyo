package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.Function.DateTime.DalyoDate;
import com.penbase.dma.Dalyo.Function.DateTime.Time;

import org.w3c.dom.Element;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NS_Date {
	public static Object AddMinutes(Element element) {
		Object date = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.DATE, ScriptAttribute.DATE);
		int minutes = Integer.valueOf(Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_MINUTES, ScriptAttribute.PARAMETER_TYPE_INT).toString());
		return ((DalyoDate)date).addMinutes(minutes);
	}
	
	public static Object CurrentDate() {
		return new DalyoDate();
	}
	
	public static Object CurrentHour() {
		return new Time();
	}
	
	public static String Format(Element element) {
		Object date = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.DATE, ScriptAttribute.DATE);
		String format = Function.getValue(element, ScriptTag.PARAMETER, ScriptAttribute.PARAMETER_NAME_FORMAT, ScriptAttribute.STRING).toString();
		
		String tempPattern = format;
		ArrayList<Integer> indexArray = new ArrayList<Integer>();
		int patternLength = format.length();
		for (int i=0; i<patternLength; i++) {
			if (tempPattern.charAt(i) == '\\') {
				indexArray.add(i);
			}
		}

		int indexArraySize = indexArray.size();
		if (indexArraySize > 0) {
			for (int i=0; i<indexArraySize; i++) {
				if (indexArray.get(i) + 1 < format.length()) {
					tempPattern = tempPattern.replace(tempPattern.charAt(indexArray.get(i) + 1), ' ');
				}
			}
		}
		
		Format formatter = new SimpleDateFormat(tempPattern);
		StringBuffer formatedDate = new StringBuffer(formatter.format(date));
		
		for (int i=0; i<indexArraySize; i++) {
			if (indexArray.get(i) + 1 < format.length()) {
				StringBuffer newString = new StringBuffer("");
				newString.append(format.charAt(indexArray.get(i) + 1));
				formatedDate = formatedDate.replace(indexArray.get(i) + 1, indexArray.get(i) + 2, newString.toString());
			}
		}
		
		return formatedDate.toString().replace("\\", "");
	}
}
