package com.qburst.lekha.trainingproject;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class HomeScreen extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DIFFICULT_TAG = "DIFFICULT";
    private static final String EASY_TAG = "EASY";
    private static final String MEDIUM_TAG = "MEDIUM";
    private static final String STATE_TAG = "STATE";

    private int EASY = 1;
    private int MEDIUM = 2;
    private int DIFFICULT = 3;
    private PlayArea playArea;
    private Button easyButton;
    private Button mediumButton;
    private Button difficultButton;
    private ImageView exitButton;
    private ImageView maskImageView;
    private int angle = -45;
    private Menu menu;


    public HomeScreen() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        final SuccessDatabase db = new SuccessDatabase(getActivity());
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Rosemary.ttf");
        easyButton = (Button) view.findViewById(R.id.easy_button);
        easyButton.setTypeface(tf);
        mediumButton = (Button) view.findViewById(R.id.medium_button);
        mediumButton.setTypeface(tf);
        difficultButton = (Button) view.findViewById(R.id.difficult_button);
        difficultButton.setTypeface(tf);
        Log.d(EASY_TAG, "onClick: count:"+db.getUnFinishedCount(EASY));
        Log.d(MEDIUM_TAG, "onClick: count:"+db.getUnFinishedCount(MEDIUM));
        Log.d(DIFFICULT_TAG, "onClick: count:"+db.getUnFinishedCount(DIFFICULT));
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.getUnFinishedCount(EASY) == 0) {
                    Toast.makeText(getActivity(),R.string.completed,Toast.LENGTH_SHORT).show();
                } else {
                    moveToPlayArea(EASY);
                }
            }
        });
        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.getUnFinishedCount(MEDIUM) == 0) {
                    Toast.makeText(getActivity(),R.string.completed,Toast.LENGTH_SHORT).show();
                } else {
                    moveToPlayArea(MEDIUM);
                }

            }
        });
        difficultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.getUnFinishedCount(DIFFICULT) == 0) {
                    Toast.makeText(getActivity(),R.string.completed,Toast.LENGTH_SHORT).show();
                } else {
                    moveToPlayArea(DIFFICULT);
                }

            }
        });
        return view;

    }

    private void moveToPlayArea(int state) {
        /*playArea = (PlayArea) getFragmentManager().findFragmentByTag(STATE_TAG);
        if (playArea == null) {*/
            playArea = new PlayArea();
            Bundle bundle = new Bundle();
            bundle.putInt(STATE_TAG, state);
            playArea.setArguments(bundle);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, playArea, STATE_TAG);
            transaction.addToBackStack(null);
            transaction.commit();
//        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateMenuTitles();
    }

    private void updateMenuTitles() {
        MenuItem menuItem = menu.findItem(R.id.back);
        menuItem .setTitle("EXIT");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.exit)
                .setTitle("EXIT");

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                getActivity().finish();
                getActivity().moveTaskToBack(true);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
        
    }

    public void callParentMethod(){
        getActivity().onBackPressed();
    }

}
