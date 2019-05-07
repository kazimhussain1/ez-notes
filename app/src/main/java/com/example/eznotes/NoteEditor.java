package com.example.eznotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.prefs.Preferences;

import static com.example.eznotes.MainActivity.DATE_MODIFIED_KEY;
import static com.example.eznotes.MainActivity.DESC_KEY;
import static com.example.eznotes.MainActivity.TITLE_KEY;

public class NoteEditor extends AppCompatActivity {

    EditText myTitle;
    EditText myDescription;

    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor myEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        myTitle = findViewById(R.id.noteEditor_titleText);
        myDescription = findViewById(R.id.noteEditor_descriptionText);


        mySharedPreferences = getApplicationContext().getSharedPreferences("TempStorage", MODE_PRIVATE);
        myEditor = mySharedPreferences.edit();



        Intent i = getIntent();
        Bundle b;
        if (i != null){
            b = i.getExtras();
            if (b != null){
                myTitle.setText(b.getString(TITLE_KEY));
                myDescription.setText(b.getString(DESC_KEY));
            }
        }
        myTitle.requestFocus();

    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent();
        Bundle b = new Bundle();

        b.putString(TITLE_KEY, myTitle.getText().toString());
        b.putString(DESC_KEY, myDescription.getText().toString());

        i.putExtras(b);

        setResult(RESULT_OK,i);

        myEditor.putBoolean(MainActivity.READ_KEY, false);

        finish();
    }

    @Override
    protected void onPause() {

        myEditor.putString(TITLE_KEY,myTitle.getText().toString());
        myEditor.putString(DESC_KEY,myDescription.getText().toString());
        myEditor.putLong(DATE_MODIFIED_KEY, Calendar.getInstance().getTime().getTime());
        myEditor.putBoolean(MainActivity.READ_KEY, true);

        myEditor.commit();

        super.onPause();
    }
}

