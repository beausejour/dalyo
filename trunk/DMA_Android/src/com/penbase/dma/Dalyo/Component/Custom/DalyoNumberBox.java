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
import com.penbase.dma.Dalyo.Component.DalyoComponent;

public class DalyoNumberBox extends LinearLayout implements DalyoComponent {
	private AutoCompleteTextView mAct;
	private ImageView mView_up;
	private ImageView mView_down;
	private int mInitialValue;
	private int mMaxValue;
	private int mMinValue;
	private int mStepValue;
	private static final char[] NUMBERCHARS = { '1', '2', '3', '4', '5', '6',
			'7', '8', '9', '0' };

	public DalyoNumberBox(Context context, int initial, int step) {
		super(context);
		this.mInitialValue = initial;
		this.mStepValue = step;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.numberbox, this, true);

		mAct = (AutoCompleteTextView) findViewById(R.id.act);
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
					int newValue = Integer.valueOf(oldValue + ""
							+ event.getDisplayLabel());
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
				return 0;
			}
		});

		mView_up = (ImageView) findViewById(R.id.up);
		mView_up.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Integer.valueOf(mAct.getText().toString()) < mMaxValue) {
					int value = Integer.valueOf(mAct.getText().toString())
							+ mStepValue;
					if (value <= mMaxValue) {
						mAct.setText(String.valueOf(value));
					}
				}
			}
		});

		mView_down = (ImageView) findViewById(R.id.down);
		mView_down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Integer.valueOf(mAct.getText().toString()) > mMinValue) {
					int value = Integer.valueOf(mAct.getText().toString())
							- mStepValue;
					if (value >= mMinValue) {
						mAct.setText(String.valueOf(value));	
					}
				}
			}
		});
		
		mAct.setText(String.valueOf(mInitialValue));
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

	public void setMaxValue(int value) {
		this.mMaxValue = value;
	}

	public void setMinValue(int value) {
		this.mMinValue = value;
	}

	@Override
	public String getComponentLabel() {
		return mAct.getText().toString();
	}

	@Override
	public Object getComponentValue() {
		return mAct.getText().toString();
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
		mAct.setText(String.valueOf(mInitialValue));
	}

	@Override
	public void setComponentEnabled(boolean enable) {
		mView_up.setEnabled(enable);
		mView_down.setEnabled(enable);
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
		mAct.setText(value.toString());
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
