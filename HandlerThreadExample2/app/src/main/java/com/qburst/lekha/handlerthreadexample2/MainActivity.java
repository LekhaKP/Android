package com.qburst.lekha.handlerthreadexample2;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements MyWorkerThread.Callback {

    public static final int LEFT_SIDE = 0;
    public static final int RIGHT_SIDE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static boolean isVisible;
    private int count = 0;
    private String[] urls;
    private Random random;
    private LinearLayout mLeftSideLayout;
    private LinearLayout mRightSideLayout;
    private MyWorkerThread mWorkerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    void init() {
        isVisible = true;
        mLeftSideLayout = (LinearLayout) findViewById(R.id.leftSideLayout);
        mRightSideLayout = (LinearLayout) findViewById(R.id.rightSideLayout);
        urls = new String[]{"principles_delight.png",
                "principles_real_objects.png",
                "principles_make_it_mine.png",
                "principles_get_to_know_me.png"};
        verifyStoragePermissions(this);
        mWorkerThread = new MyWorkerThread(new Handler(), this);
        mWorkerThread.start();
        mWorkerThread.prepareHandler();
        random = new Random();
        mWorkerThread.queueTask(urls[count], random.nextInt(2));
    }

    @Override
    protected void onPause() {
        isVisible = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWorkerThread.quit();
        super.onDestroy();
    }

    @Override
    public void onImageDownloaded(String bitmap, int side) {
        ImageView imageView;
        count++;
        imageView = new ImageView(MainActivity.this);
        imageView.setMinimumHeight(200);
        imageView.setMaxWidth(1000);
        imageView.setImageDrawable(Drawable.createFromPath(bitmap));
        if (isVisible && side == LEFT_SIDE){
            mLeftSideLayout.addView(imageView);
        } else if (isVisible && side == RIGHT_SIDE){
            mRightSideLayout.addView(imageView);
        }
        if(count<4) {
            mWorkerThread.queueTask(urls[count], random.nextInt(2));
        }


    }
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
