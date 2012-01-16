package com.SifterReader.android;

/*
 *      SifterReader.java
 *      
 *      Copyright 2012 Mark Mikofski <bwanamarko@yahoo.com>
 *      
 *      This program is free software; you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation; either version 2 of the License, or
 *      (at your option) any later version.
 *      
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *      
 *      You should have received a copy of the GNU General Public License
 *      along with this program; if not, write to the Free Software
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *      MA 02110-1301, USA.
 *      
 *      
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SifterReader extends Activity {
	
	public static final int LOGIN_ID = Menu.FIRST; // int id for login menu option
	public static final int ACTIVITY_LOGIN = 0; // int id for intent result

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	/** Menu option to enter login Domain and Access Key. */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, LOGIN_ID, 0, R.string.menu_insert);
        return result;
    }
	
	/** Callback for selected login menu option. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case LOGIN_ID:
        	loginKeys(); // method that gets login
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /** Method to get login Domain and Access Keys */
    private void loginKeys() {
    	Intent intent = new Intent(this, LoginActivity.class);
    	startActivityForResult(intent, ACTIVITY_LOGIN);
    }
    
//	public void onClickProjects(View v) {
//		
//		String[] keys = new String[2];
//		keys = getKeys();
//
//		// Create an Intent to launch Projects Activity
//		Intent intent = new Intent(this, ProjectsActivity.class);
//		intent.putExtra("domain", keys[0]);
//		intent.putExtra("accessKey", keys[1]);
//
//		startActivity(intent);
//	}

	
	
}