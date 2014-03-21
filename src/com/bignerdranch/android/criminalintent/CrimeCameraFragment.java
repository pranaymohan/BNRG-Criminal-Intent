package com.bignerdranch.android.criminalintent;

import java.io.IOException;
import java.util.List;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class CrimeCameraFragment extends Fragment {
	private static final String TAG = "CrimeCameraFragment";
	
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private ImageButton mSnapButton;
	
	
	// A simple iterative algorithm to get the largest size available
	private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for (Size s : sizes) {
			int area = s.width * s.height;
			if (area > largestArea) {
				bestSize = s;
				largestArea = area;
			}
		}
		return bestSize;
	}
	
	@Override
	//Unfortunately, have to do this for compatibility issues
	@SuppressWarnings("deprecation")
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);
		mSnapButton = (ImageButton)v.findViewById(R.id.crime_camera_snap_button);
		mSnapButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surface_view);
		SurfaceHolder holder = mSurfaceView.getHolder();
		//setType() and SURFACE_TYPE_PUSH_BUFFERS are deprecated,
		//but required for camera preview to work on pre-Honeycomb devices
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		holder.addCallback(new SurfaceHolder.Callback() {

			public void surfaceCreated(SurfaceHolder holder) {
				try {
					if (mCamera != null) {
						mCamera.setPreviewDisplay(holder);
					}
				} catch (IOException e) {
					Log.e(TAG, "Error setting up preview display", e);
				}
			}
			
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (mCamera != null) {
					mCamera.stopPreview();
				}
			}
			
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				if (mCamera == null) return;
				
				Camera.Parameters parameters = mCamera.getParameters();
				Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
				parameters.setPreviewSize(s.width, s.height);
				mCamera.setParameters(parameters);
				try {
					mCamera.startPreview();
				} catch (Exception e) {
					Log.e(TAG, "Camera could not start preview", e);
					mCamera.release();
					mCamera = null;
				}
			}
		});
		
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
