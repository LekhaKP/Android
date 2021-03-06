package com.qburst.lekha.movieapiexample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qburst.lekha.movieapiexample.MainActivity;
import com.qburst.lekha.movieapiexample.R;
import com.qburst.lekha.movieapiexample.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private int rowLayout;
    private Context context;
    private int position;
    private LayoutInflater inflater;

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        LinearLayout moviesLayout;

        ImageView moviePoster;
        TextView movieTitle;
        TextView data;
        TextView movieDescription;
        TextView rating;

        public MovieViewHolder(View v) {
            super(v);
            moviesLayout = (LinearLayout) v.findViewById(R.id.movies_layout);
            moviePoster = (ImageView) v.findViewById(R.id.poster);
            movieTitle = (TextView) v.findViewById(R.id.title);
            data = (TextView) v.findViewById(R.id.subtitle);
            movieDescription = (TextView) v.findViewById(R.id.description);
            rating = (TextView) v.findViewById(R.id.rating);
        }
    }

    public MoviesAdapter(List<Movie> movies, int rowLayout, Context context) {
        this.movies = movies;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    public MoviesAdapter(Context context, List<Movie> list) {
        this.context = context;
        position = 0;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<Movie> data) {
        int currSize = movies.size();
        this.movies.addAll(data);
        Log.d("", "setData: "+getItemCount());
        notifyItemRangeInserted(currSize, currSize-movies.size());
    }

    public void clear() {

        this.movies.clear();
        notifyDataSetChanged();
    }

    public void addAllOnRefresh(List<Movie> list) {

        this.movies.addAll(list);

    }

    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        final String end_point = movies.get(position).getPosterPath();
        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w185/"+end_point)
                .resize(50, 50)
                .centerCrop()
                .into(holder.moviePoster);
        holder.movieTitle.setText(movies.get(position).getTitle());
        holder.data.setText(movies.get(position).getReleaseDate());
        holder.movieDescription.setText(movies.get(position).getOverview());
        holder.rating.setText(movies.get(position).getVoteAverage().toString());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}
