package com.plantpurple.emojidom.preferences;

import com.plantpurple.emojidom.MyApplication;

import android.app.Activity;
import android.content.SharedPreferences;

public class Preference {
	private static final String PREFERENCE = "pref";
	private static SharedPreferences preference = MyApplication.getInstance().getSharedPreferences(PREFERENCE, Activity.MODE_PRIVATE);
	
	// preferences for main activity
	private static final String PREF_SHARE_COUNT = "share_count";
	private static final String PREF_IS_RATING_INVITATION_ENABLED = "rating_invitation_enabled";
	
	// preferences for in-app purchase
	public static final String PREF_IN_APP_INITIALIZED		= "in_app_initialized";
	//public static final String PREF_NO_ADS_PURCHASED 		= "no_ads_purchased";
	//public static final String PREF_HOLIDAYS_PURCHASED 		= "holidays_purchased";
	//public static final String PREF_NAUGHTY_PURCHASED 		= "naughty_purchased";
	//public static final String PREF_NAUGHTY_BLACK_PURCHASED = "naughty_black_purchased";
	//public static final String PREF_NAUGHTY_ASIAN_PURCHASED = "naughty_asian_purchased";
	//public static final String PREF_NATIONALITIES_PURCHASED = "nationalities_purchased";
	
	private Preference() {};
	
	public static void setShareCount(int shareCount) {
		initPreference();
		
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(PREF_SHARE_COUNT, shareCount);
		editor.commit();
	}
	
	public static int getShareCount() {
		initPreference();
		
		return preference.getInt(PREF_SHARE_COUNT, 0);
	}
	
	public static void setRatingInvitationEnabled(boolean isEnabled) {
		initPreference();
		
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(PREF_IS_RATING_INVITATION_ENABLED, isEnabled);
		editor.commit();
	}
	
	public static boolean isRatingInvitationEnabled() {
		initPreference();
		
		return preference.getBoolean(PREF_IS_RATING_INVITATION_ENABLED, true);
	}
	
	public static boolean isInAppInitialized() {
		initPreference();
		
		return preference.getBoolean(PREF_IN_APP_INITIALIZED, false);
	}
	
	public static void initializeInApp() {
		initPreference();
		
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(PREF_IN_APP_INITIALIZED, true);
		editor.commit();
	}
	
	/**
	 * Using for check product was purchased or not.
	 * @param purchasePrefName - const from this class containing information about purchase state
	 * @return - was purchased or not as boolean
	 */
	public static boolean isPurchased(String purchasePrefName) {
		initPreference();
		
		return preference.getBoolean(purchasePrefName, false);
	}
	
	public static void setPurchase(String purchasePrefName, boolean isPurchased) {
		initPreference();
		
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(purchasePrefName, isPurchased);
		editor.commit();
	}
	
	private static void initPreference() {
		//if (preference == null)
			//preference = MyApplication.getInstance().getSharedPreferences(PREFERENCE, Activity.MODE_PRIVATE);
	}
}
