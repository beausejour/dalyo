package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.penbase.dma.Dalyo.Component.DalyoComponent;
import com.penbase.dma.Dalyo.Function.Function;

public class DalyoCheckBox extends CheckBox implements DalyoComponent {
	private String mInitialText;
	private boolean mInitialState;

	public DalyoCheckBox(Context context, String text, boolean state) {
		super(context);
		mInitialText = text;
		mInitialState = state;
		setText(text);
		setChecked(state);
	}

	@Override
	public String getComponentLabel() {
		return getText().toString();
	}

	@Override
	public Object getComponentValue() {
		return isChecked();
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
		setText(mInitialText);
		setChecked(mInitialState);
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
		boolean checked = false;
		if (value != null) {
			checked = Boolean.parseBoolean(value.toString());
		}
		setChecked(checked);
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
	public void setOnChangeEvent(final String functionName) {
		setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Function.createFunction(functionName);
			}
		});
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
