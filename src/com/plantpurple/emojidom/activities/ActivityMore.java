package com.plantpurple.emojidom.activities;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.adapters.ProductAdapter;
import com.plantpurple.emojidom.consts.Purchases;
import com.plantpurple.emojidom.models.Product;
import com.plantpurple.emojidom.popups.PopupActionOverflow;
import com.plantpurple.emojidom.utils.MyFont;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMore extends RoboActivity implements OnClickListener {
	@InjectView(R.id.rlBack) RelativeLayout layoutBackButton;
	
	@InjectView(R.id.btnLetUsKnow) Button btnLetUsKnow;
	@InjectView(R.id.rlMenu) RelativeLayout mMenuLayout;
	@InjectView(R.id.tvMoreComminSoon) TextView mMoreCommingSoonText;
	
	@InjectView(R.id.listInAppProducts) ListView listInAppProducts;
	ProductAdapter productAdapter;

	protected static final String TAG = "My";
    
    // Requests for start activity for result
    public static final int REQUEST_PURCHASE = 10;
    public static final int RESULT_PURCHASE_SUCCESS = 11;
    public static final int RESULT_PURCHASE_FAIL = 12;
    
    // Intent params for start activity for result
    public static final String PARAM_PURCHASE_ITEM = "purchase_item";
    public static final String PARAM_PURCHASE_DRAWABLE_ID = "purchase_drawable_id";
    public static final String PARAM_PURCHASE_DETAILS_ID = "purchase_details_id";
    
    PopupActionOverflow popupMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_content_more);
		
		initUI();
		
		layoutBackButton.setOnClickListener(this);
		btnLetUsKnow.setOnClickListener(this);
		mMenuLayout.setOnClickListener(this);
		
		popupMenu = new PopupActionOverflow(this);
		
		listInAppProducts.setOnItemClickListener(productsOnItemClickListener);
	}
	
	private void initUI() {
		productAdapter = new ProductAdapter(this);
		listInAppProducts.setAdapter(productAdapter);
		
		mMoreCommingSoonText.setTypeface(MyFont.get(this, getString(R.string.font_name)));
		if (productAdapter.getCount() == 0)
			mMoreCommingSoonText.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlBack:
			setResult(MainActivity.RESULT_JUST_RETURN);
			finish();
			
			break;
			
		case R.id.btnLetUsKnow:
			
			AlertDialog.Builder builderRequestNew = new AlertDialog.Builder(this);
			builderRequestNew.setMessage(getString(R.string.request_new_dialog_text))
			       .setCancelable(false)
			       .setIcon(0)
			       .setPositiveButton(getString(R.string.request_new_dialog_ok), 
			    		   new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   // if OK clicked start email intent
			        	   
			        	   Uri emailUri = Uri.parse("mailto:hello@plantpurple.com");

			        	   Intent emailIntent = new Intent(Intent.ACTION_SENDTO, emailUri);
			        	   emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, 
			        			   getString(R.string.request_new_subject)); 
			        	   emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
			        			   getString(R.string.request_new_text));
			        	   
			        	   startActivity(emailIntent);
			        	   
			        	   dialog.dismiss();
			           }
			       })
			       .setNegativeButton(getString(R.string.request_new_dialog_cancel), 
			    		   new DialogInterface.OnClickListener() {
			    	   public void onClick(DialogInterface dialog, int id) {
			    		   dialog.dismiss();
			    	   }
				});
					
			AlertDialog dialogRequestNew = builderRequestNew.create();
			dialogRequestNew.show();
			
			break;
			
		case R.id.rlMenu:
			popupMenu.show(mMenuLayout, 6, 5);
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		setResult(MainActivity.RESULT_JUST_RETURN);
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (data == null)
			return;
		
		if (requestCode == REQUEST_PURCHASE) {
			if (resultCode == RESULT_PURCHASE_SUCCESS) {
				String purchaseItem = data.getStringExtra(PARAM_PURCHASE_ITEM);
				
				Intent intent = new Intent();
				intent.putExtra(MainActivity.PARAM_PURCHASE_NAME, purchaseItem);
				
				setResult(MainActivity.RESULT_PURCHASE_SUCCESS, intent);
				finish();
			}
			else if (resultCode == RESULT_PURCHASE_FAIL) {
				Toast.makeText(this, R.string.error_purchase_failed, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private OnItemClickListener productsOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
			
			Product product = (Product)adapterView.getItemAtPosition(position);
			
			String purchaseItem = null;
			
			switch (product.getDetailsId()) {
			case R.string.in_app_purchase_no_ads:
				purchaseItem = Purchases.NO_ADS;
				break;
				
			case R.string.in_app_purchase_holidays:
				purchaseItem = Purchases.HOLIDAYS;
				break;
				
			case R.string.in_app_purchase_naughty:
				purchaseItem = Purchases.NAUGHTY_WHITE;
				break;
				
			case R.string.in_app_purchase_naughty_black:
				purchaseItem = Purchases.NAUGHTY_BLACK;
				break;
				
			case R.string.in_app_purchase_naughty_asian:
				purchaseItem = Purchases.NAUGHTY_ASIAN;
				break;
				
			case R.string.in_app_purchase_nationalities:
				purchaseItem = Purchases.NATIONALITIES;
				break;

			default:
				break;
			}
			
			Log.i(TAG, "Purchase item: " + purchaseItem);
			
			Intent intent = new Intent(ActivityMore.this, ActivityPurchaseDetails.class);
			intent.putExtra(PARAM_PURCHASE_ITEM, purchaseItem);
			intent.putExtra(PARAM_PURCHASE_DRAWABLE_ID, product.getDrawableId());
			intent.putExtra(PARAM_PURCHASE_DETAILS_ID, product.getDetailsId());
			startActivityForResult(intent, REQUEST_PURCHASE);
		}
	};
}
