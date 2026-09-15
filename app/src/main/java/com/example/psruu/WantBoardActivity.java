package com.example.psruu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WantBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_wanted);

        // ตัวอย่างการเรียกใช้งานเมื่อผู้ใช้คลิกรายการในบอร์ดตามหาของ
        // showWantedDetailDialog();
    }

    private void showWantedDetailDialog() {
        Dialog dialog = new Dialog(WantBoardActivity.this);

        // เรียกใช้เลย์เอาต์ของบอร์ดตามหาของ
        dialog.setContentView(R.layout.dialog_wanted_detail);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // ปุ่มดูโปรไฟล์
        Button btnViewProfile = dialog.findViewById(R.id.btnViewProfile);
        btnViewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(WantBoardActivity.this, ProfileActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });

        // ปุ่มติดต่อผู้โพสต์
        Button btnContactWanted = dialog.findViewById(R.id.btnContactWanted);
        btnContactWanted.setOnClickListener(v -> {
            Intent intent = new Intent(WantBoardActivity.this, ChatActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();
    }
}