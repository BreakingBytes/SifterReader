package com.SifterReader.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	
	private EditText mDomainName;
    private EditText mAccessKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		// capture our View elements
		mDomainName = (EditText) findViewById(R.id.domainEntry);
		mAccessKey = (EditText) findViewById(R.id.accessKeyEntry);
		Button saveButton = (Button) findViewById(R.id.saveKey);
		Button cancelButton = (Button) findViewById(R.id.cancel);

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
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
        	
        	// anonymous inner class
        	public void onClick(View view) {
        		Intent mIntent = new Intent();
        		setResult(RESULT_CANCELED, mIntent);
        		finish();
        	}

        });

	}
}