package com.example.fe_project_cosmeticapp.api;

import com.example.fe_project_cosmeticapp.model.CartResponse;
import com.example.fe_project_cosmeticapp.model.CartUpdateRequest;
import com.example.fe_project_cosmeticapp.model.CartItemRequest;
import com.example.fe_project_cosmeticapp.model.MessageResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartApi {
    @GET("api/cart")
    Call<CartResponse> getCart(@Header("Authorization") String token);

    @POST("api/cart/add")
    Call<MessageResponse> addToCart(@Header("Authorization") String token, @Body CartItemRequest request);

    @PUT("api/cart/update")
    Call<MessageResponse> updateCartItem(@Header("Authorization") String token, @Body CartUpdateRequest request);

    @DELETE("api/cart/remove/{productId}")
    Call<MessageResponse> removeFromCart(@Header("Authorization") String token, @Path("productId") String productId);

    @DELETE("api/cart/clear")
    Call<MessageResponse> clearCart(@Header("Authorization") String token);

    @GET("api/cart/count")
    Call<Integer> getCartCount(@Header("Authorization") String token);

    @GET("api/cart/total")
    Call<Double> getCartTotal(@Header("Authorization") String token);
}
