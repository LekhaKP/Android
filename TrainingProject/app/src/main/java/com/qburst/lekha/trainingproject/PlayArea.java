package com.qburst.lekha.trainingproject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.qburst.lekha.trainingproject.Database.LevelHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static java.util.Arrays.fill;


public class PlayArea extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    protected static final int RESULT_SPEECH = 1;
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Tag";
    private static final String IMAGE_TAG = "IMAGE_ID_TAG";
    private static final String STATE_TAG = "STATE";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Canvas canvas;
    private Paint paint;
    private ImageView celebImage;
    private List<String> image_ids;
    private TypedArray imgs;
    private Random rand;
    private int rndInt;
    private int resID;
    private ImageView speakButton;
    private EditText personName;
    private int height;
    private int width;
    private String answer;
    private int leftx;
    private int topy;
    private int rightx;
    private int bottomy;
    private int reduce;
    private Rect rectanglePart;
    private List<Rect> parts;
    private Point size;
    private LevelHandler db;
    private int noOfLevels;
    private int levelsPerMode;
    private int matrixSize;
    private int score = 5;
    private int mode = 0;
    private int trials = 3;
    private int reduceAppBar;
    private List<Landmark> landmarks;
    private int[] isFilled;
    private Canvas tempCanvas;
    private Bitmap myBitmap;
    private int newFlag = 0;
    private int isSuccess = 0;
    private DisplayMetrics displaymetrics;
    private ViewGroup.LayoutParams layoutParams;
    private Paint myRectPaint;
    private LevelGrid levelGrid;


    public PlayArea() {
        // Required empty public constructor
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_area, container, false);
        displaymetrics = new DisplayMetrics();
        (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        reduce = (int) getResources().getDimension(R.dimen.reduce_padding);
        celebImage = (ImageView) view.findViewById(R.id.image_puzzle);
        Bundle bundle = this.getArguments();
        resID = (int) bundle.get(IMAGE_TAG);

        layoutParams = celebImage.getLayoutParams();
        layoutParams.height = (height-reduce)/2;
        celebImage.setLayoutParams(layoutParams);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable=true;
        myBitmap = BitmapFactory.decodeResource(
                view.getResources(),
                resID,
                options);

        myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.parseColor("#40e0d0"));
        myRectPaint.setStyle(Paint.Style.STROKE);

        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        FaceDetector faceDetector = new FaceDetector.Builder(getContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        /*if(!faceDetector.isOperational()){
            new AlertDialog.Builder(view.getContext()).setMessage("Could not set up the face detector!").show();
            return null;
        }
*/
        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);


        db = new LevelHandler(getContext());
        noOfLevels = db.getLevelCount();
        levelsPerMode = noOfLevels/3;
        for (int k = 1; k<noOfLevels; k+=levelsPerMode) {
            if(db.getLevel(resID)>=k && db.getLevel(resID)<k+levelsPerMode) {
                Log.d(TAG, "onCreateView: Level: "+db.getLevel(resID));
                mode++;
                break;
            }
            mode++;
        }

        drawMatrix();

        isFilled = new int[parts.size()];
        fill(isFilled,1);
        int flag = 0;
        int detectedCell = 0;

        for (int j=0;j<faces.size();j++) {
            Face thisFace = faces.valueAt(j);
            landmarks = thisFace.getLandmarks();
            do {
                matrixSize = (mode+2)*(mode+2);
                int randMatrixId = new Random().nextInt(matrixSize);
                for (int i=0;i<landmarks.size();i++) {
                    PointF pos = landmarks.get(i).getPosition();
                    if (parts.get(randMatrixId).contains((int)pos.x,(int)pos.y)) {
                        isFilled[randMatrixId]=0;
                        detectedCell++;
                    }
                }
                isFilled[randMatrixId] = 0;
                flag ++;
            }while (flag<=(mode+2));
        }

        if (detectedCell == 0) {
            int randomfill =  new Random().nextInt(landmarks.size());
            PointF pos = landmarks.get(randomfill).getPosition();
            for (int j=0; j<parts.size(); j++) {
                if (parts.get(j).contains((int)pos.x,(int)pos.y)) {
                    Log.d(TAG, "onCreateView: Landmark "+landmarks.get(randomfill).getType()+" is in Rectangle "+j);
                    isFilled[j] = 0;
                }
            }
        }

        for (int j = 0;j<parts.size();j++) {
            if (isFilled[j] == 1) {
                Paint paint = new Paint();
                paint.setColor(Color.parseColor("#40e0d0"));
                paint.setStyle(Paint.Style.FILL);
                tempCanvas.drawRect(parts.get(j),paint);
            }
        }

        celebImage.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
        Log.d(TAG, "onCreateView: height: "+celebImage.getHeight()+" width: "+celebImage.getWidth());





        personName = (EditText) view.findViewById(R.id.person_name);
        speakButton = (ImageView) view.findViewById(R.id.speak_button);
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    personName.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        return view;
    }

    private void drawMatrix() {
        parts = new ArrayList<>();
        int imageviewWidth =displaymetrics.widthPixels - reduce;
        width = imageviewWidth/(mode+2);
        height = layoutParams.height-reduce/2;
        Log.d(TAG, "onCreateView: height:"+height+" width:"+imageviewWidth);
        height = height/(mode+2);
        leftx = 0;
        topy = 0;
        rightx = leftx+width;
        bottomy = topy+height;
        int i ;

        for (i=0;i<(mode+2);i++) {
            leftx = 0;
            for (rightx = leftx + width; rightx <= imageviewWidth; rightx = rightx + width) {
                rectanglePart = new Rect(leftx, topy, rightx, bottomy);
                parts.add(rectanglePart);
                tempCanvas.drawRect(rectanglePart, myRectPaint);
                leftx = rightx;
            }
            topy = bottomy;
            bottomy += height;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d(TAG, "onActivityResult: speech-to-text:"+text.get(0));
                    if( text.get(0).equals(getAnswer(resID))) {
                        isSuccess = 1;
                        personName.setText(text.get(0));
                        showScore(isSuccess);
                    }
                    else {
                        trials--;
                        score--;
                        if(trials>0) {
                            Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                            tempCanvas = new Canvas(tempBitmap);
                            tempCanvas.drawBitmap(myBitmap, 0, 0, null);
                            drawMatrix();
                            do{
                                int randMatrixId = new Random().nextInt(matrixSize);
                                if (isFilled[randMatrixId] == 1) {
                                    isFilled[randMatrixId] = 0;
                                    newFlag++;
                                }

                            }while(newFlag==0);
                            newFlag=0;
                            for (int j = 0;j<parts.size();j++) {
                                if (isFilled[j] == 1) {
                                    Paint paint = new Paint();
                                    paint.setColor(Color.parseColor("#40e0d0"));
                                    paint.setStyle(Paint.Style.FILL);
                                    tempCanvas.drawRect(parts.get(j),paint);
                                }
                            }

                            celebImage.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
                        }
                        else {
                           showScore(isSuccess);
                        }

                    }
                }
                break;
            }
        }
    }

    private String getAnswer(int resID) {
        switch (resID) {
            case R.drawable.aiswarya_roy: answer = "Aishwarya Rai";
                break;
            case R.drawable.anil_kapoor: answer = "Anil Kapoor";
                break;
            case R.drawable.dharmendra: answer = "Dharmendra";
                break;
            case R.drawable.madhuri_dixit: answer = "Madhuri Dixit";
                break;
            case R.drawable.raj_kapoor: answer = "Raj Kapoor";
                break;
            case R.drawable.shah_rukh_khan: answer = "Shah Rukh Khan";
                break;
            default: answer = "";
                break;
        }
        return answer;
    }

    private void showScore(int status) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        (getActivity()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displaymetrics);
        int popupWidth = displaymetrics.widthPixels;
        int popupHeight = displaymetrics.heightPixels-reduceAppBar;
        Point point = new Point();
        point.x = displaymetrics.widthPixels/2;
        point.y = displaymetrics.heightPixels/3;

        reduceAppBar = (int) getResources().getDimension(R.dimen.reduce_appbar);
        RelativeLayout viewGroup = (RelativeLayout) getActivity().findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.score_layout, viewGroup);
        final PopupWindow popup = new PopupWindow(getContext());
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setOutsideTouchable(false);
        ImageView scoreView = (ImageView) layout.findViewById(R.id.score_view);
        TextView statusTextView = (TextView) layout.findViewById(R.id.success_status);
        if (status == 1) {
            statusTextView.setText("SUCCESS");
            scoreView.setVisibility(View.VISIBLE);
            Resources res = getContext().getResources();
            String[] levelNames = getContext().getResources().getStringArray(R.array.level_names);
            scoreView.setImageResource(res.getIdentifier("score_"+levelNames[score-1],"drawable","com.qburst.lekha.trainingproject"));
        } else {
            statusTextView.setText("FAILED");
            scoreView.setVisibility(View.GONE);
        }

        popup.showAtLocation(layout, Gravity.NO_GRAVITY, point.x-popupWidth/2, reduceAppBar );
        Button dialogButton = (Button) layout.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popup.dismiss();
            }
        });
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                /*levelGrid = (LevelGrid) getFragmentManager().findFragmentByTag(STATE_TAG);
                if (levelGrid == null) {*/
                    levelGrid = (LevelGrid) getFragmentManager().findFragmentByTag(STATE_TAG);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, levelGrid, STATE_TAG);
                    transaction.commit();

//                }
            }
        });

    }

    public void callParentMethod(){
        getActivity().onBackPressed();
    }

}
