package com.qburst.lekha.trainingproject;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    private static final int INIT_SCORE = 0;
    private static final int INIT_STATUS = 0;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = "HomeScreen";
    private static final String IMAGE_TAG = "IMAGE_ID_TAG";
    private static final int REQUEST_MICROPHONE = 1;
    public HomeScreen homeScreen;
    private PlayArea playScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        SuccessDatabase db = new SuccessDatabase(this);
        getApplicationContext().deleteDatabase("SuccessHandler");
        /*if(db.getCount() == 0) {
            Log.d("Insert: ", "Inserting ..");
            TypedArray imgs = getResources().obtainTypedArray(R.array.easy_image_ids);
            int i =0;
            int init = i;
            for (int j = 1; j < 4; j++ ) {

                for (; i<init + imgs.length()/3;i++) {
                    db.addData(i+1,INIT_SCORE,INIT_STATUS,j);
                    Log.d(TAG, "onCreate: imageId: "+(i+1));
                }
                i = init + imgs.length()/3;
                init = i;
            }

        }*/
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);

        }
        homeScreen = (HomeScreen) getSupportFragmentManager().findFragmentByTag(TAG);
        playScreen = (PlayArea) getSupportFragmentManager().findFragmentByTag(IMAGE_TAG);
        if (homeScreen == null) {
            homeScreen =  new HomeScreen();
            FragmentTransaction transaction= this.getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container,homeScreen, TAG);
            transaction.commit();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_MICROPHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
                    mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                            getContext().getPackageName());


                    SpeechRecognitionListener listener = new SpeechRecognitionListener();
                    mSpeechRecognizer.setRecognitionListener(listener);*/



                } else {

                    this.finish();
                    this.moveTaskToBack(true);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        playScreen = (PlayArea) getSupportFragmentManager().findFragmentByTag(IMAGE_TAG);
        if(homeScreen.isVisible()){
            super.onBackPressed();
        } /*else if (playScreen.isVisible()) {
            homeScreen =  new HomeScreen();
            FragmentTransaction transaction= this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container,homeScreen, TAG);
            transaction.commit();
        }*/ else {
            Log.d(TAG, "onBackPressed: Nothing");
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_action_bar,menu);
        MenuItem backItem = menu.findItem(R.id.back);
        if (homeScreen.isVisible()){
            backItem.setTitle("EXIT");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.back) {
            if (homeScreen.isVisible()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.exit)
                        .setTitle("EXIT");

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        finish();
                        moveTaskToBack(true);
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
            }/* else {
                if (playScreen instanceof PlayArea) {
                    playScreen.mSpeechRecognizer.destroy();
                    playScreen.countDownTimer.cancel();
                }

                homeScreen =  new HomeScreen();
                FragmentTransaction transaction= this.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,homeScreen, TAG);
                transaction.commit();
                return true;
            }*/
            /*else {
                homeScreen =  new HomeScreen();
                FragmentTransaction transaction= this.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,homeScreen, TAG);
                transaction.commit();
                return true;
            }
*/

        }
        return super.onOptionsItemSelected(item);
    }
}
