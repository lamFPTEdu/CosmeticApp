package com.example.fe_project_cosmeticapp.model;

public class CheckoutResponse {
    private boolean success;
    private String message;
    private int orderId;
    private double totalAmount;
    private String paymentStatus;
    private boolean requiresPaymentRedirect;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getOrderId() { return orderId; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public boolean isRequiresPaymentRedirect() { return requiresPaymentRedirect; }
}

