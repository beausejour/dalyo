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
import com.penbase.dma.Dalyo.Application;
import com.penbase.dma.view.ApplicationListView;
import com.penbase.dma.xml.XmlTag;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,
				MODE_PRIVATE);
		boolean b = settings.getBoolean("RememberMe", false);
		Log.v("Dalyo", "start = " + Boolean.toString(b));
		if (!b) 
		{
			setContentView(R.layout.login_layout);		
			Button bt = (Button) findViewById(R.id.ok);
			bt.setOnClickListener(this);
			tx_login = (TextView) findViewById(R.id.textLogin);
			tx_password = (TextView) findViewById(R.id.textePasswd);
			cb_remember_me = (CheckBox) findViewById(R.id.remember_me_cb);
		}
		else 
		{
			//showAlert("shared", 1, settings.getString("SharedPreferences", null), "", false);
			String xml = settings.getString("ApplicationList", null);						
			GetListApplicationFromXml(xml);
			//this.finish();
			startSubActivity(new Intent(this, ApplicationListView.class), 0);
		}
	}
	/**
	 * 
	 * @param xml
	 */
	public static void GetListApplicationFromXml(String xml) 
	{
		if (applicationList == null)
		{
			applicationList = new ArrayList<Application>();
		}
		else 
		{
			applicationList.clear();
		}
		
		Document doc = DmaHttpClient.CreateParseDocument(xml);
		
		NodeList root = doc.getElementsByTagName(XmlTag.TAG_ROOT);
		NodeList apps = root.item(0).getChildNodes();
		for (int s = 0; s < apps.getLength(); s++) 
		{
			NodeList els = apps.item(s).getChildNodes();
			Application app = new Application();
			for (int t = 0; t < els.getLength(); t++) 
			{
				Node noeud = els.item(t);
				if (noeud.getNodeType() == Node.ELEMENT_NODE) 
				{					
					if (noeud.getNodeName().equals(XmlTag.TAG_LOGIN_ID))
					{
						app.setAppId(Integer.parseInt(noeud.getChildNodes().item(0).getNodeValue()));
					}		
					else if (noeud.getNodeName().equals(XmlTag.TAG_LOGIN_TIT))
					{
						app.setName(noeud.getChildNodes().item(0).getNodeValue());
					}
					else if (noeud.getNodeName().equals(XmlTag.TAG_LOGIN_BLD))
					{
						app.setAppBuild(Integer.parseInt(noeud.getChildNodes().item(0).getNodeValue()));
					}
					else if (noeud.getNodeName().equals(XmlTag.TAG_LOGIN_SUB))
					{
						app.setSubId(Integer.parseInt(noeud.getChildNodes().item(0).getNodeValue()));
					}
					else if (noeud.getNodeName().equals(XmlTag.TAG_LOGIN_DBID))
					{
						app.setDbId(Integer.parseInt(noeud.getChildNodes().item(0).getNodeValue()));
					}
					else if (noeud.getNodeName().equals(XmlTag.TAG_LOGIN_VER))
					{
						app.setAppVer(Integer.parseInt(noeud.getChildNodes().item(0).getNodeValue()));
					}
				}
			}
			app.setIconRes(R.drawable.splash);
			applicationList.add(app);
		}
	}
	
	@Override
	public void onClick(View arg0) 
	{
		if ("".equals(tx_login.getText().toString())
				|| "".equals(tx_password.getText().toString())) 
		{
			showAlert("Alert", 1,"Your credential!", "OK", false);
			return;
		}
		// showAlert("Alert", "let's go!", "OK", false);
		DmaHttpClient client = new DmaHttpClient();
		String rep = client.Authentication(tx_login.getText().toString(),
				tx_password.getText().toString());
		
		if (rep == null) 
		{
			showAlert("Alert", 1,"Error : " + client.GetLastError(), "OK", false);
		}
		else 
		{								
			// save user info.
			Log.v("Dalyo", Boolean.toString(cb_remember_me.isChecked()));			
			SharedPreferences.Editor editorPrefs = getSharedPreferences(Dma.PREFS_NAME,
					MODE_PRIVATE).edit();
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
		switch (item.getId()) {
		case Menu.FIRST:
			return true;
		case Menu.FIRST+1:
			this.finish();
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
