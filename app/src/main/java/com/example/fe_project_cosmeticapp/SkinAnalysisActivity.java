package com.example.fe_project_cosmeticapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe_project_cosmeticapp.adapter.ProductAdapter;
import com.example.fe_project_cosmeticapp.api.FacePlusApi;
import com.example.fe_project_cosmeticapp.api.FacePlusClient;
import com.example.fe_project_cosmeticapp.api.RetrofitClient;
import com.example.fe_project_cosmeticapp.base.BaseActivity;
import com.example.fe_project_cosmeticapp.model.ProductResponse;
import com.example.fe_project_cosmeticapp.model.SkinAnalysisResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * SkinAnalysisActivity cho phép người dùng chụp hoặc chọn ảnh selfie và phân tích loại da
 * sử dụng Face++ API, sau đó hiển thị danh sách sản phẩm phù hợp với loại da.
 */
public class SkinAnalysisActivity extends BaseActivity {
    private static final String TAG = "SkinAnalysisActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;

    // Face++ API credentials
    private static final String FACE_PLUS_API_KEY = "YZgqJiS35UuKy_ryptsY301DHJkjKDEj"; // Thay bằng API key thật
    private static final String FACE_PLUS_API_SECRET = "sMuKyJ7d7sy7ak4baByj4Tv1PRMW78My"; // Thay bằng API secret thật

    private ImageView selfieImageView;
    private Button takePhotoButton;
    private Button choosePhotoButton;
    private Button analyzeButton;
    private ProgressBar progressBar;
    private TextView resultTextView;
    private RecyclerView recommendedProductsRecyclerView;
    private ProductAdapter productAdapter;

    private String currentPhotoPath;
    private Bitmap selectedImage;
    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo các view
        initViews();

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Thiết lập các sự kiện click
        setupClickListeners();
    }

    /**
     * Khởi tạo các view từ layout
     */
    private void initViews() {
        selfieImageView = findViewById(R.id.selfieImageView);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        choosePhotoButton = findViewById(R.id.choosePhotoButton);
        analyzeButton = findViewById(R.id.analyzeButton);
        progressBar = findViewById(R.id.progressBar);
        resultTextView = findViewById(R.id.resultTextView);
        recommendedProductsRecyclerView = findViewById(R.id.recommendedProductsRecyclerView);

        // Ban đầu nút phân tích không khả dụng khi chưa có ảnh
        analyzeButton.setEnabled(false);
    }

    /**
     * Thiết lập RecyclerView để hiển thị danh sách sản phẩm
     */
    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this);
        // Thay đổi từ LinearLayoutManager sang GridLayoutManager với 2 cột
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recommendedProductsRecyclerView.setLayoutManager(gridLayoutManager);
        recommendedProductsRecyclerView.setAdapter(productAdapter);
    }

    /**
     * Thiết lập các sự kiện click cho các nút
     */
    private void setupClickListeners() {
        // Nút chụp ảnh
        takePhotoButton.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                dispatchTakePictureIntent();
            } else {
                requestCameraPermission();
            }
        });

        // Nút chọn ảnh từ thư viện - Trên Android 10+ không cần kiểm tra quyền
        choosePhotoButton.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                openGallery(); // Android 10+ không cần kiểm tra quyền
            } else {
                // Android 9 và thấp hơn vẫn cần kiểm tra quyền
                if (checkStoragePermission()) {
                    openGallery();
                } else {
                    requestStoragePermission();
                }
            }
        });

        // Nút phân tích da
        analyzeButton.setOnClickListener(v -> {
            if (selectedImage != null) {
                analyzeImage();
            } else {
                Toast.makeText(this, "Vui lòng chụp hoặc chọn ảnh trước", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Kiểm tra quyền truy cập camera
     */
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Yêu cầu quyền truy cập camera với giải thích chi tiết
     */
    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            // Hiển thị dialog giải thích lý do cần quyền truy cập camera
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Yêu cầu quyền truy cập Camera")
                .setMessage("Ứng dụng cần quyền truy cập camera để chụp ảnh selfie và phân tích loại da của bạn.")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    // Yêu cầu quyền sau khi người dùng đã đọc giải thích
                    ActivityCompat.requestPermissions(
                            SkinAnalysisActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION);
                })
                .setNegativeButton("Từ chối", (dialog, which) -> {
                    // Thông báo cho người dùng rằng chức năng sẽ không hoạt động nếu không có quyền
                    Toast.makeText(SkinAnalysisActivity.this,
                            "Không thể sử dụng chức năng chụp ảnh khi không có quyền truy cập camera",
                            Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
        } else {
            // Yêu cầu quyền trực tiếp nếu không cần giải thích thêm
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    /**
     * Kiểm tra quyền truy cập bộ nhớ
     */
    private boolean checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ sử dụng READ_MEDIA_IMAGES thay vì READ_EXTERNAL_STORAGE
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Android 10+ có thể truy cập ảnh qua MediaStore mà không cần quyền đặc biệt
            return true;
        } else {
            // Android 9 và thấp hơn cần quyền READ_EXTERNAL_STORAGE
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Yêu cầu quyền truy cập bộ nhớ với giải thích chi tiết,
     * phù hợp với từng phiên bản Android
     */
    private void requestStoragePermission() {
        String permission;

        // Xác định quyền cần yêu cầu dựa trên phiên bản Android
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        // Kiểm tra xem có nên hiển thị giải thích trước khi yêu cầu quyền
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            // Hiển thị dialog giải thích lý do cần quyền truy cập bộ nhớ
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Yêu cầu quyền truy cập thư viện ảnh")
                .setMessage("Ứng dụng cần quyền truy cập thư viện ảnh để bạn có thể chọn ảnh selfie và phân tích loại da.")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    // Yêu cầu quyền sau khi người dùng đã đọc giải thích
                    ActivityCompat.requestPermissions(
                            SkinAnalysisActivity.this,
                            new String[]{permission},
                            REQUEST_STORAGE_PERMISSION);
                })
                .setNegativeButton("Từ chối", (dialog, which) -> {
                    // Thông báo cho người dùng rằng chức năng sẽ không hoạt động nếu không có quyền
                    Toast.makeText(SkinAnalysisActivity.this,
                            "Không thể chọn ảnh khi không có quyền truy cập thư viện ảnh",
                            Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
        } else {
            // Yêu cầu quyền trực tiếp hoặc hướng dẫn người dùng vào cài đặt nếu đã bị từ chối vĩnh viễn
            if (!hasAskedForPermission(permission)) {
                // Lần đầu yêu cầu quyền
                setAskedForPermission(permission);
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{permission},
                        REQUEST_STORAGE_PERMISSION);
            } else {
                // Đã từng yêu cầu quyền trước đó và có thể đã bị từ chối vĩnh viễn
                // Hướng dẫn người dùng vào cài đặt để cấp quyền thủ công
                showSettingsDialog();
            }
        }
    }

    /**
     * Kiểm tra xem quyền đã từng được yêu cầu chưa
     */
    private boolean hasAskedForPermission(String permission) {
        return getPreferences(MODE_PRIVATE).getBoolean("asked_" + permission, false);
    }

    /**
     * Đánh dấu quyền đã được yêu cầu
     */
    private void setAskedForPermission(String permission) {
        getPreferences(MODE_PRIVATE).edit().putBoolean("asked_" + permission, true).apply();
    }

    /**
     * Hiển thị dialog hướng dẫn người dùng vào cài đặt ứng dụng để cấp quyền
     */
    private void showSettingsDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Cần cấp quyền thủ công")
            .setMessage("Bạn cần cấp quyền truy cập thư viện ảnh trong phần Cài đặt ứng dụng để sử dụng tính năng này.")
            .setPositiveButton("Đi đến Cài đặt", (dialog, which) -> {
                // Mở màn hình cài đặt ứng dụng
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                dialog.dismiss();
            })
            .setNegativeButton("Không, cảm ơn", (dialog, which) -> {
                dialog.dismiss();
            })
            .setCancelable(false)
            .show();
    }

    /**
     * Mở camera để chụp ảnh selfie
     */
    private void dispatchTakePictureIntent() {
        try {
            // Hiển thị hướng dẫn trước khi mở camera
            showImageGuidelinesDialog(() -> {
                // Tạo intent cho camera
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Tạo file tạm thời để lưu ảnh
                File photoFile = null;
                try {
                    // Tạo tên file duy nhất dựa trên timestamp
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    String imageFileName = "JPEG_" + timeStamp + "_";
                    File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
                    photoFile = File.createTempFile(
                            imageFileName,  /* prefix */
                            ".jpg",   /* suffix */
                            storageDir      /* directory */
                    );

                    // Lưu đường dẫn file để sử dụng sau này
                    currentPhotoPath = photoFile.getAbsolutePath();

                } catch (IOException ex) {
                    Log.e(TAG, "Error creating image file", ex);
                    Toast.makeText(this, "Không thể tạo file ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tiếp tục nếu file đã được tạo thành công
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(this,
                            "com.example.fe_project_cosmeticapp.fileprovider",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    // Kiểm tra xem có ứng dụng camera nào có thể xử lý intent này không
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    } else {
                        // Trường hợp không tìm thấy ứng dụng camera, thử cách khác không kiểm tra resolveActivity
                        try {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        } catch (Exception e) {
                            Toast.makeText(this, "Không tìm thấy ứng dụng camera", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "No camera app found", e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error dispatching camera intent", e);
            Toast.makeText(this, "Lỗi khi mở camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Mở thư viện ảnh để chọn ảnh
     */
    private void openGallery() {
        try {
            // Hiển thị hướng dẫn trước khi mở thư viện ảnh
            showImageGuidelinesDialog(() -> {
                // Cách 1: Sử dụng ContentResolver và MediaStore
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_PICK_IMAGE);
                }
                // Cách 2: Cách tiếp cận truyền thống với ACTION_PICK
                else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_PICK_IMAGE);
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi mở thư viện ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error opening gallery", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Xử lý ảnh từ camera
                try {
                    if (currentPhotoPath != null) {
                        // Đọc ảnh từ file nếu đã lưu vào file
                        selectedImage = BitmapFactory.decodeFile(currentPhotoPath);
                    } else if (data != null && data.getExtras() != null) {
                        // Sử dụng thumbnail nếu không lưu vào file
                        selectedImage = (Bitmap) data.getExtras().get("data");
                    } else if (photoURI != null) {
                        // Đọc từ URI nếu có
                        InputStream input = getContentResolver().openInputStream(photoURI);
                        selectedImage = BitmapFactory.decodeStream(input);
                        if (input != null) input.close();
                    }

                    if (selectedImage != null) {
                        displayImage();
                    } else {
                        Toast.makeText(this, "Không thể lấy ảnh từ camera", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing camera image: " + e.getMessage(), e);
                    Toast.makeText(this, "Lỗi xử lý ảnh từ camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                // Xử lý ảnh từ thư viện
                try {
                    Uri imageUri = data.getData();
                    if (imageUri != null) {
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        selectedImage = BitmapFactory.decodeStream(imageStream);
                        if (imageStream != null) imageStream.close();
                        displayImage();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading gallery image", e);
                    Toast.makeText(this, "Không thể tải ảnh đã chọn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Hiển thị ảnh đã chọn/chụp và kích hoạt nút phân tích
     */
    private void displayImage() {
        if (selectedImage != null) {
            selfieImageView.setImageBitmap(selectedImage);
            // Kích hoạt nút phân tích khi đã có ảnh
            analyzeButton.setEnabled(true);
        }
    }

    /**
     * Phân tích ảnh với Face++ API
     */
    private void analyzeImage() {
        // Hiển thị progress bar
        progressBar.setVisibility(View.VISIBLE);
        // Tắt nút phân tích trong quá trình xử lý
        analyzeButton.setEnabled(false);

        try {
            // Xử lý hình ảnh trước khi gửi để cải thiện chất lượng phân tích
            Bitmap processedImage = prepareImageForAnalysis(selectedImage);

            // Chuyển đổi bitmap thành file với chất lượng cao (95%)
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            processedImage.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Log kích thước ảnh để debug
            Log.d(TAG, "Image size for analysis: " + imageBytes.length + " bytes");
            Log.d(TAG, "Image dimensions: " + processedImage.getWidth() + "x" + processedImage.getHeight());

            // Tạo RequestBody cho api_key và api_secret
            RequestBody apiKeyBody = RequestBody.create(MediaType.parse("text/plain"), FACE_PLUS_API_KEY);
            RequestBody apiSecretBody = RequestBody.create(MediaType.parse("text/plain"), FACE_PLUS_API_SECRET);

            // Tạo multipart body cho ảnh
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image_file", "face_image.jpg", requestFile);

            // Gọi API Face++ để phân tích da
            FacePlusApi facePlusApi = FacePlusClient.getFacePlusApi();
            Call<SkinAnalysisResponse> call = facePlusApi.analyzeSkin(apiKeyBody, apiSecretBody, imagePart);

            // Hiển thị thông tin về quá trình phân tích
            resultTextView.setText("Đang phân tích loại da...");

            call.enqueue(new Callback<SkinAnalysisResponse>() {
                @Override
                public void onResponse(@NonNull Call<SkinAnalysisResponse> call, @NonNull Response<SkinAnalysisResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    analyzeButton.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        SkinAnalysisResponse skinAnalysisResponse = response.body();
                        Log.d(TAG, "Face++ API response successful: " + response.code());
                        // Log toàn bộ response body để debug
                        Log.d(TAG, "Raw API response: " + new com.google.gson.Gson().toJson(response.body()));

                        if (skinAnalysisResponse.getSkinType() != null) {
                            String skinTypeDescription = skinAnalysisResponse.getSkinType().getSkinTypeDescription();
                            double confidence = skinAnalysisResponse.getSkinType().getConfidence();

                            // Hiển thị kết quả phân tích
                            String resultMessage = String.format("Loại da của bạn: %s (độ tin cậy: %.1f%%)",
                                    skinTypeDescription, confidence * 100);
                            resultTextView.setText(resultMessage);

                            // Tìm kiếm sản phẩm phù hợp với loại da
                            fetchProductsBySkinType(skinTypeDescription);
                        } else {
                            resultTextView.setText("Không thể phân tích loại da từ ảnh này");
                            Log.e(TAG, "Skin type is null in the response");
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String errorMsg = response.errorBody().string();
                                Log.e(TAG, "Face++ API error: " + errorMsg + ", Status code: " + response.code());
                                resultTextView.setText("Lỗi khi phân tích: " + errorMsg);
                                Toast.makeText(SkinAnalysisActivity.this, "Lỗi xác thực với API Face++", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                        } else {
                            resultTextView.setText("Lỗi khi phân tích da");
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SkinAnalysisResponse> call, @NonNull Throwable t) {
                    Log.e(TAG, "Face++ API call failed", t);
                    progressBar.setVisibility(View.GONE);
                    analyzeButton.setEnabled(true);
                    resultTextView.setText("Lỗi kết nối: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error analyzing image", e);
            progressBar.setVisibility(View.GONE);
            analyzeButton.setEnabled(true);
            resultTextView.setText("Lỗi xử lý ảnh: " + e.getMessage());
        }
    }

    /**
     * Chuẩn bị ảnh để phân tích da tối ưu dựa trên yêu cầu của Face++ API
     * @param originalImage Ảnh gốc được chọn/chụp
     * @return Ảnh đã được xử lý để phân tích tốt hơn
     */
    private Bitmap prepareImageForAnalysis(Bitmap originalImage) {
        // Kiểm tra kích thước của ảnh
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Log.d(TAG, "Original image size: " + width + "x" + height);

        // Kiểm tra ảnh có quá nhỏ không (Face++ yêu cầu ít nhất 200x200)
        if (width < 200 || height < 200) {
            Toast.makeText(this,
                    "Ảnh quá nhỏ, nên có kích thước ít nhất 200x200 pixels",
                    Toast.LENGTH_LONG).show();

            // Nếu quá nhỏ, scale lên 2x kích thước nhưng ít nhất phải 200px
            return Bitmap.createScaledBitmap(
                    originalImage, Math.max(200, width*2), Math.max(200, height*2), true);
        }

        // Kiểm tra ảnh có quá lớn không (Face++ yêu cầu tối đa 4096x4096)
        if (width > 4096 || height > 4096) {
            float scale;
            if (width > height) {
                scale = 4096f / width;
            } else {
                scale = 4096f / height;
            }

            int newWidth = Math.round(width * scale);
            int newHeight = Math.round(height * scale);

            Log.d(TAG, "Resizing large image from " + width + "x" + height + " to " + newWidth + "x" + newHeight);

            return Bitmap.createScaledBitmap(originalImage, newWidth, newHeight, true);
        }

        // Kiểm tra kích thước file (Face++ yêu cầu không quá 2MB)
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        originalImage.compress(Bitmap.CompressFormat.JPEG, 95, stream);
        byte[] imageData = stream.toByteArray();
        double imageSizeMB = imageData.length / (1024.0 * 1024.0);
        Log.d(TAG, "Image size in MB: " + imageSizeMB);

        // Nếu kích thước file lớn hơn 2MB, giảm chất lượng
        if (imageSizeMB > 2) {
            stream = new ByteArrayOutputStream();
            int quality = 90;

            while (imageSizeMB > 2 && quality >= 70) {
                stream = new ByteArrayOutputStream();
                originalImage.compress(Bitmap.CompressFormat.JPEG, quality, stream);
                imageData = stream.toByteArray();
                imageSizeMB = imageData.length / (1024.0 * 1024.0);
                quality -= 10;
                Log.d(TAG, "Reduced quality to " + quality + ", new size: " + imageSizeMB + "MB");
            }

            // Nếu vẫn > 2MB sau khi giảm chất lượng, cần resize ảnh
            if (imageSizeMB > 2) {
                float resizeScale = 0.8f;
                int newWidth = Math.round(width * resizeScale);
                int newHeight = Math.round(height * resizeScale);

                Log.d(TAG, "Image still too large, resizing to " + newWidth + "x" + newHeight);
                return Bitmap.createScaledBitmap(originalImage, newWidth, newHeight, true);
            }
        }

        // Nếu ảnh đã đáp ứng các yêu cầu, trả về ảnh gốc
        return originalImage;
    }

    /**
     * Lấy danh sách sản phẩm phù hợp với loại da từ backend
     * @param skinType Loại da đã phân tích được
     */
    private void fetchProductsBySkinType(String skinType) {
        progressBar.setVisibility(View.VISIBLE);

        // Chuyển đổi loại da từ tiếng Việt sang dạng phù hợp với API backend
        String apiSkinType;
        switch (skinType) {
            case "Da thường":
                apiSkinType = "normal skin";
                break;
            case "Da khô":
                apiSkinType = "dry skin";
                break;
            case "Da dầu":
                apiSkinType = "oily skin";
                break;
            case "Da hỗn hợp":
                apiSkinType = "mixed skin"; // Sửa từ "combination" thành "mixed" để phù hợp với API sản phẩm
                break;
            default:
                apiSkinType = "all";
                break;
        }

        // Log loại da đã chuyển đổi để debug
        Log.d(TAG, "Đã chuyển đổi loại da '" + skinType + "' thành '" + apiSkinType + "' cho API sản phẩm");

        // Gọi API để lấy danh sách sản phẩm phù hợp
        RetrofitClient.getProductApi().getProductsBySkinType(apiSkinType, 20, 1)
            .enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        ProductResponse productResponse = response.body();

                        if (productResponse.getItems() != null && !productResponse.getItems().isEmpty()) {
                            // Hiển thị danh sách sản phẩm
                            productAdapter.setProducts(productResponse.getItems());
                            Toast.makeText(SkinAnalysisActivity.this,
                                    "Tìm thấy " + productResponse.getItems().size() + " sản phẩm phù hợp",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SkinAnalysisActivity.this,
                                    "Không tìm thấy sản phẩm phù hợp với loại da của bạn",
                                    Toast.LENGTH_SHORT).show();
                            productAdapter.clearProducts();
                        }
                    } else {
                        Toast.makeText(SkinAnalysisActivity.this,
                                "Lỗi khi tìm kiếm sản phẩm",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching products", t);
                    Toast.makeText(SkinAnalysisActivity.this,
                            "Lỗi kết nối khi tìm kiếm sản phẩm",
                            Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_skin_analysis;
    }

    @Override
    protected int getSelectedNavigationItemId() {
        return -1; // Không chọn item nào trong bottom navigation
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true; // Hiển thị nút back
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Cần quyền truy cập camera để chụp ảnh", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Cần quyền truy cập bộ nhớ để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Hiển thị hướng dẫn chọn ảnh phù hợp cho người dùng
     */
    private void showImageGuidelinesDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Hướng dẫn chọn ảnh selfie")
            .setMessage("Để phân tích da chính xác, vui lòng lưu ý:\n\n" +
                    "1. Khuôn mặt nên chiếm khoảng 50% diện tích ảnh\n" +
                    "2. Chụp ảnh dưới ánh sáng tự nhiên, đều\n" +
                    "3. Không đeo kính, khẩu trang hoặc vật che khuất\n" +
                    "4. Không trang điểm hoặc chỉ trang điểm nhẹ\n" +
                    "5. Chụp thẳng mặt, không nghiêng quá 45 độ\n" +
                    "6. Tóc không che mặt\n" +
                    "7. Ảnh rõ nét, không bị mờ")
            .setPositiveButton("Đã hiểu", null)
            .show();
    }

    /**
     * Hiển thị hướng dẫn chọn ảnh phù hợp cho người dùng
     */
    private void showImageGuidelinesDialog(Runnable onContinue) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Hướng dẫn chọn ảnh selfie")
            .setMessage("Để phân tích da chính xác, vui lòng lưu ý:\n\n" +
                    "1. Khuôn mặt nên chiếm khoảng 50% diện tích ảnh\n" +
                    "2. Chụp ảnh dưới ánh sáng tự nhiên, đều\n" +
                    "3. Không đeo kính, khẩu trang hoặc vật che khuất\n" +
                    "4. Không trang điểm hoặc chỉ trang điểm nhẹ\n" +
                    "5. Chụp thẳng mặt, không nghiêng quá 45 độ\n" +
                    "6. Tóc không che mặt\n" +
                    "7. Ảnh rõ nét, không bị mờ")
            .setPositiveButton("Đã hiểu", (dialog, which) -> {
                dialog.dismiss();
                onContinue.run();
            })
            .setCancelable(false)
            .show();
    }
}
