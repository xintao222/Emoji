package com.plantpurple.emojidom.fragments;

import java.io.IOException;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.activities.ActivityCool;
import com.plantpurple.emojidom.adapters.CoolAdapter;
import com.plantpurple.emojidom.models.FragmentParams;
import com.plantpurple.emojidom.xml_base.XmlBase;

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
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class FragmentCoolSmileCategory2 extends BaseTitleFragment {
	GridView mHappyGrid;
	CoolAdapter mHappyAdapter;
	
	OnMyFragmentEventListener mFragmentListener;
	
	private int firstId; 
	private int lastId;
	
	private String caption;
	
	public FragmentCoolSmileCategory2() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i("My", "Cool: onCreate");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Log.i("My", "Cool: onActivityCreated");
	}

	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    
	    try {
	    	mFragmentListener = (OnMyFragmentEventListener)activity;
	    } catch (ClassCastException e) {
	        throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
	    }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_smiles, container, false);
		
		// get arguments from adapter
		Bundle args = getArguments();
		caption = args.getString(FragmentParams.CAPTION);
		firstId = args.getInt(FragmentParams.FIRST_ID);
		lastId = args.getInt(FragmentParams.LAST_ID);
		
		// get ref to Grid and customize adapter for this
		mHappyGrid = (GridView)rootView.findViewById(R.id.gridSmiles);
		mHappyAdapter = new CoolAdapter(getActivity(), firstId, lastId);
		mHappyGrid.setAdapter(mHappyAdapter);
		
		mHappyGrid.setOnItemClickListener(mHappyGridItemClickListener);
		mHappyGrid.setOnItemLongClickListener(mHappyGridLongClickListener);
		
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		Log.d("My", "FragmentCoolSmileCategory onResume");
	}

	@Override
	public String getTitle() {
		return caption;
	}

	@Override
	public void setClickable(boolean isClickable) {}
	
	OnItemClickListener mHappyGridItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			//int drawableId = (Integer)mHappyAdapter.getItem(position);
			int drawableId = (Integer)adapterView.getItemAtPosition(position);
			
			mFragmentListener.onMyEvent(ActivityCool.COOL_EVENT_INSERT_SMILE, drawableId);
		}
	};
	
	OnItemLongClickListener mHappyGridLongClickListener = new OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> adapterView, View v, int position, long id) {
			
			final int clickPosition = position;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(getString(R.string.add_to_favourites))
			       .setCancelable(false)
			       .setIcon(0)
			       .setPositiveButton(getString(R.string.request_new_dialog_ok), new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   XmlBase xmlBase = null;
			        	   
			        	   try {
			        		   xmlBase = new XmlBase(Environment.getExternalStorageDirectory() + 
			        				   getString(R.string.xml_file_directory));
			        	   } catch (IOException e) {}
			        	   
			        	   try {
			        		   int emojiId = (Integer)mHappyAdapter.getItem(clickPosition);
			        		   if (!xmlBase.isEmojiInCool(emojiId))
			        			   xmlBase.insertCool(emojiId);
			        		   else
			        			   Toast.makeText(getActivity(), getString(R.string.already_in_favourites), Toast.LENGTH_SHORT).show();
			        	   } catch (IOException e) {}
			        	   
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
