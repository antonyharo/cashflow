package com.example.cashflow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnLogout;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "users_prefs";
    private static final String KEY_LOGGED_IN_EMAIL = "logged_in_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);

        String email = getIntent().getStringExtra("email");
        if (email == null) {
            email = prefs.getString(KEY_LOGGED_IN_EMAIL, "Usuário");
        }

        String name = prefs.getString(email + "_name", null);
        if (name == null) name = email;

        tvWelcome.setText("Bem-vindo, " + name + "!");

        btnLogout.setOnClickListener(v -> {
            prefs.edit().remove(KEY_LOGGED_IN_EMAIL).apply();
            // volta para login e limpa stack
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }
}
