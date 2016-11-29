package com.qburst.lekha.asynchronoustaskexample;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private double startTime = 0;
    private double finalTime = 0;
    public static int oneTimeOnly = 0;
    private int progressBarStatus = 0;
    public static final int progress_bar_type = 0;
    public static TelephonyManager tManager;
    private LinearLayout mediaLayout;
    private RelativeLayout seekLayout;
    private Button play;
    private Button pause;
    private Button stop;
    private TextView startText;
    private TextView endText;
    private SeekBar seekbar;
    static String networkType;
    public static EditText urlText;
    public static Button downloadButton;
    private MediaPlayer mediaPlayer;
    private Handler myHandler;
    private String initTime;
    private String startTextFormat;
    private String endTextFormat;
    private String urlString;
    private int imageNameIndex;
    private Handler progressBarHandler;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(MainActivity.this);
        init();
    }


    private void init() {

        initTime = getString(R.string.initial_time);
        myHandler = new Handler();
        progressBarHandler = new Handler();
        urlText = (EditText) findViewById(R.id.url_video);
        downloadButton = (Button) this.findViewById(R.id.download_button);
        mediaLayout = (LinearLayout) findViewById(R.id.buttons);
        seekLayout = (RelativeLayout) findViewById(R.id.seek);
        startText = (TextView) findViewById(R.id.textView1);
        endText = (TextView) findViewById(R.id.textView2);
        startText.setText(initTime);
        endText.setText(initTime);
        Toast.makeText(this, getConnectivityStatusString(this), Toast.LENGTH_LONG).show();
        if(getConnectivityStatus(this)!=TYPE_NOT_CONNECTED) {

            mediaLayout.setVisibility(View.GONE);
            seekLayout.setVisibility(View.GONE);
            downloadButton.setEnabled(true);
            urlText.setEnabled(true);
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadButton.setEnabled(false);
                    urlText.setEnabled(false);
                    urlString = urlText.getText().toString();
                    imageNameIndex = urlString.lastIndexOf('/');
                    urlString = urlString.substring(imageNameIndex);
                    File file = new File(Environment.getExternalStorageDirectory().toString() + urlString);
                    if (file.exists()) {
                        Toast.makeText(getApplicationContext(), "File already exist under SD card, playing Music", Toast.LENGTH_LONG).show();
                        playMusic(urlString);
                        //playVideo(urlString);

                    } else {
                        new DownloadFileFromURL().execute(urlText.getText().toString());
                    }

                }
            });
        }

    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
            WifiManager wifiManager = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                Integer linkSpeed = wifiInfo.getLinkSpeed(); //measured using WifiInfo.LINK_SPEED_UNITS
                networkType = WifiInfo.LINK_SPEED_UNITS;
                Toast.makeText(context, linkSpeed+""+networkType, Toast.LENGTH_LONG).show();
            }

            enable(true);
        } else if (conn == TYPE_MOBILE) {
            tManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            tManager.listen(new StrengthChange(context),
                    PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            status = "Mobile data enabled";
            enable(true);
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
            enable(false);
        }
        return status;
    }

    public static void enable(boolean state) {
        downloadButton.setEnabled(state);
        urlText.setEnabled(state);
    }


    private void playMusic(String mp3title) {
        // Read Mp3 file present under SD card
        Uri myUri1 = Uri.parse(Environment.getExternalStorageDirectory().toString() + mp3title);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {

            play = (Button) findViewById(R.id.button_play);
            pause = (Button) findViewById(R.id.button_pause);
            stop = (Button) findViewById(R.id.button_stop);
            seekbar = (SeekBar) findViewById(R.id.seekBar);
            seekbar.setClickable(false);
            pause.setEnabled(true);
            play.setEnabled(true);
            mediaPlayer.setDataSource(getApplicationContext(), myUri1);
            mediaPlayer.prepare();
            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();

            if (oneTimeOnly == 0) {
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;
            }
            endTextFormat = getEndTextFormat(finalTime);
            endText.setText(endTextFormat);
            startTextFormat = getStartTextFormat(startTime);
            startText.setText(startTextFormat);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Playing sound", Toast.LENGTH_SHORT).show();
                    mediaPlayer.start();
                    seekbar.setProgress((int) startTime);
                    myHandler.postDelayed(UpdateSongTime, 100);
                    pause.setEnabled(true);
                    play.setEnabled(false);
                    stop.setEnabled(true);
                }
            });

            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Pausing sound", Toast.LENGTH_SHORT).show();
                    mediaPlayer.pause();
                    pause.setEnabled(false);
                    play.setEnabled(true);
                    stop.setEnabled(true);
                }
            });

            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Stoping sound", Toast.LENGTH_SHORT).show();
                    mediaPlayer.stop();
                    startTime = 0;
                    finalTime = 0;
                    seekbar.setProgress(0);
                    startText.setText(initTime);
                    endText.setText(initTime);
                    pause.setEnabled(false);
                    play.setEnabled(false);
                    stop.setEnabled(false);
                    downloadButton.setEnabled(true);
                    urlText.setEnabled(true);
                    urlText.setText(null);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    // Once Music is completed playing, enable the button
                    downloadButton.setEnabled(true);
                    urlText.setEnabled(true);
                    urlText.setText(null);
                    startTime = 0;
                    finalTime = 0;
                    seekbar.setProgress(0);
                    stop.setEnabled(false);
                    pause.setEnabled(false);
                    startText.setText(initTime);
                    endText.setText(initTime);
                    Toast.makeText(getApplicationContext(), "Music completed playing", Toast.LENGTH_LONG).show();
                }
            });
        } catch (IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "URI cannot be accessed, permissed needed", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            Toast.makeText(getApplicationContext(), "Media Player is not in correct state", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "IO Error occured", Toast.LENGTH_LONG).show();
        }
    }

    private String getStartTextFormat(double startTime) {
        return String.format(Locale.getDefault(), "%d : %d",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)));
    }

    private String getEndTextFormat(double finalTime) {
        return String.format(Locale.getDefault(), "%d : %d",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)));
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            startTextFormat = getStartTextFormat(startTime);
            startText.setText(startTextFormat);
            seekbar.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;

            default:

                return null;
        }
    }


    public class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);

        }

        @Override
        protected String doInBackground(String... params) {
            int count;
            try {
                URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int lenghtOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(connection.getInputStream(), lenghtOfFile);
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + urlString);
                byte data[] = new byte[lenghtOfFile];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    progressBarStatus = (int) ((total * 100) / lenghtOfFile);
                    new Thread(new Runnable() {
                        public void run() {
                            progressBarHandler.post(new Runnable() {
                                public void run() {
                                    pDialog.setProgress(progressBarStatus);
                                }
                            });
                        }
                    });
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {
            dismissDialog(progress_bar_type);
            mediaLayout.setVisibility(View.VISIBLE);
            seekLayout.setVisibility(View.VISIBLE);
            playMusic(urlString);

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

