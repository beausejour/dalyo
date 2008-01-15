package com.penbase.dma;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DMAAndroid extends Activity implements OnClickListener {
	TextView tx_login;
	TextView tx_password;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
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
		showAlert("Alert", "let's go!", "OK", false);
		DmaHttpClient client = new DmaHttpClient();
		String rep = client.Authentication(tx_login.getText().toString(), tx_password.getText().toString());
		if (rep != null)
			showAlert("Alert", rep, "OK", false);
		else
			showAlert("Alert", "No response", "OK", false);
	}
}