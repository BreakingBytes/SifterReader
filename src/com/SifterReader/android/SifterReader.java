package com.SifterReader.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SifterReader extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        URL sifter;
		try {
			// create URL object to SifterAPI
	        sifter = new URL("https://sunpower.sifterapp.com/api/projects");
			URLConnection sifterConnection = null;
			try {
				// open connection to SifterAPI
				sifterConnection = sifter.openConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 // add Access Key to header request
	        sifterConnection.setRequestProperty("X-Sifter-Token",
				"779ab91fa637368648921c811e00ba53");
			
			// add Accept: application/json to header - also not necessary
			sifterConnection.addRequestProperty("Accept","application/json");
			
			// URLconnection.connect() not necessary
			// getInputStream will connect
			
			// create buffer and open input stream
	        BufferedReader in = new BufferedReader(
	                                new InputStreamReader(
	                                sifterConnection.getInputStream()));
	        String inputLine;
	        StringBuilder myString = new StringBuilder();
	        
	        while ((inputLine = in.readLine()) != null) {
	            myString.append(inputLine); 
	            //System.out.println(inputLine);
			}
	        in.close();
	        
	        // construct json tokener from input stream or buffered reader
	        //JSONTokener x = new JSONTokener(myString.toString());
	        
	        // initialize "projects" JSONObject from string
	        JSONObject projects = new JSONObject(myString.toString());
	        
	        
	        TextView tv = new TextView(this);
	        tv.setText(projects.toString(2));
	        setContentView(tv);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}