package com.qburst.lekha.trainingproject;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class HomeScreen extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATE_TAG = "STATE";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageView startButton;
    private LevelGrid levelGrid;
    private Bundle args;
    private int state;

    public HomeScreen() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        startButton = (ImageView) view.findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLevel();

            }
        });
        return view;

    }

    private void moveToLevel() {
        levelGrid = (LevelGrid) getFragmentManager().findFragmentByTag(STATE_TAG);
        if (levelGrid == null) {
            levelGrid = new LevelGrid();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, levelGrid, STATE_TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void callParentMethod(){
        getActivity().onBackPressed();
    }

}
