package com.penbase.dma.Dalyo.Component;

import java.util.*;

import com.penbase.dma.Dalyo.Component.Custom.ComboBox;
import com.penbase.dma.Dalyo.Component.Custom.DateField;
import com.penbase.dma.Dalyo.Component.Custom.Label;
import com.penbase.dma.Dalyo.Component.Custom.NumberBox;
import com.penbase.dma.Dalyo.Component.Custom.TextField;
import com.penbase.dma.Dalyo.Component.Custom.TextZone;
import com.penbase.dma.Dalyo.Component.Custom.TimeField;
import com.penbase.dma.Dalyo.Component.Custom.Dataview.DataView;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.XmlElement.XmlTag;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;


public class Component{
	private Context context;
	private String id;
	private String type;
	private String name;
	private String fontSize;
	private String fontType;
	private View view = null;
	private Function function;
	private String tableID = null;
	private String fieldID = null;
	
	//Variables for checkbox	
	private String checked;	

	//Variables for combobox
	private ArrayList<String> itemList = null;
	private ArrayList<String> labelList = null;
	private ArrayList<String> valueList = null;
	
	//Variable for image
	private int background;
	private String extension;
	
	//Variable for dataview
	private ArrayList<ArrayList<String>> columnInfos = new ArrayList<ArrayList<String>>();	
	
	//Variable for Label
	private boolean hasAlign;
	private String align;
	
	//Variable for textfield
	private boolean editable;
	private String multiLine;
	
	public Component(Context c, String t, String i, String n, String fs, String ft)
	{
		this.context = c;
		this.id = i;
		this.type = t;
		this.name = n;
		this.fontSize = fs;
		this.fontType = ft;
		setView();
		function = new Function(context);
		Log.i("info", "function in component "+context);
	}
	
	//Constructor for button
	public Component(Context c, String t, String i, String n, String fs, String ft, int bg, String ext)
	{
		this.context = c;
		this.id = i;
		this.type = t;
		this.name = n;
		this.fontSize = fs;
		this.fontType = ft;
		this.background = bg;
		this.extension = ext;
		setView();
		function = new Function(context);
	}
	
	//Constructor for combobox with a list of items
	public Component(Context c, String t, String i, ArrayList<String> l, String fs, String ft)
	{
		this.context = c;
		this.id = i;
		this.type = t;
		this.itemList = l;
		this.fontSize = fs;
		this.fontType = ft;
		setView();
		function = new Function(context);
	}
	
	//Constructor for combobox with 2 list of table id
	public Component(Context c, String t, String i, ArrayList<String> l, ArrayList<String> v, String fs, String ft)
	{
		this.context = c;
		this.id = i;
		this.type = t;
		this.labelList = l;
		this.valueList = v;
		this.fontSize = fs;
		this.fontType = ft;
		setView();
		function = new Function(context);
	}
	
	//Constructor for label
	public Component(Context c, String t, String i, String n, String fs, String ft, String align, boolean hasalign)
	{
		this.context = c;
		this.id = i;
		this.type = t;
		this.name = n;
		this.fontSize = fs;
		this.fontType = ft;
		this.align = align;
		this.hasAlign = hasalign;
		setView();
		function = new Function(context);
	}
	
	//Constructor for textfield
	public Component(Context c, String t, String i, String fs, String ft, String align, boolean hasalign,
			String ml, boolean editable, String tid, String fid)
	{
		this.context = c;
		this.id = i;
		this.type = t;
		this.fontSize = fs;
		this.fontType = ft;
		this.align = align;
		this.hasAlign = hasalign;
		this.multiLine = ml;
		this.editable = editable;
		this.tableID = tid;
		this.fieldID = fid;
		setView();
		function = new Function(context);
	}
	
	//Constructor for checkbox
	public Component(Context c, String t, String i, String n, String fs, String ft, String check)
	{
		this.context = c;
		this.id = i;
		this.type = t;
		this.name = n;
		this.fontSize = fs;
		this.fontType = ft;
		this.checked = check;
		setView();
		function = new Function(context);
	}
	
	//Constructor for image
	public Component(Context c, String t, int bg, String ext)
	{
		this.context = c;
		this.type = t;
		this.background = bg;
		this.extension = ext;
		setView();
		function = new Function(context);
	}
	
	//Constructor for dataview
	public Component(Context c, String t, ArrayList<ArrayList<String>> l, String i, String fs, String ft, String tid)
	{
		this.context = c;
		this.type = t;
		this.columnInfos = l;
		this.id = i;
		this.fontSize = fs;
		this.fontType = ft;
		this.tableID = tid;		
		setView();
		function = new Function(context);
	}
	
	public View getView()
	{
		return view;
	}
	
	public String getId()
	{
		return id;
	}
	
	/*public String getType()
	{
		return type;
	}*/
	
	private void setView()
	{
		if(type.equals(XmlTag.TAG_COMPONENT_BUTTON))
		{
			Log.i("info", "button");
			Button button = new Button(context);			
			button.setText(name);
			button.setTypeface(setFontType(fontType));
			button.setTextSize(setFontSize(fontSize));
			if (background != 0)
			{				
				Drawable d = Drawable.createFromPath("data/misc/location/"+background+"."+extension);
				Log.i("info", "button background "+"data/misc/location/"+background+"."+extension);				
				button.setBackground(d);
			}
			view = button;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_CHECKBOX))
		{
			Log.i("info", "CHECKBOX "+context);
			CheckBox checkbox = new CheckBox(context);			
			checkbox.setText(name);
			checkbox.setTypeface(setFontType(fontType));
			checkbox.setTextSize(setFontSize(fontSize));
			if (checked.equals(true))
			{
				checkbox.setChecked(true);
			}
			view = checkbox;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_COMBOBOX))
		{
			Log.i("info", "combobox valuelist "+valueList+" labellist "+labelList);
			ComboBox combobox;
			if ((valueList != null) && (labelList != null))
			{
				combobox = new ComboBox(context, labelList, valueList);
			}
			else
			{	
				combobox = new ComboBox(context, itemList);
			}			
			view = combobox;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_LABEL))
		{
			Log.i("info", "label");
			Label label = new Label(context, setFontType(fontType), setFontSize(fontSize));
			label.setText(name);
			if (hasAlign)
			{
				label.setAlignment(setAlign(align));
			}			
			view = label;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_DATEFIELD))
		{
			Log.i("info", "datefield");
			DateField datefield = new DateField(context, setFontType(fontType), setFontSize(fontSize));			
			view = datefield;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_TIMEFIELD))
		{
			Log.i("info", "timefield");
			TimeField timefield = new TimeField(context, setFontType(fontType), setFontSize(fontSize));			
			view = timefield;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_TEXTFIELD))
		{
			Log.i("info", "textfield "+multiLine);
			if (multiLine.equals("true"))
			{				
				TextZone textzone = new TextZone(context, setFontType(fontType), setFontSize(fontSize));
				if ((tableID != null) && (fieldID != null))
				{
					textzone.setTableId(tableID);
					textzone.setFieldId(fieldID);
				}
				view = textzone;
			}
			else
			{
				TextField textfield = new TextField(context, setFontType(fontType), setFontSize(fontSize));
				if ((tableID != null) && (fieldID != null))
				{
					textfield.setTableId(tableID);
					textfield.setFieldId(fieldID);
				}
				view = textfield;
			}			
			if (hasAlign)
			{
				((TextView)view).setAlignment(setAlign(align));
			}
			if (editable)
			{
				((TextView)view).setEnabled(!editable);
			}
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_TEXTZONE))
		{
			Log.i("info", "textzone");
			TextZone textzone = new TextZone(context, setFontType(fontType), setFontSize(fontSize));
			view = textzone;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_RADIOBUTTON))
		{
			Log.i("info", "radiobutton");
			RadioButton radiobutton = new RadioButton(context);
			radiobutton.setText(name);
			radiobutton.setTypeface(setFontType(fontType));
			radiobutton.setTextSize(setFontSize(fontSize));
			view = radiobutton;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_NUMBERBOX))
		{
			Log.i("info", "numberbox");
			NumberBox numberbox = new NumberBox(context);
			view = numberbox;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_PICTUREBOX))
		{
			Log.i("info", "picturebox");
			Button image = new Button(context);
			image.setText("Picture");
			image.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					
				}				
			});			
			view = image;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_IMAGE))
		{
			Log.i("info", "image");
			ImageView imageview = new ImageView(context);
			
			if (background != 0)
			{
				Drawable d = Drawable.createFromPath("data/misc/location/"+background+"."+extension);
				Log.i("info", "image background "+"data/misc/location/"+background+"."+extension);
				imageview.setBackground(d);
			}			
			view = imageview;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_DATAVIEW))
		{
			Log.i("info", "dataview");
			DataView dataview = new DataView(context, tableID);			
			dataview.setText(setFontSize(fontSize), setFontType(fontType));
			dataview.setColumnInfo(columnInfos);
			view = dataview;
		}
		else
		{			
			Button button = new Button(context);			
			button.setText("else");
			view = button;
			Log.i("info", "else in component");
		}
	}
	
	public void setOnclickFunction(final String funcName, View view)
	{
		view.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				function.createFunction(funcName, null);
			}			
		});
	}
	
	public void setOnchangeFunction(String funcName, View view)
	{
		Log.i("info", "change event");
		function.createFunction(funcName, null);
	}
	
	private Alignment setAlign(String align)
	{
		Alignment alignment = null;
		if (align.equals("left"))
		{
			alignment = Alignment.ALIGN_NORMAL;
		}
		else if (align.equals("center"))
		{
			alignment = Alignment.ALIGN_CENTER;
		}
		else if (align.equals("right"))
		{
			alignment = Alignment.ALIGN_OPPOSITE;
		}
		return alignment;
	}
	
	private float setFontSize(String fs)
	{
		float fontSize = 12;
		if (fs.equals("small"))
		{
			fontSize = 10;
		}
		else if (fs.equals("big"))
		{
			fontSize = 14;
		}
		else if (fs.equals("extra"))
		{
			fontSize = 16;
		}
		return fontSize;
	}
	
	private Typeface setFontType(String ft)
	{
		Typeface fontType = Typeface.DEFAULT;
		if (ft.equals("italic"))
		{
			fontType = Typeface.DEFAULT_ITALIC;
		}
		else if (ft.equals("bold"))
		{
			fontType = Typeface.DEFAULT_BOLD;
		}
		else if (ft.equals("underline"))
		{
			//Underline text, bug of android
		}
		else if (ft.equals("italicbold"))
		{
			fontType = Typeface.DEFAULT_BOLD_ITALIC;	
		}
		return fontType;
	}
	
	public void refreshComponentContent(Object filter)
	{
		if (getView() instanceof DataView)
		{
			Log.i("info", "instance of DataView "+filter);
			((DataView)getView()).refresh(filter);
		}
		else if (getView() instanceof ComboBox)
		{
			Log.i("info", "instance of ComboBox");
			((ComboBox)getView()).getData(filter);
		}
	}
	
	public void setRecord(String formId)
	{
		Log.i("info", "in componenttttttttttttttttttttttt");
		if (getView() instanceof ComboBox)
		{
			((ComboBox)getView()).setCurrentValue(formId);
			Log.i("info", "here");
		}
	}
	
	public HashMap<String, Object> getRecord()
	{
		Log.i("info", "getRecord");
		HashMap<String, Object> result = null;
		if (getView() instanceof ComboBox)
		{
			result = ((ComboBox)getView()).getCurrentRecord();
			Log.i("info", "result in component "+result);
		}
		return result;
	}
	
	public void setValue(Object value)
	{
		if (getView() instanceof NumberBox)
		{
			((NumberBox)getView()).setValue(value);
		}
	}
}