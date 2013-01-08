package com.plantpurple.emojidom.adapters;

import java.util.ArrayList;

import com.plantpurple.emojidom.MyApplication;
import com.plantpurple.emojidom.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class CoolAdapter extends BaseAdapter {
	private static final String COOL_CATEGORY = "cool";
	
	LayoutInflater mInflater;
	Context mContext;
	
	ArrayList<Integer> ids;
	
	// using for loading drawable ids in loop
	int firstId, lastId;
	
	public CoolAdapter(Context c, int firstId, int lastId) {
		mContext = c;
		mInflater = LayoutInflater.from(c);
		
		this.firstId = firstId;
		this.lastId = lastId;
		
		ids = new ArrayList<Integer>();
		loadEmojiCool(firstId, lastId);
	}

	public int getCount() {
		return ids.size();
	}

	public Object getItem(int position) {
		return ids.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			holder = new ViewHolder();
			
			convertView = mInflater.inflate(R.layout.list_item_smile, parent, false);
			
			holder.image = (ImageView)convertView.findViewById(R.id.imgImage);
			convertView.setTag(holder);
		} else 
			holder = (ViewHolder)convertView.getTag();
		
		holder.image.setImageResource(ids.get(position));

		return convertView;
	}
	
	static class ViewHolder {
		ImageView image;
	}
	
	private void loadEmojiCool(int first, int last) {
		String drawableName = null;
		int drawableId = 0;
		for (int i = first; i != last+1; ++i) {
			drawableName = COOL_CATEGORY + i; 
			drawableId = mContext.getResources().getIdentifier(drawableName, "drawable", 
					MyApplication.getInstance().getPackageName());
			
			ids.add(drawableId);
		}
	}
}
