package com.qburst.lekha.celebguess;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HomeScreen extends Fragment {
    private static final String IMAGE_TAG = "IMAGE_ID_TAG";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageView startButton;
    private Bundle args;
    private int state;
    private PlayArea playArea;

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
                moveToPlayArea();

            }
        });
        return view;

    }

    private void moveToPlayArea() {
        playArea = (PlayArea) getFragmentManager().findFragmentByTag(IMAGE_TAG);
        if (playArea == null) {
            playArea = new PlayArea();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, playArea, IMAGE_TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void callParentMethod(){
        getActivity().onBackPressed();
    }

}
