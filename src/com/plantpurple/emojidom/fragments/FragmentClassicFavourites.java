package com.plantpurple.emojidom.fragments;

import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.activities.ActivityClassic;
import com.plantpurple.emojidom.activities.ActivityCool;
import com.plantpurple.emojidom.adapters.ClassicFavouriteAdapter;
import com.plantpurple.emojidom.models.FragmentParams;
import com.plantpurple.emojidom.xml_base.XmlBase;

public class FragmentClassicFavourites extends BaseTitleFragment {
	private static final String TAG = "ActivityClassic";
	
	private static TextView mEmptyFavouriteLabel;
	private static GridView mSmileysGrid;
	private static ClassicFavouriteAdapter mAdapter;
	
	private int smileCategory;
	private String caption;
	
	private OnMyFragmentEventListener mFragmentListener;
	
	public FragmentClassicFavourites() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    
	    try {
	    	mFragmentListener = (OnMyFragmentEventListener) activity;
	    } catch (ClassCastException e) {
	        throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
	    }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "Favorites: onCreateView");
		
		View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
		
		Bundle args = getArguments();
		smileCategory = args.getInt(FragmentParams.CATEGORY);
		caption = args.getString(FragmentParams.CAPTION);
		
		mEmptyFavouriteLabel = (TextView)rootView.findViewById(R.id.tvEmptyFavourite);
		mSmileysGrid = (GridView)rootView.findViewById(R.id.gridSmiles);
		
		XmlBase xmlBase = null;
		try {
			xmlBase = new XmlBase(Environment.getExternalStorageDirectory() + getString(R.string.xml_file_directory));
		} catch (IOException e) {
			Log.e(TAG, "Error: xml file with favourites not found");
			return rootView;
		}
		
		mAdapter = new ClassicFavouriteAdapter(getActivity());
		
		// hide or show text label with empty area message
		if (xmlBase.getCoolCount() != 0) {
			mAdapter.setItems(xmlBase.getEmojiCool());
			mEmptyFavouriteLabel.setVisibility(View.GONE);
		} else {
			mEmptyFavouriteLabel.setVisibility(View.VISIBLE);
		}
		
		mSmileysGrid.setAdapter(mAdapter);
		
		mSmileysGrid.setOnItemClickListener(mGridItemClickListener);
		mSmileysGrid.setOnItemLongClickListener(mGridLongClickListener);
		
		Log.d(TAG, "Favorite: reference = " + this);
		
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// restore ui elements on favorite category
		XmlBase xmlBase = null;
		try {
			xmlBase = new XmlBase(Environment.getExternalStorageDirectory() + getString(R.string.xml_file_directory));
		} catch (IOException e) {
			Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			return;
		}
		
		updateUI(xmlBase);
		
		Log.d(TAG, "Favourite onResume");
	}
	
	public void updateUI(XmlBase xmlBase) {
		// restore ui elements on favorite category
		if (xmlBase.getClassicCount() != 0) {
			mAdapter.setItems(xmlBase.getEmojiClassic());
			mEmptyFavouriteLabel.setVisibility(View.GONE);
		} else 
			mEmptyFavouriteLabel.setVisibility(View.VISIBLE);

		mSmileysGrid.setAdapter(mAdapter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		Log.d(TAG, "Favourite onPause");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		Log.d(TAG, "Favourite onStop");
	}

	@Override
	public String getTitle() {
		return caption;
	}

	@Override
	public void setClickable(boolean isClickable) {}
	
	public void hideEmptyFavoritesLabel() {
		Log.d(TAG, "FragmentFavorites: hideText");
		
		if (mEmptyFavouriteLabel != null)
			mEmptyFavouriteLabel.setVisibility(View.GONE);
		else {
			Log.e(TAG, "Favorites TextView: == " + mEmptyFavouriteLabel);
		}
	}
	
	public void showEmptyFavoritesLabel() {
		mEmptyFavouriteLabel.setVisibility(View.VISIBLE);
	}
	
	public ClassicFavouriteAdapter getAdapter() {
		return mAdapter;
	}
	
	OnItemClickListener mGridItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			int drawableId = (Integer)mAdapter.getItem(position);
			
			if (smileCategory == XmlBase.CLASSIC_EMOJI)
				mFragmentListener.onMyEvent(ActivityClassic.CLASSIC_EVENT_INSERT_SMILE, drawableId);
			else if (smileCategory == XmlBase.COOL_EMOJI)
				mFragmentListener.onMyEvent(ActivityCool.COOL_EVENT_INSERT_SMILE, drawableId);
		}
	};
	
	OnItemLongClickListener mGridLongClickListener = new OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> adapterView, View v, int position, long id) {
			final int clickPosition = position;

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(getString(R.string.remove_from_favourites))
				   .setCancelable(false)
				   .setIcon(0)
				   .setPositiveButton(getString(R.string.request_new_dialog_ok), new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog, int id) {
						   XmlBase xmlBase = null;

						   try {
							   xmlBase = new XmlBase(Environment.getExternalStorageDirectory() + 
									   getString(R.string.xml_file_directory));
						   } catch (IOException e) {
						   }

						   if (smileCategory == XmlBase.COOL_EMOJI) {
							   try {
								   xmlBase.deleteCool(clickPosition);
							   } catch (IOException e) {
							   }
						   } else if (smileCategory == XmlBase.CLASSIC_EMOJI) {
							   try {
								   xmlBase.deleteClassic(clickPosition);
							   } catch (IOException e) {
							   }
						   }

						   mAdapter.removeItem(clickPosition);
						   mAdapter.notifyDataSetChanged();
									
						   if (mAdapter.getCount() == 0)
							   mEmptyFavouriteLabel.setVisibility(View.VISIBLE);

						   dialog.cancel();
					   }
				   })
				   .setNegativeButton(getString(R.string.request_new_dialog_cancel), new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog, int id) {
						   dialog.cancel();
					   }
				   });

			AlertDialog dialogRequestNew = builder.create();
			dialogRequestNew.show();

			return false;
		}
	};
}
