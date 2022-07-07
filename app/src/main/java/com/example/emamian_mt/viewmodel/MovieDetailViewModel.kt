package com.example.emamian_mt.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.emamian_mt.MoviesApplication
import com.example.emamian_mt.models.MovieDetailResponse
import com.example.emamian_mt.repository.MovieRepository
import com.example.emamian_mt.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MovieDetailViewModel(
    val app: Application,
    val moviesRepository: MovieRepository,
    movieId: Int
) : AndroidViewModel(app) {
    val MovieDetail: MutableLiveData<Resource<MovieDetailResponse>> = MutableLiveData()
    var MovieDetailResponse: MovieDetailResponse? = null

    init {
        getMovieDetail(movieId)
    }

    private fun getMovieDetail(movieId: Int) = viewModelScope.launch {
        safeMovieDetailCall(movieId)
    }

    private suspend fun safeMovieDetailCall(movieId:Int) {
        MovieDetail.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                println("has internet")
                val response =
                    moviesRepository.getMovieDetail(movieId)
                MovieDetail.postValue(handleMovieDetailResponse(response))
            } else {
                MovieDetail.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException ->
                    MovieDetail.postValue(Resource.Error("Network Failure"))
                else ->
                    println(MovieDetail.postValue(Resource.Error(t.message!!)))

            }
        }
    }

    private fun handleMovieDetailResponse(response: Response<MovieDetailResponse>): Resource<MovieDetailResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(MovieDetailResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MoviesApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}