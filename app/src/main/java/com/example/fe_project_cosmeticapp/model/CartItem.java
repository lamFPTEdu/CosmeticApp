package com.example.fe_project_cosmeticapp.model;

import com.google.gson.annotations.SerializedName;

public class CartItem {
    @SerializedName("productId")
    private String productId;

    @SerializedName("productName")
    private String name;

    // Try different possible field names for image URL
    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("productImage")
    private String backupImageUrl;

    @SerializedName("image")
    private String secondBackupImageUrl;

    @SerializedName("productPrice")
    private double price;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("total")
    private double subtotal;

    @SerializedName("stockQuantity")
    private int stockQuantity;

    private boolean isSelected = false;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        // Check all possible image URL fields and return the first non-null one
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return imageUrl;
        } else if (backupImageUrl != null && !backupImageUrl.isEmpty()) {
            return backupImageUrl;
        } else if (secondBackupImageUrl != null && !secondBackupImageUrl.isEmpty()) {
            return secondBackupImageUrl;
        }
        return null;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBackupImageUrl() {
        return backupImageUrl;
    }

    public void setBackupImageUrl(String backupImageUrl) {
        this.backupImageUrl = backupImageUrl;
    }

    public String getSecondBackupImageUrl() {
        return secondBackupImageUrl;
    }

    public void setSecondBackupImageUrl(String secondBackupImageUrl) {
        this.secondBackupImageUrl = secondBackupImageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
