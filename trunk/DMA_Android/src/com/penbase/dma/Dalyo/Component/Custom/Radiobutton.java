package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class Radiobutton extends LinearLayout {
	private RadioButton mRb;
	private TextView mTv;

	public Radiobutton(Context context) {
		super(context);
		mRb = new RadioButton(context);
		mTv = new TextView(context);
		mTv.setGravity(Gravity.CENTER);
		this.addView(mRb, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
		this.addView(mTv, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	public TextView getTextView() {
		return mTv;
	}
	
	public boolean isSelected() {
		return mRb.isChecked();
	}
}
