package com.example.fe_project_cosmeticapp.model;

public class PaypalPaymentRequest {
    private int orderId;

    public PaypalPaymentRequest(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}

