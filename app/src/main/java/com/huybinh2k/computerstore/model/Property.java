package com.huybinh2k.computerstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by BinhBH on 11/7/2021.
 */
public class Property {
    @SerializedName("asset_property_name")
    private String title;
    @SerializedName("value")
    private String value;

    public Property(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }
}
