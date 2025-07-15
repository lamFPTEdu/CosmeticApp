package com.example.fe_project_cosmeticapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fe_project_cosmeticapp.base.BaseActivity;
import com.example.fe_project_cosmeticapp.model.User;
import com.example.fe_project_cosmeticapp.utils.SessionManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity {

    private TextView tvUserName, tvEmail, tvPhone, tvAddress;
    private Button btnLogout;
    private ImageButton btnEditAddress, btnEditName, btnEditPhone;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Không cần setContentView ở đây, đã được xử lý ở BaseActivity

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize views
        tvUserName = findViewById(R.id.tv_user_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        btnLogout = findViewById(R.id.btn_logout);
        btnEditPhone = findViewById(R.id.btn_edit_phone); // ImageButton in layout
        btnEditAddress = findViewById(R.id.btn_edit_address); // ImageButton in layout
        btnEditName = findViewById(R.id.btn_edit_name); // ImageButton in layout

        // Load user data
        loadUserData();
        loadUserProfileFromApi();

        // Set click listeners
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });
        btnEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Số điện thoại", tvPhone.getText().toString(), new OnProfileFieldUpdate() {
                    @Override
                    public void onUpdate(String newValue) {
                        updateProfileField("phone", newValue);
                    }
                });
            }
        });
        btnEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Địa chỉ", tvAddress.getText().toString(), new OnProfileFieldUpdate() {
                    @Override
                    public void onUpdate(String newValue) {
                        updateProfileField("address", newValue);
                    }
                });
            }
        });
        btnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Họ và tên", tvUserName.getText().toString(), new OnProfileFieldUpdate() {
                    @Override
                    public void onUpdate(String newValue) {
                        updateProfileField("fullName", newValue);
                    }
                });
            }
        });
    }

    private void loadUserData() {
        User user = sessionManager.getUser();
        if (user != null) {
            tvUserName.setText(user.getFullName());
            tvEmail.setText(user.getEmail());
        }
    }

    private void loadUserProfileFromApi() {
        User user = sessionManager.getUser();
        if (user == null) return;
        int userId = user.getId();
        com.example.fe_project_cosmeticapp.api.UserApi userApi = com.example.fe_project_cosmeticapp.api.RetrofitClient.getUserApi();
        retrofit2.Call<com.example.fe_project_cosmeticapp.model.UserProfile> call = userApi.getUserProfile(userId);
        call.enqueue(new retrofit2.Callback<com.example.fe_project_cosmeticapp.model.UserProfile>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.fe_project_cosmeticapp.model.UserProfile> call, retrofit2.Response<com.example.fe_project_cosmeticapp.model.UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.fe_project_cosmeticapp.model.UserProfile profile = response.body();
                    tvUserName.setText(profile.getFullName());
                    tvPhone.setText(profile.getPhone() != null ? profile.getPhone() : "Chưa cập nhật");
                    tvAddress.setText(profile.getAddress() != null ? profile.getAddress() : "Chưa cập nhật");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.fe_project_cosmeticapp.model.UserProfile> call, Throwable t) {
                // Có thể hiển thị thông báo lỗi nếu cần
            }
        });
    }

    // Hiển thị dialog chỉnh sửa
    private void showEditDialog(String title, String currentValue, final OnProfileFieldUpdate callback) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(title);
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setText(currentValue);
        builder.setView(input);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newValue = input.getText().toString().trim();
            if (!newValue.isEmpty()) {
                callback.onUpdate(newValue);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Giao diện callback cập nhật trường
    private interface OnProfileFieldUpdate {
        void onUpdate(String newValue);
    }

    // Gọi API cập nhật trường profile
    private void updateProfileField(String field, String value) {
        User user = sessionManager.getUser();
        if (user == null) return;
        int userId = user.getId();
        com.example.fe_project_cosmeticapp.api.UserApi userApi = com.example.fe_project_cosmeticapp.api.RetrofitClient.getUserApi();
        com.example.fe_project_cosmeticapp.model.UserProfile updateProfile = new com.example.fe_project_cosmeticapp.model.UserProfile();
        if (field.equals("phone")) {
            updateProfile.setPhone(value);
            updateProfile.setAddress(tvAddress.getText().toString());
            updateProfile.setFullName(tvUserName.getText().toString());
        } else if (field.equals("address")) {
            updateProfile.setAddress(value);
            updateProfile.setPhone(tvPhone.getText().toString());
            updateProfile.setFullName(tvUserName.getText().toString());
        } else if (field.equals("fullName")) {
            updateProfile.setFullName(value);
            updateProfile.setPhone(tvPhone.getText().toString());
            updateProfile.setAddress(tvAddress.getText().toString());
        }
        retrofit2.Call<com.example.fe_project_cosmeticapp.model.UserProfile> call = userApi.updateUserProfile(userId, updateProfile);
        call.enqueue(new retrofit2.Callback<com.example.fe_project_cosmeticapp.model.UserProfile>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.fe_project_cosmeticapp.model.UserProfile> call, retrofit2.Response<com.example.fe_project_cosmeticapp.model.UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (field.equals("phone")) {
                        tvPhone.setText(response.body().getPhone());
                    } else if (field.equals("address")) {
                        tvAddress.setText(response.body().getAddress());
                    } else if (field.equals("fullName")) {
                        tvUserName.setText(response.body().getFullName());
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.fe_project_cosmeticapp.model.UserProfile> call, Throwable t) {
                // Có thể hiển thị thông báo lỗi nếu cần
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }

    @Override
    protected int getSelectedNavigationItemId() {
        return R.id.nav_profile; // Đảm bảo id này đúng với menu profile trong bottom_navigation_menu.xml
    }
}
