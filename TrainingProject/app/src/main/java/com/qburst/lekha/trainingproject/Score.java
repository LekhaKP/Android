package com.qburst.lekha.trainingproject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class Score extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String SUCCESS_TAG = "Success";
    private static final String STATE_TAG = "STATE";
    private static final String IMAGE_TAG = "IMAGE_ID_TAG";
    private static final String ANSWER_TAG = "IMAGE_NAME";
    private static final String TAG = "Tag";
    private static final int SUCCESS = 1;
    private static final int FAIL = -1;

    private PlayArea playArea;
    private int state;
    private HomeScreen homeScreen;
    private int imageID;
    private ImageView celebrityImage;
    private DisplayMetrics displaymetrics;
    private int height;
    private int width;
    private int reduce;
    private ViewGroup.LayoutParams layoutParams;
    private String imageName;
    private ImageView successStateFace;
    private TextView scoreView;
    private String[] scoreValues;


    public Score() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    // TODO: Rename and change types and number of parameters
    public static Score newInstance(String param1, String param2) {
        Score fragment = new Score();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_score, container, false);
        final SuccessDatabase db = new SuccessDatabase(getActivity());
        Bundle bundle = this.getArguments();
        int currentScoreValue = bundle.getInt(SUCCESS_TAG);
        scoreValues = getActivity().getResources().getStringArray(R.array.show_score);
        state = bundle.getInt(STATE_TAG);
        imageID = bundle.getInt(IMAGE_TAG);
        InputStream ims = null;
        try {

            ims = getActivity().getAssets().open( "images/" + (imageID)+".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable d = Drawable.createFromStream(ims, null);
        imageName = bundle.getString(ANSWER_TAG);
        scoreView = (TextView) view.findViewById(R.id.score_view);
        successStateFace = (ImageView) view.findViewById(R.id.success_state_face);
        celebrityImage = (ImageView) view.findViewById(R.id.celebrity_image);
        celebrityImage.setImageDrawable(d);
        displaymetrics = new DisplayMetrics();
        (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        reduce = (int) getResources().getDimension(R.dimen.reduce_padding);
        layoutParams = celebrityImage.getLayoutParams();
        layoutParams.height = (height-reduce)/2;
        celebrityImage.setLayoutParams(layoutParams);
        TextView statusTextView = (TextView) view.findViewById(R.id.success_state_text);
        Button next = (Button) view.findViewById(R.id.next_game);
        if (currentScoreValue > 0) {
            statusTextView.setText("Yipeee! \n Its " + imageName);
            db.updateData(imageID,currentScoreValue, SUCCESS);
            successStateFace.setImageResource(R.mipmap.face_success);
            scoreView.setText(scoreValues[currentScoreValue]);
            next.setText("NEXT");
            Resources res = getContext().getResources();
            String[] levelNames = getContext().getResources().getStringArray(R.array.show_score);

//            scoreView.setImageResource(res.getIdentifier(levelNames[scoreValue-1],"drawable","com.qburst.lekha.trainingproject"));
        } else {
            statusTextView.setText("Naaa! \n Its " + imageName);
            db.updateData(imageID,currentScoreValue, FAIL);
            successStateFace.setImageResource(R.mipmap.face_failure);
            scoreView.setText(scoreValues[0]);
            next.setText("RETRY");
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*playArea = (PlayArea) getFragmentManager().findFragmentByTag(STATE_TAG);
                if (playArea == null) {*/
                if (db.getUnFinishedCount(state) == 0) {
                    Toast.makeText(getActivity(),R.string.completed,Toast.LENGTH_SHORT).show();
                    gotoHomeScreen();
                } else {
                    playArea = new PlayArea();
                    Bundle bundle = new Bundle();
                    bundle.putInt(STATE_TAG, state);
                    playArea.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, playArea, STATE_TAG);
                    transaction.commit();
                }

            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.back);
        item.setTitle("HOME");
        item.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.back) {
            gotoHomeScreen();
            return  true;
        }
        return super.onOptionsItemSelected(item) ;
    }

    private void gotoHomeScreen() {
        homeScreen =  new HomeScreen();
        FragmentTransaction transaction= this.getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,homeScreen, TAG);
        transaction.commit();
    }


}
