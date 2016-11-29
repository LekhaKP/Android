package com.qburst.lekha.movieapiexample;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.qburst.lekha.movieapiexample.adapter.MoviesAdapter;
import com.qburst.lekha.movieapiexample.model.Movie;
import com.qburst.lekha.movieapiexample.model.MovieResponse;
import com.qburst.lekha.movieapiexample.rest.ApiClient;
import com.qburst.lekha.movieapiexample.rest.ApiInterface;
import com.qburst.lekha.movieapiexample.rest.ClickListener;
import com.qburst.lekha.movieapiexample.rest.RecyclerTouchListener;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_TAG = "movieDetail";
    private final static String API_KEY = "03ce669521ec539215a8afa4cdec5884";
    private List<Movie> movies;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private ApiInterface apiService;
    private Call<MovieResponse> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please obtain your API KEY first from themoviedb.org", Toast.LENGTH_LONG).show();
            return;
        }
        initialize();
        getTopRatedMovie();
        getDetails();
        refresh();
    }

    private void initialize() {
        recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.activity_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        call = apiService.getTopRatedMovies(API_KEY);
    }

    public void getTopRatedMovie() {

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse>call, Response<MovieResponse> response) {
                int statusCode = response.code();
                movies = response.body().getResults();
                Log.d(TAG, "Number of movies received: " + movies.size());
                recyclerView.setAdapter(new MoviesAdapter(movies, R.layout.list_item, getApplicationContext()));

            }

            @Override
            public void onFailure(Call<MovieResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    public void getDetails() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Movie movie = movies.get(position);
                Intent intent = new Intent(MainActivity.this, MovieDetail.class);
                intent.putExtra(DETAIL_TAG,movie);
                startActivity(intent);
            }

        }));
    }

    private void refresh() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateView();
            }

        });
    }

    private void updateView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(new MoviesAdapter(movies, R.layout.list_item, getApplicationContext()));
                swipeRefresh.setRefreshing(false);
            }
        },1000);
    }

}
