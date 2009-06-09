package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.text.Spannable;
import android.text.method.NumberKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.penbase.dma.R;

public class NumberBox extends LinearLayout{
	private AutoCompleteTextView mAct;
	private ImageView mView_up;
	private ImageView mView_down;
	private int mInitialValue;
	private int mMaxValue;
	private int mMinValue;
	private static final char[] NUMBERCHARS = {'1','2','3','4','5','6','7','8','9','0'};
	
	public NumberBox(Context context) {
		super(context);
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.numberbox, this, true);
		
		mAct = (AutoCompleteTextView)findViewById(R.id.act);
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
					if (newValue < mMinValue) {
						mAct.setText(String.valueOf(mMinValue));
					} else if (newValue > mMaxValue) {
						mAct.setText(String.valueOf(mMaxValue));
					}
				}
				return super.lookup(event, content);
			}

			@Override
			public int getInputType() {
				// TODO Auto-generated method stub
				return 0;
			}
        });
		
		mView_up = (ImageView)findViewById(R.id.up);
		mView_up.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Integer.valueOf(mAct.getText().toString()) < mMaxValue) {
					int value = Integer.valueOf(mAct.getText().toString()) + 1;
					mAct.setText(String.valueOf(value));
				}
			}
		});
		
		mView_down = (ImageView)findViewById(R.id.down);
		mView_down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Integer.valueOf(mAct.getText().toString()) > mMinValue) {
					int value = Integer.valueOf(mAct.getText().toString()) - 1;
					mAct.setText(String.valueOf(value));
				}
			}
		});
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
		this.mInitialValue = value;
		mAct.setText(String.valueOf(mInitialValue));
	}
	
	public void setMaxValue(int value) {
		this.mMaxValue = value;
	}
	
	public void setMinValue(int value) {
		this.mMinValue = value;
	}
}
