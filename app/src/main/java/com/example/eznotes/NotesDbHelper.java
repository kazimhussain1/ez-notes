package com.example.eznotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EzNotes.db";
    private static int version = 1;


    public NotesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(NotesContract.NotesEntries.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(NotesContract.NotesEntries.DELETE_TABLE);

        version = newVersion;
        onCreate(db);
    }
}
