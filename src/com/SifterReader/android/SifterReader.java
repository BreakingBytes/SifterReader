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
	// intent extras bundle key
	public static final String DOMAIN = "domain";
	public static final String ACCESS_KEY = "accessKey";
	// header requests
	public static final String X_SIFTER_TOKEN = "X-Sifter-Token";
	public static final String APPLICATION_JSON = "application/json";
	public static final String HEADER_REQUEST_ACCEPT = "Accept";
	public static final String HTTPS_PREFIX = "https://";
	public static final String SIFTERAPI_URL = ".sifterapp.com/api/";
	public static final String PROJECTS = "projects";
	public static final String PROJECT_NAME = "name";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	private void fillData(JSONObject[] allProjects) {
		int pNum = allProjects.length;
		String[] p = new String[pNum];
		try {
			for (int j = 0; j < pNum; j++) {
				p[j] = allProjects[j].getString(SifterReader.PROJECT_NAME);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, p));
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
			URLConnection sifterConnection = getSifterConnection(domain,
					SifterReader.PROJECTS);
			if (sifterConnection == null)
				break;
			JSONObject[] allProjects = loadProjects(sifterConnection, accessKey);
			fillData(allProjects);
			break;
		}
	}

	private URLConnection getSifterConnection(String domain,
			String resourceEndpoint) {
		URL sifter;
		URLConnection sifterConnection = null;
		try {
			// create URL object to SifterAPI
			sifter = new URL(SifterReader.HTTPS_PREFIX + domain
					+ SifterReader.SIFTERAPI_URL + resourceEndpoint);
			// open connection to SifterAPI
			sifterConnection = sifter.openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sifterConnection;
	}

	private JSONObject[] loadProjects(URLConnection sifterConnection,
			String accessKey) {
		// send header requests
		sifterConnection.setRequestProperty(SifterReader.X_SIFTER_TOKEN,
				accessKey);
		sifterConnection.addRequestProperty(SifterReader.HEADER_REQUEST_ACCEPT,
				SifterReader.APPLICATION_JSON);

		JSONObject projects = null;
		JSONObject[] allProjects = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					sifterConnection.getInputStream()));
			String inputLine;
			StringBuilder x = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				x.append(inputLine);
			}
			in.close();

			// initialize "projects" JSONObject from string
			projects = new JSONObject(x.toString());

			// array of projects
			JSONArray projectArray = projects
					.getJSONArray(SifterReader.PROJECTS);
			int numberProjects = projectArray.length();
			allProjects = new JSONObject[numberProjects];

			// projects
			for (int i = 0; i < numberProjects; i++) {
				allProjects[i] = projectArray.getJSONObject(i);
			}

			// project field names
			// JSONArray fieldNames = p[0].names();
			// int numKeys = p[0].length();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return allProjects;
	}
}