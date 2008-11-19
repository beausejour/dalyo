package com.penbase.dma.Dalyo.Component.Custom.Doodle;

import android.content.Context;
import android.widget.Button;

public class DoodleView extends Button {
	private static String imageName;
	
	public DoodleView(Context context) {
		super(context);
	}
   
   public static void setImageName(String s) {
	   imageName = s;
   }
   
   public static String getImageName() {
	   return imageName;
   }
}
