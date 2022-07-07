package com.example.emamian_mt.repository

import com.example.emamian_mt.api.RetrofitInstance

class MovieRepository {

    suspend fun getPopularMovies(page:Int) =
        RetrofitInstance.api.getPopularMovies(pageNumber = page)
    suspend fun getMovieDetail(movieId:Int) =
        RetrofitInstance.api.getMovieDetail(movieId = movieId)

}