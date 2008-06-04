package com.penbase.dma.Binary;

import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.util.Log;

public class Binary {
	public static int INTBYTE = 4;	
	public static int TYPEBYTE = 1;
	
	public Binary()
	{}
	
	public static int byteArrayToType(byte[] bytes)
	{
		int n = 0;
		n += ( (int) bytes[0] & 0xff) << 0;
	    return n;
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
		Log.i("info", "type string "+result);
		return result;
	}
	
	public static double byteArrayToDouble(byte[] bytes)
	{
		long n = 0;
	    n += ( (long) bytes[0] & 0xff) << 56;
	    n += ( (long) bytes[1] & 0xff) << 48;
	    n += ( (long) bytes[2] & 0xff) << 40;
	    n += ( (long) bytes[3] & 0xff) << 32;
	    n += ( (long) bytes[4] & 0xff) << 24;
	    n += ( (long) bytes[5] & 0xff) << 16;
	    n += ( (long) bytes[6] & 0xff) << 8;
	    n += ( (long) bytes[7] & 0xff) << 0;
		return Double.longBitsToDouble(n);
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
	
	public static long byteArrayToLong(byte[] bytes) 
	{
		long n = 0;
	    n += ( (long) bytes[0] & 0xff) << 24;
	    n += ( (long) bytes[1] & 0xff) << 16;
	    n += ( (long) bytes[2] & 0xff) << 8;
	    n += ( (long) bytes[3] & 0xff) << 0;
	    return n;
	}
	
	public static String byteArrayToTime(byte[] bytes) 
	{
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"), Locale.FRENCH);
		calendar.setTimeInMillis(byteArrayToLong(bytes)*1000);		
	    return (calendar.get(Calendar.HOUR)-1)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
	}
	
	public static String byteArrayToDate(byte[] bytes) 
	{
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"), Locale.FRENCH);
		calendar.setTimeInMillis(byteArrayToLong(bytes)*1000);
		return calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public static String byteArrayToDateTime(byte[] bytes) 
	{
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"), Locale.FRENCH);
		calendar.setTimeInMillis(byteArrayToLong(bytes)*1000);	
		return calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+" "+
		(calendar.get(Calendar.HOUR)+1)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
	}
	
	public static int byteArrayToBool(byte[] bytes) 
	{
		int n = 0;
	    n += ( (int) bytes[0] & 0xff) << 0;	    
	    return n;
	}
	
	public static Object byteArrayToObject(byte[] bytes, String type)
	{
		Object result = null;
		if ((type.equals("VARCHAR")) || (type.equals("KEY")) || (type.equals("CHAR")) || (type.equals("TEXT")))
		{
			result = byteArrayToString(bytes);			
			Log.i("info", "type "+type+" result "+result);
		}
		else if (type.equals("BOOLEAN"))
		{
			result = String.valueOf(byteArrayToBool(bytes));
			Log.i("info", "type "+type+" result "+result);
		}
		else if (type.equals("INTEGER")) 
		{
			result = String.valueOf(byteArrayToInt(bytes));	
			Log.i("info", "type "+type+" result "+result);
		}
		else if (type.equals("TIME"))
		{
			result = byteArrayToTime(bytes);
			Log.i("info", "type "+type+" result "+result);
		}
		else if (type.equals("DATE"))
		{
			result = byteArrayToDate(bytes);
			Log.i("info", "type "+type+" result "+result);
		}
		else if (type.equals("DATETIME"))
		{
			result = byteArrayToDateTime(bytes);
			Log.i("info", "type "+type+" result "+result);
		}		
		else if (type.equals("DOUBLE"))
		{
			result = String.valueOf(byteArrayToDouble(bytes));
			Log.i("info", "type "+type+" result "+result);
		}
		else if (type.equals("BLOB"))
		{
			result = new byte[bytes.length];
			System.arraycopy(bytes, 0, result, 0, bytes.length);			
			Log.i("info", "type "+type+" result "+bytes.length);
		}
		return result;
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
	
	public static  byte[] doubleToByteArray (double d) 
	{
		long n = Double.doubleToLongBits(d);
	    byte[] bytes = new byte[8];
	    bytes[0] = (byte) ( (n >> 56) & 0xff);
	    bytes[1] = (byte) ( (n >> 48) & 0xff);
	    bytes[2] = (byte) ( (n >> 40) & 0xff);
	    bytes[3] = (byte) ( (n >> 32) & 0xff);
	    bytes[4] = (byte) ( (n >> 24) & 0xff);
	    bytes[5] = (byte) ( (n >> 16) & 0xff);
	    bytes[6] = (byte) ( (n >> 8) & 0xff);
	    bytes[7] = (byte) (n & 0xff);
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
