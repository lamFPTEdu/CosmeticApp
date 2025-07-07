package com.example.fe_project_cosmeticapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class LandingPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        // Inflate landing page content into the content frame
        LayoutInflater inflater = LayoutInflater.from(this);
        ViewGroup contentFrame = findViewById(R.id.content_frame);
        View landingPageContent = inflater.inflate(R.layout.landing_page, contentFrame, true);


        // Set up Know More button
        Button btnGetStarted = landingPageContent.findViewById(R.id.know_more_button);
        btnGetStarted.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, productView.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                // Hiển thị thông báo lỗi nếu cần
            }
        });

        // Load banner image
        ImageView bannerImage = landingPageContent.findViewById(R.id.rgbaepa0qd45);
        loadImageWithGlide(bannerImage, R.drawable.ba);

        // Set up click listeners for category sections
        setupCategoryClicks(landingPageContent);

        // Set up Profile button
        View profileButton = findViewById(R.id.nav_profile);
        if (profileButton != null) {
            profileButton.setOnClickListener(v -> {
                // Kiểm tra token trong SharedPreferences
                String token = getSharedPreferences("auth", MODE_PRIVATE).getString("token", null);
                Intent intent;
                if (token == null || token.isEmpty()) {
                    // Chưa đăng nhập, chuyển sang LoginActivity
                    intent = new Intent(this, com.example.fe_project_cosmeticapp.ui.LoginActivity.class);
                } else {
                    // Đã đăng nhập, chuyển sang ProfileActivity
                    intent = new Intent(this, com.example.fe_project_cosmeticapp.ui.ProfileActivity.class);
                }
                startActivity(intent);
            });
        }
    }

    private void loadImageWithGlide(ImageView imageView, int resourceId) {
        Glide.with(this)
            .load(resourceId)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(imageView);
    }

    private void setupCategoryClicks(View view) {
        // Set up click listeners for makeup, skincare, and gifts categories
        View makeupCategory = view.findViewById(R.id.makeup_category_container);
        View skincareCategory = view.findViewById(R.id.skincare_category_container);
        View giftsCategory = view.findViewById(R.id.gifts_category_container);
        View takeASelfie = view.findViewById(R.id.take_selfie_button);

        makeupCategory.setOnClickListener(v -> {
            navigateToProductViewByCategory("make up");
        });

        skincareCategory.setOnClickListener(v -> {
            navigateToProductViewByCategory("skin care");
        });

        giftsCategory.setOnClickListener(v -> {
            navigateToProductViewByCategory("gifts & sets");
        });

        takeASelfie.setOnClickListener(v -> {
            // TODO: Navigate to take a selfie feature
        });
    }

    // Phương thức để điều hướng đến trang ProductView với danh mục đã chọn
    private void navigateToProductViewByCategory(String category) {
        Intent intent = new Intent(this, productView.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}


