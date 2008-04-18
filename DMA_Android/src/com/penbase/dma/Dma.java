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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.penbase.dma.Dalyo.Application;
import com.penbase.dma.view.LoginView;
import com.penbase.dma.xml.XmlTag;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


public class Dma extends Activity {
	public static final String PREFS_NAME = "DmaPrefsFile";
	public static ArrayList<Application> applicationList = null;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.dma_layout);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,
				MODE_PRIVATE);
		boolean b = settings.getBoolean("RememberMe", false);
		Log.v("Dalyo", "start = " + Boolean.toString(b));
		if (!b) {
			startSubActivity(new Intent(this, LoginView.class), 0);
		} else {
			showAlert("shared", 1, settings.getString("SharedPreferences", null), "", false);
			String xml = settings.getString("SharedPreferences", null);
			GetListApplicationFromXml(xml);
		}
	}
	/**
	 * 
	 * @param xml
	 */
	public static void GetListApplicationFromXml(String xml) {
		if (applicationList == null) {
			applicationList = new ArrayList<Application>();
		} else {
			applicationList.clear();
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {			
			db = dbf.newDocumentBuilder();
			ByteArrayInputStream stream = new ByteArrayInputStream(xml
					.getBytes());						
			
			System.out.println(stream);
			Document doc = db.parse(stream);
			NodeList root = doc.getElementsByTagName(XmlTag.X35);
			NodeList apps = root.item(0).getChildNodes();
			for (int s = 0; s < apps.getLength(); s++) {
				NodeList els = apps.item(s).getChildNodes();
				Application app = new Application();
				for (int t = 0; t < els.getLength(); t++) {
					Node noeud = els.item(t);
					if (noeud.getNodeType() == Node.ELEMENT_NODE) {
						if (XmlTag.ID.equals(noeud.getLocalName())) {
							app.setMDalyoId(Integer.parseInt(noeud.getChildNodes().item(0).getNodeValue()));
						} else if (XmlTag.TIT.equals(els.item(t).getNodeName())) {
							app.setMName(noeud.getChildNodes().item(0).getNodeValue());
						}
					}
				}
				app.setMIconRes(R.drawable.splash);
				applicationList.add(app);
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
	}
}
