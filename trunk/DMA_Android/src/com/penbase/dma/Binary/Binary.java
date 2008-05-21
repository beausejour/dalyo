package com.penbase.dma.Binary;

import java.io.UnsupportedEncodingException;

public class Binary {
	public static int INTBYTE = 4;
	public static int TYPEBYTE = 1;
	
	public Binary()
	{
		
	}
	
	public static String byteArrayToString(byte[] bytes)
	{
		String result = null;
		try 
		{
			result = new String(bytes, "UTF-8");
		}
		catch (UnsupportedEncodingException e) 
		{e.printStackTrace();}
		return result;
	}
	
	public static int byteArrayToInt(byte[] bytes) 
	{
		int n = 0;
	    n += ( (int) bytes[0] & 0xff) << 24;
	    n += ( (int) bytes[1] & 0xff) << 16;
	    n += ( (int) bytes[2] & 0xff) << 8;
	    n += ( (int) bytes[3] & 0xff) << 0;
	    return n;
	}
	
	public static int byteArrayToType(byte[] bytes)
	{
		int n = 0;
		n += ( (int) bytes[0] & 0xff) << 0;
	    return n;
	}
	
	public static  byte[] intToByteArray (int n) 
	{
	    byte[] bytes = new byte[4];
	    bytes[0] = (byte) ( (n >> 24) & 0xff);
	    bytes[1] = (byte) ( (n >> 16) & 0xff);
	    bytes[2] = (byte) ( (n >> 8) & 0xff);
	    bytes[3] = (byte) (n & 0xff);
	    return bytes;
	}
	
	public static byte[] byteArrayForInt(byte[] bytes)
	{
		byte[] result = new byte[INTBYTE];
		System.arraycopy(bytes, 0, result, 0, result.length);
		return result;
	}
	
	public static byte[] byteArrayForType(byte[] bytes)
	{
		byte[] result = new byte[TYPEBYTE];
		System.arraycopy(bytes, 0, result, 0, result.length);
		return result;
	}
	
	public static byte[] byteArrayForValue(byte[] bytes, int length)
	{
		byte[] result = new byte[length];
		System.arraycopy(bytes, 0, result, 0, length);
		return result;
	}
	
	public static byte[] cutByteArray(byte[] bytes, int nb)
	{
		int length = bytes.length-nb;
		byte[] result = new byte[length];
		System.arraycopy(bytes, nb, result, 0, length);
		return result;
	}
}
