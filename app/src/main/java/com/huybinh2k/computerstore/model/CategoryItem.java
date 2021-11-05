package com.huybinh2k.computerstore.model;

/**
 * Created by BinhBH on 10/17/2021.
 */
public class CategoryItem {
    private final String mID;
    private final String mNameCate;
    private final String mUriImage;

    public CategoryItem(String mID, String mNameCate, String mUriImage) {
        this.mID = mID;
        this.mNameCate = mNameCate;
        this.mUriImage = mUriImage;
    }

    public String getNameCategory() {
        return mNameCate;
    }

    public String getUriImage() {
        return mUriImage;
    }

    public String getID() {
        return mID;
    }
}
