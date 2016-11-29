package com.qburst.lekha.movieapiexample;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qburst.lekha.movieapiexample.adapter.MoviesAdapter;
import com.qburst.lekha.movieapiexample.model.Movie;
import com.qburst.lekha.movieapiexample.model.MovieResponse;
import com.qburst.lekha.movieapiexample.rest.ApiClient;
import com.qburst.lekha.movieapiexample.rest.ApiInterface;
import com.qburst.lekha.movieapiexample.rest.ClickListener;
import com.qburst.lekha.movieapiexample.rest.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieList extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_TAG = "movieDetail";
    private final static String API_KEY = "03ce669521ec539215a8afa4cdec5884";
    public static final int PAGE_SIZE = 30;
    private boolean mIsLastPage = false;
    private boolean mIsLoading = false;
    private int currentPage;

    private RecyclerView recyclerView;
    private MoviesAdapter moviesAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayoutManager layoutManager;
    private ApiInterface apiService;
    private Call<MovieResponse> call;
    private List<Movie> movies;
    private View view;
    private MovieDetail newFragment;
    private ProgressBar progressBar;
    private int visibleItemCount;
    private int totalItemCount;
    private int firstVisibleItemPosition;
    private MoviesAdapter moviesAdapters;
    private int lastVisibleItemPosition;
    private int previousTotal = 0;
    private boolean isScrollingUp = false;
    private int lastFirstVisiblePosition=0;
    private int lastIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        if (API_KEY.isEmpty()) {
            Toast.makeText(getActivity(), "Please obtain your API KEY first from themoviedb.org", Toast.LENGTH_LONG).show();
            return null;
        }
        initialize();

        getDetails();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    updateView();

            }

        });
        return view;
    }

    private void initialize() {
        currentPage = 1;
        movies = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.movies_recycler_view);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.pull_refresh_layout);
        layoutManager = new LinearLayoutManager(getActivity());
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        recyclerView.setVisibility(View.INVISIBLE);

        getTopRatedMovie(currentPage);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                final int currentFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (currentFirstVisibleItem > lastFirstVisiblePosition) {
                    isScrollingUp = false;
                } else if (currentFirstVisibleItem < lastFirstVisiblePosition) {
                    isScrollingUp = true;
                }
                lastFirstVisiblePosition = currentFirstVisibleItem;

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                Log.d(TAG, "onScrolled: totalItemCount: "+totalItemCount);
                firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastFirstVisiblePosition != firstVisibleItemPosition) {
                    lastFirstVisiblePosition = firstVisibleItemPosition;
                }

                Log.d("TAG",firstVisibleItemPosition+" "+lastVisibleItemPosition);
                if (mIsLoading) {
                    if (totalItemCount > previousTotal) {
                        mIsLoading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (lastVisibleItemPosition == (totalItemCount - 1) && !mIsLoading && !isScrollingUp) {
                    Log.d(TAG, "onScrolled: ");
                    currentPage = currentPage + 1;
                    getTopRatedMovie(currentPage);
                    mIsLoading = true;
                }
            }


        });
    }

    public void getTopRatedMovie(int page) {
        Log.d(TAG, "getTopRatedMovie: page"+currentPage);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        call = apiService.getTopRatedMovies(API_KEY, page);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response != null && response.body() != null && response.body().getResults() != null && !response.body().getResults().isEmpty()) {

                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    if (movies.size() == 0) {
                        movies = response.body().getResults();
                        moviesAdapter = new MoviesAdapter(movies, R.layout.list_item, getActivity());
                        recyclerView.setAdapter(moviesAdapter);
                        recyclerView.setLayoutManager(layoutManager);
                    } else {

                        Log.d(TAG, "onResponse: size" + movies.size()+" lastIndex: "+lastIndex);
                        moviesAdapter.setData(response.body().getResults());
                        moviesAdapter.notifyDataSetChanged();
                    }
                    lastIndex += response.body().getResults().size();
                    Log.d(TAG, "onResponse: size" + movies.size()+" lastIndex: "+lastIndex);
                }

            }

            @Override
            public void onFailure(Call<MovieResponse>call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    public void getDetails() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                newFragment = (MovieDetail)getFragmentManager().findFragmentByTag(DETAIL_TAG);
                if(newFragment == null) {
                    newFragment = new MovieDetail();
                    Movie movie = movies.get(position);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Bundle args = new Bundle();
                    args.putSerializable(DETAIL_TAG, movie);
                    newFragment.setArguments(args);
                    ft.replace(R.id.fragment_container, newFragment, DETAIL_TAG);
                    ft.addToBackStack(null);
                    ft.commit();
                }

            }

        }));
    }

    private void updateView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getTopRatedMovie(currentPage);
                moviesAdapters.clear();
                moviesAdapters.addAllOnRefresh(movies);
                swipeRefresh.setRefreshing(false);
            }
        },1000);
    }

}
