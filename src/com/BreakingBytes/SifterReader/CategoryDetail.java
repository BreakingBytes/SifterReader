package com.BreakingBytes.SifterReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class CategoryDetail extends Activity {
	
	public static final String CATEGORY_ISSUES_URL = "issues_url";
	private SifterHelper mSifterHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categories);

		mSifterHelper = new SifterHelper(this);
		
		TextView categoryName = (TextView) findViewById(R.id.category_name);
		TextView issuesURL = (TextView) findViewById(R.id.category_issues_url);

		Bundle extras = getIntent().getExtras();
		if (extras == null)
			return;
		try {
			JSONObject category = new JSONObject(extras.getString(CategoriesActivity.CATEGORY));
			if (category != null && checkFields(category)) {
				categoryName.setText(category.getString(CategoriesActivity.CATEGORY_NAME));
				issuesURL.setText(category.getString(CATEGORY_ISSUES_URL));
				Linkify.addLinks(issuesURL, Linkify.WEB_URLS);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			mSifterHelper.onException(e.toString());
		}
	}
	
	private boolean checkFields(JSONObject category) throws JSONException {
		String API_ISSUES_URL = "api_issues_url";
		JSONArray fieldNames = category.names();
		int numKeys = fieldNames.length();
		for (int j = 0;j < numKeys; j++) {
			if (!CategoriesActivity.CATEGORY_NAME.equals(fieldNames.getString(j)) &&
					!CATEGORY_ISSUES_URL.equals(fieldNames.getString(j)) &&
					!API_ISSUES_URL.equals(fieldNames.getString(j)))
				return false;
		}
		return true;
	}
}

