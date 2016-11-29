package com.qburst.lekha.trainingproject;

import android.*;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static java.util.Arrays.fill;


public class PlayArea extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "HomeScreen";
    private static final String TAG2 = "Tag2";
    private static final String IMAGE_TAG = "IMAGE_ID_TAG";
    private static final String STATE_TAG = "STATE";
    private static final String SUCCESS_TAG = "Success";
    private static final String ANSWER_TAG = "IMAGE_NAME";
    private static final int TIMER_LENGTH = 30;


    // TODO: Rename and change types of parameters

    public SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private ImageView celebImage;
    private TextView chancesTextView;
    private TextView dialogView;
    private ImageView facesView;
    private ImageView speakButton;
    private ProgressBar indicator;
    private Menu menu;
    private TypedArray imgs;
    private Random rand;
    private boolean isTimeOut = false;
    private int score = 5;
    private int mode = 0;
    private int trials = 3;
    private int next = 0;
    private int rndInt;
    private int resID;
    private int height;
    private int width;
    private int leftx;
    private int topy;
    private int rightx;
    private int bottomy;
    private int reduce;
    private int permissionCheck;
    private int state;
    private float left;
    private float top;
    private float right;
    private float bottom;
    private String answer;
    private String[] stringValues;
    private int[] isRevealed;
    private String[] trialNumbers;
    private String[] chanceDialogs;
    private String[] chanceFaces;
    private String[] chanceMikes;
    private int[] landRevealed;
    private Frame frame;
    private Face face;
    private SparseArray<Face> faces;
    private List<Landmark> landmarks;
    private List<LandmarkData> landmarksObtained;
    private Rect rectanglePart;
    private List<Rect> parts;
    private int maxArea = 0;
    private int randMatrixId;
    private int[] isFilled;
    private Resources res;
    private DisplayMetrics displaymetrics;
    private ViewGroup.LayoutParams layoutParams;
    private Canvas tempCanvas;
    private Bitmap myBitmap;
    private Paint myRectPaint;
    private int landWidth;
    private int landHeight;
    private PointF leftMouth;
    private PointF rightMouth;
    private PointF baseMouth;
    private PointF leftEye;
    private PointF rightEye;
    private PointF noseBase;
    private PointF leftEarTip;
    private PointF leftEar;
    private PointF rightEarTip;
    private PointF rightEar;
    private PointF leftCheek;
    private PointF rightCheek;
    private PointF cellLeftTop;
    private PointF cellRightBottom;
    private PointF facePosition;
    private ArrayList<String> matches;
    private Circle circle;
    public CountDownTimer countDownTimer;
    private Thread myThread;
    private Score scoreFragment;
    private HomeScreen homeScreen;
    private int[][] imagePointArray;
    private int isSuccess = 0;
    private int randLandmark;
    private int cellsPerRow;

    public PlayArea() {
        setHasOptionsMenu(true);
    }

    public static PlayArea newInstance(String param1, String param2) {
        PlayArea fragment = new PlayArea();
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
        View view = inflater.inflate(R.layout.fragment_play_area, container, false);
        landmarksObtained = new ArrayList<>();
        cellLeftTop = new PointF();
        cellRightBottom = new PointF();
        trialNumbers = getActivity().getResources().getStringArray(R.array.chances);
        chanceDialogs = getActivity().getResources().getStringArray(R.array.chance_dialogs);
        chanceFaces = getActivity().getResources().getStringArray(R.array.chance_faces);
        chanceMikes = getActivity().getResources().getStringArray(R.array.chance_mike);
        res = getContext().getResources();
        Bundle bundle = this.getArguments();
        state = bundle.getInt(STATE_TAG);
        isRevealed = new int[12];
        landRevealed = new int[12];
        parts = new ArrayList<>();
        matches = new ArrayList<String>();
        matches.add("Nothing");
        circle = (Circle) view.findViewById(R.id.circle);
        indicator = (ProgressBar) view.findViewById(R.id.revealing_progress);
        indicator.setVisibility(View.VISIBLE);
        celebImage = (ImageView) view.findViewById(R.id.image_puzzle);
        celebImage.setVisibility(View.INVISIBLE);
        chancesTextView = (TextView) view.findViewById(R.id.chances_text_view);
        chancesTextView.setText(trialNumbers[trials - 1] + " chances");
        dialogView = (TextView) view.findViewById(R.id.dialog_view);
        dialogView.setText(chanceDialogs[trials - 1]);
        facesView = (ImageView) view.findViewById(R.id.faces_view);
        facesView.setImageResource(res.getIdentifier(chanceFaces[trials - 1],"mipmap","com.qburst.lekha.trainingproject"));
        speakButton = (ImageView) view.findViewById(R.id.speak_button);
        speakButton.setImageResource(res.getIdentifier(chanceMikes[trials - 1],"mipmap","com.qburst.lekha.trainingproject"));
        displaymetrics = new DisplayMetrics();
        (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        reduce = (int) getResources().getDimension(R.dimen.reduce_padding);
        imgs = getResources().obtainTypedArray(R.array.easy_image_ids);

        permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO);
        Log.d(TAG, "onCreate: "+permissionCheck);
        if (permissionCheck == 0) {
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
            mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    getContext().getPackageName());


            SpeechRecognitionListener listener = new SpeechRecognitionListener();
            mSpeechRecognizer.setRecognitionListener(listener);
        }


        SuccessDatabase db = new SuccessDatabase(getActivity());

            int min;
            if (state == 1) {
                mode = 1;
                min = 0;
            }else if (state == 2) {
                mode = 2;
                min = imgs.length()/3;
            } else {
                mode = 3;
                min = 2*imgs.length()/3;
            }

            InputStream ims = null;
            /*do {
                rand = new Random();
                rndInt = (rand.nextInt(imgs.length()/3))+min;
                resID = imgs.getResourceId(rndInt, 0);
            }while(db.checkStatus(resID)!=0);*/
            do {
                rand = new Random();
                rndInt = (rand.nextInt(imgs.length()/3))+min;
                Log.d(TAG, "onCreateView: rndInt"+(rndInt+1));
                try {
                    ims = getActivity().getAssets().open( "images/" + (rndInt+1)+".png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }while (db.checkStatus(rndInt+1)!=0);



//        resID = res.getIdentifier("arjun_kapoor_three", "drawable", "com.qburst.lekha.trainingproject");
//            Log.d(TAG, "onCreateView: resID"+resID);
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                layoutParams = celebImage.getLayoutParams();
                layoutParams.width = (width-reduce)/2;
                celebImage.setLayoutParams(layoutParams);
            }
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                layoutParams = celebImage.getLayoutParams();
                layoutParams.height = (height-reduce)/2;
                celebImage.setLayoutParams(layoutParams);
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable=true;
            /*myBitmap = BitmapFactory.decodeResource(
                    view.getResources(),
                    resID,
                    options);*/
            myBitmap = BitmapFactory.decodeStream(ims);
            myRectPaint = new Paint();
            myRectPaint.setStrokeWidth(5);
            myRectPaint.setColor(Color.parseColor("#4A335D"));
            myRectPaint.setStyle(Paint.Style.STROKE);
            final Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
            tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawBitmap(myBitmap, 0, 0, null);
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    initReveal(tempBitmap);
                }
            });

            if (permissionCheck == 0) {
                myThread.start();
            }

            countDownTimer = new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    isTimeOut = true;
                    circle.stop();
                    Log.d(TAG, "onFinish: finshed");
                    checkAnswer();

                }
            };
            countDownTimer.start();
            Log.d(TAG, "onCreateView: height: "+celebImage.getHeight()+" width: "+celebImage.getWidth());
        return view;
    }

    private void initReveal(final Bitmap bitmap) {

        FaceDetector faceDetector = new FaceDetector.Builder(getContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        if(!faceDetector.isOperational()){
            new AlertDialog.Builder(getContext()).setMessage("Could not set up the face detector!").show();
            return ;
        }
        frame = new Frame.Builder().setBitmap(myBitmap).build();
        faces = faceDetector.detect(frame);
        for (int j=0;j<faces.size();j++) {
            face = faces.valueAt(j);
            facePosition = face.getPosition();
            leftx = (int) (facePosition.x);
            topy = (int) (facePosition.y);
            rightx = (int)(leftx + face.getWidth());
            bottomy = (int)(topy + face.getHeight());
            landmarks = face.getLandmarks();
            for (Landmark land:
                    landmarks) {
                Log.d(TAG, "initReveal: "+ land.getType());
            }
            if (trials == 3) {
                getLandmarkPosition();
                for (int i = 0; i < landmarksObtained.size(); i++) {
                    Log.d(TAG, "initReveal: "+landmarksObtained.get(i).getType()+ " position: "+landmarksObtained.get(i).getLeftTop());
                }
                getLandmarkArea();
            }
            drawMatrix();
            isFilled = new int[parts.size()];
            fill(isFilled,1);
            int cellIndex = 0;
            for (Rect cell: parts
                    ) {
                cellLeftTop.x = cell.left;
                cellLeftTop.y = cell.top;
                cellRightBottom.x = cell.right;
                cellRightBottom.y = cell.bottom;

                while (landmarksObtained.get(next).getType() == Landmark.LEFT_CHEEK || landmarksObtained.get(next).getType() == Landmark.RIGHT_CHEEK || landmarksObtained.get(next).getType() == Landmark.LEFT_MOUTH || landmarksObtained.get(next).getType() == Landmark.RIGHT_MOUTH) {
                    next++;
                }
//                tempCanvas.drawRect(landmarksObtained.get(next).getLeftTop().x,landmarksObtained.get(next).getLeftTop().y,landmarksObtained.get(next).getRightBottom().x,landmarksObtained.get(next).getRightBottom().y,myRectPaint);
                maxArea = landmarksObtained.get(next).getArea();
                if (doOverlap(landmarksObtained.get(next).getLeftTop(),landmarksObtained.get(next).getRightBottom(),cellLeftTop,cellRightBottom)) {
                    isFilled[cellIndex] = 0;
                }
                landRevealed[landmarksObtained.get(next).getType()] = 1;
                cellIndex++;
            }

            myRectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            for (int part = 0;part<parts.size();part++) {
                if (isFilled[part] == 1) {
                    tempCanvas.drawRect(parts.get(part),myRectPaint);
                }
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    celebImage.setImageDrawable(new BitmapDrawable(getResources(),bitmap));
                    celebImage.setVisibility(View.VISIBLE);
                    indicator.setVisibility(View.INVISIBLE);
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    circle.start(TIMER_LENGTH);

                }
            });
            faceDetector.release();

        }

    }

    public void getLandmarkPosition() {

        for (int landmarkIndex = 0; landmarkIndex<landmarks.size();landmarkIndex++ ) {
            Landmark landmark = landmarks.get(landmarkIndex);
            switch (landmark.getType()) {
                case Landmark.BOTTOM_MOUTH: baseMouth = landmark.getPosition();
                    landmarksObtained.add( new LandmarkData(Landmark.BOTTOM_MOUTH,baseMouth));
                    if (baseMouth == null) {
                        landRevealed[Landmark.BOTTOM_MOUTH] = -1;
                    } else {
                        landRevealed[Landmark.BOTTOM_MOUTH] = 0;
                    }
                    break;
                case Landmark.LEFT_CHEEK:
                    leftCheek = landmark.getPosition();
                    landmarksObtained.add( new LandmarkData(Landmark.LEFT_CHEEK,leftCheek));
                    landRevealed[Landmark.LEFT_CHEEK] = -1;
                    break;
                case Landmark.LEFT_EAR_TIP: leftEarTip =  landmark.getPosition();
                    landmarksObtained.add( new LandmarkData(Landmark.LEFT_EAR_TIP,leftEarTip));
                    if (leftEarTip == null) {
                        landRevealed[Landmark.LEFT_EAR_TIP] = -1;
                    } else {
                        landRevealed[Landmark.LEFT_EAR_TIP] = 0;
                    }
                    break;
                case Landmark.LEFT_EAR: leftEar = landmark.getPosition();
                    landmarksObtained.add( new LandmarkData(Landmark.LEFT_EAR,leftEar));
                    if (leftEar == null) {
                        landRevealed[Landmark.LEFT_EAR] = -1;
                    } else {
                        landRevealed[Landmark.LEFT_EAR] = 0;
                    }
                    break;
                case Landmark.LEFT_EYE: leftEye =  landmark.getPosition();
                    landmarksObtained.add( new LandmarkData(Landmark.LEFT_EYE,leftEye));
                    if (leftEye == null) {
                        landRevealed[Landmark.LEFT_EYE] = -1;
                    } else {
                        landRevealed[Landmark.LEFT_EYE] = 0;
                    }
                    break;
                case Landmark.LEFT_MOUTH:
                    leftMouth = landmark.getPosition();
                    landmarksObtained.add( new LandmarkData(Landmark.LEFT_MOUTH,leftMouth));
                    if (leftMouth == null) {
                        landRevealed[Landmark.LEFT_MOUTH] = -1;
                    } else {
                        landRevealed[Landmark.LEFT_MOUTH] = 0;
                    }
                    break;
                case Landmark.NOSE_BASE:
                    noseBase =  landmark.getPosition();
                    landmarksObtained.add( new LandmarkData(Landmark.NOSE_BASE,noseBase));
                    if (noseBase == null) {
                        landRevealed[Landmark.NOSE_BASE] = -1;
                    } else {
                        landRevealed[Landmark.NOSE_BASE] = 0;
                    }
                    break;
                case Landmark.RIGHT_CHEEK:
                    rightCheek = landmark.getPosition();
                    landmarksObtained.add( new LandmarkData(Landmark.RIGHT_CHEEK,rightCheek));
                    landRevealed[Landmark.RIGHT_CHEEK] = -1;
                    break;
                case Landmark.RIGHT_EAR_TIP:
                    rightEarTip = landmark.getPosition();
                    landmarksObtained.add( new LandmarkData(Landmark.RIGHT_EAR_TIP,rightEarTip));
                    if (rightEarTip == null) {
                        landRevealed[Landmark.RIGHT_EAR_TIP] = -1;
                    } else {
                        landRevealed[Landmark.RIGHT_EAR_TIP] = 0;
                    }
                    break;
                case Landmark.RIGHT_EAR:
                    rightEar =  landmark.getPosition();
                    landmarksObtained.add( new LandmarkData(Landmark.RIGHT_EAR,rightEar));
                    if (rightEar == null) {
                        landRevealed[Landmark.RIGHT_EAR] = -1;
                    } else {
                        landRevealed[Landmark.RIGHT_EAR] = 0;
                    }
                    break;
                case Landmark.RIGHT_EYE:
                    rightEye = landmark.getPosition();
                    landmarksObtained.add( new LandmarkData(Landmark.RIGHT_EYE,rightEye));
                    if (rightEye == null) {
                        landRevealed[Landmark.RIGHT_EYE] = -1;
                    } else {
                        landRevealed[Landmark.RIGHT_EYE] = 0;
                    }
                    break;
                case Landmark.RIGHT_MOUTH:
                    rightMouth =  landmark.getPosition();
                    landmarksObtained.add( new LandmarkData(Landmark.RIGHT_MOUTH,rightMouth));
                    if (rightMouth == null) {
                        landRevealed[Landmark.RIGHT_MOUTH] = -1;
                    } else {
                        landRevealed[Landmark.RIGHT_MOUTH] = 0;

                    }
                    break;
            }
        }
    }

    public void getLandmarkArea() {

        int landIndex = 0;
        for (Landmark land: landmarks
                ) {
            PointF landLeftTop = new PointF();
            PointF landRightBottom = new PointF();
            if (land.getType() == Landmark.BOTTOM_MOUTH || land.getType() == Landmark.LEFT_MOUTH || land.getType() == Landmark.RIGHT_MOUTH) {
                if(rightMouth==null ) {
                    landLeftTop.x = baseMouth.x;
                    landLeftTop.y = bottomy - face.getHeight()/3;
                    landWidth = (int)(leftMouth.x - baseMouth.x);
                }else if (leftMouth == null){

                    landLeftTop.x = rightMouth.x;
//                    pos.y = rightMouth.y - 10 - (baseMouth.y - rightMouth.y);
                    landLeftTop.y = bottomy - face.getHeight()/3;
                    landWidth = (int) (baseMouth.x - rightMouth.x);

                } else {
                    landLeftTop.x = rightMouth.x;
//                    pos.y = rightMouth.y - 10 - (baseMouth.y - rightMouth.y);
                    landLeftTop.y = bottomy - face.getHeight()/3;
                    landWidth = (int) (leftMouth.x - rightMouth.x);
                }

                landHeight = (int)(bottomy-landLeftTop.y);
                landRightBottom.x = landLeftTop.x + landWidth;
                landRightBottom.y = landLeftTop.y + landHeight;
                landmarksObtained.get(landIndex).setLeftTop(landLeftTop);
                landmarksObtained.get(landIndex).setRightBottom(landRightBottom);
                landmarksObtained.get(landIndex).setArea(landWidth*landHeight);

                isRevealed[Landmark.BOTTOM_MOUTH] = 1;
                isRevealed[Landmark.LEFT_MOUTH] = 1;
                isRevealed[Landmark.RIGHT_MOUTH] = 1;

            } else if (land.getType() == Landmark.NOSE_BASE) {
                Log.d(TAG2, "reveal: Else if Startr"+ noseBase.x+" "+ noseBase.y);

                if(leftEye == null) {
                    landLeftTop.x = rightEye.x-10;
                    landLeftTop.y = rightEye.y + 10;
                    landHeight = (int)(noseBase.y - rightEye.y +10);
                    landWidth = (int)(2*(noseBase.x - rightEye.x))+20;
                } else if (rightEye == null){
                    landLeftTop.x = noseBase.x - (leftEye.x - noseBase.x)-20;
                    landLeftTop.y = leftEye.y + 10;
                    landHeight = (int)(noseBase.y - leftEye.y +20);
                    landWidth = (int)(leftEye.x - landLeftTop.x)+20;
                } else {
                    Log.d(TAG2, "reveal: Else if Startr els"+ noseBase.x+" "+ noseBase.y);

                    landLeftTop.x = rightEye.x - 10;
                    landLeftTop.y = (rightEye.y + 10);

                    Log.d(TAG2, "reveal: Else if Startr els pos asgmt"+ noseBase.x+" "+ noseBase.y);

                    landWidth = (int)(leftEye.x - rightEye.x)+20;
                    landHeight = (int)(noseBase.y - rightEye.y)+20;
                    if(leftEye.y > rightEye.y){
                        landLeftTop.y = leftEye.y + 10;
                        landHeight = (int)(noseBase.y - leftEye.y)+20;
                    }
                    Log.d(TAG2, "reveal: Else if Startr els pos asgmt222"+ noseBase.x+" "+ noseBase.y);

                }
                landRightBottom.x = landLeftTop.x + landWidth;
                landRightBottom.y = landLeftTop.y + landHeight;
                landmarksObtained.get(landIndex).setLeftTop(landLeftTop);
                landmarksObtained.get(landIndex).setRightBottom(landRightBottom);
                landmarksObtained.get(landIndex).setArea(landWidth*landHeight);
                Log.d(TAG, "reveal: x: "+landLeftTop.x+" y:"+landLeftTop.y+" landHeight:"+landHeight);
            } else if (land.getType() == Landmark.LEFT_EYE) {
                landLeftTop.x = noseBase.x + ((leftEye.x - noseBase.x)/2);

                landLeftTop.y = leftEye.y - (noseBase.y - leftEye.y)/2;
                if (landLeftTop.y < 0) {
                    landLeftTop.y = 0;
                }
                landWidth = (int)(leftEye.x - noseBase.x);
                landHeight = (int)(noseBase.y - leftEye.y);
                landRightBottom.x = landLeftTop.x + landWidth;
                landRightBottom.y = landLeftTop.y + landHeight;
                landmarksObtained.get(landIndex).setLeftTop(landLeftTop);
                landmarksObtained.get(landIndex).setRightBottom(landRightBottom);
                landmarksObtained.get(landIndex).setArea(landWidth*landHeight);
            } else if (land.getType() == Landmark.RIGHT_EYE) {
                landLeftTop.x = rightEye.x - (noseBase.x - rightEye.x)/2;
                if (landLeftTop.x < 0) {
                    landLeftTop.x = 0;
                }
                landLeftTop.y = rightEye.y - (noseBase.y - rightEye.y)/2;
                if (landLeftTop.y < 0) {
                    landLeftTop.y = 0;
                }
//                landWidth = (int)((rightEye.x - facePosition.x)+(noseBase.x - rightEye.x)/2);
                landWidth = (int) (noseBase.x - rightEye.x);
                landHeight = (int)((1*(rightEye.y - landLeftTop.y)/3)+(rightEye.y - landLeftTop.y));
                landRightBottom.x = landLeftTop.x + landWidth;
                landRightBottom.y = landLeftTop.y + landHeight;
                landmarksObtained.get(landIndex).setLeftTop(landLeftTop);
                landmarksObtained.get(landIndex).setRightBottom(landRightBottom);
                landmarksObtained.get(landIndex).setArea(landWidth*landHeight);
            } else {
                landLeftTop = landmarks.get(randLandmark).getPosition();
                landWidth = getLandmarkWidth(landmarks.get(randLandmark).getType());
                landHeight = getLandmarkHeight(landmarks.get(randLandmark).getType());
                landRightBottom.x = landLeftTop.x + landWidth;
                landRightBottom.y = landLeftTop.y + landHeight;
                landmarksObtained.get(landIndex).setLeftTop(landLeftTop);
                landmarksObtained.get(landIndex).setRightBottom(landRightBottom);
                landmarksObtained.get(landIndex).setArea(landWidth*landHeight);
            }
            landIndex++;
        }
    }

    private void drawMatrix() {

        int faceRightx;
        int faceLeftx;
        int faceTopy;
        int faceBottomy;
        if (leftx < 0) {
            faceLeftx = 0;
        } else {
            faceLeftx = leftx;
        }
        if (rightx > tempCanvas.getWidth()) {
            faceRightx = tempCanvas.getWidth();
        } else {
            faceRightx = rightx;
        }
        if (topy < 0) {
            faceTopy = 0;
        } else {
            faceTopy = topy;
        }
        if (bottomy > tempCanvas.getHeight()) {
            faceBottomy = tempCanvas.getHeight();
        } else {
            faceBottomy = bottomy;
        }
        cellsPerRow = mode + 3;
        width = (faceRightx - faceLeftx)/cellsPerRow;
        height = (faceBottomy - faceTopy)/cellsPerRow;
        int i ;
        int partRightx;
        int partLeftx;
        int partTopy = faceTopy;
        int partBottomy = faceTopy + height;
        for (i=0;i<cellsPerRow;i++) {
            partLeftx = faceLeftx;
            for (partRightx = partLeftx + width; partRightx <= faceRightx; partRightx = partRightx + width) {
                rectanglePart = new Rect(partLeftx, partTopy, partRightx, partBottomy);
                if (trials == 3) {
                    parts.add(rectanglePart);
                }
                /*myRectPaint.setStyle(Paint.Style.STROKE);
                tempCanvas.drawRect(rectanglePart, myRectPaint);*/
                partLeftx = partRightx;
            }
            partTopy = partBottomy;
            partBottomy += height;
        }

    }

    private boolean doOverlap(PointF leftTop, PointF rightBottom, PointF cellLeftTop, PointF cellRightBottom) {
        if(cellLeftTop.x < rightBottom.x && cellRightBottom.x > leftTop.x && cellRightBottom.y > leftTop.y && cellLeftTop.y < rightBottom.y) {
            int widthOfOverlap = (int) (Math.min(cellRightBottom.x,rightBottom.x) - Math.max(cellLeftTop.x,leftTop.x));
            int heightOfOverlap = (int) (Math.min(cellRightBottom.y,rightBottom.y) - Math.max(cellLeftTop.y, leftTop.y));
            int percent;
            if (state == 1) {
                percent = 10/100;
            }else if (state == 2) {
                percent = 25/100;
            } else {
                percent = 10/100;
            }
            if ((maxArea*percent) < (widthOfOverlap * heightOfOverlap)) {
                Log.d(TAG, "doOverlap: maxArea: "+maxArea+" maxArea(10%): "+(maxArea*percent)+" thisArea: "+(widthOfOverlap * heightOfOverlap) );
                return true;
            } else {
                return false;
            }

        }
        return false;

    }
    private void checkAnswer() {
            if (matches.get(0).equals(getAnswer(rndInt))) {
                isSuccess = 1;
                showScore(score);

            } else {
                if (isTimeOut) {
                    mSpeechRecognizer.destroy();
                    trials--;
                    score--;
                    if(trials>0) {

                        if (trials == 1) {
                            chancesTextView.setText(trialNumbers[trials - 1] + " chance");
                        } else {
                            chancesTextView.setText(trialNumbers[trials - 1] + " chances");
                        }

                        dialogView.setText(chanceDialogs[trials - 1]);
                        facesView.setImageResource(res.getIdentifier(chanceFaces[trials - 1], "mipmap", "com.qburst.lekha.trainingproject"));
                        speakButton.setImageResource(res.getIdentifier(chanceMikes[trials - 1],"mipmap","com.qburst.lekha.trainingproject"));
                        final Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                        tempCanvas = new Canvas(tempBitmap);
                        indicator.setVisibility(View.VISIBLE);
                        Thread nextThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                tempCanvas.drawBitmap(myBitmap, 0, 0, null);
                                revealNext();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        celebImage.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
                                        celebImage.setVisibility(View.VISIBLE);
                                        indicator.setVisibility(View.INVISIBLE);
                                        isTimeOut = false;
                                        SpeechRecognitionListener listener = new SpeechRecognitionListener();
                                        mSpeechRecognizer.setRecognitionListener(listener);
                                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                                        circle.start(TIMER_LENGTH);

                                    }
                                });
                            }
                        });

                        nextThread.start();
                        countDownTimer.start();

                    } else {
                        showScore(0);

                    }
                } else {
                    Toast.makeText(getActivity(),"\""+matches.get(0)+"\" is a wrong guess.. Please Try Again",Toast.LENGTH_SHORT).show();
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                }
            }
    }

    private String getAnswer(int index) {
        Resources res = getActivity().getResources();
        stringValues = res.getStringArray(R.array.easy_image_names);
        answer = stringValues[index];
        return answer;
    }

    private void revealNext() {
        int gotCell = 0;
        int i = 0 ;
        int cellIndex;


        Log.d(TAG, "revealNext::: next: "+next);
        do {

            while (landmarksObtained.get(next).getType() == Landmark.LEFT_CHEEK || landmarksObtained.get(next).getType() == Landmark.RIGHT_CHEEK || landmarksObtained.get(next).getType() == Landmark.LEFT_MOUTH || landmarksObtained.get(next).getType() == Landmark.RIGHT_MOUTH) {
                next++;
            }
            cellIndex = 0;

            for (Rect cell : parts
                    ) {
                cellLeftTop.x = cell.left;
                cellLeftTop.y = cell.top;
                cellRightBottom.x = cell.right;
                cellRightBottom.y = cell.bottom;
//                myRectPaint.setStyle(Paint.Style.STROKE);
//                tempCanvas.drawRect(landmarksObtained.get(next).getLeftTop().x, landmarksObtained.get(next).getLeftTop().y, landmarksObtained.get(next).getRightBottom().x, landmarksObtained.get(next).getRightBottom().y, myRectPaint);
//                maxArea = landmarksObtained.get(next).getArea();
                maxArea = (int) ((cellRightBottom.x - cellLeftTop.x)*(cellRightBottom.y - cellLeftTop.y));
                if (doOverlap(landmarksObtained.get(next).getLeftTop(), landmarksObtained.get(next).getRightBottom(), cellLeftTop, cellRightBottom)) {
                    if (isFilled[cellIndex] == 1) {
                        isFilled[cellIndex] = 0;
                        gotCell = 1;
                        landRevealed[landmarksObtained.get(next).getType()] = 1;
                    }

                }
                /*myRectPaint.setStyle(Paint.Style.STROKE);
                tempCanvas.drawRect(cell, myRectPaint);*/
                cellIndex++;
            }
            next++;
            if (next >= landmarksObtained.size()){
                do {
                    randMatrixId = new Random().nextInt(parts.size());
                }while(isFilled[randMatrixId] == 0);
                isFilled[randMatrixId] = 0;
                gotCell = 1;
                break;
            }
        }while (gotCell == 0);
        myRectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        cellIndex = 0 ;
        for (Rect cell :
                parts) {
            if (isFilled[cellIndex] == 1) {
                tempCanvas.drawRect(cell, myRectPaint);
            }
            cellIndex++;
        }
    }

    private void showScore(int successValue) {
        mSpeechRecognizer.stopListening();
        mSpeechRecognizer.destroy();
        countDownTimer.cancel();
        scoreFragment = new Score();
        Bundle bundle = new Bundle();
        bundle.putInt(SUCCESS_TAG, successValue);
        bundle.putInt(STATE_TAG, state);
        bundle.putInt(IMAGE_TAG, rndInt+1);
        bundle.putString(ANSWER_TAG, answer);
        scoreFragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, scoreFragment, SUCCESS_TAG);
        transaction.commit();
    }

    public void callParentMethod(){
        getActivity().onBackPressed();
    }

    private int getLandmarkHeight(int type) {
        return 100;
    }

    private int getLandmarkWidth(int type) {
        return 100;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.back) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.destroy();
            countDownTimer.cancel();
            homeScreen =  new HomeScreen();
            FragmentTransaction transaction= this.getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container,homeScreen, TAG);
            transaction.commit();
            return  true;
        }
        return super.onOptionsItemSelected(item) ;
    }

    private void updateMenuTitles() {
        MenuItem menuItem = menu.findItem(R.id.back);
            menuItem .setTitle("HOME");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }
    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
        }

        @Override
        public void onResults(Bundle results)
        {
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            Log.d(TAG, "onResults: text: "+matches.get(0));
            checkAnswer();

        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }
}