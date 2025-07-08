package com.example.fe_project_cosmeticapp.api;

import com.example.fe_project_cosmeticapp.model.LoginRequest;
import com.example.fe_project_cosmeticapp.model.LoginResponse;
import com.example.fe_project_cosmeticapp.model.RegisterRequest;
import com.example.fe_project_cosmeticapp.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    // Đăng nhập
    @POST("api/authen/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // Đăng ký
    @POST("api/authen/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);
}

