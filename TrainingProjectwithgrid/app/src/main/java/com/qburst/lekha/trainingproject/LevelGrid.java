package com.qburst.lekha.trainingproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.qburst.lekha.trainingproject.Adapter.GridAdapter;
import com.qburst.lekha.trainingproject.Database.LevelHandler;


public class LevelGrid extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String STATE_TAG = "STATE";
    private static final int LOCKED = 1;
    private static final int UNLOCKED = 2;
    private static final int PLAYED = 3;
    private static final String IMAGE_TAG = "IMAGE_ID_TAG";

    private GridView levelGridView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LevelHandler db;
    private ImageData imageData;
    private PlayArea playArea;

    public LevelGrid() {
        // Required empty public constructor
    }

    public static LevelGrid newInstance(String param1, String param2) {
        LevelGrid fragment = new LevelGrid();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_level_grid, container, false);
        levelGridView = (GridView) view.findViewById(R.id.gridview);
        levelGridView.setAdapter(new GridAdapter(getActivity()));

        levelGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                db = new LevelHandler(getContext());
                imageData = new ImageData();
                imageData = db.getLevelInfo(position+1);
                if (imageData.getStatus() == LOCKED) {
                    Toast.makeText(getActivity(), "Locked",
                            Toast.LENGTH_SHORT).show();
                } else if (imageData.getStatus() == UNLOCKED) {
                    moveToPlayArea(position);
                }

            }
        });

        return view;
    }

    private void moveToPlayArea(int position) {
        playArea = (PlayArea) getFragmentManager().findFragmentByTag(IMAGE_TAG);
        if (playArea == null) {
            playArea = new PlayArea();
            Bundle args = new Bundle();
            args.putInt(IMAGE_TAG,imageData.getImageId());
            playArea.setArguments(args);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, playArea, IMAGE_TAG);
            transaction.commit();
        }
    }

    public void callParentMethod(){
        getActivity().onBackPressed();
    }

}
