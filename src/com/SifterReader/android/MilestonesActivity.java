package com.SifterReader.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class MilestonesActivity extends ListActivity {
	
	// Members
	private JSONArray mMilestoneArray;
	private JSONObject[] mAllMilestones;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		registerForContextMenu(getListView());
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			try {
				JSONArray milestones = new JSONArray(extras.getString(SifterReader.MILESTONES));
				if (milestones != null) {
					mMilestoneArray = milestones; 
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		int numberMilestones = mMilestoneArray.length();
		JSONObject[] allMilestones = new JSONObject[numberMilestones];

		// projects
		for (int i = 0; i < numberMilestones; i++) {
			try {
				allMilestones[i] = mMilestoneArray.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mAllMilestones = allMilestones;
		fillData();
	}

	private void fillData() {
		int pNum = mAllMilestones.length;
		String[] m = new String[pNum];
		try {
			for (int j = 0; j < pNum; j++) {
				m[j] = mAllMilestones[j].getString(SifterReader.PROJECT_NAME);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, m));
	}



}
