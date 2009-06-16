package com.penbase.dma.Dalyo.Component.Custom;

import com.penbase.dma.R;
import com.penbase.dma.Dalyo.BarcodeReader.CaptureActivity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

public class Barcode extends ImageButton {
	private Context mContext;
	private String mId;
	private String mContent;

	public Barcode(Context context, String id) {
		super(context);
		mContext = context;
		mId = id;
		setTag(mId);
		setImageResource(R.drawable.barcode);
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(mContext, CaptureActivity.class);
				myIntent.putExtra("ID", mId);
				mContext.startActivity(myIntent);	
			}
		});
	}

	public void clear() {
		setImageResource(R.drawable.barcode);
		this.setScaleType(ScaleType.CENTER);
	}
	
	public void setContent(String content) {
		mContent = content;
	}
	
	public String getContent() {
		return mContent;
	}
}
