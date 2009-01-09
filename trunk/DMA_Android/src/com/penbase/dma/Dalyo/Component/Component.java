package com.penbase.dma.Dalyo.Component;

import java.util.ArrayList;
import java.util.HashMap;

import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DesignTag;
import com.penbase.dma.Dalyo.Component.Custom.*;
import com.penbase.dma.Dalyo.Component.Custom.Dataview.DataView;
import com.penbase.dma.Dalyo.Component.Custom.Doodle.DoodleView;
import com.penbase.dma.Dalyo.Component.Custom.PictureBox.*;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class Component {
	private Context context;
	private String id;
	private String type;
	private String label;
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
	private String dateTimeValue = null;
	
	//Variable for Gauge
	private int minValue;
	private int maxValue;
	private int initialValue;

	public Component(Context c, String t) {
		this.context = c;
		this.type = t;		
	}
	
	public void setFontSize(String fs) {
		this.fontSize = fs;
	}
	
	public void setFontType(String ft) {
		this.fontType = ft;
	}
	
	public void setId(String i) {
		this.id = i;
	}
	
	public void setBackGround(int bg) {
		this.background = bg;
	}
	
	public void setAlign(String align) {
		this.align = align;
	}
	
	public void setLabel(String l) {
		this.label = l;
	}
		
	public void setTableId(String tid) {
		this.tableID = tid;
	}
	
	public void setFieldId(String fid) {
		this.fieldID = fid;	
	}
	
	public void setChecked(String check) {
		this.checked = check;
	}
	
	public void setExtension(String ext) {
		this.extension = ext;
	}
	
	public void setItemList(ArrayList<String> l) {
		this.itemList = l;
	}
	
	public void setLabelList(ArrayList<String> l) {
		this.labelList = l;
	}
	
	public void setValueList(ArrayList<String> v) {
		this.valueList = v;
	}
	
	public void setDataviewColumns(ArrayList<ArrayList<String>> l) {
		this.columnInfos = l;
	}
	
	public void setDataviewOncalculate(HashMap<Integer, String> onc) {
		this.onCalculateMap = onc;
	}
	
	public void setMultiLine(String ml) {
		this.multiLine = ml;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public void setDateTimeValue(String value) {
		this.dateTimeValue = value;
	}
	
	public void setInitValue(int i) {
		this.initialValue = i;
	}
	
	public void setMinValue(int min) {
		this.minValue = min;
	}

	public void setMaxValue(int max) {
		this.maxValue = max;
	}
	
	public View getView() {
		return view;
	}
	
	public String getId() {
		return id;
	}
	
	public void setView() {
		if(type.equals(DesignTag.COMPONENT_BUTTON)) {
			Button button = new Button(context);
			button.setText(label);
			button.setTypeface(getFontType(fontType));
			button.setTextSize(getFontSize(fontSize));
			if (background != 0) {
				Drawable d = Drawable.createFromPath(DmaHttpClient.getFilesPath()+background+"."+extension);
				button.setBackgroundDrawable(d);
			}
			view = button;
		}
		else if(type.equals(DesignTag.COMPONENT_CHECKBOX)) {
			CheckBox checkbox = new CheckBox(context);
			checkbox.setText(label);
			checkbox.setTypeface(getFontType(fontType));
			checkbox.setTextSize(getFontSize(fontSize));
			if (checked.equals(true)) {
				checkbox.setChecked(true);
			}
			view = checkbox;
		}
		else if(type.equals(DesignTag.COMPONENT_COMBOBOX)) {
			ComboBox combobox;
			if ((valueList != null) && (labelList != null)) {
				combobox = new ComboBox(context, labelList, valueList);
			}
			else {
				combobox = new ComboBox(context, itemList);
			}
			view = combobox;
		}
		else if(type.equals(DesignTag.COMPONENT_LABEL)) {
			Label labelObject = new Label(context, getFontType(fontType), getFontSize(fontSize));
			labelObject.setText(label);
			if (align != null) {
				labelObject.setGravity(getGravity(align));
			}
			view = labelObject;
		}
		else if(type.equals(DesignTag.COMPONENT_DATEFIELD)) {
			DateField datefield = new DateField(context, getFontType(fontType), getFontSize(fontSize), dateTimeValue);
			view = datefield;
		}
		else if(type.equals(DesignTag.COMPONENT_TIMEFIELD)) {
			TimeField timefield = new TimeField(context, getFontType(fontType), getFontSize(fontSize), dateTimeValue);
			view = timefield;
		}
		else if(type.equals(DesignTag.COMPONENT_TEXTFIELD)) {
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
				((TextView)view).setGravity(getGravity(align));
			}
			if (editable) {
				((TextView)view).setEnabled(!editable);
			}
		}
		else if(type.equals(DesignTag.COMPONENT_TEXTZONE)) {
			TextZone textzone = new TextZone(context, getFontType(fontType), getFontSize(fontSize));
			view = textzone;
		}
		else if(type.equals(DesignTag.COMPONENT_RADIOBUTTON)) {
			RadioButton radiobutton = new RadioButton(context);
			radiobutton.setText(label);
			radiobutton.setTypeface(getFontType(fontType));
			radiobutton.setTextSize(getFontSize(fontSize));
			view = radiobutton;
		}
		else if(type.equals(DesignTag.COMPONENT_NUMBERBOX)) {
			NumberBox numberbox = new NumberBox(context);
			view = numberbox;
		}
		else if(type.equals(DesignTag.COMPONENT_PICTUREBOX)) {
			PictureBoxView pictureBox = new PictureBoxView(context);
			pictureBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent myIntent = new Intent(context, PictureBox.class);
					context.startActivity(myIntent);
				}
			});
			view = pictureBox;
		}
		else if(type.equals(DesignTag.COMPONENT_IMAGE)) {
			ImageView imageview = new ImageView(context);
			if (background != 0) {
				Drawable d = Drawable.createFromPath(DmaHttpClient.getFilesPath()+background+"."+extension);
				imageview.setBackgroundDrawable(d);
			}
			view = imageview;
		}
		else if(type.equals(DesignTag.COMPONENT_DATAVIEW)) {
			DataView dataview = new DataView(context, tableID);
			dataview.setText(getFontSize(fontSize), getFontType(fontType));
			dataview.setColumnInfo(columnInfos);
			dataview.setOncalculate(onCalculateMap);
			view = dataview;
		}
		else if (type.equals(DesignTag.COMPONENT_DOODLE)) {
			DoodleView doodleView= new DoodleView(context, id);
			view = doodleView;
		}
		else if (type.equals(DesignTag.COMPONENT_GAUGE)) {
			Gauge gauge = new Gauge(context);
			gauge.setProgress(initialValue);
			gauge.setMax(maxValue);
			gauge.setMinValue(minValue);
			view = gauge;
		}
		else {
			Button button = new Button(context);
			button.setText(label);
			view = button;
		}
	}
	
	public void setOnclickFunction(final String funcName, final View view) {
		if (view instanceof DataView) {
			((DataView)view).getListView().setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					((DataView)view).setCurrentPosition(position);
					Function.createFunction(funcName);
				}

			});
		}
		else {
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Function.createFunction(funcName);
				}
			});
		}
	}
	
	public void setOnchangeFunction(String funcName, View view) {
		if (view instanceof Gauge) {
			((Gauge)view).setOnChangeFunction(funcName);
		}
		else if (view instanceof ComboBox) {
			((ComboBox)view).setOnChangeFunction(funcName);
		}
	}
	
	private int getGravity(String align) {
		int gravity = 0;
		if (align.equals(Constant.LEFT)) {
			gravity = Gravity.LEFT;
		}
		else if (align.equals(Constant.CENTER)) {
			gravity = Gravity.CENTER;
		}
		else if (align.equals(Constant.RIGHT)) {
			gravity = Gravity.RIGHT;
		}
		return gravity;
	}
	
	private float getFontSize(String fs) {
		float fontSize = 12;
		if (fs.equals(Constant.SMALL)) {
			fontSize = 10;
		}
		else if (fs.equals(Constant.BIG)) {
			fontSize = 14;
		}
		else if (fs.equals(Constant.EXTRA)) {
			fontSize = 16;
		}
		return fontSize;
	}
	
	private Typeface getFontType(String ft) {
		Typeface fontType = Typeface.DEFAULT;
		if (ft.equals(Constant.ITALIC)) {
			fontType = Typeface.create(Typeface.SERIF, Typeface.ITALIC);
		}
		else if (ft.equals(Constant.BOLD)) {
			fontType = Typeface.DEFAULT_BOLD;
		}
		else if (ft.equals(Constant.UNDERLINE)) {
			//Underline text, not implemented yet in android
		}
		else if (ft.equals(Constant.ITALICBOLD)) {
			fontType = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC);
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
		if (getView() instanceof TextField) {
			((TextField)getView()).setText(String.valueOf(value));
		}
		if (getView() instanceof Label) {
			((Label)getView()).setText(String.valueOf(value));
		}
		else if (getView() instanceof TextZone) {
			((TextZone)getView()).setText(String.valueOf(value));
		}
		else if (getView() instanceof TimeField) {
			((TimeField)getView()).setTime(String.valueOf(value));
		}
		else if (getView() instanceof DateField) {
			((DateField)getView()).setDate(String.valueOf(value));
		}
		else if (getView() instanceof Gauge) {
			((Gauge)getView()).setValue(Integer.valueOf(String.valueOf(value)));
		}
	}
	
	public Object getValue() {
		Object result = null;
		if (getView() instanceof NumberBox) {
			result = ((NumberBox)getView()).getValue();
		}
		else if (getView() instanceof Label) {
			result = ((Label)getView()).getText();
		}
		else if (getView() instanceof TextZone) {
			result = ((TextZone)getView()).getValue();
		}
		else if (getView() instanceof TimeField) {
			result = ((TimeField)getView()).getTime();
		}
		else if (getView() instanceof DateField) {
			result = ((DateField)getView()).getDate();
		}
		else if (getView() instanceof Gauge) {
			result = ((Gauge)getView()).getValue();
		}
		else if (getView() instanceof DoodleView) {
			result = ((DoodleView)getView()).getImageName();
		}
		else if (getView() instanceof ComboBox) {
			result = ((ComboBox)getView()).getValue();
		}
		return result;
	}
	
	public String getLabel() {
		String result = "";
		if (getView() instanceof Label) {
			result = ((Label)getView()).getText().toString();
		}
		else if (getView() instanceof TextZone) {
			result = ((TextZone)getView()).getValue();
		}
		else if (getView() instanceof TextField) {
			result = ((TextField)getView()).getValue();
		}
		else if (getView() instanceof ComboBox) {
			result = ((ComboBox)getView()).getLabel();
		}
		return result;
	}
	
	public void setText(String text) {
		String newText = "";
		if (!text.equals("null")) {
			newText = text;
		}
		if (getView() instanceof TextZone) {
			((TextZone)getView()).setText(newText);
		}
		else if (getView() instanceof Label) {
			((Label)getView()).setText(newText);
		}
		else if (getView() instanceof TextField) {
			((TextField)getView()).setText(newText);
		}
	}
	
	public void setEnabled(boolean state) {
		if (getView() instanceof Button) {
			((Button)getView()).setEnabled(state);
		}
	}
	
	public boolean isEnabled() {
		return getView().isEnabled();
	}
	
	public void setFocus() {
		if (getView().isFocusableInTouchMode()) {
			getView().requestFocus();
		}
	}
	
	public void setVisible(boolean state) {
		if (state) {
			getView().setVisibility(View.VISIBLE);
		}
		else {
			getView().setVisibility(View.INVISIBLE);
		}
	}
	
	public boolean isVisible() {
		if (getView().getVisibility() == View.VISIBLE) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void reSet() {
		setValue(label);
	}
}