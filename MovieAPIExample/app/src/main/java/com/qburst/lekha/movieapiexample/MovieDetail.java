package com.qburst.lekha.movieapiexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.qburst.lekha.movieapiexample.model.Movie;
import com.qburst.lekha.movieapiexample.model.Trailer;
import com.qburst.lekha.movieapiexample.model.TrailerResponse;
import com.qburst.lekha.movieapiexample.rest.ApiClient;
import com.qburst.lekha.movieapiexample.rest.ApiInterface;
import com.squareup.picasso.Picasso;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieDetail extends AppCompatActivity {
    private static final String TAG = MovieDetail.class.getSimpleName();
    private static final String DETAIL_TAG = "movieDetail";
    private final static String API_KEY = "03ce669521ec539215a8afa4cdec5884";
    private String key;
    private ApiInterface apiTrailerService;
    private Call<TrailerResponse> callTrailer;
    private List<Trailer> trailers;
    private Movie movie;
    private ImageView posterView;
    private TextView titleView;
    private TextView descriptionView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        initialize();

    }

    private void initialize() {
        movie = (Movie) getIntent().getSerializableExtra(DETAIL_TAG);
        posterView = (ImageView) findViewById(R.id.poster_view);
        titleView = (TextView) findViewById(R.id.title_view);
        descriptionView = (TextView) findViewById(R.id.description_view);
        final String end_point = movie.getPosterPath();
        Picasso.with(this)
                .load("http://image.tmdb.org/t/p/w185/"+end_point)
                .resize(80, 80)
                .centerCrop()
                .into(posterView);
        apiTrailerService = ApiClient.getClient().create(ApiInterface.class);
        callTrailer = apiTrailerService.getMovieDetails(movie.getId(), API_KEY);
        callTrailer.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse>call, Response<TrailerResponse> response) {
                int statusCode = response.code();
                trailers = response.body().getResults();
                Log.d(TAG, "Number of trailers received: " + trailers.size());
                key = trailers.get(0).getKey();
                Log.d(TAG, "onResponse: key"+key);
                titleView.setText(Html.fromHtml(
                        "<a href=\"https://www.youtube.com/watch?v="+key+"\">"+movie.getTitle()+"</a> "));
                titleView.setMovementMethod(LinkMovementMethod.getInstance());
            }

            @Override
            public void onFailure(Call<TrailerResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });

        descriptionView.setText(movie.getOverview());
    }
}
