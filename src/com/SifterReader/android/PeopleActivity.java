package com.SifterReader.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class PeopleActivity extends ListActivity {

	// Members
	private JSONArray mPeopleArray;
	private JSONObject[] mAllPeople;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		registerForContextMenu(getListView());
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			try {
				JSONArray people = new JSONArray(extras.getString(SifterReader.PEOPLE));
				if (people != null) {
					mPeopleArray = people; 
					getPeople();
					fillData();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void getPeople() {
		int numberPeople = mPeopleArray.length();
		JSONObject[] allPeople = new JSONObject[numberPeople];

		// people
		try {
			for (int i = 0; i < numberPeople; i++) {
				allPeople[i] = mPeopleArray.getJSONObject(i);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mAllPeople = allPeople;
	}
	
	private void fillData() {
		int pNum = mAllPeople.length;
		String[] m = new String[pNum];
		try {
			for (int j = 0; j < pNum; j++) {
				m[j] = mAllPeople[j].getString(SifterReader.NAME);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, m));
	}
}
