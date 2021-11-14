package com.huybinh2k.computerstore.model;

/**
 * Created by BinhBH on 11/10/2021.
 */
public class Manufacturer {
    private String id;
    private String name;
    private String logo;

    public Manufacturer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Manufacturer(String id, String name, String logo) {
        this.id = id;
        this.name = name;
        this.logo = logo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }
}
