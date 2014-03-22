package com.bignerdranch.android.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment {
	public static final String EXTRA_IMAGE_PATH = "com.bignerdranch.android.criminalintent.image_path";
	
	public static ImageFragment newInstance(String path) {
		ImageFragment imageDialog = new ImageFragment();
		Bundle args = new Bundle();
		args.putString(EXTRA_IMAGE_PATH, path);
		imageDialog.setArguments(args);
		imageDialog.setStyle(STYLE_NO_TITLE, 0);
		return imageDialog;
	}
	
	private ImageView mImageView;
	
	@Override
	public View onCreateView(LayoutInflater inflate, ViewGroup parent, Bundle savedInstanceState) {
		mImageView = new ImageView(getActivity());
		String path = getArguments().getString(EXTRA_IMAGE_PATH);
		BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path);
		mImageView.setImageDrawable(image);
		return mImageView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		PictureUtils.cleanImageView(mImageView);
	}

}
