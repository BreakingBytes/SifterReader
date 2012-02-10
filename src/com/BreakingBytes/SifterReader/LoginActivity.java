package com.BreakingBytes.SifterReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	// Members
	private EditText mDomain; // sifter domain name
	private EditText mAccessKey; // sifter access key
	private TextView mLoginError;
	private TextView mLoginErrorMsg;
	private SifterHelper mSifterHelper;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		mSifterHelper = new SifterHelper(this);
		
		// capture our View elements
		mDomain = (EditText) findViewById(R.id.domain_entry);
		mAccessKey = (EditText) findViewById(R.id.access_key_entry);
		mLoginError = (TextView) findViewById(R.id.login_error);
		mLoginErrorMsg = (TextView) findViewById(R.id.login_error_msg);
		Button saveButton = (Button) findViewById(R.id.save_key);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String domain = extras.getString(SifterReader.DOMAIN);
			String accessKey = extras.getString(SifterReader.ACCESS_KEY);
			String loginError = extras.getString(SifterReader.LOGIN_ERROR);

			if (domain != null) {
				mDomain.setText(domain);
			}
			if (accessKey != null) {
				mAccessKey.setText(accessKey);
			}
			if (loginError != null) {
				try {
					JSONObject loginStatus = new JSONObject(loginError);
					mLoginError.setText(loginStatus.getString(SifterReader.LOGIN_ERROR));
					mLoginErrorMsg.setText(loginStatus.getString(SifterReader.LOGIN_DETAIL));
				} catch (JSONException e) {
					e.printStackTrace();
					mSifterHelper.onException(e.toString());
				}
			}
		}

		saveButton.setOnClickListener(new View.OnClickListener() {

			// anonymous inner class
			public void onClick(View view) {
				try {
					JSONObject loginKeys = new JSONObject();
					loginKeys.put(SifterReader.DOMAIN, mDomain.getText().toString());
					loginKeys.put(SifterReader.ACCESS_KEY, mAccessKey.getText().toString());
					FileOutputStream fos = openFileOutput(SifterReader.KEY_FILE, Context.MODE_PRIVATE);
					fos.write(loginKeys.toString().getBytes());
					fos.close();
				} catch (JSONException e) {
					e.printStackTrace();
					mSifterHelper.onException(e.toString());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					mSifterHelper.onException(e.toString());
				} catch (IOException e) {
					e.printStackTrace();
					mSifterHelper.onException(e.toString());
				}

				Bundle bundle = new Bundle();
				bundle.putString(SifterReader.DOMAIN, mDomain.getText().toString());
				bundle.putString(SifterReader.ACCESS_KEY, mAccessKey.getText().toString());

				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();
			}
		});
	}
	// TODO remove bundle and just read/write key file
	
	/** called by Android if the Activity is being stopped
	 *  and may be killed before it is resumed! */
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        saveState();
//        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }
	
	@Override
    protected void onPause() {
        super.onPause();
//        saveState();
    }
	
	@Override
    protected void onResume() {
        super.onResume();
//        populateFields();
    }
	
//	private void saveState() {
//	}
}