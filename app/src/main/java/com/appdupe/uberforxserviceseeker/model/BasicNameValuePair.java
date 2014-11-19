package com.appdupe.uberforxserviceseeker.model;

import org.apache.http.NameValuePair;

public class BasicNameValuePair implements NameValuePair {

	private String name, value;

	public BasicNameValuePair(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}
}
