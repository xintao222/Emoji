package com.plantpurple.emojidom.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkState {

	public static boolean isConnected(Activity a){
		NetworkInfo mNetworkInfo = (NetworkInfo)((ConnectivityManager)a
				.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

		// if object not created return false
        if(mNetworkInfo==null) 
            return false;
        
        if (!mNetworkInfo.isConnected())
        	return false;
        
        if(mNetworkInfo.isRoaming()){
            //here is the roaming option you can change it if you want to disable internet while roaming, just return false
            return true;
        }
        
        return true;
    }
}
