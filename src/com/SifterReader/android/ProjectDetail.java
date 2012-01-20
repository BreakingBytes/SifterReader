package com.SifterReader.android;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ProjectDetail extends Activity {
	public static final String PROJECT_COMPANY = "primary_company_name";
	public static final String PROJECT_ARCHIVED = "archived";
	public static final String PROJECT_URL = "url";

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project);

		// capture our View elements
		TextView projectName = (TextView) findViewById(R.id.projectName);
		TextView company = (TextView) findViewById(R.id.company);
		TextView projectArchived = (TextView) findViewById(R.id.projectArchived);
		TextView projectURL = (TextView) findViewById(R.id.projectURL);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			try {
				JSONObject project = new JSONObject(extras.getString(SifterReader.PROJECTS));
				if (project != null) {
					projectName.setText(project.getString(SifterReader.PROJECT_NAME));
					company.setText(project.getString(PROJECT_COMPANY));
					projectArchived.setText(project.getString(PROJECT_ARCHIVED));
					projectURL.setText(project.getString(PROJECT_URL));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
