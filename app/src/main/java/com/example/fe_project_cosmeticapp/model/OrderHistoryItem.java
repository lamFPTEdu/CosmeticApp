package com.example.fe_project_cosmeticapp.model;

import com.google.gson.annotations.SerializedName;

public class OrderHistoryItem {
    @SerializedName("id")
    private int orderId;
    private String orderDate;
    private String status;
    private double totalAmount;
    // Thêm các trường khác nếu cần

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
