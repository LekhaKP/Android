package com.qburst.lekha.arraylistexample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AnimalsAdapter extends RecyclerView.Adapter<AnimalsAdapter.MyViewHolder> {
    private List<Animals> animalsList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);

        }

    }


    public AnimalsAdapter(List<Animals> animalsList) {
        this.animalsList = animalsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Animals animal = animalsList.get(position);
        holder.title.setText(animal.getTitle());

    }

    @Override
    public int getItemCount() {
        return animalsList.size();
    }

    public void removeItem(int position) {
        animalsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, animalsList.size());
    }
}

