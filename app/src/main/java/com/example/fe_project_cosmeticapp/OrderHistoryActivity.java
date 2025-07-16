package com.example.fe_project_cosmeticapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_project_cosmeticapp.adapter.OrderHistoryAdapter;
import com.example.fe_project_cosmeticapp.api.RetrofitClient;
import com.example.fe_project_cosmeticapp.base.BaseActivity;
import com.example.fe_project_cosmeticapp.model.OrderHistoryItem;
import com.example.fe_project_cosmeticapp.model.OrderHistoryResponse;
import com.example.fe_project_cosmeticapp.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends BaseActivity {
    private RecyclerView rvOrderHistory;
    private ProgressBar progressBar;
    private OrderHistoryAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_order_history;
    }

    @Override
    protected int getSelectedNavigationItemId() {
        return -1; // Không chọn item nào trong bottom navigation để tránh tự động quay về Profile
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // KHÔNG gọi setContentView ở đây!
        // BaseActivity sẽ tự động chèn layout activity_order_history.xml vào base_layout.xml
        rvOrderHistory = findViewById(R.id.rv_order_history);
        progressBar = findViewById(R.id.progressBarOrderHistory);
        sessionManager = new SessionManager(this);

        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderHistoryAdapter();
        rvOrderHistory.setAdapter(adapter);

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        progressBar.setVisibility(View.VISIBLE);
        int userId = sessionManager.getUser() != null ? sessionManager.getUser().getId() : -1;
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        RetrofitClient.getUserApi().getOrderHistory(userId).enqueue(new Callback<OrderHistoryResponse>() {
            @Override
            public void onResponse(Call<OrderHistoryResponse> call, Response<OrderHistoryResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderHistoryItem> orders = response.body().getOrders();
                    adapter.setOrders(orders);
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "Không thể tải lịch sử đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderHistoryResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderHistoryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
