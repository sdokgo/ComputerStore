package com.huybinh2k.computerstore.model;

/**
 * Created by BinhBH on 11/4/2021.
 */
public class Items {
    private String mId;
    private String mName;
    private String mPathImage;
    private String mCost;

    public Items(String mId, String mName, String mPathImage, String mCost) {
        this.mId = mId;
        this.mName = mName;
        this.mPathImage = mPathImage;
        this.mCost = mCost;
    }

    public String getName() {
        return mName;
    }

    public String getPathImage() {
        return mPathImage;
    }

    public String getCost() {
        return mCost;
    }

    public String getID() {
        return  mId;
    }
}
