package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.EditText;

public class TextZone extends EditText{

	public TextZone(Context context, Typeface tf, float fs) 
	{
		super(context);			
		this.setTypeface(tf);
		this.setTextSize(fs);
	}

}
