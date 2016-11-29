package com.qburst.lekha.celebguess;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private static final int INIT_SCORE = 0;
    private static final int LOCKED = 1;
    private static final int UNLOCKED = 2;
    private static final int PLAYED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
