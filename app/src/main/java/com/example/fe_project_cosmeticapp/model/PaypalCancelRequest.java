package com.example.fe_project_cosmeticapp.model;

public class PaypalCancelRequest {
    private int orderId;
    public PaypalCancelRequest(int orderId) { this.orderId = orderId; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
}

