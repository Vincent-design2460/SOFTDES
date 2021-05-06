package com.example.softdeslogin.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.softdeslogin.R;
import com.example.softdeslogin.view.MainMenuActivity;

public class MainMenuActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(MainMenuActivity.this, SplashActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);



    }
}
