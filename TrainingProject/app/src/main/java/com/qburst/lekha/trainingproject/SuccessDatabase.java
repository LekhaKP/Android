package com.qburst.lekha.trainingproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 28/11/16.
 */

public class SuccessDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SuccessHandler";

    // Contacts table name
    private static final String TABLE_LEVEL = "SuccessTable";

    // Contacts Table Columns names
    private static final String RES_ID = "res_id";
    private static final String SCORE = "score";
    private static final String SUCCESS_STATUS_ID = "success_status_id";
    private static final String LEVEL_ID = "level_id";
    public SuccessDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SuccessDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LEVELS_TABLE = "CREATE TABLE " + TABLE_LEVEL + "("
                + RES_ID + " INTEGER PRIMARY KEY," + SCORE + " INTEGER,"
                + SUCCESS_STATUS_ID + " INTEGER," +LEVEL_ID+" INTEGER"+ ")";
        db.execSQL(CREATE_LEVELS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEVEL);
        onCreate(db);
    }

    public void addData(int imageId,int score, int status,int level) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RES_ID, imageId);
        values.put(SCORE, score);
        values.put(SUCCESS_STATUS_ID, status);
        values.put(LEVEL_ID, level);
        // Inserting Row
        db.insert(TABLE_LEVEL, null, values);
        db.close(); // Closing database connection
    }

    public void updateData(int imageID, int score, int state) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SUCCESS_STATUS_ID, state);
        values.put(SCORE,score);
        // updating row
        db.update(TABLE_LEVEL, values, RES_ID + " = "+imageID,
                null);
    }

    public int checkStatus(int imageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LEVEL, new String[] { RES_ID,
                        SCORE, SUCCESS_STATUS_ID,LEVEL_ID }, RES_ID+"=?",
                new String[] { String.valueOf(imageId) }, null, null, SUCCESS_STATUS_ID, null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor.getInt(2);

    }

    public int getCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LEVEL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        return cursor.getCount();
    }

    public int getUnFinishedCount (int level){
        String countQuery = "SELECT  * FROM " + TABLE_LEVEL+" WHERE "+LEVEL_ID+" = "+level;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        return cursor.getCount();
    }
}
