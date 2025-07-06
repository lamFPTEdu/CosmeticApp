package com.example.fe_project_cosmeticapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fe_project_cosmeticapp.ProductDetailActivity;
import com.example.fe_project_cosmeticapp.R;
import com.example.fe_project_cosmeticapp.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private static final String TAG = "ProductAdapter";
    private List<Product> products;
    private Context context;

    public ProductAdapter(Context context) {
        this.context = context;
        this.products = new ArrayList<>();
    }

    // Phương thức để đặt danh sách sản phẩm mới (thay thế hoàn toàn)
    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    // Phương thức mới để thêm sản phẩm vào danh sách hiện tại
    public void addProducts(List<Product> newProducts) {
        int startPosition = this.products.size();
        this.products.addAll(newProducts);
        notifyItemRangeInserted(startPosition, newProducts.size());
    }

    // Phương thức để xóa tất cả sản phẩm
    public void clearProducts() {
        int size = this.products.size();
        this.products.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        // Bind data to the view
        holder.tvProductName.setText(product.getName());
        holder.tvProductDescription.setText(product.getDescription());

        // Định dạng giá tiền - chuyển từ VND sang định dạng tiền tệ
        String formattedPrice = String.format("%,d₫", (int)product.getPrice());
        holder.tvPrice.setText(formattedPrice);

        // Ẩn hình ảnh và hiện ProgressBar khi bắt đầu tải ảnh
        holder.imgProduct.setVisibility(View.INVISIBLE); // Ẩn ảnh ban đầu nhưng vẫn giữ không gian
        if (holder.imageLoadingProgress != null) {
            holder.imageLoadingProgress.setVisibility(View.VISIBLE);
        }

        // Xử lý tải hình ảnh từ Cloudinary bằng Glide
        String imageUrl = product.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Log.d(TAG, "Loading image from URL: " + imageUrl);

            // Sử dụng Glide để tải hình ảnh
            Glide.with(context)
                .load(imageUrl)
                .timeout(30000) // Timeout 30 giây
                .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                        // Giữ ProgressBar hiển thị khi có lỗi, không hiển thị ảnh
                        Log.e(TAG, "Error loading image: " + imageUrl + ", Error: " +
                            (e != null ? e.getMessage() : "Unknown error"));
                        return false; // Để Glide xử lý thất bại theo cách mặc định
                    }

                    @Override
                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Ẩn ProgressBar khi tải xong
                        if (holder.imageLoadingProgress != null) {
                            holder.imageLoadingProgress.setVisibility(View.GONE);
                        }
                        // Hiển thị hình ảnh
                        holder.imgProduct.setVisibility(View.VISIBLE);
                        Log.d(TAG, "Image loaded successfully: " + imageUrl);
                        return false; // Để Glide tiếp tục xử lý
                    }
                })
                .into(holder.imgProduct);
        } else {
            // Nếu không có URL hình ảnh, vẫn giữ ProgressBar hiển thị
            holder.imgProduct.setVisibility(View.INVISIBLE);
        }

        // Set item click listener - Chuyển sang trang chi tiết sản phẩm
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName;
        TextView tvProductDescription;
        TextView tvPrice;
        ProgressBar imageLoadingProgress;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imageLoadingProgress = itemView.findViewById(R.id.image_loading_progress);
        }
    }
}
