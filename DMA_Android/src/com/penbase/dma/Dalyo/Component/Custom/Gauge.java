package com.penbase.dma.Dalyo.Component.Custom;

import com.penbase.dma.Dalyo.Function.Function;

import android.content.Context;
import android.widget.SeekBar;

public class Gauge extends SeekBar {
	private int minValue;
	private String funcName;
	
	public Gauge(Context context) {
		super(context);
	}

	public void setMinValue(int min) {
		this.minValue = min;
	}
	
	public int getValue() {
		return this.getProgress() + minValue;
	}
	
	public void setOnChangeFunction(String name) {
		this.funcName = name;
		this.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromTouch) {
				Function.createFunction(funcName);
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
