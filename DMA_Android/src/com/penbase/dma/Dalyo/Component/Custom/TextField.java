package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.AutoCompleteTextView;

public class TextField extends AutoCompleteTextView{	
	
	public TextField(Context context, Typeface tf, float fs) 
	{		
		super(context);
		this.setTypeface(tf);
		this.setTextSize(fs);
	}
}
