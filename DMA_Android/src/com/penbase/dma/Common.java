package com.penbase.dma;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/**
 * Commons methods used in project
 */
public class Common {
	/**
	 * Get bytes data of a file
	 * @param file
	 * @return an array of byte
	 * @throws IOException
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	/**
	 * Return a hexadecimal string for a given byte array.
	 * 
	 * @param binary a byte array
	 * @return the hexadecimal string
	 */
	public static String md5HexStringFromBytes(byte[] binary) {
		java.security.MessageDigest messageDigest = null;
		try {
			messageDigest = java.security.MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		messageDigest.update(binary, 0, binary.length);

		char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		String hex = "";
		int msb;
		int lsb = 0;
		byte[] bytes = messageDigest.digest();
		int length = bytes.length;
		// MSB maps to idx 0
		for (int i = 0; i < length; i++) {
			msb = ((int) bytes[i] & 0x000000FF) / 16;
			lsb = ((int) bytes[i] & 0x000000FF) % 16;
			hex = hex + hexChars[msb] + hexChars[lsb];
		}

		return hex.toUpperCase();
	}

	/**
	 * Convert a string to MD5 value
	 * @param string input value
	 * @return MD5 value
	 */
	public static String md5(String string) {
		java.security.MessageDigest messageDigest = null;
		try {
			messageDigest = java.security.MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		messageDigest.update(string.getBytes(), 0, string.length());
		return new BigInteger(1, messageDigest.digest()).toString(16);
	}

	/**
	 * Save bytes data in file
	 * @param bytes a byte array
	 * @param filePath file path
	 * @param isImage if save in image file
	 */
	public static void streamToFile(byte[] bytes, String filePath,
			boolean isImage) {
		File file = new File(filePath);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			if (isImage) {
				DataOutputStream dos = new DataOutputStream(fos);
				dos.write(bytes);
				dos.close();
			} else {
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
						fos), 8 * 1024);
				out.write(new String(bytes, "UTF8"));
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
