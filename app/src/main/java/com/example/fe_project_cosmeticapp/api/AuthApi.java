package com.example.fe_project_cosmeticapp.api;

import com.example.fe_project_cosmeticapp.model.RegisterRequest;
import com.example.fe_project_cosmeticapp.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("api/authen/login")
    Call<User> login(@Body User user);

    @POST("api/authen/register")
    Call<User> register(@Body RegisterRequest registerRequest);
}
