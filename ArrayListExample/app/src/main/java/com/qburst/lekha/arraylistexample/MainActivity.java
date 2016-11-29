package com.qburst.lekha.arraylistexample;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.qburst.lekha.arraylistexample.R.id.animalList;

public class MainActivity extends AppCompatActivity {
    private Point p;
    private RecyclerView animalList;
    private List<Animals> animalsList;
    private AnimalsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animalList=(RecyclerView) this.findViewById(R.id.animalList);
        animalsList  = new ArrayList<>();
        mAdapter = new AnimalsAdapter(animalsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        animalList.setLayoutManager(mLayoutManager);
        animalList.setItemAnimator(new DefaultItemAnimator());
        animalList.setAdapter(mAdapter);
        getAnimalNames();
        animalList.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), animalList, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {
                Animals animal = animalsList.get(position);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete "+ animal.getTitle()+"?")
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
            showAddPopUp(this,p);
            return true;


        }
        return super.onOptionsItemSelected(item);
    }

    void getAnimalNames()
    {
        Animals animals = new Animals("DOG");
        animalsList.add(animals);
        animals = new Animals("CAT");
        animalsList.add(animals);
        animals = new Animals("HORSE");
        animalsList.add(animals);
        animals = new Animals("ELEPHANT");
        animalsList.add(animals);
        animals = new Animals("LION");
        animalsList.add(animals);
        animals = new Animals("COW");
        animalsList.add(animals);
        animals = new Animals("MONKEY");
        animalsList.add(animals);
        animals = new Animals("DEER");
        animalsList.add(animals);
        animals = new Animals("RABBIT");
        animalsList.add(animals);
        animals = new Animals("BEER");
        animalsList.add(animals);
        animals = new Animals("DONKEY");
        animalsList.add(animals);
        animals = new Animals("LAMB");
        animalsList.add(animals);
        animals = new Animals("GOAT");
        animalsList.add(animals);
        mAdapter.notifyDataSetChanged();
        sortAnimalList(animalsList);
    }

    void sortAnimalList(List<Animals> sort_animal) {
        Collections.sort(sort_animal, new Comparator<Animals>() {
            public int compare(Animals v1, Animals v2) {
                return v1.getTitle().compareTo(v2.getTitle());
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

                final Animals animals = new Animals(animal_name.getText().toString());
                animalsList.add(animals);
                mAdapter.notifyItemInserted(animalsList.size() - 1);
                sortAnimalList(animalsList);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                animalList.setLayoutManager(mLayoutManager);
                popup.dismiss();

            }
        });
    }

}

