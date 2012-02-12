package com.BreakingBytes.SifterReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PeopleActivity extends ListActivity {

	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String PEOPLE = "people";

	// Members
	private JSONArray mPeopleArray;
	private JSONObject[] mAllPeople;
	private SifterHelper mSifterHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		registerForContextMenu(getListView());
		
		mSifterHelper = new SifterHelper(this);

		Bundle extras = getIntent().getExtras();
		if (extras == null)
			return;
		try {
			JSONArray people = new JSONArray(extras.getString(SifterReader.PEOPLE));
			if (people != null) {
				mPeopleArray = people; 
				getPeople();
				fillData();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			mSifterHelper.onException(e.toString()); // return not needed
		}
	}

	private void getPeople() {
		int numberPeople = mPeopleArray.length();
		JSONObject[] allPeople = new JSONObject[numberPeople];
		try {
			for (int i = 0; i < numberPeople; i++)
				allPeople[i] = mPeopleArray.getJSONObject(i);
		} catch (JSONException e) {
			e.printStackTrace();
			mSifterHelper.onException(e.toString());
			return;
		}
		mAllPeople = allPeople;
	}

	private void fillData() {
		int pNum = mAllPeople.length;
		String[] p = new String[pNum];
		try {
			for (int j = 0; j < pNum; j++) {
				p[j] = mAllPeople[j].getString(FIRST_NAME);
				p[j] = p[j] + " " + mAllPeople[j].getString(LAST_NAME);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			mSifterHelper.onException(e.toString());
			return;
		}
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, p));
	}

	/** start PeopleDetail activity for clicked project in list. */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, PeopleDetail.class);
		intent.putExtra(PEOPLE, mAllPeople[(int)id].toString());
		// TODO use safe long typecast to int
		startActivity(intent);
	}
}
