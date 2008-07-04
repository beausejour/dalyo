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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.penbase.dma.Constant.XmlTag;
import com.penbase.dma.Dalyo.Application;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;
import com.penbase.dma.View.ApplicationListView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyProperties;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Menu.Item;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class Dma extends Activity implements OnClickListener{
	public static final String PREFS_NAME = "DmaPrefsFile";
	public static ArrayList<Application> applicationList = null;
	private TextView tx_login;
	private TextView tx_password;
	private CheckBox cb_remember_me;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.login_layout);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);	
		boolean rememberMe = settings.getBoolean("RememberMe", false);
		Log.v("Dalyo", "start = " + Boolean.toString(rememberMe));
		
		if (!rememberMe){
			setContentView(R.layout.login_layout);
			Button bt = (Button) findViewById(R.id.ok);
			bt.setOnClickListener(this);
			tx_login = (TextView) findViewById(R.id.textLogin);
			tx_password = (TextView) findViewById(R.id.textePasswd);
			cb_remember_me = (CheckBox) findViewById(R.id.remember_me_cb);
		}
		else{
			String xml = settings.getString("ApplicationList", null);
			Log.i("info", "remember me");
			GetListApplicationFromXml(xml);
			//this.finish();
			startSubActivity(new Intent(this, ApplicationListView.class), 0);
		}
	}

	public static void GetListApplicationFromXml(String xml){
		if (applicationList == null){
			applicationList = new ArrayList<Application>();
		}
		else{
			applicationList.clear();
		}
		Document doc = DmaHttpClient.CreateParseDocument(xml, null);
		NodeList root = doc.getElementsByTagName(XmlTag.ROOT);
		NodeList apps = root.item(0).getChildNodes();
		int appsLen = apps.getLength();
		for (int s = 0; s < appsLen; s++){
			NodeList els = apps.item(s).getChildNodes();
			Application app = new Application();
			for (int t = 0; t < els.getLength(); t++){
				Node noeud = els.item(t);
				if (noeud.getNodeType() == Node.ELEMENT_NODE){
					if (noeud.getNodeName().equals(XmlTag.LOGIN_ID)){
						app.setAppId(noeud.getChildNodes().item(0).getNodeValue());
					}		
					else if (noeud.getNodeName().equals(XmlTag.LOGIN_TIT)){
						app.setName(noeud.getChildNodes().item(0).getNodeValue());
					}
					else if (noeud.getNodeName().equals(XmlTag.LOGIN_BLD)){
						app.setAppBuild(noeud.getChildNodes().item(0).getNodeValue());
					}
					else if (noeud.getNodeName().equals(XmlTag.LOGIN_SUB)){
						app.setSubId(noeud.getChildNodes().item(0).getNodeValue());
					}
					else if (noeud.getNodeName().equals(XmlTag.LOGIN_DBID)){
						app.setDbId(noeud.getChildNodes().item(0).getNodeValue());
					}
					else if (noeud.getNodeName().equals(XmlTag.LOGIN_VER)){
						app.setAppVer(noeud.getChildNodes().item(0).getNodeValue());
					}
				}
			}
			app.setIconRes(R.drawable.splash);
			applicationList.add(app);
		}
	}
	
	@Override
	public void onClick(View arg0){
		if ("".equals(tx_login.getText().toString())
				|| "".equals(tx_password.getText().toString())){
			showAlert("Alert", 1,"Your credential!", "OK", false);
			return;
		}
		DmaHttpClient client = new DmaHttpClient();
		String rep = client.Authentication(tx_login.getText().toString(),
				tx_password.getText().toString());
		if (rep == null){
			showAlert("Alert", 1,"Error : " + client.GetLastError()+" check your username or password", "OK", false);
		}
		else{
			Log.i("info", "rewrite pref file");
			// save user info.
			SharedPreferences.Editor editorPrefs = getSharedPreferences(Dma.PREFS_NAME, MODE_PRIVATE).edit();
			editorPrefs.putBoolean("RememberMe", cb_remember_me.isChecked());
			editorPrefs.putString("Username", tx_login.getText().toString());
			editorPrefs.putString("Userpassword", tx_password.getText().toString());
			editorPrefs.putString("ApplicationList", rep);
			editorPrefs.commit();
			
			Dma.GetListApplicationFromXml(rep);
			this.finish();
			startSubActivity(new Intent(this, ApplicationListView.class), 0);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean r = super.onCreateOptionsMenu(menu);
		menu.add(0, Menu.FIRST, getResources().getString(R.string.about));
		menu.add(0, Menu.FIRST+1, getResources().getString(R.string.quit));
		return r;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, Item item) {
		switch (item.getId()){
			case Menu.FIRST:
				return true;
			case Menu.FIRST+1:
				this.finish();
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	public static String getDeviceID(){
		String imei = android.os.SystemProperties.get(TelephonyProperties.PROPERTY_IMEI,
		"-1");
		return "defr";
		//return imei;
	}
	
	public static float getVersion(){
		float result = (float) 1.0;
		return result;
	}
	
	public void errorDialog(String message){
		new AlertDialog.Builder(this).setMessage(message).setTitle("Error").show();
	}
}
