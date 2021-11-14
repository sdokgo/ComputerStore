package com.huybinh2k.computerstore.model;

/**
 * Created by BinhBH on 11/12/2021.
 */
public class ItemMore {
    private String displayName;
    private String key;
    private String value;

    public ItemMore(String displayName, String key, String value) {
        this.displayName = displayName;
        this.key = key;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
