package com.penbase.dma.Dalyo.Component;

import java.util.ArrayList;
import java.util.HashMap;
import com.penbase.dma.Constant.XmlTag;
import com.penbase.dma.Dalyo.Component.Custom.*;
import com.penbase.dma.Dalyo.Component.Custom.Dataview.CustomLinearLayout;
import com.penbase.dma.Dalyo.Component.Custom.Dataview.DataView;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class Component{
	private Context context;
	private String id;
	private String type;
	private String name;
	private String fontSize;
	private String fontType;
	private View view = null;
	private String tableID = null;
	private String fieldID = null;
	
	//Variables for checkbox	
	private String checked = null;	

	//Variables for combobox
	private ArrayList<String> itemList = new ArrayList<String>();
	private ArrayList<String> labelList = null;
	private ArrayList<String> valueList = null;
	
	//Variable for image
	private int background = 0;
	private String extension = null;
	
	//Variable for dataview
	private ArrayList<ArrayList<String>> columnInfos = new ArrayList<ArrayList<String>>();
	private HashMap<Integer, String> onCalculateMap = new HashMap<Integer, String>();
	
	//Variable for Label
	private String align = null;
	
	//Variable for textfield
	private boolean editable;
	private String multiLine;
	
	//Variable for TimeField/DateField
	String dateTimeValue = null;

	public Component(Context c, String t) {
		this.context = c;
		this.type = t;		
	}
	
	public void setFontSize(String fs){
		this.fontSize = fs;
	}
	
	public void setFontType(String ft){
		this.fontType = ft;
	}
	
	public void setId(String i){
		this.id = i;
	}
	
	public void setBackGround(int bg){
		this.background = bg;
	}
	
	public void setAlign(String align){
		this.align = align;
	}
	
	public void setName(String n){
		this.name = n;
	}
		
	public void setTableId(String tid){
		this.tableID = tid;
	}
	
	public void setFieldId(String fid){
		this.fieldID = fid;	
	}
	
	public void setChecked(String check){
		this.checked = check;
	}
	
	public void setExtension(String ext){
		this.extension = ext;
	}
	
	public void setItemList(ArrayList<String> l){
		this.itemList = l;
	}
	
	public void setLabelList(ArrayList<String> l){
		this.labelList = l;
	}
	
	public void setValueList(ArrayList<String> v){
		this.valueList = v;
	}
	
	public void setDataviewColumns(ArrayList<ArrayList<String>> l){
		this.columnInfos = l;
	}
	
	public void setDataviewOncalculate(HashMap<Integer, String> onc){
		this.onCalculateMap = onc;
	}
	
	public void setMultiLine(String ml){
		this.multiLine = ml;
	}
	
	public void setEditable(boolean editable){
		this.editable = editable;
	}
	
	public void setDateTimeValue(String value){
		this.dateTimeValue = value;
	}
	
	public View getView() {
		return view;
	}
	
	public String getId() {
		return id;
	}
	
	public void setView() {
		if(type.equals(XmlTag.COMPONENT_BUTTON)) {
			Button button = new Button(context);
			button.setText(name);
			button.setTypeface(getFontType(fontType));
			button.setTextSize(getFontSize(fontSize));
			if (background != 0) {
				Drawable d = Drawable.createFromPath(DmaHttpClient.getFilesPath()+background+"."+extension);
				button.setBackground(d);
			}
			view = button;
		}
		else if(type.equals(XmlTag.COMPONENT_CHECKBOX)) {
			CheckBox checkbox = new CheckBox(context);
			checkbox.setText(name);
			checkbox.setTypeface(getFontType(fontType));
			checkbox.setTextSize(getFontSize(fontSize));
			if (checked.equals(true)) {
				checkbox.setChecked(true);
			}
			view = checkbox;
		}
		else if(type.equals(XmlTag.COMPONENT_COMBOBOX)) {
			ComboBox combobox;
			if ((valueList != null) && (labelList != null)) {
				combobox = new ComboBox(context, labelList, valueList);
			}
			else {
				combobox = new ComboBox(context, itemList);
			}
			view = combobox;
		}
		else if(type.equals(XmlTag.COMPONENT_LABEL)) {
			Label label = new Label(context, getFontType(fontType), getFontSize(fontSize));
			label.setText(name);
			if (align != null) {
				label.setAlignment(getAlign(align));
			}
			view = label;
		}
		else if(type.equals(XmlTag.COMPONENT_DATEFIELD)) {
			DateField datefield = new DateField(context, getFontType(fontType), getFontSize(fontSize), dateTimeValue);
			view = datefield;
		}
		else if(type.equals(XmlTag.COMPONENT_TIMEFIELD)) {
			TimeField timefield = new TimeField(context, getFontType(fontType), getFontSize(fontSize), dateTimeValue);
			view = timefield;
		}
		else if(type.equals(XmlTag.COMPONENT_TEXTFIELD)) {
			if (multiLine.equals("true")) {
				TextZone textzone = new TextZone(context, getFontType(fontType), getFontSize(fontSize));
				if ((tableID != null) && (fieldID != null)) {
					textzone.setTableId(tableID);
					textzone.setFieldId(fieldID);
				}
				view = textzone;
			}
			else {
				TextField textfield = new TextField(context, getFontType(fontType), getFontSize(fontSize));
				if ((tableID != null) && (fieldID != null)) {
					textfield.setTableId(tableID);
					textfield.setFieldId(fieldID);
				}
				view = textfield;
			}
			if (align != null) {
				((TextView)view).setAlignment(getAlign(align));
			}
			if (editable) {
				((TextView)view).setEnabled(!editable);
			}
		}
		else if(type.equals(XmlTag.COMPONENT_TEXTZONE)) {
			TextZone textzone = new TextZone(context, getFontType(fontType), getFontSize(fontSize));
			view = textzone;
		}
		else if(type.equals(XmlTag.COMPONENT_RADIOBUTTON)) {
			RadioButton radiobutton = new RadioButton(context);
			radiobutton.setText(name);
			radiobutton.setTypeface(getFontType(fontType));
			radiobutton.setTextSize(getFontSize(fontSize));
			view = radiobutton;
		}
		else if(type.equals(XmlTag.COMPONENT_NUMBERBOX)) {
			NumberBox numberbox = new NumberBox(context);
			view = numberbox;
		}
		else if(type.equals(XmlTag.COMPONENT_PICTUREBOX)) {
			Button image = new Button(context);
			image.setText("Picture");
			view = image;
		}
		else if(type.equals(XmlTag.COMPONENT_IMAGE)) {
			ImageView imageview = new ImageView(context);
			if (background != 0) {
				Drawable d = Drawable.createFromPath(DmaHttpClient.getFilesPath()+background+"."+extension);
				imageview.setBackground(d);
			}
			view = imageview;
		}
		else if(type.equals(XmlTag.COMPONENT_DATAVIEW)) {
			DataView dataview = new DataView(context, tableID);
			dataview.setText(getFontSize(fontSize), getFontType(fontType));
			dataview.setColumnInfo(columnInfos);
			dataview.setOncalculate(onCalculateMap);
			view = dataview;
		}
		else {
			Button button = new Button(context);
			button.setText("else");
			view = button;
		}
	}
	
	public void setOnclickFunction(final String funcName, final View view) {
		if (view instanceof DataView){
			((DataView)view).setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView parent, View v, int position, long id){
					((DataView)view).setCurrentPosition(position);
					Form.getFunction().createFunction(funcName, null);
				}
			});
		}
		else{
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Form.getFunction().createFunction(funcName, null);
				}
			});
		}
	}
	
	public void setOnchangeFunction(String funcName, View view) {
		Form.getFunction().createFunction(funcName, null);
	}
	
	private Alignment getAlign(String align) {
		Alignment alignment = null;
		if (align.equals("left")) {
			alignment = Alignment.ALIGN_NORMAL;
		}
		else if (align.equals("center")) {
			alignment = Alignment.ALIGN_CENTER;
		}
		else if (align.equals("right")) {
			alignment = Alignment.ALIGN_OPPOSITE;
		}
		return alignment;
	}
	
	private float getFontSize(String fs) {
		float fontSize = 12;
		if (fs.equals("small")) {
			fontSize = 10;
		}
		else if (fs.equals("big")) {
			fontSize = 14;
		}
		else if (fs.equals("extra")) {
			fontSize = 16;
		}
		return fontSize;
	}
	
	private Typeface getFontType(String ft) {
		Typeface fontType = Typeface.DEFAULT;
		if (ft.equals("italic")) {
			fontType = Typeface.DEFAULT_ITALIC;
		}
		else if (ft.equals("bold")) {
			fontType = Typeface.DEFAULT_BOLD;
		}
		else if (ft.equals("underline")) {
			//Underline text, bug of android
		}
		else if (ft.equals("italicbold")) {
			fontType = Typeface.DEFAULT_BOLD_ITALIC;
		}
		return fontType;
	}
	
	public void refreshComponentContent(Object filter) {
		if (getView() instanceof DataView) {
			((DataView)getView()).refresh(filter);
		}
		else if (getView() instanceof ComboBox) {
			((ComboBox)getView()).getData(filter);
		}
	}
	
	public HashMap<Object, Object> getRecord() {
		HashMap<Object, Object> result = null;
		if (getView() instanceof ComboBox) {
			result = ((ComboBox)getView()).getCurrentRecord();
		}
		if (getView() instanceof DataView) {
			result = ((DataView)getView()).getCurrentRecord();
		}
		return result;
	}
	
	public void setValue(Object value) {
		if (getView() instanceof NumberBox) {
			((NumberBox)getView()).setValue(value);
		}
	}
	
	public Object getValue() {
		Object result = null;
		if (getView() instanceof NumberBox) {
			result = ((NumberBox)getView()).getValue();
		}
		else if (getView() instanceof TextZone) {
			result = ((TextZone)getView()).getValue();
		}
		else if (getView() instanceof TextField) {
			result = ((TextField)getView()).getValue();
		}
		else if (getView() instanceof TimeField) {
			result = ((TimeField)getView()).getTime();
		}
		else if (getView() instanceof DateField) {
			result = ((DateField)getView()).getDate();
		}
		return result;
	}
	
	public void setText(String text) {
		((TextView)getView()).setText(text);
	}
	
	public void setEnabled(boolean state) {
		if (getView() instanceof Button){
			Log.i("info", "button setenabled "+state);
			((Button)getView()).setEnabled(state);
		}
	}
	
	public void setVisible(boolean state){
		if (state){
			getView().setVisibility(View.VISIBLE);
		}
		else{
			getView().setVisibility(View.INVISIBLE);
		}
	}
}