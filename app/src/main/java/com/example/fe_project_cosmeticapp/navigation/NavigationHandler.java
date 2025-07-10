package com.example.fe_project_cosmeticapp.navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.fe_project_cosmeticapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Lớp xử lý chuyển hướng cho bottom navigation
 */
public class NavigationHandler implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Context context;

    public NavigationHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            // Kiểm tra xem đang ở LandingPageActivity hay không
            if (!(context instanceof com.example.fe_project_cosmeticapp.LandingPageActivity)) {
                // Chỉ chuyển trang khi không phải đang ở LandingPageActivity
                Intent intent = new Intent(context, com.example.fe_project_cosmeticapp.LandingPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
            return true;
        } else if (itemId == R.id.nav_category) {
            // Hiển thị danh sách các danh mục
            showCategoryDialog();
            return true;
        } else if (itemId == R.id.nav_cart) {
            // Kiểm tra trạng thái đăng nhập trước khi mở trang giỏ hàng
            com.example.fe_project_cosmeticapp.utils.SessionManager sessionManager =
                new com.example.fe_project_cosmeticapp.utils.SessionManager(context);

            if (sessionManager.isLoggedIn()) {
                // Người dùng đã đăng nhập, chuyển đến trang giỏ hàng
                if (!(context instanceof com.example.fe_project_cosmeticapp.CartActivity)) {
                    Intent intent = new Intent(context, com.example.fe_project_cosmeticapp.CartActivity.class);
                    context.startActivity(intent);
                }
            } else {
                // Người dùng chưa đăng nhập, chuyển đến trang Login
                Intent intent = new Intent(context, com.example.fe_project_cosmeticapp.LoginActivity.class);
                context.startActivity(intent);
            }
            return true;
        } else if (itemId == R.id.nav_profile) {
            // Kiểm tra trạng thái đăng nhập và chuyển đến trang Profile hoặc Login
            com.example.fe_project_cosmeticapp.utils.SessionManager sessionManager =
                new com.example.fe_project_cosmeticapp.utils.SessionManager(context);

            if (sessionManager.isLoggedIn()) {
                // Người dùng đã đăng nhập, chỉ chuyển đến Profile nếu chưa ở ProfileActivity
                if (!(context instanceof com.example.fe_project_cosmeticapp.ProfileActivity)) {
                    Intent intent = new Intent(context, com.example.fe_project_cosmeticapp.ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            } else {
                // Người dùng chưa đăng nhập, chuyển đến trang Login
                Intent intent = new Intent(context, com.example.fe_project_cosmeticapp.LoginActivity.class);
                context.startActivity(intent);
            }
            return true;
        }

        return false;
    }

    // Phương thức để hiển thị dialog chọn danh mục
    private void showCategoryDialog() {
        final String[] categories = {"skin care", "make up", "gifts & sets", "All product"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chọn danh mục");

        builder.setItems(categories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String selectedCategory = "";

                // Nếu không phải "All product", lấy danh mục đã chọn
                if (which < categories.length - 1) {
                    selectedCategory = categories[which].toLowerCase();
                }

                // Chuyển đến trang productView với danh mục đã chọn
                Intent intent = new Intent(context, com.example.fe_project_cosmeticapp.productView.class);
                intent.putExtra("category", selectedCategory);
                context.startActivity(intent);
            }
        });

        // Tạo và hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Setup navigation cho bottom navigation view
     *
     * @param bottomNavigationView BottomNavigationView cần được setup
     */
    public static void setupNavigation(BottomNavigationView bottomNavigationView, Context context) {
        NavigationHandler handler = new NavigationHandler(context);
        bottomNavigationView.setOnNavigationItemSelectedListener(handler);
    }
}
