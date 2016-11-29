package com.qburst.lekha.handlerthreadexample2;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * Created by user on 18/10/16.
 */
public class MyWorkerThread extends HandlerThread {
    public static final String URL_KEY = "URL";
    private static final String TAG = MyWorkerThread.class.getSimpleName();
    private Handler mWorkerHandler;
    private Handler mResponseHandler;
    private Map<String, String> mRequestMap = new HashMap<String, String>();
    private Callback mCallback;
    Retrofit retrofit;

    public interface Callback {
        public void onImageDownloaded(String bitmap, int side);
    }

    public interface ServerAPI {
        @GET("{png_filename}")
        Call<ResponseBody> getbitmap(@Path("png_filename") String image_name);
    }

    public MyWorkerThread(Handler responseHandler, Callback callback) {
        super(TAG);
        mResponseHandler = responseHandler;
        mCallback = callback;
        retrofit =
                new Retrofit.Builder()
                        .baseUrl("http://developer.android.com/design/media/") // REMEMBER TO END with /
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

    }

    public void queueTask(String url, int side) {
        mRequestMap.put(URL_KEY, url);
        Log.i(TAG, url + " added to the queue");
        mWorkerHandler.obtainMessage(side)
                .sendToTarget();
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String side = msg.what == MainActivity.LEFT_SIDE ? "left side" : "right side";
                Log.i(TAG, String.format("Processing %s", side));
                String urlEnd = mRequestMap.get(URL_KEY);
                handleRequest(urlEnd, msg.what);
                return true;
            }
        });
    }

    private void handleRequest(final String end_point, final int side) {

        try {

            ServerAPI serverAPI = retrofit.create(ServerAPI.class);
            final Call<ResponseBody> responseBodyCall = serverAPI.getbitmap(end_point);
            responseBodyCall.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Log.d("DownloadImage", "Reading and writing file");
                        InputStream in = null;
                        FileOutputStream out = null;

                        try {
                            in = response.body().byteStream();
                            out = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/" + end_point);
                            int c;

                            while ((c = in.read()) != -1) {
                                out.write(c);
                            }
                        } catch (IOException e) {
                            Log.d("DownloadImage", e.toString());
                        } finally {
                            if (in != null) {
                                in.close();
                            }
                            if (out != null) {
                                out.close();
                            }
                        }
                        mResponseHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                final String file_url = Environment.getExternalStorageDirectory().toString() + "/" + end_point;
                                mCallback.onImageDownloaded(file_url, side);

                            }
                        });

                    } catch (IOException e) {
                        Log.d("DownloadImage", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onLooperPrepared() {
        mWorkerHandler = new Handler(getLooper());
    }


}

