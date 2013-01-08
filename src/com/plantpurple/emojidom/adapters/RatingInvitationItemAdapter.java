package com.plantpurple.emojidom.adapters;

import com.plantpurple.emojidom.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RatingInvitationItemAdapter extends BaseAdapter {
	private Context context;
	private String[] items;
	
	private LayoutInflater inflater;
	
	public RatingInvitationItemAdapter(Context context, String[] items) {
		this.context = context;
		this.items = items;
		
		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			holder = new ViewHolder();
			
			convertView = inflater.inflate(R.layout.list_item_rating_invitation, parent, false);
			holder.itemName = (TextView)convertView.findViewById(R.id.tvItem);
			
			convertView.setTag(holder);
		} else 
			holder = (ViewHolder)convertView.getTag();
		
		holder.itemName.setText(items[position]);

		return convertView;
	}

	private static class ViewHolder {
		TextView itemName;
	}
}
