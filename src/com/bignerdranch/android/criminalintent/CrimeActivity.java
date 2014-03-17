package com.bignerdranch.android.criminalintent;

import java.util.UUID;

import android.support.v4.app.Fragment;

public class CrimeActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		//Its okay that this activity references methods and tags from CrimeFragment.
		//Hosting activities can have specifics about how to host fragments,
		//but hopefully not the reverse. Fragments are encapsulated to preserve their modularity.
		UUID crimeId = (UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
		return CrimeFragment.newInstance(crimeId);
	}

}
