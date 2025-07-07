package com.example.fe_project_cosmeticapp.base;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fe_project_cosmeticapp.R;
import com.example.fe_project_cosmeticapp.navigation.NavigationHandler;
import com.example.fe_project_cosmeticapp.utils.HeaderManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * BaseActivity để cung cấp bottom navigation cho tất cả các activity
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;
    protected HeaderManager headerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        // Lấy content frame để add layout của activity con
        FrameLayout contentFrame = findViewById(R.id.content_frame);

        // Inflate layout của activity con vào content frame
        View childView = getLayoutInflater().inflate(getLayoutResourceId(), contentFrame, false);
        contentFrame.addView(childView);

        // Khởi tạo HeaderManager
        headerManager = new HeaderManager(this);

        // Cài đặt chế độ hiển thị cho header (menu hoặc back)
        setupHeaderMode();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    /**
     * Setup chế độ hiển thị cho header
     */
    private void setupHeaderMode() {
        // Mặc định sẽ hiển thị nút menu
        if (shouldShowBackButton()) {
            headerManager.showBackButton();
        } else {
            headerManager.showMenuButton();

            // Thiết lập sự kiện click cho nút menu (nếu cần)
            ImageButton menuButton = findViewById(R.id.header_menu);
            if (menuButton != null) {
                menuButton.setOnClickListener(v -> onMenuButtonClicked());
            }
        }
    }

    /**
     * Được gọi khi nút menu được nhấn
     * Các activity con có thể override phương thức này để xử lý sự kiện click
     */
    protected void onMenuButtonClicked() {
        // Xử lý mặc định khi click vào nút menu
        // Activity con có thể override phương thức này
    }

    /**
     * Setup bottom navigation và listener
     */
    private void setupBottomNavigation() {
        // Tìm bottom navigation view
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Thiết lập lắng nghe sự kiện cho bottom navigation
        NavigationHandler.setupNavigation(bottomNavigationView, this);

        // Chọn item theo activity hiện tại
        selectNavigationItem();
    }

    /**
     * Chọn item trong bottom navigation tương ứng với activity hiện tại
     */
    protected void selectNavigationItem() {
        int selectedItemId = getSelectedNavigationItemId();
        if (selectedItemId != -1) {
            bottomNavigationView.setSelectedItemId(selectedItemId);
        }
    }

    /**
     * Trả về true nếu activity này nên hiển thị nút back thay vì nút menu
     * Các activity con cần override phương thức này để xác định loại nút hiển thị
     * @return true nếu hiển thị nút back, false nếu hiển thị nút menu
     */
    protected boolean shouldShowBackButton() {
        // Mặc định là hiển thị nút menu
        return false;
    }

    /**
     * Trả về layout resource ID của activity con
     */
    protected abstract int getLayoutResourceId();

    /**
     * Trả về ID của item được chọn trong bottom navigation
     */
    protected abstract int getSelectedNavigationItemId();
}
