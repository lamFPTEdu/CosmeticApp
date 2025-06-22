package com.example.fe_project_cosmeticapp.base;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fe_project_cosmeticapp.R;
import com.example.fe_project_cosmeticapp.navigation.NavigationHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * BaseActivity để cung cấp bottom navigation cho tất cả các activity
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        // Lấy content frame để add layout của activity con
        FrameLayout contentFrame = findViewById(R.id.content_frame);

        // Inflate layout của activity con vào content frame
        View childView = getLayoutInflater().inflate(getLayoutResourceId(), contentFrame, false);
        contentFrame.addView(childView);

        // Setup bottom navigation
        setupBottomNavigation();
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
     * Trả về layout resource ID của activity con
     */
    protected abstract int getLayoutResourceId();

    /**
     * Trả về ID của item được chọn trong bottom navigation
     */
    protected abstract int getSelectedNavigationItemId();
}
