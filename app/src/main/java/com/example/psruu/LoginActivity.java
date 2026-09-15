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

    private EditText etLoginEmailOrId, etLoginPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginEmailOrId = findViewById(R.id.etLoginEmailOrId);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // กดปุ่มเข้าสู่ระบบ
        btnLogin.setOnClickListener(v -> {
            String inputUser = etLoginEmailOrId.getText().toString().trim();
            String inputPassword = etLoginPassword.getText().toString().trim();

            if (inputUser.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(LoginActivity.this, "กรุณากรอกข้อมูลให้ครบทุกช่อง", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("PSRU_USER_PREF", MODE_PRIVATE);
            String savedEmail = prefs.getString("USER_EMAIL", "");
            String savedStudentId = prefs.getString("USER_STUDENT_ID", "");
            String savedPassword = prefs.getString("USER_PASSWORD", "");

            boolean isMatchUser = inputUser.equals(savedEmail) || inputUser.equals(savedStudentId);
            boolean isMatchPassword = inputPassword.equals(savedPassword);

            if (isMatchUser && isMatchPassword) {
                Toast.makeText(LoginActivity.this, "เข้าสู่ระบบสำเร็จ!", Toast.LENGTH_SHORT).show();

                // พาไปหน้าแรก (MainActivity) ทันที
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(LoginActivity.this, "อีเมล/รหัสนักศึกษา หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
            }
        });

        // ไปหน้าสมัครสมาชิก
        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}