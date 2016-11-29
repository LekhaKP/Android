package com.qburst.lekha.trainingproject.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.qburst.lekha.trainingproject.Database.LevelHandler;
import com.qburst.lekha.trainingproject.ImageData;
import com.qburst.lekha.trainingproject.R;

import java.util.ArrayList;
import java.util.List;

import static android.R.drawable.checkbox_on_background;
import static android.R.drawable.ic_lock_lock;
import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by user on 8/11/16.
 */

public class GridAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private LevelHandler db;
    ImageData levelList;


    public GridAdapter(Context c) {
        context = c;
        inflater = LayoutInflater.from(c);
        db = new LevelHandler(c);
        levelList = new ImageData();
    }

    @Override
    public int getCount() {
        return db.getLevelCount();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null)
        {
            convertView = inflater.inflate(R.layout.level_layout,
                    parent,false);
            holder = new ViewHolder();
            holder.levelImage=(ImageView) convertView.findViewById(R.id.level_image);
            holder.stateImage=(ImageView) convertView.findViewById(R.id.state);
            if(position==0)

            {
                convertView.setTag(holder);
            }
        }

        else

        {

            holder = (ViewHolder) convertView.getTag();

        }

        String[] levelNames = context.getResources().getStringArray(R.array.level_names);
        levelList = db.getLevelInfo(position+1);
        Resources res = context.getResources();
        if(levelList.getStatus() == 1) {
            holder.stateImage.setImageResource(ic_lock_lock);
            holder.levelImage.setImageResource(res.getIdentifier(""+levelNames[position],"drawable","com.qburst.lekha.trainingproject"));
        } else if (levelList.getStatus() == 2) {
            holder.stateImage.setVisibility(View.GONE);
            holder.levelImage.setImageResource(res.getIdentifier(""+levelNames[position],"drawable","com.qburst.lekha.trainingproject"));
        } else {
            holder.stateImage.setImageResource(checkbox_on_background);
            holder.levelImage.setImageResource(levelList.getImageId());
        }
        return convertView;

    }

    static class ViewHolder

    {
        ImageView levelImage;
        ImageView stateImage;
    }

}
