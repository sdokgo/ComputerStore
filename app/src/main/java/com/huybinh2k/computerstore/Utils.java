package com.huybinh2k.computerstore;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by BinhBH on 11/4/2021.
 */
public class Utils {
    private static final String STORAGE ="Store";
    public static final String IS_LOGIN ="IS_LOGIN";
    public static final String NUMBER_ITEMS_CART = "NUMBER_ITEMS_CART";

    public static boolean isConnectedInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static void saveStringPreferences(Context context, String key, String value){
        SharedPreferences mPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static String getStringPreferences(Context context, String key) {
        SharedPreferences mPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return mPreferences.getString(key, "");
    }

    public static void saveBooleanPreferences(Context context, String key, boolean value){
        SharedPreferences mPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public static boolean getBooleanPreferences(Context context, String key) {
        SharedPreferences mPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return mPreferences.getBoolean(key, false);
    }

    public static void saveIntPreferences(Context context, String key, int value){
        SharedPreferences mPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }

    public static int getIntPreferences(Context context, String key) {
        SharedPreferences mPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return mPreferences.getInt(key, 0);
    }

    public static void removePreferences(Context context, String key) {
        SharedPreferences mPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        mPreferences.edit().remove(key).apply();
    }

    public static void hideKeyboard(View view, Activity activity){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /** BinhBH
     * @param params
     * @return chuyển về string liên kết key và value
     */
    public static String getDataString(LinkedHashMap<String, String> params){
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        return result.toString();
    }
}
