package com.plantpurple.emojidom.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.consts.Purchases;
import com.plantpurple.emojidom.models.Product;
import com.plantpurple.emojidom.preferences.Preference;

public class ProductAdapter extends BaseAdapter {
	Context context;
	
	ArrayList<Product> products;
	LayoutInflater inflater;
	
	public ProductAdapter(Context c) {
		context = c;
		products = new ArrayList<Product>();
		
		inflater = LayoutInflater.from(context);
		
		initProducts();
	}

	@Override
	public int getCount() {
		return products.size();
	}

	@Override
	public Object getItem(int position) {
		return products.get(position);
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
			
			convertView = inflater.inflate(R.layout.list_item_product, null);
			
			holder.image = (ImageView)convertView.findViewById(R.id.imgImage);
			holder.text = (TextView)convertView.findViewById(R.id.tvDetails);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder)convertView.getTag();
		
		holder.image.setImageResource(products.get(position).getDrawableId());

		holder.text.setText(products.get(position).getDetailsId());
		
		return convertView;
	}
	
	private void initProducts() {
		// Init in-app items using information from preference. If it's not purchased (false)
		// then insert product in array.
		
		if (!Preference.isPurchased(Purchases.NO_ADS))
			products.add(new Product(R.drawable.cool320, R.string.in_app_purchase_no_ads));
		
		Log.d("My", "Product adapter: Holidays - " + Preference.isPurchased(Purchases.HOLIDAYS));
		if (!Preference.isPurchased(Purchases.HOLIDAYS))
			products.add(new Product(R.drawable.cool641, R.string.in_app_purchase_holidays));
		
		if (!Preference.isPurchased(Purchases.NAUGHTY_WHITE))
			products.add(new Product(R.drawable.cool546, R.string.in_app_purchase_naughty));
		
		if (!Preference.isPurchased(Purchases.NAUGHTY_BLACK))
			products.add(new Product(R.drawable.cool576, R.string.in_app_purchase_naughty_black));
		
		if (!Preference.isPurchased(Purchases.NAUGHTY_ASIAN))
			products.add(new Product(R.drawable.cool606, R.string.in_app_purchase_naughty_asian));
		
		if (!Preference.isPurchased(Purchases.NATIONALITIES))
			products.add(new Product(R.drawable.cool676, R.string.in_app_purchase_nationalities));
	}
	
	private static class ViewHolder {
		ImageView image;
		TextView text;
	}
}