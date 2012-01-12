package com.SifterReader.android;

/*
 *      SifterReader.java
 *      
 *      Copyright 2012 Mark Mikofski <bwanamarko@yahoo.com>
 *      
 *      This program is free software; you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation; either version 2 of the License, or
 *      (at your option) any later version.
 *      
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *      
 *      You should have received a copy of the GNU General Public License
 *      along with this program; if not, write to the Free Software
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *      MA 02110-1301, USA.
 *      
 *      
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SifterReader extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // capture our View elements
        final EditText domain = (EditText) findViewById(R.id.domainEntry);
        final EditText accessKey = (EditText) findViewById(R.id.accessKeyEntry);
        final TextView issues = (TextView) findViewById(R.id.issues);
        
        final Button projectsButton = (Button) findViewById(R.id.projectsButton);
        projectsButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	// Perform action on clicks
            	URL sifter;
        		try {
        			// create URL object to SifterAPI
        	        sifter = new URL("https://"+domain.getText().toString()+".sifterapp.com/api/projects");
        			URLConnection sifterConnection = null;
        			try {
        				// open connection to SifterAPI
        				sifterConnection = sifter.openConnection();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        			// add Access Key to header request
        	        sifterConnection.setRequestProperty("X-Sifter-Token",accessKey.getText().toString());
        			
        			// add Accept: application/json to header - also not necessary
        			sifterConnection.addRequestProperty("Accept","application/json");
        			
        			// create buffer and open input stream
        	        BufferedReader in = new BufferedReader(
        	                                new InputStreamReader(
        	                                sifterConnection.getInputStream()));
        	        String inputLine;
        	        StringBuilder x = new StringBuilder();
        	        
        	        while ((inputLine = in.readLine()) != null) {
        	            x.append(inputLine); 
        	        }
        	        in.close();
        	        
        	        // initialize "projects" JSONObject from string
        	        JSONObject projects = new JSONObject(x.toString());
        	        
        	        // array of projects
        	        JSONArray projectArray = projects.getJSONArray("projects");
        	        int numberProjects = projectArray.length();
        	        JSONObject[] p = new JSONObject[numberProjects];
        	        
        	        // projects
        	        for (int i=0;i<numberProjects;i++) {
        				p[i] = projectArray.getJSONObject(i);
        	        }
        	        
        	        // project field names
        	        JSONArray fieldNames = p[0].names();
        			int numKeys = p[0].length();
        			SifterProj[] proj = new SifterProj[numberProjects];
        			StringBuilder y = new StringBuilder();
        			for (int j=0;j<numberProjects;j++) {
        				proj[j] = new SifterProj(p[j].getString("api_url"),
        							p[j].getString("archived"),
        							p[j].getString("api_issues_url"),
        							p[j].getString("milestones_url"),
        							p[j].getString("api_milestones_url"),
        							p[j].getString("api_categories_url"),
        							p[j].getString("issues_url"),
        							p[j].getString("name"),
        							p[j].getString("url"),
        							p[j].getString("api_people_url"),
        							p[j].getString("primary_company_name"));
        				y.append("************ project: " + (j+1) + " ************\n");
        				for (int i=0;i<numKeys;i++) {
        					y.append(fieldNames.getString(i)+" : "+p[j].getString(fieldNames.getString(i))+"\n");
        				}
        			}
        			
					issues.append(y);
					
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
        });
        
//        URL sifter;
//		try {
//			// create URL object to SifterAPI
//	        sifter = new URL("https://" + domain.getText().toString() + "/api/projects");
//			URLConnection sifterConnection = null;
//			try {
//				// open connection to SifterAPI
//				sifterConnection = sifter.openConnection();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			 // add Access Key to header request
//	        sifterConnection.setRequestProperty("X-Sifter-Token",accessKey.getText().toString());
//			
//			// add Accept: application/json to header - also not necessary
//			sifterConnection.addRequestProperty("Accept","application/json");
//			
//			// URLconnection.connect() not necessary
//			// getInputStream will connect
//			
//			// create buffer and open input stream
//	        BufferedReader in = new BufferedReader(
//	                                new InputStreamReader(
//	                                sifterConnection.getInputStream()));
//	        String inputLine;
//	        StringBuilder myString = new StringBuilder();
//	        
//	        while ((inputLine = in.readLine()) != null) {
//	            myString.append(inputLine); 
//	        }
//	        in.close();
//	        
//	        // initialize "projects" JSONObject from string
//	        JSONObject projects = new JSONObject(myString.toString());
//	        
//	        setContentView(R.layout.issues);
//	        
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
}