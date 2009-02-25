package com.penbase.dma.Dalyo.Component.Custom.Dataview;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DataViewAdapter extends BaseAdapter{
	private ArrayList<CustomLinearLayout> items = new ArrayList<CustomLinearLayout>();
	
	public DataViewAdapter() {
	}

	@Override
	public int getCount() {
		return items.size();
	}

	public void addItem(CustomLinearLayout cll) {
		boolean isAdded = false;
		int count = getCount();
		for (int i=0; i<count; i++) {
			CustomLinearLayout item = items.get(i);
			if (item.getDataList().equals(cll.getDataList())) {
				isAdded = true;
			}
		}
		if (!isAdded) {
			items.add(cll);
		}
	}
	
	@Override
	public Object getItem(int position) {
		if (position < getCount()-1) {
			return items.get(position);
		}
		else {
			return null;
		}		
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return items.get(position);
	}
	
	public void removeItems() {
		items.clear();
	}
	
	public ArrayList<CustomLinearLayout> getItems() {
		return items;
	}
}