package com.penbase.dma.Dalyo.Component.Custom.PictureBox;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import com.penbase.dma.R;

public class PictureBoxView extends ImageButton {
	private Context mContext;
	private String mId;
	private String mPhotoName;
	
	public PictureBoxView(Context c, String i) {
		super(c);
		this.mContext = c;
		this.mId = i;
		this.mPhotoName = "";
		this.setImageResource(R.drawable.camera);
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(mContext, PictureBox.class);
				myIntent.putExtra("ID", mId);
				mContext.startActivity(myIntent);
			}
		});
	}
	
	public void clear() {
		this.setImageResource(R.drawable.camera);
		this.setScaleType(ScaleType.CENTER);
	}
	
	public void setPhotoName(String name) {
		mPhotoName = name;
	}
	
	public String getPhotoName() {
		return mPhotoName;
	}
}
