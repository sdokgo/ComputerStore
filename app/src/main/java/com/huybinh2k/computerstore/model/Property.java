package com.huybinh2k.computerstore.model;

/**
 * Created by BinhBH on 11/7/2021.
 */
public class Property {
    private String title;
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
