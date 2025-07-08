package com.example.fe_project_cosmeticapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fe_project_cosmeticapp.LandingPageActivity;
import com.example.fe_project_cosmeticapp.R;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Lấy thông tin user từ SharedPreferences (giả sử đã lưu email và name khi login/register)
        String email = getSharedPreferences("auth", MODE_PRIVATE).getString("email", "");
        String name = getSharedPreferences("auth", MODE_PRIVATE).getString("name", "");

        // Gán thông tin vào layout
        android.widget.TextView tvEmail = findViewById(R.id.tvEmail);
        android.widget.TextView tvName = findViewById(R.id.tvName);
        if (tvEmail != null) tvEmail.setText("Email: " + email);
        if (tvName != null) tvName.setText("Họ và tên: " + name);

        // Xử lý nút Đăng xuất
        android.widget.Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // Xóa toàn bộ thông tin user khỏi SharedPreferences
                getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
                // Quay về màn hình đăng nhập
                android.content.Intent intent = new android.content.Intent(ProfileActivity.this, LandingPageActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}
