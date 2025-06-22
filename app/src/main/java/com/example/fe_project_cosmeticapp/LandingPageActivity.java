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


        // Set up Get Started button
        Button btnGetStarted = landingPageContent.findViewById(R.id.know_more_button);
        btnGetStarted.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, product.class);
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

        makeupCategory.setOnClickListener(v -> {
            // TODO: Navigate to makeup category
        });

        skincareCategory.setOnClickListener(v -> {
            // TODO: Navigate to skincare category
        });

        giftsCategory.setOnClickListener(v -> {
            // TODO: Navigate to gifts category
        });
    }
}
