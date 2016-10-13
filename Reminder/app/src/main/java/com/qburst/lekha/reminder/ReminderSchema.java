package com.qburst.lekha.reminder;

import android.util.Log;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

/**
 * Created by user on 12/10/16.
 */

public class ReminderSchema {
    int _id;
    Date _alarm_date;
    Time _alarm_time;
    public ReminderSchema(){   }
    public ReminderSchema(int id, String _input_time, String _input_date){
        this._id = id;
        try {

            SimpleDateFormat format = new SimpleDateFormat("hh:mm"); //if 24 hour format
            java.util.Date d1 =(java.util.Date)format.parse(_input_time);
            this._alarm_time = new java.sql.Time(d1.getTime());
            format = new SimpleDateFormat("dd/MM/yyyy");
            d1 = (java.util.Date)format.parse(_input_date);
            this._alarm_date = new Date(d1.getTime());

        } catch(Exception e) {

            Log.e("Exception is ", e.toString());
        }
    }
    public ReminderSchema(String _input_time, String _input_date){


        try {

            SimpleDateFormat format = new SimpleDateFormat("hh:mm"); //if 24 hour format
            java.util.Date d1 =(java.util.Date)format.parse(_input_time);
            this._alarm_time = new java.sql.Time(d1.getTime());
            format = new SimpleDateFormat("dd/MM/yyyy");
            d1 = (java.util.Date)format.parse(_input_date);
            this._alarm_date = new Date(d1.getTime());

        } catch(Exception e) {

            Log.e("Exception is ", e.toString());
        }





    }
    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String get_alarm_time(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        return  dateFormat.format(this._alarm_time);
    }

    public void set_alarm_time(String input_time){
        try{
            SimpleDateFormat format = new SimpleDateFormat("hh:mm"); //if 24 hour format
            java.util.Date d1 =(java.util.Date)format.parse(input_time);
            this._alarm_time = new java.sql.Time(d1.getTime());
        }
        catch (Exception e) {
            Log.e("Exception is ", e.toString());
        }

    }

    public String get_alarm_date(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return  dateFormat.format(this._alarm_date);
    }

    public void set_alarm_date(String input_date){
        try{
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); //if 24 hour format
            java.util.Date d1 =(java.util.Date)format.parse(input_date);
            this._alarm_date = new java.sql.Date(d1.getTime());
        }
        catch (Exception e) {
            Log.e("Exception is ", e.toString());
        }
    }
}

