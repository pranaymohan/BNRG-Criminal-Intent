package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class CrimeFragment extends Fragment {
	
	public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
	private static final String DIALOG_DATE = "date";
	private static final String DIALOG_CHOICE = "choice";
	public static final int REQUEST_DATE = 0;
	private static final int REQUEST_CHOICE = 0;
	
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	
	//Instead of having activity use a constructor to create fragment, 
	//write a method to create a fragment AND set arguments
	//This allows us to use bundles before we ask fragment manager to add/commit it
	//Static method because we need to call it before we instantiate fragment object
	public static CrimeFragment newInstance(UUID crimeId) {
		//Make bundle
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);
		//Create fragment and set arguments
		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	//Get simple date and set it as text for date button
	public void updateDate() {
		mDateButton.setText(mCrime.getSimpleDate());
	}
	
	//Launch date picker dialog
	private void launchDatePickerDialog(String choiceId) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		
		UUID crimeId = mCrime.getId();
		DatePickerFragment dateDialog = DatePickerFragment.newInstance(crimeId, choiceId);
		dateDialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
		dateDialog.show(fm, DIALOG_DATE);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Unpack bundle and get the crimeId
		UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
		//Set mCrime to be the specific one matching crimeId
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, parent, false);
		
		mTitleField = (EditText)v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		//Set listener and use anonymous inner class to implement listener methods
		mTitleField.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				mCrime.setTitle(c.toString());
			}
			
			public void beforeTextChanged(CharSequence c, int count,int start, int after) {
				//Intentionally blank
			}

			public void afterTextChanged(Editable c) {
				//Intentionally blank
			}
			
		});
		
		//Get reference to inflated date button widget
		mDateButton = (Button)v.findViewById(R.id.crime_date);
		updateDate();
		//Opens date picker dialog
		mDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DateTimeChoiceFragment choiceDialog = new DateTimeChoiceFragment();
				choiceDialog.setTargetFragment(CrimeFragment.this, REQUEST_CHOICE);
				choiceDialog.show(fm, DIALOG_CHOICE);
			}
		});
		
		mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			// @Override not required for interfaces... learn more about this
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//Set the crime's solved property if box is checked
				mCrime.setSolved(isChecked);
			}
		});
		
		return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent i) {
		
		if (resultCode != Activity.RESULT_OK) return;
		
		if (requestCode == REQUEST_DATE) {
			Date date = (Date)i.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			if (date!=null) {
				mCrime.setDate(date);
				updateDate();
			}
		}
		
		if (requestCode == REQUEST_CHOICE) {
			String choice = i.getStringExtra(DateTimeChoiceFragment.EXTRA_CHOICE);
			if (choice != null) {
				launchDatePickerDialog(choice);
			}
		}
	}
}
