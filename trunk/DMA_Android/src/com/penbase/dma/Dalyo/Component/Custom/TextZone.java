package com.penbase.dma.Dalyo.Component.Custom;

import java.util.HashMap;
import com.penbase.dma.Dalyo.Database.DatabaseAdapter;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.EditText;

public class TextZone extends EditText{
	private String tableId = "";
	private String fieldId = "";

	public TextZone(Context context, Typeface tf, float fs){
		super(context);
		this.setTypeface(tf);
		this.setTextSize(fs);
	}
	
	public void setTableId(String tid){
		this.tableId = tid;
	}
	
	public void setFieldId(String fid){
		this.fieldId = fid;
	}
	
	public String getTableId(){
		return tableId;
	}
	
	public String getFieldId(){
		return fieldId;
	}
	
	public void refresh(HashMap<Object, Object> record){
		if (!getFieldId().equals("")){
			TextZone.this.setText((String)record.get(DatabaseAdapter.FIELD+getFieldId()));
		}
	}
	
	public String getValue() {
		return this.getText().toString();
	}
}
