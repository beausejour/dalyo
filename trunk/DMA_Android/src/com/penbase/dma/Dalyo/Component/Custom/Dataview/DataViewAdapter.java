package com.penbase.dma.Dalyo.Component.Custom.Dataview;

import java.util.ArrayList;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DataViewAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<CustomLinearLayout> items = new ArrayList<CustomLinearLayout>();
	private boolean test = true;
	
	public DataViewAdapter(Context c){
		this.context = c;
	}

	@Override
	public int getCount(){
		return items.size();
	}

	public void addItem(CustomLinearLayout cll){
		items.add(cll);
	}
	
	@Override
	public Object getItem(int position){
		if (position < getCount()-1){
			return items.get(position);
		}
		else{
			return null;
		}		
	}

	@Override
	public long getItemId(int position){
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		CustomLinearLayout layout = null;
		if (convertView == null){
			if ((position == 0) && (test)){
				layout = new CustomLinearLayout(context, items.get(position).getDataList(),
						items.get(position).getWidthList(), true);
				test = false;
			}
			else{
				layout = new CustomLinearLayout(context, items.get(position).getDataList(),
						items.get(position).getWidthList(),false);
			}
		}
		else{
			layout = (CustomLinearLayout) convertView;
		}
		return layout;
	}
	
	public void removeItems(){
		items.clear();
	}
	
	public ArrayList<CustomLinearLayout> getItems(){
		return items;
	}
}