package com.example.fe_project_cosmeticapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_project_cosmeticapp.adapter.ProductAdapter;
import com.example.fe_project_cosmeticapp.api.RetrofitClient;
import com.example.fe_project_cosmeticapp.base.BaseActivity;
import com.example.fe_project_cosmeticapp.model.Product;
import com.example.fe_project_cosmeticapp.model.ProductResponse;
import com.example.fe_project_cosmeticapp.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class productView extends BaseActivity {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ProgressBar progressBar;
    private View errorView;
    private TextView categoryTitleTextView;
    private int currentPage = 1; // Bắt đầu từ trang 1 thay vì 0
    private int pageSize = 10;
    private String currentCategory = "";
    private String currentSkinType = ""; // Thêm biến lưu loại da hiện tại
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private EndlessRecyclerViewScrollListener scrollListener;

    // Thêm mảng chứa các loại da
    private final String[] SKIN_TYPES = {"normal skin", "dry skin", "mixed skin", "oily skin"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy category từ intent nếu có
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("category")) {
            currentCategory = intent.getStringExtra("category");
        }

        // Khởi tạo các thành phần của màn hình sản phẩm
        initializeProductComponents();

        // Hiển thị tiêu đề danh mục nếu có
        if (categoryTitleTextView != null && currentCategory != null && !currentCategory.isEmpty()) {
            categoryTitleTextView.setText(currentCategory.toUpperCase());
            categoryTitleTextView.setVisibility(View.VISIBLE);
        }

        // Load dữ liệu sản phẩm từ API
        loadProducts(true);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_product;
    }

    @Override
    protected int getSelectedNavigationItemId() {
        // Không trả về R.id.nav_category để tránh kích hoạt lại dialog
        return -1; // Hoặc một ID khác không tương ứng với nút category
    }

    @Override
    protected boolean shouldShowBackButton() {
        // Hiển thị nút Back thay vì nút Menu
        return true;
    }

    private void initializeProductComponents() {
        // Khởi tạo RecyclerView
        productRecyclerView = findViewById(R.id.product_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        productRecyclerView.setLayoutManager(layoutManager);

        // Thêm ProgressBar ở cuối danh sách
        progressBar = findViewById(R.id.progress_bar);

        // Tìm TextView để hiển thị tiêu đề danh mục
        categoryTitleTextView = findViewById(R.id.category_title);

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

        // Thiết lập thanh tìm kiếm trên header
        setupSearchBar();
    }

    // Phương thức để lọc sản phẩm theo loại da
    public void filterBySkinType(String skinType) {
        // Đặt lại loại da hiện tại
        currentSkinType = skinType;

        // Cập nhật tiêu đề hiển thị
        updateDisplayTitle();

        // Tải lại sản phẩm từ đầu với loại da mới (giữ nguyên category nếu có)
        loadProducts(true);
    }

    // Phương thức để cập nhật tiêu đề hiển thị dựa trên các bộ lọc hiện tại
    private void updateDisplayTitle() {
        if (categoryTitleTextView != null) {
            if (currentCategory != null && !currentCategory.isEmpty() &&
                currentSkinType != null && !currentSkinType.isEmpty()) {
                // Nếu cả hai bộ lọc đều được áp dụng
                categoryTitleTextView.setText(currentCategory.toUpperCase() + " - " + currentSkinType.toUpperCase());
            } else if (currentSkinType != null && !currentSkinType.isEmpty()) {
                // Chỉ có skin type
                categoryTitleTextView.setText("FOR " + currentSkinType.toUpperCase());
            } else if (currentCategory != null && !currentCategory.isEmpty()) {
                // Chỉ có category
                categoryTitleTextView.setText(currentCategory.toUpperCase());
            } else {
                // Không có bộ lọc
                categoryTitleTextView.setText("TẤT CẢ SẢN PHẨM");
            }
            categoryTitleTextView.setVisibility(View.VISIBLE);
        }
    }

    private void showFilterDialog() {
        // Tạo dialog để chọn loại da
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn loại da");

        // Đặt danh sách các loại da vào dialog
        builder.setItems(SKIN_TYPES, (dialog, which) -> {
            String selectedSkinType = SKIN_TYPES[which];
            filterBySkinType(selectedSkinType);
        });

        // Thêm tùy chọn "Tất cả sản phẩm" để xóa bộ lọc
        builder.setNeutralButton("Tất cả sản phẩm", (dialog, which) -> {
            // Xóa tất cả bộ lọc
            currentSkinType = "";
            currentCategory = "";

            if (categoryTitleTextView != null) {
                categoryTitleTextView.setText("TẤT CẢ SẢN PHẨM");
                categoryTitleTextView.setVisibility(View.VISIBLE);
            }

            loadProducts(true);
        });

        // Hiển thị dialog
        builder.show();
    }

    // Thiết lập thanh tìm kiếm trên header thay vì dialog
    private void setupSearchBar() {
        // Tìm các view trong header
        LinearLayout mainHeaderRow = findViewById(R.id.header_main_row);
        LinearLayout searchBarLayout = findViewById(R.id.header_search_bar);
        ImageButton searchButton = findViewById(R.id.header_search);
        ImageButton searchBackButton = findViewById(R.id.search_back);
        ImageButton searchClearButton = findViewById(R.id.search_clear);
        EditText searchEditText = findViewById(R.id.search_edit_text);

        // Xử lý sự kiện khi nhấn nút tìm kiếm trong header
        if (searchButton != null) {
            searchButton.setOnClickListener(v -> {
                // Hiển thị thanh tìm kiếm, ẩn header chính
                if (mainHeaderRow != null) mainHeaderRow.setVisibility(View.GONE);
                if (searchBarLayout != null) searchBarLayout.setVisibility(View.VISIBLE);

                // Focus vào ô nhập liệu và hiển thị bàn phím
                if (searchEditText != null) {
                    searchEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            });
        }

        // Xử lý sự kiện khi nhấn nút quay lại trong thanh tìm kiếm
        if (searchBackButton != null) {
            searchBackButton.setOnClickListener(v -> {
                // Ẩn thanh tìm kiếm, hiển thị header chính
                if (mainHeaderRow != null) mainHeaderRow.setVisibility(View.VISIBLE);
                if (searchBarLayout != null) searchBarLayout.setVisibility(View.GONE);

                // Ẩn bàn phím
                if (searchEditText != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                    }
                    searchEditText.setText("");
                }
            });
        }

        // Xử lý sự kiện khi nhấn nút xóa trong thanh tìm kiếm
        if (searchClearButton != null) {
            searchClearButton.setOnClickListener(v -> {
                if (searchEditText != null) {
                    searchEditText.setText("");
                    searchEditText.requestFocus();
                }
            });
        }

        // Xử lý sự kiện khi nhấn nút Enter/Search trên bàn phím
        if (searchEditText != null) {
            searchEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String searchQuery = searchEditText.getText().toString().trim();
                    if (!searchQuery.isEmpty()) {
                        // Thực hiện tìm kiếm với từ khóa nhập vào
                        searchProducts(searchQuery);

                        // Ẩn bàn phím sau khi tìm kiếm
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                        }

                        // Ẩn thanh tìm kiếm, hiển thị header chính
                        if (mainHeaderRow != null) mainHeaderRow.setVisibility(View.VISIBLE);
                        if (searchBarLayout != null) searchBarLayout.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(productView.this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            });
        }
    }

    private void searchProducts(String query) {
        // Đánh dấu đang tải
        isLoading = true;

        // Hiển thị loading indicator
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // Reset trang về 1 khi thực hiện tìm kiếm mới
        currentPage = 1;

        // Reset các biến khác
        currentCategory = "";
        currentSkinType = "";

        // Cập nhật tiêu đề thành từ khóa tìm kiếm
        if (categoryTitleTextView != null) {
            categoryTitleTextView.setText("TÌM KIẾM: " + query.toUpperCase());
            categoryTitleTextView.setVisibility(View.VISIBLE);
        }

        // Gọi API để tìm kiếm sản phẩm với tham số searchTerm đúng
        Call<ProductResponse> call = RetrofitClient.getProductApi().searchProducts(query, pageSize, currentPage);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                // Ẩn loading indicator
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                // Đánh dấu không còn đang tải
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    List<Product> searchResults = productResponse.getItems();

                    // Kiểm tra xem có phải trang cuối không
                    isLastPage = !productResponse.isHasNextPage();

                    if (searchResults != null && !searchResults.isEmpty()) {
                        // Xóa danh sách sản phẩm cũ và hiển thị kết quả tìm kiếm mới
                        productAdapter.setProducts(searchResults);

                        // Chỉ tăng số trang nếu còn trang tiếp theo
                        if (!isLastPage) {
                            currentPage++;
                        }

                        // Hiển thị thông báo số kết quả tìm thấy
                        int totalCount = productResponse.getTotalCount();
                        Toast.makeText(productView.this, "Tìm thấy " + totalCount + " sản phẩm phù hợp từ khóa \"" + query + "\"", Toast.LENGTH_SHORT).show();
                    } else {
                        // Không có sản phẩm nào, đánh dấu là trang cuối
                        isLastPage = true;
                        productAdapter.clearProducts(); // Xóa các sản phẩm cũ
                        Toast.makeText(productView.this, "Không tìm thấy sản phẩm nào phù hợp từ khóa \"" + query + "\"", Toast.LENGTH_SHORT).show();
                    }

                    // Reset scroll listener để có thể cuộn lại từ đầu
                    if (scrollListener != null) {
                        scrollListener.resetState();
                    }
                } else {
                    // Xử lý lỗi response
                    Toast.makeText(productView.this, "Lỗi khi tìm kiếm sản phẩm: " + response.message(), Toast.LENGTH_SHORT).show();
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

        // Gọi API để lấy danh sách sản phẩm dựa trên bộ lọc
        Call<ProductResponse> call;

        if (currentSkinType != null && !currentSkinType.isEmpty() &&
            currentCategory != null && !currentCategory.isEmpty()) {
            // Nếu cả category và skin type đều được chọn, gọi API với cả hai tham số
            call = RetrofitClient.getProductApi().getProductsByCategoryAndSkinType(
                currentCategory, currentSkinType, pageSize, currentPage);
        } else if (currentSkinType != null && !currentSkinType.isEmpty()) {
            // Nếu chỉ có skin type được chọn
            call = RetrofitClient.getProductApi().getProductsBySkinType(
                currentSkinType, pageSize, currentPage);
        } else if (currentCategory != null && !currentCategory.isEmpty()) {
            // Nếu chỉ có category được chọn
            call = RetrofitClient.getProductApi().getProductsByCategory(
                currentCategory, pageSize, currentPage);
        } else {
            // Không có bộ lọc nào, lấy tất cả sản phẩm
            call = RetrofitClient.getProductApi().getProducts(pageSize, currentPage, "");
        }

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

        // Cập nhật tiêu đề danh mục
        if (categoryTitleTextView != null) {
            categoryTitleTextView.setText(category.toUpperCase());
            categoryTitleTextView.setVisibility(View.VISIBLE);
        }

        // Tải lại sản phẩm từ đầu với danh mục mới
        loadProducts(true);
    }
}
