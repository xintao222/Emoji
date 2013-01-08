package com.plantpurple.emojidom.models;

public class PopupNavigationItem {
	private String categoryType; // cool or classic
	private String categoryName;
	private int categoryImageId;
	private boolean isSection;
	
	public PopupNavigationItem(String type, String name, int imageId, boolean isSection) {
		categoryType = type;
		categoryName = name;
		categoryImageId = imageId;
		this.isSection = isSection;
	}
	
	public String getType() {
		return categoryType;
	}
	
	public String getName() {
		return categoryName;
	}
	
	public int getImageId() {
		return categoryImageId;
	}
	
	public boolean isSection() {
		return isSection;
	}
}
