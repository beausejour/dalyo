package com.penbase.dma.view;

import com.penbase.dma.DmaHttpClient;
import com.penbase.dma.R;
import com.penbase.dma.R.id;
import com.penbase.dma.R.layout;
import com.penbase.dma.R.string;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Menu.Item;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LoginView extends Activity implements OnClickListener {
	TextView tx_login;
	TextView tx_password;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.login_layout);
		Button bt = (Button) findViewById(R.id.ok);
		bt.setOnClickListener(this);
		tx_login = (TextView) findViewById(R.id.textLogin);
		tx_password = (TextView) findViewById(R.id.textePasswd);
	}

	@Override
	public void onClick(View arg0) {
		if ("".equals(tx_login.getText().toString()) || "".equals(tx_password.getText().toString())) {
			showAlert("Alert", "Your credential!", "OK", false);
			return;
		}
		//showAlert("Alert", "let's go!", "OK", false);
		DmaHttpClient client = new DmaHttpClient();
		String rep = client.Authentication(tx_login.getText().toString(), tx_password.getText().toString());
		if (rep == null) {
			showAlert("Alert", "Error : " + client.GetLastError(), "OK", false);
		}
		else {
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