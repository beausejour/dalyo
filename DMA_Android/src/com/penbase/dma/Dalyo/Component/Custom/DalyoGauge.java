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
	private String mFuncName;
	
	public DalyoGauge(Context context) {
		super(context);
	}

	public void setMinValue(int min) {
		this.mMinValue = min;
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
		// TODO Auto-generated method stub
		
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
		setValue(Integer.valueOf(text));
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
	public void setOnClickEvent(String functionName) {
		// TODO Auto-generated method stub
		
	}
}
