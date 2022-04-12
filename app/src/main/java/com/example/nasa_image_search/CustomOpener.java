package com.example.nasa_image_search;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class CustomOpener extends SQLiteOpenHelper {
    public final static String DB_NAME = "nasa_image_app_db";
    public final static int DB_VERSION = 1;
    public final static String TABLE_NAME = "NASA_IMAGES";
    public final static String COL_ID = "_id";
    public final static String COL_DATE = "DATE";
    public final static String COL_IMAGE = "IMAGE";

    public CustomOpener(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DATE + " TEXT, "
                + COL_IMAGE + " BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");

        onCreate(sqLiteDatabase);
    }
}
