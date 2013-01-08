package com.plantpurple.emojidom.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.models.MineImage;

public class MineAdapter extends BaseAdapter {
	Context context;
	
	ArrayList<MineImage> mineImages;
	
	LayoutInflater inflater;
	
	public MineAdapter(Context c) {
		context = c;
		
		mineImages = new ArrayList<MineImage>();
		
		// Add button bitmap in array firstly
		MineImage addButtonImage = new MineImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.add_photo));
		mineImages.add(addButtonImage);
		
		inflater = LayoutInflater.from(context);
	}

	public void removeItem(int clickPosition) {
		mineImages.remove(clickPosition);
	}

	public int getCount() {
		// return size of bitmap array. Real size will be -1 because in first position
		// always will be "Add" button
		return mineImages.size();
	}

	public Object getItem(int position) {
		return mineImages.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			holder = new ViewHolder();
			
			convertView = inflater.inflate(R.layout.list_item_smile, parent, false);
			
			holder.image = (ImageView)convertView.findViewById(R.id.imgImage);
			convertView.setTag(holder);
		} else 
			holder = (ViewHolder)convertView.getTag();
		
		holder.image.setImageBitmap(mineImages.get(position).getBitmap());

		return convertView;
	}
	
	public void addMineImage(MineImage image) {
		mineImages.add(1, image);
	}
	
	public void addMineImages(ArrayList<MineImage> array) {
		for (int i = 0; i != array.size(); ++i) {
			mineImages.add(array.get(i));
		}
	}
	
	static class ViewHolder {
		ImageView image;
	}
}
