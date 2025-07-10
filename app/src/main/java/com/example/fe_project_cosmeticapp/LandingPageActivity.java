package com.example.fe_project_cosmeticapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fe_project_cosmeticapp.base.BaseActivity;
import com.example.fe_project_cosmeticapp.utils.SessionManager;

public class LandingPageActivity extends BaseActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Set up các view và sự kiện (không cần inflate layout vì BaseActivity đã làm điều đó)
        setupViews();
    }

    private void setupViews() {
        // Set up Know More button
        Button btnGetStarted = findViewById(R.id.know_more_button);
        if (btnGetStarted != null) {
            btnGetStarted.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(this, productView.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // Load banner image
        ImageView bannerImage = findViewById(R.id.rgbaepa0qd45);
        if (bannerImage != null) {
            loadImageWithGlide(bannerImage, R.drawable.ba);
        }

        // Set up category clicks
        setupCategoryClicks();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.landing_page;
    }

    @Override
    protected int getSelectedNavigationItemId() {
        return R.id.nav_home;
    }

    @Override
    protected boolean shouldShowBackButton() {
        return false; // Sử dụng nút menu cho trang chính
    }

    private void loadImageWithGlide(ImageView imageView, int resourceId) {
        Glide.with(this)
            .load(resourceId)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(imageView);
    }

    private void setupCategoryClicks() {
        // Set up click listeners for makeup, skincare, and gifts categories
        View makeupCategory = findViewById(R.id.makeup_category_container);
        View skincareCategory = findViewById(R.id.skincare_category_container);
        View giftsCategory = findViewById(R.id.gifts_category_container);
        View takeASelfie = findViewById(R.id.take_selfie_button);

        if (makeupCategory != null) {
            makeupCategory.setOnClickListener(v -> navigateToProductViewByCategory("make up"));
        }

        if (skincareCategory != null) {
            skincareCategory.setOnClickListener(v -> navigateToProductViewByCategory("skin care"));
        }

        if (giftsCategory != null) {
            giftsCategory.setOnClickListener(v -> navigateToProductViewByCategory("gifts & sets"));
        }

        if (takeASelfie != null) {
            takeASelfie.setOnClickListener(v -> {
                // Mở SkinAnalysisActivity khi người dùng nhấn nút "Take a Selfie"
                Intent intent = new Intent(this, SkinAnalysisActivity.class);
                startActivity(intent);
            });
        }
    }

    // Phương thức để điều hướng đến trang ProductView với danh mục đã chọn
    private void navigateToProductViewByCategory(String category) {
        Intent intent = new Intent(this, productView.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
