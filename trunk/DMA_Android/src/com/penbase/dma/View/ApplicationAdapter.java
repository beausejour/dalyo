package com.penbase.dma.View;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.penbase.dma.R;
import com.penbase.dma.Dalyo.Application;

/**
 * Contains items associated with the ApplicationListView
 */
public class ApplicationAdapter extends BaseAdapter {
	private ArrayList<Application> mApps = new ArrayList<Application>();
	private LayoutInflater mInflater;
	
	public ApplicationAdapter(Context context) {
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Returns the item in the given position which contains an icon and the application's name
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Application application = mApps.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.application, parent, false);
			holder = new ViewHolder();
			holder.icon = (ImageView)convertView.findViewById(R.id.appicon);
			holder.text = (TextView)convertView.findViewById(R.id.appname);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		Drawable iconDrawable = application.getIcon();
		if (iconDrawable != null) {
			holder.icon.setImageDrawable(iconDrawable);
		} else {
			holder.icon.setImageResource(R.drawable.splash);	
		}
		holder.icon.setBackgroundResource(android.R.drawable.picture_frame);
		holder.text.setText(application.getName());
		return convertView;
	}

	@Override
	public int getCount() {
		return mApps.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	public void addApplication(Application app) {
		mApps.add(app);
	}
	
	public void removeAll() {
		mApps.clear();
	}
	
	private static class ViewHolder {
		ImageView icon;
		TextView text;
	}
}