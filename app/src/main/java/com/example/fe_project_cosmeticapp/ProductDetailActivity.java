package com.example.fe_project_cosmeticapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.fe_project_cosmeticapp.api.RetrofitClient;
import com.example.fe_project_cosmeticapp.base.BaseActivity;
import com.example.fe_project_cosmeticapp.model.Product;
import com.example.fe_project_cosmeticapp.utils.SessionManager;

import android.view.LayoutInflater;
import android.widget.NumberPicker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends BaseActivity {

    private ImageView productImage;
    private TextView productTitle, productSubtitle, productSkinType;
    private TextView productPrice;
    private Button btnAddToCart;
    private SessionManager sessionManager;

    private int productId;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Khởi tạo các view
        initViews();

        // Set up bottom navigation
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            com.example.fe_project_cosmeticapp.navigation.NavigationHandler.setupNavigation(bottomNavigationView, this);
        }

        // Lấy ID sản phẩm từ intent
        productId = getIntent().getIntExtra("product_id", -1);
        if (productId != -1) {
            // Tải thông tin chi tiết sản phẩm
            loadProductDetails(productId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.product_detailed;
    }

    @Override
    protected int getSelectedNavigationItemId() {
        // Có thể trả về ID của item tương ứng trong bottom navigation
        // hoặc -1 nếu không muốn highlight item nào
        return -1;
    }

    @Override
    protected boolean shouldShowBackButton() {
        // Hiển thị nút Back thay vì nút Menu
        return true;
    }

    private void initViews() {
        productImage = findViewById(R.id.product_image);
        productTitle = findViewById(R.id.product_title);
        productSubtitle = findViewById(R.id.product_subtitle);
        productSkinType = findViewById(R.id.product_skin_type);
        productPrice = findViewById(R.id.product_price);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
    }

    private void loadProductDetails(int productId) {
        Call<Product> call = RetrofitClient.getProductApi().getProductById(productId);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentProduct = response.body();
                    displayProductDetails(currentProduct);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không thể tải thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProductDetails(Product product) {
        // Hiển thị thông tin sản phẩm
        productTitle.setText(product.getName());
        productSubtitle.setText(product.getDescription());
        productSkinType.setText(product.getSkinType() != null ? product.getSkinType() : "all types of skin");

        // Định dạng giá tiền
        String formattedPrice = String.format("%,d₫", (int)product.getPrice());
        productPrice.setText(formattedPrice);


        // Tải hình ảnh sản phẩm
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this)
                .load(product.getImageUrl())
                .into(productImage);
        }

        // Thiết lập sự kiện click cho nút thêm vào giỏ hàng
        btnAddToCart.setOnClickListener(v -> {
            showQuantityDialog();
        });
    }

    private void showQuantityDialog() {
        // Sử dụng dialog tùy chỉnh để chọn số lượng
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.quantity_selector_dialog, null);
        builder.setView(view);

        final NumberPicker quantityPicker = view.findViewById(R.id.quantity_picker);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnConfirm = view.findViewById(R.id.btn_confirm);

        // Cấu hình NumberPicker
        quantityPicker.setMinValue(1);
        quantityPicker.setMaxValue(10);
        quantityPicker.setValue(1);

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            int selectedQuantity = quantityPicker.getValue();
            addToCart(selectedQuantity);
            dialog.dismiss();
        });
    }

    private void addToCart(int quantity) {
        // Kiểm tra người dùng đã đăng nhập chưa
        if (!sessionManager.isLoggedIn()) {
            // Nếu chưa đăng nhập, chuyển đến trang đăng nhập
            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        // Hiển thị thông báo đang xử lý
        Toast.makeText(this, "Đang thêm vào giỏ hàng...", Toast.LENGTH_SHORT).show();

        // Tạo request để thêm vào giỏ hàng
        com.example.fe_project_cosmeticapp.model.CartItemRequest cartItemRequest =
            new com.example.fe_project_cosmeticapp.model.CartItemRequest(
                String.valueOf(currentProduct.getId()),
                quantity
            );

        // Lấy token từ session
        String token = "Bearer " + sessionManager.getToken();

        // Gọi API để thêm vào giỏ hàng
        com.example.fe_project_cosmeticapp.api.RetrofitClient.getCartApi()
            .addToCart(token, cartItemRequest)
            .enqueue(new Callback<com.example.fe_project_cosmeticapp.model.MessageResponse>() {
                @Override
                public void onResponse(Call<com.example.fe_project_cosmeticapp.model.MessageResponse> call,
                                      Response<com.example.fe_project_cosmeticapp.model.MessageResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Thêm thành công
                        Toast.makeText(ProductDetailActivity.this,
                            "Đã thêm " + quantity + " " + currentProduct.getName() + " vào giỏ hàng",
                            Toast.LENGTH_SHORT).show();
                    } else {
                        // Xử lý lỗi
                        Toast.makeText(ProductDetailActivity.this,
                            "Không thể thêm vào giỏ hàng. Vui lòng thử lại sau.",
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<com.example.fe_project_cosmeticapp.model.MessageResponse> call, Throwable t) {
                    // Xử lý lỗi kết nối
                    Toast.makeText(ProductDetailActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    // Method to navigate to profile screen
    private void navigateToProfile() {
        if (sessionManager.isLoggedIn()) {
            // User is logged in, go to profile
            startActivity(new Intent(this, ProfileActivity.class));
        } else {
            // User is not logged in, go to login
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
