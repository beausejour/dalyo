package com.penbase.dma.Dalyo.BarcodeReader;

import com.penbase.dma.R;
import com.penbase.dma.Dalyo.Component.DalyoComponent;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

/**
 * Barcode component
 */
public class BarcodeComponent extends ImageButton implements DalyoComponent {
	private Context mContext;
	private String mId;
	private String mContent;
	private String mPath;

	public BarcodeComponent(Context context, String id) {
		super(context);
		mContext = context;
		mId = id;
		mPath = "";
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

	/**
	 * Removes previously barcode image 
	 */
	public void clear() {
		setImageResource(R.drawable.barcode);
		this.setScaleType(ScaleType.CENTER);
	}

	/**
	 * Sets barcode image's text content
	 * @param content barcode image's text content
	 */
	public void setContent(String content) {
		mContent = content;
	}

	/**
	 * Gets barcode image's text content
	 * @return barcode image's text content
	 */
	public String getContent() {
		return mContent;
	}

	/**
	 * Gets barcode image's path
	 * @return barcode image's path
	 */
	public String getImagePath() {
		return mPath;
	}

	/**
	 * Sets barcode image's path
	 * @param n barcode image's path
	 */
	public void setImagePath(String n) {
		mPath = n;
	}

	@Override
	public String getComponentLabel() {
		return getContent();
	}

	@Override
	public Object getComponentValue() {
		return getContent();
	}

	@Override
	public boolean isComponentEnabled() {
		return isEnabled();
	}

	@Override
	public boolean isComponentVisible() {
		if (getVisibility() == View.VISIBLE) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void resetComponent() {
		setImageResource(R.drawable.barcode);
	}

	@Override
	public void setComponentEnabled(boolean enable) {
		setEnabled(enable);
	}

	@Override
	public void setComponentFocus() {
		requestFocus();
	}

	@Override
	public void setComponentLabel(String label) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setComponentText(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setComponentValue(Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setComponentVisible(boolean visible) {
		if (visible) {
			setVisibility(View.VISIBLE);
		} else {
			setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void setOnChangeEvent(String functionName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOnClickEvent(String functionName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMinimumHeight() {
		return getSuggestedMinimumHeight();
	}

	@Override
	public int getMinimumWidth() {
		return getSuggestedMinimumWidth();
	}

	@Override
	public void setDatabase(String tid, String fid) {
		// TODO Auto-generated method stub
		
	}
}
