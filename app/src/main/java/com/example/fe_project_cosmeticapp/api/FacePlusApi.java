package com.example.fe_project_cosmeticapp.api;

import com.example.fe_project_cosmeticapp.model.SkinAnalysisResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FacePlusApi {
    /**
     * Phân tích da mặt sử dụng Face++ API
     * @param apiKey API key của Face++
     * @param apiSecret API secret của Face++
     * @param imageFile Ảnh selfie dưới dạng file
     * @return Kết quả phân tích da
     */
    @Multipart
    @POST("facepp/v1/skinanalyze")
    Call<SkinAnalysisResponse> analyzeSkin(
            @Part("api_key") RequestBody apiKey,
            @Part("api_secret") RequestBody apiSecret,
            @Part MultipartBody.Part imageFile
    );
}
