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
package com.penbase.dma;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.ErrorCode;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;
import com.penbase.dma.View.ApplicationListView;

/**
 * Authentication interface of DMA
 */
public class Dma extends Activity implements OnClickListener {
	private TextView mTx_login;
	private TextView mTx_password;
	private CheckBox mCb_remember_me;
	private AlertDialog mAlertDialog;
	private ProgressDialog mLoadApps = null;
	private String mServerResponse = null;
	private static String sDeviceId = null;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					callApplicationListView();
					break;
			}
		}
	};
	private AlertDialog mAboutDialog;
	private LayoutInflater mInflater;
	private static String sVersion;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		sDeviceId = ((TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		SharedPreferences settings = getSharedPreferences(Constant.PREFNAME, MODE_PRIVATE);
		boolean rememberMe = settings.getBoolean("RememberMe", false);
		mAlertDialog = new AlertDialog.Builder(this).create();
		
		PackageInfo pi = null;
		try {
			pi = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		sVersion = pi.versionName;
		
		if (!rememberMe) {
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				setContentView(R.layout.login_layout);
			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				setContentView(R.layout.login_layout_landscape);
			}
			Button bt = (Button) findViewById(R.id.ok);
			bt.setOnClickListener(this);
			mTx_login = (TextView) findViewById(R.id.textLogin);
			mTx_password = (TextView) findViewById(R.id.textePasswd);
			mCb_remember_me = (CheckBox) findViewById(R.id.remember_me_cb);
			mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View aboutView = mInflater.inflate(R.layout.about, null, false);
			TextView nameVersionView = (TextView) aboutView.findViewById(R.id.nameversion);
			nameVersionView.setText("Dalyo Mobile Agent "+Dma.getVersion());
			mAboutDialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info)
	        .setTitle(R.string.menu_about).setView(aboutView).create();
			/*mAboutDialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info)
	        .setTitle(R.string.menu_about).setView(mInflater.inflate(R.layout.about, null, false)).create();*/
		} else {
			this.finish();
			startActivityForResult(new Intent(this, ApplicationListView.class), 0);
		}
	}
	
	@Override
	public void onClick(View arg0) {
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		if ("".equals(mTx_login.getText().toString())
				&& "".equals(mTx_password.getText().toString())) {
			findViewById(R.id.textLogin).startAnimation(shake);
	        findViewById(R.id.textePasswd).startAnimation(shake);
	        return;
		} else if ("".equals(mTx_login.getText().toString())) {
			findViewById(R.id.textLogin).startAnimation(shake);
			return;
		} else if ("".equals(mTx_password.getText().toString())) {
			findViewById(R.id.textePasswd).startAnimation(shake);
			return;
		}
		mLoadApps = ProgressDialog.show(this, "Please wait...", "Connecting to server...", true, false);

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mHandler != null) {
					mServerResponse = new DmaHttpClient(mTx_login.getText().toString().trim()).Authentication(mTx_login.getText().toString().trim(),
							Common.md5(mTx_password.getText().toString().trim()));
					mHandler.sendEmptyMessage(0);
				}
			}
		}).start();
	}
	
	/**
	 * Checks the result of authentication, display the ApplicationListView if authentication success
	 */
	private void callApplicationListView() {
		if (mServerResponse == null) {
			mLoadApps.dismiss();
			mAlertDialog.setTitle("Error");
			showMessage("Connection failed!");
		} else if (mServerResponse.equals(String.valueOf(ErrorCode.UNAUTHORIZED))) {
			mLoadApps.dismiss();
			mAlertDialog.setTitle("Error");
			showMessage("Check your username or password!");
		} else {
			mLoadApps.setMessage("Loading application list...");
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// save user info
						SharedPreferences.Editor editorPrefs = getSharedPreferences(Constant.PREFNAME, MODE_PRIVATE).edit();
						editorPrefs.putBoolean("RememberMe", mCb_remember_me.isChecked());
						editorPrefs.putString("Username", mTx_login.getText().toString());
						editorPrefs.putString("Userpassword", Common.md5(mTx_password.getText().toString().trim()));
						editorPrefs.putString("ApplicationList", mServerResponse);
						editorPrefs.commit();
					} catch(Exception e) {
						e.printStackTrace();
					}

					startActivityForResult(new Intent(Dma.this, ApplicationListView.class), 0);
					Dma.this.finish();
				}
			}).start();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.menu_about).setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.menu_quit).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				mAboutDialog.show();
				break;
			case 1:
				finish();
				break;
		}
		return true;
	}
	
	public void showMessage(String message) {
		mAlertDialog.setMessage("Check your username or password!");
		mAlertDialog.show();
	}
	
	public static String getDeviceID() {
		return sDeviceId;
	}
	
	public static String getVersion() {
		return sVersion; 
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLoadApps != null) {
			mLoadApps.dismiss();
		}
	}
}
