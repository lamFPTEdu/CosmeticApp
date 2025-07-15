package com.example.fe_project_cosmeticapp.api;

import com.example.fe_project_cosmeticapp.model.UserProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {
    @GET("api/Users/{userId}")
    Call<UserProfile> getUserProfile(@Path("userId") int userId);

    @PUT("api/Users/{userId}")
    Call<UserProfile> updateUserProfile(@Path("userId") int userId, @Body UserProfile userProfile);
}

