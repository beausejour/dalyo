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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DesignTag;
import com.penbase.dma.Dalyo.Application;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;
import com.penbase.dma.View.ApplicationListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class Dma extends Activity implements OnClickListener {
	public static ArrayList<Application> applicationList = null;
	private TextView tx_login;
	private TextView tx_password;
	private CheckBox cb_remember_me;
	private AlertDialog alertDialog;
	private ProgressDialog loadApps = null;
	private static Context context;
	private String serverResponse = null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					buildAppsList();
					break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle icicle) {
		context = this;
		super.onCreate(icicle);
		
		SharedPreferences settings = getSharedPreferences(Constant.PREFNAME, MODE_PRIVATE);
		boolean rememberMe = settings.getBoolean("RememberMe", false);
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Error");
		if (!rememberMe) {
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				setContentView(R.layout.login_layout);
			}
			else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				setContentView(R.layout.login_layout_landscape);
			}
			Button bt = (Button) findViewById(R.id.ok);
			bt.setOnClickListener(this);
			tx_login = (TextView) findViewById(R.id.textLogin);
			tx_password = (TextView) findViewById(R.id.textePasswd);
			cb_remember_me = (CheckBox) findViewById(R.id.remember_me_cb);
		}
		else {
			String xml = settings.getString("ApplicationList", null);
			GetListApplicationFromXml(xml);
			this.finish();
			startActivityForResult(new Intent(this, ApplicationListView.class), 0);
		}
	}

	public static void GetListApplicationFromXml(String xml) {
		/*if (applicationList == null) {
			applicationList = new ArrayList<Application>();
		}
		else {
			applicationList.clear();
		}*/
		HashMap<String, Application> applicationMap = new HashMap<String, Application>();
		Document doc = DmaHttpClient.CreateParseDocument(xml, null);
		NodeList root = doc.getElementsByTagName(DesignTag.ROOT);
		NodeList apps = root.item(0).getChildNodes();
		int appsLen = apps.getLength();
		for (int s = 0; s < appsLen; s++) {
			NodeList els = apps.item(s).getChildNodes();
			Application app = new Application();
			int elsLength = els.getLength();
			for (int t = 0; t < elsLength; t++) {
				Node noeud = els.item(t);
				if (noeud.getNodeType() == Node.ELEMENT_NODE) {
					if (noeud.getNodeName().equals(DesignTag.LOGIN_ID)) {
						app.setAppId(noeud.getChildNodes().item(0).getNodeValue());
					}		
					else if (noeud.getNodeName().equals(DesignTag.LOGIN_TIT)) {
						app.setName(noeud.getChildNodes().item(0).getNodeValue());
					}
					else if (noeud.getNodeName().equals(DesignTag.LOGIN_BLD)) {
						app.setAppBuild(noeud.getChildNodes().item(0).getNodeValue());
					}
					else if (noeud.getNodeName().equals(DesignTag.LOGIN_SUB)) {
						app.setSubId(noeud.getChildNodes().item(0).getNodeValue());
					}
					else if (noeud.getNodeName().equals(DesignTag.LOGIN_DBID)) {
						app.setDbId(noeud.getChildNodes().item(0).getNodeValue());
					}
					else if (noeud.getNodeName().equals(DesignTag.LOGIN_VER)) {
						app.setAppVer(noeud.getChildNodes().item(0).getNodeValue());
					}
				}
			}
			app.setIconRes(R.drawable.splash);
			applicationMap.put(app.getName(), app);
			//app.setIconRes(R.drawable.icon);
		}
		
		
		buildApplicationsList(applicationMap);
		/*ArrayList<String> tempList = new ArrayList<String>();
		tempList.addAll(applicationMap.keySet());
		Collections.sort(tempList);
		
		for (int i=0; i<appsLen; i++) {
			applicationList.add(applicationMap.get(tempList.get(i)));
		}*/
	}
	
	public static void buildApplicationsList(HashMap<String, Application> applicationMap) {
		if (applicationList == null) {
			applicationList = new ArrayList<Application>();
		}
		else {
			applicationList.clear();
		}
		ArrayList<String> tempList = new ArrayList<String>();
		tempList.addAll(applicationMap.keySet());
		Collections.sort(tempList);
		int appsLen = applicationMap.size();
		for (int i=0; i<appsLen; i++) {
			applicationList.add(applicationMap.get(tempList.get(i)));
		}
	}
	
	@Override
	public void onClick(View arg0) {
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		if ("".equals(tx_login.getText().toString())
				&& "".equals(tx_password.getText().toString())) {
			findViewById(R.id.textLogin).startAnimation(shake);
	        findViewById(R.id.textePasswd).startAnimation(shake);
	        return;
		}
		else if ("".equals(tx_login.getText().toString())) {
			findViewById(R.id.textLogin).startAnimation(shake);
			return;
		}
		else if ("".equals(tx_password.getText().toString())) {
			findViewById(R.id.textePasswd).startAnimation(shake);
			return;
		}
		loadApps = ProgressDialog.show(this, "Please wait...", "Connecting to server...", true, false);
		final DmaHttpClient client = new DmaHttpClient();
		
		ConnectThread connectThread = new ConnectThread(client, handler);
		connectThread.start();
	}
	
	public void buildAppsList() {
		if (serverResponse == null) {
			loadApps.dismiss();
			alertDialog.setMessage("Connection failed!");
			alertDialog.show();
		}
		else if (serverResponse.equals("error")) {
			loadApps.dismiss();
			alertDialog.setMessage("Check your username or password!");
			alertDialog.show();
		}
		else {
			loadApps.setMessage("Loading application list...");
			new Thread() {
				public void run() {
					try {
						// save user info
						SharedPreferences.Editor editorPrefs = getSharedPreferences(Constant.PREFNAME, MODE_PRIVATE).edit();
						editorPrefs.putBoolean("RememberMe", cb_remember_me.isChecked());
						editorPrefs.putString("Username", tx_login.getText().toString());
						editorPrefs.putString("Userpassword", tx_password.getText().toString());
						editorPrefs.putString("ApplicationList", serverResponse);
						editorPrefs.commit();
						Dma.GetListApplicationFromXml(serverResponse);
					}
					catch(Exception e) {
						e.printStackTrace();
					}

					startActivityForResult(new Intent(Dma.this, ApplicationListView.class), 0);
					Dma.this.finish();
				}
			}.start();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean r = super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 0, Menu.NONE, "About");
		menu.add(Menu.NONE, 1, Menu.NONE, "Quit");
		return r;
	}

	public boolean onOptionsItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				return true;
			case 1:
				this.finish();
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	public static String getDeviceID() {
		String imei = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		return "zfikn";
		//return imei;
	}
	
	public static float getVersion() {
		float result = (float) 1.0;
		return result;
	}
	
	public class ConnectThread extends Thread {
		private DmaHttpClient client;
		private Handler handler;
		Thread m_thread = null;
		
		public ConnectThread(DmaHttpClient c, Handler h) {
			this.client = c;
			this.handler = h;
		}
		
		@Override
		public void run() {
			if (handler != null) {
				serverResponse = client.Authentication(tx_login.getText().toString().trim(),
						tx_password.getText().toString().trim());
				handler.sendEmptyMessage(0);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (loadApps != null) {
			loadApps.dismiss();
		}
	}
	
	public static ArrayList<Application> getApplications() {
		return applicationList;
	}
}
