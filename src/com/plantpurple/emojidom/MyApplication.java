package com.plantpurple.emojidom;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.res.Resources;

@ReportsCrashes(formKey="dHdLLUcwa0JaeXNwaVFWZEJGNnZ1TkE6MQ")
public class MyApplication extends Application {
	static Resources mRes;
	
	private static MyApplication instance;
	
	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
		
		instance = this;
		
		mRes = getResources();
	}
	
	public static Resources getRes() {
		return mRes;
	}
	
	public static MyApplication getInstance() {
		return instance;
	}
}
