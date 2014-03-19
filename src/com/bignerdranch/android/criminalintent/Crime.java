package com.bignerdranch.android.criminalintent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {
	
	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_SOLVED = "solved";
	private static final String JSON_DATE = "date";
	
	private UUID mId;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;
	
	public Crime() {
		//Generate unique identifier
		mId = UUID.randomUUID();
		mDate = new Date();
	}
	
	//Another constructor that allows you to create a crime from a JSON object
	public Crime(JSONObject json) throws JSONException {
		mId = UUID.fromString(json.get(JSON_ID).toString());
		mTitle = json.getString(JSON_TITLE);
		mSolved = json.getBoolean(JSON_SOLVED);
		mDate = new Date(json.getLong(JSON_DATE));
	}
	
	//Override toString() method to generate more useful data 
	//instead of just class name and memory address of object
	@Override
	public String toString() {
		return getTitle();
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public UUID getId() {
		return mId;
	}

	public Date getDate() {
		return mDate;
	}
	
	public String getSimpleDate() {
		//SimpleDateFormat sdFormat = new SimpleDateFormat("EEEE, MMM d, y", Locale.US);
		SimpleDateFormat sdFormat = new SimpleDateFormat("E, M/d/y 'at' hh:mm a", Locale.US);
		String simpleDate = sdFormat.format(mDate);
		return simpleDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId);
		json.put(JSON_TITLE, mTitle);
		json.put(JSON_SOLVED, mSolved);
		json.put(JSON_DATE, mDate.getTime());
		return json;
	}

}
