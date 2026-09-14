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

    private TextView tabMarketplace, tabWantedBoard;
    private TextView tvSectionTitle;
    private boolean isWantedTab = false;

    // ปุ่มหมวดหมู่ตาม ID ใน XML (catAll, catBook, catEquipment, catIt)
    private TextView catAll, catBook, catEquipment, catIt;
    private int selectedCategoryIndex = 0; // 0: ทั้งหมด, 1: หนังสือเรียน, 2: อุปกรณ์เรียน, 3: ไอที/หูฟัง

    private static final String PREF_NAME = "PSRU_SWAP_PRODUCTS";
    private static final String PREF_WANTED_NAME = "PSRU_WANTED_PRODUCTS";
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivPreviewImage;
    private LinearLayout layoutPlaceholderImage;
    private String selectedImageUriStr = "";
    private Dialog currentDialog;
    private LinearLayout currentRowLayout = null;

    private int selectedPostTypeIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenPost = findViewById(R.id.btnOpenPost);
        containerProducts = findViewById(R.id.containerProducts);

        tabMarketplace = findViewById(R.id.tabMarketplace);
        tabWantedBoard = findViewById(R.id.tabWantedBoard);
        tvSectionTitle = findViewById(R.id.tvSectionTitle);

        // ผูกตัวแปรปุ่มหมวดหมู่ให้ตรงกับ ID ใน XML
        catAll = findViewById(R.id.catAll);
        catBook = findViewById(R.id.catBook);
        catEquipment = findViewById(R.id.catEquipment);
        catIt = findViewById(R.id.catIt);

        if (tabMarketplace != null && tabWantedBoard != null) {
            tabMarketplace.setOnClickListener(v -> switchTab(false));
            tabWantedBoard.setOnClickListener(v -> switchTab(true));
        }

        // เซ็ต Event การกดเปลี่ยนหน้าหมวดหมู่
        if (catAll != null) catAll.setOnClickListener(v -> filterCategory(0));
        if (catBook != null) catBook.setOnClickListener(v -> filterCategory(1));
        if (catEquipment != null) catEquipment.setOnClickListener(v -> filterCategory(2));
        if (catIt != null) catIt.setOnClickListener(v -> filterCategory(3));

        if (btnOpenPost != null) {
            btnOpenPost.setOnClickListener(v -> showPostItemDialog(isWantedTab ? 3 : 0));
        }

        loadSavedProducts();
    }

    private void switchTab(boolean wanted) {
        isWantedTab = wanted;

        if (isWantedTab) {
            if (tabMarketplace != null) {
                tabMarketplace.setBackgroundColor(Color.TRANSPARENT);
                tabMarketplace.setTextColor(Color.parseColor("#6C757D"));
                tabMarketplace.setElevation(0f);
            }
            if (tabWantedBoard != null) {
                tabWantedBoard.setBackgroundColor(Color.WHITE);
                tabWantedBoard.setTextColor(Color.parseColor("#00794C"));
                tabWantedBoard.setElevation(1f);
            }
            if (tvSectionTitle != null) {
                tvSectionTitle.setText("รายการประกาศตามหาของ");
            }
        } else {
            if (tabMarketplace != null) {
                tabMarketplace.setBackgroundColor(Color.WHITE);
                tabMarketplace.setTextColor(Color.parseColor("#00794C"));
                tabMarketplace.setElevation(1f);
            }
            if (tabWantedBoard != null) {
                tabWantedBoard.setBackgroundColor(Color.TRANSPARENT);
                tabWantedBoard.setTextColor(Color.parseColor("#6C757D"));
                tabWantedBoard.setElevation(0f);
            }
            if (tvSectionTitle != null) {
                tvSectionTitle.setText("รายการสินค้า (ขาย / แลก / ให้ฟรี)");
            }
        }

        loadSavedProducts();
    }

    private void filterCategory(int index) {
        selectedCategoryIndex = index;
        updateCategoryButtonUI();
        loadSavedProducts();
    }

    // ฟังก์ชันอัปเดตสีพื้นหลังและสีตัวหนังสือของปุ่มหมวดหมู่เวลาสลับหน้า
    private void updateCategoryButtonUI() {
        TextView[] buttons = {catAll, catBook, catEquipment, catIt};
        String[] texts = {"🔥 ทั้งหมด", "📖 หนังสือเรียน", "✏️ อุปกรณ์เรียน", "💻 ไอที/หูฟัง"};

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] != null) {
                buttons[i].setText(texts[i]);
                if (i == selectedCategoryIndex) {
                    buttons[i].setBackgroundColor(Color.parseColor("#00794C"));
                    buttons[i].setTextColor(Color.WHITE);
                } else {
                    buttons[i].setBackgroundColor(Color.parseColor("#FFFFFF"));
                    buttons[i].setTextColor(Color.parseColor("#495057"));
                }
            }
        }
    }

    private void showPostItemDialog(int defaultTypeIndex) {
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

        LinearLayout btnTypeSell = currentDialog.findViewById(R.id.btnTypeSell);
        LinearLayout btnTypeSwap = currentDialog.findViewById(R.id.btnTypeSwap);
        LinearLayout btnTypeFree = currentDialog.findViewById(R.id.btnTypeFree);
        LinearLayout btnTypeWanted = currentDialog.findViewById(R.id.btnTypeWanted);

        selectedPostTypeIndex = defaultTypeIndex;
        updatePostTypeUI(btnTypeSell, btnTypeSwap, btnTypeFree, btnTypeWanted);

        if (btnTypeSell != null) btnTypeSell.setOnClickListener(v -> { selectedPostTypeIndex = 0; updatePostTypeUI(btnTypeSell, btnTypeSwap, btnTypeFree, btnTypeWanted); });
        if (btnTypeSwap != null) btnTypeSwap.setOnClickListener(v -> { selectedPostTypeIndex = 1; updatePostTypeUI(btnTypeSell, btnTypeSwap, btnTypeFree, btnTypeWanted); });
        if (btnTypeFree != null) btnTypeFree.setOnClickListener(v -> { selectedPostTypeIndex = 2; updatePostTypeUI(btnTypeSell, btnTypeSwap, btnTypeFree, btnTypeWanted); });
        if (btnTypeWanted != null) btnTypeWanted.setOnClickListener(v -> { selectedPostTypeIndex = 3; updatePostTypeUI(btnTypeSell, btnTypeSwap, btnTypeFree, btnTypeWanted); });

        String[] categories = {"📖 หนังสือเรียน", "✏️ อุปกรณ์เรียน", "💻 ไอที/หูฟัง", "👕 เสื้อผ้า/เบ็ดเตล็ด"};
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

                boolean isWanted = (selectedPostTypeIndex == 3);

                saveProductToPrefs(name, "฿" + price, category, selectedImageUriStr, detail, location, isWanted);
                loadSavedProducts();

                Toast.makeText(MainActivity.this, "โพสต์ประกาศสำเร็จ! 🎉", Toast.LENGTH_SHORT).show();
                currentDialog.dismiss();
            });
        }

        currentDialog.show();
    }

    private void updatePostTypeUI(LinearLayout s, LinearLayout sw, LinearLayout f, LinearLayout w) {
        if (s == null || sw == null || f == null || w == null) return;

        s.setBackgroundResource(R.drawable.bg_unselected_type);
        sw.setBackgroundResource(R.drawable.bg_unselected_type);
        f.setBackgroundResource(R.drawable.bg_unselected_type);
        w.setBackgroundResource(R.drawable.bg_unselected_type);

        if (selectedPostTypeIndex == 0) s.setBackgroundColor(Color.parseColor("#D1E7DD"));
        else if (selectedPostTypeIndex == 1) sw.setBackgroundColor(Color.parseColor("#E2E3E5"));
        else if (selectedPostTypeIndex == 2) f.setBackgroundColor(Color.parseColor("#F8D7DA"));
        else if (selectedPostTypeIndex == 3) w.setBackgroundColor(Color.parseColor("#E8DAEF"));
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

    private void addProductCardUI(String name, String price, String category, String imageUriStr, String detail, String location, boolean wanted) {
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
            cardParams.setMargins(0, 0, 6, 0);
        } else {
            cardParams.setMargins(6, 0, 0, 0);
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
        tvBadge.setText(wanted ? "ตามหา" : "ขาย");
        tvBadge.setTextColor(Color.WHITE);
        tvBadge.setTextSize(8);
        tvBadge.setPadding(6, 2, 6, 2);
        tvBadge.setBackgroundColor(wanted ? Color.parseColor("#6F42C1") : Color.parseColor("#00794C"));

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

    private void saveProductToPrefs(String name, String price, String category, String imageUri, String detail, String location, boolean wanted) {
        String prefFileName = wanted ? PREF_WANTED_NAME : PREF_NAME;
        SharedPreferences prefs = getSharedPreferences(prefFileName, MODE_PRIVATE);
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

    // ฟังก์ชันโหลดและกรองข้อมูลสินค้า ป้องกันจอดำและแอปค้าง
    private void loadSavedProducts() {
        if (containerProducts != null) {
            containerProducts.removeAllViews();
        }
        currentRowLayout = null;

        String prefFileName = isWantedTab ? PREF_WANTED_NAME : PREF_NAME;
        SharedPreferences prefs = getSharedPreferences(prefFileName, MODE_PRIVATE);
        String productsJson = prefs.getString("product_list", "[]");

        try {
            JSONArray jsonArray = new JSONArray(productsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.optJSONObject(i);
                if (obj == null) continue;

                String category = obj.optString("category", "");

                // กรองตามหมวดหมู่ (0: ทั้งหมด, 1: หนังสือเรียน, 2: อุปกรณ์เรียน, 3: ไอที)
                if (selectedCategoryIndex == 1 && !category.contains("หนังสือเรียน")) continue;
                if (selectedCategoryIndex == 2 && !category.contains("อุปกรณ์เรียน")) continue;
                if (selectedCategoryIndex == 3 && !category.contains("ไอที")) continue;

                String imageUriStr = obj.optString("image", "");
                if (!imageUriStr.isEmpty()) {
                    try {
                        Uri parsedUri = Uri.parse(imageUriStr);
                        if (parsedUri != null && parsedUri.getPath() != null) {
                            File imgFile = new File(parsedUri.getPath());
                            if (!imgFile.exists()) {
                                imageUriStr = "";
                            }
                        }
                    } catch (Exception e) {
                        imageUriStr = "";
                    }
                }

                String name = obj.optString("name", "ไม่มีชื่อสินค้า");
                String price = obj.optString("price", "฿0");
                String detail = obj.optString("detail", "ไม่มีรายละเอียดเพิ่มเติม");
                String location = obj.optString("location", "ม.ราชภัฏพิบูลสงคราม");

                addProductCardUI(
                        name,
                        price,
                        category,
                        imageUriStr,
                        detail,
                        location,
                        isWantedTab
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // หากข้อมูล JSON เกิดความเสียหาย ให้รีเซ็ตค่าเป็นค่าว่างอัตโนมัติเพื่อป้องกันจอค้างซ้ำ
            prefs.edit().putString("product_list", "[]").apply();
        }
    }
}