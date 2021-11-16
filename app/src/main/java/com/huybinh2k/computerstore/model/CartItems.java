package com.huybinh2k.computerstore.model;

/**
 * Created by BinhBH on 11/14/2021.
 */
public class CartItems {
    private String name;
    private String idItems;
    private String idCarts;
    private float price;
    private float discount;
    private int cartQuantity;
    private int itemQuantity;
    private String image;

    public CartItems(String name, String idItems, String idCarts, float price, float discount, int cartQuantity, int itemQuantity, String image) {
        this.name = name;
        this.idItems = idItems;
        this.idCarts = idCarts;
        this.price = price;
        this.discount = discount;
        this.cartQuantity = cartQuantity;
        this.itemQuantity = itemQuantity;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getIdItems() {
        return idItems;
    }

    public float getPrice() {
        return price;
    }

    public float getDiscount() {
        return discount;
    }

    public int getCartQuantity() {
        return cartQuantity;
    }

    public String getImage() {
        return image;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public String getIdCarts() {
        return idCarts;
    }

    public void setCartQuantity(int cartQuantity) {
        this.cartQuantity = cartQuantity;
    }
}
