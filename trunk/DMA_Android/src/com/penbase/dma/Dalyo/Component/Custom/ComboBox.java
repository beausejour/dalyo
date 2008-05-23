package com.penbase.dma.Dalyo.Component.Custom;

import java.util.ArrayList;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ComboBox extends Spinner{

	public ComboBox(Context context, ArrayList<String> itemList)
	{
		super(context);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,
		        android.R.layout.simple_spinner_item, itemList);
		this.setAdapter(spinnerArrayAdapter);
	}
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
}
