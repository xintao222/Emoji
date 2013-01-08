package com.plantpurple.emojidom.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MineImage {
	private String filePath;
	private Bitmap bitmap;
	
	public MineImage(String imageFilePath) {
		filePath = imageFilePath;
		bitmap = BitmapFactory.decodeFile(filePath);
	}
	
	public MineImage(Bitmap b) {
		bitmap = b;
		filePath = null;
	}
	
	public MineImage(Bitmap b, String imageFilePath) {
		bitmap = b;
		filePath = imageFilePath;
	}

	public String getImagePath() { return filePath; }
	public Bitmap getBitmap() { return bitmap; }
}
