package com.plantpurple.emojidom.adapters;

import java.util.ArrayList;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.activities.MainActivity;
import com.plantpurple.emojidom.consts.Purchases;
import com.plantpurple.emojidom.models.PopupNavigationItem;
import com.plantpurple.emojidom.preferences.Preference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	
	private ArrayList<PopupNavigationItem> items;
	
	public NavigationAdapter(Context c) {
		context = c;
		inflater = LayoutInflater.from(context);
		
		initValues();
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		//if (convertView == null) {
			holder = new ViewHolder();
			
			if (items.get(position).isSection()) {
				// it is header, get it layout..
				convertView = inflater.inflate(R.layout.list_item_side_navigation_category_header, null);
				holder.categoryText = (TextView)convertView.findViewById(R.id.tvCategoryName);
				holder.categoryImage = null;
				holder.headerImage = (ImageView)convertView.findViewById(R.id.imgCategory);
				
				// header is not clickable layout
				convertView.setOnClickListener(null);
				convertView.setOnLongClickListener(null);
			} else {
				// it is item, get layout for this..
				convertView = inflater.inflate(R.layout.list_item_side_navigation_category, null);
				holder.categoryText = (TextView)convertView.findViewById(R.id.tvCategoryName);
				holder.categoryImage = (ImageView)convertView.findViewById(R.id.imgSmile);
				holder.headerImage = null;
			}
			
			convertView.setTag(holder);
		//} else
			//holder = (ViewHolder)convertView.getTag();
		
		PopupNavigationItem item = items.get(position);
		
		if (item.isSection()) {
			holder.categoryText.setText(item.getName());
			
			if (item.getName().equals(context.getString(R.string.tab_cool))) {
				if (holder.headerImage != null)
					holder.headerImage.setImageResource(R.drawable.icon_cool);
			}
			else {
				if (holder.headerImage != null)
					holder.headerImage.setImageResource(R.drawable.icon_classic);
			}
		} else {
			holder.categoryText.setText(item.getName());
			if (holder.categoryImage != null)
				holder.categoryImage.setImageResource(item.getImageId());
		}
		
		return convertView;
	}
	
	private void initValues() {
		items = new ArrayList<PopupNavigationItem>();
		
		// Initialize cool category
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL, 
				context.getString(R.string.tab_cool), 0, true));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
				context.getString(R.string.cool_tab_fragment_favourites), R.drawable.star, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
				context.getString(R.string.cool_tab_fragment_happy), R.drawable.cool1, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
				context.getString(R.string.cool_tab_fragment_romantic), R.drawable.cool46, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
				context.getString(R.string.cool_tab_fragment_angry), R.drawable.cool91, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
				context.getString(R.string.cool_tab_fragment_expressions), R.drawable.cool143, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
				context.getString(R.string.cool_tab_fragment_descriptive), R.drawable.cool186, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
				context.getString(R.string.cool_tab_fragment_clothes), R.drawable.cool227, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
				context.getString(R.string.cool_tab_fragment_looks), R.drawable.cool271, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
				context.getString(R.string.cool_tab_fragment_busy), R.drawable.cool323, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
				context.getString(R.string.cool_tab_fragment_leisure), R.drawable.cool361, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
				context.getString(R.string.cool_tab_fragment_food), R.drawable.cool411, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
				context.getString(R.string.cool_tab_fragment_sport), R.drawable.cool453, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
				context.getString(R.string.cool_tab_fragment_places), R.drawable.cool507, false));
		
		// In-app items initialization in navigation panel:
		// Holidays
		if (Preference.isPurchased(Purchases.HOLIDAYS))
			items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
					context.getString(R.string.cool_tab_fragment_holidays), R.drawable.cool641, false));
				
		// Naughty
		if (Preference.isPurchased(Purchases.NAUGHTY_WHITE))
			items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
					context.getString(R.string.cool_tab_fragment_naughty), R.drawable.cool546, false));
		
		// Naughty Black
		if (Preference.isPurchased(Purchases.NAUGHTY_BLACK))
			items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
					context.getString(R.string.cool_tab_fragment_naughty_black), R.drawable.cool576, false));
		
		//Naughty Asian
		if (Preference.isPurchased(Purchases.NAUGHTY_ASIAN))
			items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
					context.getString(R.string.cool_tab_fragment_naughty_asian), R.drawable.cool606, false));
		
		// ..and last is Nationalities
		if (Preference.isPurchased(Purchases.NATIONALITIES))
			items.add(new PopupNavigationItem(MainActivity.TAB_TAG_COOL,
					context.getString(R.string.cool_tab_fragment_nationalities), R.drawable.cool676, false));
		
		// Initialize classic category
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.tab_classic), 0, true));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.cool_tab_fragment_favourites), R.drawable.star, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.classic_tab_fragment_smileys), 
				R.drawable.classic1, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.classic_tab_fragment_romantic), 
				R.drawable.classic81, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.classic_tab_fragment_gestures), 
				R.drawable.classic105, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.classic_tab_fragment_food), 
				R.drawable.classic138, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.classic_tab_fragment_sports), 
				R.drawable.classic174, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.classic_tab_fragment_communication), 
				R.drawable.classic238, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.classic_tab_fragment_shopping), 
				R.drawable.classic256, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.classic_tab_fragment_plants), 
				R.drawable.classic284, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.classic_tab_fragment_animals), 
				R.drawable.classic323, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.classic_tab_fragment_time_weather), 
				R.drawable.classic398, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.classic_tab_fragment_travel), 
				R.drawable.classic460, false));
		items.add(new PopupNavigationItem(MainActivity.TAB_TAG_CLASSIC,
				context.getString(R.string.classic_tab_fragment_symbols), 
				R.drawable.classic549, false));
	}
	
	private static class ViewHolder {
		TextView categoryText;
		ImageView categoryImage;
		ImageView headerImage;
	}
}
