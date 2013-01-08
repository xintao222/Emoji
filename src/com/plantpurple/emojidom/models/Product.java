package com.plantpurple.emojidom.models;

public class Product {
	private int drawableId;
	private int detailsId;
	
	public Product(int drawableId, int detailsId) {
		this.drawableId = drawableId;
		this.detailsId = detailsId;
	}
	
	public int getDrawableId() {
		return drawableId;
	}
	
	public int getDetailsId() {
		return detailsId;
	}
}
