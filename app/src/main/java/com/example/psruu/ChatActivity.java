package com.example.psruu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView; // เพิ่มการ import TextView
import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 1. กดที่กล่องแชทของ ธนวัฒน์ ศึกษาดี เพื่อเปิดห้องแชท
        LinearLayout itemChatSample = findViewById(R.id.itemChatSample);
        if (itemChatSample != null) {
            itemChatSample.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, ChatRoomActivity.class);
                intent.putExtra("PARTNER_NAME", "ธนวัฒน์ ศึกษาดี");
                startActivity(intent);
            });
        }

        // 2. ระบบควบคุม Bottom Navigation ด้านล่าง (เปลี่ยนเป็น TextView ทั้งหมดตาม XML)
        TextView navMarket = findViewById(R.id.navMarket);
        if (navMarket != null) {
            navMarket.setOnClickListener(v -> {
                startActivity(new Intent(ChatActivity.this, MainActivity.class));
                finish();
            });
        }

        TextView navChat = findViewById(R.id.navChat);
        if (navChat != null) {
            navChat.setOnClickListener(v -> {
                // อยู่หน้าแชทแล้ว
            });
        }

        TextView navFavorite = findViewById(R.id.navFavorite);
        if (navFavorite != null) {
            navFavorite.setOnClickListener(v -> {
                startActivity(new Intent(ChatActivity.this, FavoriteActivity.class));
                finish();
            });
        }

        TextView navProfile = findViewById(R.id.navProfile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                startActivity(new Intent(ChatActivity.this, ProfileActivity.class));
                finish();
            });
        }
    }
}