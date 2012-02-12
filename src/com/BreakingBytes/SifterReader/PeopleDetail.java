package com.BreakingBytes.SifterReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class PeopleDetail extends Activity {
	
	public static final String USERNAME = "username";
	public static final String EMAIL = "email";
	public static final String PEOPLE_ISSUES_URL = "issues_url";
	private SifterHelper mSifterHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people);
		
		mSifterHelper = new SifterHelper(this);

		// capture our View elements
		TextView username = (TextView) findViewById(R.id.username);
		TextView firstName = (TextView) findViewById(R.id.first_name);
		TextView lastName = (TextView) findViewById(R.id.last_name);
		TextView email = (TextView) findViewById(R.id.email);
		TextView issuesURL = (TextView) findViewById(R.id.people_issues_url);

		Bundle extras = getIntent().getExtras();
		if (extras == null)
			return;
		try {
			JSONObject people = new JSONObject(extras.getString(PeopleActivity.PEOPLE));
			if (people != null && checkFields(people)) {
				username.setText(people.getString(USERNAME));
				firstName.setText(people.getString(PeopleActivity.FIRST_NAME));
				lastName.setText(people.getString(PeopleActivity.LAST_NAME));
				email.setText(people.getString(EMAIL));
				Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
				issuesURL.setText(people.getString(PEOPLE_ISSUES_URL));
				Linkify.addLinks(issuesURL, Linkify.WEB_URLS);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			mSifterHelper.onException(e.toString()); // return not needed
		}
	}
	
	private boolean checkFields(JSONObject people) throws JSONException {
		String API_ISSUES_URL = "api_issues_url";
		JSONArray fieldNames = people.names();
		int numKeys = fieldNames.length();
		for (int j = 0;j < numKeys; j++) {
			if (!USERNAME.equals(fieldNames.getString(j)) &&
					!PeopleActivity.FIRST_NAME.equals(fieldNames.getString(j)) &&
					!PeopleActivity.LAST_NAME.equals(fieldNames.getString(j)) &&
					!EMAIL.equals(fieldNames.getString(j)) &&
					!PEOPLE_ISSUES_URL.equals(fieldNames.getString(j)) &&
					!API_ISSUES_URL.equals(fieldNames.getString(j)))
				return false;
		}
		return true;
	}
}

