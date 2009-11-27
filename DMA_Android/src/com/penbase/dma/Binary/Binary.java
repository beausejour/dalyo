package com.penbase.dma.Binary;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Manages all the conversion from byte array to different objects and inverse
 */
public class Binary {
	/**
	 * Converts a byte array to synchronize type
	 * @param bytes byte array
	 * @return integer value which represents synchronization's type
	 */
	public static int byteArrayToType(byte[] bytes) {
		int n = 0;
		n += ((int) bytes[0] & 0xff) << 0;
		return n;
	}

	/**
	 * Converts a byte array to an integer value
	 * @param bytes a byte array
	 * @return an integer value
	 */
	public static int byteArrayToInt(byte[] bytes) {
		int n = 0;
		n += ((int) bytes[0] & 0xff) << 24;
		n += ((int) bytes[1] & 0xff) << 16;
		n += ((int) bytes[2] & 0xff) << 8;
		n += ((int) bytes[3] & 0xff) << 0;
		return n;
	}

	/**
	 * Converts a byte array to a string
	 * @param bytes a byte array
	 * @return a string
	 */
	private static String byteArrayToString(byte[] bytes) {
		String result = null;
		try {
			result = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Converts a byte array to a double value
	 * @param bytes a byte array
	 * @return a double value
	 */
	private static double byteArrayToDouble(byte[] bytes) {
		long n = 0;
		n += ((long) bytes[0] & 0xff) << 56;
		n += ((long) bytes[1] & 0xff) << 48;
		n += ((long) bytes[2] & 0xff) << 40;
		n += ((long) bytes[3] & 0xff) << 32;
		n += ((long) bytes[4] & 0xff) << 24;
		n += ((long) bytes[5] & 0xff) << 16;
		n += ((long) bytes[6] & 0xff) << 8;
		n += ((long) bytes[7] & 0xff) << 0;
		return Double.longBitsToDouble(n);
	}

	/**
	 * Converts a byte array to a long value
	 * @param bytes a byte array
	 * @return a long value
	 */
	private static long byteArrayToLong(byte[] bytes) {
		long n = 0;
		n += ((long) bytes[0] & 0xff) << 24;
		n += ((long) bytes[1] & 0xff) << 16;
		n += ((long) bytes[2] & 0xff) << 8;
		n += ((long) bytes[3] & 0xff) << 0;
		return n;
	}

	/**
	 * Converts a byte array to a time format string
	 * @param bytes a byte array
	 * @return a time format string
	 */
	private static String byteArrayToTime(byte[] bytes) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"),
				Locale.FRENCH);
		calendar.setTimeInMillis(byteArrayToLong(bytes) * 1000);
		StringBuffer result = new StringBuffer(String.valueOf((calendar
				.get(Calendar.HOUR) - 1)));
		result.append(":");
		result.append(calendar.get(Calendar.MINUTE));
		result.append(":");
		result.append(calendar.get(Calendar.SECOND));
		return result.toString();
	}

	/**
	 * Converts a byte array to a date format string
	 * @param bytes a byte array
	 * @return a date format string
	 */
	private static String byteArrayToDate(byte[] bytes) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"),
				Locale.FRENCH);
		calendar.setTimeInMillis(byteArrayToLong(bytes) * 1000);
		StringBuffer result = new StringBuffer(String.valueOf(calendar
				.get(Calendar.DAY_OF_MONTH)));
		result.append("/");
		result.append((calendar.get(Calendar.MONTH) + 1));
		result.append("/");
		result.append(calendar.get(Calendar.YEAR));
		return result.toString();
	}

	/**
	 * Converts a byte array to date and time format string
	 * @param bytes a byte array
	 * @return date and time format string
	 */
	private static String byteArrayToDateTime(byte[] bytes) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"),
				Locale.FRENCH);
		calendar.setTimeInMillis(byteArrayToLong(bytes) * 1000);
		StringBuffer result = new StringBuffer(String.valueOf(calendar
				.get(Calendar.DAY_OF_MONTH)));
		result.append("/");
		result.append((calendar.get(Calendar.MONTH) + 1));
		result.append("/");
		result.append(calendar.get(Calendar.YEAR));
		result.append(" ");
		result.append((calendar.get(Calendar.HOUR) + 1));
		result.append(":");
		result.append(calendar.get(Calendar.MINUTE));
		result.append(":");
		result.append(calendar.get(Calendar.SECOND));
		return result.toString();
	}

	/**
	 * Converts a byte array to a boolean value
	 * @param bytes a byte array
	 * @return boolean value
	 */
	private static int byteArrayToBool(byte[] bytes) {
		int n = 0;
		n += ((int) bytes[0] & 0xff) << 0;
		return n;
	}

	/**
	 * Converts a byte array to a given type's value
	 * @param bytes a byte array
	 * @param type object's type
	 * @return object's value
	 */
	public static Object byteArrayToObject(byte[] bytes, String type) {
		Object result = null;
		if ((type.equals("VARCHAR")) || (type.equals("KEY"))
				|| (type.equals("CHAR")) || (type.equals("TEXT"))
				|| (type.equals("BLOB"))) {
			result = byteArrayToString(bytes);
		} else if (type.equals("BOOLEAN")) {
			result = String.valueOf(byteArrayToBool(bytes));
		} else if (type.equals("INTEGER")) {
			result = String.valueOf(byteArrayToInt(bytes));
		} else if (type.equals("TIME")) {
			result = byteArrayToTime(bytes);
		} else if (type.equals("DATE")) {
			result = byteArrayToDate(bytes);
		} else if (type.equals("DATETIME")) {
			result = byteArrayToDateTime(bytes);
		} else if (type.equals("DOUBLE")) {
			result = String.valueOf(byteArrayToDouble(bytes));
		}
		return result;
	}

	/**
	 * Converts an integer value to a byte array
	 * @param n an integer value
	 * @return a byte array
	 */
	public static byte[] intToByteArray(int n) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ((n >> 24) & 0xff);
		bytes[1] = (byte) ((n >> 16) & 0xff);
		bytes[2] = (byte) ((n >> 8) & 0xff);
		bytes[3] = (byte) (n & 0xff);
		return bytes;
	}

	/**
	 * Converts a double value to a byte array
	 * @param d a double value
	 * @return a byte array
	 */
	private static byte[] doubleToByteArray(double d) {
		long n = Double.doubleToLongBits(d);
		byte[] bytes = new byte[8];
		bytes[0] = (byte) ((n >> 56) & 0xff);
		bytes[1] = (byte) ((n >> 48) & 0xff);
		bytes[2] = (byte) ((n >> 40) & 0xff);
		bytes[3] = (byte) ((n >> 32) & 0xff);
		bytes[4] = (byte) ((n >> 24) & 0xff);
		bytes[5] = (byte) ((n >> 16) & 0xff);
		bytes[6] = (byte) ((n >> 8) & 0xff);
		bytes[7] = (byte) (n & 0xff);
		return bytes;
	}

	/**
	 * Converts a string value to a byte array
	 * @param s a string
	 * @return a byte array
	 */
	public static byte[] stringToByteArray(String s) {
		return s.getBytes();
	}

	/**
	 * Converts a boolean value to a byte array
	 * @param n a boolean value
	 * @return a byte array
	 */
	private static byte[] boolToByteArray(int n) {
		byte[] bytes = new byte[1];
		bytes[0] = (byte) (n & 0xff);
		return bytes;
	}

	/**
	 * Converts synchronize type to a byte array
	 * @param n synchronize type
	 * @return a byte array
	 */
	public static byte[] typeToByteArray(int n) {
		byte[] bytes = new byte[1];
		bytes[0] = (byte) (n & 0xff);
		return bytes;
	}

	/**
	 * Converts a long value to a byte array
	 * @param n a long value
	 * @return a byte array
	 */
	private static byte[] longToByteArray(long n) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ((n >> 24) & 0xff);
		bytes[1] = (byte) ((n >> 16) & 0xff);
		bytes[2] = (byte) ((n >> 8) & 0xff);
		bytes[3] = (byte) (n & 0xff);
		return bytes;
	}

	/**
	 * Converts a date and time format string to a byte array
	 * @param d a date and time format string
	 * @return a byte array
	 */
	private static byte[] dateTimeToByteArray(String d) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(d.split(" ")[0]
				.split("/")[0]));
		calendar.set(Calendar.MONTH, Integer
				.valueOf(d.split(" ")[0].split("/")[1]) - 1);
		calendar.set(Calendar.YEAR, Integer
				.valueOf(d.split(" ")[0].split("/")[2]));

		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(d.split(" ")[1]
				.split(":")[0]));
		calendar.set(Calendar.MINUTE, Integer.valueOf(d.split(" ")[1]
				.split(":")[1]));
		calendar.set(Calendar.SECOND, Integer.valueOf(d.split(" ")[1]
				.split(":")[2]));
		return longToByteArray(calendar.getTimeInMillis() / 1000);
	}

	/**
	 * Converts a date format string to a byte array
	 * @param d a date format string
	 * @return a byte array
	 */
	private static byte[] dateToByteArray(String d) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(d.split("/")[0]));
		calendar.set(Calendar.MONTH, Integer.valueOf(d.split("/")[1]) - 1);
		calendar.set(Calendar.YEAR, Integer.valueOf(d.split("/")[2]));
		return longToByteArray(calendar.getTimeInMillis() / 1000);
	}

	/**
	 * Converts a time format string to a byte array
	 * @param d a time format string
	 * @return a byte array
	 */
	private static byte[] timeToByteArray(String d) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(d.split(":")[0]));
		calendar.set(Calendar.MINUTE, Integer.valueOf(d.split(":")[1]));
		return longToByteArray(calendar.getTimeInMillis() / 1000);
	}

	/**
	 * Converts a given type's value to a byte array
	 * @param value object's value
	 * @param type a given type
	 * @return a byte array
	 */
	public static byte[] objectToByteArray(Object value, String type) {
		byte[] result = null;
		if ((type.equals("VARCHAR")) || (type.equals("KEY"))
				|| (type.equals("CHAR")) || (type.equals("TEXT"))) {
			if (value == null) {
				result = stringToByteArray("");
			} else {
				result = stringToByteArray(value.toString());
			}
		} else if (type.equals("BOOLEAN")) {
			result = boolToByteArray(Integer.valueOf(value.toString()));
		} else if (type.equals("INTEGER")) {
			if (value == null) {
				result = intToByteArray(0);
			} else {
				result = intToByteArray(Integer.valueOf(value.toString()));
			}
		} else if (type.equals("TIME")) {
			result = timeToByteArray(value.toString());
		} else if (type.equals("DATE")) {
			result = dateToByteArray(value.toString());
		} else if (type.equals("DATETIME")) {
			result = dateTimeToByteArray(value.toString());
		} else if (type.equals("DOUBLE")) {
			if (value == null) {
				result = intToByteArray(0);
			} else {
				result = doubleToByteArray(Double.valueOf(value.toString()));
			}
		} else if (type.equals("BLOB")) {
			if (value == null) {
				result = stringToByteArray("");
			} else {
				result = stringToByteArray(value.toString());
			}
		}
		return result;
	}
}
