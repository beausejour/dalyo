package com.penbase.dma.Dalyo.Database;

import java.util.Random;

/**
 * Generates random value for database type key
 */
public class KeyGenerator {
	private static Random sRandom = new Random();
	
	public static String getKeyGenerated() {
		StringBuffer result = new StringBuffer("");
		for (int i=0; i<16; i++) {
			if (sRandom.nextInt(2) == 0) {
				result.append(getAlphabet());
			} else {
				result.append(getNumeric());
			}
			if ((((i+1) % 4) == 0) && (i != 15)) {
				result.append("-");
			}
		}
		return result.toString();
	}
	
	private static char getAlphabet() {
		int range = 26;
		return (char)('A'+sRandom.nextInt(range));
	}
	
	private static char getNumeric() {
		int range = 10;
		return (char)('0'+sRandom.nextInt(range));
	}
	
	public static String getDefaultKey() {
		return "0000-0000-0000-0000";
	}
}
