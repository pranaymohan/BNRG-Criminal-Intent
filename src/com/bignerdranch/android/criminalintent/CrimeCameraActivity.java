package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

public class CrimeCameraActivity extends SingleFragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Hide the window title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Hide the status bar, and other OS level stuff
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//Superclass call has to be made after because previous two calls
		//must be made before Activity.setContententView(...) is called
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected Fragment createFragment() {
		return new CrimeCameraFragment();
	}

}
