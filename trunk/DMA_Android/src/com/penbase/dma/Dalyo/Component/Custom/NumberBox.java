package com.penbase.dma.Dalyo.Component.Custom;

import com.penbase.dma.R;

import android.content.Context;
import android.text.Spannable;
import android.text.method.NumberKeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NumberBox extends LinearLayout{
	private AutoCompleteTextView act;
	private ImageView view_up;
	private ImageView view_down;
	private int initialValue;
	private int maxValue;
	private int minValue;
	private static final char[] numberChars = {'1','2','3','4','5','6','7','8','9','0'};
	
	public NumberBox(Context c) {
		super(c);
		this.setGravity(Gravity.CENTER_VERTICAL);
		act = new AutoCompleteTextView(c);
		act.setGravity(Gravity.CENTER);
		act.setEnabled(false);
        act.setKeyListener(new NumberKeyListener() {
			@Override
			protected char[] getAcceptedChars() {
				return numberChars;
			}

			@Override
			protected int lookup(KeyEvent event, Spannable content) {
				if (hasChar(event.getDisplayLabel())) {
					String oldValue = "";
					if (act.getText().toString().length() > 0) {
						oldValue = act.getText().toString();
					}
					int newValue = Integer.valueOf(oldValue + "" + event.getDisplayLabel());
					if (newValue < minValue) {
						act.setText(String.valueOf(minValue));
					}
					else if (newValue > maxValue) {
						act.setText(String.valueOf(maxValue));
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
				if (Integer.valueOf(act.getText().toString()) < maxValue) {
					int value = Integer.valueOf(act.getText().toString()) + 1;
					act.setText(String.valueOf(value));
				}
			}
		});
		
		view_down = new ImageView(c);
		view_down.setImageResource(R.drawable.minus);
		view_down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Integer.valueOf(act.getText().toString()) > minValue) {
					int value = Integer.valueOf(act.getText().toString()) - 1;
					act.setText(String.valueOf(value));
				}
			}
		});
		

		this.addView(act, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, 90));
		this.addView(view_up, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		this.addView(view_down, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
	}
	
    private boolean hasChar(char c) {
    	int numbers = numberChars.length;
    	boolean result = false;
    	int i = 0;
    	while (i < numbers) {
    		if (numberChars[i] == c) {
    			result = true;
    			i = numbers;
    		}
    		i++;
    	}
    	return result;
    }
	
	public String getValue() {
		return act.getText().toString();
	}
	
	public void setValue(Object v) {
		act.setText(v.toString());
	}
	
	public void setInitialValue(int value) {
		this.initialValue = value;
		act.setText(String.valueOf(initialValue));
	}
	
	public void setMaxValue(int value) {
		this.maxValue = value;
	}
	
	public void setMinValue(int value) {
		this.minValue = value;
	}
}
