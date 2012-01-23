package com.SifterReader.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class CategoriesActivity extends ListActivity {

	public static final String CATEGORY_NAME = "name";
	
	// Members
	private JSONArray mCategoryArray;
	private JSONObject[] mAllCategories;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		registerForContextMenu(getListView());
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			try {
				JSONArray categories = new JSONArray(extras.getString(SifterReader.CATEGORIES));
				if (categories != null) {
					mCategoryArray = categories; 
					getCategories();
					fillData();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void getCategories() {
		int numberCategories = mCategoryArray.length();
		JSONObject[] allCategories = new JSONObject[numberCategories];

		// categories
		try {
			for (int i = 0; i < numberCategories; i++) {
				allCategories[i] = mCategoryArray.getJSONObject(i);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mAllCategories = allCategories;
	}
	
	private void fillData() {
		int cNum = mAllCategories.length;
		String[] c = new String[cNum];
		try {
			for (int j = 0; j < cNum; j++) {
				c[j] = mAllCategories[j].getString(CATEGORY_NAME);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, c));
	}
}
