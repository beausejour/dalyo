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
		boolean isAdded = false;
		int count = getCount();
		for (int i=0; i<count; i++) {
			CustomLinearLayout item = mItems.get(i);
			if (item.getDataList().equals(cll.getDataList())) {
				isAdded = true;
			}
		}
		if (!isAdded) {
			mItems.add(cll);
		}
	}
	
	@Override
	public Object getItem(int position) {
		Object result = null;
		if (position < getCount()-1) {
			result = mItems.get(position);
		}
		return result;
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