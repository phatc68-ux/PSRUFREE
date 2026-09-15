package com.example.psruu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // กดที่กล่องแชทของ ธนวัฒน์ ศึกษาดี เพื่อเปิดห้องแชทใช้งานจริง
        LinearLayout itemChatSample = findViewById(R.id.itemChatSample);
        if (itemChatSample != null) {
            itemChatSample.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, ChatRoomActivity.class);
                intent.putExtra("PARTNER_NAME", "ธนวัฒน์ ศึกษาดี");
                startActivity(intent);
            });
        }

        // ระบบควบคุม Bottom Navigation ด้านล่าง
        LinearLayout navMarket = findViewById(R.id.navMarket);
        if (navMarket != null) {
            navMarket.setOnClickListener(v -> {
                startActivity(new Intent(ChatActivity.this, MainActivity.class));
                finish();
            });
        }
    }
}