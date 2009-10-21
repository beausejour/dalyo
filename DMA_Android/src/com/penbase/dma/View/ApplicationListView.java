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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.penbase.dma.Common;
import com.penbase.dma.Dma;
import com.penbase.dma.R;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DesignTag;
import com.penbase.dma.Constant.ResourceTag;
import com.penbase.dma.Dalyo.Application;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Displays a GridView which contains all applications' icons
 */
public class ApplicationListView extends Activity implements
		OnItemSelectedListener, OnItemClickListener {
	private static ApplicationListView sApplicationListView = null;
	private ApplicationAdapter mAdapter;
	private TextView mApplicationName;
	private static ProgressDialog sLoadProgressDialog = null;
	private static ProgressDialog sUpdateProgressDialog = null;
	private Intent mIntent = null;
	private static HashMap<String, String> sApplicationInfos = null;
	private DmaHttpClient mDmahttpclient;
	private GridView mGridView;
	private AlertDialog mAboutDialog;
	private LayoutInflater mInflater;
	private ArrayList<Application> mApplicationList;
	private AlertDialog mAlertDialog;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mAlertDialog.setMessage(mResources
						.getString(R.string.connectionunavailable));
				mAlertDialog.show();
				break;
			case 1:
				mAlertDialog.setMessage(mResources
						.getString(R.string.connectionerror));
				mAlertDialog.show();
				break;
			case 2:
				mAdapter.removeAll();
				addApplicationInAdapter();
				mAdapter.notifyDataSetChanged();
				sUpdateProgressDialog.dismiss();
				break;
			}
		}
	};
	private String mUsername;
	private String mUserpassword;
	private SQLiteDatabase mSqlite;
	private Resources mResources;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mResources = getResources();
		sApplicationListView = this;
		sApplicationInfos = new HashMap<String, String>();
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.wave_scale);
		setContentView(R.layout.applicationlist);

		StringBuffer title = new StringBuffer("Dalyo ");

		mUsername = getIntent().getStringExtra("USERNAME");
		mUserpassword = getIntent().getStringExtra("USERPWD");

		if (mUsername.length() > 0) {
			title.append("(");
			title.append(mUsername);
			title.append(")");
		}

		setTitle(title.toString());

		mAlertDialog = new AlertDialog.Builder(ApplicationListView.this)
				.create();
		mAlertDialog.setTitle("Dalyo");

		ImageView imageView = (ImageView) findViewById(R.id.banner);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			imageView.setBackgroundResource(R.drawable.banniere_dalyo);
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			imageView.setBackgroundResource(R.drawable.banniere_dalyo1);
		}

		mApplicationName = (TextView) findViewById(R.id.appnametv);
		mAdapter = new ApplicationAdapter(this);

		String xml = getIntent().getStringExtra("APPLICATIONLIST");
		createApplicationListFromXml(xml, false);
		addApplicationInAdapter();

		mGridView = (GridView) findViewById(R.id.appsgv);
		mGridView.setVerticalScrollBarEnabled(false);
		mGridView.setScrollingCacheEnabled(false);
		mGridView.setLayoutAnimation(new LayoutAnimationController(animation));
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
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View aboutView = mInflater.inflate(R.layout.about, null, false);
		TextView nameVersionView = (TextView) aboutView
				.findViewById(R.id.nameversion);
		nameVersionView.setText("Dalyo Mobile Agent " + Dma.getVersion());
		mAboutDialog = new AlertDialog.Builder(this).setIcon(
				android.R.drawable.ic_dialog_info)
				.setTitle(R.string.menu_about).setView(aboutView).create();
		checkSystemTable();
	}

	private void addApplicationInAdapter() {
		int size = mApplicationList.size();
		for (int i = 0; i < size; i++) {
			Application application = mApplicationList.get(i);
			mAdapter.addApplication(application);
			if (i == 0) {
				mApplicationName.setText(application.getName());
			}
		}
	}
	
	private void checkSystemTable() {
		StringBuffer systemTable = new StringBuffer(Constant.APPPACKAGE);
		systemTable.append(Constant.DATABASEDIRECTORY).append(
				Constant.SYSTEMTABLE);
		mSqlite = SQLiteDatabase.openOrCreateDatabase(systemTable.toString(),
				null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.menu_update).setIcon(
				R.drawable.ic_menu_refresh);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.menu_about).setIcon(
				android.R.drawable.ic_menu_info_details);
		menu.add(Menu.NONE, 2, Menu.NONE, R.string.menu_logout).setIcon(
				R.drawable.ic_menu_logout);
		menu.add(Menu.NONE, 3, Menu.NONE, R.string.menu_quit).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			update();
			break;
		case 1:
			mAboutDialog.show();
			break;
		case 2:
			logout();
			break;
		case 3:
			finish();
			break;
		}
		return true;
	}

	/**
	 * Parses the given xml to get the necessary values and creates applications
	 * 
	 * @param xml
	 *            which contains application's information
	 */
	private void createApplicationListFromXml(String xml, boolean update) {
		HashMap<String, Application> applicationMap = new HashMap<String, Application>();
		Application app = null;
		boolean isInAppId = false;
		boolean isInAppTitle = false;
		boolean isInAppBuild = false;
		boolean isInAppSub = false;
		boolean isInAppDBID = false;
		boolean isInAppVer = false;
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(xml));
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagName = xpp.getName();
				if (eventType == XmlPullParser.START_DOCUMENT) {

				} else if (eventType == XmlPullParser.END_DOCUMENT) {

				} else if (eventType == XmlPullParser.START_TAG) {
					if (tagName.equals(DesignTag.APP)) {
						app = new Application();
					} else if (tagName.equals(DesignTag.LOGIN_ID)) {
						isInAppId = true;
					} else if (tagName.equals(DesignTag.LOGIN_TIT)) {
						isInAppTitle = true;
					} else if (tagName.equals(DesignTag.LOGIN_BLD)) {
						isInAppBuild = true;
					} else if (tagName.equals(DesignTag.LOGIN_SUB)) {
						isInAppSub = true;
					} else if (tagName.equals(DesignTag.LOGIN_DBID)) {
						isInAppDBID = true;
					} else if (tagName.equals(DesignTag.LOGIN_VER)) {
						isInAppVer = true;
					} else if (tagName.equals(DesignTag.APPICON)) {
						String iconId = xpp.getAttributeValue(null,
								ResourceTag.RESOURCES_R_ID);
						String iconHashcode = xpp.getAttributeValue(null,
								ResourceTag.RESOURCES_R_HASHCODE);
						String iconExt = xpp.getAttributeValue(null,
								ResourceTag.RESOURCES_R_EXT);
						checkApplicationIcon(app, iconId, iconHashcode, iconExt);
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					if (tagName.equals(DesignTag.APP)) {
						app.setIconRes(R.drawable.splash);
						applicationMap.put(app.getName(), app);
					} else if (tagName.equals(DesignTag.LOGIN_ID)) {
						isInAppId = false;
					} else if (tagName.equals(DesignTag.LOGIN_TIT)) {
						isInAppTitle = false;
					} else if (tagName.equals(DesignTag.LOGIN_BLD)) {
						isInAppBuild = false;
					} else if (tagName.equals(DesignTag.LOGIN_SUB)) {
						isInAppSub = false;
					} else if (tagName.equals(DesignTag.LOGIN_DBID)) {
						isInAppDBID = false;
					} else if (tagName.equals(DesignTag.LOGIN_VER)) {
						isInAppVer = false;
					}
				} else if (eventType == XmlPullParser.TEXT) {
					String value = xpp.getText();
					if (isInAppId) {
						app.setAppId(value);
					} else if (isInAppTitle) {
						app.setName(value);
					} else if (isInAppBuild) {
						app.setAppBuild(value);
					} else if (isInAppSub) {
						app.setSubId(value);
					} else if (isInAppDBID) {
						app.setDbId(value);
					} else if (isInAppVer) {
						app.setAppVer(value);
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (update) {
			Set<String> applicationNames = applicationMap.keySet();
			ArrayList<Application> applications = mApplicationList;
			for (Application application : applications) {
				String appName = application.getName();
				String appId = application.getAppId();
				boolean deleteApplication = true;
				if (applicationNames.contains(appName)) {
					deleteApplication = false;
					Application newApplication = applicationMap.get(appName);
					String oldDbid = application.getDbId();
					// If db id changed, rename database file and delete db.xml
					if (!oldDbid.equals(newApplication.getDbId())) {
						backupDatabase(appId, oldDbid);
						deleteXmlFile(appId, Constant.DBXML);
					}

					// If other id changed, delete design.xml, behavior.xml,
					// resource.xml
					if ((!application.getAppBuild().equals(
							newApplication.getAppBuild()))
							|| (!application.getAppVer().equals(
									newApplication.getAppVer()))
							|| (!application.getSubId().equals(
									newApplication.getSubId()))) {
						deleteXmlFile(appId, Constant.DESIGNXML);
						deleteXmlFile(appId, Constant.BEHAVIORXML);
						deleteXmlFile(appId, Constant.RESOURCEXML);
					}
				}
				if (deleteApplication) {
					deleteApplication(appId);
				}
			}
		}
		sortApplicationsList(applicationMap);
	}

	private void checkApplicationIcon(Application application, String iconId,
			String hashcode, String ext) {
		boolean willDownload = true;
		StringBuffer iconFilePath = new StringBuffer(Constant.APPPACKAGE);
		iconFilePath.append(Constant.USERDIRECTORY);
		File directory = new File(iconFilePath.toString());
		if (!directory.exists()) {
			directory.mkdir();
		}

		iconFilePath.append(mUsername).append("/");
		directory = new File(iconFilePath.toString());
		if (!directory.exists()) {
			directory.mkdir();
		}

		iconFilePath.append(Constant.RESOURCE);
		directory = new File(iconFilePath.toString());
		if (!directory.exists()) {
			directory.mkdir();
		}

		iconFilePath.append(iconId).append(".").append(ext);
		File iconFile = new File(iconFilePath.toString());
		if (iconFile.exists()) {
			// check icon's hashcode
			byte[] bytes = null;
			try {
				bytes = Common.getBytesFromFile(iconFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String hexString = Common.md5HexStringFromBytes(bytes);
			if (hexString.equals(hashcode)) {
				willDownload = false;
			}
		}
		if (willDownload) {
			// download application's icon
			mDmahttpclient = new DmaHttpClient(mUsername, null);
			String urlRequest = mDmahttpclient.generateRegularUrlRequest(
					application.getAppId(), application.getAppVer(),
					application.getAppBuild(), application.getSubId(),
					mUsername, mUserpassword);
			StringBuffer action = new StringBuffer("act=getresource");
			action.append(urlRequest).append("&resourceid=").append(iconId);
			byte[] resourceStream = mDmahttpclient.sendPost(action.toString());
			String hexString = Common.md5HexStringFromBytes(resourceStream);
			if (hexString.equals(hashcode)) {
				Common.streamToFile(resourceStream, iconFilePath.toString(),
						true);
			}
		}
		application.setIconPath(iconFilePath.toString());
	}

	/**
	 * Get user name
	 * 
	 * @return
	 */
	private String getUserName() {
		return mUsername;
	}

	private void backupDatabase(String appId, String dbId) {
		String userName = getUserName();
		StringBuffer databaseFilePath = new StringBuffer(Constant.APPPACKAGE);
		databaseFilePath.append(Constant.USERDIRECTORY).append(userName)
				.append("/");
		databaseFilePath.append(appId).append("/");
		databaseFilePath.append(Constant.APPDB);

		StringBuffer backupdatabaseFilePath = new StringBuffer(databaseFilePath
				.toString());
		backupdatabaseFilePath.append("_").append(dbId);

		File backupDatabaseFile = new File(backupdatabaseFilePath.toString());
		File databaseFile = new File(databaseFilePath.toString());
		databaseFile.renameTo(backupDatabaseFile);
	}

	/**
	 * Delete xml file
	 * 
	 * @param appName
	 *            application name
	 * @param xmlName
	 *            xml name
	 */
	private void deleteXmlFile(String appId, String xmlName) {
		String userName = getUserName();
		StringBuffer filePath = new StringBuffer(Constant.APPPACKAGE);
		filePath.append(Constant.USERDIRECTORY).append(userName).append("/");
		filePath.append(appId).append("/").append(xmlName);
		File xmlFile = new File(filePath.toString());
		if (xmlFile.exists()) {
			xmlFile.delete();
		}
	}

	/**
	 * Delete application directory and rename database file(backup user old
	 * data)
	 * 
	 * @param appName
	 *            application name
	 */
	private void deleteApplication(String appId) {
		String userName = getUserName();
		StringBuffer directoryPath = new StringBuffer(Constant.APPPACKAGE);
		directoryPath.append(Constant.USERDIRECTORY).append(userName).append(
				"/");
		directoryPath.append(appId).append("/");
		File appDirectory = new File(directoryPath.toString());
		if (deleteDirectory(appDirectory)) {
			// Delete all xml files
		}
	}

	/**
	 * Delete directory's content
	 * 
	 * @param directory
	 * @return
	 */
	private boolean deleteDirectory(File directory) {
		if (directory.isDirectory()) {
			String[] children = directory.list();
			int length = children.length;
			for (int i = 0; i < length; i++) {
				if (!children[i].equals(Constant.DBXML)) {
					boolean success = deleteDirectory(new File(directory,
							children[i]));
					if (!success) {
						return false;
					}
				}
			}
		}
		return directory.delete();
	}

	/**
	 * Sorts the applications list in alphabetical order
	 * 
	 * @param applicationMap
	 *            The application hashmap, key is application's name and value
	 *            is application
	 */
	private void sortApplicationsList(
			HashMap<String, Application> applicationMap) {
		mApplicationList = new ArrayList<Application>();
		ArrayList<String> tempList = new ArrayList<String>();
		tempList.addAll(applicationMap.keySet());
		Collections.sort(tempList);
		int appsLen = applicationMap.size();
		for (int i = 0; i < appsLen; i++) {
			mApplicationList.add(applicationMap.get(tempList.get(i)));
		}
	}

	/**
	 * Deletes preference data and finishes this activity
	 */
	public void logout() {
		ContentValues values = new ContentValues();
		values.put("Username", "");
		values.put("Userpassword", "");
		values.put("Rememberme", "");
		values.put("Applicationlist", "");
		mSqlite.update(Constant.SYSTEMTABLE, values, "ID = \"0\"", null);
		this.finish();
		startActivity(new Intent(this, Dma.class));
	}

	/**
	 * Updates the application list
	 */
	public void update() {
		sUpdateProgressDialog = ProgressDialog.show(this, mResources
				.getText(R.string.waiting), mResources
				.getText(R.string.updatingapplicationlist), true, false);
		new Thread(new Runnable() {
			@Override
			public void run() {
				mDmahttpclient = new DmaHttpClient(mUsername, null);
				String appsList = mDmahttpclient.Authentication(mUsername,
						mUserpassword);

				if (getNetworkInfo() == null) {
					sUpdateProgressDialog.dismiss();
					mHandler.sendEmptyMessage(0);
				} else {
					if (appsList != null) {
						createApplicationListFromXml(appsList, true);
						ContentValues values = new ContentValues();
						values.put("Applicationlist", appsList);
						mSqlite.update(Constant.SYSTEMTABLE, values,
								"ID = \"0\"", null);
						mHandler.sendEmptyMessage(2);
					} else {
						sUpdateProgressDialog.dismiss();
						mHandler.sendEmptyMessage(1);
					}
				}
			}
		}).start();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		mApplicationName.setText(mApplicationList.get(position).getName());
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	/**
	 * Launches the selected application
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View v, final int position,
			long id) {
		final Application application = mApplicationList.get(position);
		final String applicationId = application.getAppId();
		sApplicationInfos.put("Username", mUsername);
		sApplicationInfos.put("Userpassword", mUserpassword);
		sApplicationInfos.put("AppId", applicationId);
		sApplicationInfos.put("AppVer", application.getAppVer());
		sApplicationInfos.put("AppBuild", application.getAppBuild());
		sApplicationInfos.put("SubId", application.getSubId());
		sApplicationInfos.put("DbId", application.getDbId());
		sLoadProgressDialog = ProgressDialog.show(this, mResources
				.getText(R.string.waiting), mResources
				.getText(R.string.preparingapplication), true, false);
		mIntent = new Intent(this, ApplicationView.class);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ApplicationView.prepareData();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				mIntent.putExtra("ID", applicationId);
				mIntent.putExtra("USERNAME", mUsername);
				mIntent.putExtra("TITLE", application.getName());
				startActivity(mIntent);
				sLoadProgressDialog.dismiss();
			}
		}).start();
	}

	public static HashMap<String, String> getApplicationsInfo() {
		return sApplicationInfos;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSqlite.close();
		sApplicationListView = null;
		sLoadProgressDialog = null;
		sUpdateProgressDialog = null;
		sApplicationInfos = null;
	}

	public static void quit() {
		sApplicationListView.finish();
	}

	public static NetworkInfo getNetworkInfo() {
		ConnectivityManager manager = (ConnectivityManager) sApplicationListView
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return manager.getActiveNetworkInfo();
	}
}
