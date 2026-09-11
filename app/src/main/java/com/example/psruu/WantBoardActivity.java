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

public class WantBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_wanted);

        // 1. กดสลับแท็บไปหน้าตลาดสินค้า (MainActivity)
        TextView tabMarketplace = findViewById(R.id.tabMarketplace);
        if (tabMarketplace != null) {
            tabMarketplace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WantBoardActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });
        }

        // 2. ดักคลิกการ์ดในหน้าบอร์ดตามหาของ
        View cardWanted = findViewById(R.id.cardWanted1);
        if (cardWanted != null) {
            cardWanted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showWantedDetailDialog();
                }
            });
        }

        // 3. เมนูด้านล่าง: กดไปหน้าแชท
        TextView navChat = findViewById(R.id.navChat);
        if (navChat != null) {
            navChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WantBoardActivity.this, ChatActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }
    }

    private void showWantedDetailDialog() {
        final Dialog dialog = new Dialog(WantBoardActivity.this);
        dialog.setContentView(R.layout.dialog_wanted_detail);

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
}