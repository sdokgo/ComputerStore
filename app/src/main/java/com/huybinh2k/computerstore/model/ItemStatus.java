package com.huybinh2k.computerstore.model;

/**
 * Created by BinhBH on 11/11/2021.
 */
public class ItemStatus {
    private String id;
    private String name;

    public ItemStatus(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
