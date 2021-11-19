package com.huybinh2k.computerstore.model;

/**
 * Created by BinhBH on 11/4/2021.
 */
public class Items {
    private String mId;
    private String mName;
    private String mPathImage;
    private double mPrice;
    private double mDiscountPrice;
    private int mQuality;
    private String mDescription;
    private int mAssetId;
    private int mManufacturerId;
    private String mItemCode;
    private String mStatus;


    public Items(String mId, String mName, String mPathImage, int price) {
        this.mId = mId;
        this.mName = mName;
        this.mPathImage = mPathImage;
        this.mPrice = price;
    }

    public Items(String mId, String mName, String mPathImage, double price, double discount) {
        this.mId = mId;
        this.mName = mName;
        this.mPathImage = mPathImage;
        this.mPrice = price;
        this.mDiscountPrice = discount;
    }

    public Items(String mId, String mName, String mPathImage, double mPrice, double mDiscountPrice, int mQuality, String mDescription, String mStatus) {
        this.mId = mId;
        this.mName = mName;
        this.mPathImage = mPathImage;
        this.mPrice = mPrice;
        this.mDiscountPrice = mDiscountPrice;
        this.mQuality = mQuality;
        this.mDescription = mDescription;
        this.mStatus = mStatus;
    }

    public Items(String mId, String mName, String mPathImage, double mPrice, double mDiscountPrice, int mQuality, String mDescription, int mAssetId, int mManufacturerId, String mItemCode, String mStatus) {
        this.mId = mId;
        this.mName = mName;
        this.mPathImage = mPathImage;
        this.mPrice = mPrice;
        this.mDiscountPrice = mDiscountPrice;
        this.mQuality = mQuality;
        this.mDescription = mDescription;
        this.mAssetId = mAssetId;
        this.mManufacturerId = mManufacturerId;
        this.mItemCode = mItemCode;
        this.mStatus = mStatus;
    }

    public String getName() {
        return mName;
    }

    public String getPathImage() {
        return mPathImage;
    }

    public double getPrice() {
        return mPrice;
    }

    public String getID() {
        return  mId;
    }

    public double getDiscountPrice() {
        return mDiscountPrice;
    }

    public int getQuality() {
        return mQuality;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getAssetId() {
        return mAssetId;
    }

    public int getManufacturerId() {
        return mManufacturerId;
    }

    public String getItemCode() {
        return mItemCode;
    }

    public String getStatus() {
        return mStatus;
    }
}
