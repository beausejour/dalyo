package com.penbase.dma.view;

import com.penbase.dma.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Menu.Item;

public class ApplicationListView extends Activity {
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.applicationlist_layout);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean r = super.onCreateOptionsMenu(menu);
		menu.add(0, 0, getResources().getString(R.string.menu_logout));
		menu.add(0, 1, getResources().getString(R.string.menu_about));
		return r;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, Item item) {
		switch (item.getId()) {
		case 0:
			this.finish();
			return true;
		case 1:
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
