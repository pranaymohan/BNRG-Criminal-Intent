package com.bignerdranch.android.criminalintent;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class CrimeCameraFragment extends Fragment {
	private static final String TAG = "CrimeCameraFragment";
	
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime_camera, parent);
		ImageButton snapButton = (ImageButton)v.findViewById(R.id.crime_camera_snap_button);
		snapButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surface_view);
		return v;
	}
	
	@TargetApi(9)
	@Override
	public void onResume() {
		super.onResume();
		//On resume is always called when interacting with this fragment, even during startup
		//That is why we will use this as the threshold for opening camera
		//If we opened it onCreate, then we might waste resources.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			//NOTE: Bear with me, opening on main thread for now...
			mCamera = Camera.open(0);
		} else {
			//If device is Froyo or below
			mCamera = Camera.open();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//Being a good citizen and releasing the camera whenever the fragment is paused
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

}
