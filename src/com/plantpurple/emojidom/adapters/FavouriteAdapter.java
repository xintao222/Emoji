package com.plantpurple.emojidom.adapters;

import java.util.ArrayList;

import com.plantpurple.emojidom.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class FavouriteAdapter extends BaseAdapter {
	ArrayList<Integer> items;
	
	LayoutInflater inflater;
	
	public FavouriteAdapter(Context context) {
		items = new ArrayList<Integer>();
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			holder = new ViewHolder();
			
			convertView = inflater.inflate(R.layout.list_item_smile, parent, false);
			
			holder.smile = (ImageView)convertView.findViewById(R.id.imgImage);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder)convertView.getTag();
		
		holder.smile.setImageResource(items.get(position));

		return convertView;
	}
	
	public void setItems(ArrayList<Integer> array) {
		if (items.size() != 0)
			items.clear();
		
		items = array;
	}
	
	public void removeItem(int position) {
		items.remove(position);
	}
	
	private static class ViewHolder {
		ImageView smile;
	}
}


