package com.SifterReader.android;

/*      SifterReader.java
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
 *      MA 02110-1301, USA. */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SifterReader extends ListActivity {

	// Constants
	public static final int LOGIN_ID = Menu.FIRST; // id for login menu option
	public static final int MILESTONES_ID = Menu.FIRST + 1; // id for list menu option
	public static final int CATEGORIES_ID = Menu.FIRST + 2;
	public static final int PEOPLE_ID = Menu.FIRST + 3;
	public static final int ISSUES_ID = Menu.FIRST + 4;
	public static final int ACTIVITY_LOGIN = 0; // id for intent result
	public static final String LOGIN_ERROR = "error"; // url error key
	public static final String LOGIN_DETAIL = "detail";
	public static final String KEY_FILE = "key_file";
	public static final String DOMAIN = "domain";
	public static final String ACCESS_KEY = "accessKey";
	public static final String X_SIFTER_TOKEN = "X-Sifter-Token";
	public static final String APPLICATION_JSON = "application/json";
	public static final String HEADER_REQUEST_ACCEPT = "Accept";
	public static final String HTTPS_PREFIX = "https://";
	public static final String PROJECTS_URL = ".sifterapp.com/api/projects";
	public static final String PROJECTS = "projects";
	public static final String PROJECT_NAME = "name";
	public static final String MILESTONES_URL = "api_milestones_url";
	public static final String MILESTONES = "milestones";
	public static final String CATEGORIES_URL = "api_categories_url";
	public static final String CATEGORIES = "categories";
	public static final String PEOPLE_URL = "api_people_url";
	public static final String PEOPLE = "people";
	public static final String ISSUES_URL = "api_issues_url";
	public static final String ISSUES = "issues";
	
	// Members
	private JSONObject[] mAllProjects;
	private String mDomain;
	private String mAccessKey;
	private JSONObject mLoginError = new JSONObject();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setTitle(R.string.projects);
		registerForContextMenu(getListView());

		File keyFile = getFileStreamPath(KEY_FILE);
		if (!keyFile.exists()) {
			loginKeys();
		} else {
			boolean fileReadError = false;
			try {
				BufferedReader in = new BufferedReader(new FileReader(keyFile));
				String inputLine;
				StringBuilder x = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					x.append(inputLine);
				}
				in.close();
				JSONObject loginKeys = new JSONObject(x.toString());
				mDomain = loginKeys.getString(DOMAIN);
				mAccessKey = loginKeys.getString(ACCESS_KEY);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fileReadError = true;
			} catch (IOException e) {
				e.printStackTrace();
				fileReadError = true;
			} catch (JSONException e) {
				e.printStackTrace();
				fileReadError = true;
			}
			if (!fileReadError) { 
				String projectsURL = HTTPS_PREFIX + mDomain + PROJECTS_URL;
				URLConnection sifterConnection = getSifterConnection(projectsURL);
				if (sifterConnection != null) {
					JSONObject sifterJSONObject = getSifterJSONObject(sifterConnection);
					if (!getSifterError(sifterJSONObject)) {
						loadProjects(sifterJSONObject);
						fillData();
					} else {
						loginKeys();
					}
				} else {
					loginKeys(); // TODO is this needed? should check in getSifterConnection
				}
			} else {
				loginKeys();
			}
		}
	}

	/** Method to pass project names to list adapter. */
	private void fillData() {
		int pNum = mAllProjects.length;
		String[] p = new String[pNum];
		try {
			for (int j = 0; j < pNum; j++) {
				p[j] = mAllProjects[j].getString(PROJECT_NAME);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, p));
	}

	/** Menu button options. */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, LOGIN_ID, 0, R.string.menu_login);
		return result;
	}

	/** Methods for selected menu option.
	 *  LOGIN_ID option calls loginKeys method. */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case LOGIN_ID:
			loginKeys(); // method that gets login
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/** Intent for LoginActivity to get domain and access key. */
	private void loginKeys() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra(DOMAIN, mDomain);
		intent.putExtra(ACCESS_KEY, mAccessKey);
		intent.putExtra(LOGIN_ERROR, mLoginError.toString());
		startActivityForResult(intent, ACTIVITY_LOGIN);
	}

	/** Intent for ProjectDetail activity for clicked project in list. */
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, ProjectDetail.class);
        intent.putExtra(PROJECTS, mAllProjects[(int)id].toString());
        // TODO use safe long typecast to int
        startActivity(intent);
    }
	
	/** List context menu options. */
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, MILESTONES_ID, 0, R.string.milestones);
        menu.add(0, CATEGORIES_ID, 0, R.string.categories);
        menu.add(0, PEOPLE_ID, 0, R.string.people);
        menu.add(0, ISSUES_ID, 0, R.string.issues);
    }

    /** Methods for selected list context menu option. */
	@Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case MILESTONES_ID:
                milestones(info.id);
                return true;
            case CATEGORIES_ID:
                categories(info.id);
                return true;
            case PEOPLE_ID:
                people(info.id);
                return true;
            case ISSUES_ID:
                issues(info.id);
                return true;
        }
        return super.onContextItemSelected(item);
    }
    /* TODO combine methods for milestones, categories, people into single method
	 * use switch in onContextItemSelected to select args to pass to method. */
	/** Intent for MilestonesActivity. */
    private void milestones(long id) {
    	String milestonesURL = null;
    	// get milestones url from project
    	try {
    		milestonesURL = mAllProjects[(int)id].getString(MILESTONES_URL);
    		// TODO use safe long typecast to int
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    	// get url connection
    	URLConnection sifterConnection = getSifterConnection(milestonesURL);
		if (sifterConnection == null)
			return;
		// get milestones
		JSONArray milestones = loadProjectDetails(sifterConnection, MILESTONES);
		// intent for MilestonesActivity
    	Intent intent = new Intent(this, MilestonesActivity.class);
		intent.putExtra(MILESTONES, milestones.toString());
		startActivity(intent);
	}
	
    /** Intent for CategoriesActivity. */
    private void categories(long id) {
		String categoriesURL = null;
    	// get categories url from project
    	try {
    		categoriesURL = mAllProjects[(int)id].getString(CATEGORIES_URL);
    		// TODO use safe long typecast to int
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    	// get url connection
    	URLConnection sifterConnection = getSifterConnection(categoriesURL);
		if (sifterConnection == null)
			return;
		// get categories
		JSONArray categories = loadProjectDetails(sifterConnection, CATEGORIES);
		// intent for CategoriesActivity
    	Intent intent = new Intent(this, CategoriesActivity.class);
		intent.putExtra(CATEGORIES, categories.toString());
		startActivity(intent);
	}
	
    /** Intent for PeopleActivity. */
    private void people(long id) {
		String peopleURL = null;
    	// get people url from project
    	try {
    		peopleURL = mAllProjects[(int)id].getString(PEOPLE_URL);
    		// TODO use safe long typecast to int
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    	// get url connection
    	URLConnection sifterConnection = getSifterConnection(peopleURL);
		if (sifterConnection == null)
			return;
		// get people
		JSONArray people = loadProjectDetails(sifterConnection, PEOPLE);
		// intent for PeopleActivity
    	Intent intent = new Intent(this, PeopleActivity.class);
		intent.putExtra(PEOPLE, people.toString());
		startActivity(intent);
	}
	
    /** Intent for IssuesActivity. */
    private void issues(long id) {
		String issueURL = null;
    	// get issues url from project
    	try {
    		issueURL = mAllProjects[(int)id].getString(ISSUES_URL);
    		// TODO use safe long typecast to int
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    	// get url connection
    	URLConnection sifterConnection = getSifterConnection(issueURL);
		if (sifterConnection == null)
			return;
		// get issues
		JSONObject issues = loadIssues(sifterConnection);
		// intent for PeopleActivity
    	Intent intent = new Intent(this, IssuesActivity.class);
		intent.putExtra(ISSUES, issues.toString());
		startActivity(intent);
	}
	
    /** Determine activity by result code. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
    		Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	Bundle extras = intent.getExtras();
    	if (extras != null) {
    		switch (requestCode) {
    		case ACTIVITY_LOGIN:
    			mDomain = extras.getString(DOMAIN);
    			mAccessKey = extras.getString(ACCESS_KEY);
    			String projectsURL = HTTPS_PREFIX + mDomain + PROJECTS_URL;
    			URLConnection sifterConnection = getSifterConnection(projectsURL);
    			if (sifterConnection != null) {
    				JSONObject sifterJSONObject = getSifterJSONObject(sifterConnection);
    				if (!getSifterError(sifterJSONObject)) {
    					loadProjects(sifterJSONObject);
    					fillData();
    					break;
    				} else {
    					loginKeys();
    				}
    			} else {
    				loginKeys();
    			}
    		}
    	}
    }

	private URLConnection getSifterConnection(String sifterURL) {
		URL sifter;
		URLConnection sifterConnection = null;
		try {
			// create URL object to SifterAPI
			sifter = new URL(sifterURL);
			// open connection to SifterAPI
			sifterConnection = sifter.openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			// TODO let user re-enter url or quit
		} catch (IOException e) {
			e.printStackTrace();
			// TODO inform user cannot connect to URL
			// display URL to check & let user re-enter or quit
		}
		return sifterConnection;
	}

	private InputStream getSifterInputStream(URLConnection sifterConnection) {
		// send header requests
		sifterConnection.setRequestProperty(X_SIFTER_TOKEN, mAccessKey);
		sifterConnection.addRequestProperty(HEADER_REQUEST_ACCEPT, APPLICATION_JSON);

		InputStream is = null;
		try {
			is = sifterConnection.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			// also catches FileNotFoundException: invalid domain
			// IOException: invalid access key
			HttpURLConnection httpSifterConnection = (HttpURLConnection)sifterConnection;
			is = httpSifterConnection.getErrorStream();
		}
		return is;
	}
	
	private JSONObject getSifterJSONObject(URLConnection sifterConnection) {
		JSONObject sifterJSONObject = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					getSifterInputStream(sifterConnection)));
			String inputLine;
			StringBuilder x = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				x.append(inputLine);
			}
			in.close();
			sifterJSONObject = new JSONObject(x.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sifterJSONObject;
	}
	
	private boolean getSifterError(JSONObject sifterJSONObject) {
		try {
			JSONArray sifterJSONObjFieldNames = sifterJSONObject.names();
			int numKeys = sifterJSONObjFieldNames.length();
			if (numKeys==2
					&& LOGIN_ERROR.equals(sifterJSONObjFieldNames.getString(0))
					&& LOGIN_DETAIL.equals(sifterJSONObjFieldNames.getString(1))) {
				// check for incorrect header
				// SifterAPI says:
				// {"error":"Invalid Account","detail":"Please correct the account subdomain."}
				// {"error":"Invalid Token","detail":"Please make sure that you are using the correct token."}
				mLoginError = sifterJSONObject;
				return true;
			}
			mLoginError.put(LOGIN_ERROR,getResources().getString(R.string.token_accepted));
			mLoginError.put(LOGIN_DETAIL,getResources().getString(R.string.token_accepted_msg));
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private void loadProjects(JSONObject sifterJSONObject) {
		try {
			// array of projects
			JSONArray projectArray = sifterJSONObject.getJSONArray(PROJECTS);
			int numberProjects = projectArray.length();
			JSONObject[] allProjects = new JSONObject[numberProjects];

			// projects
			for (int i = 0; i < numberProjects; i++) {
				allProjects[i] = projectArray.getJSONObject(i);
			}
			mAllProjects = allProjects;
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	private JSONArray loadProjectDetails(URLConnection sifterConnection, String projectDetail) {
		// send header requests
		sifterConnection.setRequestProperty(X_SIFTER_TOKEN, mAccessKey);
		sifterConnection.addRequestProperty(HEADER_REQUEST_ACCEPT, APPLICATION_JSON);

		JSONArray projectDetailArray = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					sifterConnection.getInputStream()));
			String inputLine;
			StringBuilder x = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				x.append(inputLine);
			}
			in.close();

			// initialize "projectDetails" JSONObject from string
			JSONObject projectDetails = new JSONObject(x.toString());
			// TODO check for incorrect header
			// JSON says did you enter access key
			
			// array of projectDetails
			projectDetailArray = projectDetails.getJSONArray(projectDetail);

		} catch (FileNotFoundException e) {
			e.printStackTrace(); // bad domain name
		} catch (IOException e) {
			e.printStackTrace(); // bad access key
		} catch (JSONException e) {
			e.printStackTrace(); // JSON object or array issue
		}
		return projectDetailArray;
	}
	
	private JSONObject loadIssues(URLConnection sifterConnection) {
		// send header requests
		sifterConnection.setRequestProperty(X_SIFTER_TOKEN, mAccessKey);
		sifterConnection.addRequestProperty(HEADER_REQUEST_ACCEPT, APPLICATION_JSON);

		JSONObject issues = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					sifterConnection.getInputStream()));
			String inputLine;
			StringBuilder x = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				x.append(inputLine);
			}
			in.close();

			// initialize "projectDetails" JSONObject from string
			issues = new JSONObject(x.toString());
			// TODO check for incorrect header
			// JSON says did you enter access key

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return issues;
	}
}