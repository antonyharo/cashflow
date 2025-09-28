package com.example.cashflow;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TransactionListActivity extends AppCompatActivity {

    private ListView lvTransactions;
    private Button btnNewTransaction;
    private BDHelper db;
    private ArrayList<Transaction> transactions;
    private int userId = 1; // Exemplo: usuário logado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        db = new BDHelper(this);
        lvTransactions = findViewById(R.id.lvTransactions);
        btnNewTransaction = findViewById(R.id.btnNewTransaction);

        btnNewTransaction.setOnClickListener(v -> {
            Intent i = new Intent(this, TransactionActivity.class);
            startActivity(i);
        });

        lvTransactions.setOnItemClickListener((parent, view, position, id) -> {
            Transaction t = transactions.get(position);
            String[] options = {"Editar", "Excluir"};
            new AlertDialog.Builder(this)
                    .setTitle("O que deseja fazer?")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            // Editar
                            Intent i = new Intent(this, TransactionActivity.class);
                            i.putExtra("transaction", t);
                            startActivity(i);
                        } else {
                            // Excluir
                            boolean success = db.deleteTransaction(t.getId());
                            Toast.makeText(this, success ? "Transação excluida" : "Erro ao excluir", Toast.LENGTH_SHORT).show();
                            loadTransactions();
                        }
                    }).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions();
    }

    private void loadTransactions() {
        Cursor cursor = db.getUserTransactions(userId);
        transactions = new ArrayList<>();
        ArrayList<String> displayList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Transaction t = new Transaction();
                t.setId(cursor.getInt(cursor.getColumnIndexOrThrow(BDHelper.COL_ID)));
                t.setType(cursor.getString(cursor.getColumnIndexOrThrow(BDHelper.COL_TYPE)));
                t.setValue(cursor.getDouble(cursor.getColumnIndexOrThrow(BDHelper.COL_VALUE)));
                t.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(BDHelper.COL_DESCRIPTION)));
                t.setDate(cursor.getString(cursor.getColumnIndexOrThrow(BDHelper.COL_DATE)));
                t.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(BDHelper.COL_CATEGORY)));
                t.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(BDHelper.COL_USERID)));

                transactions.add(t);
                displayList.add(t.getDate() + " | " + t.getType() + " | $" + t.getValue() + " | " + t.getDescription());
            } while (cursor.moveToNext());
        }

        cursor.close();
        lvTransactions.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList));
    }
}
