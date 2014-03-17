package com.bignerdranch.android.criminalintent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Crime {
	
	private UUID mId;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;
	
	public Crime() {
		//Generate unique identifier
		mId = UUID.randomUUID();
		mDate = new Date();
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

}
