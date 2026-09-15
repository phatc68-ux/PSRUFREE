package com.example.psruu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etRegName, etRegStudentId, etRegEmail, etRegPassword;
    private LinearLayout btnSelectProfileImage;
    private TextView tvSelectedImageStatus, tvBackToLogin;
    private Button btnRegister;

    private String profileImageUriStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegName = findViewById(R.id.etRegName);
        etRegStudentId = findViewById(R.id.etRegStudentId);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        btnSelectProfileImage = findViewById(R.id.btnSelectProfileImage);
        tvSelectedImageStatus = findViewById(R.id.tvSelectedImageStatus);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // กดเลือกรูปภาพจากเครื่อง
        btnSelectProfileImage.setOnClickListener(v -> openGallery());

        // กดปุ่มสมัครสมาชิก
        btnRegister.setOnClickListener(v -> {
            String name = etRegName.getText().toString().trim();
            String studentId = etRegStudentId.getText().toString().trim();
            String email = etRegEmail.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();

            if (name.isEmpty() || studentId.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "กรุณากรอกข้อมูลให้ครบทุกช่อง", Toast.LENGTH_SHORT).show();
                return;
            }

            if (profileImageUriStr.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "กรุณาเลือกรูปโปรไฟล์", Toast.LENGTH_SHORT).show();
                return;
            }

            // บันทึกข้อมูลลง SharedPreferences สำหรับนำไปใช้งานต่อ
            SharedPreferences prefs = getSharedPreferences("PSRU_USER_PREF", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("USER_NAME", name);
            editor.putString("USER_STUDENT_ID", studentId);
            editor.putString("USER_EMAIL", email);
            editor.putString("USER_PASSWORD", password);
            editor.putString("USER_IMAGE", profileImageUriStr);
            editor.apply();

            Toast.makeText(RegisterActivity.this, "สมัครสมาชิกสำเร็จ!", Toast.LENGTH_SHORT).show();

            // กลับไปหน้าเข้าสู่ระบบ
            finish();
        });

        // กลับไปหน้า Login
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                profileImageUriStr = imageUri.toString();
                tvSelectedImageStatus.setText("เลือกไฟล์แล้ว: " + imageUri.getLastPathSegment());
                tvSelectedImageStatus.setTextColor(android.graphics.Color.parseColor("#00794C"));
            }
        }
    }
}