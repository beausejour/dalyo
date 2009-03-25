package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.penbase.dma.R;

public class Radiobutton extends LinearLayout {
	private RadioButton mRb;
	private TextView mTv;

	public Radiobutton(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.radiobutton, this, true);
		mRb = (RadioButton)findViewById(R.id.mRb);
		mTv = (TextView)findViewById(R.id.mTv);
	}
	
	public TextView getTextView() {
		return mTv;
	}
	
	public boolean isSelected() {
		return mRb.isChecked();
	}
}
