package com.penbase.dma.Dalyo.Component;

public interface DalyoComponent {
	//Namespace Component's methods
	String getComponentLabel();
	Object getComponentValue();
	boolean isComponentVisible();
	boolean isComponentEnabled();
	void setComponentEnabled(boolean enable);
	void resetComponent();
	void setComponentFocus();
	void setComponentLabel(String label);
	void setComponentText(String text);
	void setComponentValue(Object value);
	void setComponentVisible(boolean visible);
	
	//Events
	void setOnClickEvent(String functionName);
	/**
	 * Calls a given function when user change the current item
	 * @param functionName which will be called
	 */
	void setOnChangeEvent(String functionName);
	
	//Database
	void setDatabase(String tid, String fid);
	
	//Component default size
	int getMinimumHeight();
	int getMinimumWidth();
}