package com.example.fe_project_cosmeticapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_project_cosmeticapp.adapter.ProductAdapter;
import com.example.fe_project_cosmeticapp.api.RetrofitClient;
import com.example.fe_project_cosmeticapp.model.Product;
import com.example.fe_project_cosmeticapp.model.ProductResponse;
import com.example.fe_project_cosmeticapp.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class productView extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ProgressBar progressBar;
    private View errorView;
    private int currentPage = 1; // Bắt đầu từ trang 1 thay vì 0
    private int pageSize = 10;
    private String currentCategory = "";
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        // Inflate product layout vào content_frame
        LayoutInflater inflater = LayoutInflater.from(this);
        ViewGroup contentFrame = findViewById(R.id.content_frame);
        View productContent = inflater.inflate(R.layout.activity_product, contentFrame, true);

        // Khởi tạo các thành phần của màn hình sản phẩm
        initializeProductComponents();

        // Load dữ liệu sản phẩm từ API
        loadProducts(true);
    }

    private void initializeProductComponents() {
        // Khởi tạo RecyclerView
        productRecyclerView = findViewById(R.id.product_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        productRecyclerView.setLayoutManager(layoutManager);

        // Thêm ProgressBar ở cuối danh sách (nếu không có sẵn trong layout)
        progressBar = findViewById(R.id.progress_bar);
        if (progressBar == null) {
            // Nếu không tìm thấy progress_bar trong layout, bạn có thể tạo mới hoặc bỏ qua dòng này
            // và thêm ProgressBar vào layout của bạn
        }

        // Khởi tạo adapter
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this);
        productRecyclerView.setAdapter(productAdapter);

        // Thiết lập scroll listener cho tính năng cuộn vô hạn
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Chỉ tải thêm nếu chưa phải trang cuối và không đang tải
                if (!isLastPage && !isLoading) {
                    loadMoreProducts();
                }
            }
        };
        productRecyclerView.addOnScrollListener(scrollListener);

        // Thiết lập các sự kiện cho nút Filter
        View filterButton = findViewById(R.id.filter_button);
        if (filterButton != null) {
            filterButton.setOnClickListener(v -> {
                // Mở dialog filter
                showFilterDialog();
            });
        }
    }

    private void showFilterDialog() {
        // Implement filter dialog later
        Toast.makeText(this, "Tính năng lọc sẽ được triển khai sau", Toast.LENGTH_SHORT).show();
    }

    private void loadProducts(boolean isFirstLoad) {
        // Đánh dấu đang tải
        isLoading = true;

        // Hiển thị loading indicator
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // Reset trang về 1 và xóa danh sách cũ nếu đây là lần tải đầu tiên
        if (isFirstLoad) {
            currentPage = 1;
            if (productAdapter != null) {
                productAdapter.clearProducts();
            }
            // Reset trạng thái của scroll listener
            if (scrollListener != null) {
                scrollListener.resetState();
            }
        }

        // Gọi API để lấy danh sách sản phẩm
        Call<ProductResponse> call = RetrofitClient.getProductApi().getProducts(pageSize, currentPage, currentCategory);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                // Ẩn loading indicator
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                // Đánh dấu không còn đang tải
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    List<Product> newProducts = productResponse.getItems();

                    // Kiểm tra xem có phải trang cuối không
                    isLastPage = !productResponse.isHasNextPage();

                    if (newProducts != null && !newProducts.isEmpty()) {
                        // Thêm sản phẩm vào danh sách hiện tại thay vì thay thế
                        if (isFirstLoad) {
                            productAdapter.setProducts(newProducts);
                        } else {
                            productAdapter.addProducts(newProducts);
                        }

                        // Tăng số trang cho lần tải tiếp theo
                        currentPage++;

                        // Log số lượng sản phẩm đã nhận
                        Toast.makeText(productView.this, "Đã tải " + newProducts.size() + " sản phẩm", Toast.LENGTH_SHORT).show();
                    } else {
                        // Không có sản phẩm mới, đánh dấu là trang cuối
                        isLastPage = true;
                        if (isFirstLoad) {
                            Toast.makeText(productView.this, "Không tìm thấy sản phẩm nào", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(productView.this, "Đã tải hết sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Xử lý lỗi response
                    Toast.makeText(productView.this, "Lỗi khi tải sản phẩm: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                // Ẩn loading indicator
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                // Đánh dấu không còn đang tải
                isLoading = false;

                // Hiển thị thông báo lỗi
                Toast.makeText(productView.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức để tải thêm sản phẩm (gọi khi cuộn đến cuối danh sách)
    private void loadMoreProducts() {
        // Gọi loadProducts với tham số false để thêm vào danh sách hiện có
        loadProducts(false);
    }

    // Phương thức để lọc sản phẩm theo danh mục
    public void filterByCategory(String category) {
        // Đặt lại danh mục hiện tại
        currentCategory = category;

        // Tải lại sản phẩm từ đầu với danh mục mới
        loadProducts(true);
    }
}