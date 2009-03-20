package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.AutoCompleteTextView;

import com.penbase.dma.Constant.DatabaseAttribute;

import java.util.HashMap;

public class TextField extends AutoCompleteTextView {
	private String mTableId = "";
	private String mFieldId = "";
	
	public TextField(Context context, Typeface tf, float fs) {
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
	
	public void refresh(HashMap<Object, Object> record) {
		if (!getFieldId().equals("")) {
			TextField.this.setText((String)record.get(DatabaseAttribute.FIELD+getFieldId()));
		}
	}
	
	public void clear() {
		this.setText("");
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
}
