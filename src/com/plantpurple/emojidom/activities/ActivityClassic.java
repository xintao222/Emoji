package com.plantpurple.emojidom.activities;

import java.io.IOException;
import java.util.ArrayList;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.adapters.ClassicTabFragmentAdapter;
import com.plantpurple.emojidom.consts.ClassicCategory;
import com.plantpurple.emojidom.fragments.BaseTitleFragment.OnMyFragmentEventListener;
import com.plantpurple.emojidom.fragments.FragmentClassicFavourites;
import com.plantpurple.emojidom.models.FragmentParams;
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
import android.widget.RelativeLayout;

public class ActivityClassic extends FragmentActivity implements OnMyFragmentEventListener {
	private static final String TAG = "ActivityClassic";
	
	static ViewPager mViewPager;
	PagerTabStrip mPagerTabStrip;
	static ClassicTabFragmentAdapter mTabFragmentAdapter;
	private ArrayList<FragmentParams> mFragmentParams;
	
	RelativeLayout mMainLayout;
	
	// Event ids
	public static final int CLASSIC_EVENT_INSERT_SMILE = 1;
	
	// This variable using for setting current fragment on screen.
	// Defaultly, it's have 1 value. Also this var using with static method
	// setCurrentFragment, because when app starting at first time and user selecting some
	// category from navigation list the setCurrentFragment work incorrect. This var is help to do
	// correctly fragment selecting.
	private static int defaultFragmentIndex = 1;
	
	private boolean isFirstExecution = true;
	private int currentFragmentPosition = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_classic_tab);
		
		Log.d(TAG, "onCreate");
		
		currentFragmentPosition = 1;
		
		initUI();
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				currentFragmentPosition = position;
				
				// if we now in Favorites then get references to Text and Grid
				if (position == 0) {
					FragmentClassicFavourites f = (FragmentClassicFavourites)mTabFragmentAdapter.getItem(position);
					
					XmlBase xml = null;
					try {
						xml = new XmlBase(Environment.getExternalStorageDirectory() + getString(R.string.xml_file_directory));
					} catch (IOException e) {
						return;
					}
					
					f.updateUI(xml);
					
					if (f.getAdapter() == null)
						Log.e(TAG, "FavoriteFragment from Activity: adapter is NULL");
					else 
						Log.d(TAG, "FavoriteFragment from Activity: adapter count = " + f.getAdapter().getCount());
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
	}

	private void initUI() {
		mMainLayout = (RelativeLayout)findViewById(R.id.main);
		
		mViewPager = (ViewPager)findViewById(R.id.ViewPager);
		initFragmentParams();
		mTabFragmentAdapter = new ClassicTabFragmentAdapter(this, getSupportFragmentManager(), mFragmentParams);
		mViewPager.setAdapter(mTabFragmentAdapter);
		
		mPagerTabStrip = (PagerTabStrip)findViewById(R.id.PagerTabStrip);
		mPagerTabStrip.setTabIndicatorColor(Color.parseColor("#00CCFF"));
		
		if (isFirstExecution) {
			mViewPager.setCurrentItem(defaultFragmentIndex);
			isFirstExecution = false;
		} else 
			mViewPager.setCurrentItem(currentFragmentPosition);
	}
	
	public void onMyEvent(int eventId, int drawableId) {
		switch (eventId) {
		case CLASSIC_EVENT_INSERT_SMILE:
			Drawable d = getResources().getDrawable( drawableId );
			MainActivity.appendSmileToEdit(d);

			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Log.d(TAG, "onResume");
	}
	
	public static void setCurrentFragment(String fragmentName) {
		Log.d(TAG, "mViewPager = " + mViewPager);
		Log.d(TAG, "mTabFragmentAdapter = " + mTabFragmentAdapter);
		
		if (mTabFragmentAdapter != null)
			Log.d(TAG, "mTabFragmentAdapter count = " + mTabFragmentAdapter.getCount());
		
		int index = mTabFragmentAdapter.getItemIndexByTitle(fragmentName);
		Log.d(TAG, "Adapter index: " + index);
		
		if (index != -1) {
			mViewPager.setCurrentItem(index);
		} else
			mViewPager.setCurrentItem(1);
	}
	
	private void initFragmentParams() {
		mFragmentParams = new ArrayList<FragmentParams>();
		mFragmentParams.add(new FragmentParams(XmlBase.CLASSIC_EMOJI, getString(R.string.cool_tab_fragment_favourites)));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.classic_tab_fragment_smileys),
				ClassicCategory.SMILEYS_FIRST_ID, 
				ClassicCategory.SMILEYS_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.classic_tab_fragment_romantic),
				ClassicCategory.ROMANTIC_FIRST_ID, 
				ClassicCategory.ROMANTIC_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.classic_tab_fragment_gestures),
				ClassicCategory.GESTURES_FIRST_ID, 
				ClassicCategory.GESTURES_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.classic_tab_fragment_food),
				ClassicCategory.FOOD_FIRST_ID, 
				ClassicCategory.FOOD_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.classic_tab_fragment_sports),
				ClassicCategory.SPORTS_FIRST_ID, 
				ClassicCategory.SPORTS_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.classic_tab_fragment_communication),
				ClassicCategory.COMMUNICATION_FIRST_ID, 
				ClassicCategory.COMMUNICATION_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.classic_tab_fragment_shopping),
				ClassicCategory.SHOPPING_FIRST_ID, 
				ClassicCategory.SHOPPING_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.classic_tab_fragment_plants),
				ClassicCategory.PLANTS_FIRST_ID, 
				ClassicCategory.PLANTS_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.classic_tab_fragment_animals),
				ClassicCategory.ANIMALS_FIRST_ID, 
				ClassicCategory.ANIMALS_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.classic_tab_fragment_time_weather),
				ClassicCategory.TIME_WEATHER_FIRST_ID, 
				ClassicCategory.TIME_WEATHER_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.classic_tab_fragment_travel),
				ClassicCategory.TRAVEL_FIRST_ID, 
				ClassicCategory.TRAVEL_LAST_ID));
		
		mFragmentParams.add(new FragmentParams(getString(R.string.classic_tab_fragment_symbols),
				ClassicCategory.SYMBOLS_FIRST_ID, 
				ClassicCategory.SYMBOLS_LAST_ID));
	}
}
