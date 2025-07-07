package com.example.fe_project_cosmeticapp.utils;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

import com.example.fe_project_cosmeticapp.R;

/**
 * Class tiện ích để quản lý hiển thị nút menu/back trên header
 */
public class HeaderManager {

    private final ImageButton menuButton;
    private final ImageButton backButton;
    private final Activity activity;

    /**
     * Khởi tạo HeaderManager
     * @param activity Activity hiện tại
     */
    public HeaderManager(Activity activity) {
        this.activity = activity;
        this.menuButton = activity.findViewById(R.id.header_menu);
        this.backButton = activity.findViewById(R.id.header_back);
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
}
