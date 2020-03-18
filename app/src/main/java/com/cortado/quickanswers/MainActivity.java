package com.cortado.quickanswers;

import android.os.Bundle;

import com.cortado.quickanswers.gui.activities.MainGui;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new MainGui(this).setup();
    }
}
