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

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
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

import com.penbase.dma.Dma;
import com.penbase.dma.R;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Dalyo.Application;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Displays a GridView which contains all applications' icons
 */
public class ApplicationListView extends Activity implements OnItemSelectedListener, OnItemClickListener {

	private AppsAdapter mAdapter;
	private TextView mApplicationName;
	private static ProgressDialog sLoadProgressDialog = null;
	private static ProgressDialog sUpdateProgressDialog = null;
	private Intent mIntent = null;
	private static HashMap<String, String> sApplicationInfos = new HashMap<String, String>();
	private DmaHttpClient mDmahttpclient = new DmaHttpClient();
	private static String sApplicationName;
	private GridView mGridView;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.wave_scale);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(Color.WHITE);
		ImageView imageView = new ImageView(this);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			imageView.setBackgroundResource(R.drawable.banniere_dalyo);
		}
		else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			imageView.setBackgroundResource(R.drawable.banniere_dalyo1);
		}
		layout.addView(imageView, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		TextView textView = new TextView(this);
		textView.setText("Application list:");
		textView.setTextColor(Color.BLACK);
		layout.addView(textView, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		mGridView = new GridView(this);
		mGridView.setCacheColorHint(Color.TRANSPARENT);
		mGridView.setVerticalScrollBarEnabled(false);
		mGridView.setScrollingCacheEnabled(false);
		mGridView.setVerticalSpacing(5);
		mGridView.setHorizontalSpacing(10);
		mGridView.setNumColumns(GridView.AUTO_FIT);
		mGridView.setColumnWidth(60);
		mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		mGridView.setGravity(Gravity.CENTER);
		mGridView.setLayoutAnimation(new LayoutAnimationController(animation));
		mAdapter = new AppsAdapter(this);		
		mApplicationName = new TextView(this);
		mApplicationName.setGravity(Gravity.CENTER);
		mApplicationName.setTextColor(Color.BLACK);
		mApplicationName.setTypeface(Typeface.DEFAULT_BOLD);
		layout.addView(mApplicationName, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		int size = Dma.applicationList.size();
		for (int i =0; i < size; i++) {
			mAdapter.addApplication(Dma.applicationList.get(i));
			if (i == 0) {
				mApplicationName.setText(Dma.applicationList.get(i).getName());
			}
		}
		
		mGridView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch (arg1.getAction()) {
					case MotionEvent.ACTION_UP:
						mGridView.setVerticalScrollBarEnabled(false);
						break;
					default:
						mGridView.setVerticalScrollBarEnabled(true);
						break;
				}
				return false;
			}
		});
		
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
		mGridView.setOnItemSelectedListener(this);
		layout.addView(mGridView, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		setContentView(layout);
	}

	/**
	 * Contains items associated with the ApplicationListView
	 */
	private class AppsAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<Integer> mApps = new ArrayList<Integer>();
		
		public AppsAdapter(Context context) {
			mContext = context;
		}

		/**
		 * Returns the item in the given position which contains an icon and the application's name
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout layout = new LinearLayout(mContext);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setGravity(Gravity.CENTER);
			
			ImageView i = new ImageView(mContext);
			i.setImageResource(mApps.get(position));
			i.setAdjustViewBounds(true);
			i.setScaleType(ImageView.ScaleType.FIT_CENTER);
            i.setLayoutParams(new GridView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			i.setBackgroundResource(android.R.drawable.picture_frame);
			
			TextView tv = new TextView(mContext);
			tv.setText(Dma.applicationList.get(position).getName());
			tv.setTextSize(10);
			tv.setTextColor(Color.BLACK);
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			
			layout.addView(i, new LinearLayout.LayoutParams(60, 60));
			layout.addView(tv, new LinearLayout.LayoutParams(60, 30));
			return layout;
		}

		public final int getCount() {
			return mApps.size();
		}

		public final Object getItem(int position) {
			return mApps.get(position);
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

	/**
	 * Deletes preference data and finishes this activity 
	 */
	public void logout() {
		SharedPreferences.Editor editor = getSharedPreferences(Constant.PREFNAME, MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
		this.finish();
		startActivityForResult(new Intent(this, Dma.class), 0);
	}

	/**
	 * Updates the application list
	 */
	public void update() {
		sUpdateProgressDialog = ProgressDialog.show(this, "Please wait...", "Updaing application list...", true, false);
		new Thread() {
			public void run() {
				try {
					SharedPreferences prefs = getSharedPreferences(Constant.PREFNAME, MODE_PRIVATE);
					String appsList = mDmahttpclient.Authentication(prefs.getString("Username", ""),
							prefs.getString("Userpassword", ""));
					mDmahttpclient.update(appsList);
					SharedPreferences.Editor editorPrefs = getSharedPreferences(Constant.PREFNAME, MODE_PRIVATE).edit();
					editorPrefs.remove("ApplicationList");
					editorPrefs.putString("ApplicationList", appsList);
					editorPrefs.commit();
				}
				catch(Exception e)
				{e.printStackTrace();}
				
				sUpdateProgressDialog.dismiss();
				ApplicationListView.this.finish();
				startActivityForResult(new Intent(ApplicationListView.this, ApplicationListView.class), 0);
			}
		}.start();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		mApplicationName.setText(Dma.applicationList.get(position).getName());
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}

	/**
	 * Launches the selected application
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
		SharedPreferences prefs = getSharedPreferences(Constant.PREFNAME, MODE_PRIVATE);
		sApplicationName = Dma.applicationList.get(position).getName();
		sApplicationInfos.put("Username", prefs.getString("Username", ""));
		sApplicationInfos.put("Userpassword", prefs.getString("Userpassword", ""));
		sApplicationInfos.put("AppId", Dma.applicationList.get(position).getAppId());
		sApplicationInfos.put("AppVer", Dma.applicationList.get(position).getAppVer());
		sApplicationInfos.put("AppBuild", Dma.applicationList.get(position).getAppBuild());
		sApplicationInfos.put("SubId", Dma.applicationList.get(position).getSubId());
		sApplicationInfos.put("DbId", Dma.applicationList.get(position).getDbId());
		sLoadProgressDialog = ProgressDialog.show(this, "Please wait...", "Preparing application...", true, false);
		mIntent = new Intent(this, ApplicationView.class);
		
		new Thread() {
			public void run() {
				try {
					ApplicationView.prepareData(position, sApplicationInfos.get("Username"),
							sApplicationInfos.get("Userpassword"));
				}	
				catch(Exception e)
				{e.printStackTrace();}
				
				startActivityForResult(mIntent, 0);
			}
		}.start();
	}
	
	public static HashMap<String, String> getApplicationsInfo() {
		return sApplicationInfos;
	}
	
	public static String getApplicationName() {
		return sApplicationName;
	}

	@Override
	protected void onStop() {
		if (sLoadProgressDialog != null) {
			sLoadProgressDialog.dismiss();
			sLoadProgressDialog = null;
		}
		super.onStop();
	}
}
