package com.qburst.lekha.cameraexample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.FloatProperty;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

import static android.graphics.PixelFormat.TRANSLUCENT;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private boolean isPreviewRunning = false;
    private boolean mExternalStorageAvailable = false;
    private boolean mExternalStorageWriteable = false;
    private boolean drawOK = false;
    private int front = 0;
    private Camera.Size bestSize = null;
    private SurfaceView surfaceView;
    private SurfaceView topSurfaceView;
    private SurfaceHolder surfaceHolder;
    private SurfaceHolder topSurfaceHolder;
    private Camera camera;
    private Camera.AutoFocusCallback myAutoFocusCallback;
    private Camera.PictureCallback jpegCallback;
    private Paint paint;
    private Canvas canvas;
    private Button capture;
    private Bitmap bitmap;
    private boolean meteringAreaSupported;
    private File outputFile;
    private Button chooseCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int id = getSurfaceId(holder);
        if (id < 0) {
            Log.w("TAG", "surfaceCreated UNKNOWN holder=" + holder);
        } else {
            Log.d("TAG", "surfaceCreated #" + id + " holder=" + holder);

        }
            try {
                camera = Camera.open();
            }

            catch (RuntimeException e) {
                System.err.println(e);
                return;
            }
        Camera.Parameters param;
        param = camera.getParameters();
        param.setPreviewFrameRate(100);
        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
        bestSize = sizeList.get(0);

        for(int i = 1; i < sizeList.size(); i++){
            if((sizeList.get(i).width * sizeList.get(i).height) >
                    (bestSize.width * bestSize.height)){
                bestSize = sizeList.get(i);
            }
        }

        param.setPreviewSize(bestSize.width, bestSize.height);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            Log.e("TAG", "init_camera: " + e);
            return;
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (isPreviewRunning)
        {
            camera.stopPreview();
        }
        Camera.Parameters p = camera.getParameters();
        if (p.getMaxNumMeteringAreas() > 0) {
            this.meteringAreaSupported = true;
        }
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
        setPreview();

    }

    private void setPreview() {
        initializeSurface();
        Camera.Parameters parameters = camera.getParameters();
        bestSize = camera.getParameters().getPreviewSize();
        parameters.setPreviewSize(bestSize.width, bestSize.height);
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0)
        {

            camera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_270)
        {
            camera.setDisplayOrientation(180);
        }
        camera.setParameters(parameters);
        previewCamera();
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                surfaceView.setFocusable(false);
                surfaceView.setClickable(false);
                doTouchFocus(event);
                return true;
            }
        });

        jpegCallback = new android.hardware.Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                storePicture(data, camera);
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private int getSurfaceId(SurfaceHolder holder) {
        if (holder.equals(surfaceView.getHolder())) {
            return 1;
        } else if (holder.equals(topSurfaceView.getHolder())) {
            return 2;
        } else {
            return -1;
        }
    }

    public void previewCamera()
    {
        try
        {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            isPreviewRunning = true;
        }
        catch(Exception e)
        {
            Log.d("APP_CLASS", "Cannot start preview", e);
        }
    }

    private void startCamera() {

        try{
            camera = Camera.open(front);
        }catch(RuntimeException e){
            Log.e("TAG", "init_camera: " + e);
            return;
        }

        capture = (Button) findViewById(R.id.capture_button);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    captureImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        chooseCamera = (Button) findViewById(R.id.choose_camera);
        if (Camera.getNumberOfCameras() == 2) {

            chooseCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    camera.release();
                    switch (front) {
                        case 0: front=1;
                            break;
                        case 1:front = 0;
                            break;
                        default: front = 1;
                    }
                    camera = Camera.open(front);
                    setPreview();


                }
            });
        }
        else {
            chooseCamera.setVisibility(View.GONE);
        }


    }

    private void initializeSurface() {

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        topSurfaceView = (SurfaceView) findViewById(R.id.surfaceView2);
        topSurfaceView.setZOrderMediaOverlay(true);
        topSurfaceHolder = topSurfaceView.getHolder();
        topSurfaceHolder.addCallback(this);
        topSurfaceHolder.setFormat(TRANSLUCENT);

    }

    private void doTouchFocus(MotionEvent event) {
        final Surface topSurface = topSurfaceHolder.getSurface();
        canvas = null;
        paint = new Paint();
        paint.setColor(0xeed7d7d7);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        if (camera != null) {

            camera.cancelAutoFocus();
            Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);
            Rect meteringRect = calculateTapArea(event.getX(), event.getY(), 1.5f);
            List<Camera.Area> focusList = new ArrayList<Camera.Area>();
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            focusList.add(new Camera.Area(focusRect, 1000));
            parameters.setFocusAreas(focusList);
            try {
                topSurfaceView.setVisibility(View.VISIBLE);
                canvas = topSurface.lockCanvas(focusRect);
                canvas.drawRect(focusRect,paint);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        topSurfaceView.setVisibility(View.GONE);
                    }
                },300);
            } finally {
                topSurface.unlockCanvasAndPost(canvas);
            }
            if (meteringAreaSupported) {
                focusList.remove(0);
                focusList.add(new Camera.Area(meteringRect, 1000));
                parameters.setMeteringAreas(focusList);
            }

            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        camera.cancelAutoFocus();
                        topSurfaceView.setVisibility(View.GONE);
                        surfaceView.setFocusable(true);
                        surfaceView.setClickable(true);
                    }
                }
            });
        }


    }

    private void storePicture(byte[] data, Camera camera) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        options.inSampleSize = 5;
        bitmap=BitmapFactory.decodeByteArray(data, 0, data.length, options);
        Matrix m = new Matrix();
        if (front == 0) {
            m.postRotate(90);
        }else {
            m.postRotate(-90);
        }

        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Log.d("TAG", "storePicture: "+dateFormat.format(Calendar.getInstance().getTime()));

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        if (mExternalStorageAvailable && mExternalStorageWriteable) {
            outputFile = new File(Environment.getExternalStorageDirectory(), "photo_" + dateFormat.format(Calendar.getInstance().getTime()) + ".jpeg");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outputFile)));
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                bitmap.recycle();

                fileOutputStream.flush();
                fileOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(getApplicationContext(), "Picture Saved", Toast.LENGTH_SHORT).show();
            refreshCamera();
        }
        else {
            Toast.makeText(this, "Cannot save", Toast.LENGTH_LONG).show();
        }
    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        }

        catch (Exception e) {
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch (Exception e) {
        }
    }

    public void captureImage() throws IOException {
        camera.takePicture(null, null, jpegCallback);
    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
        Matrix matrix = new Matrix();
        Float focusAreaSize = 100f;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int left = clamp((int) x - areaSize / 2, 0, surfaceView.getWidth() - areaSize);
        int top = clamp((int) y - areaSize / 2, 0, surfaceView.getHeight() - areaSize);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        matrix.mapRect(rectF);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }
}
