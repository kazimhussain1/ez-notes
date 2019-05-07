package com.example.eznotes;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.eznotes.NotesContract.NotesEntries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String TITLE_KEY = "title key";
    public static final String DESC_KEY = "Description Key";
    public static final String DATE_MODIFIED_KEY = "date modified";
    public static final String READ_KEY = "To Read or not to read";

    private static final int REQUEST_CODE_NOTE_EDITOR_EDIT_NOTE = 101;
    private static final int REQUEST_CODE_NOTE_EDITOR_NEW_NOTE = 707;



    private int currentPosition = -1;
    private String currentNoteTitle;
    private String currentNoteDesc;
    private ArrayList<Integer> selectedItems;

    private RecyclerView myRecyclerList;
    private ImageButton myDeleteButton;
    private ImageButton myListButton;
    private ImageButton myGridButton;

    RecyclerAdapter myAdapter;
    LinearLayoutManager myLinearLayoutManager;
    StaggeredGridLayoutManager myGridLayoutManager;

    private NotesDbHelper myDbHelper;


    private ArrayList<String> titles;
    private ArrayList<String> descriptions;
    private ArrayList<Long> dateModified;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        selectedItems = new ArrayList<>();

        EditText myEditText = findViewById(R.id.activity_main_startTyping);
        myRecyclerList = findViewById(R.id.activity_main_mainGrid);
        myDeleteButton = findViewById(R.id.activity_main_deleteButton);
        myListButton = findViewById(R.id.activity_main_listButton);
        myGridButton = findViewById(R.id.activity_main_GridButton);


        myDbHelper = new NotesDbHelper(this);

        retrieveNotes();
        //if somehow arrays are null then initialize with empty arrays
        if (titles == null) {
            titles = new ArrayList<>();
        }
        if (descriptions == null) {
            descriptions = new ArrayList<>();
        }
        if (dateModified == null){
            dateModified = new ArrayList<>();
        }

        //Check if there is temporary data in SharedPreferences

        SharedPreferences mySharedPreferences = getApplicationContext().getSharedPreferences("TempStorage", MODE_PRIVATE);

        Boolean readOrNot = mySharedPreferences.getBoolean(READ_KEY,false);

        if (readOrNot){
            String temp;

            temp = mySharedPreferences.getString(TITLE_KEY, "");
            titles.add(temp);

            temp = mySharedPreferences.getString(DESC_KEY, "");
            descriptions.add(temp);

            long myLong = mySharedPreferences.getLong(DATE_MODIFIED_KEY, (long) 0);
            dateModified.add(myLong);
        }


        myAdapter = new RecyclerAdapter(titles, descriptions, dateModified, R.layout.cell, this);

        myLinearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        myGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);




        myRecyclerList.setLayoutManager(myGridLayoutManager);
        myRecyclerList.setAdapter(myAdapter);


        myDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Long> myList = myAdapter.getDateModified();
                ArrayList<Long> deleteList = new ArrayList<>();

                for (int i =0; i< selectedItems.size();i++){

                    deleteList.add(myList.get(selectedItems.get(i)));
                }

                myAdapter.deleteSelected(selectedItems);

                v.setVisibility(View.GONE);


                deleteNotes(deleteList);

                selectedItems.clear();

            }
        });

        myListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRecyclerList.setLayoutManager(myLinearLayoutManager);

                v.setVisibility(View.GONE);
                myGridButton.setVisibility(View.VISIBLE);
            }
        });

        myGridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRecyclerList.setLayoutManager(myGridLayoutManager);

                v.setVisibility(View.GONE);
                myListButton.setVisibility(View.VISIBLE);
            }
        });


        myEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getBaseContext(), NoteEditor.class);

                startActivityForResult(myIntent, REQUEST_CODE_NOTE_EDITOR_NEW_NOTE);
            }
        });


        myAdapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = myRecyclerList.indexOfChild(v);

                Intent myIntent = new Intent(getBaseContext(), NoteEditor.class);

                currentNoteTitle = myAdapter.getTitles().get(position);
                currentNoteDesc =  myAdapter.getDescription().get(position);

                Bundle myBundle = new Bundle();
                myBundle.putString(TITLE_KEY, currentNoteTitle);
                myBundle.putString(DESC_KEY, currentNoteDesc);

                currentPosition = position;


                myIntent.putExtras(myBundle);

                startActivityForResult(myIntent, REQUEST_CODE_NOTE_EDITOR_EDIT_NOTE);
            }
        });

        myAdapter.setLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                int position = myRecyclerList.indexOfChild(view);

                if (selectedItems.contains(position)) {

                    selectedItems.remove((Integer) position);
                    view.setBackground(getDrawable(R.drawable.cell_bg));
                    myDeleteButton.setVisibility(View.GONE);
                } else {
                    selectedItems.add(position);
                    view.setBackground(getDrawable(R.drawable.cell_pressed_bg));
                    myDeleteButton.setVisibility(View.VISIBLE);

                }

                return true;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_NOTE_EDITOR_EDIT_NOTE  && data != null){

            Bundle myBundle = data.getExtras();
            if (myBundle != null){
                String newTitle = myBundle.getString(TITLE_KEY,"");
                String newDesc = myBundle.getString(DESC_KEY,"");

                if (currentPosition != -1 && newTitle != "" && newDesc != ""){


                    Date myDate = Calendar.getInstance().getTime();
                    myAdapter.pushNote(currentPosition,newTitle,newDesc, myDate.getTime());
                    updateNote(newTitle,newDesc, currentPosition);

                    currentPosition = -1;
                }

            }
        }else if (requestCode == REQUEST_CODE_NOTE_EDITOR_NEW_NOTE  && data != null){

            Bundle myBundle = data.getExtras();
            if (myBundle != null) {
                String newTitle = myBundle.getString(TITLE_KEY, "??");
                String newDesc = myBundle.getString(DESC_KEY, "??");



                if (!newTitle.equals("") || !newDesc.equals(""))  {

                    Date myDate = Calendar.getInstance().getTime();
                    myAdapter.pushNote(newTitle, newDesc, myDate.getTime());
                    addNote(newTitle, newDesc, myDate);
                }

                currentNoteTitle = null;
                currentNoteDesc = null;


            }

        }

    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    private void retrieveNotes(){

        SQLiteDatabase myDB = myDbHelper.getReadableDatabase();

        String[] columns = new String[]{
                NotesEntries.COLUMN_TITLE,
                NotesEntries.COLUMN_DESCRIPTION,
                NotesEntries.COLUMN_DATETIME
        };


        String order = NotesEntries.COLUMN_DATETIME + " DESC";
        Cursor myCursor = myDB.query(
                NotesEntries.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                order
        );

        int titleIndex = myCursor.getColumnIndex(NotesEntries.COLUMN_TITLE);
        int descIndex = myCursor.getColumnIndex(NotesEntries.COLUMN_DESCRIPTION);
        int dateIndex = myCursor.getColumnIndex(NotesEntries.COLUMN_DATETIME);

        titles = new ArrayList<>();
        descriptions = new ArrayList<>();
        dateModified = new ArrayList<>();

        while (myCursor.moveToNext()){
            String newTitle = myCursor.getString(titleIndex);
            String newDesc = myCursor.getString(descIndex);
            long newDate = myCursor.getLong(dateIndex);

            titles.add(newTitle);
            descriptions.add(newDesc);
            dateModified.add(newDate);
        }

        myCursor.close();
    }

    private void addNote(String newTitle, String newDescription, Date myDate){

        SQLiteDatabase myDb = myDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();




        values.put(NotesEntries.COLUMN_TITLE, newTitle);
        values.put(NotesEntries.COLUMN_DESCRIPTION, newDescription);
        values.put(NotesEntries.COLUMN_DATETIME, myDate.getTime());





        String query = "UPDATE " + NotesEntries.TABLE_NAME +
                " SET " + NotesEntries.COLUMN_DATETIME +
                " =  " + NotesEntries.COLUMN_DATETIME +
                "+1;";


       // myDb.rawQuery(query,null);

        myDb.insert(NotesEntries.TABLE_NAME, null, values);
    }

    private void updateNote(String newTitle, String newDescription, int position){

        SQLiteDatabase myDb = myDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        Date myDate = Calendar.getInstance().getTime();

        values.put(NotesEntries.COLUMN_TITLE, newTitle);
        values.put(NotesEntries.COLUMN_DESCRIPTION, newDescription);
        values.put(NotesEntries.COLUMN_DATETIME, myDate.getTime());

        Boolean isTitleNull = currentNoteTitle == "" ;

        String selector = (isTitleNull?NotesEntries.COLUMN_DESCRIPTION:NotesEntries.COLUMN_TITLE) + " = ?";
        String[] selections = new String[]{isTitleNull? currentNoteDesc: currentNoteTitle};
        myDb.update(
                NotesEntries.TABLE_NAME,
                values,
                selector,
                selections
                );

    }

    private void deleteNotes(ArrayList<Long> myList){

        SQLiteDatabase myDb = myDbHelper.getWritableDatabase();

        String selector = NotesEntries.COLUMN_DATETIME + " = ?";

        for (Long i:myList) {
            myDb.delete(NotesEntries.TABLE_NAME, selector, new String[]{String.valueOf(i)});
        }
    }


}
