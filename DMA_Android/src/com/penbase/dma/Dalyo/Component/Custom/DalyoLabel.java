package com.penbase.dma.Dalyo.Component.Custom;

import java.util.HashMap;

import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Dalyo.Component.DalyoComponent;
import com.penbase.dma.Dalyo.Function.Function;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

public class DalyoLabel extends TextView implements DalyoComponent {
	private String mLabel;
	private String mTableId = null;
	private String mFieldId = null;
	
	public DalyoLabel(Context context, String label, Typeface tf, float fs) {
		super(context);
		this.mLabel = label;
		this.setTypeface(tf);
		this.setTextSize(fs);
		this.setTextColor(Color.BLACK);
		setText(label);
	}

	@Override
	public String getComponentLabel() {
		return getText().toString();
	}

	@Override
	public Object getComponentValue() {
		return getText();
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
		setText(mLabel);
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
		setText(value.toString());
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
		addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
		this.mTableId = tid;
		this.mFieldId = fid;
	}
	
	public void refresh(HashMap<Object, Object> record) {
		if (mFieldId != null) {
			setText(record.get(DatabaseAttribute.FIELD + mFieldId).toString());
		}
	}
	
	public String getTableId() {
		return mTableId;
	}
	
	public String getFieldId() {
		return mFieldId;
	}
}
