package com.qburst.lekha.trainingproject;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HomeScreen";
    private static final String STATE_TAG = "STATE";
    private HomeScreen homeScreen;
    private LevelGrid levelScreen;
    private PlayArea playScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeScreen = (HomeScreen) getSupportFragmentManager().findFragmentByTag(TAG);

        if (homeScreen == null) {
            homeScreen =  new HomeScreen();
            FragmentTransaction transaction= this.getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container,homeScreen, TAG);
            transaction.commit();
        }

    }
    @Override
    public void onBackPressed() {
        levelScreen = (LevelGrid) getSupportFragmentManager().findFragmentByTag(STATE_TAG);
        if(homeScreen.isVisible()){
            super.onBackPressed();
        } else if (levelScreen.isVisible()) {
            super.onBackPressed();
        } else {
            Log.d(TAG, "onBackPressed: Nothing");
        }

    }
}
