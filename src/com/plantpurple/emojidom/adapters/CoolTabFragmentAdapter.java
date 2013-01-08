package com.plantpurple.emojidom.adapters;

import java.util.ArrayList;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.fragments.FragmentCoolFavourites;
import com.plantpurple.emojidom.fragments.FragmentCoolSmileCategory2;
import com.plantpurple.emojidom.models.FragmentParams;
import com.plantpurple.emojidom.xml_base.XmlBase;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class CoolTabFragmentAdapter extends FragmentStatePagerAdapter {
	Context context;
	
	ArrayList<FragmentParams> mFragmentParams;

	public CoolTabFragmentAdapter(Context c, FragmentManager fm, ArrayList<FragmentParams> params) {
		super(fm);
		
		context = c;
		
		mFragmentParams = params;
	}
	
	@Override
	public Fragment getItem(int position) {
		//BaseTitleFragment fragment;
		
		if (position == 0) {
			FragmentCoolFavourites fragment = new FragmentCoolFavourites();
			
			Bundle args = new Bundle();
			args.putInt(FragmentParams.CATEGORY, XmlBase.COOL_EMOJI);
			args.putString(FragmentParams.CAPTION, context.getString(R.string.cool_tab_fragment_favourites));
			fragment.setArguments(args);
			
			return fragment;
		} else {
			FragmentCoolSmileCategory2 fragment = new FragmentCoolSmileCategory2();
			
	        Bundle args = new Bundle();
	        args.putString(FragmentParams.CAPTION, mFragmentParams.get(position).getCaption());
	        args.putInt(FragmentParams.FIRST_ID, mFragmentParams.get(position).getFirstId());
	        args.putInt(FragmentParams.LAST_ID, mFragmentParams.get(position).getLastId());
	        fragment.setArguments(args);
	        
	        return fragment;
		}
        
        //return fragment;
	}

	@Override
	public int getCount() {
		return mFragmentParams.size();
	}

	@Override
    public CharSequence getPageTitle(int position) {
        return mFragmentParams.get(position).getCaption();
    }
	
	public void addFragment(FragmentParams params) {
		mFragmentParams.add(params);
	}
	
	public int getItemIndexByTitle(String title) {
		int index = -1;
		for (int i = 0; i != mFragmentParams.size(); ++i) {
			if (mFragmentParams.get(i).getCaption().equals(title))
				index = i;
		}
		
		return index;
	}
}

