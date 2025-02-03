package com.firstapp.homework2.Product;

public class ProductItem {
    private String name;
    private int quantity;

    public ProductItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return name + " - כמות: " + quantity;
    }
}
