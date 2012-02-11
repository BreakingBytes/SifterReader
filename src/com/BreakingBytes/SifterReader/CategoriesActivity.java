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

public class CategoriesActivity extends ListActivity {

	public static final String CATEGORY_NAME = "name";
	public static final String CATEGORY = "category";
	
	// Members
	private JSONArray mCategoryArray;
	private JSONObject[] mAllCategories;
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
			JSONArray categories = new JSONArray(extras.getString(SifterReader.CATEGORIES));
			if (categories != null) {
				mCategoryArray = categories; 
				getCategories();
				fillData();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			mSifterHelper.onException(e.toString());
		}		
	}

	private void getCategories() {
		int numberCategories = mCategoryArray.length();
		JSONObject[] allCategories = new JSONObject[numberCategories];
		try {
			for (int i = 0; i < numberCategories; i++)
				allCategories[i] = mCategoryArray.getJSONObject(i);
		} catch (JSONException e) {
			e.printStackTrace();
			mSifterHelper.onException(e.toString());
		}
		mAllCategories = allCategories;
	}
	
	private void fillData() {
		int cNum = mAllCategories.length;
		String[] c = new String[cNum];
		try {
			for (int j = 0; j < cNum; j++)
				c[j] = mAllCategories[j].getString(CATEGORY_NAME);
		} catch (JSONException e) {
			e.printStackTrace();
			mSifterHelper.onException(e.toString());
		}
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, c));
	}
	
	/** start CategoryDetail activity for clicked project in list. */
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, CategoryDetail.class);
        intent.putExtra(CATEGORY, mAllCategories[(int)id].toString());
        // TODO use safe long typecast to int
        startActivity(intent);
    }
}
