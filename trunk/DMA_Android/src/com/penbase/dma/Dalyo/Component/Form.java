package com.penbase.dma.Dalyo.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.AbsoluteLayout;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Dalyo.Component.Custom.ComboBox;
import com.penbase.dma.Dalyo.Component.Custom.TextField;
import com.penbase.dma.Dalyo.Component.Custom.TextZone;
import com.penbase.dma.Dalyo.Component.Custom.Doodle.DoodleView;
import com.penbase.dma.Dalyo.Function.Function;

public class Form extends AbsoluteLayout{
	private String tableId;
	private String title;
	private ArrayList<String> menuItemNameList;
	private ArrayList<String> menuItemOnClickList;
	
	public Form(Context context) {		
		super(context);
		menuItemNameList = new ArrayList<String>();
		menuItemOnClickList = new ArrayList<String>();
		this.setLayoutParams(new AbsoluteLayout.LayoutParams(LayoutParams.FILL_PARENT, 
				LayoutParams.FILL_PARENT, 0, 0));
	}
	
	public void onLoad(String name) {
		//Function.createFunction(name, null);
		Function.createFunction(name);
	}
	
	public ArrayList<String> getMenuItemNameList() {
		return menuItemNameList;
	}
	
	public void setMenuItemNameList(ArrayList<String> nameList) {
		menuItemNameList = nameList;
	}
	
	public ArrayList<String> getMenuItemOnClickList() {
		return menuItemOnClickList;
	}
	
	public void setMenuItemOnClickList(ArrayList<String> onClickList) {
		menuItemOnClickList = onClickList;
	}
	
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}
	
	public String getTableId() {
		return tableId;
	}
	
	public void setTitle(String t) {
		this.title = t;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setRecord(String formId, HashMap<Object, Object> record) {
		Log.i("info", "setrecord in form");
		int viewLen = this.getChildCount();
		for (int i=0; i<viewLen; i++) {
			if (this.getChildAt(i) instanceof ComboBox) {
				((ComboBox)this.getChildAt(i)).setCurrentValue(formId, record);
			}
			else if (this.getChildAt(i) instanceof TextField) {
				((TextField)this.getChildAt(i)).refresh(record);
			}
			else if (this.getChildAt(i) instanceof TextZone) {
				((TextZone)this.getChildAt(i)).refresh(record);
			}
		}
	}
	
	public void refresh(HashMap<Object, Object> record) {
		if (record.size() > 0) {
			int viewLen = this.getChildCount();
			for (int i=0; i<viewLen; i++) {
				if (this.getChildAt(i) instanceof TextField) {
					((TextField)this.getChildAt(i)).refresh(record);
				}
				else if (this.getChildAt(i) instanceof TextZone) {
					((TextZone)this.getChildAt(i)).refresh(record);
				}
			}
		}
	}
	
	public void setPreview() {
		int viewLen = this.getChildCount();
		for (int i=0; i<viewLen; i++) {
			if (this.getChildAt(i) instanceof DoodleView) {
				String path = Constant.PACKAGENAME+((DoodleView)this.getChildAt(i)).getImageName();
				File file = new File(path);
				if (file.exists()) {
					((DoodleView)this.getChildAt(i)).setText("");
					((DoodleView)this.getChildAt(i)).setBackgroundDrawable(Drawable.createFromPath(path));
				}
			}
			//check picturebox
		}
	}
}
