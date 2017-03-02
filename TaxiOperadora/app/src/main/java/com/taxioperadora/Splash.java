package com.taxioperadora;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    //private static long TIME_SPLASH_SCREEN = 5000;
    private static int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION=100;


    private Button btn_panel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(Splash.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(Splash.this, android.Manifest.permission.ACCESS_FINE_LOCATION);

            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {
                // The Permissions to ask user.
                String[] permissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION};
                // Show a dialog asking the user to allow the above permissions.
                ActivityCompat.requestPermissions(Splash.this, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
            }

        }

        btn_panel = (Button) findViewById(R.id.buttonPanel);

        btn_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_panel = new Intent(Splash.this,Home.class);
                startActivity(intent_panel);
            }
        });

       /* TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent_ini = new Intent(Splash.this, Home.class);
                startActivity(intent_ini);
                finish();

            }
        };
        Timer timer = new Timer();
        timer.schedule(task, TIME_SPLASH_SCREEN);*/
    }
}
