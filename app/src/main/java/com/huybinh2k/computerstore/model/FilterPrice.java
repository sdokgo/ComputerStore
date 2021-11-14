package com.huybinh2k.computerstore.model;

/**
 * Created by BinhBH on 11/11/2021.
 */
public class FilterPrice {
    private String name;
    private float minPrice;
    private float maxPrice;

    public FilterPrice(String name, float minPrice, float maxPrice) {
        this.name = name;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public String getName() {
        return name;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public float getMaxPrice() {
        return maxPrice;
    }
}
