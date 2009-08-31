package com.penbase.dma.Dalyo.Component.Custom.Dataview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Displays a cell of DataView
 */
public class CustomTextView extends TextView{
	private Paint mBorderPaint;
	private RectF mDrawRect;
	
	public CustomTextView(Context context) {
		super(context);
		this.setGravity(Gravity.LEFT);
		this.setTextColor(Color.BLACK);
		mBorderPaint = new Paint();
		mBorderPaint.setARGB(255, 223, 217, 217);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setStyle(Style.STROKE);
		mDrawRect = new RectF();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mDrawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
		canvas.drawRoundRect(mDrawRect, 0, 0, mBorderPaint);
	}
}
