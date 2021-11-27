package com.huybinh2k.computerstore.model;

/**
 * Created by BinhBH on 11/26/2021.
 */
public class Region {
    private int id;
    private int parentId;
    private String name;

    public Region(int id, int parent_id, String name) {
        this.id = id;
        this.parentId = parent_id;
        this.name = name;
    }

    public Region(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
