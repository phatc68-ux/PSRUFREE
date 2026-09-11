package com.example.psruu; // ปรับชื่อ Package ให้ตรงกับโปรเจกต์ของคุณ

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegName, etRegEmail, etRegPassword;
    private Button btnRegister;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // เชื่อมโยง ID จากหน้า XML
        etRegName = findViewById(R.id.etRegName);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // เมื่อกดปุ่มสมัครสมาชิก
        btnRegister.setOnClickListener(v -> {
            String name = etRegName.getText().toString().trim();
            String email = etRegEmail.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();

            // ตรวจสอบว่ากรอกข้อมูลครบถ้วนไหม
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "กรุณากรอกข้อมูลให้ครบทุกช่อง", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                etRegPassword.setError("รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร");
                return;
            }

            // บันทึกข้อมูลลงใน SharedPreferences (เก็บบันทึกถาวรในเครื่อง)
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("saved_name", name);
            editor.putString("saved_email", email);
            editor.putString("saved_password", password);
            editor.apply(); // บันทึกข้อมูล

            Toast.makeText(RegisterActivity.this, "สมัครสมาชิกสำเร็จ! 🎉", Toast.LENGTH_SHORT).show();

            // ปิดหน้าสมัครสมาชิก เพื่อกลับไปหน้า Login
            finish();
        });

        // กดปุ่มกลับไปหน้าเข้าสู่ระบบ
        tvBackToLogin.setOnClickListener(v -> finish());
    }
}