package com.example.fe_project_cosmeticapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fe_project_cosmeticapp.R;
import com.example.fe_project_cosmeticapp.productView;

/**
 * Class tiện ích để quản lý hiển thị nút menu/back trên header và chức năng tìm kiếm
 */
public class HeaderManager {

    private final ImageButton menuButton;
    private final ImageButton backButton;
    private final Activity activity;

    // Các thành phần tìm kiếm
    private final ImageButton searchButton;
    private final LinearLayout mainHeaderRow;
    private final LinearLayout searchBar;
    private final ImageButton searchBackButton;
    private final EditText searchEditText;
    private final ImageButton searchClearButton;

    /**
     * Khởi tạo HeaderManager
     * @param activity Activity hiện tại
     */
    public HeaderManager(Activity activity) {
        this.activity = activity;
        this.menuButton = activity.findViewById(R.id.header_menu);
        this.backButton = activity.findViewById(R.id.header_back);

        // Khởi tạo các thành phần tìm kiếm
        this.searchButton = activity.findViewById(R.id.header_search);
        this.mainHeaderRow = activity.findViewById(R.id.header_main_row);
        this.searchBar = activity.findViewById(R.id.header_search_bar);
        this.searchBackButton = activity.findViewById(R.id.search_back);
        this.searchEditText = activity.findViewById(R.id.search_edit_text);
        this.searchClearButton = activity.findViewById(R.id.search_clear);

        // Thiết lập sự kiện cho các thành phần tìm kiếm
        setupSearchEvents();
    }

    /**
     * Hiển thị nút back thay cho nút menu
     */
    public void showBackButton() {
        if (menuButton != null && backButton != null) {
            menuButton.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);

            // Thiết lập sự kiện click cho nút back
            backButton.setOnClickListener(v -> activity.onBackPressed());
        }
    }

    /**
     * Hiển thị nút menu (mặc định)
     */
    public void showMenuButton() {
        if (menuButton != null && backButton != null) {
            menuButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.GONE);
        }
    }

    /**
     * Thiết lập các sự kiện cho chức năng tìm kiếm
     */
    private void setupSearchEvents() {
        // Nút tìm kiếm - hiển thị thanh tìm kiếm
        if (searchButton != null) {
            searchButton.setOnClickListener(v -> showSearchBar());
        }

        // Nút quay lại - ẩn thanh tìm kiếm
        if (searchBackButton != null) {
            searchBackButton.setOnClickListener(v -> hideSearchBar());
        }

        // Nút xóa - xóa nội dung tìm kiếm
        if (searchClearButton != null) {
            searchClearButton.setOnClickListener(v -> {
                if (searchEditText != null) {
                    searchEditText.setText("");
                }
            });
        }

        // Xử lý sự kiện khi nhấn Enter trên bàn phím
        if (searchEditText != null) {
            searchEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    performSearch();
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Hiển thị thanh tìm kiếm
     */
    private void showSearchBar() {
        if (mainHeaderRow != null && searchBar != null) {
            mainHeaderRow.setVisibility(View.GONE);
            searchBar.setVisibility(View.VISIBLE);

            // Focus vào ô nhập liệu
            if (searchEditText != null) {
                searchEditText.requestFocus();
                // Hiển thị bàn phím
                KeyboardUtils.showKeyboard(activity, searchEditText);
            }
        }
    }

    /**
     * Ẩn thanh tìm kiếm
     */
    private void hideSearchBar() {
        if (mainHeaderRow != null && searchBar != null) {
            searchBar.setVisibility(View.GONE);
            mainHeaderRow.setVisibility(View.VISIBLE);

            // Ẩn bàn phím
            if (searchEditText != null) {
                KeyboardUtils.hideKeyboard(activity, searchEditText);
            }
        }
    }

    /**
     * Thực hiện tìm kiếm và chuyển đến trang kết quả
     */
    private void performSearch() {
        if (searchEditText != null) {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                // Chuyển đến trang productView với từ khóa tìm kiếm
                Intent intent = new Intent(activity, productView.class);
                intent.putExtra("searchQuery", query);
                activity.startActivity(intent);

                // Ẩn thanh tìm kiếm sau khi tìm kiếm
                hideSearchBar();
            }
        }
    }
}
