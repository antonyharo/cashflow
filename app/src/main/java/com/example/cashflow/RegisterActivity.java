package com.example.cashflow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etPasswordConfirm;
    private Button btnRegister;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "users_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString();
        String confirm = etPasswordConfirm.getText().toString();

        if (name.isEmpty()) {
            etName.setError("Insira seu nome");
            return;
        }
        if (!Utils.isValidEmail(email)) {
            etEmail.setError("E-mail inválido");
            return;
        }
        if (!Utils.isStrongPassword(password)) {
            etPassword.setError("Senha fraca (mín. 6 caracteres)");
            return;
        }
        if (!password.equals(confirm)) {
            etPasswordConfirm.setError("Senhas não coincidem");
            return;
        }

        // checar se já existe usuário
        if (prefs.contains(email + "_pw")) {
            Toast.makeText(this, "E-mail já cadastrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashed = Utils.sha256(password);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(email + "_name", name);
        editor.putString(email + "_pw", hashed);
        editor.apply();

        Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();

        // voltar para login automaticamente
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
