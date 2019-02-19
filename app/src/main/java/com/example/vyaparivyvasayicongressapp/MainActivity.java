package com.example.vyaparivyvasayicongressapp;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.FirebaseApp;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,MainPage.class);
                startActivity(intent);
                finish();
            }
        },500);


        ProgressBar pgsBar = (ProgressBar)findViewById(R.id.progressBar);
        pgsBar.setVisibility(View.VISIBLE);

    }
}
