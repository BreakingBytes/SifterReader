package com.SifterReader.android;

public class SifterProj {
	String api_url;
	String archived;
	String api_issues_url;
	String milestones_url;
	String api_milestones_url;
	String api_categories_url;
	String issues_url;
	String name;
	String url;
	String api_people_url;
	String primary_company_name;
	
	public SifterProj(String api_url, String archived, String api_issues_url, String milestones_url, 
			String api_milestones_url, String api_categories_url, String issues_url, String name, 
			String url, String api_people_url, String primary_company_name) {
		this.api_url = api_url;
		this.archived = archived;
		this.api_issues_url = api_issues_url;
		this.milestones_url = milestones_url;
		this.api_milestones_url = api_milestones_url;
		this.api_categories_url = api_categories_url;
		this.issues_url = issues_url;
		this.name = name;
		this.url = url;
		this.api_people_url = api_people_url;
		this.primary_company_name = primary_company_name;
	}
}
