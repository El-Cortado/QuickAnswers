package com.cortado.quickanswers;

import android.os.Bundle;
import android.widget.Toast;

import com.cortado.quickanswers.gui.activities.main.MainGui;
import com.cortado.quickanswers.gui.activities.main.MainGuiModel;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainGuiModel mainGui = new MainGui(this);
        mainGui.setup();
        mainGui.registerSurveySendListener((inputType, inputOptions) ->
                Toast.makeText(MainActivity.this, inputType.name() + ": " + inputOptions.toString(), Toast.LENGTH_LONG).show());
    }
}
