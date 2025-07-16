package com.example.fe_project_cosmeticapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fe_project_cosmeticapp.api.CartApi;
import com.example.fe_project_cosmeticapp.api.PaypalApi;
import com.example.fe_project_cosmeticapp.api.RetrofitClient;
import com.example.fe_project_cosmeticapp.model.PaypalCancelRequest;
import com.example.fe_project_cosmeticapp.model.PaypalCaptureResponse;
import com.example.fe_project_cosmeticapp.utils.SessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebViewActivity extends AppCompatActivity {
    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_ORDER_ID = "extra_order_id";

    private WebView webView;
    private String orderId;
    private String orderNumber;
    private PaypalApi paypalApi;
    private SessionManager sessionManager;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng FrameLayout để chồng WebView và Button
        FrameLayout rootLayout = new FrameLayout(this);
        webView = new WebView(this);
        FrameLayout.LayoutParams webParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        rootLayout.addView(webView, webParams);

        Button btnBackToApp = new Button(this);
        btnBackToApp.setText("Xác nhận thanh toán");
        FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        btnParams.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL;
        btnParams.bottomMargin = 60;
        btnParams.topMargin = 0;
        btnParams.leftMargin = 0;
        btnParams.rightMargin = 0;
        btnBackToApp.setLayoutParams(btnParams);
        btnBackToApp.setAlpha(0.85f);
        btnBackToApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi nhấn xác nhận thanh toán, kiểm tra trạng thái đơn hàng qua API
                com.example.fe_project_cosmeticapp.utils.SessionManager sessionManager = new com.example.fe_project_cosmeticapp.utils.SessionManager(WebViewActivity.this);
                com.example.fe_project_cosmeticapp.model.User user = sessionManager.getUser();
                if (user == null) {
                    Toast.makeText(WebViewActivity.this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                int userId = user.getId();
                com.example.fe_project_cosmeticapp.api.UserApi userApi = com.example.fe_project_cosmeticapp.api.RetrofitClient.getUserApi();
                userApi.getOrderHistory(userId).enqueue(new retrofit2.Callback<com.example.fe_project_cosmeticapp.model.OrderHistoryResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.fe_project_cosmeticapp.model.OrderHistoryResponse> call, retrofit2.Response<com.example.fe_project_cosmeticapp.model.OrderHistoryResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            java.util.List<com.example.fe_project_cosmeticapp.model.OrderHistoryItem> orders = response.body().getOrders();
                            boolean found = false;
                            if (orders != null) {
                                for (com.example.fe_project_cosmeticapp.model.OrderHistoryItem order : orders) {
                                    if (String.valueOf(order.getOrderId()).equals(orderId)) {
                                        found = true;
                                        if ("Pending".equalsIgnoreCase(order.getStatus())) {
                                            Toast.makeText(WebViewActivity.this, "Đơn hàng đang chờ thanh toán lại!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(WebViewActivity.this, com.example.fe_project_cosmeticapp.OrderHistoryActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            startActivity(intent);
                                            finish();
                                        } else if ("Success".equalsIgnoreCase(order.getStatus()) || "Completed".equalsIgnoreCase(order.getStatus())) {
                                            Toast.makeText(WebViewActivity.this, "Đơn hàng đã thanh toán thành công!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(WebViewActivity.this, com.example.fe_project_cosmeticapp.OrderHistoryActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(WebViewActivity.this, "Trạng thái đơn hàng: " + order.getStatus(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(WebViewActivity.this, com.example.fe_project_cosmeticapp.OrderHistoryActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                        break;
                                    }
                                }
                            }
                            if (!found) {
                                Toast.makeText(WebViewActivity.this, "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WebViewActivity.this, "Không thể kiểm tra trạng thái đơn hàng!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(retrofit2.Call<com.example.fe_project_cosmeticapp.model.OrderHistoryResponse> call, Throwable t) {
                        Toast.makeText(WebViewActivity.this, "Lỗi kết nối khi kiểm tra đơn hàng!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        // Add the button after the WebView so it appears on top and at the bottom
        rootLayout.addView(btnBackToApp);
        setContentView(rootLayout);

        paypalApi = RetrofitClient.getPaypalApi();
        sessionManager = new SessionManager(this);

        Intent intent = getIntent();
        String url = intent.getStringExtra(EXTRA_URL);
        orderId = intent.getStringExtra(EXTRA_ORDER_ID);
        orderNumber = intent.getStringExtra("extra_order_number");
        ArrayList<String> selectedProductIds = intent.getStringArrayListExtra("selectedProductIds");

        if (url == null) {
            Toast.makeText(this, "Không có link thanh toán.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // TODO: Nếu cần truyền selectedProductIds lên backend khi xác nhận/capture thanh toán,
        // hãy truyền selectedProductIds vào request hoặc gọi API phù hợp.
        // Hiện tại, selectedProductIds đã được truyền từ CartActivity và có thể sử dụng ở đây.

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                android.util.Log.d("PayPalWebView", "onPageFinished: " + url);
                Toast.makeText(WebViewActivity.this, url, Toast.LENGTH_SHORT).show();
                // Nếu API trả về status trên trang, kiểm tra và tự động đóng WebView
                if (url.contains("status=Success") || url.contains("/success") || url.contains("/completed")) {
                    // Lấy các tham số từ URL
                    Uri uri = Uri.parse(url);
                    String orderNumber = uri.getQueryParameter("orderNumber");
                    String paypalToken = uri.getQueryParameter("token");
                    String payerId = uri.getQueryParameter("PayerID");
                    String bearerToken = sessionManager.getToken();
                    if (bearerToken != null && !bearerToken.startsWith("Bearer ")) {
                        bearerToken = "Bearer " + bearerToken;
                    }
                    android.util.Log.d("PayPalCapture", "orderNumber=" + orderNumber + ", paypalToken=" + paypalToken + ", payerId=" + payerId + ", bearerToken=" + bearerToken);
                    if (orderNumber != null && paypalToken != null && payerId != null && bearerToken != null) {
                        android.util.Log.d("PayPalCapture", "Calling capturePaypalPayment...");
                        // Tạo request object đúng chuẩn backend
                        com.example.fe_project_cosmeticapp.model.PaypalCaptureRequest request = new com.example.fe_project_cosmeticapp.model.PaypalCaptureRequest(Integer.parseInt(orderNumber), paypalToken, payerId);
                        paypalApi.capturePaypalPayment(
                                bearerToken,
                                request
                        ).enqueue(new retrofit2.Callback<PaypalCaptureResponse>() {
                            @Override
                            public void onResponse(retrofit2.Call<PaypalCaptureResponse> call, retrofit2.Response<PaypalCaptureResponse> response) {
                                android.util.Log.d("PayPalCapture", "HTTP code: " + response.code());
                                if (response.body() != null) {
                                    android.util.Log.d("PayPalCapture", "Response body: " + response.body().toString());
                                } else {
                                    android.util.Log.d("PayPalCapture", "Response body is null");
                                }
                                if (response.isSuccessful() && response.body() != null) {
                                    PaypalCaptureResponse captureResponse = response.body();
                                    String captureStatus = captureResponse.getStatus();
                                    android.util.Log.d("PayPalCapture", "Capture status: " + captureStatus);
                                    if ("Success".equalsIgnoreCase(captureStatus) || "COMPLETED".equalsIgnoreCase(captureStatus)) {
                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("payment_status", "Success");
                                        setResult(RESULT_OK, resultIntent);
                                        Toast.makeText(WebViewActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                                        // Sau khi xác thực thanh toán thành công, chỉ thanh toán các sản phẩm đã chọn
                                        ArrayList<String> selectedProductIds = getIntent().getStringArrayListExtra("selectedProductIds");
                                        if (selectedProductIds != null && !selectedProductIds.isEmpty()) {
                                            String productIdsParam = android.text.TextUtils.join(",", selectedProductIds);
                                            com.example.fe_project_cosmeticapp.api.CheckoutApi checkoutApi = com.example.fe_project_cosmeticapp.api.RetrofitClient.getCheckoutApi();
                                            String token = sessionManager.getToken();
                                            if (token != null && !token.startsWith("Bearer ")) {
                                                token = "Bearer " + token;
                                            }
                                            checkoutApi.checkoutOrder(token, Integer.parseInt(orderId), productIdsParam)
                                                    .enqueue(new retrofit2.Callback<com.example.fe_project_cosmeticapp.model.CheckoutResponse>() {
                                                        @Override
                                                        public void onResponse(Call<com.example.fe_project_cosmeticapp.model.CheckoutResponse> call, Response<com.example.fe_project_cosmeticapp.model.CheckoutResponse> response) {
                                                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                                                Toast.makeText(WebViewActivity.this, "Thanh toán thành công cho sản phẩm đã chọn!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(WebViewActivity.this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                        @Override
                                                        public void onFailure(Call<com.example.fe_project_cosmeticapp.model.CheckoutResponse> call, Throwable t) {
                                                            Toast.makeText(WebViewActivity.this, "Lỗi kết nối khi thanh toán!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                        finish();
                                    } else {
                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("payment_status", captureStatus != null ? captureStatus : "Failed");
                                        setResult(RESULT_CANCELED, resultIntent);
                                        Toast.makeText(WebViewActivity.this, "Thanh toán thất bại! Status: " + captureStatus, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(WebViewActivity.this, "Lỗi xác thực thanh toán!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(retrofit2.Call<PaypalCaptureResponse> call, Throwable t) {
                                android.util.Log.e("PayPalCapture", "onFailure: ", t);
                                Toast.makeText(WebViewActivity.this, "Lỗi kết nối xác thực thanh toán!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        android.util.Log.e("PayPalCapture", "Thiếu tham số: orderNumber=" + orderNumber + ", paypalToken=" + paypalToken + ", payerId=" + payerId + ", bearerToken=" + bearerToken);
                    }
                } else {
                    // Inject JavaScript để lấy nội dung trang và kiểm tra Status nếu không có trên URL
                    view.evaluateJavascript(
                            "(function() { return document.body.innerText; })();",
                            value -> {
                                if (value != null && value.length() > 10) {
                                    try {
                                        // Loại bỏ dấu " ở đầu/cuối nếu có
                                        String json = value;
                                        if (json.startsWith("\"") && json.endsWith("\"")) {
                                            json = json.substring(1, json.length() - 1);
                                        }
                                        // Unescape các ký tự đặc biệt
                                        json = json.replace("\\\"", "\"");
                                        json = json.replace("\\n", "");
                                        json = json.replace("\\r", "");
                                        // Parse JSON
                                        org.json.JSONObject obj = new org.json.JSONObject(json);
                                        String status = obj.optString("status", "");
                                        String orderNumber = obj.optString("orderNumber", "");
                                        String paypalToken = obj.optString("token", "");
                                        String payerId = obj.optString("PayerID", "");
                                        if ("Success".equalsIgnoreCase(status) && orderNumber != null && !orderNumber.isEmpty() && paypalToken != null && !paypalToken.isEmpty() && payerId != null && !payerId.isEmpty()) {
                                            com.example.fe_project_cosmeticapp.model.PaypalCaptureRequest request = new com.example.fe_project_cosmeticapp.model.PaypalCaptureRequest(Integer.parseInt(orderNumber), paypalToken, payerId);
                                            paypalApi.capturePaypalPayment("Bearer " + sessionManager.getToken(), request)
                                                    .enqueue(new retrofit2.Callback<PaypalCaptureResponse>() {
                                                        @Override
                                                        public void onResponse(retrofit2.Call<PaypalCaptureResponse> call, retrofit2.Response<PaypalCaptureResponse> response) {
                                                            android.util.Log.d("PayPalCapture", "HTTP code: " + response.code());
                                                            if (response.body() != null) {
                                                                android.util.Log.d("PayPalCapture", "Response body: " + response.body().toString());
                                                            } else {
                                                                android.util.Log.d("PayPalCapture", "Response body is null");
                                                            }
                                                            if (response.isSuccessful() && response.body() != null) {
                                                                PaypalCaptureResponse captureResponse = response.body();
                                                                String captureStatus = captureResponse.getStatus();
                                                                android.util.Log.d("PayPalCapture", "Capture status: " + captureStatus);
                                                                if ("Success".equalsIgnoreCase(captureStatus) || "COMPLETED".equalsIgnoreCase(captureStatus)) {
                                                                    Intent resultIntent = new Intent();
                                                                    resultIntent.putExtra("payment_status", "Success");
                                                                    setResult(RESULT_OK, resultIntent);
                                                                    Toast.makeText(WebViewActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                                                                    // Sau khi xác thực thanh toán thành công, chỉ thanh toán các sản phẩm đã chọn
                                                                    ArrayList<String> selectedProductIds = getIntent().getStringArrayListExtra("selectedProductIds");
                                                                    if (selectedProductIds != null && !selectedProductIds.isEmpty()) {
                                                                        String productIdsParam = android.text.TextUtils.join(",", selectedProductIds);
                                                                        com.example.fe_project_cosmeticapp.api.CheckoutApi checkoutApi = com.example.fe_project_cosmeticapp.api.RetrofitClient.getCheckoutApi();
                                                                        String token = sessionManager.getToken();
                                                                        if (token != null && !token.startsWith("Bearer ")) {
                                                                            token = "Bearer " + token;
                                                                        }
                                                                        checkoutApi.checkoutOrder(token, Integer.parseInt(orderId), productIdsParam)
                                                                                .enqueue(new retrofit2.Callback<com.example.fe_project_cosmeticapp.model.CheckoutResponse>() {
                                                                                    @Override
                                                                                    public void onResponse(Call<com.example.fe_project_cosmeticapp.model.CheckoutResponse> call, Response<com.example.fe_project_cosmeticapp.model.CheckoutResponse> response) {
                                                                                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                                                                            Toast.makeText(WebViewActivity.this, "Thanh toán thành công cho sản phẩm đã chọn!", Toast.LENGTH_SHORT).show();
                                                                                        } else {
                                                                                            Toast.makeText(WebViewActivity.this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                    @Override
                                                                                    public void onFailure(Call<com.example.fe_project_cosmeticapp.model.CheckoutResponse> call, Throwable t) {
                                                                                        Toast.makeText(WebViewActivity.this, "Lỗi kết nối khi thanh toán!", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                    }
                                                                    finish();
                                                                } else {
                                                                    Intent resultIntent = new Intent();
                                                                    resultIntent.putExtra("payment_status", captureStatus != null ? captureStatus : "Failed");
                                                                    setResult(RESULT_CANCELED, resultIntent);
                                                                    Toast.makeText(WebViewActivity.this, "Thanh toán thất bại! Status: " + captureStatus, Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                }
                                                            } else {
                                                                Toast.makeText(WebViewActivity.this, "Lỗi xác thực thanh toán!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(retrofit2.Call<PaypalCaptureResponse> call, Throwable t) {
                                                            Toast.makeText(WebViewActivity.this, "Lỗi kết nối xác thực thanh toán!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    } catch (Exception e) {
                                        // Không parse được JSON, bỏ qua
                                    }
                                }
                            }
                    );
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // BỎ QUA lỗi SSL chỉ dùng cho DEV/SANDBOX
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String url = uri.toString();

                if (url.contains("status=") && url.contains("orderNumber=") && url.contains("token=")) {
                    String status = uri.getQueryParameter("status");
                    String orderNumber = uri.getQueryParameter("orderNumber");
                    String paypalToken = uri.getQueryParameter("token");
                    String payerId = uri.getQueryParameter("PayerID");
                    String bearerToken = sessionManager.getToken();
                    if (bearerToken != null && !bearerToken.startsWith("Bearer ")) {
                        bearerToken = "Bearer " + bearerToken;
                    }
                    if ("Success".equalsIgnoreCase(status) && orderNumber != null && paypalToken != null && payerId != null && bearerToken != null) {
                        com.example.fe_project_cosmeticapp.model.PaypalCaptureRequest captureRequest =
                                new com.example.fe_project_cosmeticapp.model.PaypalCaptureRequest(
                                        Integer.parseInt(orderNumber), paypalToken, payerId
                                );
                        paypalApi.capturePaypalPayment(
                                bearerToken,
                                captureRequest
                        ).enqueue(new Callback<PaypalCaptureResponse>() {
                            @Override
                            public void onResponse(Call<PaypalCaptureResponse> call, Response<PaypalCaptureResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    if ("Success".equalsIgnoreCase(response.body().getStatus())) {
                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("payment_status", "Success");
                                        setResult(RESULT_OK, resultIntent);
                                        Toast.makeText(WebViewActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                                        // Sau khi xác thực thanh toán thành công, chỉ thanh toán các sản phẩm đã chọn
                                        ArrayList<String> selectedProductIds = getIntent().getStringArrayListExtra("selectedProductIds");
                                        if (selectedProductIds != null && !selectedProductIds.isEmpty()) {
                                            String productIdsParam = android.text.TextUtils.join(",", selectedProductIds);
                                            com.example.fe_project_cosmeticapp.api.CheckoutApi checkoutApi = com.example.fe_project_cosmeticapp.api.RetrofitClient.getCheckoutApi();
                                            String token = sessionManager.getToken();
                                            if (token != null && !token.startsWith("Bearer ")) {
                                                token = "Bearer " + token;
                                            }
                                            checkoutApi.checkoutOrder(token, Integer.parseInt(orderId), productIdsParam)
                                                    .enqueue(new Callback<com.example.fe_project_cosmeticapp.model.CheckoutResponse>() {
                                                        @Override
                                                        public void onResponse(Call<com.example.fe_project_cosmeticapp.model.CheckoutResponse> call, Response<com.example.fe_project_cosmeticapp.model.CheckoutResponse> response) {
                                                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                                                Toast.makeText(WebViewActivity.this, "Thanh toán thành công cho sản phẩm đã chọn!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(WebViewActivity.this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<com.example.fe_project_cosmeticapp.model.CheckoutResponse> call, Throwable t) {
                                                            Toast.makeText(WebViewActivity.this, "Lỗi kết nối khi thanh toán!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                        finish();
                                    } else {
                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("payment_status", "Failed");
                                        setResult(RESULT_CANCELED, resultIntent);
                                        Toast.makeText(WebViewActivity.this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(WebViewActivity.this, "Lỗi xác thực thanh toán!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<PaypalCaptureResponse> call, Throwable t) {
                                Toast.makeText(WebViewActivity.this, "Lỗi kết nối xác thực thanh toán!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return true; // chặn load tiếp
                    }
                }

                return false;
            }

        });
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        // Khi nhấn nút "Quay lại ứng dụng" chỉ đóng WebView, KHÔNG gọi cancel-payment
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
