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
import android.view.Menu;
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
	public static final int SETTINGS_ID = Menu.FIRST;
	public static final int EXIT_ID = Menu.FIRST + 1;
	
	// Members
	private SifterHelper mSifterHelper;
	private String mIssuesURL;
	private JSONObject mIssues = new JSONObject();
	private JSONObject[] mAllIssues;
	private int mTotalPages;
	private int mPage;

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
					mPage = mIssues.getInt(PAGE);
					mTotalPages = mIssues.getInt(TOTAL_PAGES);
					pageNumber.setText(String.valueOf(mPage));
					pageTotal.setText(" / " + String.valueOf(mTotalPages));

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

	/** Menu button options. */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, SETTINGS_ID, 0, R.string.issues_settings);
		menu.add(0, EXIT_ID, 0, R.string.issues_exit);
		return result;
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
		String pageURL = null;
		// get project detail url from project
		try {
			pageURL = mIssues.getString(PAGE_URL);
		} catch (JSONException e) {
			e.printStackTrace();
			mSifterHelper.onException(e.toString());
			return;
		}
		if (pageURL == null)
			return;
		changePage(pageURL);
	}
	
	/** Intent for Project Details Activities. */
	private void loadIssuesPage(EditText pageNumber) {
		int newPage;
		try {
		newPage = Integer.valueOf(pageNumber.getText().toString());
		} catch (NumberFormatException e) {
			pageNumber.setText(String.valueOf(mPage));
			e.printStackTrace();
			return;
		}
		mPage = (newPage < mTotalPages) ? mPage : mTotalPages;
		mPage = (newPage > 1) ? mPage : 1;
		String pageURL = mIssuesURL + "?" + PER_PAGE + "=" + 25;
		pageURL += "&" + GOTO_PAGE + "=" + mPage;
		changePage(pageURL);
	}
	
	private void changePage(String pageURL) {
		// get url connection
		URLConnection sifterConnection = mSifterHelper.getSifterConnection(pageURL);
		if (sifterConnection == null)
			return;
		// get JSON object
		JSONObject sifterJSONObject = new JSONObject();
		try {
			sifterJSONObject = mSifterHelper.getSifterJSONObject(sifterConnection);
		} catch (Exception e) {
			e.printStackTrace();
			mSifterHelper.onException(e.toString());
			return;
		}
//				if (getSifterError(sifterJSONObject)) {
//					loginKeys();
//					return;
//				}
		Intent intent = new Intent(this, IssuesActivity.class);
		intent.putExtra(SifterReader.ISSUES, sifterJSONObject.toString());
		intent.putExtra(SifterReader.ISSUES_URL, mIssuesURL);
		startActivity(intent);
		return;
	}
}
