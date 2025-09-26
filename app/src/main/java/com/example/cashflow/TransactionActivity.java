package com.example.cashflow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class TransactionActivity extends AppCompatActivity {

    private EditText etType, etValue, etDescription, etDate, etCategory;
    private Button btnSave;
    private BDHelper db;
    private Transaction currentTransaction; // Para edição
    private int userId = 1; // Exemplo: usuário logado (pode vir de SharedPreferences)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        db = new BDHelper(this);

        etType = findViewById(R.id.etType);
        etValue = findViewById(R.id.etValue);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etCategory = findViewById(R.id.etCategory);
        btnSave = findViewById(R.id.btnSave);

        // Checa se veio transação para edição
        currentTransaction = (Transaction) getIntent().getSerializableExtra("transaction");
        if (currentTransaction != null) {
            etType.setText(currentTransaction.getType());
            etValue.setText(String.valueOf(currentTransaction.getValue()));
            etDescription.setText(currentTransaction.getDescription());
            etDate.setText(currentTransaction.getDate());
            etCategory.setText(currentTransaction.getCategory());
            btnSave.setText("Update");
        }

        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        String type = etType.getText().toString().trim();
        String valueStr = etValue.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String category = etCategory.getText().toString().trim();

        if (type.isEmpty() || valueStr.isEmpty() || description.isEmpty() || date.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double value;
        try {
            value = Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid value", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success;
        if (currentTransaction == null) {
            // Nova transação
            success = db.insertTransaction(type, value, description, date, category, userId);
            Toast.makeText(this, success ? "Transaction added" : "Error adding transaction", Toast.LENGTH_SHORT).show();
        } else {
            // Atualiza transação existente
            success = db.updateTransaction(currentTransaction.getId(), type, value, description, date, category, userId);
            Toast.makeText(this, success ? "Transaction updated" : "Error updating transaction", Toast.LENGTH_SHORT).show();
        }

        if (success) finish(); // Volta para lista
    }
}
