package com.example.psruu;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatRoomActivity extends AppCompatActivity {

    private LinearLayout containerMessages;
    private EditText etMessageInput;
    private Button btnSendMessage;
    private TextView btnBack, tvChatPartnerName;
    private ScrollView scrollViewChat;

    private static final String PREF_CHAT_ROOM = "PSRU_CHAT_MESSAGES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        containerMessages = findViewById(R.id.containerMessages);
        etMessageInput = findViewById(R.id.etMessageInput);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnBack = findViewById(R.id.btnBack);
        tvChatPartnerName = findViewById(R.id.tvChatPartnerName);
        scrollViewChat = findViewById(R.id.scrollViewChat);

        String partnerName = getIntent().getStringExtra("PARTNER_NAME");
        if (partnerName != null && !partnerName.isEmpty()) {
            tvChatPartnerName.setText(partnerName);
        }

        btnBack.setOnClickListener(v -> finish());

        btnSendMessage.setOnClickListener(v -> {
            String messageText = etMessageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()) + " น.";
                saveMessage(messageText, currentTime, true);
                addMessageBubbleUI(messageText, currentTime, true);
                etMessageInput.setText("");

                scrollViewChat.post(() -> scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN));
            }
        });

        loadMessages();
    }

    private void saveMessage(String text, String time, boolean isSender) {
        SharedPreferences prefs = getSharedPreferences(PREF_CHAT_ROOM, MODE_PRIVATE);
        String jsonStr = prefs.getString("messages", "[]");
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject obj = new JSONObject();
            obj.put("text", text);
            obj.put("time", time);
            obj.put("isSender", isSender);
            jsonArray.put(obj);
            prefs.edit().putString("messages", jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadMessages() {
        containerMessages.removeAllViews();
        SharedPreferences prefs = getSharedPreferences(PREF_CHAT_ROOM, MODE_PRIVATE);
        String jsonStr = prefs.getString("messages", "[]");

        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            if (jsonArray.length() == 0) {
                saveMessage("นัดรับที่อาคาร กป. ช่วงบ่ายสองนะครับ", "10:42 น.", false);
                jsonArray = new JSONArray(prefs.getString("messages", "[]"));
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.optJSONObject(i);
                if (obj != null) {
                    addMessageBubbleUI(
                            obj.optString("text", ""),
                            obj.optString("time", ""),
                            obj.optBoolean("isSender", false)
                    );
                }
            }
            scrollViewChat.post(() -> scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMessageBubbleUI(String text, String time, boolean isSender) {
        LinearLayout messageRow = new LinearLayout(this);
        messageRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        messageRow.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 4, 0, 4);

        if (isSender) {
            params.gravity = android.view.Gravity.END;
        } else {
            params.gravity = android.view.Gravity.START;
        }
        messageRow.setLayoutParams(params);

        TextView tvBubble = new TextView(this);
        tvBubble.setText(text);
        tvBubble.setTextSize(14f); // แก้ไขจาก 14sp เป็น 14f
        tvBubble.setPadding(16, 12, 16, 12);
        tvBubble.setTextColor(isSender ? Color.parseColor("#FFFFFF") : Color.parseColor("#212529"));
        tvBubble.setBackgroundColor(isSender ? Color.parseColor("#00794C") : Color.parseColor("#E9ECEF"));

        TextView tvTime = new TextView(this);
        tvTime.setText(time);
        tvTime.setTextSize(10f); // แก้ไขจาก 10sp เป็น 10f
        tvTime.setTextColor(Color.parseColor("#ADB5BD"));
        tvTime.setPadding(4, 2, 4, 2);
        if (isSender) {
            tvTime.setGravity(android.view.Gravity.END);
        }

        messageRow.addView(tvBubble);
        messageRow.addView(tvTime);
        containerMessages.addView(messageRow);
    }
}