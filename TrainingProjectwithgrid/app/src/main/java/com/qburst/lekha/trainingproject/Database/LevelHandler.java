package com.qburst.lekha.trainingproject.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qburst.lekha.trainingproject.ImageData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 9/11/16.
 */

public class LevelHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "levelManager";

    // Contacts table name
    private static final String TABLE_LEVEL = "levels";

    // Contacts Table Columns names
    private static final String RES_ID = "res_id";
    private static final String SCORE = "score";
    private static final String STATE_ID = "state_id";
    private static final String LEVEL_ID = "level_id";

    public LevelHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public LevelHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LEVELS_TABLE = "CREATE TABLE " + TABLE_LEVEL + "("
                + RES_ID + " INTEGER PRIMARY KEY," + SCORE + " INTEGER,"
                + STATE_ID + " INTEGER," + LEVEL_ID+" INTEGER"+")";
        db.execSQL(CREATE_LEVELS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEVEL);

        // Create tables again
        onCreate(db);
    }

    public void addLevelInfo(ImageData imageData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RES_ID, imageData.getImageId());
        values.put(SCORE, imageData.getScore());
        values.put(STATE_ID, imageData.getStatus());
        values.put(LEVEL_ID, imageData.getLevel());

        // Inserting Row
        db.insert(TABLE_LEVEL, null, values);
        db.close(); // Closing database connection
    }

    public ImageData getLevelInfo(int level_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LEVEL, new String[] { RES_ID,
                        SCORE, STATE_ID, LEVEL_ID }, LEVEL_ID+"=?",
                new String[] { String.valueOf(level_id) }, null, null, LEVEL_ID, null);
        if (cursor != null)
            cursor.moveToFirst();

        ImageData imageData = new ImageData(Integer.parseInt(cursor.getString(0)),
                cursor.getInt(1), cursor.getInt(2), cursor.getInt(3));
        // return contact
        return imageData;
    }

    public int getLevel(int res_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LEVEL, new String[] { RES_ID,
                        SCORE, STATE_ID, LEVEL_ID }, RES_ID+"=?",
                new String[] { String.valueOf(res_id) }, null, null, LEVEL_ID, null);
        if (cursor != null)
            cursor.moveToFirst();

        ImageData imageData = new ImageData(Integer.parseInt(cursor.getString(0)),
                cursor.getInt(1), cursor.getInt(2), cursor.getInt(3));
        return imageData.getLevel();

    }

    public List<ImageData> getAllLevelInfo() {
        List<ImageData> levelList = new ArrayList<ImageData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LEVEL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ImageData imageData = new ImageData();
                imageData.setImageId(Integer.parseInt(cursor.getString(0)));
                imageData.setScore(cursor.getInt(1));
                imageData.setStatus(cursor.getInt(2));
                imageData.setLevel(cursor.getInt(3));
                // Adding contact to list
                levelList.add(imageData);
            } while (cursor.moveToNext());
        }

        // return contact list
        return levelList;
    }


    public int getLevelCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LEVEL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        // return count
        return cursor.getCount();
    }

    public int updateStatus(ImageData imageData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STATE_ID, imageData.getStatus());
        //values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating row
        return db.update(TABLE_LEVEL, values, RES_ID + " = ?",
                new String[] { String.valueOf(imageData.getImageId()) });
    }
}
