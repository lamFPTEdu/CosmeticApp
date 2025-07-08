package com.example.fe_project_cosmeticapp.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    private String email;
    private String password;
    @SerializedName("name") // Đổi tên trường cho đồng bộ với RegisterResponse và backend
    private String fullName;

    public RegisterRequest(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
