package com.huybinh2k.computerstore.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Created by BinhBH on 10/10/2021.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "computer_data";
    public static final String SUGGESTION_TABLE = "suggestion_table";
    public static final String SUGGESTION = "suggestion";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlAllSongs = "CREATE TABLE " + SUGGESTION_TABLE + "(" +
                SUGGESTION + " TEXT)";
        sqLiteDatabase.execSQL(sqlAllSongs);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
