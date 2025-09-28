package com.example.cashflow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnViewTransactions;
    private Button btnNewTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnViewTransactions = findViewById(R.id.btnViewTransactions);
        btnNewTransaction = findViewById(R.id.btnNewTransaction);

        String userName = getIntent().getStringExtra("username");

        tvWelcome.setText("Bem vindo, " + userName + "!");

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
