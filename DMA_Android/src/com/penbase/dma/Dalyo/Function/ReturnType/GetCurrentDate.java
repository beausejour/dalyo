package com.penbase.dma.Dalyo.Function.ReturnType;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.w3c.dom.NodeList;

public class GetCurrentDate {
	Object currentDate = null;
	public static final String DATE_FORMAT_NOW = "dd-MM-yyyy";

	
	public GetCurrentDate(NodeList params)
	{
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	    currentDate = sdf.format(cal.getTime());
	}
	
	public Object getDate()
	{
		return currentDate;
	}
}
