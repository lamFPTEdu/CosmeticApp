package com.example.fe_project_cosmeticapp.model;

public class PaypalCaptureResponse {
    private boolean success;
    private String status;
    private String message;
    private boolean cancelled;

    public boolean isSuccess() { return success; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public boolean isCancelled() { return cancelled; }
}
