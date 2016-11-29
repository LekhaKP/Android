package com.qburst.lekha.celebguess;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String IMAGE_TAG = "IMAGE_ID_TAG";
    private static final String TAG = "HomeScreen";

    private HomeScreen homeScreen;
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
        playScreen = (PlayArea) getSupportFragmentManager().findFragmentByTag(IMAGE_TAG);
        if(homeScreen.isVisible()){
            super.onBackPressed();
        } else if (playScreen.isVisible()) {
            super.onBackPressed();
        } else {
            Log.d(TAG, "onBackPressed: Nothing");
        }

    }
}
