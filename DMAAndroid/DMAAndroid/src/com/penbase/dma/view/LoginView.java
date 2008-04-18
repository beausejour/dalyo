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
package com.penbase.dma.view;

import com.penbase.dma.Dma;
import com.penbase.dma.DmaHttpClient;
import com.penbase.dma.R;
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

public class LoginView extends Activity implements OnClickListener {
	TextView tx_login;
	TextView tx_password;
	CheckBox cb_remember_me;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.login_layout);
		Button bt = (Button) findViewById(R.id.ok);
		bt.setOnClickListener(this);
		tx_login = (TextView) findViewById(R.id.textLogin);
		tx_password = (TextView) findViewById(R.id.textePasswd);
		cb_remember_me = (CheckBox) findViewById(R.id.remember_me_cb);
	}

	@Override
	public void onClick(View arg0) {
		if ("".equals(tx_login.getText().toString())
				|| "".equals(tx_password.getText().toString())) {
			showAlert("Alert", 1,"Your credential!", "OK", false);
			return;
		}
		// showAlert("Alert", "let's go!", "OK", false);
		DmaHttpClient client = new DmaHttpClient();
		String rep = client.Authentication(tx_login.getText().toString(),
				tx_password.getText().toString());
		if (rep == null) {
			showAlert("Alert", 1,"Error : " + client.GetLastError(), "OK", false);
		} else {
			// save user info.
			Log.v("Dalyo", Boolean.toString(cb_remember_me.isChecked()));
			SharedPreferences settings = getSharedPreferences(Dma.PREFS_NAME,
					MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("RememberMe", cb_remember_me.isChecked());
			editor.putString("ApplicationList", rep);
			editor.commit();
			Dma.GetListApplicationFromXml(rep);
			startSubActivity(new Intent(this, ApplicationListView.class), 0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean r = super.onCreateOptionsMenu(menu);
		menu.add(0, Menu.FIRST, getResources().getString(R.string.about));
		return r;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, Item item) {
		switch (item.getId()) {
		case 0:

			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}