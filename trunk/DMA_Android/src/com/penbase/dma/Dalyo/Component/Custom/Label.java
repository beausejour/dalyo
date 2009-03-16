package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

public class Label extends TextView{
	public Label(Context context, Typeface tf, float fs) {
		super(context);
		this.setTypeface(tf);
		this.setTextSize(fs);
		this.setTextColor(Color.BLACK);
	}
}
