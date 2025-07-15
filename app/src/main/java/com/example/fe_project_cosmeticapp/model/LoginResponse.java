package com.example.fe_project_cosmeticapp.model;

public class LoginResponse {
    private int code;
    private String message;
    private TokenData token;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TokenData getToken() {
        return token;
    }

    public void setToken(TokenData token) {
        this.token = token;
    }

    public static class TokenData {
        private String token;
        private int userId;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }
}

