package com.huybinh2k.computerstore;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by BinhBH on 11/4/2021.
 */
public class Utils {
    public static boolean isConnectedInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
