package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CrimeFragment extends Fragment {
	
	private static final String TAG = "CrimeFragment";
	public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
	private static final String DIALOG_DATE = "date";
	private static final String DIALOG_CHOICE = "choice";
	public static final int REQUEST_DATE = 0;
	private static final int REQUEST_CHOICE = -1;
	private static final int REQUEST_PHOTO = 1;
	
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	private ImageButton mPhotoButton;
	private ImageView mPhotoView;
	
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
		setHasOptionsMenu(true);
		
		//Unpack bundle and get the crimeId
		UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
		//Set mCrime to be the specific one matching crimeId
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
	}
	
	//Increase target api from min level 8 to 11 for onCreateView
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, parent, false);
		
		//If the build version is greater than honeycomb (Api 11)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//Enable the app icon to work as an "up" button to go up
			//one hierarchy, enabling Ancestral Navigation
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}	
		
		mPhotoButton = (ImageButton)v.findViewById(R.id.crime_image_button);
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);
			}
		});
		
		//If camera is not available, disable button
		PackageManager pm = getActivity().getPackageManager();
		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
				!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			mPhotoButton.setEnabled(false);
		}
		
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
		
		//Inflate mPhotoView and load photo
		mPhotoView = (ImageView)v.findViewById(R.id.crime_image_view);
		
		return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
		
		if (resultCode != Activity.RESULT_OK) return;
		
		if (requestCode == REQUEST_DATE) {
			Date date = (Date)returnIntent.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			if (date != null) {
				mCrime.setDate(date);
				updateDate();
			}
		} else if (requestCode == REQUEST_CHOICE) {
			String choice = returnIntent.getStringExtra(DateTimeChoiceFragment.EXTRA_CHOICE);
			if (choice != null) {
				launchDatePickerDialog(choice);
			}
		} else if (requestCode == REQUEST_PHOTO) {
			String filename = returnIntent.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
			if (filename != null) {
				Photo photo = new Photo(filename);
				mCrime.setPhoto(photo);
				showPhoto();
			}
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		showPhoto();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Use a switch statement to have the options menu behave differently
		//based on which item id was received
		Log.d("menu item", item.getTitle().toString());
		switch (item.getItemId()) {
			case R.id.menu_item_delete_crime:
				//Build alert dialog and set its details
				AlertDialog.Builder aDB = new AlertDialog.Builder(getActivity());
				aDB.setTitle("Confirm Deletion")
					.setMessage("Are you sure you want to delete this?")
					.setNegativeButton("NO", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();	
						}
					})
					.setPositiveButton("YES", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							CrimeLab.get(getActivity()).deleteCrime(mCrime);
							if (NavUtils.getParentActivityName(getActivity()) != null) {
								NavUtils.navigateUpFromSameTask(getActivity());
							}
						}
					});
				//Create and show the alert dialog
				AlertDialog alert = aDB.create();
				alert.show();
				return true;
			case android.R.id.home:
				
				if (NavUtils.getParentActivityName(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void showPhoto() {
		//Reset the image view based on the photo
		Photo p = mCrime.getPhoto();
		BitmapDrawable b = null;
		if (p != null) {
			String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
			b = PictureUtils.getScaledDrawable(getActivity(), path);
		}
		mPhotoView.setImageDrawable(b);
	}

}
