package com.penbase.dma.Dalyo.Component;

import java.util.*;
import com.penbase.dma.Dalyo.Component.Custom.ComboBox;
import com.penbase.dma.Dalyo.Component.Custom.DataView;
import com.penbase.dma.Dalyo.Component.Custom.DateField;
import com.penbase.dma.Dalyo.Component.Custom.NumberBox;
import com.penbase.dma.Dalyo.Component.Custom.TimeField;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.XmlElement.XmlTag;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
	
	//Variables for checkbox
	private ArrayList<String> itemList = null;
	private String checked;	

	//Variable for image
	private int background;
	private String extension;
	
	//Variable for dataview
	private ArrayList<ArrayList<String>> columnInfos = new ArrayList<ArrayList<String>>();
	private String tableID;
	
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
	
	//Constructor for combobox
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
			Log.i("info", "combobox");
			ComboBox combobox = new ComboBox(context, itemList);
			view = combobox;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_LABEL))
		{
			Log.i("info", "label");
			TextView textview = new TextView(context);			
			textview.setText(name);
			textview.setTypeface(setFontType(fontType));
			textview.setTextSize(setFontSize(fontSize));
			view = textview;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_DATEFIELD))
		{
			Log.i("info", "datefield");
			DateField datefield = new DateField(context);
			datefield.setTypeface(setFontType(fontType));
			datefield.setTextSize(setFontSize(fontSize));
			view = datefield;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_TIMEFIELD))
		{
			Log.i("info", "timefield");
			TimeField timefield = new TimeField(context);
			timefield.setTypeface(setFontType(fontType));
			timefield.setTextSize(setFontSize(fontSize));
			view = timefield;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_TEXTFIELD))
		{
			Log.i("info", "textfield");
			AutoCompleteTextView actextview = new AutoCompleteTextView(context);						
			actextview.setTypeface(setFontType(fontType));
			actextview.setTextSize(setFontSize(fontSize));
			view = actextview;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_TEXTZONE))
		{
			Log.i("info", "textzone");
			EditText edittext = new EditText(context);			
			edittext.setTypeface(setFontType(fontType));
			edittext.setTextSize(setFontSize(fontSize));
			view = edittext;
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
	
	public void refreshComponentContent()
	{
		if (getView() instanceof DataView)
		{
			Log.i("info", "instance of DataView");
			((DataView)getView()).refresh();
		}
		else if (getView() instanceof Spinner)
		{
			Log.i("info", "instance of ComboBox");
		}
	}
}