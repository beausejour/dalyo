package com.penbase.dma.Dalyo.Component.Custom.PictureBox;

import com.penbase.dma.R;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

public class PictureBoxView extends ImageButton {
	private Context context;
	private String id;
	private String photoName;
	
	public PictureBoxView(Context c, String i) {
		super(c);
		this.context = c;
		this.id = i;
		this.photoName = "";
		this.setImageResource(R.drawable.camera);
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(context, PictureBox.class);
				myIntent.putExtra("ID", id);
				context.startActivity(myIntent);
			}
		});
	}
	
	public void clear() {
		this.setImageResource(R.drawable.camera);
	}
	
	public void setPhotoName(String name) {
		photoName = name;
	}
	
	public String getPhotoName() {
		return photoName;
	}
}
