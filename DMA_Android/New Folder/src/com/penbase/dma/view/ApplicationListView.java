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
import java.util.HashMap;
import com.penbase.dma.Dma;
import com.penbase.dma.DmaHttpClient;
import com.penbase.dma.R;
import com.penbase.dma.Dalyo.Application;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class ApplicationListView extends Activity implements OnItemSelectedListener, OnItemClickListener {

	AppsAdapter mAdapter;
	TextView mApplicationName;
	private static ProgressDialog progressDialog = null;
	private Intent i = null;
	public static HashMap<String, Integer> applicationInfos = new HashMap<String, Integer>();
	private DmaHttpClient dmahttpclient = new DmaHttpClient();

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.applicationlist_layout);
		GridView g = (GridView) findViewById(R.id.mlist);
		mAdapter = new AppsAdapter(this);		
		mApplicationName = (TextView) findViewById(R.id.label2);
				
		for (int i =0; i < Dma.applicationList.size(); i++)
		{
			mAdapter.addApplication(Dma.applicationList.get(i));
		}
		g.setAdapter(mAdapter);
		g.setOnItemSelectedListener(this);
		g.setOnItemClickListener(this);
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
			mApps.add(app.getIconRes());
			//notifyAll();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean r = super.onCreateOptionsMenu(menu);
		menu.add(0, 0, getResources().getString(R.string.menu_logout));
		menu.add(0, 1, getResources().getString(R.string.menu_about));
		menu.add(0, 2, getResources().getString(R.string.menu_update));
		return r;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, Item item) {
		switch (item.getId()) {
		case 0:
			logout();
			return true;
		case 1:
			return true;
		case 2:
			Log.i("info", "update");
			update();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	//Logout delete the preference data
	public void logout()
	{
		SharedPreferences.Editor editor = getSharedPreferences(Dma.PREFS_NAME,
				MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
		this.finish();
		startSubActivity(new Intent(this, Dma.class), 0);
	}
	
	//Using the preference data to rebuild application list
	public void update()
	{
		SharedPreferences prefs = getSharedPreferences(Dma.PREFS_NAME, MODE_PRIVATE);
		String appsList = dmahttpclient.Authentication(prefs.getString("Username", ""),
				prefs.getString("Userpassword", ""));
		Dma.GetListApplicationFromXml(appsList);
		
		SharedPreferences.Editor editorPrefs = getSharedPreferences(Dma.PREFS_NAME,
				MODE_PRIVATE).edit();
		editorPrefs.remove("ApplicationList");
		editorPrefs.putString("ApplicationList", appsList);
		editorPrefs.commit();
		this.finish();
		startSubActivity(new Intent(this, ApplicationListView.class), 0);	
	}
	
	@Override
	public void onItemSelected(AdapterView parent, View v, int position, long id) 
	{
		mApplicationName.setText(Dma.applicationList.get(position).getName());
	}
	
	@Override
	public void onNothingSelected(AdapterView arg0) {}
	
	@Override
	public void onItemClick(AdapterView parent, View v, int position, long id) 
	{
		Log.i("info", "item clicked "+position);
		applicationInfos.put("AppId", Dma.applicationList.get(position).getAppId());
		applicationInfos.put("AppVer", Dma.applicationList.get(position).getAppVer());
		applicationInfos.put("AppBuild", Dma.applicationList.get(position).getAppBuild());
		applicationInfos.put("SubId", Dma.applicationList.get(position).getSubId());
		applicationInfos.put("DbId", Dma.applicationList.get(position).getDbId());
		//applicationInfos.put("Name", Dma.applicationList.get(position).getName());
		
		progressDialog = ProgressDialog.show(this, "Please wait...", "Loading application...", true, true);
		i = new Intent(this, ApplicationView.class);	     

		new Thread(){
			public void run(){
				try
				{
					SharedPreferences prefs = getSharedPreferences(Dma.PREFS_NAME, MODE_PRIVATE);								
					ApplicationView.prepareData(prefs.getString("Username", ""), 
							prefs.getString("Userpassword", ""));	    
				}
				catch(Exception e)
				{e.printStackTrace();}
				
				progressDialog.dismiss();
				ApplicationListView.this.finish();
				startSubActivity(i, 0);
			}
		}.start();	    
	}
}
