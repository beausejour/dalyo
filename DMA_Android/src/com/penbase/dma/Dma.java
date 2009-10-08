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

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

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
	private SQLiteDatabase mSqlite = null;
	private String mUsername;
	private String mUserpassword;
	private boolean mRememberme;
	private String mApplicationlist;
	private boolean mStorageCardRemoved;
	private Resources mResources;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mResources = getResources();
		sDeviceId = ((TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		checkSystemTable();
		mAlertDialog = new AlertDialog.Builder(this).create();

		PackageInfo pi = null;
		try {
			pi = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		sVersion = pi.versionName;

		if (!mRememberme) {
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				setContentView(R.layout.login_layout);
			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				setContentView(R.layout.login_layout_landscape);
			}
			Button bt = (Button) findViewById(R.id.ok);
			if (!mStorageCardRemoved) {
				bt.setOnClickListener(this);
			}
			mTx_login = (TextView) findViewById(R.id.textLogin);
			mTx_password = (TextView) findViewById(R.id.textePasswd);
			mCb_remember_me = (CheckBox) findViewById(R.id.remember_me_cb);
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View aboutView = mInflater.inflate(R.layout.about, null, false);
			TextView nameVersionView = (TextView) aboutView
					.findViewById(R.id.nameversion);
			nameVersionView.setText("Dalyo Mobile Agent " + Dma.getVersion());
			mAboutDialog = new AlertDialog.Builder(this).setIcon(
					android.R.drawable.ic_dialog_info).setTitle(
					R.string.menu_about).setView(aboutView).create();
		} else {
			this.finish();
			Intent intent = new Intent(this, ApplicationListView.class);
			intent.putExtra("USERNAME", mUsername);
			intent.putExtra("USERPWD", mUserpassword);
			intent.putExtra("APPLICATIONLIST", mApplicationlist);
			startActivityForResult(intent, 0);
		}
		if (mStorageCardRemoved) {
			Toast.makeText(this, R.string.nosdcard, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View arg0) {
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		final String login = mTx_login.getText().toString();
		final String pwd = mTx_password.getText().toString();
		if (login.equals("") && pwd.equals("")) {
			findViewById(R.id.textLogin).startAnimation(shake);
			findViewById(R.id.textePasswd).startAnimation(shake);
			return;
		} else if (login.equals("")) {
			findViewById(R.id.textLogin).startAnimation(shake);
			return;
		} else if (pwd.equals("")) {
			findViewById(R.id.textePasswd).startAnimation(shake);
			return;
		}
		mLoadApps = ProgressDialog.show(this, mResources
				.getText(R.string.waiting), mResources
				.getText(R.string.connecting), true, false);

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mHandler != null) {
					mServerResponse = new DmaHttpClient(login.trim(), null)
							.Authentication(login.trim(), Common
									.md5(pwd.trim()));
					mHandler.sendEmptyMessage(0);
				}
			}
		}).start();
	}

	/**
	 * Checks the result of authentication, display the ApplicationListView if
	 * authentication success
	 */
	private void callApplicationListView() {
		if (mServerResponse == null) {
			mLoadApps.dismiss();
			showMessage(mResources.getText(R.string.error), mResources
					.getString(R.string.connectionerror));
		} else if (mServerResponse.equals(String
				.valueOf(ErrorCode.UNAUTHORIZED))) {
			mLoadApps.dismiss();
			showMessage(mResources.getText(R.string.error), mResources
					.getString(R.string.connectionerror));
		} else {
			mLoadApps.setMessage(mResources
					.getString(R.string.loadingapplicationlist));
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						mUsername = mTx_login.getText().toString();
						mUserpassword = Common.md5(mTx_password.getText()
								.toString().trim());
						// save user info
						ContentValues values = new ContentValues();
						values.put("Username", mUsername);
						values.put("Userpassword", mUserpassword);
						values.put("Rememberme", String.valueOf(mCb_remember_me
								.isChecked()));
						values.put("Applicationlist", mServerResponse);
						mSqlite.update(Constant.SYSTEMTABLE, values,
								"ID = \"0\"", null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(Dma.this,
							ApplicationListView.class);
					intent.putExtra("USERNAME", mUsername);
					intent.putExtra("USERPWD", mUserpassword);
					intent.putExtra("APPLICATIONLIST", mServerResponse);
					startActivityForResult(intent, 0);
					Dma.this.finish();
				}
			}).start();
		}
	}

	private void checkSystemTable() {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_REMOVED)
				|| storageState.equals(Environment.MEDIA_BAD_REMOVAL)) {
			mStorageCardRemoved = true;
			mRememberme = false;
		} else {
			StringBuffer systemTable = new StringBuffer(Constant.APPPACKAGE);
			File dalyoDirectory = new File(systemTable.toString());
			if (!dalyoDirectory.exists()) {
				dalyoDirectory.mkdir();
			}
			systemTable.append(Constant.DATABASEDIRECTORY);
			File databaseDirectory = new File(systemTable.toString());
			if (!databaseDirectory.exists()) {
				databaseDirectory.mkdir();
			}
			systemTable.append(Constant.SYSTEMTABLE);
			File systemTableFile = new File(systemTable.toString());
			if (systemTableFile.exists()) {
				// Get saved values
				mSqlite = SQLiteDatabase.openOrCreateDatabase(systemTable
						.toString(), null);
				Cursor cursor = mSqlite.query(Constant.SYSTEMTABLE,
						new String[] { "Username", "Userpassword",
								"Rememberme", "Applicationlist" }, null, null,
						null, null, null);
				cursor.moveToFirst();
				mUsername = cursor.getString(0);
				mUserpassword = cursor.getString(1);
				mRememberme = Boolean.valueOf(cursor.getString(2));
				mApplicationlist = cursor.getString(3);
				cursor.close();
			} else {
				mSqlite = SQLiteDatabase.openOrCreateDatabase(systemTable
						.toString(), null);
				// Create system table
				mSqlite.execSQL(Constant.CREATE_SYSTEMTABLE);

				// Insert default values
				ContentValues values = new ContentValues();
				values.put("Id", "0");
				values.put("Username", Constant.EMPTY_STRING);
				values.put("Userpassword", Constant.EMPTY_STRING);
				values.put("Rememberme", Constant.EMPTY_STRING);
				values.put("Applicationlist", Constant.EMPTY_STRING);
				mSqlite.insert(Constant.SYSTEMTABLE, null, values);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.menu_about).setIcon(
				android.R.drawable.ic_menu_info_details);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.menu_quit).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
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

	public void showMessage(CharSequence title, String message) {
		mAlertDialog.setTitle(title);
		mAlertDialog.setMessage(message);
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
		if (mSqlite != null) {
			mSqlite.close();
		}
		sDeviceId = null;
		sVersion = null;
	}
}
