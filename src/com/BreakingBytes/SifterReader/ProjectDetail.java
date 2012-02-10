package com.BreakingBytes.SifterReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class ProjectDetail extends Activity {
	public static final String PROJECT_COMPANY = "primary_company_name";
	public static final String PROJECT_ARCHIVED = "archived";
	public static final String PROJECT_URL = "url";
	public static final String ISSUES_URL = "issues_url";
	public static final String MILESTONES_URL = "milestones_url";

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project);

		// capture our View elements
		TextView projectName = (TextView) findViewById(R.id.project_name);
		TextView company = (TextView) findViewById(R.id.company);
		TextView projectArchived = (TextView) findViewById(R.id.project_archived);
		TextView projectURL = (TextView) findViewById(R.id.project_url);
		TextView issuesURL = (TextView) findViewById(R.id.issues_url);
		TextView milestonesURL = (TextView) findViewById(R.id.milestones_url);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			try {
				JSONObject project = new JSONObject(extras.getString(SifterReader.PROJECTS));
				if (project != null && checkFields(project)) {
					projectName.setText(project.getString(SifterReader.PROJECT_NAME));
					company.setText(project.getString(PROJECT_COMPANY));
					projectArchived.setText(project.getString(PROJECT_ARCHIVED));
					projectURL.setText(project.getString(PROJECT_URL));
					Linkify.addLinks(projectURL, Linkify.WEB_URLS);
					issuesURL.setText(project.getString(ISSUES_URL));
					Linkify.addLinks(issuesURL, Linkify.WEB_URLS);
					milestonesURL.setText(project.getString(MILESTONES_URL));
					Linkify.addLinks(milestonesURL, Linkify.WEB_URLS);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean checkFields(JSONObject project) throws JSONException {
		String API_URL = "api_url";
		String API_ISSUES_URL = "api_issues_url";
		String API_MILESTONES_URL = "api_milestones_url";
		String API_CATEGORIES_URL = "api_categories_url";
		String API_PEOPLE_URL = "api_people_url";
		JSONArray fieldNames = project.names();
		int numKeys = fieldNames.length();
		for (int j = 0;j < numKeys; j++) {
			if (!SifterReader.PROJECT_NAME.equals(fieldNames.getString(j)) &&
					!PROJECT_COMPANY.equals(fieldNames.getString(j)) &&
					!PROJECT_ARCHIVED.equals(fieldNames.getString(j)) &&
					!PROJECT_URL.equals(fieldNames.getString(j)) &&
					!ISSUES_URL.equals(fieldNames.getString(j)) &&
					!MILESTONES_URL.equals(fieldNames.getString(j)) &&
					!API_URL.equals(fieldNames.getString(j)) &&
					!API_ISSUES_URL.equals(fieldNames.getString(j)) &&
					!API_MILESTONES_URL.equals(fieldNames.getString(j)) &&
					!API_CATEGORIES_URL.equals(fieldNames.getString(j)) &&
					!API_PEOPLE_URL.equals(fieldNames.getString(j)))
				return false;
		}
		return true;
	}
}
