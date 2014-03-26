package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class CrimeListActivity extends SingleFragmentActivity 
	implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_masterdetail;
	}
	
	@Override
	protected Fragment createFragment() {
		return new CrimeListFragment();
	}

	@Override
	public void onCrimeSelected(Crime crime) {
		//If there is no detail_fragment_container, then its not a tablet style layout
		if (findViewById(R.id.detail_fragment_container) == null) {
			//Start CrimePagerActivity
			Intent i = new Intent(this, CrimePagerActivity.class);
			i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
			startActivity(i);
		} else {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			Fragment oldDetail = fm.findFragmentById(R.id.detail_fragment_container);
			Fragment newDetail = CrimeFragment.newInstance(crime.getId());
			
			if (oldDetail != null) ft.remove(oldDetail);
			ft.add(R.id.detail_fragment_container, newDetail);
			ft.commit();
		}
	}
	
	@Override
	public void onCrimeUpdated(Crime crime) {
		FragmentManager fm = getSupportFragmentManager();
		CrimeListFragment listFragment = (CrimeListFragment)fm.findFragmentById(R.id.fragment_container);
		listFragment.updateUI();
	}

}
