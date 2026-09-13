package com.example.psruu; // ปรับชื่อ Package ตามโปรเจกต์ของคุณ

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private TextView btnOpenPost;
    private LinearLayout containerProducts;
    private static final String PREF_NAME = "PSRU_SWAP_PRODUCTS";
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivPreviewImage;
    private LinearLayout layoutPlaceholderImage;
    private String selectedImageUriStr = "";
    private Dialog currentDialog;
    private LinearLayout currentRowLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenPost = findViewById(R.id.btnOpenPost);
        containerProducts = findViewById(R.id.containerProducts);

        if (btnOpenPost != null) {
            btnOpenPost.setOnClickListener(v -> showPostItemDialog());
        }

        loadSavedProducts();
    }

    private void showPostItemDialog() {
        currentDialog = new Dialog(MainActivity.this);
        currentDialog.setContentView(R.layout.dialog_post_item);

        if (currentDialog.getWindow() != null) {
            currentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92);
            currentDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        EditText etProductName = currentDialog.findViewById(R.id.etProductName);
        EditText etProductPrice = currentDialog.findViewById(R.id.etProductPrice);
        EditText etProductDetail = currentDialog.findViewById(R.id.etProductDetail);
        EditText etLocation = currentDialog.findViewById(R.id.etLocation);
        Spinner spinnerCategory = currentDialog.findViewById(R.id.spinnerCategory);
        TextView btnClose = currentDialog.findViewById(R.id.btnClose);
        Button btnSubmitPost = currentDialog.findViewById(R.id.btnSubmitPost);

        LinearLayout btnSelectImage = currentDialog.findViewById(R.id.btnSelectImage);
        ivPreviewImage = currentDialog.findViewById(R.id.ivPreviewImage);
        layoutPlaceholderImage = currentDialog.findViewById(R.id.layoutPlaceholderImage);
        selectedImageUriStr = "";

        String[] categories = {"📖 หนังสือเรียน", "✏️ อุปกรณ์การเรียน", "💻 ไอที/หูฟัง", "👕 เสื้อผ้า/เบ็ดเตล็ด"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> currentDialog.dismiss());
        }

        if (btnSelectImage != null) {
            btnSelectImage.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            });
        }

        if (btnSubmitPost != null) {
            btnSubmitPost.setOnClickListener(v -> {
                String name = etProductName.getText().toString().trim();
                String price = etProductPrice.getText().toString().trim();
                String detail = etProductDetail.getText().toString().trim();
                String location = etLocation.getText().toString().trim();
                String category = spinnerCategory.getSelectedItem().toString();

                if (name.isEmpty()) {
                    etProductName.setError("กรุณากรอกชื่อสินค้า");
                    return;
                }

                if (price.isEmpty()) {
                    price = "0";
                }

                if (detail.isEmpty()) {
                    detail = "ไม่มีรายละเอียดเพิ่มเติม";
                }

                if (location.isEmpty()) {
                    location = "ม.ราชภัฏพิบูลสงคราม";
                }

                saveProductToPrefs(name, "฿" + price, category, selectedImageUriStr, detail, location);
                addProductCardUI(name, "฿" + price, category, selectedImageUriStr, detail, location);

                Toast.makeText(MainActivity.this, "โพสต์ประกาศสำเร็จ! 🎉", Toast.LENGTH_SHORT).show();
                currentDialog.dismiss();
            });
        }

        currentDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri sourceUri = data.getData();

            File savedFile = saveUriToInternalCache(sourceUri);
            if (savedFile != null) {
                selectedImageUriStr = Uri.fromFile(savedFile).toString();

                if (ivPreviewImage != null && layoutPlaceholderImage != null) {
                    ivPreviewImage.setImageURI(Uri.parse(selectedImageUriStr));
                    ivPreviewImage.setVisibility(View.VISIBLE);
                    layoutPlaceholderImage.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "ไม่สามารถโหลดรูปภาพนี้ได้", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File saveUriToInternalCache(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File cacheDir = getCacheDir();
            File destinationFile = new File(cacheDir, "img_" + System.currentTimeMillis() + ".jpg");

            FileOutputStream outputStream = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return destinationFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addProductCardUI(String name, String price, String category, String imageUriStr, String detail, String location) {
        // จัดแถวละ 2 คอลัมน์
        if (currentRowLayout == null || currentRowLayout.getChildCount() >= 2) {
            currentRowLayout = new LinearLayout(this);
            currentRowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            currentRowLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams rowParams = (LinearLayout.LayoutParams) currentRowLayout.getLayoutParams();
            rowParams.setMargins(0, 0, 0, 12);
            currentRowLayout.setLayoutParams(rowParams);

            containerProducts.addView(currentRowLayout, 0);
        }

        LinearLayout cardLayout = new LinearLayout(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );

        int childIndex = currentRowLayout.getChildCount();
        if (childIndex == 0) {
            cardParams.setMargins(0, 0, 6, 0); // ตัวซ้าย
        } else {
            cardParams.setMargins(6, 0, 0, 0); // ตัวขวา
        }

        cardLayout.setLayoutParams(cardParams);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setBackgroundColor(Color.WHITE);
        cardLayout.setPadding(8, 8, 8, 10);
        cardLayout.setElevation(2f);
        cardLayout.setClickable(true);
        cardLayout.setFocusable(true);

        cardLayout.setOnClickListener(v -> showProductDetailDialog(name, price, category, imageUriStr, detail, location));

        FrameLayout imageContainer = new FrameLayout(this);
        // ขยายขนาดความสูงรูปภาพให้ใหญ่ขึ้น (175 dp)
        int imageHeightPx = (int) (175 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams imgContainerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, imageHeightPx
        );
        imgContainerParams.setMargins(0, 0, 0, 6);
        imageContainer.setLayoutParams(imgContainerParams);
        imageContainer.setBackgroundColor(Color.WHITE);

        ImageView ivProduct = new ImageView(this);
        ivProduct.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        ivProduct.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (imageUriStr != null && !imageUriStr.isEmpty()) {
            try {
                ivProduct.setImageURI(Uri.parse(imageUriStr));
            } catch (Exception e) {
                ivProduct.setBackgroundColor(Color.parseColor("#CED4DA"));
            }
        }

        TextView tvBadge = new TextView(this);
        tvBadge.setText("ขาย");
        tvBadge.setTextColor(Color.WHITE);
        tvBadge.setTextSize(8);
        tvBadge.setPadding(6, 2, 6, 2);
        tvBadge.setBackgroundColor(Color.parseColor("#00794C"));

        FrameLayout.LayoutParams badgeParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        tvBadge.setLayoutParams(badgeParams);

        imageContainer.addView(ivProduct);
        imageContainer.addView(tvBadge);

        TextView tvCategory = new TextView(this);
        tvCategory.setText(category);
        tvCategory.setTextColor(Color.parseColor("#00794C"));
        tvCategory.setTextSize(9);
        tvCategory.setMaxLines(1);

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextColor(Color.parseColor("#333333"));
        tvName.setTextSize(11);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvName.setMaxLines(2);

        TextView tvPrice = new TextView(this);
        tvPrice.setText(price);
        tvPrice.setTextColor(Color.parseColor("#00794C"));
        tvPrice.setTextSize(12);
        tvPrice.setTypeface(null, android.graphics.Typeface.BOLD);
        tvPrice.setPadding(0, 2, 0, 0);

        cardLayout.addView(imageContainer);
        cardLayout.addView(tvCategory);
        cardLayout.addView(tvName);
        cardLayout.addView(tvPrice);

        currentRowLayout.addView(cardLayout);
    }

    private void showProductDetailDialog(String name, String price, String category, String imageUriStr, String detail, String location) {
        Dialog detailDialog = new Dialog(MainActivity.this);
        detailDialog.setContentView(R.layout.dialog_product_detail);

        if (detailDialog.getWindow() != null) {
            detailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92);
            detailDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        ImageView ivDetailProduct = detailDialog.findViewById(R.id.ivDetailProduct);
        TextView tvDetailCategory = detailDialog.findViewById(R.id.tvDetailCategory);
        TextView tvDetailName = detailDialog.findViewById(R.id.tvDetailName);
        TextView tvDetailPrice = detailDialog.findViewById(R.id.tvDetailPrice);
        TextView tvDetailDescription = detailDialog.findViewById(R.id.tvDetailDescription);
        TextView tvDetailLocation = detailDialog.findViewById(R.id.tvDetailLocation);
        View btnCloseDetail = detailDialog.findViewById(R.id.btnClose);

        if (tvDetailName != null) tvDetailName.setText(name);
        if (tvDetailPrice != null) tvDetailPrice.setText(price);
        if (tvDetailCategory != null) tvDetailCategory.setText(category + " · PSRU SWAP");
        if (tvDetailDescription != null) tvDetailDescription.setText(detail);
        if (tvDetailLocation != null) tvDetailLocation.setText(location);

        if (ivDetailProduct != null && imageUriStr != null && !imageUriStr.isEmpty()) {
            try {
                ivDetailProduct.setImageURI(Uri.parse(imageUriStr));
            } catch (Exception e) {
                ivDetailProduct.setBackgroundColor(Color.parseColor("#CED4DA"));
            }
        }

        if (btnCloseDetail != null) {
            btnCloseDetail.setOnClickListener(v -> detailDialog.dismiss());
        }

        detailDialog.show();
    }

    private void saveProductToPrefs(String name, String price, String category, String imageUri, String detail, String location) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String productsJson = prefs.getString("product_list", "[]");

        try {
            JSONArray jsonArray = new JSONArray(productsJson);
            JSONObject newObj = new JSONObject();
            newObj.put("name", name);
            newObj.put("price", price);
            newObj.put("category", category);
            newObj.put("image", imageUri);
            newObj.put("detail", detail);
            newObj.put("location", location);

            jsonArray.put(newObj);
            prefs.edit().putString("product_list", jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadSavedProducts() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String productsJson = prefs.getString("product_list", "[]");

        try {
            JSONArray jsonArray = new JSONArray(productsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String imageUriStr = obj.optString("image", "");
                if (!imageUriStr.isEmpty()) {
                    try {
                        File imgFile = new File(Uri.parse(imageUriStr).getPath());
                        if (!imgFile.exists()) {
                            imageUriStr = "";
                        }
                    } catch (Exception e) {
                        imageUriStr = "";
                    }
                }

                String detail = obj.optString("detail", "ไม่มีรายละเอียดเพิ่มเติม");
                String location = obj.optString("location", "ม.ราชภัฏพิบูลสงคราม");

                addProductCardUI(
                        obj.getString("name"),
                        obj.getString("price"),
                        obj.getString("category"),
                        imageUriStr,
                        detail,
                        location
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}