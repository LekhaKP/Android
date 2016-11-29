package com.qburst.lekha.handlerthreadexample;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private Handler mUiHandler = new Handler();
    private MyWorkerThread mWorkerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWorkerThread = new MyWorkerThread("myWorkerThread");
        Runnable task = new Runnable() {
          //  Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 4; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i == 2) {
                        mUiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,
                                        "I am at the middle of background task",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                    }
                }
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                "Background task is completed",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        }
        //)
        ;
        //myThread.start();

        mWorkerThread.start();
        mWorkerThread.prepareHandler();
        mWorkerThread.postTask(task);
        mWorkerThread.postTask(task);

    }

    @Override
    protected void onDestroy() {
        mWorkerThread.quit();
        super.onDestroy();
    }



}
