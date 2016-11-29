package com.qburst.lekha.trainingproject;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.qburst.lekha.trainingproject.Database.LevelHandler;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private static final int INIT_SCORE = 0;
    private static final int LOCKED = 1;
    private static final int UNLOCKED = 2;
    private static final int PLAYED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LevelHandler db = new LevelHandler(this);
//        getApplicationContext().deleteDatabase("levelManager");
        if(db.getLevelCount() == 0) {
            Log.d("Insert: ", "Inserting ..");
            db.addLevelInfo(new ImageData(R.drawable.aiswarya_roy, INIT_SCORE, UNLOCKED, 1));
            db.addLevelInfo(new ImageData(R.drawable.anil_kapoor, INIT_SCORE, LOCKED, 2));
            db.addLevelInfo(new ImageData(R.drawable.dharmendra, INIT_SCORE, LOCKED, 3));
            db.addLevelInfo(new ImageData(R.drawable.madhuri_dixit,INIT_SCORE,LOCKED, 4));
            db.addLevelInfo(new ImageData(R.drawable.raj_kapoor, INIT_SCORE,LOCKED, 5));
            db.addLevelInfo(new ImageData(R.drawable.shah_rukh_khan,INIT_SCORE, LOCKED, 6));
        }

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
