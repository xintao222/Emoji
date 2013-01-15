package com.plantpurple.emojidom.activities;

import java.io.IOException;
import java.util.ArrayList;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.adapters.CoolTabFragmentAdapter;
import com.plantpurple.emojidom.consts.CoolCategory;
import com.plantpurple.emojidom.consts.Purchases;
import com.plantpurple.emojidom.fragments.FragmentCoolFavourites;
import com.plantpurple.emojidom.fragments.BaseTitleFragment.OnMyFragmentEventListener;
import com.plantpurple.emojidom.models.FragmentParams;
import com.plantpurple.emojidom.preferences.Preference;
import com.plantpurple.emojidom.xml_base.XmlBase;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

public class ActivityCool extends FragmentActivity implements OnMyFragmentEventListener { 
	private static final String TAG = "ActivityCool";
	
	private static ViewPager mViewPager;
	private PagerTabStrip mPagerTabStrip;
	private static CoolTabFragmentAdapter mTabFragmentAdapter;
	
	// Event ids
	public static final int COOL_EVENT_INSERT_SMILE = 1;
	
	private static boolean isFirstExecution = true;
	private static int currentFragmentPosition;
	
	ArrayList<FragmentParams> mFragmentParams;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_cool_tab);
		
		Log.d(TAG, "ActivityCool onCreate");
		
		currentFragmentPosition = 1;
		
		initUI();
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				currentFragmentPosition = position;
				
				// if we now in Favorites then get references to Text and Grid
				if (position == 0) {
					FragmentCoolFavourites f = (FragmentCoolFavourites)mTabFragmentAdapter.getItem(position);
					Log.d(TAG, "Favorite: ref from Activity = " + f);
					Log.d(TAG, "Favorite caption = " + f.getTitle());
						
					// get ref on xml database..
					XmlBase xml = null;
					try {
						xml = new XmlBase(Environment.getExternalStorageDirectory() + getString(R.string.xml_file_directory));
					} catch (IOException e) {
						return;
					}
					
					f.updateUI(xml);
					
					if (f.getAdapter() == null)
						Log.e(TAG, "FavoriteFragment from Activity: adapter is NULL");
					else {
						Log.d(TAG, "FavoriteFragment from Activity: adapter count = " + f.getAdapter().getCount());
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	private void initUI() {
		mViewPager = (ViewPager)findViewById(R.id.ViewPager);
		mPagerTabStrip = (PagerTabStrip)findViewById(R.id.PagerTabStrip);
		
		initFragmentParams();
		mTabFragmentAdapter = new CoolTabFragmentAdapter(this, getSupportFragmentManager(), mFragmentParams);
		mViewPager.setAdapter(mTabFragmentAdapter);
		
		mPagerTabStrip.setTabIndicatorColor(Color.parseColor("#00CCFF"));
		
		if (isFirstExecution) {
			mViewPager.setCurrentItem(1);
			isFirstExecution = false;
		} else
			mViewPager.setCurrentItem(currentFragmentPosition);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		Log.d(TAG, "Activity Cool resume");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		
		Log.d(TAG, "Activity Cool restart");
	}
	
	public static void setCurrentFragment(String fragmentName) {
		int index = mTabFragmentAdapter.getItemIndexByTitle(fragmentName);
		
		if (index != -1)
			mViewPager.setCurrentItem(index);
		else
			mViewPager.setCurrentItem(1);
	}
	
	public static void addFragment(FragmentParams params) {
		mTabFragmentAdapter.addFragment(params);
		mTabFragmentAdapter.notifyDataSetChanged();
	}

	public void onMyEvent(int eventId, int drawableId) {
		switch (eventId) {
		case COOL_EVENT_INSERT_SMILE:
			Drawable d = getResources().getDrawable( drawableId );
			MainActivity.appendSmileToEdit(d);

			break;

		default:
			break;
		}
	}
	
	private void initFragmentParams() {
		mFragmentParams = new ArrayList<FragmentParams>();
		mFragmentParams.add(new FragmentParams(XmlBase.COOL_EMOJI, getString(R.string.cool_tab_fragment_favourites)));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_happy), 
				CoolCategory.HAPPY_FIRST_ID, 
				CoolCategory.HAPPY_LAST_ID)); 
		
		mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_romantic), 
				CoolCategory.ROMANTIC_FIRST_ID, 
				CoolCategory.ROMANTIC_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_angry), 
				CoolCategory.ANGRY_FIRST_ID, 
				CoolCategory.ANGRY_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_expressions),
				CoolCategory.EXPRESSIONS_FIRST_ID, 
				CoolCategory.EXPRESSIONS_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_descriptive),
				CoolCategory.DESCRIPTIVE_FIRST_ID, 
				CoolCategory.DESCRIPTIVE_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_clothes),
				CoolCategory.CLOTHES_FIRST_ID, 
				CoolCategory.CLOTHES_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_looks),
				CoolCategory.LOOKS_FIRST_ID, 
				CoolCategory.LOOKS_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_busy),
				CoolCategory.BUSY_FIRST_ID, 
				CoolCategory.BUSY_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_leisure),
				CoolCategory.LEISURE_FIRST_ID, 
				CoolCategory.LEISURE_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_food),
				CoolCategory.FOOD_FIRST_ID, 
				CoolCategory.FOOD_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_sport),
				CoolCategory.SPORT_FIRST_ID, 
				CoolCategory.SPORT_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_places),
				CoolCategory.PLACES_FIRST_ID, 
				CoolCategory.PLACES_LAST_ID));
		
		// Add fragments with in-app content
		// Holidays
		if (Preference.isPurchased(Purchases.HOLIDAYS)) {
			mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_holidays),
					CoolCategory.HOLIDAYS_FIRST_ID, 
					CoolCategory.HOLIDAYS_LAST_ID));
		
			//Toast.makeText(this, "Holidays add in adapter", Toast.LENGTH_LONG).show();
		}
						
		// Naughty
		if (Preference.isPurchased(Purchases.NAUGHTY_WHITE)) {
			mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_naughty),
					CoolCategory.NAUGHTY_FIRST_ID, 
					CoolCategory.NAUGHTY_LAST_ID));
			
			//Toast.makeText(this, "Naughty_White add in adapter", Toast.LENGTH_LONG).show();
		}
				
		// Naughty Black
		if (Preference.isPurchased(Purchases.NAUGHTY_BLACK)) {
			mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_naughty_black),
					CoolCategory.NAUGHTY_BLACK_FIRST_ID, 
					CoolCategory.NAUGHTY_BLACK_LAST_ID));
			
			Log.d(TAG, "NAUGHTY_BLACK was added to cool category");
			//Toast.makeText(this, "Naughty_Black add in adapter", Toast.LENGTH_LONG).show();
		}
				
		// Naughty Asian
		if (Preference.isPurchased(Purchases.NAUGHTY_ASIAN)) {
			mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_naughty_asian),
					CoolCategory.NAUGHTY_ASIAN_FIRST_ID, 
					CoolCategory.NAUGHTY_ASIAN_LAST_ID));
			
			//Toast.makeText(this, "Naughty_Asian add in adapter", Toast.LENGTH_LONG).show();
		}
				
		// Nationalities
		if (Preference.isPurchased(Purchases.NATIONALITIES)) {
			mFragmentParams.add(new FragmentParams(getString(R.string.cool_tab_fragment_nationalities),
					CoolCategory.NATIONALITIES_FIRST_ID, 
					CoolCategory.NATIONALITIES_LAST_ID));
		
			//Toast.makeText(this, "Nationalities add in adapter", Toast.LENGTH_LONG).show();
		}
	}
}
