package com.penbase.dma.Binary;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import android.util.Log;

public class Binary {
	public static int INTBYTE = 4;	
	public static int TYPEBYTE = 1;
	
	public static int byteArrayToType(byte[] bytes){
		int n = 0;
		n += ( (int) bytes[0] & 0xff) << 0;
	    return n;
	}
	
	public static int byteArrayToInt(byte[] bytes){
		int n = 0;
		n += ( (int) bytes[0] & 0xff) << 24;
		n += ( (int) bytes[1] & 0xff) << 16;
		n += ( (int) bytes[2] & 0xff) << 8;
		n += ( (int) bytes[3] & 0xff) << 0;
		return n;
	}
	
	private static String byteArrayToString(byte[] bytes){
		String result = null;
		try{
			result = new String(bytes, "UTF-8");
		}
		catch (UnsupportedEncodingException e) 
		{e.printStackTrace();}
		return result;
	}
	
	private static double byteArrayToDouble(byte[] bytes){
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
	
	private static long byteArrayToLong(byte[] bytes){
		long n = 0;
		n += ( (long) bytes[0] & 0xff) << 24;
		n += ( (long) bytes[1] & 0xff) << 16;
		n += ( (long) bytes[2] & 0xff) << 8;
		n += ( (long) bytes[3] & 0xff) << 0;
		return n;
	}
	
	private static String byteArrayToTime(byte[] bytes){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"), Locale.FRENCH);
		calendar.setTimeInMillis(byteArrayToLong(bytes)*1000);
		return (calendar.get(Calendar.HOUR)-1)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
	}
	
	private static String byteArrayToDate(byte[] bytes){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"), Locale.FRENCH);
		calendar.setTimeInMillis(byteArrayToLong(bytes)*1000);
		return calendar.get(Calendar.DAY_OF_MONTH)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR);
	}
	
	private static String byteArrayToDateTime(byte[] bytes){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"), Locale.FRENCH);
		calendar.setTimeInMillis(byteArrayToLong(bytes)*1000);
		return calendar.get(Calendar.DAY_OF_MONTH)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR)+
		(calendar.get(Calendar.HOUR)+1)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
	}
	
	private static int byteArrayToBool(byte[] bytes){
		int n = 0;
		n += ( (int) bytes[0] & 0xff) << 0;
		return n;
	}
	
	public static Object byteArrayToObject(byte[] bytes, String type){
		Object result = null;
		if ((type.equals("VARCHAR")) || (type.equals("KEY")) || (type.equals("CHAR")) || (type.equals("TEXT"))){
			result = byteArrayToString(bytes);
		}
		else if (type.equals("BOOLEAN")){
			result = String.valueOf(byteArrayToBool(bytes));
		}
		else if (type.equals("INTEGER")){
			result = String.valueOf(byteArrayToInt(bytes));	
		}
		else if (type.equals("TIME")){
			result = byteArrayToTime(bytes);
		}
		else if (type.equals("DATE")){
			result = byteArrayToDate(bytes);
		}
		else if (type.equals("DATETIME")){
			result = byteArrayToDateTime(bytes);
		}		
		else if (type.equals("DOUBLE")){
			result = String.valueOf(byteArrayToDouble(bytes));
		}
		else if (type.equals("BLOB")){
			result = new byte[bytes.length];
			System.arraycopy(bytes, 0, result, 0, bytes.length);
		}
		return result;
	}
	
	public static  byte[] intToByteArray (int n){
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ( (n >> 24) & 0xff);
		bytes[1] = (byte) ( (n >> 16) & 0xff);
		bytes[2] = (byte) ( (n >> 8) & 0xff);
		bytes[3] = (byte) (n & 0xff);
		return bytes;
	}
	
	private static  byte[] doubleToByteArray (double d){
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
	
	public static byte[] stringToByteArray (String s){
		return s.getBytes();
	}
	
	private static byte[] boolToByteArray(int n){
		byte[] bytes = new byte[1];
		bytes[0] = (byte) (n & 0xff);
		return bytes;
	}
	
	public static byte[] typeToByteArray(int n){
		byte[] bytes = new byte[1];
		bytes[0] = (byte) (n & 0xff);
		return bytes;
	}
	
	private static  byte[] longToByteArray (long n){
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ( (n >> 24) & 0xff);
		bytes[1] = (byte) ( (n >> 16) & 0xff);
		bytes[2] = (byte) ( (n >> 8) & 0xff);
		bytes[3] = (byte) (n & 0xff);
		return bytes;
	}
	
	private static  byte[] dateToByteArray (String d){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(d.split("/")[0]));
		calendar.set(Calendar.MONTH, Integer.valueOf(d.split("/")[1])-1);
		calendar.set(Calendar.YEAR, Integer.valueOf(d.split("/")[2]));
		return longToByteArray(calendar.getTimeInMillis()/1000);
	}
	
	private static  byte[] timeToByteArray (String d){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(d.split("/")[0]));
		calendar.set(Calendar.MINUTE, Integer.valueOf(d.split("/")[1]));
		return longToByteArray(calendar.getTimeInMillis()/1000);
	}
	
	public static byte[] objectToByteArray (Object value, String type){
		Log.i("info", "value "+value+" type "+type);
		byte[] result = null;
		if ((type.equals("VARCHAR")) || (type.equals("KEY")) || (type.equals("CHAR")) || (type.equals("TEXT"))){
			if (value == null){
				result = stringToByteArray("");	
			}
			else{
				result = stringToByteArray(String.valueOf(value));
			}
		}
		else if (type.equals("BOOLEAN")){
			result = boolToByteArray(Integer.valueOf(String.valueOf(value)));
		}
		else if (type.equals("INTEGER")){
			if (value == null){
				result = intToByteArray(0);
			}
			else{
				result = intToByteArray(Integer.valueOf(String.valueOf(value)));
			}
		}
		else if (type.equals("TIME")){
			result = timeToByteArray(String.valueOf(value));
		}
		else if (type.equals("DATE")){
			result = dateToByteArray(String.valueOf(value));
		}
		else if (type.equals("DATETIME")){
			result = dateToByteArray(String.valueOf(value));
		}		
		else if (type.equals("DOUBLE")){
			if (value == null){
				result = intToByteArray(0);
			}
			else{
				result = doubleToByteArray(Double.valueOf(String.valueOf(value)));
			}
		}
		else if (type.equals("BLOB")){
			if (value == null){
				result = stringToByteArray("");	
			}
		}
		return result;
	}
}
