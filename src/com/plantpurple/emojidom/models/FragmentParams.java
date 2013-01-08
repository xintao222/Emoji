package com.plantpurple.emojidom.models;

/**
 * This class using as fragment parameters and contain information about
 * caption of fragment and firts and last identifiers for loading smileys from
 * resources.
 * @author Vladimir
 *
 */
public class FragmentParams {
	public static final String CAPTION = "caption";
	public static final String CATEGORY = "category";
	public static final String FIRST_ID = "firstId";
	public static final String LAST_ID = "lastId";
	
	private String mCaption;
	
	private int mFirstId;
	private int mLastId;
	
	// using for favorites Cool or Classic
	private int mCategory = -1;
	
	public FragmentParams(String caption, int firstId, int lastId) {
		mCaption = caption;
		
		mFirstId = firstId;
		mLastId = lastId;
	}
	
	public FragmentParams(int category, String caption) {
		mCategory = category;
		mCaption = caption;
		
		mFirstId = 0;
		mLastId = 0;
	}
	
	public int getCategory() { return mCategory; }
	
	public String getCaption() { return mCaption; }

	public int getFirstId() { return mFirstId; }
	public int getLastId() { return mLastId; }
}
