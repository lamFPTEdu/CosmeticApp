package com.example.fe_project_cosmeticapp.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.http.GET;
import retrofit2.http.Query;
import com.example.fe_project_cosmeticapp.model.PaypalPaymentRequest;
import com.example.fe_project_cosmeticapp.model.PaypalPaymentResponse;
import com.example.fe_project_cosmeticapp.model.PaypalCaptureRequest;
import com.example.fe_project_cosmeticapp.model.PaypalCaptureResponse;
import com.example.fe_project_cosmeticapp.model.PaypalCancelRequest;

public interface PaypalApi {
    @POST("api/Paypal/create-payment")
    Call<PaypalPaymentResponse> createPaypalPayment(@Header("Authorization") String token, @Body PaypalPaymentRequest request);

    @POST("/api/Paypal/capture-payment")
    Call<PaypalCaptureResponse> capturePaypalPayment(
            @Header("Authorization") String bearerToken,
            @Body PaypalCaptureRequest request
    );

}
