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
	private ProgressDialog loadApps;
	private static Context context;
	
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
		if (applicationList == null) {
			applicationList = new ArrayList<Application>();
		}
		else {
			applicationList.clear();
		}
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
		
		ArrayList<String> tempList = new ArrayList<String>();
		tempList.addAll(applicationMap.keySet());
		Collections.sort(tempList);
		
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
		DmaHttpClient client = new DmaHttpClient();
		final String rep = client.Authentication(tx_login.getText().toString().trim(),
				tx_password.getText().toString().trim());
		if (rep == null) {
			alertDialog.setMessage("Check your username or password!");
			alertDialog.show();
		}
		else {
			loadApps = ProgressDialog.show(this, "Please wait...", "Loading application list...", true, false);
			new Thread() {
				public void run() {
					try {
						// save user info
						SharedPreferences.Editor editorPrefs = getSharedPreferences(Constant.PREFNAME, MODE_PRIVATE).edit();
						editorPrefs.putBoolean("RememberMe", cb_remember_me.isChecked());
						editorPrefs.putString("Username", tx_login.getText().toString());
						editorPrefs.putString("Userpassword", tx_password.getText().toString());
						editorPrefs.putString("ApplicationList", rep);
						editorPrefs.commit();
						Dma.GetListApplicationFromXml(rep);
					}
					catch(Exception e)
					{e.printStackTrace();}
					Dma.this.finish();
					loadApps.dismiss();
					startActivityForResult(new Intent(Dma.this, ApplicationListView.class), 0);
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

	//@Override
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
		//Get imei
		String imei = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		return "zfsqsrffusds";
		//return imei;
	}
	
	public static float getVersion() {
		float result = (float) 1.0;
		return result;
	}
}
