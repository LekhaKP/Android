package com.qburst.lekha.handlerthreadexample;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by user on 18/10/16.
 */

public class MyWorkerThread extends HandlerThread {
    private Handler mWorkerHandler;
    public MyWorkerThread(String name) {
        super(name);
    }

    public MyWorkerThread(String name, int priority) {
        super(name, priority);
    }

    public void postTask(Runnable task){
        mWorkerHandler.post(task);
    }

    public void prepareHandler(){
        mWorkerHandler = new Handler(getLooper());
    }
}
