package com.plantpurple.emojidom.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.adapters.CoolAdapter;
import com.plantpurple.emojidom.consts.CoolCategory;
import com.plantpurple.emojidom.consts.Purchases;
import com.plantpurple.emojidom.popups.PopupActionOverflow;
import com.plantpurple.emojidom.preferences.Preference;
import com.plantpurple.emojidom.utils.NetworkState;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class ActivityPurchaseDetails extends RoboActivity implements OnClickListener {
	
	@InjectView(R.id.rlBack) RelativeLayout mBackButtonLayout;
	@InjectView(R.id.btnPurchase) Button mPurchaseButton;
	@InjectView(R.id.rlMenu) RelativeLayout mMenuButtonLayout;
	
	@InjectView(R.id.imgProductImage) ImageView mProductImage;
	@InjectView(R.id.tvProductName) TextView mProductName;
	
	@InjectView(R.id.imgImage1) ImageView mNoAdsImage;
	
	@InjectView(R.id.gridSmiles) GridView mSmileysGrid;
	CoolAdapter mAdapter;
	
	protected static final String TAG = "My";
	
	// Columns count for nationalities and holidays category. For else using 6 
	// columns by default (was set in layout).
	private static final int HOLIDAYS_AND_NATIONALITIES_NUM_COLUMNS = 5;
	
	IabHelper mIAPsHelper;
	
	// Selected purchase item from list of previous activity and result for this
	private String purchaseItem;
	private int purchaseResult;
	
	PopupActionOverflow popupMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_purchase_details);
		
		initUI();
		
		mBackButtonLayout.setOnClickListener(this);
		mMenuButtonLayout.setOnClickListener(this);
		mPurchaseButton.setOnClickListener(this);
		
		mSmileysGrid.setOnItemClickListener(null);
		mSmileysGrid.setOnItemLongClickListener(null);
		mSmileysGrid.setClickable(false);
		
		// init IAPs library components
		mIAPsHelper = new IabHelper(this, Purchases.BASE64_ENCODED_PUBLIC_KEY);
		mIAPsHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					Log.d(TAG, "Problem setting up In-app Billing: " + result);
				}

				Log.d(TAG, "Setting up In-app Billing: " + result);
				
				// get information about price of item
				if (NetworkState.isConnected(ActivityPurchaseDetails.this)) {
					List<String> additionalSkuList = new ArrayList<String>();
					additionalSkuList.add(purchaseItem);
					mIAPsHelper.queryInventoryAsync(true, additionalSkuList, mQueryFinishedListener);
				}
			}
		});
	}

	private void initUI() {
		int drawableId = getIntent().getIntExtra(ActivityMore.PARAM_PURCHASE_DRAWABLE_ID, 0);
		mProductImage.setImageResource(drawableId);
		
		int detailsId = getIntent().getIntExtra(ActivityMore.PARAM_PURCHASE_DETAILS_ID, 0);
		mProductName.setText(detailsId);
		
		// get purchase item from intent..
		purchaseItem = getIntent().getStringExtra(ActivityMore.PARAM_PURCHASE_ITEM);
		
		if (purchaseItem.equals(Purchases.NO_ADS)) {
			mNoAdsImage.setVisibility(ImageView.VISIBLE);
		
			purchaseResult = Purchases.RESULT_NO_ADS;
		}
		
		if (purchaseItem.equals(Purchases.NAUGHTY_WHITE)) {
			mAdapter = new CoolAdapter(this, CoolCategory.NAUGHTY_FIRST_ID, CoolCategory.NAUGHTY_LAST_ID);
			
			purchaseResult = Purchases.RESULT_NAUGHTY_WHITE;
		}
		
		if (purchaseItem.equals(Purchases.NAUGHTY_BLACK)) {
			mAdapter = new CoolAdapter(this, CoolCategory.NAUGHTY_BLACK_FIRST_ID, CoolCategory.NAUGHTY_BLACK_LAST_ID);
		
			purchaseResult = Purchases.RESULT_NAUGHTY_BLACK;
		}
		
		if (purchaseItem.equals(Purchases.NAUGHTY_ASIAN)) {
			mAdapter = new CoolAdapter(this, CoolCategory.NAUGHTY_ASIAN_FIRST_ID, CoolCategory.NAUGHTY_ASIAN_LAST_ID);
			
			purchaseResult = Purchases.RESULT_NAUGHTY_ASIAN;
		}
		
		if (purchaseItem.equals(Purchases.HOLIDAYS)) {
			mAdapter = new CoolAdapter(this, CoolCategory.HOLIDAYS_FIRST_ID, CoolCategory.HOLIDAYS_LAST_ID);
			
			mSmileysGrid.setNumColumns(HOLIDAYS_AND_NATIONALITIES_NUM_COLUMNS);
			
			purchaseResult = Purchases.RESULT_HOLIDAYS;
		}
		
		if (purchaseItem.equals(Purchases.NATIONALITIES)) {
			mAdapter = new CoolAdapter(this, CoolCategory.NATIONALITIES_FIRST_ID, CoolCategory.NATIONALITIES_LAST_ID);
			
			mSmileysGrid.setNumColumns(HOLIDAYS_AND_NATIONALITIES_NUM_COLUMNS);
			
			purchaseResult = Purchases.RESULT_NATIONALITIES;
		}
		
		mSmileysGrid.setAdapter(mAdapter);
		
		popupMenu = new PopupActionOverflow(this);
	}
	
	@Override
    protected void onStart() {
        super.onStart();

        //ResponseHandler.register(purchaseObserver);
    }
	
	@Override
	protected void onStop() {
		super.onStop();

		//ResponseHandler.unregister(purchaseObserver);
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();	
		
		if (mIAPsHelper != null) mIAPsHelper.dispose();
		mIAPsHelper = null;
	}

	@Override
	public void onClick(View v) {
		if (v == mMenuButtonLayout) 
			popupMenu.show(mMenuButtonLayout, 6, 5);
		
		if (v == mPurchaseButton) {
			if (NetworkState.isConnected(this)) {
				try {
					mIAPsHelper.launchPurchaseFlow(this, purchaseItem, purchaseResult, mPurchaseFinishedListener, purchaseItem);
				} catch (Exception e) {
					Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			} else
				Toast.makeText(this, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
		}
		
		if (v == mBackButtonLayout) {
			
			/*Preference.setPurchase(Purchases.NAUGHTY_BLACK, true);
			
			Intent intent = new Intent();
			intent.putExtra(ActivityMore.PARAM_PURCHASE_ITEM, Purchases.NAUGHTY_BLACK);
			setResult(ActivityMore.RESULT_PURCHASE_SUCCESS, intent);*/
			finish();
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.d(TAG, "Request: " + requestCode);
		Log.d(TAG, "Result: " + resultCode);
		
		if (requestCode == Purchases.RESULT_NO_ADS) {
			if (resultCode == RESULT_OK) {
				Preference.setPurchase(Purchases.NO_ADS, true);
				
				Intent intent = new Intent();
				intent.putExtra(ActivityMore.PARAM_PURCHASE_ITEM, Purchases.NO_ADS);
				setResult(ActivityMore.RESULT_PURCHASE_SUCCESS, intent);
				finish();
			}
		}
		
		if (requestCode == Purchases.RESULT_HOLIDAYS) {
			if (resultCode == RESULT_OK) {
				Preference.setPurchase(Purchases.HOLIDAYS, true);
				
				Intent intent = new Intent();
				intent.putExtra(ActivityMore.PARAM_PURCHASE_ITEM, Purchases.HOLIDAYS);
				setResult(ActivityMore.RESULT_PURCHASE_SUCCESS, intent);
				finish();
			}
		}
		
		if (requestCode == Purchases.RESULT_NAUGHTY_WHITE) {
			if (resultCode == RESULT_OK) {
				Preference.setPurchase(Purchases.NAUGHTY_WHITE, true);
				
				Intent intent = new Intent();
				intent.putExtra(ActivityMore.PARAM_PURCHASE_ITEM, Purchases.NAUGHTY_WHITE);
				setResult(ActivityMore.RESULT_PURCHASE_SUCCESS, intent);
				finish();
			}
		}
		
		if (requestCode == Purchases.RESULT_NAUGHTY_BLACK) {
			if (resultCode == RESULT_OK) {
				Preference.setPurchase(Purchases.NAUGHTY_BLACK, true);
				
				Intent intent = new Intent();
				intent.putExtra(ActivityMore.PARAM_PURCHASE_ITEM, Purchases.NAUGHTY_BLACK);
				setResult(ActivityMore.RESULT_PURCHASE_SUCCESS, intent);
				finish();
			}
		}
		
		if (requestCode == Purchases.RESULT_NAUGHTY_ASIAN) {
			if (resultCode == RESULT_OK) {
				Preference.setPurchase(Purchases.NAUGHTY_ASIAN, true);
				
				Intent intent = new Intent();
				intent.putExtra(ActivityMore.PARAM_PURCHASE_ITEM, Purchases.NAUGHTY_ASIAN);
				setResult(ActivityMore.RESULT_PURCHASE_SUCCESS, intent);
				finish();
			}
		}
		
		if (requestCode == Purchases.RESULT_NATIONALITIES) {
			if (resultCode == RESULT_OK) {
				Preference.setPurchase(Purchases.NATIONALITIES, true);
				
				Intent intent = new Intent();
				intent.putExtra(ActivityMore.PARAM_PURCHASE_ITEM, Purchases.NATIONALITIES);
				setResult(ActivityMore.RESULT_PURCHASE_SUCCESS, intent);
				finish();
			}
		}
	};
	
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			//Intent intent = new Intent();
			//intent.putExtra(ActivityMore.PARAM_PURCHASE_ITEM, purchase.getSku());
			
			Log.d(TAG, "Purchase query is finished");

			// error, send fail result to previous activity
			if (result.isFailure()) {
				Log.d(TAG, "Error purchasing: " + result);
				
				setResult(ActivityMore.RESULT_PURCHASE_FAIL);
				finish();
				
				return;
			} 
			/*else if (purchase.getSku().equals(Purchases.NO_ADS)) {
				Preference.setPurchase(Purchases.NO_ADS, true);
			} 
			else if (purchase.getSku().equals(Purchases.HOLIDAYS)) {
				Preference.setPurchase(Purchases.HOLIDAYS, true);
			} 
			else if (purchase.getSku().equals(Purchases.NAUGHTY_WHITE)) {
				Preference.setPurchase(Purchases.NAUGHTY_WHITE, true);
			} 
			else if (purchase.getSku().equals(Purchases.NAUGHTY_BLACK)) {
				Preference.setPurchase(Purchases.NAUGHTY_BLACK, true);
			} 
			else if (purchase.getSku().equals(Purchases.NAUGHTY_ASIAN)) {
				Preference.setPurchase(Purchases.NAUGHTY_ASIAN, true);
			} 
			else if (purchase.getSku().equals(Purchases.NATIONALITIES)) {
				Preference.setPurchase(Purchases.NATIONALITIES, true);
			}

			setResult(ActivityMore.RESULT_PURCHASE_SUCCESS, intent);
			finish();*/
		}
	};
	
	IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			if (result.isFailure()) {
				// handle error
				Log.e(TAG, "Error: price not loaded.");
				return;
			}

			// get price for selected item
			String itemPrice = inventory.getSkuDetails(purchaseItem).getPrice();
			Log.d(TAG, "Item: " + purchaseItem + ", price: " + itemPrice);
			
			String language = Locale.getDefault().getLanguage();
			
			Log.i(TAG, "Language: " + language);
			if (language.equals("ko") || language.equals("ja"))
				mPurchaseButton.setText(itemPrice + " " + getString(R.string.buy_button_text));
			else
				mPurchaseButton.setText(getString(R.string.buy_button_text) + " " + itemPrice);
		}
	};
}
