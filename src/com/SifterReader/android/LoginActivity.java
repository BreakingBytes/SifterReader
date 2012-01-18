package com.SifterReader.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	
	// Members
	private EditText mDomainName; // sifter domain name
    private EditText mAccessKey; // sifter access key
    
    /** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		// capture our View elements
		mDomainName = (EditText) findViewById(R.id.domainEntry);
		mAccessKey = (EditText) findViewById(R.id.accessKeyEntry);
		Button saveButton = (Button) findViewById(R.id.saveKey);

		Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String domain = extras.getString(SifterReader.DOMAIN);
            String accessKey = extras.getString(SifterReader.ACCESS_KEY);

            if (domain != null) {
                mDomainName.setText(domain);
            }
            if (accessKey != null) {
            	mAccessKey.setText(accessKey);
            }
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
        	
        	// anonymous inner class
        	public void onClick(View view) {
        		Bundle bundle = new Bundle();

        		bundle.putString(SifterReader.DOMAIN, mDomainName.getText().toString());
        		bundle.putString(SifterReader.ACCESS_KEY, mAccessKey.getText().toString());
        		
        		Intent mIntent = new Intent();
        		mIntent.putExtras(bundle);
        		setResult(RESULT_OK, mIntent);
        		finish();
        	}

        });

	}
}