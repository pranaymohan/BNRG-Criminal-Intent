package com.bignerdranch.android.criminalintent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
	
	public static final String EXTRA_PHOTO_FILENAME = "com.bignerdranch.android.criminalintent.photo_filename";
	
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private ImageButton mSnapButton;
	private View mProgressBarContainer;
	
	//Define private methods for Camera callback interfaces that can then 
	//be passed to takePicture method later as necessary. They could be 
	//done as anonymous inner classes, but its quite a bit of text.
	private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		public void onShutter() {
			//Display progress bar, thereby disabling take picture button
			mProgressBarContainer.setVisibility(View.VISIBLE);
		}
	};
	
	private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			//Create filename
			String filename = UUID.randomUUID().toString()+".jpg";
			//Save the jpg to disk
			FileOutputStream outputStream = null;
			boolean success = true;
			
			try {
				outputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
				outputStream.write(data);
			} catch (Exception e) {
				Log.e(TAG, "Error writing to file "+filename, e);
				success = false;
			} finally {
				try {
					if (outputStream != null) outputStream.close();
				} catch (Exception e) {
					Log.e(TAG, "Error closing file: "+filename, e);
					success = false;
				}
			}
			
			if (success) {
				//Create a return intent and attach filename
				Intent returnIntent = new Intent();
				returnIntent.putExtra(EXTRA_PHOTO_FILENAME, filename);
				//Sets result on CrimeCameraActivity!
				getActivity().setResult(Activity.RESULT_OK, returnIntent);
			} else {
				getActivity().setResult(Activity.RESULT_CANCELED);
			}
			getActivity().finish();
		}
	};
	
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
				if (mCamera != null) {
					mCamera.takePicture(mShutterCallback, null, mPictureCallback);
				}
			}
		});
		
		mProgressBarContainer = (View)v.findViewById(R.id.crime_camera_progress_container);
		mProgressBarContainer.setVisibility(View.INVISIBLE);
		
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
			
			public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
				if (mCamera == null) return;
				
				Camera.Parameters parameters = mCamera.getParameters();
				//To set preview size
				Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
				parameters.setPreviewSize(s.width, s.height);
				//Now to set picture size
				s = getBestSupportedSize(parameters.getSupportedPictureSizes(), w, h);
				parameters.setPictureSize(s.width, s.height);
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
