package com.SifterReader.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}

	private String[] getKeys() {

		String[] keys = new String[2];

		// capture our View elements
		EditText domainEntry = (EditText) findViewById(R.id.domainEntry);
		EditText accessKeyEntry = (EditText) findViewById(R.id.accessKeyEntry);

		keys[0] = domainEntry.getText().toString();
		keys[1] = accessKeyEntry.getText().toString();
		return keys;
	}

}
