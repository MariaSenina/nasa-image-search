package com.example.nasa_image_search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class NameActivity extends ActivityHeaderCreator {
    SharedPreferences preferences;
    EditText typeField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        createActivityHeader();

        Button backHomeButton = findViewById(R.id.backHomeButton);
        backHomeButton.setOnClickListener(clickBack -> {
            typeField = findViewById(R.id.inputText);
            Intent homePage = new Intent(this, MainActivity.class);
            homePage.putExtra("name", typeField.getText().toString());
            startActivity(homePage);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveSharedPreferences(typeField.getText().toString());
    }

    private void saveSharedPreferences(String stringToSave) {
        SharedPreferences preferences = getSharedPreferences("SavedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ReserveName", stringToSave);
        editor.commit();
    }
}