package com.mad.bookreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

public class settingsMain extends AppCompatActivity {

    final static String TAG = "settingsMain.java";
    private Switch darkSwitch;
    public static final String MYPREFERENCES="nightModePrefs";
    public static final String KEY_ISNIGHTMODE="isNightMode";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_main);
        Toolbar toolbar = findViewById(R.id.normal_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.v(TAG,"Top toolbar set");

        sharedPreferences=getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);

        darkSwitch=findViewById(R.id.darkswitch);
        checkNightModeSwitch();
        darkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    saveNightModeState(true);
                    recreate();
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    saveNightModeState(false);
                    recreate();
                }
            }

            private void saveNightModeState(boolean b) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean(KEY_ISNIGHTMODE,b);
                editor.apply();
            }
        });

    }

    //Check sharedpreference to see if night mode is on and display the switch accordingly
    public void checkNightModeSwitch() {
        if (sharedPreferences.getBoolean(KEY_ISNIGHTMODE,false)){
            darkSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            darkSwitch.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
