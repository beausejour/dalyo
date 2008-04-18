/**
 	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.penbase.dma.view;

import java.util.ArrayList;
import com.penbase.dma.Dma;
import com.penbase.dma.R;
import com.penbase.dma.Dalyo.Application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu.Item;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.AdapterView.OnItemSelectedListener;


public class ApplicationListView extends Activity implements
		OnItemSelectedListener {

	AppsAdapter mAdapter;
	TextView mApplicationName;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.applicationlist_layout);
		GridView g = (GridView) findViewById(R.id.mlist);
		mAdapter = new AppsAdapter(this);		
		mApplicationName = (TextView) findViewById(R.id.label2);
		
		Log.i("info", "size "+Dma.applicationList.size());
		
		for (int i =0; i < Dma.applicationList.size(); i++)
		{
			mAdapter.addApplication(Dma.applicationList.get(i));
		}
		g.setAdapter(mAdapter);
		g.setOnItemSelectedListener(this);
	}

	public class AppsAdapter extends BaseAdapter {
		private Context mContext;
		
		private ArrayList<Integer> mApps = new ArrayList<Integer>();


		public AppsAdapter(Context context) {
			mContext = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// Make an ImageView to show a photo
			ImageView i = new ImageView(mContext);
			i.setImageResource(mApps.get(position));
			i.setAdjustViewBounds(true);
			i.setLayoutParams(new ViewGroup.MarginLayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			// Give it a nice background
			i.setBackground(android.R.drawable.picture_frame);
			return i;
		}

		public final int getCount() {
			return mApps.size();
		}

		public final Object getItem(int position) {
			return position;
		}

		public final long getItemId(int position) {
			return position;
		}

		public void addApplication(Application app) {		
			mApps.add(app.getMIconRes());
			//notifyAll();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean r = super.onCreateOptionsMenu(menu);
		menu.add(0, 0, getResources().getString(R.string.menu_logout));
		menu.add(0, 1, getResources().getString(R.string.menu_about));
		return r;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, Item item) {
		switch (item.getId()) {
		case 0:
			// save user info.
			SharedPreferences settings = getSharedPreferences(Dma.PREFS_NAME,
					MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("RememberMe", false);
			editor.commit();
			this.finish();
			startSubActivity(new Intent(this, LoginView.class), 0);
			return true;
		case 1:
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onItemSelected(AdapterView parent, View v, int position, long id) {
		mApplicationName.setText(Dma.applicationList.get(position).getMName());
	}

	@Override
	public void onNothingSelected(AdapterView arg0) {
	}
}
