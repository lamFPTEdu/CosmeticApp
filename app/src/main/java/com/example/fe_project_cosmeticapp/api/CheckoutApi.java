package com.example.fe_project_cosmeticapp.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import com.example.fe_project_cosmeticapp.model.CheckoutRequest;
import com.example.fe_project_cosmeticapp.model.CheckoutResponse;

public interface CheckoutApi {
    @POST("api/checkout/cart")
    Call<CheckoutResponse> checkoutCart(@Header("Authorization") String token, @Body CheckoutRequest request);

    @GET("api/checkout/order/{orderId}")
    Call<CheckoutResponse> checkoutOrder(
        @Header("Authorization") String token,
        @Path("orderId") int orderId,
        @Query("productIds") String productIds // comma-separated product IDs
    );

    @POST("api/checkout")
    Call<CheckoutResponse> checkout(@Header("Authorization") String token, @Body CheckoutRequest request);
}
