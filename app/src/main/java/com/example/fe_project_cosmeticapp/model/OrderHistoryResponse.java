package com.example.fe_project_cosmeticapp.model;

import java.util.List;

public class OrderHistoryResponse {
    private List<OrderHistoryItem> orders;

    public List<OrderHistoryItem> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderHistoryItem> orders) {
        this.orders = orders;
    }
}

