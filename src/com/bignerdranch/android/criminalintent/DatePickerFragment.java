package com.bignerdranch.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, 
																  TimePickerDialog.OnTimeSetListener {
	
	public static final String EXTRA_ID = "com.bignerdranch.android.criminalintent.crime_id";
	public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
	public static final String EXTRA_CHOICE = "com.bignerdranch.android.criminalintent.choice";
	
	private Crime mCrime;
	private Date mDate;
	
	public static DatePickerFragment newInstance(UUID id, String choice) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_ID, id);
		args.putString(EXTRA_CHOICE, choice);
		
		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	private void sendResult(int resultCode) {
		if (getTargetFragment() == null) {
			return;
		}
		
		Intent i = new Intent();
		i.putExtra(EXTRA_DATE, mDate);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		String choice = getArguments().getString(EXTRA_CHOICE);
		UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
		mDate = mCrime.getDate();
		
		//Convert mDate into calendar format to show in DatePickerDialog
        Calendar c = Calendar.getInstance();
        c.setTime(mDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        
        // Create a new instance of DatePickerDialog and return it if user chose date
        if (choice == "date") {
        	DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.setTitle(R.string.date_picker_title);
            return dialog;
        } else {
        	TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        	return dialog;
        }
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
		Calendar c = Calendar.getInstance();
        c.setTime(mDate);
		c.set(year, month, day);
		mDate = c.getTime();
		sendResult(Activity.RESULT_OK);
    }

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Calendar c = Calendar.getInstance();
		c.setTime(mDate);
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		mDate = c.getTime();
		sendResult(Activity.RESULT_OK);
	}
}
