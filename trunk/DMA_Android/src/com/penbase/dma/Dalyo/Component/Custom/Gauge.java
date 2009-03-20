package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.widget.SeekBar;

import com.penbase.dma.Dalyo.Function.Function;

public class Gauge extends SeekBar {
	private int mMinValue;
	private String mFuncName;
	
	public Gauge(Context context) {
		super(context);
	}

	public void setMinValue(int min) {
		this.mMinValue = min;
	}
	
	public int getValue() {
		return this.getProgress() + mMinValue;
	}
	
	public void setValue(int value) {
		this.setProgress(value);
	}
	
	public void setOnChangeFunction(String name) {
		this.mFuncName = name;
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
}
