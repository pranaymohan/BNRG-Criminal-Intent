package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DateTimeChoiceFragment extends DialogFragment {
	
	public static final String EXTRA_CHOICE = "com.bignerdranch.android.criminalintent.choice";
	
	private final String items[] = {"Date", "Time"};
	private String mChoice;
	
	private void sendResult(int resultCode) {
		if (getTargetFragment() == null) return;
		
		Intent i = new Intent();
		i.putExtra(EXTRA_CHOICE, mChoice);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		return new AlertDialog.Builder(getActivity())
		.setTitle(R.string.date_time_choice_title)
		.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int choice) {
				if (choice == 0) {
					mChoice = "date";
					sendResult(Activity.RESULT_OK);
				}
				if (choice == 1) {
					mChoice = "time";
					sendResult(Activity.RESULT_OK);
				}
			}
		})
		.create();
	}

}
