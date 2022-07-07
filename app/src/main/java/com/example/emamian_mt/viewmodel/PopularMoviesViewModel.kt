package com.example.emamian_mt.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.emamian_mt.util.Resource
import com.example.emamian_mt.MoviesApplication
import com.example.emamian_mt.models.MoviesResponse
import com.example.emamian_mt.repository.MovieRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class PopularMoviesViewModel(
    val app: Application,
    val moviesRepository: MovieRepository
) : AndroidViewModel(app) {
    val popularMovies: MutableLiveData<Resource<MoviesResponse>> = MutableLiveData()
    var popularMoviesPage = 1
    var popularMoviesResponse: MoviesResponse? = null

    init {
        getPopularMovies()
    }

    fun getPopularMovies() = viewModelScope.launch {
        safePopularMoviesCall()
    }

    private suspend fun safePopularMoviesCall() {
        popularMovies.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                println("has internet")
                val response =
                    moviesRepository.getPopularMovies(popularMoviesPage)
                popularMovies.postValue(handlePopularMoviesResponse(response))
            } else {
                popularMovies.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException ->
                    popularMovies.postValue(Resource.Error("Network Failure"))
                else ->
                    println(popularMovies.postValue(Resource.Error(t.message!!)))

            }
        }
    }


    private fun handlePopularMoviesResponse(response: Response<MoviesResponse>): Resource<MoviesResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                popularMoviesPage++
                if (popularMoviesResponse == null) {
                    popularMoviesResponse = resultResponse
                } else {
                    val oldArticles = popularMoviesResponse?.results
                    val newArticles = resultResponse.results
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(popularMoviesResponse ?: resultResponse)
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