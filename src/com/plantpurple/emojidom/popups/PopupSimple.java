package com.plantpurple.emojidom.popups;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;

public abstract class PopupSimple {
	protected Context context;
	
	protected PopupWindow popup;
	
	public PopupSimple(Context c, int width, int height) {
		context = c;
		
		popup = new PopupWindow(context);
		popup.setWidth(width);
		popup.setHeight(height);
		
		popup.setOutsideTouchable(true);
		popup.setTouchable(true);
		popup.setFocusable(true);
		
		popup.setTouchInterceptor(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popup.dismiss();
					return true;
				} else
					return false;
			}
		});
	}
	
	public abstract void show(View anchor, int offsetX, int offsetY);
}
