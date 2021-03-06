package com.qburst.lekha.databindingexample;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    Point p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        init();

    }

    private void init() {
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MyAdapter(Data.list);
        mRecyclerView.setAdapter(mAdapter);
        int index = (int) ((System.currentTimeMillis() / 1000) % Data.list.size());
        MyModel myModel = Data.list.get(index);
        myModel.setData(myModel.getData() + ".");
        sortAnimalList(Data.list);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {
                MyModel animal = Data.list.get(position);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete "+ animal.getData()+"?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                mAdapter.removeItem(position);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }));
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
        if (id == R.id.add_animal) {
            p = new Point();
            DisplayMetrics displaymetrics = new DisplayMetrics();
            (this).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displaymetrics);
            p.x = displaymetrics.widthPixels ;
            p.y =displaymetrics.heightPixels ;
            showAddPopUp(MainActivity.this,p);
            return true;


        }
        return super.onOptionsItemSelected(item);
    }

    void sortAnimalList(List<MyModel> sort_animal) {
        Collections.sort(sort_animal, new Comparator<MyModel>() {
            public int compare(MyModel v1, MyModel v2) {
                return v1.getData().compareTo(v2.getData());
            }
        });
    }
    public void showAddPopUp(final Activity context, final Point p) {
        LinearLayout viewGroup = (LinearLayout) MainActivity.this.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.add_list_item, viewGroup);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        (this).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displaymetrics);
        int popupWidth = 80 * displaymetrics.widthPixels / 100;
        int popupHeight= 20 * displaymetrics.heightPixels / 100;
        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(MainActivity.this);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);
        layout.setBackgroundColor(Color.WHITE);
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x/2 - popupWidth/2, p.y/2 - popupHeight/2 );

        final EditText animal_name = (EditText)layout.findViewById(R.id.animal);
        Button Add = (Button) layout.findViewById(R.id.add);
        Add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                final MyModel animals = new MyModel(animal_name.getText().toString());
                Data.list.add(animals);
                mAdapter.notifyItemInserted(Data.list.size() - 1);
                sortAnimalList(Data.list);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                popup.dismiss();

            }
        });
    }
}
