package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.EditText;

import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Dalyo.Component.DalyoComponent;

import java.util.HashMap;

public class DalyoTextZone extends EditText implements DalyoComponent {
	private String mTableId = "";
	private String mFieldId = "";

	public DalyoTextZone(Context context, Typeface tf, float fs) {
		super(context);
		this.setTypeface(tf);
		this.setTextSize(fs);
	}
	
	public void setTableId(String tid) {
		this.mTableId = tid;
	}
	
	public void setFieldId(String fid) {
		this.mFieldId = fid;
	}
	
	public String getTableId() {
		return mTableId;
	}
	
	public String getFieldId() {
		return mFieldId;
	}
	
	public void clear() {
		this.setText("");
	}
	
	public void refresh(HashMap<Object, Object> record) {
		if ((!getFieldId().equals("")) && (record != null)) {
			DalyoTextZone.this.setText((String)record.get(DatabaseAttribute.FIELD+getFieldId()));
		}
	}
	
	public String getValue() {
		return this.getText().toString();
	}
	
	public boolean isEmpty() {
		boolean result = false;
		if (this.getValue().trim().length() == 0) {
			result = true;
		}
		return result;
	}

	@Override
	public String getComponentLabel() {
		return getValue();
	}

	@Override
	public Object getComponentValue() {
		return getValue();
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
	public void setOnChangeEvent(String functionName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOnClickEvent(String functionName) {
		// TODO Auto-generated method stub
		
	}
}
