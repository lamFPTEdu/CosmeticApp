package com.example.fe_project_cosmeticapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_project_cosmeticapp.R;
import com.example.fe_project_cosmeticapp.WebViewActivity;
import com.example.fe_project_cosmeticapp.api.PaypalApi;
import com.example.fe_project_cosmeticapp.api.RetrofitClient;
import com.example.fe_project_cosmeticapp.model.OrderHistoryItem;
import com.example.fe_project_cosmeticapp.model.PaypalPaymentRequest;
import com.example.fe_project_cosmeticapp.model.PaypalPaymentResponse;
import com.example.fe_project_cosmeticapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {
    private List<OrderHistoryItem> orders = new ArrayList<>();

    public void setOrders(List<OrderHistoryItem> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderHistoryItem order = orders.get(position);
        holder.tvOrderId.setText("Mã đơn: " + order.getOrderId());
        holder.tvOrderDate.setText("Ngày: " + order.getOrderDate());
        holder.tvOrderStatus.setText("Trạng thái: " + order.getStatus());
        holder.tvOrderTotal.setText("Tổng: " + order.getTotalAmount());
        if ("Pending".equalsIgnoreCase(order.getStatus())) {
            holder.btnPayAgain.setVisibility(View.VISIBLE);
            holder.tvPaidStatus.setVisibility(View.GONE);
            holder.btnPayAgain.setOnClickListener(v -> {
                Context context = holder.itemView.getContext();
                SessionManager sessionManager = new SessionManager(context);
                String token = sessionManager.getToken();
                if (token == null) {
                    // Có thể chuyển về login nếu cần
                    return;
                }
                PaypalApi paypalApi = RetrofitClient.getPaypalApi();
                PaypalPaymentRequest request = new PaypalPaymentRequest(order.getOrderId());
                paypalApi.createPaypalPayment("Bearer " + token, request).enqueue(new Callback<PaypalPaymentResponse>() {
                    @Override
                    public void onResponse(Call<PaypalPaymentResponse> call, Response<PaypalPaymentResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            String approvalUrl = response.body().getApprovalUrl();
                            Intent intent = new Intent(context, WebViewActivity.class);
                            intent.putExtra(WebViewActivity.EXTRA_URL, approvalUrl);
                            intent.putExtra(WebViewActivity.EXTRA_ORDER_ID, String.valueOf(order.getOrderId()));
                            context.startActivity(intent);
                        }
                    }
                    @Override
                    public void onFailure(Call<PaypalPaymentResponse> call, Throwable t) {
                        // Có thể hiển thị thông báo lỗi nếu cần
                    }
                });
            });
        } else if ("Success".equalsIgnoreCase(order.getStatus())) {
            holder.btnPayAgain.setVisibility(View.GONE);
            holder.tvPaidStatus.setVisibility(View.VISIBLE);
        } else {
            holder.btnPayAgain.setVisibility(View.GONE);
            holder.tvPaidStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderTotal, tvPaidStatus;
        Button btnPayAgain;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            btnPayAgain = itemView.findViewById(R.id.btn_pay_again);
            tvPaidStatus = itemView.findViewById(R.id.tv_paid_status);
        }
    }
}
