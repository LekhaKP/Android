package com.qburst.lekha.reminder;



import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ReminderList extends Fragment {
    String key;
    Point p;
    final String TAG_MY_CLASS = "Type1";
    TextView text, vers;
    String myRem;
    static int show = 0;

    TimePicker alarm_time;
    DatePicker alarm_date;
    int hour,minute,year,month,day;
    String time_selected, date_selected;
    Button next_finish;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        DatabaseHandler db = new DatabaseHandler(getActivity());
        View view = inflater.inflate(R.layout.fragment_reminder_list, container, false);
        view.setBackgroundColor(Color.parseColor("#5ba4e5"));
        text = (TextView) view.findViewById(R.id.AndroidOs);
        vers = (TextView) view.findViewById(R.id.Version);
        Bundle args = this.getArguments();
        myRem = args.getString(TAG_MY_CLASS, key);
        p = new Point();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        (getActivity()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displaymetrics);
        p.x = displaymetrics.widthPixels / 2;
        p.y = displaymetrics.heightPixels / 3;
        if (myRem == null) {
            actionBar.setTitle("Reminder");
        } else {


            actionBar.setTitle(myRem);
            //db.deleteContact(new ReminderSchema());
            // Reading all alarm_list
            Log.d("Reading: ", "Reading all alarm_list..");
            List<ReminderSchema> alarm_list = db.getAllAlarms();

            for (ReminderSchema cn : alarm_list) {
                String log = "Id: " + cn.getID() + " ,Time: " + cn.get_alarm_time() + " ,Date: " +
                        cn.get_alarm_date();


            }
        }
        container.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_out_right, R.animator.slide_in_left);
                ReminderType newFragment = new ReminderType();
                ft.replace(R.id.reminder_list_id, newFragment);
                ft.commit();
            }


        });

        return view;
    }

    public void showPopup(final Activity context, final Point p, final String type) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        (getActivity()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displaymetrics);
        int popupWidth = 80 * displaymetrics.widthPixels / 100;
        int popupHeight;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.pop_layout, viewGroup);
        alarm_time = (TimePicker) layout.findViewById(R.id.timePicker);
        alarm_date = (DatePicker) layout.findViewById(R.id.datePicker);
        next_finish = (Button) layout.findViewById(R.id.next);
        switch (show) {
            case 0:
                alarm_time.setVisibility(View.VISIBLE);
                alarm_date.setVisibility(View.GONE);
                popupHeight = 50 * displaymetrics.heightPixels / 100;
                break;
            case 1:
                alarm_time.setVisibility(View.GONE);
                alarm_date.setVisibility(View.VISIBLE);
                next_finish.setText("FINISH");
                popupHeight = 80 * displaymetrics.heightPixels / 100;
                break;
            default:
                alarm_time.setVisibility(View.VISIBLE);
                alarm_date.setVisibility(View.INVISIBLE);
                popupHeight = 50 * displaymetrics.heightPixels / 100;
                break;

        }
        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Clear the default translucent background
        // popup.setBackgroundDrawable(new BitmapDrawable());
        layout.setBackgroundColor(Color.WHITE);
        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x - (80 * p.x / 100), p.y / 2);

        // Getting a reference to Close button, and close the popup when clicked.
        Button close = (Button) layout.findViewById(R.id.next);
        close.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                switch (show) {
                    case 0:
                        show++;
                        hour = alarm_time.getHour();
                        minute = alarm_time.getMinute();
                        time_selected = hour+":"+minute;
                        popup.dismiss();
                        showPopup(getActivity(), p, type);
                        break;
                    case 1:
                        show = 0;
                        year = alarm_date.getYear();
                        month = alarm_date.getMonth();
                        day = alarm_date.getDayOfMonth();
                        date_selected = day+"/"+month+"/"+year;
                        //SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        //date_selected = df.format(new Date(alarm_date.getDayOfMonth(), alarm_date.getMonth(),alarm_date.getYear()));
                        DatabaseHandler db = new DatabaseHandler(context);

                        // Inserting Contacts
                        Log.d("Insert: ", "Inserting ..");
                        db.addAlarm(new ReminderSchema(time_selected, date_selected));
                        popup.dismiss();
                        break;
                    default:
                        show = 0;
                        popup.dismiss();
                        break;
                }

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onPrepareOptionsMenu (Menu menu) {
        MenuItem item = menu.findItem(R.id.add_reminder);
        item.setEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_reminder) {
            showPopup(getActivity(),p,myRem);
            return false;
        }
        return true;

    }
}



