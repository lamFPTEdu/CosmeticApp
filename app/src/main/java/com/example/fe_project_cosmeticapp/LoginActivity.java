package com.example.fe_project_cosmeticapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fe_project_cosmeticapp.api.RetrofitClient;
import com.example.fe_project_cosmeticapp.model.User;
import com.example.fe_project_cosmeticapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            finish();
        }

        // Initialize views
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        // Set click listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        // Show loading
        btnLogin.setText("Đang đăng nhập...");
        btnLogin.setEnabled(false);

        // Create user object for login
        User user = new User(email, password);

        // Call login API (updated to LoginResponse)
        Call<com.example.fe_project_cosmeticapp.model.LoginResponse> call = RetrofitClient.getAuthApi().login(user);
        call.enqueue(new Callback<com.example.fe_project_cosmeticapp.model.LoginResponse>() {
            @Override
            public void onResponse(Call<com.example.fe_project_cosmeticapp.model.LoginResponse> call, Response<com.example.fe_project_cosmeticapp.model.LoginResponse> response) {
                btnLogin.setText("Đăng nhập");
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    // Save userId to session (no token in User)
                    com.example.fe_project_cosmeticapp.model.LoginResponse.TokenData tokenData = response.body().getToken();
                    User loggedInUser = new User();
                    loggedInUser.setEmail(email);
                    loggedInUser.setId(tokenData.getUserId());
                    sessionManager.saveUser(loggedInUser);

                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Vui lòng kiểm tra email và mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.fe_project_cosmeticapp.model.LoginResponse> call, Throwable t) {
                btnLogin.setText("Đăng nhập");
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
