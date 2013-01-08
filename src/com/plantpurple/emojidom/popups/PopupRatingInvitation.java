package com.plantpurple.emojidom.popups;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.adapters.RatingInvitationItemAdapter;
import com.plantpurple.emojidom.preferences.Preference;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PopupRatingInvitation extends PopupSimple implements OnItemClickListener {

	public static final int MENU_ITEM_RATE_EMOJIDOM = 0;
	public static final int MENU_ITEM_SEND_FEEDBACK = 1;
	public static final int MENU_ITEM_DONT_SHOW = 2;
	
	// using for showing this popup when share count equals 15
	public static final int POPUP_SHARE_COUNT = 15;
	
	private final String[] items = {context.getString(R.string.rating_invitation_rate_emojidom), 
			context.getString(R.string.rating_invitation_send_feedback), 
			context.getString(R.string.rating_invitation_dont_show)};
	
	public PopupRatingInvitation(Context c) {
		super(c, (int) c.getResources().getDimension(R.dimen.popup_rating_invitation_width),
				WindowManager.LayoutParams.WRAP_CONTENT);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.popup_rating_invitation, null);
		
		RatingInvitationItemAdapter adapter = new RatingInvitationItemAdapter(context, items);
		
		ListView popupList = (ListView)layout.findViewById(R.id.listItems);
		popupList.setAdapter(adapter);
		popupList.setOnItemClickListener(this);
		
		popup.setContentView(layout);
	}
	
	public void show(View anchor, int offsetX, int offsetY) {
		popup.showAtLocation(anchor, Gravity.CENTER, 0, 0);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
		switch (position) {
		
		case MENU_ITEM_RATE_EMOJIDOM: 
			Intent intentRate = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:com.plantpurple.emojidom"));
			PopupRatingInvitation.this.context.startActivity(intentRate);
			
			popup.dismiss();
			break;
			
		case MENU_ITEM_SEND_FEEDBACK:
			String mailId = "hello@plantpurple.com"; 
        	   
        	Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",mailId, null));  
        	feedbackIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, 
        			PopupRatingInvitation.this.context.getString(R.string.send_feedback_subject)); 
        	   
        	PopupRatingInvitation.this.context.startActivity(feedbackIntent);
        	   
			popup.dismiss();
			break;
			
		case MENU_ITEM_DONT_SHOW:
			Preference.setRatingInvitationEnabled(false);
			
			popup.dismiss();
			break;
			
		default:
			break;
		}
	}
}
