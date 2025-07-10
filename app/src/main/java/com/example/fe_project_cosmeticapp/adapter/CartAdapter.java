package com.example.fe_project_cosmeticapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fe_project_cosmeticapp.R;
import com.example.fe_project_cosmeticapp.model.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private CartItemListener listener;
    private NumberFormat currencyFormatter;

    public interface CartItemListener {
        void onUpdateQuantity(String productId, int newQuantity);
        void onRemoveItem(String productId);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Hiển thị thông tin sản phẩm
        holder.tvProductName.setText(item.getName());
        holder.tvProductPrice.setText(currencyFormatter.format(item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvSubtotal.setText(currencyFormatter.format(item.getSubtotal()));

        // Tải hình ảnh sản phẩm
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.ic_launcher_background);
        }

        // Xử lý sự kiện tăng số lượng
        holder.btnIncreaseQuantity.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            if (listener != null) {
                listener.onUpdateQuantity(item.getProductId(), newQuantity);
            }
        });

        // Xử lý sự kiện giảm số lượng
        holder.btnDecreaseQuantity.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                if (listener != null) {
                    listener.onUpdateQuantity(item.getProductId(), newQuantity);
                }
            } else {
                if (listener != null) {
                    listener.onRemoveItem(item.getProductId());
                }
            }
        });

        // Xử lý sự kiện xóa sản phẩm
        holder.btnRemoveItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(item.getProductId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvQuantity, tvSubtotal;
        ImageButton btnIncreaseQuantity, btnDecreaseQuantity, btnRemoveItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            btnIncreaseQuantity = itemView.findViewById(R.id.btnIncreaseQuantity);
            btnDecreaseQuantity = itemView.findViewById(R.id.btnDecreaseQuantity);
            btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);
        }
    }
}
