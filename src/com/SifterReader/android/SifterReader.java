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
import android.view.View;
import android.widget.EditText;

public class SifterReader extends Activity {

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onClickProjects(View v) {
		
		String[] keys = new String[2];
		keys = getKeys();

		// Create an Intent to launch Projects Activity
		Intent intent = new Intent(this, ProjectsActivity.class);
		intent.putExtra("domain", keys[0]);
		intent.putExtra("accessKey", keys[1]);

		startActivity(intent);
	}

	public String[] getKeys() {
		
		String[] keys = new String[2];
		
		// capture our View elements
		EditText domainEntry = (EditText) findViewById(R.id.domainEntry);
		EditText accessKeyEntry = (EditText) findViewById(R.id.accessKeyEntry);
		
		keys[0] = domainEntry.getText().toString();
		keys[1] = accessKeyEntry.getText().toString();
		return keys;
	}
	
}