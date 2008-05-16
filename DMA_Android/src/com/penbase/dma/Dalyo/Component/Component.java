package com.penbase.dma.Dalyo.Component;

import java.util.*;

import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.view.ApplicationView;
import com.penbase.dma.xml.XmlTag;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.DatePicker.OnDateSetListener;
import android.widget.TimePicker.OnTimeSetListener;


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
	
	//Variables for DateField 
	private int year;
	private int month;
	private int day;
	private Button dateButton;
	
	//Variables for TimeField
	private int hour;
	private int minute;
	private Button timeButton;
	
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
	
	public String getType()
	{
		return type;
	}
	
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
			Log.i("info", "create CHECKBOX");			
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
			Spinner combobox = new Spinner(context);
			view = combobox;
			/*int itemNb = itemList.size();
			ArrayList<TextView> tvList = new ArrayList<TextView>();
			for (int i=0; i<itemNb; i++)
			{
				TextView textview = new TextView(context);
				textview.setText(itemList.get(i));
				Log.i("info", "combo item "+itemList.get(i));
				textview.setTypeface(setFontType(fontType));
				textview.setTextSize(setFontSize(fontSize));
				tvList.add(textview);
			}
			ArrayAdapter<TextView> spinnerArrayAdapter = new ArrayAdapter<TextView>(context,
			        android.R.layout.simple_spinner_item, tvList);*/
			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,
			        android.R.layout.simple_spinner_item, itemList);
			combobox.setAdapter(spinnerArrayAdapter);
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_LABEL))
		{
			Log.i("info", "label");
			TextView textview = new TextView(context);
			view = textview;
			textview.setText(name);
			textview.setTypeface(setFontType(fontType));
			textview.setTextSize(setFontSize(fontSize));
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_DATEFIELD))
		{
			Log.i("info", "datefield");
			
			dateButton = new Button(context);
	        Calendar calendar = Calendar.getInstance();
	        year = calendar.get(Calendar.YEAR);
	        month = calendar.get(Calendar.MONTH);
	        day = calendar.get(Calendar.DAY_OF_MONTH);
	        String currentDate = day+"/"+month+"/"+year;
	        dateButton.setText(currentDate);
	        dateButton.setTypeface(setFontType(fontType));
	        dateButton.setTextSize(setFontSize(fontSize));
	        dateButton.setOnClickListener(new OnClickListener()
	        {
				@Override
				public void onClick(View arg0) 
				{
					 new DatePickerDialog(context, new OnDateSetListener()
					 {
						@Override
						public void dateSet(DatePicker arg0, int arg1, int arg2, int arg3) 
						{
							year = arg1;
							month = arg2;
							day = arg3;
							String newDate = day+"/"+month+"/"+year;
							dateButton.setText(newDate);
						}						 
					 }, year, month, day, Calendar.MONDAY).show();
				}	        	
	        });
			view = dateButton;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_TIMEFIELD))
		{
			Log.i("info", "timefield");
			
			timeButton = new Button(context);
			Calendar calendar = Calendar.getInstance();
			hour = calendar.get(Calendar.HOUR);
	        minute = calendar.get(Calendar.MINUTE);
	        String currentTime = hour+":"+minute;
	        timeButton.setText(currentTime);
	        timeButton.setTypeface(setFontType(fontType));
	        timeButton.setTextSize(setFontSize(fontSize));
	        timeButton.setOnClickListener(new OnClickListener()
	        {
				@Override
				public void onClick(View arg0) 
				{
					new TimePickerDialog(context, new OnTimeSetListener()
					{
						@Override
						public void timeSet(TimePicker arg0, int arg1, int arg2) 
						{
							hour = arg1;
							minute = arg2;
							String newTime = hour+":"+minute;
							timeButton.setText(newTime);
						}					
					}, "Set the time", hour, minute, false).show();
				}	        	
	        });
			view = timeButton;
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_TEXTFIELD))
		{
			Log.i("info", "textfield");
			AutoCompleteTextView actextview = new AutoCompleteTextView(context);			
			view = actextview;
			actextview.setTypeface(setFontType(fontType));
			actextview.setTextSize(setFontSize(fontSize));
		}
		else if(type.equals(XmlTag.TAG_COMPONENT_TEXTZONE))
		{
			Log.i("info", "textzone");
			EditText edittext = new EditText(context);
			view = edittext;
			edittext.setTypeface(setFontType(fontType));
			edittext.setTextSize(setFontSize(fontSize));
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
			DataView dataview = new DataView(context);			
			dataview.setText(setFontSize(fontSize), setFontType(fontType));
			dataview.setColumnInfo(columnInfos);
			view = dataview;
		}
		else
		{
			Log.i("info", "else");
			Button button = new Button(context);
			view = button;
			button.setText(name);
			button.setTypeface(setFontType(fontType));
			button.setTextSize(setFontSize(fontSize));
		}
	}
	
	public void setOnclickFunction(final String funcName, View view)
	{
		view.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) 
			{
				function.createFunction(funcName);
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
}