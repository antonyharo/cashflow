package com.example.cashflow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDHelper extends SQLiteOpenHelper {

    private static final String BD_NAME = "cashflow.db";
    private static final int BD_VERSION = 1;
    private static final String TABLE_TRANSACTIONS = "transactions";

    public static final String COL_ID = "id";
    public static final String COL_TYPE = "type";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_VALUE = "value";
    public static final String COL_DATE = "date";
    public static final String COL_CATEGORY = "category";
    public static final String COL_USERID = "user_id";

    public BDHelper(Context context) {
        super(context, BD_NAME, null, BD_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TYPE + " TEXT, " +
                COL_VALUE + " REAL, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_CATEGORY + " TEXT, " +
                COL_USERID + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    // Inserir transação
    public boolean insertTransaction(String type, double value, String description, String date, String category, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TYPE, type);
        cv.put(COL_VALUE, value);
        cv.put(COL_DESCRIPTION, description);
        cv.put(COL_DATE, date);
        cv.put(COL_CATEGORY, category);
        cv.put(COL_USERID, userId);
        long result = db.insert(TABLE_TRANSACTIONS, null, cv);
        return result != -1;
    }

    // Atualizar transação
    public boolean updateTransaction(int id, String type, double value, String description, String date, String category, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TYPE, type);
        cv.put(COL_VALUE, value);
        cv.put(COL_DESCRIPTION, description);
        cv.put(COL_DATE, date);
        cv.put(COL_CATEGORY, category);
        cv.put(COL_USERID, userId);
        int result = db.update(TABLE_TRANSACTIONS, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Excluir transação
    public boolean deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TRANSACTIONS, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Obter todas transações de um usuário
    public Cursor getUserTransactions(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_TRANSACTIONS,
                null,
                COL_USERID + "=?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                COL_DATE + " DESC"
        );
    }
}
