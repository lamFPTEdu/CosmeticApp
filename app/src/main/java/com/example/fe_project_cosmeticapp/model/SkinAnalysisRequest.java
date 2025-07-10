package com.example.fe_project_cosmeticapp.model;

import com.google.gson.annotations.SerializedName;

public class SkinAnalysisRequest {
    @SerializedName("api_key")
    private String apiKey;

    @SerializedName("api_secret")
    private String apiSecret;

    @SerializedName("image_base64")
    private String imageBase64;

    public SkinAnalysisRequest(String apiKey, String apiSecret, String imageBase64) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.imageBase64 = imageBase64;
    }

    // Getters và setters
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
