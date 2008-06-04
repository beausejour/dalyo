package com.penbase.dma.Dalyo;

public class Transaction {
	private int currentState = 0;
	private static final int START = 1;
	private static final int CANCEL = -1;
	//private static final int START = 1;
	
	public Transaction()
	{
		
	}
	
	public void startTransaction()
	{
		currentState = START;
	}
	
	public void cancelTransaction()
	{
		currentState = CANCEL;
	}
	
	public void commitTransaction()
	{
		
	}
	
	public int getState()
	{
		return currentState;
	}
}
