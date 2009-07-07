package com.penbase.dma.Dalyo.Component.Custom.Dataview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class DataViewAdapter extends BaseAdapter{
	private ArrayList<CustomLinearLayout> mItems = new ArrayList<CustomLinearLayout>();
	
	public DataViewAdapter() {
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	public void addItem(CustomLinearLayout cll) {
		mItems.add(cll);
	}
	
	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return mItems.get(position);
	}
	
	public void removeItems() {
		mItems.clear();
	}
	
	public ArrayList<CustomLinearLayout> getItems() {
		return mItems;
	}
}