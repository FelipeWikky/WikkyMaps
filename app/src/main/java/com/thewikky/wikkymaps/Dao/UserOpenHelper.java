package com.thewikky.wikkymaps.Dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserOpenHelper extends SQLiteOpenHelper {

    private final String DB_NAME = "Dados";
    private final String TABELA = "INFO";

    public UserOpenHelper(Context context) {
        super(context, "Dados", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( ScriptSQL.createTable() );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
