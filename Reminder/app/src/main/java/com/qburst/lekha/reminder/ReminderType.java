package com.qburst.lekha.reminder;


import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import static com.qburst.lekha.reminder.R.id.fragment_container;

public class ReminderType extends android.app.ListFragment {
    final String TAG_MY_CLASS = "Type1";
    String key,prev;
    String[] AndroidOS = new String[] { "Alarm","Calls","Entertainments","Functions","Medicine","Money","Shopping","Study" };

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_reminder_type, container, false);
    }*/

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        view.setLayoutParams(new LinearLayout.LayoutParams(700, 1650));
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,AndroidOS);
        setListAdapter(adapter);

        }






    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ReminderList newFragment = new ReminderList();
        getListView().setSelector(android.R.color.holo_blue_dark);





        Bundle bundle = new Bundle();
        bundle.putString(TAG_MY_CLASS, AndroidOS[position]);
        newFragment.setArguments(bundle);




        getFragmentManager()
                .beginTransaction()
                .replace(fragment_container,newFragment,TAG_MY_CLASS)
                .setCustomAnimations(R.animator.slide_out_right, R.animator.slide_in_left, R.animator.slide_in_left, R.animator.slide_out_right)
                .commit();



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onPrepareOptionsMenu (Menu menu) {
        MenuItem item = menu.findItem(R.id.add_reminder);
        item.setEnabled(false);
    }




}
