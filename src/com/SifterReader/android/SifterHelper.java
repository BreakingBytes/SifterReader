package com.SifterReader.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources.NotFoundException;

public class SifterHelper {
	private final Context mContext;
	private final String mAccessKey;

	public SifterHelper(Context context, String accessKey) {
		mContext = context;
		mAccessKey = accessKey;
	}
	
	public URLConnection getSifterConnection(String sifterURL) {
		URLConnection sifterConnection = null;
		try {
			// create URL object to SifterAPI
			URL sifter = new URL(sifterURL); // throws MalformedURLException
			// open connection to SifterAPI
			sifterConnection = sifter.openConnection(); // throws IOException
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} // catch errors, do nothing, return null, use onConnectionError
		return sifterConnection;
	}
	
	public InputStream getSifterInputStream(URLConnection sifterConnection) {
		// send header requests
		sifterConnection.setRequestProperty(SifterReader.X_SIFTER_TOKEN, mAccessKey);
		sifterConnection.addRequestProperty(SifterReader.HEADER_REQUEST_ACCEPT, SifterReader.APPLICATION_JSON);

		InputStream is = null;
		try {
			is = sifterConnection.getInputStream(); // throws FileNotFound, IOException
		} catch (IOException e) {
			e.printStackTrace();
			// also catches FileNotFoundException: invalid domain
			// IOException: invalid access key
			HttpURLConnection httpSifterConnection = (HttpURLConnection)sifterConnection;
			is = httpSifterConnection.getErrorStream();
		} // catch errors, return error stream, could be null if none found
		return is;
	}
	
	public JSONObject onConnectionError() throws JSONException, NotFoundException {
		JSONObject connectionError = new JSONObject();
			connectionError.put(SifterReader.LOGIN_ERROR, // throws JSONException
					mContext.getResources().getString(R.string.connection_error)); // throws NotFoundException
			connectionError.put(SifterReader.LOGIN_DETAIL,
					mContext.getResources().getString(R.string.connection_error_msg));
		return connectionError;
	}

}
