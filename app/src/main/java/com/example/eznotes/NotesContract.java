package com.example.eznotes;

import android.provider.BaseColumns;

public class NotesContract {

    private NotesContract(){}

    public static class NotesEntries implements BaseColumns {

        public static final String TABLE_NAME = "notes";


        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DATETIME = "date_time";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                COLUMN_DATETIME + " REAL NOT NULL);";

        public static final String DELETE_TABLE = "DROP IF EXISTS " + TABLE_NAME + ";";


    }


}
