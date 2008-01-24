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

import com.penbase.dma.view.ApplicationListView;
import com.penbase.dma.view.LoginView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class Dma extends Activity {
	public static final String PREFS_NAME = "DmaPrefsFile";
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.dma_layout);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		boolean b = settings.getBoolean("RememberMe", false);
		Log.v("Dalyo", Boolean.toString(b));
		if (!b) {			
			startSubActivity(new Intent(this, LoginView.class), 0);
		} else {
			startSubActivity(new Intent(this, ApplicationListView.class), 0);			
		}
	}
}