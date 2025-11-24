package com.example.cashflow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    // UI Components
    private TextView tvTotalIncome, tvTotalExpense, tvTotalBalance;
    private Spinner spinnerMonth, spinnerType;
    private EditText etCategoryFilter;
    private Button btnApplyFilters;
    private ListView lvReportTransactions;

    // Database
    private BDHelper bdHelper;
    private int currentUserId = 1; // TODO: trocar pelo usuário logado

    // Lista interna para clique/edição
    private ArrayList<Transaction> filteredTransactions;

    // SharedPreferences
    private static final String PREFS_NAME = "ReportPrefs";
    private static final String KEY_MONTH = "pref_month";
    private static final String KEY_TYPE = "pref_type";
    private static final String KEY_CATEGORY = "pref_category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        bdHelper = new BDHelper(this);

        // Inicializar Views
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerType = findViewById(R.id.spinnerType);
        etCategoryFilter = findViewById(R.id.etCategoryFilter);
        btnApplyFilters = findViewById(R.id.btnApplyFilters);
        lvReportTransactions = findViewById(R.id.lvReportTransactions);

        filteredTransactions = new ArrayList<>();

        setupSpinners();
        loadFilters();
        calculateSummary();

        // Clique em item da lista (editar/excluir)
        lvReportTransactions.setOnItemClickListener((parent, view, position, id) -> {
            Transaction t = filteredTransactions.get(position);

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
                            boolean success = bdHelper.deleteTransaction(t.getId());
                            Toast.makeText(this,
                                    success ? "Transação excluída" : "Erro ao excluir",
                                    Toast.LENGTH_SHORT).show();

                            calculateSummary(); // atualizar a exibição
                        }
                    }).show();
        });

        // Botão de aplicar filtros
        btnApplyFilters.setOnClickListener(v -> {
            saveFilters();
            calculateSummary();
            Toast.makeText(this, "Filtros aplicados e salvos!", Toast.LENGTH_SHORT).show();
        });
    }

    // -----------------------------------------------------------------------

    private void setupSpinners() {
        String[] months = {"Todos", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

        ArrayAdapter<String> monthAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        String[] types = {"Todos", "Entrada", "Saída"};

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);
    }

    // -----------------------------------------------------------------------

    private void calculateSummary() {
        Cursor cursor = bdHelper.getUserTransactions(currentUserId);

        double totalIncome = 0.0;
        double totalExpense = 0.0;

        filteredTransactions.clear();
        ArrayList<String> displayList = new ArrayList<>();

        int selectedMonth = spinnerMonth.getSelectedItemPosition();
        String selectedType = spinnerType.getSelectedItem().toString();
        String categoryFilter = etCategoryFilter.getText().toString().trim().toLowerCase();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Transaction t = new Transaction();

                t.setId(cursor.getInt(cursor.getColumnIndexOrThrow(BDHelper.COL_ID)));
                t.setType(cursor.getString(cursor.getColumnIndexOrThrow(BDHelper.COL_TYPE)));
                t.setValue(cursor.getDouble(cursor.getColumnIndexOrThrow(BDHelper.COL_VALUE)));
                t.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(BDHelper.COL_DESCRIPTION)));
                t.setDate(cursor.getString(cursor.getColumnIndexOrThrow(BDHelper.COL_DATE)));
                t.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(BDHelper.COL_CATEGORY)));
                t.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(BDHelper.COL_USERID)));

                boolean matchMonth = true;
                boolean matchType = true;
                boolean matchCategory = true;

                // Filtrar por mês
                if (selectedMonth > 0) {
                    int month = extractMonthFromDate(t.getDate());
                    if (month != selectedMonth) matchMonth = false;
                }

                // Filtrar por tipo
                if (!selectedType.equals("Todos") &&
                        !t.getType().equalsIgnoreCase(selectedType)) {
                    matchType = false;
                }

                // Filtrar por categoria
                if (!categoryFilter.isEmpty() &&
                        (t.getCategory() == null ||
                                !t.getCategory().toLowerCase().contains(categoryFilter))) {
                    matchCategory = false;
                }

                // Se passou nos filtros, adicionar
                if (matchMonth && matchType && matchCategory) {

                    filteredTransactions.add(t);

                    displayList.add(
                            t.getDate() + " | " +
                                    t.getType() + " | R$ " +
                                    String.format(Locale.getDefault(), "%.2f", t.getValue()) +
                                    " | " + t.getDescription()
                    );

                    if (t.getType().equalsIgnoreCase("Entrada"))
                        totalIncome += t.getValue();
                    else
                        totalExpense += t.getValue();
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        // Atualizar lista
        lvReportTransactions.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList)
        );

        // Atualizar totais
        updateUI(totalIncome, totalExpense, totalIncome - totalExpense);
    }

    // -----------------------------------------------------------------------

    private void updateUI(double income, double expense, double balance) {
        tvTotalIncome.setText(String.format(Locale.getDefault(), "R$ %.2f", income));
        tvTotalExpense.setText(String.format(Locale.getDefault(), "R$ %.2f", expense));
        tvTotalBalance.setText(String.format(Locale.getDefault(), "R$ %.2f", balance));
    }

    // -----------------------------------------------------------------------

    private int extractMonthFromDate(String dateStr) {
        try {
            String[] parts;
            if (dateStr.contains("/")) {
                parts = dateStr.split("/");
                return Integer.parseInt(parts[1]);
            } else if (dateStr.contains("-")) {
                parts = dateStr.split("-");
                return Integer.parseInt(parts[1]);
            }
        } catch (Exception ignored) {}
        return -1;
    }

    // -----------------------------------------------------------------------

    private void saveFilters() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(KEY_MONTH, spinnerMonth.getSelectedItemPosition());
        editor.putInt(KEY_TYPE, spinnerType.getSelectedItemPosition());
        editor.putString(KEY_CATEGORY, etCategoryFilter.getText().toString());

        editor.apply();
    }

    private void loadFilters() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        spinnerMonth.setSelection(prefs.getInt(KEY_MONTH, 0));
        spinnerType.setSelection(prefs.getInt(KEY_TYPE, 0));
        etCategoryFilter.setText(prefs.getString(KEY_CATEGORY, ""));
    }
}
