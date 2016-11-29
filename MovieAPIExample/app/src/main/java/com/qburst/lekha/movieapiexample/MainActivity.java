package com.qburst.lekha.movieapiexample;

import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity{

    private static final String LIST_TAG = "listing";
    private MovieList fragmentOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

        fragmentOne = (MovieList)getSupportFragmentManager().findFragmentByTag(LIST_TAG);
        if (fragmentOne == null) {
            fragmentOne = new MovieList();
            FragmentManager manager=getSupportFragmentManager();
            FragmentTransaction transaction= manager.beginTransaction();
            transaction.add(R.id.fragment_container, fragmentOne, LIST_TAG);
            transaction.commit();
        }

    }


}
