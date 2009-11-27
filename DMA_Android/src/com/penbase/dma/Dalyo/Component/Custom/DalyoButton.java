package com.penbase.dma.Dalyo.Component.Custom;

import com.penbase.dma.Dalyo.Component.DalyoComponent;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

/**
 * Dalyo button component
 */
public class DalyoButton extends Button implements DalyoComponent {
	private String mInitalText = null;
	private boolean mHasBackground = false;

	public DalyoButton(Context context, String label, boolean hasBackground) {
		super(context);
		if (hasBackground) {
			setBackgroundDrawable(Drawable.createFromPath(label));
			setText(null);
		} else {
			setText(label);
		}
		mHasBackground = hasBackground;
		mInitalText = label;
	}
	
	@Override
	public String getComponentLabel() {
		return getText().toString();
	}

	@Override
	public Object getComponentValue() {
		return mInitalText;
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
		if (mHasBackground) {
			setBackgroundDrawable(Drawable.createFromPath(mInitalText));
			setText(null);	
		} else {
			setText(mInitalText);
		}
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
		setText(label);
	}

	@Override
	public void setComponentText(String text) {
		setText(text);
	}

	@Override
	public void setComponentValue(Object value) {
		if (value == null) {
			setText(null);
		} else {
			String text = value.toString();
			if (text.contains(DmaHttpClient.getResourcePath())) {
				setBackgroundDrawable(Drawable.createFromPath(text));
				setText(null);
			} else {
				setText(text);
			}	
		}
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
	public void setOnClickEvent(final String functionName) {
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Function.createFunction(functionName);
			}
		});
	}

	@Override
	public void setOnChangeEvent(String functionName) {
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
