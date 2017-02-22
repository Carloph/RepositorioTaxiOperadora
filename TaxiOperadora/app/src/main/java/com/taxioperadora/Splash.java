package com.taxioperadora;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    private static long TIME_SPLASH_SCREEN = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent_ini = new Intent(Splash.this, Home.class);
                startActivity(intent_ini);
                finish();

            }
        };
        Timer timer = new Timer();
        timer.schedule(task, TIME_SPLASH_SCREEN);
    }
}
