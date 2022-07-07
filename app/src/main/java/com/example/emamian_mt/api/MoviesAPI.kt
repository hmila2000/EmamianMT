package com.example.emamian_mt.api

import com.example.emamian_mt.models.MovieDetailResponse
import com.example.emamian_mt.util.Constants.Companion.API_KEY
import com.example.emamian_mt.models.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesAPI {

    @GET("popular")
    suspend fun getPopularMovies(
        @Query("api_key")
        apiKey: String = API_KEY,
        @Query("language")
        language: String = "en-US",
        @Query("page")
        pageNumber: Int = 1
    ): Response<MoviesResponse>

    @GET("{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id")
        movieId: Int,
        @Query("api_key")
        apiKey: String = API_KEY
    ): Response<MovieDetailResponse>
}