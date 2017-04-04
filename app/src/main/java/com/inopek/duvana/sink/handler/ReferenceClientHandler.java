package com.inopek.duvana.sink.handler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReferenceClientHandler extends SQLiteOpenHelper {

    public static final String COLUMN_KEY = "id";
    public static final String COLUMN_REFERENCE = "reference";
    public static final String COLUMN_CLIENT_NAME = "client_name";
    public static final String COLUMN_FILE_NAME = "file_name";
    public static final String COLUMN_PROFILE_NAME = "profile_name";
    public static final String COLUMN_DATE_NAME = "date";
    public static final String UNIQUE_CONSTRAINT = "UNIQUE (" + COLUMN_REFERENCE + ", " + COLUMN_CLIENT_NAME + ", " + COLUMN_PROFILE_NAME + ") ON CONFLICT REPLACE";

    public static final String TABLE_NAME = "client_reference";
    public static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + COLUMN_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_REFERENCE + " TEXT, " +
                    COLUMN_FILE_NAME + " TEXT, " +
                    COLUMN_PROFILE_NAME + " TEXT, " +
                    COLUMN_CLIENT_NAME + " TEXT, " +
                    COLUMN_DATE_NAME + " INTEGER, " +
                    UNIQUE_CONSTRAINT + ");";


    public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public ReferenceClientHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(TABLE_DROP);
        onCreate(db);
    }
}
