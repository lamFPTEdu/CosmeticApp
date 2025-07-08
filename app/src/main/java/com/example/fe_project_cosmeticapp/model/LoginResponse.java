package com.example.fe_project_cosmeticapp.model;

public class LoginResponse {
    private String token;
    private String message;
    private String email;
    private String name;
    // Thêm các trường khác nếu API trả về

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
