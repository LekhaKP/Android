package com.qburst.lekha.trainingproject;

import android.media.Image;
import android.provider.ContactsContract;

import java.util.List;

/**
 * Created by user on 8/11/16.
 */

public class ImageData {
    int imageId;
    int score;
    int status;
    int level;

    public ImageData() {

    }

    public ImageData(int imageId, int score, int status, int level) {
        this.imageId = imageId;
        this.score = score;
        this.status = status;
        this.level = level;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
