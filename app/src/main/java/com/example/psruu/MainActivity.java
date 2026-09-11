package com.example.psruu;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // โหลดหน้าตลาดสินค้า

        // 1. กดสลับแท็บไปหน้า "บอร์ดตามหาของ" (WantBoardActivity)
        TextView tabWantedBoard = findViewById(R.id.tabWantedBoard);
        if (tabWantedBoard != null) {
            tabWantedBoard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, WantBoardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });
        }

        // 2. ดักคลิกการ์ดสินค้าเพื่อเปิด Popup รายละเอียดสินค้า
        View cardProduct = findViewById(R.id.cardProduct1);
        if (cardProduct != null) {
            cardProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProductDetailDialog();
                }
            });
        }

        // 3. ดักคลิกปุ่ม "+ ลงประกาศ" ด้านบนขวาเพื่อเปิด Popup ฟอร์มลงประกาศ
        TextView btnOpenPost = findViewById(R.id.btnOpenPost);
        if (btnOpenPost != null) {
            btnOpenPost.setOnClickListener(v -> showPostItemDialog());
        }

        // 4. เมนูด้านล่าง: ตลาดนัด (อยู่หน้านี้อยู่แล้ว)
        TextView navMarket = findViewById(R.id.navMarket);
        if (navMarket != null) {
            navMarket.setOnClickListener(v -> {
                // อยู่หน้าตลาดนัดแล้ว
            });
        }

        // 5. เมนูด้านล่าง: กดไปหน้าแชท
        TextView navChat = findViewById(R.id.navChat);
        if (navChat != null) {
            navChat.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        // 6. เมนูด้านล่าง: กดไปหน้ารายการโปรด
        TextView navFavorite = findViewById(R.id.navFavorite);
        if (navFavorite != null) {
            navFavorite.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        // 7. เมนูด้านล่าง: กดไปหน้าโปรไฟล์
        TextView navProfile = findViewById(R.id.navProfile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }

    // ฟังก์ชันแสดง Popup รายละเอียดสินค้า
    private void showProductDetailDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_product_detail);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.92);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView btnClose = dialog.findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    // ฟังก์ชันแสดง Popup ฟอร์มลงประกาศสินค้าใหม่
    private void showPostItemDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_post_item);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.92);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // ปุ่มกากบาทปิด Popup
        TextView btnClose = dialog.findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        // ปุ่มยืนยันโพสต์ประกาศ
        TextView btnSubmitPost = dialog.findViewById(R.id.btnSubmitPost);
        if (btnSubmitPost != null) {
            btnSubmitPost.setOnClickListener(v -> {
                // โค้ดสำหรับบันทึกข้อมูลประกาศ (ถ้ามี)
                dialog.dismiss();
            });
        }

        dialog.show();
    }
}