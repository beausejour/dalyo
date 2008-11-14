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
package com.penbase.dma.View;

import java.util.ArrayList;
import java.util.HashMap;
import com.penbase.dma.Dma;
import com.penbase.dma.R;
import com.penbase.dma.Dalyo.Application;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class ApplicationListView extends Activity implements OnItemSelectedListener, OnItemClickListener {

	private AppsAdapter mAdapter;
	private TextView mApplicationName;
	private static ProgressDialog loadProgressDialog = null;
	private static ProgressDialog updateProgressDialog = null;
	private Intent i = null;
	private static HashMap<String, String> applicationInfos = new HashMap<String, String>();
	private DmaHttpClient dmahttpclient = new DmaHttpClient();
	private static String applicationName;
	private GridView gridView;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.wave_scale);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(Color.WHITE);
		ImageView imageView = new ImageView(this);
		imageView.setBackgroundResource(R.drawable.banniere_dalyo);
		layout.addView(imageView, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		TextView textView = new TextView(this);
		textView.setText("Application list:");
		textView.setTextColor(Color.BLACK);
		layout.addView(textView, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		gridView = new GridView(this);
		gridView.setVerticalScrollBarEnabled(false);
		gridView.setPadding(10, 10, 10, 10);
		gridView.setVerticalSpacing(10);
		gridView.setHorizontalSpacing(10);
		gridView.setNumColumns(GridView.AUTO_FIT);
		gridView.setColumnWidth(60);
		gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridView.setGravity(Gravity.CENTER);
		gridView.setLayoutAnimation(new LayoutAnimationController(animation));
		mAdapter = new AppsAdapter(this);		
		mApplicationName = new TextView(this);
		mApplicationName.setGravity(Gravity.CENTER);
		mApplicationName.setTextColor(Color.BLACK);
		mApplicationName.setTypeface(Typeface.DEFAULT_BOLD);
		layout.addView(mApplicationName, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		for (int i =0; i < Dma.applicationList.size(); i++){
			mAdapter.addApplication(Dma.applicationList.get(i));
			if (i == 0) {
				mApplicationName.setText(Dma.applicationList.get(i).getName());
			}
		}
		gridView.setAdapter(mAdapter);
		gridView.setOnItemClickListener(this);
		gridView.setOnItemSelectedListener(this);
		layout.addView(gridView, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setContentView(layout);
	}

	public class AppsAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<Integer> mApps = new ArrayList<Integer>();
		public AppsAdapter(Context context){
			mContext = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout layout = new LinearLayout(mContext);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setGravity(Gravity.CENTER);
			
			ImageView i = new ImageView(mContext);		// Make an ImageView to show a photo
			i.setImageResource(mApps.get(position));
			i.setAdjustViewBounds(true);
			i.setScaleType(ImageView.ScaleType.FIT_CENTER);
            i.setLayoutParams(new GridView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			i.setBackgroundResource(android.R.drawable.picture_frame);		// Give it a nice background
			
			TextView tv = new TextView(mContext);
			tv.setText(Dma.applicationList.get(position).getName());
			tv.setTextSize(10);
			
			//layout.addView(i, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			layout.addView(i, new LinearLayout.LayoutParams(48, 48));
			layout.addView(tv, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			return layout;
			//return i;
		}

		public final int getCount() {
			return mApps.size();
		}

		public final Object getItem(int position) {
			return position;
			//return mApps.get(position);
		}

		public final long getItemId(int position) {
			return position;
		}

		public void addApplication(Application app) {
			mApps.add(app.getIconRes());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean r = super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 0, Menu.NONE, "Logout");
		menu.add(Menu.NONE, 1, Menu.NONE, "About");
		menu.add(Menu.NONE, 2, Menu.NONE, "Update");
		return r;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				logout();
				return true;
			case 1:
				Dialog dialog = new Dialog(this);
				dialog.setTitle("DMA version 0.9");
				dialog.show();
				return true;
			case 2:
				update();
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	//Logout delete the preference data
	public void logout() {
		SharedPreferences.Editor editor = getSharedPreferences(Dma.PREFS_NAME, MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
		this.finish();
		startActivityForResult(new Intent(this, Dma.class), 0);
	}

	//Using the preference data to rebuild application list
	public void update() {
		updateProgressDialog = ProgressDialog.show(this, "Please wait...", "Updaing application list...", true, false);
		new Thread(){
			public void run() {
				try {
					SharedPreferences prefs = getSharedPreferences(Dma.PREFS_NAME, MODE_PRIVATE);
					String appsList = dmahttpclient.Authentication(prefs.getString("Username", ""),
							prefs.getString("Userpassword", ""));
					Dma.GetListApplicationFromXml(appsList);
					SharedPreferences.Editor editorPrefs = getSharedPreferences(Dma.PREFS_NAME, MODE_PRIVATE).edit();
					editorPrefs.remove("ApplicationList");
					editorPrefs.putString("ApplicationList", appsList);
					editorPrefs.commit();
					DmaHttpClient.update();
				}
				catch(Exception e)
				{e.printStackTrace();}
				
				updateProgressDialog.dismiss();
				ApplicationListView.this.finish();
				startActivityForResult(new Intent(ApplicationListView.this, ApplicationListView.class), 0);
			}
		}.start();
	}

	@Override
	public void onItemSelected(AdapterView parent, View v, int position, long id) {
		mApplicationName.setText(Dma.applicationList.get(position).getName());
	}

	@Override
	public void onNothingSelected(AdapterView arg0) {}

	@Override
	public void onItemClick(AdapterView parent, View v, final int position, long id) {
		SharedPreferences prefs = getSharedPreferences(Dma.PREFS_NAME, MODE_PRIVATE);
		applicationName = Dma.applicationList.get(position).getName();
		applicationInfos.put("Username", prefs.getString("Username", ""));
		applicationInfos.put("Userpassword", prefs.getString("Userpassword", ""));
		applicationInfos.put("AppId", Dma.applicationList.get(position).getAppId());
		applicationInfos.put("AppVer", Dma.applicationList.get(position).getAppVer());
		applicationInfos.put("AppBuild", Dma.applicationList.get(position).getAppBuild());
		applicationInfos.put("SubId", Dma.applicationList.get(position).getSubId());
		applicationInfos.put("DbId", Dma.applicationList.get(position).getDbId());
		loadProgressDialog = ProgressDialog.show(this, "Please wait...", "Downloading application...", true, false);
		i = new Intent(this, ApplicationView.class);
		
		new Thread() {
			public void run() {
				try {
					ApplicationView.prepareData(position, applicationInfos.get("Username"),
							applicationInfos.get("Userpassword"));
				}	
				catch(Exception e)
				{e.printStackTrace();}
				
				loadProgressDialog.dismiss();
				//ApplicationListView.this.finish();
				startActivityForResult(i, 0);
			}
		}.start();
	}
	
	public static HashMap<String, String> getApplicationsInfo() {
		return applicationInfos;
	}
	
	public static String getApplicationName(){
		return applicationName;
	}
}
