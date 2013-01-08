package com.plantpurple.emojidom.utils;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Class using for providing fonts in app without crashes. )
 * @author Vladimir
 */
public class MyFont{
	private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

	public static Typeface get(Context c, String name){
		synchronized(cache){
			if(!cache.containsKey(name)){
				Typeface t = Typeface.createFromAsset(
						c.getAssets(), 
						String.format("fonts/%s.ttf", name)
					);
				cache.put(name, t);
			}
			return cache.get(name);
		}
	}
}
