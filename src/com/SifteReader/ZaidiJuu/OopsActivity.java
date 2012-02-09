package com.SifteReader.ZaidiJuu;

import com.SifterReader.ZaidiJuu.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class OopsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oops);

		TextView oopsMsg = (TextView) findViewById(R.id.stack_trace);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String e = extras.getString(SifterHelper.OOPS);
			if (e != null) {
				oopsMsg.setText(e);
			}
		}
	}
}
