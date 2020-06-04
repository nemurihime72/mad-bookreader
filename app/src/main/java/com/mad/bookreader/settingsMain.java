package com.mad.bookreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

public class settingsMain extends AppCompatActivity {

    final static String TAG = "settingsMain.java";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_main);
        Toolbar toolbar = findViewById(R.id.normal_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.v(TAG,"Top toolbar set");
    }
}
