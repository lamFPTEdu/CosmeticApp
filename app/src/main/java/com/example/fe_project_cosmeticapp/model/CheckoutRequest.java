package com.example.fe_project_cosmeticapp.model;

import java.util.List;

public class CheckoutRequest {
    private String paymentMethod;
    private List<Item> items;

    public CheckoutRequest(String paymentMethod, List<Item> items) {
        this.paymentMethod = paymentMethod;
        this.items = items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
