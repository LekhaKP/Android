package com.qburst.lekha.reminder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    final String TAG_MY_CLASS = "initial";
    String para = "initial";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View myView = findViewById(R.id.activity_main);

        ReminderList fragmentOne = new ReminderList();
        Bundle bundle = new Bundle();
        bundle.putString(TAG_MY_CLASS, para);
        bundle.putString(TAG_MY_CLASS, para);
        fragmentOne.setArguments(bundle);
        FragmentManager manager=getFragmentManager();
        FragmentTransaction transaction= manager.beginTransaction();
        transaction.add(R.id.fragment_container,fragmentOne,TAG_MY_CLASS);
        transaction.addToBackStack(null);
        transaction.commit();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_action_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ActionBar actionBar = this.getSupportActionBar();
        if (id == R.id.add_reminder) {
            if (actionBar.getTitle() == "Reminder") {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_out_right, R.animator.slide_in_left);
                ReminderType newFragment = new ReminderType();
                ft.replace(R.id.reminder_list_id, newFragment);

                ft.commit();
                return true;
            }

        }



        return super.onOptionsItemSelected(item);
    }



}
