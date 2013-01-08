package com.plantpurple.emojidom.activities;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlManager;
import com.adwhirl.AdWhirlTargeting;
import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.flurry.android.FlurryAgent;
import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.consts.CoolCategory;
import com.plantpurple.emojidom.consts.FlurryEvents;
import com.plantpurple.emojidom.consts.Purchases;
import com.plantpurple.emojidom.models.FragmentParams;
import com.plantpurple.emojidom.popups.PopupActionOverflow;
import com.plantpurple.emojidom.popups.PopupNavigation;
import com.plantpurple.emojidom.popups.PopupRatingInvitation;
import com.plantpurple.emojidom.preferences.Preference;
import com.plantpurple.emojidom.tasks.ShareMessageTask;
import com.plantpurple.emojidom.utils.MyFont;
import com.plantpurple.emojidom.utils.NetworkState;

import roboguice.activity.RoboTabActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class MainActivity extends RoboTabActivity implements OnClickListener, AdWhirlInterface {
	private static final String TAG = "ActivityCool";
	
	@InjectView(R.id.main) RelativeLayout mMainLayout;
	@InjectView(R.id.rlTopBar) RelativeLayout mTopBarLayout;
	
	@InjectView(R.id.rlList) RelativeLayout mListLayout;
	@InjectView(R.id.rlShare) RelativeLayout mShareLayout;
	@InjectView(R.id.rlClear) RelativeLayout mClearLayout;
	@InjectView(R.id.rlBackspace) RelativeLayout mBackspaseLayout;
	@InjectView(R.id.rlMenu) RelativeLayout mMenuLayout;
	
	@InjectView(R.id.scroll) ScrollView mScrollView;
	@InjectView(R.id.rlMessage) RelativeLayout mMessageLayout;
	static EditText mMessageEdit;
	
	static TabHost mTabHost;
	public static final String TAB_TAG_KEYBOARD = "Keyboard";
	public static final String TAB_TAG_MINE = "Mine";
	public static final String TAB_TAG_COOL = "Cool";
	public static final String TAB_TAG_CLASSIC = "Classic";
	public static final String TAB_TAG_MORE = "More";
	
	// Intent request for share results
	public static final int REQUEST_SHARE_RESULT = 10;
	private static final int REQUEST_OPEN_MORE = 11;
	
	public static final int RESULT_JUST_RETURN = 20;
	public static final int RESULT_PURCHASE_SUCCESS = 21;
	public static final String PARAM_PURCHASE_NAME = "purchase_name";
	
	private PopupActionOverflow popupMenu;
	private PopupRatingInvitation popupRatingInvitation;
	
	// using for insert Mine smileys in typing area
	static int factWidth;
	static int factHeight;
	
	IabHelper mIAPsHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // check for NO_ADS purchased..
        if (Preference.isPurchased(Purchases.NO_ADS))
        	setContentView(R.layout.activity_main_no_ads);
        else
        	setContentView(R.layout.activity_main);
        
        Log.d(TAG, "MainActivity onCreate");
        
        factWidth = (int) getResources().getDimension(R.dimen.smile_width);
    	factHeight = (int) getResources().getDimension(R.dimen.smile_height);
        
        //initUI();
        
        // for selectors
    	mListLayout.setOnClickListener(this);
    	mShareLayout.setOnClickListener(this);
    	mClearLayout.setOnClickListener(this);
    	mBackspaseLayout.setOnClickListener(this);
    	mMenuLayout.setOnClickListener(this);
        
    	mMessageEdit = (EditText)findViewById(R.id.editMessage);
    	mMessageEdit.setTypeface(MyFont.get(this, getString(R.string.font_name)));
        mScrollView.setOnTouchListener(mScrollTouchListener);
        
        popupMenu = new PopupActionOverflow(this);
        popupRatingInvitation = new PopupRatingInvitation(this);
        
        if (!Preference.isPurchased(Purchases.NO_ADS)) {
        	AdWhirlManager.setConfigExpireTimeout(1000 * 60 * 5);
        	AdWhirlTargeting.setTestMode(false);
        } 
        
        progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getString(R.string.loading));
        
        // restore transaction
		// init IAPs library components
		mIAPsHelper = new IabHelper(this, Purchases.BASE64_ENCODED_PUBLIC_KEY);
		mIAPsHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					Log.d(TAG, "Problem setting up In-app Billing: " + result);
					
					initUI();
					
					return;
				}

				Log.d(TAG, "Setting up In-app Billing: " + result);
				
				// If it's first app execution then do query for restoring purchase.
				//if (!Preference.isInAppInitialized()) {
					if (NetworkState.isConnected(MainActivity.this)) {
						
						//mIAPsHelper.queryInventoryAsync(mGotInventoryListener);
						new RestoreIAPTask().execute();
					} else {
						// just initialize UI...
						initUI();
						
						// ... and show Toast with error
						Toast.makeText(MainActivity.this, R.string.error_cant_restoring_purchase, Toast.LENGTH_LONG).show();
					}
				//}
			}
		});
    }

    private void initUI() {
    	mTabHost = getTabHost();
    	
    	// add tabs
    	mTabHost.addTab(createTab(TAB_TAG_KEYBOARD, R.layout.tab_indicator_keyboard, 
    			R.drawable.tab_keyboard, R.id.rlKeyboard));
    	mTabHost.addTab(createTab(TAB_TAG_MINE, R.layout.tab_indicator_camera, 
    			R.drawable.tab_camera, new Intent(this, ActivityMine.class)));
    	mTabHost.addTab(createTab(TAB_TAG_COOL, R.layout.tab_indicator_cool, 
    			R.drawable.tab_cool, new Intent(this, ActivityCool.class)));
    	mTabHost.addTab(createTab(TAB_TAG_CLASSIC, R.layout.tab_indicator_classic, 
    			R.drawable.tab_classic, new Intent(this, ActivityClassic.class)));
    	mTabHost.addTab(createTab(TAB_TAG_MORE, R.layout.tab_indicator_more, 
    			R.drawable.tab_more, R.id.rlKeyboard));
    	
    	mTabHost.setCurrentTabByTag(TAB_TAG_COOL);
    	
    	mTabHost.setOnTabChangedListener(mTabChangeListaner);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	FlurryAgent.onStartSession(this, getString(R.string.flurry_private_key));
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	
    	FlurryAgent.onEndSession(this);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	if (mIAPsHelper != null) mIAPsHelper.dispose();
		mIAPsHelper = null;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	Log.d("My", "Share request = " + requestCode);
    	Log.d("My", "Share result = " + resultCode);
    	
    	if (requestCode == REQUEST_OPEN_MORE) {
    		if (resultCode == RESULT_JUST_RETURN || resultCode == 0)
    			mTabHost.setCurrentTabByTag(TAB_TAG_COOL);
    		
    		if (resultCode == RESULT_PURCHASE_SUCCESS) {
    			if (data != null) {
    				String purchaseItem = data.getStringExtra(PARAM_PURCHASE_NAME);
    				
    				if (purchaseItem.equals(Purchases.NO_ADS)) {
    					AdWhirlLayout adLayout = (AdWhirlLayout)findViewById(R.id.adwhirl_layout);
    					
    					if (adLayout != null) {
    						mMainLayout.removeView(adLayout);
    						
    						// set layout params for edit typing area
    						LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 
    								LayoutParams.MATCH_PARENT, 
    								0.5f);
    						mScrollView.setLayoutParams(params);
    					}
    					
    					setCurrentTabAndFragment(TAB_TAG_COOL, getString(R.string.cool_tab_fragment_happy));
    				}
    				
    				if (purchaseItem.equals(Purchases.NAUGHTY_WHITE)) {
    					FragmentParams params = new FragmentParams(getString(R.string.cool_tab_fragment_naughty),
    							CoolCategory.NAUGHTY_FIRST_ID, 
    							CoolCategory.NAUGHTY_LAST_ID);

    					ActivityCool.addFragment(params);
    					
    					setCurrentTabAndFragment(TAB_TAG_COOL, getString(R.string.cool_tab_fragment_naughty));
    				}
    				
    				if (purchaseItem.equals(Purchases.NAUGHTY_BLACK)) {
    					FragmentParams params = new FragmentParams(getString(R.string.cool_tab_fragment_naughty_black),
    							CoolCategory.NAUGHTY_BLACK_FIRST_ID, 
    							CoolCategory.NAUGHTY_BLACK_LAST_ID);
    					
    					ActivityCool.addFragment(params);
    					
    					setCurrentTabAndFragment(TAB_TAG_COOL, getString(R.string.cool_tab_fragment_naughty_black));
    				}
    				
    				if (purchaseItem.equals(Purchases.NAUGHTY_ASIAN)) {
    					FragmentParams params = new FragmentParams(getString(R.string.cool_tab_fragment_naughty_asian),
    							CoolCategory.NAUGHTY_ASIAN_FIRST_ID, 
    							CoolCategory.NAUGHTY_ASIAN_LAST_ID);
    					
    					ActivityCool.addFragment(params);
    					
    					setCurrentTabAndFragment(TAB_TAG_COOL, getString(R.string.cool_tab_fragment_naughty_asian));
    				}
    				
    				if (purchaseItem.equals(Purchases.HOLIDAYS)) {
    					FragmentParams params = new FragmentParams(getString(R.string.cool_tab_fragment_holidays),
    							CoolCategory.HOLIDAYS_FIRST_ID, 
    							CoolCategory.HOLIDAYS_LAST_ID);
    					
    					ActivityCool.addFragment(params);
    					
    					setCurrentTabAndFragment(TAB_TAG_COOL, getString(R.string.cool_tab_fragment_holidays));
    				}
    				
    				if (purchaseItem.equals(Purchases.NATIONALITIES)) {
    					FragmentParams params = new FragmentParams(getString(R.string.cool_tab_fragment_nationalities),
    							CoolCategory.NATIONALITIES_FIRST_ID, 
    							CoolCategory.NATIONALITIES_LAST_ID);
    					
    					ActivityCool.addFragment(params);
    					
    					setCurrentTabAndFragment(TAB_TAG_COOL, getString(R.string.cool_tab_fragment_nationalities));
    				}
    			}
    		}
    	}
    	
    	if (requestCode == REQUEST_SHARE_RESULT) {
    		
    		// increment share count for showing popup
    		Preference.setShareCount(Preference.getShareCount() + 1);
    		Log.d("My", "Share count: " + Preference.getShareCount());
    		
    		// check for count of sharing. If it equals on 15 then show rating
    		// invitation popup window
    		if (Preference.isRatingInvitationEnabled()) 
    			if ((Preference.getShareCount() % PopupRatingInvitation.POPUP_SHARE_COUNT) == 0)
    				popupRatingInvitation.show(mMainLayout, 0, 0);
    	}
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlList:
			int width = getResources().getDisplayMetrics().widthPixels;
	        PopupNavigation popupNavigation = new PopupNavigation(this, width);
			popupNavigation.show(mListLayout, -20, 0);
			
			break;
			
		case R.id.rlClear:
			mMessageEdit.setText("");

			break;
			
		case R.id.rlMenu:
			popupMenu.show(mMenuLayout, 6, 4);
			
			break;
			
		case R.id.rlBackspace:
			mMessageEdit.onKeyDown(KeyEvent.KEYCODE_DEL, new KeyEvent(KeyEvent.KEYCODE_DEL, KeyEvent.ACTION_DOWN));
			
			break;
			
		case R.id.rlShare:
			// if typing area is empty then show message
			if (mMessageEdit.getText().toString().equals("")) {
				AlertDialog.Builder builderRequestNew = new AlertDialog.Builder(MainActivity.this);
				builderRequestNew.setMessage(getString(R.string.type_something))
				       .setCancelable(false)
				       .setIcon(0)
				       .setPositiveButton(getString(R.string.request_new_dialog_ok), new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   dialog.cancel();
				           }
				       });
						
				AlertDialog dialogRequestNew = builderRequestNew.create();
				dialogRequestNew.show();
				
				return;
			}
			
			FlurryAgent.logEvent(FlurryEvents.EVENT_SHARE_PRESSED);
			
			// using for hide cursor from typing area. It must be invisible in area
			mMessageEdit.clearFocus();
			
			// create bitmap from inputed message
			mMessageLayout.setDrawingCacheEnabled(true);
			Bitmap image = Bitmap.createBitmap(mMessageLayout.getDrawingCache());
			mMessageLayout.setDrawingCacheEnabled(false);
			
			// create bitmap for message
			Bitmap messageBitmap = Bitmap.createBitmap(image.getWidth(), 
					image.getHeight()+45, 
					Bitmap.Config.ARGB_8888);
			
			Canvas canvas = new Canvas(messageBitmap);
			
			// fill by white color..
			Rect rect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
			Paint whitePaint = new Paint();
			whitePaint.setColor(Color.WHITE);
			canvas.drawRect(rect, whitePaint);
			
			// draw message..
			canvas.drawBitmap(image, 0, 0, new Paint());
			
			Paint paintConfig = new Paint();
			paintConfig.setTextSize(14f);
			paintConfig.setAntiAlias(true);
			paintConfig.setColor(Color.parseColor("#380EFF"));
			
			// draw long line and text below..
			canvas.drawLine(5, image.getHeight()+15, image.getWidth()-5, image.getHeight()+15, paintConfig);
			canvas.drawText(getString(R.string.made_in_emojidom), 5, image.getHeight()+35, paintConfig);
			
			// Start share task
			new ShareMessageTask(this).execute(messageBitmap);
			
			break;

		default:
			break;
		}
	}
	
	public static void setCurrentTabAndFragment(String tabTag, String fragmentName) {
		Log.d("My", "MainActivity: set current tab called");
		
		Log.d(TAG, "mTabHost = " + mTabHost);
		mTabHost.setCurrentTabByTag(tabTag);
		
		if (tabTag.equals(TAB_TAG_CLASSIC)) 
			ActivityClassic.setCurrentFragment(fragmentName);
		else if (tabTag.equals(TAB_TAG_COOL)) 
			ActivityCool.setCurrentFragment(fragmentName);   
	}
	
	OnTouchListener mScrollTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mMessageEdit.requestFocus();
				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mMessageEdit, InputMethodManager.SHOW_IMPLICIT);
			}

			return false;
		}
	};

	OnTabChangeListener mTabChangeListaner = new OnTabChangeListener() {
		public void onTabChanged(String tabId) {
			
			if (tabId.equals(TAB_TAG_MINE)) {
				FlurryAgent.logEvent(FlurryEvents.EVENT_MINE_TAB_OPENED);
				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mMessageEdit.getWindowToken(), 0);
			}
			
			if (tabId.equals(TAB_TAG_COOL)) {
				FlurryAgent.logEvent(FlurryEvents.EVENT_COOL_TAB_OPENED);
				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mMessageEdit.getWindowToken(), 0);
			}
			
			if (tabId.equals(TAB_TAG_CLASSIC)) {
				FlurryAgent.logEvent(FlurryEvents.EVENT_CLASSIC_TAB_OPENED);
				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mMessageEdit.getWindowToken(), 0);
			}
			
			if (tabId.equals(TAB_TAG_KEYBOARD)) {
				mMessageEdit.requestFocus();
				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mMessageEdit, InputMethodManager.SHOW_IMPLICIT);
			}
			
			if (tabId.equals(TAB_TAG_MORE)) {
				FlurryAgent.logEvent(FlurryEvents.EVENT_MORE_TAB_OPENED);
				
				startActivityForResult(new Intent(MainActivity.this, ActivityMore.class),
						REQUEST_OPEN_MORE);
			}
		}
	};

	public void adWhirlGeneric() {}
	
	/**
	 * Using for sending data between main activity and tab content activity 
	 * with smiles.
	 * @param d - smile drawable
	 */
	public static void appendSmileToEdit(Drawable d) {
		SpannableString ss = new SpannableString("()");
		
		// prepare size of image using 75% ratio
		float ratio = 0.75f;
		Log.d("My", "first: " + d.getIntrinsicWidth() + "x" + d.getIntrinsicHeight());
		int width = (int)(d.getIntrinsicWidth() * ratio);
		int height = (int)(d.getIntrinsicHeight() * ratio);
		
		d.setBounds(0, 0, width, height);
		ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
		ss.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE); 

		//mMessageEdit.getText().insert(mMessageEdit.getSelectionStart(), " ");
		mMessageEdit.getText().insert(mMessageEdit.getSelectionStart(), ss);
		//mMessageEdit.getText().insert(mMessageEdit.getSelectionStart(), " ");
	}
	
	public static void appendMineSmileToEdit(Drawable d) {
		SpannableString ss = new SpannableString("()");
		
		// prepare size of image using 75% ratio
		float ratio = 0.75f;
		Log.d("My", "second: " + factWidth + "x" + factHeight);
		
		int width = (int)(factWidth * ratio);
		int height = (int)(factHeight * ratio);
		
		d.setBounds(0, 0, width, height);
		ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
		ss.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE); 

		//mMessageEdit.getText().insert(mMessageEdit.getSelectionStart(), " ");
		mMessageEdit.getText().insert(mMessageEdit.getSelectionStart(), ss);
		mMessageEdit.getText().insert(mMessageEdit.getSelectionStart(), " ");
	}
	
	/**
	 * Create a tab with next parameters
	 * @param tag - tag name for tab
	 * @param indicator - string id contain ref to tab caption
	 * @param intent - tab content, using activity
	 * @return - tabspec object
	 */
	private TabSpec createTab(String tag, int layoutId, int drawableId, Intent intent) {
		TabHost.TabSpec tabSpec;
        
        tabSpec = mTabHost.newTabSpec(tag);
        tabSpec.setContent(intent);
        
        View v = getLayoutInflater().inflate(layoutId, null);
        //ImageView imageIndicator = (ImageView)v.findViewById(R.id.imgImage);
        //imageIndicator.setImageResource(drawableId);
        tabSpec.setIndicator(v);
        
        return tabSpec;
	}
	
	/**
	 * Creating tab with next parameters
	 * @param tag - tab tag
	 * @param stringId - caption
	 * @param contentId - content layout id
	 * @return - tabSpec
	 */
	private TabSpec createTab(String tag, int layoutId, int drawableId, int contentId) {
		TabHost.TabSpec tabSpec;
        
        tabSpec = mTabHost.newTabSpec(tag);
        tabSpec.setContent(contentId);
        
        View v = getLayoutInflater().inflate(layoutId, null);
        //ImageView imageIndicator = (ImageView)v.findViewById(R.id.imgImage);
        //imageIndicator.setImageResource(drawableId);
        tabSpec.setIndicator(v);
        
        return tabSpec;
	}
	
	/*IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

			if (result.isFailure()) {
				Log.e(TAG, "MainActivity: purchase restoring error");
			} else {
				// does the user have some purchases?
				if (inventory.hasPurchase(Purchases.NO_ADS)) {
					Log.d(TAG, "Purchase NO_ADS is purcahsed.");
					
					Preference.setPurchase(Purchases.NO_ADS, true);
					
					// hide ads layout
					AdWhirlLayout adLayout = (AdWhirlLayout)findViewById(R.id.adwhirl_layout);
					if (adLayout != null) {
						mMainLayout.removeView(adLayout);
						
						// set layout params for edit typing area
						LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 
								LayoutParams.MATCH_PARENT, 
								0.5f);
						mScrollView.setLayoutParams(params);
					}
				} else {
					Log.d(TAG, "Purchase NO_ADS is not purcahsed.");
				}
				
				if (inventory.hasPurchase(Purchases.HOLIDAYS))
					Preference.setPurchase(Purchases.HOLIDAYS, true);
				
				if (inventory.hasPurchase(Purchases.NAUGHTY_WHITE))
					Preference.setPurchase(Purchases.NAUGHTY_WHITE, true);
				
				if (inventory.hasPurchase(Purchases.NAUGHTY_BLACK))
					Preference.setPurchase(Purchases.NAUGHTY_BLACK, true);
				
				if (inventory.hasPurchase(Purchases.NAUGHTY_ASIAN))
					Preference.setPurchase(Purchases.NAUGHTY_ASIAN, true);
				
				if (inventory.hasPurchase(Purchases.NATIONALITIES))
					Preference.setPurchase(Purchases.NATIONALITIES, true);
				
				// update UI accordingly...
			}
		}
	};*/
	
	private ProgressDialog progressDialog;
	
	private class RestoreIAPTask extends AsyncTask<Void, Void, Inventory> {

		protected void onPreExecute() {
			super.onPreExecute();
			
			progressDialog.show();
		};

		@Override
		protected Inventory doInBackground(Void... params) {
			
			Inventory inventory = null;
			try {
				inventory = mIAPsHelper.queryInventory(true, null);
			} catch (Exception e) {
				
			}
			
			return inventory;
		}
		
		@Override
		protected void onPostExecute(Inventory inventory) {
			super.onPostExecute(inventory);
			
			progressDialog.dismiss();
			
			if (inventory != null) {
				Log.i(TAG, "Inventory loading success!");
				
				Log.d(TAG, "No_ads purchase status:" + inventory.hasPurchase(Purchases.NO_ADS));
				Log.d(TAG, "Holidays purchase status:" + inventory.hasPurchase(Purchases.HOLIDAYS));
				Log.d(TAG, "Naughty White purchase status:" + inventory.hasPurchase(Purchases.NAUGHTY_WHITE));
				Log.d(TAG, "Naughty Black purchase status:" + inventory.hasPurchase(Purchases.NAUGHTY_BLACK));
				Log.d(TAG, "Naughty Asian purchase status:" + inventory.hasPurchase(Purchases.NAUGHTY_ASIAN));
				Log.d(TAG, "Nationalities purchase status:" + inventory.hasPurchase(Purchases.NATIONALITIES));
				
				// does the user have some purchases?
				if (inventory.hasPurchase(Purchases.NO_ADS)) {
					Log.d(TAG, "Purchase NO_ADS is purcahsed.");
					
					Preference.setPurchase(Purchases.NO_ADS, true);
					
					// hide ads layout
					AdWhirlLayout adLayout = (AdWhirlLayout)findViewById(R.id.adwhirl_layout);
					if (adLayout != null) {
						mMainLayout.removeView(adLayout);
						
						// set layout params for edit typing area
						LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 
								LayoutParams.MATCH_PARENT, 
								0.5f);
						mScrollView.setLayoutParams(params);
					}
				} else {
					Log.d(TAG, "Purchase NO_ADS is not purcahsed.");
				}
				
				if (inventory.hasPurchase(Purchases.HOLIDAYS))
					Preference.setPurchase(Purchases.HOLIDAYS, true);
				
				if (inventory.hasPurchase(Purchases.NAUGHTY_WHITE))
					Preference.setPurchase(Purchases.NAUGHTY_WHITE, true);
				
				if (inventory.hasPurchase(Purchases.NAUGHTY_BLACK))
					Preference.setPurchase(Purchases.NAUGHTY_BLACK, true);
				
				if (inventory.hasPurchase(Purchases.NAUGHTY_ASIAN))
					Preference.setPurchase(Purchases.NAUGHTY_ASIAN, true);
				
				if (inventory.hasPurchase(Purchases.NATIONALITIES))
					Preference.setPurchase(Purchases.NATIONALITIES, true);
				
				/*String message = "No_ads purchase status:" + inventory.hasPurchase(Purchases.NO_ADS) + "\n" +
						"Holidays purchase status:" + inventory.hasPurchase(Purchases.HOLIDAYS) + "\n" +
						"Naughty White purchase status:" + inventory.hasPurchase(Purchases.NAUGHTY_WHITE) + "\n" +
						"Naughty Black purchase status:" + inventory.hasPurchase(Purchases.NAUGHTY_BLACK) + "\n" +
						"Naughty Asian purchase status:" + inventory.hasPurchase(Purchases.NAUGHTY_ASIAN) + "\n" +
						"Nationalities purchase status:" + inventory.hasPurchase(Purchases.NATIONALITIES);
				
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();*/
				
				Log.i(TAG, "Purchaces are restored");
			} else {
				Log.e(TAG, "Inventory loading FAILED!");
			}
			
			// after restoring purchases we initialize UI
			initUI();
		}
	}
}
