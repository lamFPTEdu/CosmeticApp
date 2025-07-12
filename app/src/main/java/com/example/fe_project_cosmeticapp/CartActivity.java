package com.example.fe_project_cosmeticapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_project_cosmeticapp.adapter.CartAdapter;
import com.example.fe_project_cosmeticapp.api.CartApi;
import com.example.fe_project_cosmeticapp.api.RetrofitClient;
import com.example.fe_project_cosmeticapp.base.BaseActivity;
import com.example.fe_project_cosmeticapp.model.CartItem;
import com.example.fe_project_cosmeticapp.model.CartResponse;
import com.example.fe_project_cosmeticapp.model.CartUpdateRequest;
import com.example.fe_project_cosmeticapp.model.MessageResponse;
import com.example.fe_project_cosmeticapp.utils.SessionManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends BaseActivity implements CartAdapter.CartItemListener {

    private RecyclerView rvCartItems;
    private TextView tvEmptyCart, tvTotalPrice;
    private ProgressBar progressBar;
    private Button btnCheckout;

    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private SessionManager sessionManager;
    private CartApi cartApi;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_cart;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo views
        rvCartItems = findViewById(R.id.rvCartItems);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        progressBar = findViewById(R.id.progressBar);
        btnCheckout = findViewById(R.id.btnCheckout);

        // Khởi tạo SessionManager và CartApi
        sessionManager = new SessionManager(this);
        cartApi = RetrofitClient.getCartApi();

        // Khởi tạo RecyclerView
        cartAdapter = new CartAdapter(this, cartItems, this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(cartAdapter);

        // Lắng nghe thay đổi chọn select để cập nhật tổng tiền ngay khi chọn/bỏ chọn
        cartAdapter.setOnSelectionChangedListener(this::updateSelectedTotalPrice);

        // Kiểm tra trạng thái đăng nhập và tải giỏ hàng nếu đã đăng nhập
        if (sessionManager.isLoggedIn()) {
            loadCartItems();
        } else {
            showEmptyCart();
        }

        // Thiết lập sự kiện cho nút thanh toán
        btnCheckout.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                // TODO: Chuyển đến trang thanh toán
                Toast.makeText(CartActivity.this, "Chức năng thanh toán đang được phát triển", Toast.LENGTH_SHORT).show();
            } else {
                // Chuyển đến trang đăng nhập nếu chưa đăng nhập
                Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Kiểm tra lại trạng thái đăng nhập khi Activity được resume
        if (sessionManager.isLoggedIn()) {
            loadCartItems();
        } else {
            showEmptyCart();
        }
    }

    private void loadCartItems() {
        showLoading();

        String token = "Bearer " + sessionManager.getToken();
        Call<CartResponse> call = cartApi.getCart(token);

        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    CartResponse cartResponse = response.body();
                    updateCartUI(cartResponse);
                } else {
                    showError("Không thể tải giỏ hàng");
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                hideLoading();
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void updateCartUI(CartResponse cartResponse) {
        cartItems.clear();
        List<CartItem> items = cartResponse.getItems();
        if (items != null && !items.isEmpty()) {
            cartItems.addAll(items);
            cartAdapter.notifyDataSetChanged();
            showCartItems();
            // Khi load lại giỏ hàng, reset tất cả select về false và tổng tiền về 0
            for (CartItem item : cartItems) {
                item.setSelected(false);
            }
            updateSelectedTotalPrice();
        } else {
            showEmptyCart();
        }
    }

    private void updateTotalPrice(double totalPrice) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(currencyFormatter.format(totalPrice));
    }

    // Hàm cập nhật tổng tiền dựa trên các sản phẩm được chọn
    private void updateSelectedTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                total += item.getSubtotal();
            }
        }
        updateTotalPrice(total);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvCartItems.setVisibility(View.GONE);
        tvEmptyCart.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showCartItems() {
        rvCartItems.setVisibility(View.VISIBLE);
        tvEmptyCart.setVisibility(View.GONE);
    }

    private void showEmptyCart() {
        rvCartItems.setVisibility(View.GONE);
        tvEmptyCart.setVisibility(View.VISIBLE);
        updateTotalPrice(0);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Các phương thức từ CartItemListener interface
    @Override
    public void onUpdateQuantity(String productId, int newQuantity) {
        if (sessionManager.isLoggedIn()) {
            String token = "Bearer " + sessionManager.getToken();
            Call<MessageResponse> call = cartApi.updateCartItem(token, new CartUpdateRequest(productId, newQuantity));

            call.enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Tải lại giỏ hàng sau khi cập nhật
                        loadCartItems();
                    } else {
                        showError("Không thể cập nhật số lượng");
                    }
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    showError("Lỗi kết nối: " + t.getMessage());
                }
            });
        }
    }

    @Override
    public void onRemoveItem(String productId) {
        if (sessionManager.isLoggedIn()) {
            String token = "Bearer " + sessionManager.getToken();
            Call<MessageResponse> call = cartApi.removeFromCart(token, productId);

            call.enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Tải lại giỏ hàng sau khi xóa
                        loadCartItems();
                        Toast.makeText(CartActivity.this, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                    } else {
                        showError("Không thể xóa sản phẩm");
                    }
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    showError("Lỗi kết nối: " + t.getMessage());
                }
            });
        }
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected int getSelectedNavigationItemId() {
        return R.id.nav_cart;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_clear_cart) {
            clearCart();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearCart() {
        if (sessionManager.isLoggedIn()) {
            String token = "Bearer " + sessionManager.getToken();
            Call<MessageResponse> call = cartApi.clearCart(token);

            call.enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        cartItems.clear();
                        cartAdapter.notifyDataSetChanged();
                        showEmptyCart();
                        Toast.makeText(CartActivity.this, "Giỏ hàng đã được xóa", Toast.LENGTH_SHORT).show();
                    } else {
                        showError("Không thể xóa giỏ hàng");
                    }
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    showError("Lỗi kết nối: " + t.getMessage());
                }
            });
        }
    }
}
