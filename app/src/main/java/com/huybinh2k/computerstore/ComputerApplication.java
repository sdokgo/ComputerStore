package com.huybinh2k.computerstore;

import android.app.Application;

import com.huybinh2k.computerstore.model.CartItems;

import java.util.LinkedHashMap;

/**
 * Created by BinhBH on 11/27/2021.
 */
public class ComputerApplication extends Application {

    public static ComputerApplication Instance;
    public static LinkedHashMap<String, CartItems> mMapCart = new LinkedHashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
    }
}
