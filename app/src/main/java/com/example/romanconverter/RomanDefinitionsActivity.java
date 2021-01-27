package com.example.romanconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RomanDefinitionsActivity extends AppCompatActivity {
    MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roman_definitions);
        mainActivity = new MainActivity();
    }

    /**
     * Switch activity back to the main activity
     * to access the calculator and converter
     * @param view the back button
     */
    public void switchActivityClick(View view) {
        mainActivity.buttonAnimation(view);
        Intent intent = new Intent(RomanDefinitionsActivity.this, MainActivity.class);
        startActivity(intent);
    }

}