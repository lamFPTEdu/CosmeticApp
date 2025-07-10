package com.example.fe_project_cosmeticapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CartResponse {
    @SerializedName("items")
    private List<CartItem> items;

    // Try all possible field names for the total price
    @SerializedName("total")
    private double totalPrice;

    @SerializedName("totalPrice")
    private Double backupTotalPrice;

    @SerializedName("cartTotal")
    private Double secondBackupTotalPrice;

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotalPrice() {
        // If the primary total price is 0, try the backup fields
        if (totalPrice == 0 && backupTotalPrice != null && backupTotalPrice > 0) {
            return backupTotalPrice;
        }
        if (totalPrice == 0 && secondBackupTotalPrice != null && secondBackupTotalPrice > 0) {
            return secondBackupTotalPrice;
        }
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Calculate total manually if needed
    public double calculateTotalFromItems() {
        if (items == null || items.isEmpty()) {
            return 0;
        }

        double calculatedTotal = 0;
        for (CartItem item : items) {
            calculatedTotal += item.getSubtotal();
        }
        return calculatedTotal;
    }
}
