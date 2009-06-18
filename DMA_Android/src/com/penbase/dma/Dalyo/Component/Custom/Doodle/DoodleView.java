package com.penbase.dma.Dalyo.Component.Custom.Doodle;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class DoodleView extends Button {
	private String mImageName;
	private Context mContext;
	private String mId;
	
	public DoodleView(Context c, String i) {
		super(c);
		mContext = c;
		mId = i;
		mImageName = "";
		setText("Open Doodle");
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(mContext, Doodle.class);
				myIntent.putExtra("ID", mId);
				mContext.startActivity(myIntent);
			}
		});
	}
   
   public void setImageName(String s) {
	   mImageName = s;
   }
   
   public String getImageName() {
	   return mImageName;
   }
}
