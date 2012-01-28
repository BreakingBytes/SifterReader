package com.SifterReader.android;

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

public class LoginActivity extends Activity {
	
	// Members
	private EditText mDomain; // sifter domain name
    private EditText mAccessKey; // sifter access key
    
    /** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		// capture our View elements
		mDomain = (EditText) findViewById(R.id.domainEntry);
		mAccessKey = (EditText) findViewById(R.id.accessKeyEntry);
		Button saveButton = (Button) findViewById(R.id.saveKey);

		Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String domain = extras.getString(SifterReader.DOMAIN);
            String accessKey = extras.getString(SifterReader.ACCESS_KEY);

            if (domain != null) {
                mDomain.setText(domain);
            }
            if (accessKey != null) {
            	mAccessKey.setText(accessKey);
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
        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		} catch (IOException e) {
        			e.printStackTrace();
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
}