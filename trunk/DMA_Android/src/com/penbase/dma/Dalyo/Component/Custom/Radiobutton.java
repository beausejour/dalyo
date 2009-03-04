package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class Radiobutton extends LinearLayout {
	private RadioButton rb;
	private TextView tv;

	public Radiobutton(Context context) {
		super(context);
		rb = new RadioButton(context);
		tv = new TextView(context);
		tv.setGravity(Gravity.CENTER);
		this.addView(rb, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
		this.addView(tv, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	public TextView getTextView() {
		return tv;
	}
	
	public boolean isSelected() {
		return rb.isChecked();
	}
}
