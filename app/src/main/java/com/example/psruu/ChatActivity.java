package com.example.psruu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // จัดการคลิกปุ่ม "ตลาดนัด" ที่เมนูด้านล่าง เพื่อกลับไปหน้าหลัก
        TextView navMarket = findViewById(R.id.navMarket);
        if (navMarket != null) {
            navMarket.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }

        TextView navChat = findViewById(R.id.navChat);
        if (navChat != null) {
            navChat.setOnClickListener(v -> { /* อยู่หน้าแชทแล้ว */ });
        }

        TextView navFavorite = findViewById(R.id.navFavorite);
        if (navFavorite != null) {
            navFavorite.setOnClickListener(v -> {
                startActivity(new Intent(this, FavoriteActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }

        TextView navProfile = findViewById(R.id.navProfile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }
}