package com.SifterReader.android;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class IssuesActivity extends ListActivity {

	public static final String NUMBER = "number";
	public static final String STATUS = "status";
	public static final String PRIORITY = "priority";
	public static final String SUBJECT = "subject";
	public static final String PAGE = "page";
	public static final String TOTAL_PAGES = "total_pages";
	public static final String NEXT_PAGE_URL = "next_page_url";
	public static final String PREVIOUS_PAGE_URL = "previous_page_url";
	public static final String GOTO_PAGE = "page";
	public static final String PER_PAGE = "per_page";
	
	// Members
	private SifterHelper mSifterHelper;
	private String mIssuesURL;
	private JSONObject mIssues = new JSONObject();
	private JSONObject[] mAllIssues;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issues_list);
		registerForContextMenu(getListView());
		
		mSifterHelper = new SifterHelper(this);
		
		TextView pageTotal = (TextView)findViewById(R.id.page_total);
		EditText pageNumber = (EditText)findViewById(R.id.page_number);
		Button gotoPageButton = (Button)findViewById(R.id.goto_page);
		Button prevPageButton = (Button)findViewById(R.id.previous_page);
		Button nextPageButton = (Button)findViewById(R.id.next_page);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			try {
				String issuesURL = extras.getString(SifterReader.ISSUES_URL);
				if (issuesURL != null)
					mIssuesURL = issuesURL;
				JSONObject issues = new JSONObject(extras.getString(SifterReader.ISSUES));
				if (issues != null) {
					mIssues = issues; 
					getIssues();
					pageNumber.setText(String.valueOf(mIssues.getInt(PAGE)));
					pageTotal.setText(" / " + String.valueOf(mIssues.getInt(TOTAL_PAGES)));

					prevPageButton.setOnClickListener(new View.OnClickListener() {
						// anonymous inner class
						public void onClick(View view) {
							loadIssuesPage(PREVIOUS_PAGE_URL);
						}
					});
					
					nextPageButton.setOnClickListener(new View.OnClickListener() {
						// anonymous inner class
						public void onClick(View view) {
							loadIssuesPage(NEXT_PAGE_URL);
						}
					});
					
					gotoPageButton.setOnClickListener(new View.OnClickListener() {
						// anonymous inner class
						public void onClick(View view) {
							EditText pageNumber = (EditText)findViewById(R.id.page_number);
							loadIssuesPage(pageNumber);
						}
					});
					
					fillData();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void getIssues() {
		JSONObject[] allIssues = null;
		try {
			// array of issues on first page
			JSONArray issuesArray = mIssues.getJSONArray(SifterReader.ISSUES);
			int numberIssues = issuesArray.length();
			allIssues = new JSONObject[numberIssues];

			// issues
			for (int i = 0; i < numberIssues; i++) {
				allIssues[i] = issuesArray.getJSONObject(i);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mAllIssues = allIssues;
	}
	
	private void fillData() {
		int iNum = mAllIssues.length;
		List<Map<String,String>> issuesList = new ArrayList<Map<String,String>>(iNum); 
		try {
			for (int j = 0; j < iNum; j++) {
				Map<String,String> map = new HashMap<String,String>();
				map.put(NUMBER,mAllIssues[j].getString(NUMBER));
				map.put(STATUS,mAllIssues[j].getString(STATUS));
				map.put(PRIORITY,mAllIssues[j].getString(PRIORITY));
				map.put(SUBJECT,mAllIssues[j].getString(SUBJECT));
				issuesList.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        ListAdapter adapter = new SimpleAdapter(this, issuesList,
        		R.layout.issue_row,
                new String[] {NUMBER, STATUS, PRIORITY, SUBJECT},
                new int[] {R.id.issue_number, R.id.issue_status, R.id.issue_priority, R.id.issue_subject});
        setListAdapter(adapter);
	}

	/** Intent for Project Details Activities. */
	private void loadIssuesPage(String PAGE_URL) {
		String projDetailURL = null;
		// get project detail url from project
		try {
			projDetailURL = mIssues.getString(PAGE_URL);
		} catch (JSONException e) {
			e.printStackTrace();
//			onException(e.toString());
			return;
		}
		if (projDetailURL == null)
			return;
		// get url connection
		URLConnection sifterConnection = mSifterHelper.getSifterConnection(projDetailURL);
		if (sifterConnection == null)
			return;
		// get JSON object
		JSONObject sifterJSONObject = new JSONObject();
		try {
			sifterJSONObject = mSifterHelper.getSifterJSONObject(sifterConnection);
		} catch (Exception e) {
			e.printStackTrace();
//			onException(e.toString());
			return;
		}
//		if (getSifterError(sifterJSONObject)) {
//			loginKeys();
//			return;
//		}
		Intent intent = new Intent(this, IssuesActivity.class);
		intent.putExtra(SifterReader.ISSUES, sifterJSONObject.toString());
		intent.putExtra(SifterReader.ISSUES_URL, mIssuesURL);
		startActivity(intent);
		return;
	}
	
	/** Intent for Project Details Activities. */
	private void loadIssuesPage(EditText pageNumber) {
		int pageOffset;
		try {
		pageOffset = Integer.valueOf(pageNumber.getText().toString());
		} catch (NumberFormatException e1) {
			try {
			pageNumber.setText(String.valueOf(mIssues.getInt(PAGE)));
			} catch (JSONException e2) {
				e2.printStackTrace();
				return;
			}
			e1.printStackTrace();
			return;
		}
		String projDetailURL = mIssuesURL + "?" + PER_PAGE + "=" + 25;
		projDetailURL = projDetailURL + "&" + GOTO_PAGE + "=" + pageOffset;
		// get url connection
		URLConnection sifterConnection = mSifterHelper.getSifterConnection(projDetailURL);
		if (sifterConnection == null)
			return;
		// get JSON object
		JSONObject sifterJSONObject = new JSONObject();
		try {
			sifterJSONObject = mSifterHelper.getSifterJSONObject(sifterConnection);
		} catch (Exception e) {
			e.printStackTrace();
//			onException(e.toString());
			return;
		}
//		if (getSifterError(sifterJSONObject)) {
//			loginKeys();
//			return;
//		}
		Intent intent = new Intent(this, IssuesActivity.class);
		intent.putExtra(SifterReader.ISSUES, sifterJSONObject.toString());
		intent.putExtra(SifterReader.ISSUES_URL, mIssuesURL);
		startActivity(intent);
		return;
	}
}
