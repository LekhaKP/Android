package com.qburst.lekha.trainingproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by user on 7/11/16.
 */

public class MatrixView extends ImageView {

    private static final String TAG = "Tag";
    private int height;
    private int width;
    private Point size;
    private int reduce;
    private List<Rect> parts;
    private Rect rectanglePart;
    private int leftx;
    private int topy;
    private int rightx;
    private int bottomy;

    public MatrixView(Context context) {
        super(context);
    }

    public MatrixView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        reduce = (int) getResources().getDimension(R.dimen.reduce_padding);
        Display display = wm.getDefaultDisplay();
        parts = new ArrayList<>();
        size = new Point();
        display.getSize(size);
        width = size.x - reduce;
        width /= 4;
        height = size.y/2;
        height /= 4;
        leftx = 0;
        topy = 0;
        rightx = leftx+width;
        bottomy = topy+height;
        reduce = (int) getResources().getDimension(R.dimen.reduce_padding);
        Log.d(TAG, "onDraw: width"+size.x+" height"+size.y);
        int i = 0;

        for (i=0;i<4;i++) {
            leftx = 0;
            for (rightx = leftx + width; rightx <= size.x; rightx = rightx + width) {
                rectanglePart = new Rect(leftx, topy, rightx, bottomy);
                parts.add(rectanglePart);
                canvas.drawRect(rectanglePart, paint);
                leftx = rightx;
            }
            topy = bottomy;
            bottomy += height;
        }
        for (i = 0; i< 8; i++) {
            paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            int randomfill =  new Random().nextInt(16);
            canvas.drawRect(parts.get(randomfill),paint);
        }


    }
}
