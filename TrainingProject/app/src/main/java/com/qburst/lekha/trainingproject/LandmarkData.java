package com.qburst.lekha.trainingproject;

import android.graphics.PointF;

/**
 * Created by user on 23/11/16.
 */

public class LandmarkData {
    int type;
    PointF leftTop;
    PointF rightBottom;
    int area;

    public LandmarkData(int type, PointF leftTop) {
        this.type = type;
        this.leftTop = leftTop;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PointF getLeftTop() {
        return leftTop;
    }

    public void setLeftTop(PointF leftTop) {
        this.leftTop = leftTop;
    }

    public PointF getRightBottom() {
        return rightBottom;
    }

    public void setRightBottom(PointF rightBottom) {
        this.rightBottom = rightBottom;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }
}
