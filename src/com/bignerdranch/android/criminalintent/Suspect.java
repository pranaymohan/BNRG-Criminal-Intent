package com.bignerdranch.android.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

public class Suspect {
	private static final String JSON_NAME = "name";
	private static final String JSON_PHONE = "phone";
	
	private String mName;
	private String mPhone;
	
	public Suspect(String name, String phone) {
		mName = name;
		mPhone = phone;
	}
	
	public Suspect(JSONObject json) throws JSONException {;
		mName = json.getString(JSON_NAME);
		mPhone = json.getString(JSON_PHONE);
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_NAME, mName);
		json.put(JSON_PHONE, mPhone);
		return json;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getPhone() {
		return mPhone;
	}
}
