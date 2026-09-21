package com.example.psruu;

import android.widget.ImageView;
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

    private TextView catAll, catBook, catEquipment, catIt;
    private int selectedCategoryIndex = 0;

    private TextView typeAll, typeSell, typeSwap, typeFree;
    private int selectedPostTypeFilter = 0;

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

        catAll = findViewById(R.id.catAll);
        catBook = findViewById(R.id.catBook);
        catEquipment = findViewById(R.id.catEquipment);
        catIt = findViewById(R.id.catIt);

        typeAll = findViewById(R.id.typeAll);
        typeSell = findViewById(R.id.typeSell);
        typeSwap = findViewById(R.id.typeSwap);
        typeFree = findViewById(R.id.typeFree);

        if (tabMarketplace != null && tabWantedBoard != null) {
            tabMarketplace.setOnClickListener(v -> switchTab(false));
            tabWantedBoard.setOnClickListener(v -> switchTab(true));
        }

        if (catAll != null) catAll.setOnClickListener(v -> filterCategory(0));
        if (catBook != null) catBook.setOnClickListener(v -> filterCategory(1));
        if (catEquipment != null) catEquipment.setOnClickListener(v -> filterCategory(2));
        if (catIt != null) catIt.setOnClickListener(v -> filterCategory(3));

        if (typeAll != null) typeAll.setOnClickListener(v -> filterPostType(0));
        if (typeSell != null) typeSell.setOnClickListener(v -> filterPostType(1));
        if (typeSwap != null) typeSwap.setOnClickListener(v -> filterPostType(2));
        if (typeFree != null) typeFree.setOnClickListener(v -> filterPostType(3));

        if (btnOpenPost != null) {
            btnOpenPost.setOnClickListener(v -> showPostItemDialog(isWantedTab ? 3 : 0));
        }

        // ==========================================
        // เชื่อมโยงปุ่มเมนูด้านล่าง
        // ==========================================
        LinearLayout navMarket = findViewById(R.id.navMarket);
        LinearLayout navChat = findViewById(R.id.navChat);
        LinearLayout navFavorite = findViewById(R.id.navFavorite);
        LinearLayout navProfile = findViewById(R.id.navProfile);

        if (navMarket != null) {
            navMarket.setOnClickListener(v -> {
                // อยู่หน้าหลักแล้ว
            });
        }

        if (navChat != null) {
            navChat.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            });
        }

        if (navFavorite != null) {
            navFavorite.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }

        loadSavedProducts();
    }

    private void switchTab(boolean wanted) {
        isWantedTab = wanted;

        int filterVisibility = wanted ? View.GONE : View.VISIBLE;
        if (typeAll != null) typeAll.setVisibility(filterVisibility);
        if (typeSell != null) typeSell.setVisibility(filterVisibility);
        if (typeSwap != null) typeSwap.setVisibility(filterVisibility);
        if (typeFree != null) typeFree.setVisibility(filterVisibility);

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

    private void filterPostType(int typeIndex) {
        selectedPostTypeFilter = typeIndex;
        updatePostTypeFilterUI();
        loadSavedProducts();
    }

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

    private void updatePostTypeFilterUI() {
        if (typeAll == null || typeSell == null || typeSwap == null || typeFree == null) return;

        typeAll.setBackgroundColor(Color.parseColor(selectedPostTypeFilter == 0 ? "#212529" : "#E9ECEF"));
        typeAll.setTextColor(Color.parseColor(selectedPostTypeFilter == 0 ? "#FFFFFF" : "#495057"));

        typeSell.setBackgroundColor(Color.parseColor(selectedPostTypeFilter == 1 ? "#00794C" : "#D1E7DD"));
        typeSell.setTextColor(Color.parseColor(selectedPostTypeFilter == 1 ? "#FFFFFF" : "#00794C"));

        typeSwap.setBackgroundColor(Color.parseColor(selectedPostTypeFilter == 2 ? "#495057" : "#E2E3E5"));
        typeSwap.setTextColor(Color.parseColor(selectedPostTypeFilter == 2 ? "#FFFFFF" : "#495057"));

        typeFree.setBackgroundColor(Color.parseColor(selectedPostTypeFilter == 3 ? "#842029" : "#F8D7DA"));
        typeFree.setTextColor(Color.parseColor(selectedPostTypeFilter == 3 ? "#FFFFFF" : "#842029"));
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

                saveProductToPrefs(name, "฿" + price, category, selectedImageUriStr, detail, location, isWanted, selectedPostTypeIndex);
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

            try {
                final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(sourceUri, takeFlags);
            } catch (Exception ignored) {}

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

    private void addProductCardUI(String name, String price, String category, String imageUriStr, String detail, String location, boolean wanted, int postType) {
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
        String badgeText = "ขาย";
        int badgeColor = Color.parseColor("#00794C");

        if (wanted) {
            badgeText = "ตามหา";
            badgeColor = Color.parseColor("#6F42C1");
        } else {
            switch (postType) {
                case 0:
                    badgeText = "ขาย";
                    badgeColor = Color.parseColor("#00794C");
                    break;
                case 1:
                    badgeText = "แลก";
                    badgeColor = Color.parseColor("#495057");
                    break;
                case 2:
                    badgeText = "ให้ฟรี";
                    badgeColor = Color.parseColor("#842029");
                    break;
                case 3:
                    badgeText = "ตามหา";
                    badgeColor = Color.parseColor("#6F42C1");
                    break;
                default:
                    badgeText = "ขาย";
                    badgeColor = Color.parseColor("#00794C");
                    break;
            }
        }

        tvBadge.setText(badgeText);
        tvBadge.setTextColor(Color.WHITE);
        tvBadge.setTextSize(9);
        tvBadge.setPadding(8, 3, 8, 3);
        tvBadge.setBackgroundColor(badgeColor);

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

        // [แก้ไขแล้ว] เปลี่ยนชนิดข้อมูลเป็น TextView ให้ตรงกับ dialog_product_detail.xml
        TextView btnFavorite = detailDialog.findViewById(R.id.ivFavorite);

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

        // ระบบกดบันทึกรายการโปรด
        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> {
                SharedPreferences prefs = getSharedPreferences("PSRU_FAVORITE_PREF", MODE_PRIVATE);
                String favJson = prefs.getString("favorite_list", "[]");
                try {
                    JSONArray jsonArray = new JSONArray(favJson);
                    JSONObject newObj = new JSONObject();
                    newObj.put("name", name);
                    newObj.put("price", price);
                    newObj.put("category", category);
                    newObj.put("image", imageUriStr);
                    newObj.put("detail", detail);
                    newObj.put("location", location);

                    jsonArray.put(newObj);
                    prefs.edit().putString("favorite_list", jsonArray.toString()).apply();

                    Toast.makeText(MainActivity.this, "บันทึกเข้ารายการโปรดแล้ว ❤️", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        if (btnCloseDetail != null) {
            btnCloseDetail.setOnClickListener(v -> detailDialog.dismiss());
        }

        detailDialog.show();
    }

    private void saveProductToPrefs(String name, String price, String category, String imageUri, String detail, String location, boolean wanted, int postType) {
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
            newObj.put("postType", postType);

            jsonArray.put(newObj);
            prefs.edit().putString("product_list", jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
                int postType = obj.optInt("postType", 0);

                if (selectedCategoryIndex == 1 && !category.contains("หนังสือเรียน")) continue;
                if (selectedCategoryIndex == 2 && !category.contains("อุปกรณ์เรียน")) continue;
                if (selectedCategoryIndex == 3 && !category.contains("ไอที")) continue;

                if (!isWantedTab && selectedPostTypeFilter > 0) {
                    if (selectedPostTypeFilter == 1 && postType != 0) continue; // ขาย
                    if (selectedPostTypeFilter == 2 && postType != 1) continue; // แลก
                    if (selectedPostTypeFilter == 3 && postType != 2) continue; // ให้ฟรี
                }

                String imageUriStr = "";
                try {
                    String rawUri = obj.optString("image", "");
                    if (!rawUri.isEmpty()) {
                        Uri parsedUri = Uri.parse(rawUri);
                        if (parsedUri != null && parsedUri.getPath() != null) {
                            File imgFile = new File(parsedUri.getPath());
                            if (imgFile.exists()) {
                                imageUriStr = rawUri;
                            }
                        }
                    }
                } catch (Exception ignored) {
                    imageUriStr = "";
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
                        isWantedTab,
                        postType
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            prefs.edit().putString("product_list", "[]").apply();
        }
    }
}