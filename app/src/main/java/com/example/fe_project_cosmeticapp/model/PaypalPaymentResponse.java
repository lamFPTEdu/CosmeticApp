package com.example.fe_project_cosmeticapp.model;

public class PaypalPaymentResponse {
    private boolean success;
    private String approvalUrl;
    private int orderId;
    private double amount;

    public boolean isSuccess() { return success; }
    public String getApprovalUrl() { return approvalUrl; }
    public int getOrderId() { return orderId; }
    public double getAmount() { return amount; }
}

