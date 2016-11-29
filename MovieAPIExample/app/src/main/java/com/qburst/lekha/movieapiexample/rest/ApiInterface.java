package com.qburst.lekha.movieapiexample.rest;

import com.qburst.lekha.movieapiexample.model.MovieResponse;
import com.qburst.lekha.movieapiexample.model.TrailerResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey,@Query("page") int pageNum);

    @GET("movie/{id}/videos")
    Call<TrailerResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

}
