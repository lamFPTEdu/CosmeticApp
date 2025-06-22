package com.example.fe_project_cosmeticapp.navigation;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

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
            // Chuyển đến trang Home
            // Bạn có thể thêm Intent cho trang Home ở đây
            // Intent intent = new Intent(context, HomeActivity.class);
            // context.startActivity(intent);
            return true;
        } else if (itemId == R.id.nav_category) {
            // Chuyển đến trang Category
            // Bạn có thể thêm Intent cho trang Category ở đây
            // Intent intent = new Intent(context, CategoryActivity.class);
            // context.startActivity(intent);
            return true;
        } else if (itemId == R.id.nav_cart) {
            // Chuyển đến trang Cart
            // Bạn có thể thêm Intent cho trang Cart ở đây
            // Intent intent = new Intent(context, CartActivity.class);
            // context.startActivity(intent);
            return true;
        } else if (itemId == R.id.nav_profile) {
            // Chuyển đến trang Profile
            // Bạn có thể thêm Intent cho trang Profile ở đây
            // Intent intent = new Intent(context, ProfileActivity.class);
            // context.startActivity(intent);
            return true;
        }

        return false;
    }

    /**
     * Setup navigation cho bottom navigation view
     * @param bottomNavigationView BottomNavigationView cần được setup
     */
    public static void setupNavigation(BottomNavigationView bottomNavigationView, Context context) {
        NavigationHandler handler = new NavigationHandler(context);
        bottomNavigationView.setOnNavigationItemSelectedListener(handler);
    }
}
