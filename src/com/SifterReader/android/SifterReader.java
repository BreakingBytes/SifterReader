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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class SifterReader extends ListActivity {

	// Constants
	public static final int LOGIN_ID = Menu.FIRST; // id for login menu option
	public static final int ACTIVITY_LOGIN = 0; // id for intent result
	public static final String DOMAIN = "domain"; // key to unbundle domain from
	// intent extras
	public static final String ACCESS_KEY = "accessKey"; // key to unbundle

	// access key from
	// intent extras

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	private void fillData(SifterProj[] proj) {
		int pNum = proj.length;
		String[] p = new String[pNum];
		for (int j = 0; j < pNum; j++) {
			p[j] = proj[j].name;
		}
		setListAdapter(
				new ArrayAdapter<String>(
						this, android.R.layout.simple_list_item_1, p));
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

	/**
	 * Determine activity by result code ACTIVITY_LOGIN unbundle domain & access
	 * key
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Bundle extras = intent.getExtras();
		switch (requestCode) {
		case ACTIVITY_LOGIN:
			String domain = extras.getString(SifterReader.DOMAIN);
			String accessKey = extras.getString(SifterReader.ACCESS_KEY);
			SifterProj[] proj = loadProjects(domain, accessKey);
			fillData(proj);
			break;
		}
	}

	private SifterProj[] loadProjects(String domain, String accessKey) {
		// capture our View elements
		// TextView issues = (TextView) findViewById(R.id.projects);

		URL sifter;
		try {
			// create URL object to SifterAPI
			sifter = new URL("https://" + domain
					+ ".sifterapp.com/api/projects");
			URLConnection sifterConnection = null;
			try {
				// open connection to SifterAPI
				sifterConnection = sifter.openConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// issues.append(e.toString());
				return null;
			}
			// add Access Key to header request
			sifterConnection.setRequestProperty("X-Sifter-Token", accessKey);

			// add Accept: application/json to header - also not necessary
			sifterConnection.addRequestProperty("Accept", "application/json");

			// create buffer and open input stream
			BufferedReader in = new BufferedReader(new InputStreamReader(
					sifterConnection.getInputStream()));
			String inputLine;
			StringBuilder x = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				x.append(inputLine);
			}
			in.close();

			// initialize "projects" JSONObject from string
			JSONObject projects = new JSONObject(x.toString());

			// array of projects
			JSONArray projectArray = projects.getJSONArray("projects");
			int numberProjects = projectArray.length();
			JSONObject[] p = new JSONObject[numberProjects];

			// projects
			for (int i = 0; i < numberProjects; i++) {
				p[i] = projectArray.getJSONObject(i);
			}

			// project field names
			// JSONArray fieldNames = p[0].names();
			// int numKeys = p[0].length();
			SifterProj[] proj = new SifterProj[numberProjects];
			for (int j = 0; j < numberProjects; j++) {
				proj[j] = new SifterProj(p[j].getString("api_url"),
						p[j].getString("archived"),
						p[j].getString("api_issues_url"),
						p[j].getString("milestones_url"),
						p[j].getString("api_milestones_url"),
						p[j].getString("api_categories_url"),
						p[j].getString("issues_url"), p[j].getString("name"),
						p[j].getString("url"),
						p[j].getString("api_people_url"),
						p[j].getString("primary_company_name"));
				// issues.append("************ project: " + (j + 1) +
				// " ************\n");
				// for (int i = 0; i < numKeys; i++) {
				// issues.append(fieldNames.getString(i) + " : "
				// + p[j].getString(fieldNames.getString(i)) + "\n");
				// }
				// issues.append(proj[j].name + "\n");
			}
			return proj;
			// issues.append(y);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// issues.append(e.toString());
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// //issues.append(e.toString());
			return null;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// issues.append(e.toString());
			return null;
		}
	}
}