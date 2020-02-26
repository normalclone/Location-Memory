package com.app.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    private static final String LOG = "DB";
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "dbMapOfMemory";
    private static final String CREATE_TABLE_PLACE = "CREATE TABLE Place("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "place_id TEXT NOT NULL,"+
            "name TEXT NOT NULL,"+
            "description TEXT NOT NULL"+ ")";
    private static final String CREATE_TABLE_tLOCATION = "CREATE TABLE tLocation ("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "location_name TEXT NOT NULL,"+
            "created_at DATE NOT NULL,"+
            "latitude DOUBLE NOT NULL,"+
            "longitude DOUBLE NOT NULL)";
    private static final String CREATE_TABLE_MEMORY = "CREATE TABLE memory("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "title VARCHAR(150),"+
            "content TEXT,"+
            "created_at DATE NOT NULL,"+
            "location_id INTEGER NOT NULL,"+
            "FOREIGN KEY(location_id) REFERENCES tLocation(id))";
    private static final String CREATE_TABLE_IMG = "CREATE TABLE images("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "memory_id INTEGER NOT NULL,"+
            "img_order INTEGER NOT NULL,"+
            "link TEXT NOT NULL," +
            "FOREIGN KEY(memory_id) REFERENCES memory(id))";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_tLOCATION);
        db.execSQL(CREATE_TABLE_MEMORY);
        db.execSQL(CREATE_TABLE_IMG);
        db.execSQL(CREATE_TABLE_PLACE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS images");
        db.execSQL("DROP TABLE IF EXISTS memory");
        db.execSQL("DROP TABLE IF EXISTS tLocation");
        db.execSQL("DROP TABLE IF EXISTS Place");
        // create new tables
        onCreate(db);
    }
}
