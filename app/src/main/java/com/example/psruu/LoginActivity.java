package com.example.psruu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // เชื่อมโยง ID จากหน้า XML
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // เมื่อกดปุ่มเข้าสู่ระบบ
        btnLogin.setOnClickListener(v -> {
            String inputEmail = etLoginEmail.getText().toString().trim();
            String inputPassword = etLoginPassword.getText().toString().trim();

            if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(LoginActivity.this, "กรุณากรอกอีเมลและรหัสผ่าน", Toast.LENGTH_SHORT).show();
                return;
            }

            // ดึงข้อมูลที่เคยสมัครเก็บไว้ในเครื่อง
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String registeredEmail = prefs.getString("saved_email", "");
            String registeredPassword = prefs.getString("saved_password", "");

            // ตรวจสอบว่าตรงกับที่เคยสมัครไว้ไหม
            if (inputEmail.equals(registeredEmail) && inputPassword.equals(registeredPassword)) {
                Toast.makeText(LoginActivity.this, "เข้าสู่ระบบสำเร็จ 🎉", Toast.LENGTH_SHORT).show();

                // พาข้ามไปหน้าหลัก (MainActivity)
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // ปิดหน้า Login เพื่อไม่ให้กดBackกลับมาได้
            } else {
                Toast.makeText(LoginActivity.this, "อีเมลหรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
            }
        });

        // กดเพื่อย้ายไปหน้าสมัครสมาชิก (RegisterActivity)
        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}