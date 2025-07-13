package com.example.fe_project_cosmeticapp.model;

public class PaypalCaptureRequest {
    private int orderId;
    private String token;
    private String payerId;

    public PaypalCaptureRequest(int orderId, String token, String payerId) {
        this.orderId = orderId;
        this.token = token;
        this.payerId = payerId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }
}
