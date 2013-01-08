package com.plantpurple.emojidom.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class BaseTitleFragment extends Fragment {
	
	/*
	 * Interface for event listener of fragment
	 */
	public interface OnMyFragmentEventListener {
		public void onMyEvent(int eventId, int drawableId);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	}

	abstract public String getTitle();
	abstract public void setClickable(boolean isClickable);
}
