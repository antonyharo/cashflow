package com.example.cashflow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";

    private TextView tvWelcome;
    private Button btnViewTransactions;
    private Button btnNewTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnViewTransactions = findViewById(R.id.btnViewTransactions);
        btnNewTransaction = findViewById(R.id.btnNewTransaction);

        // pegando dados do user do shared preferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int userId = prefs.getInt(KEY_USER_ID, -1);
        String userName = prefs.getString(KEY_USER_NAME, "User");

        // Se usuário não estiver logado, pode redirecionar para LoginActivity
        if (userId == -1) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
            return;
        }

        // Boas-vindas
        tvWelcome.setText("Welcome, " + userName + "!");

        // Botão para ver a lista de transações
        btnViewTransactions.setOnClickListener(v -> {
            Intent i = new Intent(this, TransactionListActivity.class);
            startActivity(i);
        });

        btnNewTransaction.setOnClickListener(v -> {
            Intent i = new Intent(this, TransactionActivity.class);
            startActivity(i);
        });
    }
}
