package com.penbase.dma.Dalyo.Component.Custom.Dataview;

import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DataViewAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<CustomLinearLayout> items = new ArrayList<CustomLinearLayout>();
	
	public DataViewAdapter(Context c) 
	{
		this.context = c;
	}

	@Override
	public int getCount() 
	{
		return items.size();
	}

	public boolean checkDataList(ArrayList<String> listA, ArrayList<String> listB)
	{
		boolean result = false;
		int listSize = listA.size();
		int i = 0;
		while (i < listSize)
		{
			if (listA.get(i).equals(listB.get(i)))
			{
				if (i == listSize-1)
				{
					result = true;
				}
				i++;
			}
			else
			{
				i = listSize;
			}
		}
		return result;
	}
	
	public void addItem(CustomLinearLayout cll)
	{
		if (items.size() > 0)
		{		
			boolean add = false;
			int itemsSize = items.size();
			int check = 0;
			for (int i=0; i<itemsSize; i++)
			{
				CustomLinearLayout item = items.get(i);
				if (!checkDataList(item.getDataList(), cll.getDataList()))
				{
					check++;
				}
				if ((i == itemsSize -1) && (check != i))
				{
					add = true;
				}
			}
			if (add)
			{
				items.add(cll);
			}
		}
		else
		{
			items.add(cll);
		}		
	}
	
	@Override
	public Object getItem(int position) 
	{
		if (position < getCount()-1)
		{
			return items.get(position);
		}
		else
		{
			return null;
		}		
	}

	@Override
	public long getItemId(int position) 
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{		
		CustomLinearLayout layout = null;

		if (convertView == null)
		{
			if (position == 0)
			{
				layout = new CustomLinearLayout(context, items.get(position).getDataList(), 
						items.get(position).getWidthList(), true);
			}
			else
			{
				layout = new CustomLinearLayout(context, items.get(position).getDataList(), 
						items.get(position).getWidthList(),false);
			}
		}
		else
		{
			layout = (CustomLinearLayout) convertView;
		}
		return layout;
	}
}
