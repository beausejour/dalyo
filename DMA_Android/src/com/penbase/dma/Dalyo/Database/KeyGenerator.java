package com.penbase.dma.Dalyo.Database;

import java.util.Random;

public class KeyGenerator {
	private static Random random = new Random();
	
	public static String getKeyGenerated(){
		String result = "";
		for (int i=0; i<16; i++){
			if (random.nextInt(2) == 0){
				result += getAlphabet();
			}
			else{
				result += getNumeric();
			}
			if ((((i+1) % 4) == 0) && (i != 15)){
				result += "-";
			}
		}
		return result;
	}
	
	private static char getAlphabet(){
		int range = 26;
		return (char)('A'+random.nextInt(range));
	}
	
	private static char getNumeric(){
		int range = 10;
		return (char)('0'+random.nextInt(range));
	}
	
	public static String getDefaultKey(){
		return "0000-0000-0000-0000";
	}
}
