package com.penbase.dma.Dalyo.Component.Custom.Doodle;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class DoodleView extends Button {
	private static String imageName;
	private Context context;
	private String id;
	
	public DoodleView(Context c, String i) {
		super(c);
		this.context = c;
		this.id = i;
		this.setText("Open Doodle");
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(context, Doodle.class);
				myIntent.putExtra("ID", id);
				context.startActivity(myIntent);
			}
		});
	}
   
   public void setImageName(String s) {
	   imageName = s;
   }
   
   public String getImageName() {
	   return imageName;
   }
}
