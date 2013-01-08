package com.plantpurple.emojidom.popups;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.activities.MainActivity;
import com.plantpurple.emojidom.adapters.NavigationAdapter;
import com.plantpurple.emojidom.models.PopupNavigationItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PopupNavigation extends PopupSimple implements OnItemClickListener {
	
	// Using for calculate popup width on screen. This popup 
	// will replace with 1/4 of screen size.
	private static final double POPUP_WIDTH_RATIO = 0.75;

	public PopupNavigation(Context c, int screenWidth) {
		// Call parent constructor with popup size equals 1/4 width of screen
		// and WRAP_CONTENT for popup height
		super(c, (int)(screenWidth * POPUP_WIDTH_RATIO), WindowManager.LayoutParams.WRAP_CONTENT);
		
		popup.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.background_dark));
		
		// get layout for popup window
		LayoutInflater inflater = LayoutInflater.from(context);
		View popupLayout = inflater.inflate(R.layout.popup_rearrange, null);
		
		ListView list = (ListView)popupLayout.findViewById(R.id.listCategories);
		NavigationAdapter adapter = new NavigationAdapter(context);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
		popup.setContentView(popupLayout);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
		PopupNavigationItem item = (PopupNavigationItem)adapterView.getItemAtPosition(position);
		
		MainActivity.setCurrentTabAndFragment(item.getType(), item.getName());
		
		popup.dismiss();
	}

	@Override
	public void show(View anchor, int offsetX, int offsetY) {
		float density = context.getResources().getDisplayMetrics().density;
		
		int x = (int)(offsetX * density);
		int y = (int)(offsetY * density);
			
		//popup.showAtLocation(anchor, Gravity.LEFT, x, y);
		
		popup.showAsDropDown(anchor, x, y);
	}
}
