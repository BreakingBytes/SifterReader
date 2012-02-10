package com.BreakingBytes.SifterReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class MilestoneDetail extends Activity {
	
	public static final String MILESTONE_DUE_DATE = "due_date";
	public static final String MILESTONE_ISSUES_URL = "issues_url";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.milestones);

		// capture our View elements
		TextView milestoneName = (TextView) findViewById(R.id.milestone_name);
		TextView dueDate = (TextView) findViewById(R.id.milestone_due_date);
		TextView issuesURL = (TextView) findViewById(R.id.milestone_issues_url);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			try {
				JSONObject milestone = new JSONObject(extras.getString(MilestonesActivity.MILESTONE));
				if (milestone != null && checkFields(milestone)) {
					milestoneName.setText(milestone.getString(MilestonesActivity.MILESTONE_NAME));
					dueDate.setText(milestone.getString(MILESTONE_DUE_DATE));
					issuesURL.setText(milestone.getString(MILESTONE_ISSUES_URL));
					Linkify.addLinks(issuesURL, Linkify.WEB_URLS);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean checkFields(JSONObject milestone) throws JSONException {
		String API_ISSUES_URL = "api_issues_url";
		JSONArray fieldNames = milestone.names();
		int numKeys = fieldNames.length();
		for (int j = 0;j < numKeys; j++) {
			if (!MilestonesActivity.MILESTONE_NAME.equals(fieldNames.getString(j)) &&
					!MILESTONE_DUE_DATE.equals(fieldNames.getString(j)) &&
					!MILESTONE_ISSUES_URL.equals(fieldNames.getString(j)) &&
					!API_ISSUES_URL.equals(fieldNames.getString(j)))
				return false;
		}
		return true;
	}
}

