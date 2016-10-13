package com.qburst.lekha.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12/10/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "reminder";
    private static final String TABLE_NAME = "alarms";
    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "time";
    private static final String KEY_DATE = "date";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TIME + " TIME,"
                + KEY_DATE + " DATE" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);

    }
    // code to add the new contact
    void addAlarm(ReminderSchema alarm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIME, String.valueOf(alarm.get_alarm_time()));
        values.put(KEY_DATE, String.valueOf(alarm.get_alarm_date()));

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get all contacts in a list view
    public List<ReminderSchema> getAllAlarms() {
        List<ReminderSchema> alarmList = new ArrayList<ReminderSchema>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ReminderSchema reminder = new ReminderSchema();
                reminder.setID(Integer.parseInt(cursor.getString(0)));
                reminder.set_alarm_time(cursor.getString(1));
                reminder.set_alarm_date(cursor.getString(2));
                // Adding reminder to list
                alarmList.add(reminder);
            } while (cursor.moveToNext());
        }

        // return contact list
        return alarmList;
    }

    // Deleting single contact
    public void deleteContact(ReminderSchema alarm_list) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = 4",null);
        db.close();
    }
}
