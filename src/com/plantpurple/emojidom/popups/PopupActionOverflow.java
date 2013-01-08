package com.plantpurple.emojidom.popups;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.flurry.android.FlurryAgent;
import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.consts.FlurryEvents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PopupActionOverflow extends PopupSimple implements OnItemClickListener {

	public static final int MENU_ITEM_REQUEST_NEW = 0;
	public static final int MENU_ITEM_TELL_FRIENDS = 1;
	public static final int MENU_ITEM_RATE_EMOJIDOM = 2;
	public static final int MENU_ITEM_SEND_FEEDBACK = 3;
	
	private final String[] items = {context.getString(R.string.menu_item_request_new), 
			context.getString(R.string.menu_item_tell_friends), 
			context.getString(R.string.menu_item_rate_emojidom),
			context.getString(R.string.menu_item_send_us_feedback)};
	
	public PopupActionOverflow(Context context) {
		super(context, (int) context.getResources().getDimension(R.dimen.popup_menu_width), 
				WindowManager.LayoutParams.WRAP_CONTENT);
		
		popup.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.background_dark));
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.popup_main_menu, null);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_item_main_menu_item, 
				R.id.tvMenuItemName, items);
		
		ListView popupList = (ListView)layout.findViewById(R.id.listMenuItems);
		popupList.setAdapter(adapter);
		
		popupList.setOnItemClickListener(this);
		
		popup.setContentView(layout);
	}
	
	public void show(View anchor, int offsetX, int offsetY) {
		float density = context.getResources().getDisplayMetrics().density;
		
		popup.showAsDropDown(anchor, (int)(offsetX*density), (int)(offsetY*density));
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
		switch (position) {
		
		case MENU_ITEM_REQUEST_NEW: 
			AlertDialog.Builder builderRequestNew = new AlertDialog.Builder(PopupActionOverflow.this.context);
			builderRequestNew.setMessage(PopupActionOverflow.this.context.getString(R.string.request_new_dialog_text))
			       .setCancelable(false)
			       .setIcon(0)
			       .setPositiveButton(PopupActionOverflow.this.context.getString(R.string.request_new_dialog_ok), 
			    		   new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   // if OK clicked start email intent
			        	   String mailId = "hello@plantpurple.com"; 
			        	   
			        	   Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",mailId, null));  
			        	   emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, 
			        			   PopupActionOverflow.this.context.getString(R.string.request_new_subject)); 
			        	   emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
			        			   PopupActionOverflow.this.context.getString(R.string.request_new_text));
			        	   
			        	   PopupActionOverflow.this.context.startActivity(emailIntent);
			        	   
			        	   dialog.cancel();
			           }
			       })
			       .setNegativeButton(PopupActionOverflow.this.context.getString(R.string.request_new_dialog_cancel), 
			    		   new DialogInterface.OnClickListener() {
			    	   public void onClick(DialogInterface dialog, int id) {
			    		   dialog.dismiss();
			    	   }
				});
					
			AlertDialog dialogRequestNew = builderRequestNew.create();
			dialogRequestNew.show();
			
			popup.dismiss();
			
			break;
			
		case MENU_ITEM_TELL_FRIENDS:
			FlurryAgent.logEvent(FlurryEvents.EVENT_TELL_FRIENDS_PRESSED);
			
			String filePath = Environment.getExternalStorageDirectory()
					+ PopupActionOverflow.this.context.getString(R.string.xml_file_directory);
			String fileName = "emojidom_image.jpg";

			File path = new File(filePath);
			if (!path.exists()) 
				path.mkdirs();
				
			if (!(new File(filePath + fileName).exists()) ) {
				Bitmap b = null;
				try {
					b = BitmapFactory.decodeStream(PopupActionOverflow.this.context.
							getAssets().open("images/tell_friends_image.png"));
				} catch (IOException e1) { Log.e("My", "Error: get tell_friends_image.png from assets!"); }
					
				try {
					b.compress(Bitmap.CompressFormat.JPEG, 100,
							new FileOutputStream(filePath + fileName));
				} catch (FileNotFoundException e) {
					Log.e("My", "Bitmap not saved on sd-card");
				}
			}
			
			Uri emailUri = Uri.parse("mailto:");//?subject=" + 
					//PopupActionOverflow.this.context.getString(R.string.tell_friends_subject) +
					//"&body=" + PopupActionOverflow.this.context.getString(R.string.tell_friends_text));
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, emailUri);
			
			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath + fileName)));
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, 
					PopupActionOverflow.this.context.getString(R.string.tell_friends_subject));
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
					PopupActionOverflow.this.context.getString(R.string.tell_friends_text));
			//emailIntent.setType("message/rfc822");
			
			PopupActionOverflow.this.context.startActivity(emailIntent);

			popup.dismiss();
			break;
			
		case MENU_ITEM_RATE_EMOJIDOM:
			FlurryAgent.logEvent(FlurryEvents.EVENT_RATE_EMOJIDOM_PRESSED);
			
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));
			PopupActionOverflow.this.context.startActivity(intent);
			
			popup.dismiss();
			break;
			
		case MENU_ITEM_SEND_FEEDBACK:
			String mailId = "hello@plantpurple.com"; 
        	   
        	Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",mailId, null));  
        	feedbackIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, 
        			PopupActionOverflow.this.context.getString(R.string.send_feedback_subject)); 
        	   
        	PopupActionOverflow.this.context.startActivity(feedbackIntent);
        	   
			popup.dismiss();
			break;

		default:
			break;
		}
	}
}
