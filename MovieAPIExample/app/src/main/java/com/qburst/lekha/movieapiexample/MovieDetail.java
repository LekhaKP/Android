package com.qburst.lekha.movieapiexample;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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

public class MovieDetail extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_TAG = "movieDetail";
    private final static String API_KEY = "03ce669521ec539215a8afa4cdec5884";

    private String key;

    private Movie movie;
    private ImageView posterView;
    private TextView titleView;
    private TextView descriptionView;
    private ApiInterface apiTrailerService;
    private Call<TrailerResponse> callTrailer;
    private List<Trailer> trailers;
    private View view;
    private String end_point;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        initialize();
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initialize() {
        Bundle bundle = this.getArguments();
        movie = (Movie) bundle.getSerializable(DETAIL_TAG);
        posterView = (ImageView) view.findViewById(R.id.poster_view);
        titleView = (TextView) view.findViewById(R.id.title_view);
        descriptionView = (TextView) view.findViewById(R.id.description_view);
        end_point = movie.getPosterPath();
        Picasso.with(getActivity())
                .load("http://image.tmdb.org/t/p/w185/"+end_point)
                .resize(80, 80)
                .centerCrop()
                .into(posterView);
        /*posterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullSizeImage();
            }
        });*/
        apiTrailerService = ApiClient.getClient().create(ApiInterface.class);
        callTrailer = apiTrailerService.getMovieDetails(movie.getId(), API_KEY);
        callTrailer.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                int statusCode = response.code();
                trailers = response.body().getResults();
                Log.d(TAG, "Number of trailers received: " + trailers.size());
                if(trailers.size() != 0) {
                    key = trailers.get(0).getKey();
                    Log.d(TAG, "onResponse: key"+key);
                    titleView.setText(Html.fromHtml(
                            "<a href=\"https://www.youtube.com/watch?v="+key+"\">"+movie.getTitle()+"</a> "));
                    titleView.setMovementMethod(LinkMovementMethod.getInstance());
                }
                else {
                    titleView.setText(movie.getTitle());
                }

            }

            @Override
            public void onFailure(Call<TrailerResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });

        descriptionView.setText(movie.getOverview());
    }

    private void showFullSizeImage() {
        final Dialog nagDialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setCancelable(false);
        nagDialog.setContentView(R.layout.preview_image);
        Button btnClose = (Button)nagDialog.findViewById(R.id.btnIvClose);
        ImageView ivPreview = (ImageView)nagDialog.findViewById(R.id.iv_preview_image);
        //ivPreview.setBackgroundDrawable(dd);
        Picasso.with(getActivity())
                .load("http://image.tmdb.org/t/p/w185/"+end_point)
                .resize(80, 80)
                .centerCrop()
                .into(ivPreview);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                nagDialog.dismiss();
            }
        });
        nagDialog.show();
    }

}
