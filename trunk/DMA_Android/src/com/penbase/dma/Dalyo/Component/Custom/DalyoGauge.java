package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;

import com.penbase.dma.Dalyo.Component.DalyoComponent;
import com.penbase.dma.Dalyo.Function.Function;

/**
 * Gauge object
 */
public class DalyoGauge extends SeekBar implements DalyoComponent {
	private int mMinValue;
	private int mMaxValue;
	private int mInitialValue;
	private String mFuncName;
	
	public DalyoGauge(Context context, int initial, int min, int max) {
		super(context);
		mInitialValue = initial;
		mMinValue = min;
		mMaxValue = max;
		setProgress(mInitialValue);
		setMax(mMaxValue);
	}
	
	public void setValue(int value) {
		this.setProgress(value);
	}

	@Override
	public void setComponentValue(Object value) {
		setValue(Integer.valueOf(value.toString()));
	}

	@Override
	public Object getComponentValue() {
		return this.getProgress() + mMinValue;
	}

	@Override
	public String getComponentLabel() {
		// TODO Auto-generated method stub
		return null;
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
		setProgress(mInitialValue);
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
		
	}

	@Override
	public void setComponentText(String text) {
		
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
		this.mFuncName = functionName;
		this.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromTouch) {
				Function.createFunction(mFuncName);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
		});
	}

	@Override
	public void setOnClickEvent(final String functionName) {
		//Seekbar doesn't have on click event
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
