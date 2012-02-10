package com.BreakingBytes.SifterReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;

public class SifterHelper {
	
	// Members
	private final Context mContext;
	public String mAccessKey;
	public String mDomain;
	public JSONObject mLoginError = new JSONObject();
	
	// SifterAPI headers
	public static final String X_SIFTER_TOKEN = "X-Sifter-Token";
	public static final String HEADER_REQUEST_ACCEPT = "Accept";
	public static final String APPLICATION_JSON = "application/json";
	public static final String OOPS = "oops";

	public SifterHelper(Context context, String accessKey) {
		mContext = context;
		mAccessKey = accessKey;
	}
	
	public SifterHelper(Context context) {
		mContext = context;
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
		sifterConnection.setRequestProperty(X_SIFTER_TOKEN, mAccessKey);
		sifterConnection.addRequestProperty(HEADER_REQUEST_ACCEPT, APPLICATION_JSON);

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

	public JSONObject onMissingToken() throws JSONException, NotFoundException {
		JSONObject missingToken = new JSONObject();
		missingToken.put(SifterReader.LOGIN_ERROR, // throws JSONException
				mContext.getResources().getString(R.string.token_missing)); // throws NotFoundException
		missingToken.put(SifterReader.LOGIN_DETAIL,
				mContext.getResources().getString(R.string.token_missing_msg));
		return missingToken;
	}
	
	public boolean getKey() throws JSONException, NotFoundException, FileNotFoundException, IOException {
		File keyFile = mContext.getFileStreamPath(SifterReader.KEY_FILE);
		if (!keyFile.exists()) {
			mLoginError = onMissingToken(); // throws JSONException, NotFoundException
			return false;
		}
		BufferedReader in = new BufferedReader(new FileReader(keyFile)); // throws FileNotFoundException
		String inputLine;
		StringBuilder x = new StringBuilder();
		try {
			while ((inputLine = in.readLine()) != null)
				x.append(inputLine);
		} catch (IOException e) {
			in.close();
			throw e;
		} // catch error and close buffered reader
		in.close();
		JSONObject loginKeys = new JSONObject(x.toString()); // throws JSONException
		mDomain = loginKeys.getString(SifterReader.DOMAIN); // throws JSONException
		mAccessKey = loginKeys.getString(SifterReader.ACCESS_KEY); // throws JSONException
		if (mDomain.isEmpty() || mAccessKey.isEmpty()) {
			mLoginError = onMissingToken();
			return false;
		}
		return true;
	}
	
	public JSONObject getSifterJSONObject(URLConnection sifterConnection)
			throws JSONException, NotFoundException, IOException {
		JSONObject sifterJSONObject = new JSONObject();
		
		InputStream sifterInputStream = getSifterInputStream(sifterConnection);
		if (sifterInputStream == null) { // null means MalformedURLException or IOException
			return onConnectionError(); // throws JSONException, NotFoundException
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(sifterInputStream));
		String inputLine;
		StringBuilder x = new StringBuilder();
		try {
			while ((inputLine = in.readLine()) != null)
				x.append(inputLine);
		} catch (IOException e) {
			in.close();sifterInputStream.close();
			throw e;
		} // catch error and close buffered reader
		in.close();sifterInputStream.close();
		// sifterInputStream must stay open for buffered reader 
		sifterJSONObject = new JSONObject(x.toString()); // throws JSONException
		return sifterJSONObject;
	}


	/** Intent for OopsActivity to display exceptions. */
	public void onException(String eString) {
		Intent intent = new Intent(mContext, OopsActivity.class);
		intent.putExtra(OOPS, eString);
		mContext.startActivity(intent);
	}
	
	public void saveFilters(boolean[] filterStatus, boolean[] filterPriority) {
		try {JSONObject filters = new JSONObject();
			filters.put(IssuesActivity.STATUS, new JSONArray(Arrays.asList(filterStatus)));
			filters.put(IssuesActivity.PRIORITY, new JSONArray(Arrays.asList(filterPriority)));
			FileOutputStream fos = mContext.openFileOutput(SifterReader.KEY_FILE, Context.MODE_APPEND);
			fos.write(filters.toString().getBytes());
			fos.close();
		} catch (JSONException e) {
			e.printStackTrace();
			onException(e.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			onException(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			onException(e.toString());
		}
	}
	
	public JSONObject getFilters() throws JSONException, FileNotFoundException, IOException {
		File keyFile = mContext.getFileStreamPath(SifterReader.KEY_FILE);
		if (!keyFile.exists())
			return new JSONObject();
		BufferedReader in = new BufferedReader(new FileReader(keyFile)); // throws FileNotFoundException
		String inputLine;
		StringBuilder x = new StringBuilder();
		try {
			while ((inputLine = in.readLine()) != null)
				x.append(inputLine);
		} catch (IOException e) {
			in.close();
			throw e;
		} // catch error and close buffered reader
		in.close();
		JSONObject filters = new JSONObject(x.toString()); // throws JSONException
		return filters;
	}
}
