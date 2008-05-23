package com.penbase.dma.Dalyo.Component.Custom.Dataview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.widget.TextView;

public class CustomTextView extends TextView{
	private Paint borderPaint;
	
	public CustomTextView(Context context) {
		super(context);
		
		 borderPaint = new Paint();
         borderPaint.setARGB(255, 255, 255, 255);
         borderPaint.setAntiAlias(true);
         borderPaint.setStyle(Style.STROKE);         
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		 RectF drawRect = new RectF();
	     drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
	     canvas.drawRoundRect(drawRect, 0, 0, borderPaint); 
	}
	
}
