package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.text.Spannable;
import android.text.method.NumberKeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.penbase.dma.R;

public class NumberBox extends LinearLayout{
	private AutoCompleteTextView mAct;
	private ImageView view_up;
	private ImageView view_down;
	private int initialValue;
	private int maxValue;
	private int minValue;
	private static final char[] NUMBERCHARS = {'1','2','3','4','5','6','7','8','9','0'};
	
	public NumberBox(Context c) {
		super(c);
		this.setGravity(Gravity.CENTER_VERTICAL);
		mAct = new AutoCompleteTextView(c);
		mAct.setGravity(Gravity.CENTER);
		mAct.setEnabled(false);
		mAct.setKeyListener(new NumberKeyListener() {
			@Override
			protected char[] getAcceptedChars() {
				return NUMBERCHARS;
			}

			@Override
			protected int lookup(KeyEvent event, Spannable content) {
				if (hasChar(event.getDisplayLabel())) {
					String oldValue = "";
					if (mAct.getText().toString().length() > 0) {
						oldValue = mAct.getText().toString();
					}
					int newValue = Integer.valueOf(oldValue + "" + event.getDisplayLabel());
					if (newValue < minValue) {
						mAct.setText(String.valueOf(minValue));
					} else if (newValue > maxValue) {
						mAct.setText(String.valueOf(maxValue));
					}
				}
				return super.lookup(event, content);
			}
        });
		
		view_up = new ImageView(c);
		view_up.setImageResource(R.drawable.plus);
		view_up.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Integer.valueOf(mAct.getText().toString()) < maxValue) {
					int value = Integer.valueOf(mAct.getText().toString()) + 1;
					mAct.setText(String.valueOf(value));
				}
			}
		});
		
		view_down = new ImageView(c);
		view_down.setImageResource(R.drawable.minus);
		view_down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Integer.valueOf(mAct.getText().toString()) > minValue) {
					int value = Integer.valueOf(mAct.getText().toString()) - 1;
					mAct.setText(String.valueOf(value));
				}
			}
		});
		

		this.addView(mAct, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, 90));
		this.addView(view_up, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		this.addView(view_down, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
	}
	
    private boolean hasChar(char c) {
    	int numbers = NUMBERCHARS.length;
    	boolean result = false;
    	int i = 0;
    	while (i < numbers) {
    		if (NUMBERCHARS[i] == c) {
    			result = true;
    			i = numbers;
    		}
    		i++;
    	}
    	return result;
    }
	
	public String getValue() {
		return mAct.getText().toString();
	}
	
	public void setValue(Object v) {
		mAct.setText(v.toString());
	}
	
	public void setInitialValue(int value) {
		this.initialValue = value;
		mAct.setText(String.valueOf(initialValue));
	}
	
	public void setMaxValue(int value) {
		this.maxValue = value;
	}
	
	public void setMinValue(int value) {
		this.minValue = value;
	}
}
