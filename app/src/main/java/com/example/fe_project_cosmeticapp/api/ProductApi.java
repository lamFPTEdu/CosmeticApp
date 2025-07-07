package com.example.fe_project_cosmeticapp.api;

import com.example.fe_project_cosmeticapp.model.Product;
import com.example.fe_project_cosmeticapp.model.ProductResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductApi {

    // Lấy tất cả sản phẩm với phân trang
    @GET("api/products")
    Call<ProductResponse> getProducts(
        @Query("pageSize") int pageSize,
        @Query("page") int page,
        @Query("category") String category
    );

    // Lấy một sản phẩm theo ID
    @GET("api/products/{id}")
    Call<Product> getProductById(@Path("id") int productId);

    // Lấy sản phẩm theo danh mục
    @GET("api/products")
    Call<ProductResponse> getProductsByCategory(
        @Query("category") String category,
        @Query("pageSize") int pageSize,
        @Query("page") int page
    );

    // Lấy sản phẩm theo loại da
    @GET("api/products")
    Call<ProductResponse> getProductsBySkinType(
        @Query("skinType") String skinType,
        @Query("pageSize") int pageSize,
        @Query("page") int page
    );

    // Tìm kiếm sản phẩm theo tên
    @GET("api/products")
    Call<ProductResponse> searchProducts(
        @Query("searchTerm") String searchTerm,
        @Query("pageSize") int pageSize,
        @Query("page") int page
    );

    // Lấy tất cả các danh mục sản phẩm
    @GET("api/products/categories")
    Call<String[]> getAllCategories();

    // Lấy tất cả các loại da
    @GET("api/products/skintypes")
    Call<String[]> getAllSkinTypes();

    // Lấy sản phẩm theo cả danh mục và loại da
    @GET("api/products")
    Call<ProductResponse> getProductsByCategoryAndSkinType(
        @Query("category") String category,
        @Query("skinType") String skinType,
        @Query("pageSize") int pageSize,
        @Query("page") int page
    );
}
