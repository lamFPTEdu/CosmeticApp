package com.example.fe_project_cosmeticapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class product extends AppCompatActivity {

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
    }

    private void initializeProductComponents() {
        // Thiết lập các sự kiện cho nút Filter
        View filterButton = findViewById(R.id.filter_button);
        if (filterButton != null) {
            filterButton.setOnClickListener(v -> {
                // Xử lý khi nhấp vào nút filter
                // Ví dụ mở dialog filter
            });
        }

        // Thiết lập RecyclerView cho danh sách sản phẩm (nếu có)
        // RecyclerView productRecyclerView = findViewById(R.id.product_recycler_view);
        // if (productRecyclerView != null) {
        //     productRecyclerView.setAdapter(new ProductAdapter());
        // }
    }
}