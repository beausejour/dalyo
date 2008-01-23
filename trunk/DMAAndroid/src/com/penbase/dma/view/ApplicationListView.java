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
import com.penbase.dma.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
			// save user info.
			SharedPreferences settings = getSharedPreferences(Dma.PREFS_NAME,
					MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("RememberMe", false);
			editor.commit();			
			this.finish();
			startSubActivity(new Intent(this, LoginView.class), 0);
			return true;
		case 1:
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
