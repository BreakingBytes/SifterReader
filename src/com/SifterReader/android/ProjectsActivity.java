package com.SifterReader.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ProjectsActivity extends Activity {

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.projects);

		// capture our View elements
		TextView issues = (TextView) findViewById(R.id.projects);

		String domain = getIntent().getStringExtra("domain");
		String accessKey = getIntent().getStringExtra("accessKey");
		
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
				issues.append(e.toString());
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
//			JSONArray fieldNames = p[0].names();
//			int numKeys = p[0].length();
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
				issues.append("************ project: " + (j + 1) + " ************\n");
//				for (int i = 0; i < numKeys; i++) {
//					issues.append(fieldNames.getString(i) + " : "
//							+ p[j].getString(fieldNames.getString(i)) + "\n");
//				}
				issues.append(proj[j].name + "\n");
			}

			// issues.append(y);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			issues.append(e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			issues.append(e.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			issues.append(e.toString());
		}

	}
}