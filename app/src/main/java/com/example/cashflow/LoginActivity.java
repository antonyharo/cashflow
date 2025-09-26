package com.example.cashflow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoRegister;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "users_prefs";
    private static final String KEY_LOGGED_IN_EMAIL = "logged_in_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        btnLogin.setOnClickListener(v -> doLogin());
        tvGoRegister.setOnClickListener(v -> {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        });

        // se já estiver logado redireciona
        String loggedEmail = prefs.getString(KEY_LOGGED_IN_EMAIL, null);
        if (loggedEmail != null) {
            goToMain(loggedEmail);
        }
    }

    private void doLogin() {
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString();

        if (!Util.isValidEmail(email)) {
            etEmail.setError("E-mail inválido");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Insira a senha");
            return;
        }

        String storedHash = prefs.getString(email + "_pw", null);
        if (storedHash == null) {
            Toast.makeText(this, "Conta não encontrada", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashed = Util.sha256(password);
        if (!hashed.equals(storedHash)) {
            Toast.makeText(this, "Senha incorreta", Toast.LENGTH_SHORT).show();
            return;
        }

        // salvar estado de login (simples)
        prefs.edit().putString(KEY_LOGGED_IN_EMAIL, email).apply();

        Toast.makeText(this, "Login bem-sucedido", Toast.LENGTH_SHORT).show();
        goToMain(email);
    }

    private void goToMain(String email) {
        Intent i = new Intent(this, MainActivity.class);
        // limpar backstack para que usuário não volte ao login com back
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("email", email);
        startActivity(i);
        finish();
    }
}
