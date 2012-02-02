package com.SifterReader.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;

public class SifterHelper {
	private final String mAccessKey;

	public SifterHelper(Context context, String AccessKey) {
		mAccessKey = AccessKey;
	}
	
	public URLConnection getSifterConnection(String sifterURL) {
		URLConnection sifterConnection = null;
		try {
			// create URL object to SifterAPI
			URL sifter = new URL(sifterURL);
			// open connection to SifterAPI
			sifterConnection = sifter.openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sifterConnection;
	}
	
	public InputStream getSifterInputStream(URLConnection sifterConnection) {
		// send header requests
		sifterConnection.setRequestProperty(SifterReader.X_SIFTER_TOKEN, mAccessKey);
		sifterConnection.addRequestProperty(SifterReader.HEADER_REQUEST_ACCEPT, SifterReader.APPLICATION_JSON);

		InputStream is = null;
		try {
			is = sifterConnection.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			// also catches FileNotFoundException: invalid domain
			// IOException: invalid access key
			HttpURLConnection httpSifterConnection = (HttpURLConnection)sifterConnection;
			is = httpSifterConnection.getErrorStream();
		}
		return is;
	}

}
